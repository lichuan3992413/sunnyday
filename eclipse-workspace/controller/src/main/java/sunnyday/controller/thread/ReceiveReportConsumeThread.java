package sunnyday.controller.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import sunnyday.controller.task.RecReportTask;
import sunnyday.controller.util.DealRAO;
import sunnyday.controller.util.UtilTool;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 可以多个节点同时启动
 * 处理接收到网关状态报告记录,和初步状态报告匹配<br>
 * 启动RecReportTask任务用于下发短信和状态报告进行匹配<br>
 * @author 1307365
 *
 */
@Service
public class ReceiveReportConsumeThread extends Thread {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	@Value("#{config.recv_report_queue_name}")
	private String recv_report_queue_name;

	@Value("#{config.recv_report_queue_limit}")
	private int recv_report_queue_limit;

	@Value("#{config.submit_sent}")
	private String submit_sent;


	@Autowired
	private DealRAO redisDeal = null;
	private boolean isRunnable = true;
 
	private ExecutorService receiveReportConsumePool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 
			Runtime.getRuntime().availableProcessors()+2, 60,TimeUnit.SECONDS, DataCenter_old.getReceiveReportConsumeWorkQueue());

	public void run() {
		log.info("---------------> 处理接收到网关状态报告线程启动 <---------------");
		while (isRunnable) {
			try {
				int deal_report_in_redis_timeOut = UtilTool.getDealReportInRedisTimeOut();
				List<Object> reportList = redisDeal.getMessageFromDataCenter(recv_report_queue_name, recv_report_queue_limit);
				if(reportList != null && reportList.size() > 0){
					boolean flag = true;
					while (flag) {
						flag = false;
						int size = DataCenter_old.getReceiveReportConsumeWorkQueue().remainingCapacity();
						if (size <= 20) {
							Thread.sleep(1000);
							log.warn("reportSendWorkQueue[" + size + "]   Capacity is <= 20 .");
							flag = true;
							continue;
						}
					}
					if (!receiveReportConsumePool.isShutdown()) {
						receiveReportConsumePool.submit(new RecReportTask(redisDeal, reportList, submit_sent,recv_report_queue_name,deal_report_in_redis_timeOut));
					}
				 try {
					 sleep(50);
				} catch (Exception e) {
				}
				}else {
					sleep(100);
				}
			} catch (Exception e) {
				if(e.getMessage()!=null&&!e.getMessage().contains("sleep interrupted")){
					log.error("ReceiveReportConsumeThread Exception", e);
				}
				
				
			} 
		}
	}

	 
	@Override
	public boolean doStop() {
		this.isRunnable = false;
		log.info("--------< 处理接收到网关状态报告线程关闭   >--------");
		interrupt();
		return false;
	}


}
