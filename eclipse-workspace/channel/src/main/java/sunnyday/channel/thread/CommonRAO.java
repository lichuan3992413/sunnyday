package sunnyday.channel.thread;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.spi.LocationInfo;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import sunnyday.adapter.redis.Chargable;
import sunnyday.adapter.redis.DCAdapter;
import sunnyday.adapter.redis.impl.Utils;
import sunnyday.channel.model.ThreadControllerForm;
import sunnyday.common.model.UserBean;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;
@Repository
public abstract class CommonRAO {
	
	protected Logger log = CommonLogFactory.getLog(CommonRAO.class);
	protected abstract DCAdapter getDc();
	 
	
	public void updateThreadControllerStatus(Map<String, ?> tcInfo) {
		if(tcInfo != null && tcInfo.size() > 0){
			String[] fields = new String[]{"status"};
			getDc().updateCachesValue(fields, tcInfo);
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
			jedis = getDc().getJedis();
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
			getDc().returnJedis(jedis);
		}
		return result;
	}
	
	public Long HSSADD(String key,String members){
		Long values=  0l;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 =System.currentTimeMillis();
			values = jedis.sadd(key, members);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSSADD too long time  keys:"+key+"; members="+members+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSSADD,[key="+key+"],members="+members,e);
		}finally{
			getDc().returnJedis(jedis);
		}
		
		return values;
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
	
	public long HSLen(String key){
		long values= 0l;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 =System.currentTimeMillis();
			values=jedis.llen(key);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSKeys too long time  keys:"+key+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSKeys,[key="+key+"]",e);
		}finally{
			getDc().returnJedis(jedis);
		}
		
		return values;
	}
	
	public Set<String> HSSmembers(String key){
		Set<String> values= new HashSet<String>();
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 =System.currentTimeMillis();
			values=jedis.smembers(key);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSSmembers too long time  keys:"+key+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSSmembers,[key="+key+"]",e);
		}finally{
			getDc().returnJedis(jedis);
		}
		
		return values;
	}
	
	public long HSSrem(String regxKey,String key){
		long result= 0;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 =System.currentTimeMillis();
			result=jedis.srem(regxKey,key);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSSrem too long time  keys:"+key+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSSrem,[regxKey="+regxKey+", key="+key+"]",e);
		}finally{
			getDc().returnJedis(jedis);
		}
		
		return result;
	}
	
	public Map<String, String> HSHgetAll(String key){
	   Map<String, String> result = new HashMap<String,String>();
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 =System.currentTimeMillis();
			result=jedis.hgetAll(key);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSHgetAll too long time  keys:"+key+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSHgetAll,[key="+key+"]",e);
		}finally{
			getDc().returnJedis(jedis);
		}
		
		return result;
	}
	
	public long HSDel(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 = System.currentTimeMillis();
			result = jedis.del(key);
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 2000) {
				log.warn("HSDel too long time  keys:" + key + "; "+ (time2 - time1) + " ms");
			}
		} catch (Exception e) {
			log.error("HSDel,[key=" + key + "]", e);
		} finally {
			getDc().returnJedis(jedis);
		}

		return result;
	}
	
	  
	public List<ThreadControllerForm>  getNewThreadControllerInfo(String regxKey) {
		List<ThreadControllerForm> tmpList = null;
		 try {
			tmpList = getDc().getRWCache(regxKey, ThreadControllerForm.class);
		} catch (Exception e) {
			log.error("getNewThreadControllerInfo,[key=" + regxKey + "]", e);
		} 
		return tmpList;
	}
	
	/**
	 * 不支持正则
	 * @param set_key
	 * @return
	 */
	public List<ThreadControllerForm>  getNewThreadControllerInfoByKey(String set_key) {
		List<ThreadControllerForm> tmpList = null;
		 try {
			tmpList = getCache(set_key, ThreadControllerForm.class);
		} catch (Exception e) {
			log.error("getNewThreadControllerInfoByKey,[key=" + set_key + "]", e);
		} 
		return tmpList;
	}
	
	/**
	 * 返回所期望的key
	 * 删除不存在的key
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> List<T> getCache(String key,Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			Set<String> keySet = jedis.smembers(key);
			Pipeline pipeline = jedis.pipelined();
            List<String> tmp_list = new ArrayList<String>();
			for (String each : keySet) {
				pipeline.hgetAll(each.getBytes());
				tmp_list.add(each);
			}
			List<Object> tmpList = pipeline.syncAndReturnAll();
			for (int i=0;i<tmpList.size();i++) {
				Object each  =  tmpList.get(i);
				if(each!=null){
					@SuppressWarnings("unchecked")
					Map<byte[], byte[]> tmp = (Map<byte[], byte[]>) each;
					T o = clazz.newInstance();
					Method[] ms = clazz.getMethods();
					for (byte[] field : tmp.keySet()) {
						Object obj = Utils.toObject(tmp.get(field));
						for (Method m : ms) {
							if (m.getName().equals("set"+ Utils.FirstUpperCase(new String(field)))) {
								m.invoke(o, obj);
							}
						}
					}
					result.add(o);
				}else{
					jedis.srem(key, tmp_list.get(i));
				}
				
			}
		} catch (Exception e) {
			log.error("getCache,key=[" + key+ "]", e);
		} finally {
			getDc().returnJedis(jedis);
		}
		return result;
	}
	
	
	
	public List<Object> getSmsListByYwcode(String queueName,int count) throws Exception{
		return getDc().getQueueElements(queueName,count);
	}
	
	public List<Object> getReportByUserId(String queueName,int count) throws Exception{
		return getDc().getQueueElements(queueName,count);
	}
	
	public List<Object> getDeliverByUserId(String queueName,int count){
		List<Object> result = null;
		try {
			result = getDc().getQueueElements(queueName,count);
		} catch (Exception e) {
			log.error(queueName,e);
		}
		return result;
	}
	
	public boolean addSubmitDoneList(String listName,List<Object> list){
		return getDc().addQueueElements(listName, list);
	}
	
	/**
	 * @param send_queue_name  
	 * @param list
	 * @return
	 */
	public boolean submitMessageToDc(String send_queue_name, List<Object> list) {
		return getDc().addQueueElements(send_queue_name, list);
	}
	
	public List<Object> changeBalance(Collection<? extends Chargable> list){
		return getDc().chargingByBatch(list);
	}
	
	public boolean addSendDoneList(String listName,List<Object> list ){
		return  getDc().addQueueElements(listName, list);
	}
	
	public boolean addReceiveReportList(String listName,List<Object> list){
		return getDc().addQueueElements(listName, list);
	}
	
	public boolean addReceiveDeliverList(String listName,List<Object> list){
		return getDc().addQueueElements(listName, list);
	}
	
	public boolean addSendDeliverRespList(String listName,List<Object> list){
		return getDc().addQueueElements(listName, list);
	}
	
	public boolean addSendReportRespList(String listName,List<Object> list){
		return getDc().addQueueElements(listName, list);
	}

	/*public Map<String, UserBalanceInfo> getLastUserBalanceInfo(String regxKey) {
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
				//������û�ж�Ӧ��ֵ
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
	
	public void updateRemoteBalance(Map<String, UserBalanceInfo> localBalanceCache) {
		getDc().chargingByBatch(localBalanceCache.values());
	}*/
	
	@SuppressWarnings("unchecked")
	public Map<String, UserBean> getUserInfo(){
		return (Map<String, UserBean>)getDc().getCacheData(ParamUtil.REDIS_KEY_USER_INFO);
	}

	

	@SuppressWarnings("unchecked")
	public Map<String, LocationInfo> getLocationInfo() {
		return (Map<String, LocationInfo>)getDc().getCacheData(ParamUtil.REDIS_KEY_LOCATION_INFO_LIST);
	}


	
	@SuppressWarnings("unchecked")
	public Map<String, String> getChargeTermidMap() {
		return (Map<String, String>)getDc().getCacheData(ParamUtil.REDIS_KEY_ChARGE_TERMID);
	}

	

	@SuppressWarnings("unchecked")
	public Map<String, String> getGateConfig() {
		return (Map<String, String>)getDc().getCacheData(ParamUtil.REDIS_KEY_GATE_CONFIG);
	}
	
}
