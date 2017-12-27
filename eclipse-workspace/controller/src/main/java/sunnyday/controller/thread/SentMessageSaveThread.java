package sunnyday.controller.thread;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hskj.utils.EncodeResponse;
import com.hskj.utils.HSToolCode;
import com.hskj.utils.UtilTool;

import sunnyday.common.model.SmsMessage;
import sunnyday.controller.DAO.IMessageDAO;

/**
 * 下发短信超时未匹配时，将短信保存在数据库submit_message_send_catch表中，以便进行后续的表匹配
 */
@Service
public class SentMessageSaveThread extends ObjectWriteThread{
	@Resource(name="${db.type}_MessageDAO")
	private IMessageDAO dao;
	@Override
	protected Object getOneMessage() {
		return DataCenter_old.getSentMessageToDB();
	}

	@Override
	protected String getGroupKey(Object smsObj) {
		return "catchMessage";
	}

	@Override
	protected void writeList(List<Object> list) {
		if(UtilTool.isEncode()){
			for(Object objet:list){
				SmsMessage sms = (SmsMessage) objet;
				if(sms.getIs_encode()==0){
					EncodeResponse rep = HSToolCode.encoded(sms.getComplete_content());
					if(rep.isSuccess()){
						sms.setIs_encode(1);
						sms.setComplete_content(rep.getContent());
						sms.setMsg_content(HSToolCode.encoded(sms.getMsg_content()).getContent());	
					}
					
				}
			}
		}
		dao.insertIntoSubmitMessageCatch(list);
	}

}
