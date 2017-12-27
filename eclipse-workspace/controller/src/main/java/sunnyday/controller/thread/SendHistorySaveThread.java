package sunnyday.controller.thread;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;


import sunnyday.common.model.SmsMessage;
import sunnyday.controller.DAO.IMessageDAO;
import sunnyday.controller.task.SendHistorySaveTask;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 启动SendHistorySaveTask任务，将数据中心submitHistoryQueue队列中短信保存至数据库中submit_message_send_history表中
 */
@Service
public class SendHistorySaveThread extends Thread {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	private boolean isRunnable = true;
	@Resource(name="${db.type}_MessageDAO")
	private IMessageDAO dao;
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
						log.warn("dataToDbWorkQueue[" + size + "]   Capacity is <= 20 .");
						flag = true;
						continue;
					}
					
					List<SmsMessage> list =  getSubmitHistory() ;
					if(list!=null&&list.size()>0){
						if (!DataCenter_old.getDataToDbPool().isShutdown()) {
							DataCenter_old.getDataToDbPool().submit(new SendHistorySaveTask(dao,list));
						}
					}else{
						sleep(100);
					}
					
				} catch (Exception e) {
					log.warn("SendHistorySaveThread"+ e);
				}
			
			}
			
		}
	}


	protected List<SmsMessage> getSubmitHistory() {
		return DataCenter_old.getSubmitHistory(COUNT);
	}
	 
	@Override
	public boolean doStop() {
		isRunnable = false ;
		log.info("--------< 下发记录存储线程关闭   >--------");
		interrupt();
		return true;
	}
}
