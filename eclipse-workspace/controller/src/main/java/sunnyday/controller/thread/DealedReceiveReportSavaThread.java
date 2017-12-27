package sunnyday.controller.thread;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunnyday.common.model.ReportBean;
import sunnyday.controller.DAO.IReportDAO;
import sunnyday.controller.task.ReceiveReportSaveTask;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 将数据中心dealedReportToCacheQueue队列中状态报告存入数据库receive_report_info_cache表中
 */
@Service
public class DealedReceiveReportSavaThread extends Thread{
	private Logger info_log = CommonLogFactory.getCommonLog("infoLog");
	private boolean isRunnable = true;
	@Resource(name="${db.type}_ReportDAO")
	protected IReportDAO dao;
	private int  COUNT = 1000;
	@Override
	public void run() {
		while (isRunnable) {
			boolean flag = true;
			while (flag) {
				try {
					flag = false;
					int size = DataCenter_old.getDataToDbWorkQueue().remainingCapacity();
					if (size <= 20) {
						Thread.sleep(1000);
						info_log.warn("dataToDbWorkQueue[" + size + "]   Capacity is <= 20 .");
						flag = true;
						continue;
					}
					
					List<ReportBean> list =  getSentRepors() ;
					if(list!=null&&list.size()>0){
						if (!DataCenter_old.getDataToDbPool().isShutdown()) {
							DataCenter_old.getDataToDbPool().submit(new ReceiveReportSaveTask(dao,list));
						}
					}else{
						sleep(100);
					}
					
				} catch (Exception e) {
					info_log.warn("DealedReceiveReportSavaThread"+ e);
				}
			
			}
			
		}
	}
	protected List<ReportBean> getSentRepors() {
		return DataCenter_old.getDealedReportToCacheQueue(COUNT);
	}
	
	public boolean doStop() {
		isRunnable = false ;
		info_log.info("--------< 网关返回状态存储线程关闭   >--------");
		interrupt();
		return true;
	}
	/*@Autowired
	private ReportDAO reportDAO;
	@Override
	protected Object getOneMessage() {
		return DataCenter_old.getDealedReportToCacheQueue().poll();
	}

	@Override
	protected String getGroupKey(Object smsObj) {
		ReportBean report = (ReportBean)smsObj;
		return String.valueOf(report.getStatus());
	}

	@Override
	protected void writeList(List<Object> list) {
		reportDAO.insertIntoReceiveReportCache(list);
	}*/

}
