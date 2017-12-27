package sunnyday.controller.task;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import sunnyday.common.model.ReportBean;
import sunnyday.controller.DAO.IReportDAO;
import sunnyday.tools.util.CommonLogFactory;

public class SentReportSaveTask implements Callable<Integer>{
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	private IReportDAO dao;
	private List<ReportBean> reports;
	
	public SentReportSaveTask(IReportDAO dao,List<ReportBean> reports){
		this.dao = dao ;
		this.reports = reports;
	}

	public Integer call() throws Exception {
		try {
			if (reports != null && reports.size() > 0) {
				long time1 =System.currentTimeMillis();
				boolean flag = dao.insertIntoReportMessageNew(reports, "report_message");
				long time =System.currentTimeMillis()-time1  ;
				if(!flag){
					log.error("insertIntoReportMessageNew fail ["+reports.size()+"] "+reports);
				}
				if(time>3000){
					log.warn("insertIntoReportMessageNew ["+reports.size()+"]too long time ["+time+"]ms ");
				}
			}
		} catch (Exception e) {
			log.error("",e);
		}
		return null;
	}

}
