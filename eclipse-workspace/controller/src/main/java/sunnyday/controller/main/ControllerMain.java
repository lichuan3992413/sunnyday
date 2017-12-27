package sunnyday.controller.main;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import sunnyday.controller.cache.CacheCenter;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.PropertiesUtil;
import sunnyday.tools.util.Spring;

public class ControllerMain {

	private static Logger log;
	
	private static Map<String, Thread> allThreads = new ConcurrentHashMap <String, Thread>();
	 
	public static Map<String, Thread> getAllThreads() {
		return allThreads;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ControllerMain mainDealData = new ControllerMain();
		try {
			if(args != null && args.length > 0){
				
//				PathCache.configDir = args[0];
				System.setProperty("my.dir", args[0]);
				
//				if(args.length > 1){
//					PathCache.shutdDownCacheDir = args[1];
//				}
			}else{
//				PathCache.configDir = System.getProperty("user.dir");
				System.setProperty("my.dir", System.getProperty("user.dir"));
//				PathCache.shutdDownCacheDir = System.getProperty("user.dir");
			}
			
			log = CommonLogFactory.getCommonLog("monitor");
		
			mainDealData.start();

		} catch (Exception e) {
			log.error("", e);
		}
		
	}

	public void start() {
		log.info("------< 开始启动程序  >------");
		Spring.initFileSystemSpring(System.getProperty("user.dir") + "/config/bean-config.xml");
		Spring.getApx().getBean(CacheCenter.class).doStart();
		//初始化esb调用环境
//		Spring.getApx().getBean(ESBFactory.class).initEsbClientContext();
		
		Properties props =  new Properties();
		Properties dealGroup = (Properties) Spring.getApx().getBean("dealGroup");
		Properties dealSingle = (Properties) Spring.getApx().getBean("dealSingle");
	 
		if(dealGroup!=null&&!dealGroup.isEmpty()){
			for(Object key : dealGroup.keySet()){
				String tmp =String.valueOf(key);
				if(tmp.startsWith("threads")){
					props.put(key, dealGroup.get(key));	
				}
				
			}
		}
		
		Properties masterSlave = PropertiesUtil.loadMasterSlaveProperties();
		
		
        int count=1;
		for (Object key : props.keySet()) {
			String strKey = (String) key;
			String class_name = strKey.substring(8);
			Thread t = (Thread) Spring.getApx().getBean(class_name);
			if (t != null) {
				t.setName(class_name);
				t.start();
				allThreads.put(class_name, t);
				log.info(count+". "+class_name + " is started.");
				count++;
			} else {
				log.error(class_name + " is null.");
			}

		}
		 
	 
		log.info("------> 程序启动成功  <------");
	}

}
