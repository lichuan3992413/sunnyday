package sunnyday.controller.thread;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import sunnyday.common.model.ErrCode;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.UserBean;
import sunnyday.controller.cache.ErrCodeCache;
import sunnyday.controller.cache.UserInfoCache;
import sunnyday.controller.util.ConcatUtil;
import sunnyday.controller.util.DateUtil;
import sunnyday.controller.util.DealRAO;
import sunnyday.controller.util.StatisticsUtil;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;

/**
 * 处理在发送端线程下发网关失败的短信队列q:submitFin, 生成错误的状态报告，然后根据短信状态报告推送类型向sendReportQueue队列中放入或者sentReportToDBQueue，
 * 同时，将短信移入submitHisotryQueue队列中<br>
 * 可以多个节点同时启动
 * 处理下发记录,分配到不同队列分组中
 * 
 * @author 1307365
 * 
 */
@Service
public class SentMessageConsumeThread extends Thread {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	@Value("#{config.submit_fin_queue}")
	private String queueName;
	
	@Value("#{config.submit_fin_queue_limit}")
	private int submit_fin_queue_limit;
	
	@Value("#{config.submit_sent}")
	private String submit_sent;
	
	@Autowired
	private DealRAO redisDeal = null;
	private boolean isRunnable = true;
	
	@Override
	public void run() {
		log.info("---------------> 处理下发记录线程启动 <---------------");
		while (isRunnable) {
			long time1  = System.currentTimeMillis() ;
			try {
				List<Object> sentMessageList = redisDeal.getMessageFromDataCenter(queueName, submit_fin_queue_limit);
				//发送完成
				if(sentMessageList!=null&&sentMessageList.size()>0){
					for(Object eachObj : sentMessageList){
						SmsMessage sms = (SmsMessage)eachObj;
						 
						if (sms.getResponse() == 1000) {
							log.error("此处不应该出现提交运营商成功的数据！"+sms);
						}else{
							String fail_desc = sms.getFail_desc();
							if(sms.getResponse() == ErrCodeCache.getErrCode(ErrCode.codeName.submitNoResponse).getResponse()){
								sms.setFail_desc(ConcatUtil.concat(fail_desc, ErrCodeCache.getErrCode(ErrCode.codeName.submitNoResponse).getFail_desc()));
								sms.setErr(String.valueOf(ErrCodeCache.getErrCode(ErrCode.codeName.submitNoResponse).getErr()));
							}else{
								sms.setFail_desc(ConcatUtil.concat(fail_desc, ErrCodeCache.getErrCode(ErrCode.codeName.submitErr).getFail_desc()));
								sms.setErr(String.valueOf(sms.getResponse()));
							}
							
							//sms.setRpt_seq(ReportUtil.getUniqueSeq());
							sms.setMsg_report_time(DateUtil.currentTimeToMs());
							
							//生成失败状态报告
							List<ReportBean> reports = sms.toReportForm();
							int send_type = getUserReportType(sms.getUser_id());
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
							 
							
							//放入发送历史队列
							DataCenter_old.addSubmitHistoryQueue(sms);
							StatisticsUtil.Statistics4Memory(sms);
						}
					}
					try {
						sleep(20);
					} catch (Exception e) {
					}
					
				}else {
					sleep(100);
				}

			} catch (Exception e) {
				if(e.getMessage()!=null&&!e.getMessage().contains("sleep interrupted")){
					log.error("SentMessageConsumeThread Exception", e);
				}
				
			}finally{
				long time2  = System.currentTimeMillis() ;
				if(time2-time1>3000){
					log.warn("SentMessageConsumeThread  ,处理耗时:["+(time2-time1)+"]ms");
				}
				
				
			}
		}
	}

	private  int getUserReportType(String user_id){
		int result = 0;
		UserBean user = UserInfoCache.getUser_info().get(user_id);
		if(user != null){
			result = user.getReport_type();
		}
		return result;
	}
	public boolean doStop() {
		this.isRunnable = false;
		log.info("--------< 处理下发记录线程关闭   >--------");
		interrupt();
		return false;
	}

}
