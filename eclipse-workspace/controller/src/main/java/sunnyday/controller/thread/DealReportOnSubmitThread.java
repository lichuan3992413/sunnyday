package sunnyday.controller.thread;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.controller.util.DealRAO;
import sunnyday.controller.util.UtilTool;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;

/**
 * 可以多个节点同时启动，建议只在一个处理节点启动
 * 检测状态报告 redis匹配超时的下发信息
 * @author 1307365
 *
 */
@Service
public class DealReportOnSubmitThread extends Thread{
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	@Value("#{config.recv_report_queue_name}")
	private String recv_report_queue_name;
	
	@Value("#{config.recv_report_queue_limit}")
	private int recv_report_queue_limit;
	
	@Value("#{config.submit_sent}")
	private String submit_sent;
	
	 
	
/*	@Value("#{config.deal_submitFn_in_redis_timeOut}")
	private int deal_submitFn_in_redis_timeOut;*/
	@Autowired
	private DealRAO redisReport = null;
	/*private String queueName ="list:";*/
	
	private boolean isRunnable = true;
	public void run() {
		log.info("---------------> 由下发记录侧匹配状态报告线程启动 <---------------");
		/*queueName = queueName +submit_sent+"_*";*/
		while (isRunnable) {
			try { 
				Set<String> keys = redisReport.HSKeys(ParamUtil.REDIS_SET_SENT_KEY);
				if(keys!=null&&keys.size()>1000){
					Set<String> set = new HashSet<String>();
					int count = 0 ;
					for(String key:keys){
						count++;
						set.add(key);
						if(count>=500){
							List<String> list = getTimeOutQueue(set);
							log.debug("处理redis中超时"+key+"的数量为："+list.size());
							redisReport.dealSubmitFinTimeOut(list,submit_sent,0);
							count = 0 ;
							set.clear();
						}
					}
					if(set.size()>0){
						List<String> list = getTimeOutQueue(set);
						log.debug("处理redis中超时"+ParamUtil.REDIS_SET_SENT_KEY+"的数量为："+list.size());
						redisReport.dealSubmitFinTimeOut(list,submit_sent,0);
					}
					
				}else{
					List<String> list = getTimeOutQueue(keys);
					log.debug("处理redis中超时"+ParamUtil.REDIS_SET_SENT_KEY+"的数量为："+list.size());
					redisReport.dealSubmitFinTimeOut(list,submit_sent,0);
				}
				
			} catch (Exception e) {
				log.error("DealReportOnSubmitThread Exception", e);
			}finally{
				try {
					sleep(2000);
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
	}
	
	List<String>  getTimeOutQueue(Set<String> keys){
		int deal_submitFn_in_redis_timeOut=UtilTool.getDealSubmitFnInRedisTimeOut();
		List<String> lsit = new ArrayList<String>();
		if(keys!=null&&keys.size()>0){
			for(String key:keys){
				String[] array = key.split("_");
				if(UtilTool.isTimeOut(array[2], deal_submitFn_in_redis_timeOut)){
					lsit.add(key);
				}
			}
		}
		return lsit;
		
	}
 
 
	public boolean doStop() {
		this.isRunnable = false;
		log.info("--------< 由下发记录侧匹配状态报告线程关闭   >--------");
		interrupt();
		return false;
	}

}
