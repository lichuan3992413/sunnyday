package sunnyday.controller.thread;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.UserBean;
import sunnyday.controller.cache.UserInfoCache;
import sunnyday.controller.util.DateUtil;
import sunnyday.controller.util.StatisticsUtil;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;

/**
 * 短信由于各种原因被拒绝驳回之后，将下发短信移至数据中心submitHistoryQueue队列的同时根据短信的状态报告处理类型进行不同处理,<br>
 * 不需要状态报告时，短信状态报告入数据中心sentReportToDBQueue队列<br>
 * 需要状态报告，短信状态报告入数据中心sentReportQueue队列<br>
 */
@Service
public class RejectMessageDealThread extends Thread {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	

	private boolean isRunnable = false;
	
	public RejectMessageDealThread(){
		this.isRunnable = true;
	}
	
	public void run(){
		while(isRunnable){
			try {
				SmsMessage rejectSms = DataCenter_old.getRejectMessage();
				if(rejectSms != null){
					//rejectSms.setRpt_seq(ReportUtil.getUniqueSeq());
					rejectSms.setMsg_scan_time(DateUtil.currentTimeToMs());
					rejectSms.setMsg_send_time(DateUtil.currentTimeToMs());
					rejectSms.setMsg_report_time(DateUtil.currentTimeToMs());
					
					List<ReportBean> reports = rejectSms.toReportForm();
					int send_type = getUserReportType(rejectSms.getUser_id());
					if (reports != null) {
						for (ReportBean each : reports) {
							    each.setSend_type(send_type);
							if (send_type == ParamUtil.NO_NEED) {
								each.setSend_status(-1);
								each.setStatus(-1);
							}
						}

						if (send_type == ParamUtil.NO_NEED) {
							DataCenter_old.addSentReportToDBQueue(reports);
						} else {
							DataCenter_old.addSendReportQueue(reports);
						}
					}
					
					DataCenter_old.addSubmitHistoryQueue(rejectSms);
					StatisticsUtil.Statistics4Memory(rejectSms);
				}else{
					try {
						Thread.sleep(500);
					} catch (Exception e) {
						log.error("",e);
					}
				}
			} catch (Exception e) {
				log.error("",e);
			}
			
		
		}
	}
	public boolean doStop() {
		this.isRunnable = false;
		return true;
	}
	
	private  int getUserReportType(String user_id){
		int result = 0;
		UserBean user = UserInfoCache.getUser_info().get(user_id);
		if(user != null){
			result = user.getReport_type();
		}
		return result;
	}
}
