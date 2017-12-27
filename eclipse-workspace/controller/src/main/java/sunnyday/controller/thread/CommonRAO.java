package sunnyday.controller.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import sunnyday.adapter.redis.DCAdapter;
import sunnyday.adapter.redis.exception.NoSuchQueueException;
import sunnyday.common.model.ThreadControllerForm;
import sunnyday.tools.util.CommonLogFactory;


public abstract class CommonRAO {
	protected Logger log = CommonLogFactory.getCommonLog("daoLog");
	protected Logger log_info = CommonLogFactory.getCommonLog("infoLog");
	
	protected abstract DCAdapter getDc();

	public List<ThreadControllerForm> getAppThreadList() {
		return getDc().getRWCache("threads:*", ThreadControllerForm.class); 
	}

	public void updateDataCenterControlCache(ThreadControllerForm each) {
		String[] fields = new String[]{"sn", "server_ip", "thread_name", "action", "status", "thread_param", "thread_type", "group_id", "app_name"};
		Map<String, ThreadControllerForm> cache = new HashMap<String, ThreadControllerForm>();
		cache.put(each.getCacheKey(), each);
		getDc().updateCachesValue(fields, cache);
	}

	public void removeDataCenterControlCache(ThreadControllerForm each) {
		getDc().removeCacheKey(each.getCacheKey());
	}

	public void addNewThreadToDC(Map<String, ?> newTcInfo) {
		getDc().putRWCacheData(newTcInfo);
	}
	
	
	public boolean addObject2List(String listName,Object object ){
		return getDc().addQueueElements(listName, object);
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
						Object object = matchList.get(i);
						if(object!=null){
							size = (Long) matchList.get(i);
							if(size>0){
								result.add(values.get(i));
							}
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
	
	/**
	 * 获取指定set 对应的队列的大小为0
	 * @param key
	 * @return
	 */
	public Map<String, Long> DealSetKeys(String key){
		/*Set<String> result = new HashSet<String>();*/
		Map<String, Long> result = new HashMap<String, Long>();
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
						Object object = matchList.get(i);
						if(object!=null){
							size = (Long) matchList.get(i);
							result.put(values.get(i), size);	
						}else{
							result.put(values.get(i), 0L);
						}
						
					} catch (Exception e) {
					}
				}
			}
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("DealSetKeys too long time  key:"+key+";"+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("DealSetKeys,[key="+key+"]",e);
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
	public Long HSSADD(String key,Set<String> set){
		Long values=  0l;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 =System.currentTimeMillis();
			if(set!=null&&!set.isEmpty()){
				Pipeline p = jedis.pipelined();
				for(String m:set){
					p.sadd(key, m);
				}
				p.sync();
			}
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSSADD too long time  keys:"+key+"; members="+set+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSSADD,[key="+key+"],members="+set,e);
		}finally{
			getDc().returnJedis(jedis);
		}
		return values;
	}
	
	public Long HSSREM(String key,String members){
		Long values=  0l;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 =System.currentTimeMillis();
			values = jedis.srem(key, members);
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSSREM too long time  keys:"+key+"; members="+members+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSSREM,[key="+key+"],members="+members,e);
		}finally{
			getDc().returnJedis(jedis);
		}
		
		return values;
	}
	
	public Long HSSREM4SET(String key,String members){
		Long values=  0l;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 =System.currentTimeMillis();
			boolean t = jedis.exists(members);
			if(!t){
				values = jedis.srem(key, members);
			}
			long time2 =System.currentTimeMillis();
			if(time2-time1>2000){
				log.warn("HSSREM too long time  keys:"+key+"; members="+members+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSSREM,[key="+key+"],members="+members,e);
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
			if(time2-time1>3000){
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
			if(time2-time1>3000){
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
			if(time2-time1>3000){
				log.warn("HSSrem too long time  keys:"+key+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSSrem,[regxKey="+regxKey+", key="+key+"]",e);
		}finally{
			getDc().returnJedis(jedis);
		}
		
		return result;
	}
	  
	public boolean HSISMEMBER(String regxKey,String key){
		boolean result =  false;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 =System.currentTimeMillis();
			result=jedis.sismember(regxKey,key);
			long time2 =System.currentTimeMillis();
			if(time2-time1>3000){
				log.warn("HSISMEMBER too long time  keys:"+key+"; "+(time2-time1)+" ms");
			}
		} catch (Exception e) {
			log.error("HSISMEMBER,[regxKey="+regxKey+", key="+key+"]",e);
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
			if(time2-time1>3000){
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
			if (time2 - time1 > 3000) {
				log.warn("HSDel too long time  keys:" + key + "; "+ (time2 - time1) + " ms; result: "+result);
			}
		} catch (Exception e) {
			log.error("HSDel,[key=" + key + "]", e);
		} finally {
			getDc().returnJedis(jedis);
		}

		return result;
	}
	
	public long HSHDel(String key,String field) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 = System.currentTimeMillis();
			result = jedis.hdel(key, field);
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 3000) {
				log.warn("HSHDel too long time  keys:" + key + "; "+ (time2 - time1) + " ms; result: "+result);
			}
		} catch (Exception e) {
			log.error("HSHDel,[key=" + key + ",field= "+field+"]", e);
		} finally {
			getDc().returnJedis(jedis);
		}

		return result;
	}
	
	public String HSHmset(String key,Map<String, String> map) {
		String result = "";
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 = System.currentTimeMillis();
			result = jedis.hmset(key, map);
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 3000) {
				log.warn("HSHmset too long time  keys:" + key + "; "+ (time2 - time1) + " ms; result: "+result);
			}
		} catch (Exception e) {
			log.error("HSHmset,[key=" + key + "]", e);
		} finally {
			getDc().returnJedis(jedis);
		}

		return result;
	}
	
 
	public long HSHSet(String key,String field,double value) {
		long result = 0l;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 = System.currentTimeMillis();
			result = jedis.hset(key, field, String.valueOf(value));
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 3000) {
				log.warn("HSHSet too long time  keys:" + key + "; "+ (time2 - time1) + " ms; result: "+result);
			}
		} catch (Exception e) {
			log.error("HSHSet,[key=" + key + "]", e);
		} finally {
			getDc().returnJedis(jedis);
		}

		return result;
	}
	
	
	public long HSSadd(String key,String data) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 = System.currentTimeMillis();
			result = jedis.sadd(key, data);
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 3000) {
				log.warn("HSSadd too long time  keys:" + key + "; "+ (time2 - time1) + " ms; result: "+result);
			}
		} catch (Exception e) {
			log.error("HSSadd,[key=" + key + "]", e);
		} finally {
			getDc().returnJedis(jedis);
		}

		return result;
	}
	 
	public double HSHINCRBYFLOAT(String key,String field,double increment) {
		double result = 0;
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long time1 = System.currentTimeMillis();
			result = jedis.hincrByFloat(key, field, increment);
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 3000) {
				log.warn("HSHINCRBYFLOAT too long time  keys:" + key + "; "+ (time2 - time1) + " ms; result: "+result);
			}
		} catch (Exception e) {
			log.error("HSHINCRBYFLOAT,[key=" + key + "]", e);
		} finally {
			getDc().returnJedis(jedis);
		}

		return result;
	}
	 
 
	
	
	public List<Object> getObjectFromList(String queueName, int size) {
		List<Object> result = null;
		try {
			result = getDc().getQueueElements(queueName, size);
		} catch (Exception e) {
			log.error("getObjectFromList,[key=" + queueName + "]", e);
		}
		return result;
	}
	
	public List<Object> getMessageFromDataCenter(String queueName, int size) {
		List<Object> result = null;
		try {
			result = getDc().getQueueElements(queueName, size);
		} catch (Exception e) {
			log.error("getObjectFromList,[key=" + queueName + "]", e);
		}
		return result;
	}

	public boolean submitMessageToDc(String send_queue_name, List<Object> list) {
		return getDc().addQueueElements(send_queue_name, list);
	}
	


	public long getQueueLength(String queue) {
		long result = 0;
		try {
			result = getDc().getQueueSize(queue);
		} catch (NoSuchQueueException e) {
			e.printStackTrace();
		}
		return result; 
	}
}
