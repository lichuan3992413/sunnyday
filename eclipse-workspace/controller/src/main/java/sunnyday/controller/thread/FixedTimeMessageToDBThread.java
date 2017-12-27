package sunnyday.controller.thread;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import sunnyday.controller.DAO.IMessageDAO;

/**
 * 数据中心fixedTimeMessageQueue队列中定时短信存到数据库send_message_timing表中
 */
@Service
public class FixedTimeMessageToDBThread extends ObjectWriteThread {

	private String tableName = "send_message_timing";

	@Resource(name = "${db.type}_MessageDAO")
	private IMessageDAO msgDAO;

	public void initConfigParam() {
		this.setTimeInterval(1000);
	}

	
	protected Object getOneMessage() {
//		return DataCenter_old.getFixedTimeMessage();
		return null;
	}

	
	protected String getGroupKey(Object smsObj) {
		return "fixedTimeMessage";
	}

	
	protected void writeList(List<Object> list) {
		msgDAO.insertIntoFixedTimeMessage(list, tableName);
	}

}
