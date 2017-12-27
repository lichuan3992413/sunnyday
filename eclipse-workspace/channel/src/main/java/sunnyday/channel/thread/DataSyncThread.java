package sunnyday.channel.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.tools.util.Spring;
import sunnyday.tools.util.Task;
import sunnyday.tools.util.ThreadPool;
@Service
public class DataSyncThread extends Thread{
	private Log log = LogFactory.getLog("infoLog");
	private Map<String, Long> reloadMap = new TreeMap<String, Long>();
	private Map<String, Task> taskMap = new TreeMap<String, Task>();
	private ThreadPool threadPool;
	@Value("#{config.dataSyncConfigPath}")
	private String configPath;
	private boolean isRunable;
	
	
	public DataSyncThread(){
		isRunable = true;
	}
	
	public void run() {
		try {
			initTaskMap(configPath);
			initThreadPool();
			while (isRunable) {
				for (String key : taskMap.keySet()) {
					if (!reloadMap.containsKey(key) || isNeedReload(key)) {
						Task task = taskMap.get(key);
						try {
							if(task!=null&&!task.isPerforming()){
								threadPool.performTask(taskMap.get(key));
							}
							reloadMap.put(key, System.currentTimeMillis());	
						} catch (Exception e) {
							log.error("", e);
						}
						
					}
				}
				Thread.sleep(10);
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	private void initThreadPool() {
		threadPool = new ThreadPool("SyncThreadPool", Runtime.getRuntime().availableProcessors()+2);
	}

	private void initTaskMap(String configPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException, IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream(new File(configPath)));
		
		for(Object each : prop.keySet()){
			String clazz = (String)each;
			Task task = (Task)Spring.getApx().getBean(clazz);
			try {
				String tmp = prop.getProperty(clazz);
				if(tmp!=null){
					task.setIntervalTime(Integer.parseInt(tmp.trim()));
				}
				
			} catch (Exception e) {
				log.error("initTaskMap["+each+"]",e);
				task.setIntervalTime(60*1000);
			}
			
			taskMap.put(each.toString(), task);
		}
	}
	
	private boolean isNeedReload(String keyStr) {
		boolean result = false;
		long timeInterval = System.currentTimeMillis() - reloadMap.get(keyStr);
		int reloadInterval = taskMap.get(keyStr).getIntervalTime();
		result = timeInterval >= reloadInterval ? true : false;
		return result;
	}

	public boolean doStop() {
		if(log.isInfoEnabled()){
			log.info("DataSyncThread try to stop ... ");
		}
		threadPool.close();
		return true;
	}
}
