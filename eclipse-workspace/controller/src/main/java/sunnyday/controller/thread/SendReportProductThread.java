package sunnyday.controller.thread;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.common.model.ReportBean;
import sunnyday.controller.util.GateRAO;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;
@Service
public class SendReportProductThread extends ObjectWriteThread {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	@Value("#{config.send_report_queue_name}")
	private String send_report_queue_name;
	@Autowired
	private GateRAO rao = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	
	@Override
	protected Object getOneMessage() {
		ReportBean report = DataCenter_old.getSendReport();
		return report;
	}
	@Override
	protected String getGroupKey(Object smsObj) {
		ReportBean sms = (ReportBean)smsObj;
		String key = sms.getUser_id();
		return key;
	}
  
	@Override
	protected void writeList(List<Object> list) {
		if(list != null && list.size() > 0){
			ReportBean tmp = (ReportBean)list.get(0);
			String user_id = tmp.getUser_id();
			if(user_id != null){
				String queue = send_report_queue_name.replace("user_id", user_id);
				long length = rao.getQueueLength(queue);
				boolean result = false;
				long count =0;
				while (!result&&count<5) {
					rao.HSSADD(ParamUtil.REDIS_SET_REPORT_KEY, queue);
					result = rao.submitMessageToDc(queue, list);
					count++;
				}
				if (log.isDebugEnabled()) {
				log.debug(queue+"'s length in redis: "+length + ";size: " + list.size() + "; " + list+"; result: "+result+"; count: "+count);
				}
				}else{
				log.error("未知用户的状态报告：" + list);
			}
		}
	}
}
