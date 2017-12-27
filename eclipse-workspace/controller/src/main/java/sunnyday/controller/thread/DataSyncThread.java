package sunnyday.controller.thread;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;


import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.Spring;
import sunnyday.tools.util.Task;
import sunnyday.tools.util.ThreadPool;
@Service
public class DataSyncThread extends Thread{
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	private Map<String, Long> reloadMap = new TreeMap<String, Long>();
	private Map<String, Task> taskMap = new TreeMap<String, Task>();
	private ThreadPool threadPool;
	private boolean isRunable;
	
	
	public DataSyncThread(){
		isRunable = true;
		log.info("DataSyncThread try to start ... ");
	}
	
	public void run() {
		try {
			initTaskMap();
			initThreadPool();
			while (isRunable) {
				for (String key : taskMap.keySet()) {
					
					if (!reloadMap.containsKey(key) || isNeedReload(key)) {
						Task task = taskMap.get(key);
						if(!task.isPerforming()){
							try {
								if(task.isFished()){
									threadPool.performTask(taskMap.get(key));
								}else {
									if(log.isDebugEnabled()){
										log.debug(key+" -> "+key +" 尚未完成！！！！");
									}
								}
								
							} catch (Exception e) {
								log.info("key: "+key ,e);
							}
						}
						reloadMap.put(key, System.currentTimeMillis());
					}
				}
				Thread.sleep(5);
			}
		} catch (Exception e) {
			log.error("DataSyncThread", e);
		}
	}
	
	private void initThreadPool() {
		//线程池大小为CPU核心数+1
		threadPool = new ThreadPool("SyncThreadPool", Runtime.getRuntime().availableProcessors()+1);
	}

	private void initTaskMap() throws InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException, IOException {
		Properties prop = new Properties();
		Properties dealGroup = (Properties) Spring.getApx().getBean("dealGroup");
		Properties dealSingle = (Properties) Spring.getApx().getBean("dealSingle");
		
		if(dealGroup!=null&&!dealGroup.isEmpty()){
			for(Object key : dealGroup.keySet()){
				String tmp =String.valueOf(key);
				if(tmp.endsWith("Task")){
					prop.put(key, dealGroup.get(key));
				}
				
			}
		}
		
        if(dealSingle!=null&&!dealSingle.isEmpty()){
            for(Object key : dealSingle.keySet()){
            	String tmp =String.valueOf(key);
            	if(tmp.endsWith("Task")){
            		prop.put(key, dealSingle.get(key));
            	}
            	
			}
		}
        
		for(Object each : prop.keySet()){
			String clazz = (String)each;
			Task task = (Task)Spring.getApx().getBean(clazz);
			try{
				task.setIntervalTime(Integer.parseInt(prop.getProperty(clazz).trim()));
			}catch(Exception e){
				task.setIntervalTime(30*1000);
				log.error("initTaskMap["+clazz+"-"+prop.getProperty(clazz)+"]", e);
			}
			
			taskMap.put(each.toString(), task);
		}
	}
	
	private boolean isNeedReload(String keyStr) {
		boolean result = false;
		try {
			long timeInterval = System.currentTimeMillis() - reloadMap.get(keyStr);
			int reloadInterval = taskMap.get(keyStr).getIntervalTime();
			result = timeInterval >= reloadInterval ? true : false;
		} catch (Exception e) {
			log.error("isNeedReload["+keyStr+"]", e);
		}
		
		return result;
	}

	public boolean doStop() {
		log.info("DataSyncThread try to stop ... ");
		this.isRunable = false;
		threadPool.close();
		return true;
	}
}
