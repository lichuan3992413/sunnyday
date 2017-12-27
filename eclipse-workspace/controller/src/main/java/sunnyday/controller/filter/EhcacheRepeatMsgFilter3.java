package sunnyday.controller.filter;

import org.slf4j.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import sunnyday.tools.util.CommonLogFactory;
/**
 * 简陋版的重复过滤器,只对从第一次过滤开始后的一个时间段做计数,到期之后释放,重新开始计数
 * @author zhouhongna
 *
 */
public class EhcacheRepeatMsgFilter3 implements IFilter {
	private  Logger log = CommonLogFactory.getCommonLog(EhcacheRepeatMsgFilter3.class);
	private CacheManager manager = null;
	private String cacheName = null;
	
	public void doStart(String filterName) {
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

	
	public void doStop() {
		log.info("[ program shutdown ] repeat cache write back start ...");
		manager.shutdown();
	}

	
	public Object doFilter(String key, int liveSecond, int repeatTime) {
		String result = null;
		Cache cache = manager.getCache(cacheName);
		try{
			Element e = cache.get(key);
			if(e == null){
				e = new Element(key, "", 0, liveSecond);
				cache.put(e);
			}else{
				long totalCount = e.getHitCount() + 1;
				if(totalCount >  repeatTime){
					log.info("[Ehcache-repeatFilter]重复下发驳回" + key);
					result = new String(totalCount + "");
				}
			}
		}catch(Exception e){
			log.error("",e);
		}
		
		return result;
	}

	
	public Object doFilter(int pk_toal,String key_content, String key_word, int idleSecond,
			int repeatTime) {
		// TODO Auto-generated method stub
		return null;
	}
}
