package sunnyday.controller.filter;

import org.slf4j.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import sunnyday.tools.util.CommonLogFactory;

public class EhcacheRepeatMsgFilter2 implements IFilter {
	private  Logger log = CommonLogFactory.getCommonLog(EhcacheRepeatMsgFilter2.class);
	private CacheManager manager = null;
	private String cacheName = null;
//	private int cacheCount = 1;
	
	public void doStart(String filterName) {
		log.info("-------------- cache starting ----------------");
		if(filterName != null){
			cacheName = filterName; 
			String path=System.getProperty("my.dir") + "/config/ehcache.xml";
			manager = CacheManager.create(path);
			//manager = CacheManager.create(EhcacheFactory.class.getClassLoader().getResourceAsStream("ehcache.xml"));
//		for(int index = 0; index < cacheCount; index++){
//			Cache cache = EhcacheFactory.newCache(filterName + "_" + index);
			Cache cache = EhcacheFactory.newCache(cacheName);
			manager.addCache(cache);
//		}
			
			for(String cacheName : manager.getCacheNames()){
				log.info("cache all right  ---- " + cacheName + " ----- size = " + manager.getCache(cacheName).getSize());
			}
		}else{
			throw new RuntimeException("filter name is null");
		}
	}

	
	public void doStop() {
		log.info("[ program shutdown ] repeat cache write back start ...");
		manager.shutdown();
	}

	
	public Object doFilter(String key, int idleSecond, int repeatTime) {
		String result = null;
		
		long current_time = System.currentTimeMillis();
		Cache cache = manager.getCache(cacheName);
		try{
			Element e = cache.get(key);
//			System.out.println(e);
			String ehcacheValue = "";
			if(isNeedReset(e, idleSecond, repeatTime)){
//				log.info("[repeatFilter] 缓存重置 -- key = " + key);
				ehcacheValue = CacheValueUtil.getNewValue(repeatTime);
				ehcacheValue = CacheValueUtil.addValueElement(ehcacheValue, current_time);
//				ehcacheValue.add(current_time);
			}else{
				ehcacheValue = (String)e.getObjectValue();
				if(ehcacheValue != null){
					
					int amount = CacheValueUtil.getSendTimes(ehcacheValue);
					long frist_time = CacheValueUtil.getFirstElement(ehcacheValue);
					
					long time_difference = (current_time - frist_time) / 1000;
					log.debug("[repeatFilter] ----------  距离最早下发记录的时间差：" + time_difference + " 秒");
					
					if(time_difference < idleSecond && amount < repeatTime){
						log.debug("[repeatFilter]未达到过滤重复次数，成功下发并在队列中添加当前时间");
//						ehcacheValue.add(current_time);
						ehcacheValue = CacheValueUtil.addValueElement(ehcacheValue, current_time);
					}else if(time_difference < idleSecond && amount >= repeatTime){
						log.debug("[repeatFilter]重复下发驳回");
//						result =  "重复下发驳回";
						result = amount + "_" + ehcacheValue;
					}else if(time_difference >= idleSecond){
						log.debug("[repeatFilter]首条下发成功记录超时，移除");
						ehcacheValue = CacheValueUtil.removeHeadElement(ehcacheValue);
						ehcacheValue = CacheValueUtil.addValueElement(ehcacheValue, current_time);
					}
				}
			}
			e = new Element(key, ehcacheValue, idleSecond, 0);
//			log.info("[repeatFilter]过滤完成后的Elment = " + e);
			
			cache.put(e);
		}catch(Exception e){
			log.error("",e);
		}
		
		return result;
	}
	private boolean isNeedReset(Element e, int interval_time, int repeat_time) {
		boolean result = false;
		
		if(e == null){
			result = true;
		}else {
			String value = (String)e.getObjectValue();
			
			boolean isIntervalChanged = e.getTimeToIdle() != interval_time;
			boolean isRepeatNumChanged = (CacheValueUtil.getRepeatTime(value)) != repeat_time;
			
			result = isIntervalChanged || isRepeatNumChanged;
		}
		
		return result;
	}

	
	public Object doFilter(int pk_toal,String key_content, String key_word, int idleSecond,
			int repeatTime) {
		return null;
	}
}
