package sunnyday.controller.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import sunnyday.adapter.redis.DCAdapter;
import sunnyday.adapter.redis.impl.Utils;
import sunnyday.common.model.CheckMethod;
import sunnyday.common.model.LocationInfo;
import sunnyday.common.model.NetSwitchedMobileInfo;
import sunnyday.common.model.TdInfo;
import sunnyday.common.model.UserBalanceForm;
import sunnyday.common.model.UserBean;
import sunnyday.tools.util.ConnectUtil;

@Repository
public class GateRAO extends CommonRAO {
	@Resource(name = "redisCli")
	protected DCAdapter dc;
	
	@Value("#{config.must_arrive_valid_time}")
	private String must_arrive_valid_time;
	
	protected DCAdapter getDc() {
		return dc;
	}
	
	public void updateDataCenterUserInfoCache(Map<String, Map<String, UserBean>> cacheMode) {
		getDc().putCacheData(cacheMode);
	}

	public void updateDataCenterNetSwitchedMobileInfoCache(Map<String, Map<String, NetSwitchedMobileInfo>> numberPortability) {
		getDc().putCacheData(numberPortability);
	}

	public void updateDataCenterLocationInfoCache(Map<String, Map<String, LocationInfo>> locationInfo) {
		getDc().putCacheData(locationInfo);
	}

	public void updateDataCenterCheckMethodCache(Map<String, List<CheckMethod>> checkMethod) {
		getDc().putCacheData(checkMethod);
	}
	
	public void updateDataCenterTdInfoCache(Map<String, Map<String, TdInfo>> tdInfo) {
		getDc().putCacheData(tdInfo);
	}
	
	
	
	
	public void updateGateConfigCache(Map<String, Map<String, String>> gateConfig) {
		getDc().putCacheData(gateConfig);
	}

	 
	public void updateCacheData(Map<String, ?>  map) {
		getDc().putCacheData(map);
	}


	
	/**
	 * 得到redis中所有的余额信息
	 * @return
	 */
	public List<UserBalanceForm> getCurrentDcBalanceList() {
		List<UserBalanceForm> result = new ArrayList<UserBalanceForm>(); 
		try{
			Set<String> keySet = HSSmembers("keys:balance");
			for(String each : keySet){
				Map<String, String> tmp = HSHgetAll(each);
				if(!tmp.isEmpty()){
					UserBalanceForm ubf = new UserBalanceForm();  
					for(String field : tmp.keySet() ){
						String str = tmp.get(field);
						Method m = UserBalanceForm.class.getMethod("set" + Utils.FirstUpperCase(new String(field)), String.class);
						m.invoke(ubf, str);
					}
					result.add(ubf);
				}
			}
			 
		}catch(Exception e){
			log.info("",e);
		} 
		return result;
	}
	
	public void updateDataUserUserBalance(List<UserBalanceForm> dcUpdateList) {
		//待写：将last_balance更新到数据中心，将add_balance增加到cur_balance。
		if(dcUpdateList != null && dcUpdateList.size() > 0){
			String[] fields = new String[]{"last_balance"};
			Map<String, UserBalanceForm> cache = new HashMap<String, UserBalanceForm>();
			for(UserBalanceForm each : dcUpdateList){
				cache.put("balance:" + each.getUser_id(), each);
			}
//			System.out.println("将更新到数据中心，将add_balance增加到cur_balance ： " + cache);
			getDc().updateCachesValueInStringMode(fields, cache);
			//充值
			getDc().rechargingByBatch(dcUpdateList);
		}
	}
	
	public void updateUserUserBalance(UserBalanceForm each) {
		try {
			String key = "balance:" + each.getUser_id();
			String field_last_balance = "last_balance";
			String field_diffBalance = "diffBalance";
			double increment_add_balance = each.getAdd_balance();
			double increment_changeBalance = -each.getChangeBalance();
			double now_cur_balance = HSHINCRBYFLOAT(key, field_last_balance, increment_add_balance);
			double now_diff_balance = HSHINCRBYFLOAT(key, field_diffBalance, increment_changeBalance);
			if (Math.abs(now_cur_balance - each.getCur_balance()) < 1.0E-5) {
				log_info.info("[" + each.getUser_id() + "]余额同步失败，期望值为[" + each.getCur_balance() + "]，实际值为[" + now_cur_balance + "]，同步期间余额变动为[" + now_diff_balance + "]");
				long result = HSHSet(key, field_last_balance, each.getCur_balance());
				log_info.info("[" + each.getUser_id() + "]余额同步重置为[" + each.getCur_balance() + "],结果：" + result);
			}
		} catch (Exception e) {
			log.error("updateUserUserBalance" + each, e);
		}
		 
	}
	
	 
	
	public Object getCacheData(String key){
		return  dc.getCacheData(key);
	}
	public void addNewUserBalanceToDC(Map<String, ?> cacheMap) {
		try{
			for (String eachKey : cacheMap.keySet()) {
				Object cache = cacheMap.get(eachKey);
				Field[] fields = cache.getClass().getDeclaredFields();
				Map<String, String> hash = new HashMap<String, String>();
				for (Field eachField : fields) {
					String strValue;
					try {
						Class<?>[] cs = null;
						Object[] params = null; 
						Method m = cache.getClass().getMethod("get"	+ Utils.FirstUpperCase(eachField.getName()), cs);
						strValue = m.invoke(cache, params).toString();
						hash.put(eachField.getName(), strValue);
					} catch (Exception e) {
						log.info("",e);
					} 
				}
				HSHmset(eachKey, hash);
				HSSadd("keys:balance", eachKey);
			}
		}catch(Exception e){
			log.info("",e);
		} 
	}
	
	public List<Object> getReportByUserId(String queueName,int count) throws Exception{
		return dc.getQueueElements(queueName,count);
	}
	
	public List<Object> getDeliverByUserId(String queueName,int count){
		List<Object> result = null;
		try {
			result = dc.getQueueElements(queueName,count);
		} catch (Exception e) {
			log.error(queueName,e);
		}
		return result;
	}
	
	
	/**
	 * 根据hashKey列表，循环执行如下操作<br>
	 * 删除redis中存放某一批次必达短信回执对象<br>
	 * 删除redis中存放某一批次必达短信时效和下发时间的键值对<br>
	 * 
	 * @param hashKeyList
	 */
	public void delArriveReplyFromRedis(List<String> hashKeyList) {
		if(hashKeyList == null || hashKeyList.size() < 1){
			return ;
		}
		
		long time1 = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			Pipeline pipeline = jedis.pipelined();
			for (String hashKey : hashKeyList) {
				if(StringUtils.isBlank(hashKey)){
					continue;
				}
				
				//删除redis中存放某一批次必达短信回执对象
				pipeline.del(hashKey.getBytes());
				
				//删除redis中存放某一批次必达短信时效和下发时间的键值对
				String arriveNumber = hashKey.split(":")[2];
				pipeline.hdel(must_arrive_valid_time.getBytes(), arriveNumber.getBytes());
			}
			pipeline.sync();
		} catch (Exception e) {
			log_info.error("delArriveReplyFromRedis", e);
		} finally {
			getDc().returnJedis(jedis);
		}

		long time2 = System.currentTimeMillis();
		if (time2 - time1 > 3000) {
			log_info.warn("delArriveReplyFromRedis too long time  " + (time2 - time1) + " ms");
		}
	}
	
}
