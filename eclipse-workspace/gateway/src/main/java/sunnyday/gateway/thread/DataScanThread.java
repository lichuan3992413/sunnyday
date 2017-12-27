package sunnyday.gateway.thread;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.gateway.dc.DataCenter;
import sunnyday.gateway.util.CommonRAO;
import sunnyday.gateway.util.ParamUtil;
import sunnyday.tools.util.CommonLogFactory;


@Service
public class DataScanThread extends Thread implements Stoppable{
	private  Logger log = CommonLogFactory.getLog("monitor");
	@Autowired
	private CommonRAO dao = null;
	private boolean running = false;
	private String queueReportName = "q:report:";
	private String queueDeliverName = "q:deliver:";
	@Value("#{config.submitQueueName}")
	private String queue_name = "submitDoneList";
	
	@Override
	public void run() {
		log.info("DataScanThread is start");
		running = true;
		while(running){
			try {
				boolean flag = false ;
				Set<String> keys_report = dao.HSKeys(ParamUtil.REDIS_SET_REPORT_KEY);
				Set<String> keys_deliver = dao.HSKeys(ParamUtil.REDIS_SET_DELIVER_KEY);
 				
				if(keys_report!=null&&keys_report.size()>0){
 					DataCenter.init_report_user_set(keys_report,queueReportName);
 					flag =  true ;
 				}
 				
 				if(keys_deliver!=null&&keys_deliver.size()>0){
 					DataCenter.init_deliver_user_set(keys_deliver,queueDeliverName);
 					flag =  true ;
 				}
 				
 				Map<String,Long> submit_user_map_tmp = new ConcurrentHashMap<String, Long>();
 				Set<String> submit_user_set  = dao.HSKeys(ParamUtil.REDIS_SET_SUBMIT_KEY);
				 if(submit_user_set!=null&&submit_user_set.size()>0){
					 for(String key : submit_user_set){
						 long size = dao.HSLen(key);
						 submit_user_map_tmp.put(key, size);
					 }
					 DataCenter.setSubmit_user_map(submit_user_map_tmp);
				 }else{
					 DataCenter.setSubmit_user_map( submit_user_map_tmp);
				 }
 				
 				try {
 					if(!flag){
 	 					sleep(1000);
 					}else{
 						sleep(500);
 					}
				} catch (Exception e) {
					log.error("DataScanThread ",e);
				}
 				
			} catch (Exception e) {
				if(log.isErrorEnabled()){
					log.error("DataScanThread ",e);
				}
				DataCenter.setSubmit_user_map( new ConcurrentHashMap<String, Long>());
			}
			
		}
		
		log.info("DataScanThread is closed");
	}
	 
	public boolean doStop() {
		running = false;
		return true;
	}

}
