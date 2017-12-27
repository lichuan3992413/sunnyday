package sunnyday.gateway.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.gateway.threadPool.Task;
import sunnyday.gateway.threadPool.ThreadPool;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.Spring;
@Service
public class DataSyncThread extends Thread implements Stoppable{
	private Logger log =  CommonLogFactory.getCommonLog(DataSyncThread.class);
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
							log.error("",e);
						}
						
					}
				}
				Thread.sleep(5L);
			}
		} catch (Exception e) {
			log.error("",e);
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
//			Task task = (Task)Class.forName(clazz).newInstance();
			Task task = (Task)Spring.getApx().getBean(clazz);
			task.setIntervalTime(Integer.parseInt(prop.getProperty(clazz)));
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
