package sunnyday.gateway.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import sunnyday.adapter.redis.Chargable;
import sunnyday.adapter.redis.DCAdapter;
import sunnyday.adapter.redis.impl.Utils;
import sunnyday.common.model.CheckMethod;
import sunnyday.common.model.LocationInfo;
import sunnyday.common.model.NetSwitchedMobileInfo;
import sunnyday.common.model.ServiceInfo;
import sunnyday.common.model.SmsTemplateInfo;
import sunnyday.common.model.SubmitBean;
import sunnyday.common.model.TdInfo;
import sunnyday.common.model.ThreadControllerForm;
import sunnyday.common.model.UserBalanceForm;
import sunnyday.common.model.UserBalanceInfo;
import sunnyday.common.model.UserBean;
import sunnyday.tools.util.CommonLogFactory;
@Repository
public class CommonRAO implements Serializable{
	 
	private static final long serialVersionUID = -6549951661047021543L;
	protected Logger log_info = CommonLogFactory.getLog(CommonRAO.class);
	protected Logger log = CommonLogFactory.getLog("receiver");
	
	@Autowired
	protected DCAdapter dc;
	
	public DCAdapter getDc() {
		return dc;
	}

	public void updateThreadControllerStatus(Map<String, ?> tcInfo) {
		if(tcInfo != null && tcInfo.size() > 0){
			String[] fields = new String[]{"status"};
			dc.updateCachesValue(fields, tcInfo);
		}
		
	}

	/**
	 * 该方法进行重新写,不支持正则模糊匹配
	 * 1.首先获取到指定key set 中的所有元素
	 * 2.其次执行下进行队列大小为0的过滤，即通过改方法获取的
	 * key 对应的队列的大小当时时间是不为零的
	 * @param key
	 * @return
	 */
	public Set<String> HSKeys(String key){
		Set<String> result = new HashSet<String>();
		List<String> values= HSSmembersList(key);
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			long time1 =System.currentTimeMillis();
			if(values!=null && values.size()>0){
				Pipeline p = jedis.pipelined();
				for(String tk:values){
					p.llen(tk);
				}
				List<Object> matchList = p.syncAndReturnAll();
				for(int i=0;i<matchList.size();i++){
					Long size = 0l ;
					try {
						size = (Long) matchList.get(i);
						if(size>0){
							result.add(values.get(i));
						}
					} catch (Exception e) {
					}
				}
			}
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSKeys too long time  key:"+key+";"+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSKeys,[key="+key+"]",e);
		}finally{
			dc.returnJedis(jedis);
		}
		return result;
	}
	
	public Long HSSADD(String key,String members){
		Long values=  0l;
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			long time1 =System.currentTimeMillis();
			values = jedis.sadd(key, members);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSSADD too long time  keys:"+key+"; members="+members+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSSADD,[key="+key+"],members="+members,e);
		}finally{
			dc.returnJedis(jedis);
		}
		
		return values;
	}
	public long HSLen(String key){
		long values= 0l;
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			long time1 =System.currentTimeMillis();
			values=jedis.llen(key);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSKeys too long time  keys:"+key+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSKeys,[key="+key+"]",e);
		}finally{
			dc.returnJedis(jedis);
		}
		
		return values;
	}
	
	public Set<String> HSSmembers(String key){
		Set<String> values= new HashSet<String>();
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			long time1 =System.currentTimeMillis();
			values=jedis.smembers(key);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSSmembers too long time  keys:"+key+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSSmembers,[key="+key+"]",e);
		}finally{
			dc.returnJedis(jedis);
		}
		return values;
	}
	
	public  void doIdempotent(List<SubmitBean> list,int second){
		Jedis jedis = null;
		try {
			if(list!=null&&list.size()>0){
				jedis = dc.getJedis();
				Pipeline p = jedis.pipelined();
				String key = null ;
				Set<String> key_set = new HashSet<String>();
				for(SubmitBean bean:list){
					if(bean.getExtraFields().get("transaction_id")!=null){
						key = MD5.convert((String)bean.getExtraFields().get("transaction_id")+bean.getMobilesString());
						key_set.add(key);
					}else {
						key = MD5.convert(UUID.randomUUID().toString()+bean.getMobilesString());
					}
				    p.exists(key);
				}
				List<Object> matchList = p.syncAndReturnAll();
				for(int i=0;i< matchList.size();i++){
					boolean t = Boolean.parseBoolean(matchList.get(i).toString());
					if(t){ 
						list.get(i).getExtraFields().put("repeat-data", 1);
					}else {
						list.get(i).getExtraFields().put("repeat-data", 0);
					}
				}
				for(String set:key_set){
					p.set(set, "1");
					p.expire(set, second);
				}
				p.sync();
			}
		} catch (Exception e) {
			log.error("doIdempotent",e);
		}finally{
			dc.returnJedis(jedis);
		}
	}
	
	public List<String> HSSmembersList(String key) {
		List<String> values = new ArrayList<String>();
		Set<String> tmp = HSSmembers(key);
		if (tmp != null && tmp.size() > 0) {
			for (String v : tmp) {
				values.add(v);
			}
		}
		return values;
	}
	
	public long HSSrem(String regxKey,String key){
		long result= 0;
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			long time1 =System.currentTimeMillis();
			result=jedis.srem(regxKey,key);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSSrem too long time  keys:"+key+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSSrem,[regxKey="+regxKey+", key="+key+"]",e);
		}finally{
			dc.returnJedis(jedis);
		}
		
		return result;
	}
	
	public Map<String, String> HSHgetAll(String key){
	   Map<String, String> result = new HashMap<String,String>();
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			long time1 =System.currentTimeMillis();
			result=jedis.hgetAll(key);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSHgetAll too long time  keys:"+key+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSHgetAll,[key="+key+"]",e);
		}finally{
			dc.returnJedis(jedis);
		}
		
		return result;
	}
	
	public long HSDel(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			long time1 = System.currentTimeMillis();
			result = jedis.del(key);
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 2000) {
				log.warn("HSDel too long time  keys:" + key + "; "+ (time2 - time1) + " ms");
			}
		} catch (Exception e) {
			log.error("HSDel,[key=" + key + "]", e);
		} finally {
			dc.returnJedis(jedis);
		}

		return result;
	}
	
	
	 
	
/*	*//**
	 * 获取到去除了key关键字后的数据
	 * @param key
	 * @return
	 *//*
	public Set<String> getMembersWithOutKey(String key){
		Set<String> result = new HashSet<String>();
		Set<String> values = HSKeys(key);
		 if(values!=null){
			 for(String str:values){
				 result.add(str.substring(key.length()-1, str.length()));
			 }
		 }
		return result;
	}*/


	public List<ThreadControllerForm>  getNewThreadControllerInfo(String regxKey) {
		List<ThreadControllerForm> tmpList = null;
		 try {
			tmpList = dc.getRWCache(regxKey, ThreadControllerForm.class);
		} catch (Exception e) {
			log.error("getNewThreadControllerInfo,[key=" + regxKey + "]", e);
		} 
		return tmpList;
	}
	
	public List<ThreadControllerForm>  getNewThreadControllerInfoByKey(String regxKey) {
		List<ThreadControllerForm> tmpList = null;
		 try {
			tmpList = dc.getRWCacheByKey(regxKey, ThreadControllerForm.class);
		} catch (Exception e) {
			log.error("getNewThreadControllerInfoByKey,[key=" + regxKey + "]", e);
		} 
		return tmpList;
	}
	
	
	
	
	public List<Object> getSmsListByYwcode(String queueName,int count) throws Exception{
		return dc.getQueueElements(queueName,count);
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
	
	 
	public <T> boolean addSubmitDoneList(String listName,List<T> list){
		return dc.addQueueElements(listName, list);
	}
	
	
	
	
	/**
	 * 回写到发送队列中
	 * @param send_queue_name 队列名称
	 * @param list
	 * @return
	 */
	public boolean submitMessageToDc(String send_queue_name, List<Object> list) {
		return dc.addQueueElements(send_queue_name, list);
	}
	
	public List<Object> changeBalance(Collection<? extends Chargable> list){
		return dc.chargingByBatch(list);
	}
	
	public boolean addSendDoneList(String listName,List<Object> list ){
		
		return  dc.addQueueElements(listName, list);
	}
	
	public boolean addReceiveReportList(String listName,List<Object> list){
		return dc.addQueueElements(listName, list);
	}
	
	public boolean addReceiveDeliverList(String listName,List<Object> list){
		return dc.addQueueElements(listName, list);
	}
	
	public boolean addSendDeliverRespList(String listName,List<Object> list){
		return dc.addQueueElements(listName, list);
	}
	
	public boolean addSendReportRespList(String listName,List<Object> list){
		return dc.addQueueElements(listName, list);
	}
	
	public boolean addSubmitTimeList(String listName,List<?> list){
		return dc.addQueueElements(listName, list);
	}
	public double HSHINCRBYFLOAT(String key,String field,double increment) {
		double result = 0;
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			long time1 = System.currentTimeMillis();
			result = jedis.hincrByFloat(key, field, increment);
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 2000) {
				log.warn("HSHINCRBYFLOAT too long time  keys:" + key + "; "+ (time2 - time1) + " ms; result: "+result);
			}
		} catch (Exception e) {
			log.error("HSHINCRBYFLOAT,[key=" + key + "]", e);
		} finally {
			dc.returnJedis(jedis);
		}

		return result;
	}
	
	public long HSHSet(String key,String field,double value) {
		long result = 0l;
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			long time1 = System.currentTimeMillis();
			result = jedis.hset(key, field, String.valueOf(value));
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 2000) {
				log.warn("HSHSet too long time  keys:" + key + "; "+ (time2 - time1) + " ms; result: "+result);
			}
		} catch (Exception e) {
			log.error("HSHSet,[key=" + key + "]", e);
		} finally {
			dc.returnJedis(jedis);
		}

		return result;
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
	
	public Map<String,UserBalanceForm> getCurrentDcBalanceMap() {
		Map<String,UserBalanceForm> map = new HashMap<String, UserBalanceForm>();
		List<UserBalanceForm> list =  this.getCurrentDcBalanceList(); 
		if(list!=null&&list.size()>0){
			for(UserBalanceForm  balace:list){
				map.put(balace.getUser_id(), balace);
			}
		}
		return map;
	}
	

	public Map<String, UserBalanceInfo> getLastUserBalanceInfo(String regxKey) {
		Map<String, UserBalanceInfo> result = new HashMap<String, UserBalanceInfo>();
		try{
			Set<String> keys = HSSmembers(regxKey);
			for(String key : keys){
				if(key==null||"".equals(key)){
					try {
						long count= HSSrem(regxKey, key);
						log.warn("Balance key is null . ["+key+"] ,remove it from keys:balance, result="+count);
					} catch (Exception e) {
					}
					continue ;
				}
				 
				Map<String, String> balanceInfo = HSHgetAll(key);
				//该主键没有对应的值
				if(balanceInfo==null||balanceInfo.isEmpty()){
					try {
						long count=HSSrem(regxKey, key);
						log.warn("balanceInfo is empty ,remove  ["+key+"] from keys:balance, result="+count);
					} catch (Exception e) {
					}
					continue ;
				}
				
				String user_id ="";
				String cur_balance ="";
				try {
					UserBalanceInfo ubi = new UserBalanceInfo();
					user_id = balanceInfo.get("user_id");
					if(user_id==null||"".equals(user_id)){
						try {
							long count=HSDel(key);
							log.warn("user_id  is null and del key ["+key+"] from redis, result="+count);
						} catch (Exception e) {
						}
						
						continue ;
					}
					cur_balance = balanceInfo.get("cur_balance");
					ubi.setUser_id(user_id);
					ubi.setBalance(Double.parseDouble(cur_balance));
					result.put(ubi.getUser_id(), ubi);
				} catch (Exception e) {
					log.warn("[key="+key+",user_id="+user_id+",cur_balance="+cur_balance+"']",e);
				}
				
			}
		}catch(Exception e){
				log.error("["+regxKey+"]",e);
				result = new HashMap<String, UserBalanceInfo>();
		} 
		return result;
	}
	
	public String queryReceiveTime(String msgId){
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
//			Pipeline pipeline = jedis.pipelined();
			String time = jedis.hget("arrive:valid_time", msgId);
//			pipeline.sync();
			return time;
		} catch (Exception e) {
//			log_info.error("queryArriveReplyFromRedis", e);
			return null;
		} finally {
			getDc().returnJedis(jedis);
		}
	}
	
	public void updateRemoteBalance(Map<String, UserBalanceInfo> localBalanceCache) {
		dc.chargingByBatch(localBalanceCache.values());
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, UserBean> getUserInfo(){
		return (Map<String, UserBean>)dc.getCacheData(ParamUtil.REDIS_KEY_USER_INFO);
	}

	@SuppressWarnings("unchecked")
	public List<CheckMethod> getCheckMethod() {
		return (List<CheckMethod>)dc.getCacheData(ParamUtil.REDIS_KEY_CHECK_METHOD_INFO);
	}

	@SuppressWarnings("unchecked")
	public Map<String, LocationInfo> getLocationInfo() {
		return (Map<String, LocationInfo>)dc.getCacheData(ParamUtil.REDIS_KEY_LOCATION_INFO_LIST);
	}

	@SuppressWarnings("unchecked")
	public Map<String, TdInfo> getTdInfo() {
		return (Map<String, TdInfo>)dc.getCacheData(ParamUtil.REDIS_KEY_TD_INFO);
	}

	@SuppressWarnings("unchecked")
	public Map<String, NetSwitchedMobileInfo> getNetSwitchedMobileInfo() {
		return (Map<String, NetSwitchedMobileInfo>)dc.getCacheData(ParamUtil.REDIS_KEY_NET_SWITCHED_MOBILE);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getGateConfig() {
		return (Map<String, String>)dc.getCacheData(ParamUtil.REDIS_KEY_GATE_CONFIG);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getAdminIdMobile() {
		return (Map<String, String>)dc.getCacheData(ParamUtil.REDIS_KEY_ADMIN_ID_MOBILE);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> getAdminIdUsers() {
		return (Map<String, List<String>>)dc.getCacheData(ParamUtil.REDIS_KEY_ADMIN_ID_USERS);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, SmsTemplateInfo> getTemplateCommon() {
		return  (Map<String, SmsTemplateInfo>) dc.getCacheData(ParamUtil.REDIS_KEY_SMS_COMMON_TEMPLATE_COMMON);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, SmsTemplateInfo> getTemplateUser() {
		return  (Map<String, SmsTemplateInfo>) dc.getCacheData(ParamUtil.REDIS_KEY_SMS_USER_TEMPLATE_COMMON);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, ServiceInfo> getServiceInfo() {
		return  (Map<String, ServiceInfo>) dc.getCacheData(ParamUtil.REDIS_KEY_SERVICE_INFO);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getTemplateServices() {
		return  (Map<String, String>) dc.getCacheData(ParamUtil.REDIS_KEY_TEMPLATE_SERVICES);
	}


	/**
	 * 将值 value 关联到 key ，并将 key 的生存时间设为 seconds (以秒为单位)。 如果 key 已经存在， SETEX
	 * 命令将覆写旧值。
	 *
	 * @param key
	 * @param seconds
	 * @param value
	 * @return 设置成功时返回 OK 。 当 seconds 参数不合法时，返回一个错误。
	 */
	public String setex(String key, int seconds, String value) {
		String l = null;
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			l = jedis.setex(key, seconds, value);
		} catch (Exception e) {
			e.printStackTrace();
			log_info.error("",e);
		} finally {
			if (jedis != null) {
				try {
					dc.returnJedis(jedis);
				} catch (Exception e) {
					e.printStackTrace();
					log_info.error("",e);
				}
			}
		}
		return l;
	}

	/**
	 * 将 key 的值设为 value ，当且仅当 key 不存在。 若给定的 key 已经存在，则 SETNX 不做任何动作。
	 *
	 * @param key
	 * @param value
	 * @return 设置成功，返回 1 。 设置失败，返回 0 。
	 */
	public Long setnx(String key, String value) {
		Long l = null;
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			l = jedis.setnx(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			log_info.error("",e);
		} finally {
			if (jedis != null) {
				try {
					dc.returnJedis(jedis);
				} catch (Exception e) {
					e.printStackTrace();
					log_info.error("",e);
				}
			}
		}
		return l;
	}

	/*
	 * 设置key 过期时间
	 */
	public Long expire(String key, int value) {
		Long l = null;
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			l = jedis.expire(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			log_info.error("",e);
		} finally {
			if (jedis != null) {
				try {
					dc.returnJedis(jedis);
				} catch (Exception e) {
					e.printStackTrace();
					log_info.error("",e);
				}
			}
		}
		return l;
	}

	/*
	 * 获取key
	 */
	public String getValue(String key) {
		String str = null;
		Jedis jedis = null;
		try {
			jedis = dc.getJedis();
			str = jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
			log_info.error("",e);
		} finally {
			if (jedis != null) {
				try {
					dc.returnJedis(jedis);
				} catch (Exception e) {
					e.printStackTrace();
					log_info.error("",e);
				}
			}
		}
		return str;
	}


}
