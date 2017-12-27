package sunnyday.controller.util;

import java.util.List;

import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.UserBean;
import sunnyday.controller.cache.UserInfoCache;
import sunnyday.controller.thread.DataCenter_old;
import sunnyday.tools.util.ParamUtil;

public class DeliverUtil {
	
	/**
	 * 0 NO_NEED
	 * 1 用户自取上行或者状态报告
	 * 2 平台推送上行或状态报告
	 * @param user_id
	 * @return
	 */
	public static int getUserDeliverType(String user_id){
		int result = 0;
		UserBean user = UserInfoCache.getUser_info().get(user_id);
		if(user != null){
			result = user.getDeliver_type();
		}
		return result;
	}
	
	/**
	 * 0 NO_NEED
	 * 1 用户自取上行或者状态报告
	 * 2 平台推送上行或状态报告
	 * @param user_id
	 * @return
	 */
	public static int getUserReportType(String user_id){
		int result = 0;
		UserBean user = UserInfoCache.getUser_info().get(user_id);
		if(user != null){
			result = user.getReport_type();
		}
		return result;
	}
	
	public static void matchReportCommonOperation(SmsMessage sms, ReportBean report) {
		//Long seq = ReportUtil.getUniqueSeq();
		sms.updatePropertiesByReport(report);
		//sms.setRpt_seq(seq);
		if (sms.getPktotal() > 1) {
			report.setSub_seq(sms.getPknumber());
		}
		if(sms.getRpt_seq()==0){
			sms.setRpt_seq(ReportUtil.getUniqueSeq());
		}
		report.setRpt_match_time(DateUtil.currentTimeToMs());
		report.setSubmit_time(sms.getMsg_send_time());
		report.setRpt_seq(sms.getRpt_seq());
		report.setPk_toal(sms.getPktotal());
		report.setPk_index(sms.getPknumber());
		int send_type = getUserReportType(sms.getUser_id());
		List<ReportBean> sendReport = sms.toReportForm(report);
		if (sendReport != null) {
			for (ReportBean each : sendReport) {
				each.setSend_type(send_type);

				if (send_type == ParamUtil.NO_NEED) {
					each.setSend_status(-1);
					each.setStatus(-1);
				}
			}

			if (send_type == ParamUtil.NO_NEED) {
				DataCenter_old.addSentReportToDBQueue(sendReport);
			} else {
				DataCenter_old.addSendReportQueue(sendReport);
			}

		}
		 

		/**
		 * 状态报告完成，把信息发送到历史表
		 */
		DataCenterIOUtil.addSubmitHistoryQueue(sms);
	}
}
