package sunnyday.controller.thread;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import sunnyday.common.model.ReportBean;
import sunnyday.controller.DAO.IReportDAO;
import sunnyday.controller.task.SentReportSaveTask;
import sunnyday.tools.util.CommonLogFactory;
/**
 * 处理已经发送完毕的状态报告到数据中
 *  
 *
 */
@Service
public class SentReportSaveThread extends Thread{
	private Logger info_log = CommonLogFactory.getCommonLog("infoLog");
	private boolean isRunnable = true;
	@Resource(name="${db.type}_ReportDAO")
	protected IReportDAO reportDAO;
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
							DataCenter_old.getDataToDbPool().submit(new SentReportSaveTask(reportDAO,list));
						}
					}else{
						sleep(100);
					}
					
				} catch (Exception e) {
					info_log.warn("SendHistorySaveThread"+ e);
				}
			
			}
			
		}
	}


	protected List<ReportBean> getSentRepors() {
		return DataCenter_old.getSentReportToDB(COUNT);
	}
	 

	public boolean doStop() {
		isRunnable = false ;
		info_log.info("--------< 状态报告记录存储线程关闭   >--------");
		interrupt();
		return true;
	}
	

}
