package sunnyday.controller.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import sunnyday.common.model.CheckMsgBatch;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.DAO.IMessageDAO;
import sunnyday.tools.util.DateUtil;

/**
 * 数据中心manualCheckMessageQueue队列中需要人工审核的短信数据保存到数据库中
 */
@Service
public class ManualCheckToDBThread extends ObjectWriteThread{
	@Resource(name="${db.type}_MessageDAO")
	private IMessageDAO msgDAO;
	private int squence_number = 0;
	public void initConfigParam(){
		this.setTimeInterval(1000);
	}
	@Override
	protected Object getOneMessage() {
		return DataCenter_old.getManualCheckMessage();
	}

	@Override
	protected String getGroupKey(Object smsObj) {
		return "manual_check";
	}

	@Override
	protected void writeList(List<Object> list) {
		Map<String, CheckMsgBatch> batch_message_map = new HashMap<String, CheckMsgBatch>();
		for(Object o : list){
			SmsMessage sms = (SmsMessage)o;
			String batch_number = (String)sms.getExtraField("batch_number");
			if(batch_number==null){
				String date = DateUtil.currentDate();
				int number = generalSquenceNumber();
				batch_number = date+number;
				sms.addExtraField("batch_number", batch_number);
			}
			if(!batch_message_map.containsKey(batch_number)){
				CheckMsgBatch cmb = new CheckMsgBatch();
				cmb.setBatch_content(sms.getComplete_content());
				cmb.setBatch_count(1);
				cmb.setBatch_number(batch_number);
				cmb.setCheck_status(0);
				cmb.setCheck_user(sms.getCheck_user());
				cmb.setSend_type(2);
				cmb.setTiming_time((String)sms.getExtraField("timing_time"));
				String remark = (String)sms.getExtraField("batch_purpose");
				if(remark==null){
					remark = "";
				}
				cmb.setRemark(remark);
				cmb.setUser_id(sms.getUser_id());
				cmb.setMd5_index("");
				batch_message_map.put(batch_number, cmb);
			}else{
				CheckMsgBatch bcmf = batch_message_map.get(batch_number);
				int batch_count = bcmf.getBatch_count();
				bcmf.setBatch_count(batch_count+1);
			}
			
		}
		
		
		
		msgDAO.insertIntoCheckMessage(list);
		msgDAO.insertCheckMessageBatch(batch_message_map);
	}
	private int generalSquenceNumber() {
		if(squence_number==Integer.MAX_VALUE){
			squence_number = 0;
		}
		return squence_number++;
	}
}
