package sunnyday.controller.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.Spring;

@Service
public class CacheCenter {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	private boolean isReady = false;
	private boolean isRunable = false;

	private Properties prop = new Properties();
	private Map<String, Long> reloadMap = new TreeMap<String, Long>();

	public void doStart() {
		InputStream inStream = null;
		try {
			log.info(System.getProperty("user.dir"));
			String filePath = System.getProperty("user.dir")+ "/config/cacheFresh.properties";
			inStream = new FileInputStream(new File(filePath));
			prop.load(inStream);
			// prop.load(DataCache.class.getResourceAsStream("cacheFresh.property"));
			log.info("缓存加载信息：" + prop);
			isRunable = true;
			reloadData();
			while (!isReady) {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					log.error("", e);
				}
			}
			log.info("缓存服务启动并完成初始化");
		} catch (IOException e) {
			log.error("", e);
		}finally{
			if(inStream!=null){
				try {
					inStream.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
			
		}
	}

	public  void doStop() {
		isRunable = false;
	}

	public void reloadData() {
		new DataReloadThread().start();
	}

	private boolean isNeedReload(String keyStr) {
		boolean result = false;
		long timeInterval = System.currentTimeMillis() - reloadMap.get(keyStr);
		Integer reloadInterval = Integer.parseInt((String) prop.get(keyStr)) * 1000;
		result = timeInterval >= reloadInterval ? true : false;
		return result;
	}
	
	class DataReloadThread extends Thread {

		public void run() {
			try {
				while (isRunable) {
					boolean isAllReady = true;
					
					for (Object key : prop.keySet()) {
						String keyStr = (String) key;
						if (!reloadMap.containsKey(keyStr)|| isNeedReload(keyStr)) {
							Cache c = (Cache) Spring.getApx().getBean(keyStr);
							
							boolean eachReady = c.reloadCache();
							isAllReady = eachReady && isAllReady;
							if(eachReady){
								reloadMap.put(keyStr, System.currentTimeMillis());
							}
						}
					}
					isReady = isAllReady;
					Thread.sleep(1000);
				}
				// 加入手机号归属地
				
				
			} catch (Exception e) {
				log.error("", e);
				isReady = false;
			}
		}
	}
}
