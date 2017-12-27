package sunnyday.gateway.thread;


import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.gateway.dc.DataCenter;
import sunnyday.gateway.threadPool.Task;
import sunnyday.gateway.util.CommonRAO;
import sunnyday.tools.util.CommonLogFactory;


@Service
public class DealDateCenterToRedisTask extends Task{
	private Logger log =  CommonLogFactory.getCommonLog(DealDateCenterToRedisTask.class);
	@Autowired
	private CommonRAO dao = null;
	
	@Value("#{config.reportRespQueueName}")
	private String reportRespQueueName;
	
	@Value("#{config.deliverRespQueueName}")
	private String deliverRespQueueName;
	
	@Value("#{config.getQueueCount}")
	private String getQueueCount;
	

	@Override
	public void reloadCache() {
		try {
			
			List<Object> reportResp = DataCenter.queryReportRespMessage(Integer.parseInt(getQueueCount.trim()));
			if(reportResp!=null&&reportResp.size()>0){
				dao.addSendReportRespList(reportRespQueueName, reportResp);
			}
			List<Object> deliverResp = DataCenter.queryDeliverRespMessage(Integer.parseInt(getQueueCount.trim()));
			if(deliverResp!=null&&deliverResp.size()>0){
				dao.addSendReportRespList(deliverRespQueueName, deliverResp);
			}
			
			/*List<SubmitBean> submit_time_list = DataCenter.getSubmitTimeCache(Integer.parseInt(getQueueCount.trim()));
			if(submit_time_list!=null&&submit_time_list.size()>0){
				dao.addSubmitTimeList(deliverRespQueueName, submit_time_list);
			}*/
		} catch (Exception e) {
			if(log.isErrorEnabled()){
				log.error("", e);
			}
		}
	}

}
