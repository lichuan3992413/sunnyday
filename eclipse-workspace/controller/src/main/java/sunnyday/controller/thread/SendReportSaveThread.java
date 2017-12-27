package sunnyday.controller.thread;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import sunnyday.common.model.ReportBean;
import sunnyday.controller.DAO.IReportDAO;
@Service
public class SendReportSaveThread extends ObjectWriteThread {
	@Resource(name="${db.type}_ReportDAO")
	protected IReportDAO reportDAO;
//	private long timeInterval = 100; 废弃
	@Override
	protected Object getOneMessage() {
		return null;//DataCenter_old.getSendReportToDB();
	}

	@Override
	protected String getGroupKey(Object smsObj) {
		String key = ((ReportBean)smsObj).getUser_id();
		return key;
	}

	@Override
	protected void writeList(List<Object> list) {
		reportDAO.insertIntoReportMessage(list, "to_send_report");
	}
}
