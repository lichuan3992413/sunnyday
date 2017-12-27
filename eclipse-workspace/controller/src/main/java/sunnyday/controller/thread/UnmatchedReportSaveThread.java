package sunnyday.controller.thread;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import sunnyday.controller.DAO.IReportDAO;

/**
 * 状态报告队列匹失败并且状态报告超时时，将数据中心unmatchedReportToDBQueue队列短信入数据库receive_report_info表
 */
@Service
public class UnmatchedReportSaveThread extends ObjectWriteThread {
	private final String groupKey = "unmatchedReports";  
	@Resource(name="${db.type}_ReportDAO")
	protected IReportDAO reportDAO;
//	private long timeInterval = 100;
	@Override
	protected Object getOneMessage() {
		return DataCenter_old.getUnmatchedReport();
	}

	@Override
	protected String getGroupKey(Object smsObj) {
		String key = groupKey;
		return key;
	}

	@Override
	protected void writeList(List<Object> list) {
		if(log.isDebugEnabled()){
		  log.debug("unmatch "+list);
		}
		reportDAO.insertIntoReceiveReport(list);
	}
}
