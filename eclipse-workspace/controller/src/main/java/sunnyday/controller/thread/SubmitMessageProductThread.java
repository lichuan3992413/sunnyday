package sunnyday.controller.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.common.model.SmsMessage;
import sunnyday.controller.util.SendRAO;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 启动SubmitMessageProductTask任务，<br>
 * 将数据中心submitMessageQueue队列中短信数据写入发送端redis队列(q:send:td_code:user_id)中
 */
@Service
public class SubmitMessageProductThread extends Thread {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	@Autowired
	private SendRAO redisSend = null;
	@Value("#{config.send_queue_name}")
	private String send_queue_name;
	
	private boolean isRunnable = true;
	private ExecutorService submitMessageProductPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors()+2, 60,TimeUnit.SECONDS, DataCenter_old.getSubmitMessageProductWorkQueue());

	   
	@Override
	public void run() {
		log.info("---------------> 下发信息生产 <---------------");
		while (isRunnable) {
			long time1  = System.currentTimeMillis() ;
			try {
				List<SmsMessage> sentMessageList = DataCenter_old.getSubmitMessage(1000);
				//发送完成
				if(sentMessageList!=null&&sentMessageList.size()>0){
					 boolean flag = true;
						while (flag) {
							flag = false;
							int size = DataCenter_old.getSubmitMessageProductWorkQueue().remainingCapacity();
							if (size <= 20) {
								Thread.sleep(1000);
								log.warn("submitMessageProductWorkQueue[" + size + "]   Capacity is <= 20 .");
								flag = true;
								continue;
							}
						}
						if (!submitMessageProductPool.isShutdown()) {
							submitMessageProductPool.submit(new SubmitMessageProductTask(send_queue_name, redisSend, sentMessageList));
						 }
				}else {
					sleep(100);
				}

			} catch (Exception e) {
				if(e.getMessage()!=null&&!e.getMessage().contains("sleep interrupted")){
					log.error("SubmitMessageProductThread Exception", e);
				}
				
			}finally{
				long time2  = System.currentTimeMillis() ;
				if(time2-time1>3000){
					log.warn("SubmitMessageProductThread  ,处理耗时:["+(time2-time1)+"]ms");
				}
			}
		}
	}
	
 
	public boolean doStop() {
		this.isRunnable = false;
		interrupt();
		return false;
	}
}
