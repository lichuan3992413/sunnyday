package sunnyday.controller.thread;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import sunnyday.controller.DAO.IMessageDAO;

/**
 * 数据中心LongMessageInsertDBQueue队列中长短信分条保存到数据库submit_message_long表中
 */
@Service
public class LongMsgInsertDBThread extends ObjectWriteThread{
	private String tableName = "submit_message_long";
	@Resource(name="${db.type}_MessageDAO")
	private IMessageDAO msgDAO;
	
	public void initConfigParam(){
		this.setTimeInterval(1000);
	}
	
	protected Object getOneMessage() {
		return DataCenter_old.getInsertDBLongMessage();
	}

	
	protected String getGroupKey(Object smsObj) {
		return "longMessage";
	}

	
	protected void writeList(List<Object> list) {
		msgDAO.insertIntoSubmitMessageLong(list, tableName);
	}

}
