package sunnyday.controller.filter;

import org.slf4j.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 由EhcacheRepeatMsgFilter3 改为EhcacheRepeatMsgFilter4
 * 去掉接口实现，方便其他工具类直接调用
 * @author 1202211
 *
 */
public class EhcacheRepeatMsgFilter4 {
	private  static Logger log = CommonLogFactory.getCommonLog(EhcacheRepeatMsgFilter4.class);
	private static CacheManager manager = null;
	private static String cacheName = null;

	public static void doStart(String filterName) {
		log.info("-------------- cache starting ----------------");
		if(filterName != null){
			cacheName = filterName; 
			String path=System.getProperty("my.dir") + "/config/ehcache.xml";
			manager = CacheManager.create(path);
			//manager = CacheManager.create(EhcacheFactory.class.getClassLoader().getResourceAsStream("ehcache.xml"));
			Cache cache = EhcacheFactory.newCache(cacheName);
			manager.addCache(cache);
			for(String cacheName : manager.getCacheNames()){
				log.info("cache all right  ---- " + cacheName + " ----- size = " + manager.getCache(cacheName).getSize());
			}
		}else{
			throw new RuntimeException("filter name is null");
		}
	}

	public static void doStop() {
		log.info("[ program shutdown ] repeat cache write back start ...");
		manager.shutdown();
	}

	public static Object doFilter(String key, int liveSecond, int repeatTime) {
		String result = null;
		Cache cache = manager.getCache(cacheName);
		try{
			Element e = cache.get(key);
			if(e == null){
				e = new Element(key, "", 0, liveSecond);
				cache.put(e);
			}else{
				long totalCount = e.getHitCount() + 1;
				if(totalCount >= repeatTime){
					log.debug("[repeatFilter]重复下发驳回" + key);
					result = new String(totalCount + "");
				}
			}
		}catch(Exception e){
			log.error("",e);
		}
		
		return result;
	}
}
