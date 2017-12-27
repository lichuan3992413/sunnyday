package sunnyday.controller.thread;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import sunnyday.controller.DAO.IMessageDAO;

/**
 * 数据中心avoidDisturbMessageQueue队列中打扰短信存到数据库submit_message_distrub表中
 */
@Service
public class AvoidDisturbMessageToDBThread extends ObjectWriteThread {

	private String tableName = "submit_message_disturb";

	@Resource(name = "${db.type}_MessageDAO")
	private IMessageDAO msgDAO;

	public void initConfigParam() {
		this.setTimeInterval(1000);
	}

	@Override
	protected Object getOneMessage() {
		return DataCenter_old.getAvoidDisturbMessage();
	}

	@Override
	protected String getGroupKey(Object smsObj) {
		return "disturbMessage";
	}

	@Override
	protected void writeList(List<Object> list) {
		msgDAO.insertIntoSubmitMessageDisturb(list, tableName);
	}

}
