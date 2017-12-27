package sunnyday.channel.thread;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.channel.cache.DataCenter;
import sunnyday.common.model.SmsMessage;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;


@Service
public class MessageScanTask extends Thread{

	@Autowired
	private SendRAO dao = null;
	private ConcurrentHashMap<String, ArrayBlockingQueue<SmsMessage>> scanMap;
	private ExecutorService scanMessagePool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors()+2, 60,TimeUnit.SECONDS, DataCenter.getScanMessageWorkQueue());
	private Map<String, ScanMessageTask> map = new ConcurrentHashMap<String, ScanMessageTask>();

	@Value("#{config.msgTime}")
	private String msgTime;

	@Value("#{config.msgCount}")
	private String msgCount;

	/**
	 * 通道待发短信
	 */
	@Value("#{config.sendQueueName}")
	private String queueName;

	/**
	 * 单次从通道中获取下发数据的数量
	 */
	@Value("#{config.getQueueCount}")
	private String getQueueCount;

	private ArrayBlockingQueue<SmsMessage> destQueue = null;
	private  Logger log = CommonLogFactory.getLog(MessageScanTask.class);
	
	private boolean running = false;
	
	public MessageScanTask(){
		running = true;
	}

	
	@Override
	public void run() {
		 
		while(running){
			try {
				scanMap = DataCenter.getScanMap();
				Set<String> keys = dao.HSKeys(ParamUtil.REDIS_SET_SEND_KEY);
 				if(keys!=null&&keys.size()>0){
 					if (scanMap != null) {
 						Map<String,HashSet<String>> td_user_id = new ConcurrentHashMap<String, HashSet<String>>();
 						for(String key:keys){
 							String yw_code_user_id = key.replace(queueName, "");
 							if(yw_code_user_id.contains(":")){
 								String[] params = yw_code_user_id.split(":");
 		 						
 	 							if(!td_user_id.containsKey(params[0])){
 	 								HashSet<String> userSet = new HashSet<String>();
 	 								td_user_id.put(params[0], userSet);
 	 							
 	 							}
 	 							
 	 							td_user_id.get(params[0]).add(params[1]);
 							}else{
 								if(!td_user_id.containsKey(yw_code_user_id)){
 	 								HashSet<String> userSet = new HashSet<String>();
 	 								td_user_id.put(yw_code_user_id, userSet);
 	 							}
 								td_user_id.get(yw_code_user_id).add("");
 							}
 						
 						}
 							
 							for(String yw_code:td_user_id.keySet()){
 					
 								HashSet<String> userSet = td_user_id.get(yw_code);
 								
 								if(userSet!=null&&userSet.size()>0){
 									
 									destQueue = scanMap.get(yw_code);
 		 							int minCapacity = Integer.parseInt(getQueueCount.trim());
 		 							if(destQueue == null){
 		 								continue;
 		 							}
 		 							int capacity_size = destQueue.remainingCapacity();
 		 							if (destQueue != null&&capacity_size>=minCapacity) {
 		 								if (DataCenter.getScanMessageWorkQueueRemainingCapacity()<= 10) {
 											log.warn("ScanMessageWorkQueue["+DataCenter.getScanMessageWorkQueueSize()+"]   Capacity["+DataCenter.getScanMessageWorkQueueRemainingCapacity()+"] is <= 10 .");
 											sleep(50);
 											continue;
 										}
 									
 									
 									
 									int userCount = userSet.size();
 									int per = minCapacity/userCount;
 									for(String user_id:userSet){
 										String key = "";
 										if(user_id.equals("")){
 											key = yw_code;
 										}else{
 											key = yw_code+":"+user_id;
 										}
 	 									
 										if(!map.containsKey(key)){
 		 	 								// 不包含
 		 									ScanMessageTask task= new ScanMessageTask(destQueue,dao,key,queueName,msgTime,msgCount,getQueueCount,per);
 		 									if (!scanMessagePool.isShutdown()) {
 		 										scanMessagePool.submit(task);
 		 										map.put(key, task);
 		 									}
 		 	 							}else {
 											//已经有在执行的
 		 	 								ScanMessageTask task= map.get(key);
 		 	 								if(task.isFinshed()){
 		 	 									if (!scanMessagePool.isShutdown()) {
 		 	 										task = new ScanMessageTask(destQueue,dao,key,queueName,msgTime,msgCount,getQueueCount,per);
 		 	 										scanMessagePool.submit(task);
 		 	 										map.put(key, task);
 		 	 									}
 		 	 								}else {
 		 	 									sleep(10);
 		 	 									if(log.isDebugEnabled()){
 		 	 										log.debug("[scan_Message_UnFinshed]yw_code: "+key+" 下发扫描任务尚未结束，跳过本次扫描！");
 		 	 									}
 											}
 										}
 										
 										
 	 								}
 		 							}else {
 		 								if(destQueue!=null){
 		 									log.warn("yw_code: "+yw_code+"; QueueSize: "+destQueue.size()+"; 队列剩余空间["+capacity_size+"]不足["+minCapacity+"], 跳过本轮扫描！");
 		 								}
 		 								sleep(100);
 									}	
 								}
 						}
 					}else {
 						sleep(1000);
					}
 					try {
 						sleep(5);//减少和redis的交互
					} catch (Exception e) {
						
					}
 				}else {
 					sleep(1000);
				}
			} catch (Exception e) {
				if(log.isErrorEnabled()){
					log.error(""+scanMap,e);
				}
			}
			
		}
	}
	 
	public boolean doStop() {
		running = false;
		//DataCenter.setScanMap(scanMap);// 回写队列中的数据
		//log.info("！！！！扫描队列关闭，回写扫描数据！！！！");
		return true;
	}

}
