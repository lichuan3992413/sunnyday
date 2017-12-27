package sunnyday.controller.filter;

import org.slf4j.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import sunnyday.tools.util.CommonLogFactory;

public class EhcacheRepeatMsgFilter {
	private static Logger log = CommonLogFactory.getCommonLog(EhcacheRepeatMsgFilter.class);
	private static CacheManager manager = null;
	private static int cacheCount = 1;

	public static void doStart() {
		log.info("-------------- cache starting ----------------");
		String path=System.getProperty("my.dir") + "/config/ehcache.xml";
		manager = CacheManager.create(path);
		//manager = CacheManager.create(EhcacheRepeatMsgFilter.class.getClassLoader().getResourceAsStream("ehcache.xml"));
		for (int index = 0; index < cacheCount; index++) {
			Cache cache = EhcacheFactory.newCache(EhcacheRepeatMsgFilter.class.getName() + "_" + index);
			manager.addCache(cache);
		}

		for (String cacheName : manager.getCacheNames()) {
			log.info("cache all right  ---- " + cacheName + " ----- size = "+ manager.getCache(cacheName).getSize());
		}
	}

	public static void doStop() {
		log.info("[ program shutdown ] repeat cache write back start ...");
		manager.shutdown();
	}

	public static Object doFilter(String key, int idleSecond, int repeatTime) {
		String result = null;
		String cacheIndex = EhcacheRepeatMsgFilter.class.getName() + "_0";

		long current_time = System.currentTimeMillis();
		Cache cache = manager.getCache(cacheIndex);
		try {
			Element e = cache.get(key);
			String ehcacheValue = "";
			if (isNeedReset(e, idleSecond, repeatTime)) {
				ehcacheValue = CacheValuesUtil.getNewValue(repeatTime);
				ehcacheValue = CacheValuesUtil.addValueElement(ehcacheValue,current_time, key);
			} else {
				ehcacheValue = (String) e.getObjectValue();
				if (ehcacheValue != null) {

					int amount = CacheValuesUtil.getSendTimes(ehcacheValue, key);
					long frist_time = CacheValuesUtil.getFirstElement(ehcacheValue, key);

					long time_difference = (current_time - frist_time) / 1000;
					if (log.isDebugEnabled()) {
						log.debug("[repeatFilter] ----------  距离最早下发记录的时间差："+ time_difference + " 秒");
					}

					if (time_difference < idleSecond && amount < repeatTime) {
						if (log.isDebugEnabled()) {
							log.debug("[repeatFilter]未达到过滤重复次数，成功下发并在队列中添加当前时间");
						}
						ehcacheValue = CacheValuesUtil.addValueElement(ehcacheValue, current_time, key);
					} else if (time_difference < idleSecond&& amount >= repeatTime) {
						if (log.isDebugEnabled()) {
							log.debug("[repeatFilter]重复下发驳回");
						}
						result = amount + "_" + ehcacheValue;
					} else if (time_difference >= idleSecond) {
						if (log.isDebugEnabled()) {
							log.debug("[repeatFilter]首条下发成功记录超时，移除");
						}
						ehcacheValue = CacheValuesUtil.removeHeadElement(ehcacheValue, key);
						ehcacheValue = CacheValuesUtil.addValueElement(ehcacheValue, current_time, key);
					}
				}
			}
			e = new Element(key, ehcacheValue, idleSecond, 0);
			cache.put(e);
		} catch (Exception e) {
			log.warn("["+key+"]",e);
		}

		return result;
	}

	private static boolean isNeedReset(Element e, int interval_time,int repeat_time) {
		boolean result = false;

		if (e == null) {
			result = true;
		} else {
			String value = (String) e.getObjectValue();

			boolean isIntervalChanged = e.getTimeToIdle() != interval_time;
			boolean isRepeatNumChanged = (CacheValuesUtil.getRepeatTime(value)) != repeat_time;

			result = isIntervalChanged || isRepeatNumChanged;
		}

		return result;
	}
	
	public static void main(String[] args) {
		EhcacheRepeatMsgFilter.doStart();
	}
}
