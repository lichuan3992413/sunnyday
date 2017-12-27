package sunnyday.channel.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.channel.cache.DataCenter;
import sunnyday.common.model.SmsMessage;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 可以多个节点同时启动
 * 处理下发记录,分配到不同队列分组中
 * 
 * @author 1307365
 * 
 */
@Service
public class SentMessageConsumeThread extends Thread  {
	private Logger log = CommonLogFactory.getCommonLog(SentMessageConsumeThread.class);
	
 
	
	@Value("#{config.submit_sent}")
	private String submit_sent;
	/**
	 * 单次处理队列中的条数
	 */
	@Value("#{config.queryCount}")
	private String queryCount;
	  
	
	 
	@Autowired
	private DealRAO redisDeal = null;
	private boolean isRunnable = true;
	private ExecutorService sentMessageConsumePool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors()+2, 60,TimeUnit.SECONDS, DataCenter.getSentMessageConsumeWorkQueue());
	
	@Override
	public void run() {
		log.info("---------------> 处理下发记录线程启动 <---------------");
		while (isRunnable) {
			long time1  = System.currentTimeMillis() ;
			try {
				/**
				 * 下发完成，待匹配的数据队列写入到redis
				 */
				List<SmsMessage> smss = DataCenter.querySubmitRespSucessMessage(Integer.parseInt(queryCount.trim()));
				if (smss != null && smss.size() > 0) {
				boolean flag = true;
					while (flag) {
						flag = false;
						int size = DataCenter.getSentMessageConsumeWorkQueue().remainingCapacity();
						if (size <= 20) {
							Thread.sleep(1000);
							log.warn("reportSendWorkQueue[" + size + "]   Capacity is <= 20 .");
							flag = true;
							continue;
						}
					}
					 
					 
					try {
						if (!sentMessageConsumePool.isShutdown()) {
							sentMessageConsumePool.submit(new DealSubmitFinTask(redisDeal ,smss,submit_sent));
						}
						sleep(100);
					} catch (Exception e) {
					}

				}else {
					sleep(200);
				}

			} catch (Exception e) {
				log.error("SentMessageConsumeThread Exception", e);
			}finally{
				long time2  = System.currentTimeMillis() ;
				if(time2-time1>3000){
					log.warn("SentMessageConsumeThread  ,处理耗时:["+(time2-time1)+"]ms");
				}
				
				
			}
		}
	}

 
	public boolean doStop() {
		this.isRunnable = false;
		log.info("--------< 处理下发记录线程关闭   >--------");
		interrupt();
		return false;
	}

}
