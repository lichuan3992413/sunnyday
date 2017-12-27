package sunnyday.gateway.dc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.common.model.DeliverBean;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.UserBean;
import sunnyday.gateway.util.CommonRAO;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.DateUtil;
@Service
public class DeliverReportCenter implements Serializable{
	private static final long serialVersionUID = -177114392366700197L;
	private  Logger log = CommonLogFactory.getLog("receiver");
	@Autowired
	private CommonRAO dao = null;
	
	@Value("#{config.reportQueueName}")
	private String reportQueueName;
	
	@Value("#{config.deliverQueueName}")
	private String deliverQueueName;

	@Value("#{config.getQueueCount}")
	private String getQueueCount;
	
	
	public List<ReportBean> getReportList(String user_id,int limit){
		List<ReportBean>  result = new ArrayList<ReportBean>();
			List<Object> reportList;
			try {
				try {
					UserBean user = DataCenter.getUserBeanMap().get(user_id);
					if(user!=null){
						String tmp = (String) user.getParamMap().get("report_limit");
						if(tmp!=null&&!"".equals(tmp)){
							limit = Integer.parseInt(tmp.trim());
							if(limit>1000||limit<0){
								limit = 1000;
							}
						}
					}
				} catch (Exception e) {
				}
			
				if(DataCenter.hit_report_user_set(user_id)){
					long time1 = System.currentTimeMillis();
					reportList = dao.getReportByUserId(reportQueueName + user_id,limit);
					long time2 = System.currentTimeMillis();
					long time= time2-time1 ;
					if(time>1000){
						log.warn("getReportByUserId from redis too long time ["+time+"] ms .");
					} 
					if(reportList!=null&&reportList.size()>0){
						log.debug("["+user_id+"] get report from "+reportQueueName + user_id+", size: "+reportList.size()+", "+reportList+", time: ["+time+"] ms");
					}
					for(Object o :reportList){
						ReportBean  report  =(ReportBean)o ;
						report.setRpt_ready_push_time(DateUtil.currentTimeToMs());
						report.setStatus(1);
						report.setTry_times(report.getTry_times() + 1);
						result.add(report);
					}
				}
			
			}  catch (Exception e) {
				log.error("getReportList:",e);
			}
		return result ;
	}
	
	
	public List<DeliverBean> getDeliverBeanList(String user_id,int limit){
		    List<DeliverBean>  result = new ArrayList<DeliverBean>();
			List<Object> deliverList;
			try {
				UserBean user = DataCenter.getUserBeanMap().get(user_id);
				try {
					if(user!=null){
						String tmp = (String) user.getParamMap().get("deliver_limit");
						if(tmp!=null&&!"".equals(tmp)){
							limit = Integer.parseInt(tmp.trim());
							if(limit>1000||limit<0){
								limit = 1000;
							}
						}
					}
				} catch (Exception e) {
				}
				if(DataCenter.hit_deliver_user_set(user_id)){
					long time1 = System.currentTimeMillis();
					deliverList = dao.getDeliverByUserId(deliverQueueName + user_id,limit);
					long time2 = System.currentTimeMillis();
					long time= time2-time1 ;
					if(time>1000){
						log.warn("getDeliverBeanList from redis too long time ["+time+"] ms .");
					}
					if(deliverList!=null&&deliverList.size()>0){
						log.debug("["+user_id+"] get deliver from "+deliverQueueName + user_id+", size: "+deliverList.size()+", "+deliverList+", time: ["+time+"] ms");
					}
					
					for(Object o :deliverList){
						DeliverBean  deliver  =(DeliverBean)o ;
						deliver.setStatus(1);
						deliver.setTry_times(deliver.getTry_times() + 1);
						result.add(deliver);
					}
				}
			
			}  catch (Exception e) {
				log.error("getReportList:",e);
			}
		return result ;
	}
}
