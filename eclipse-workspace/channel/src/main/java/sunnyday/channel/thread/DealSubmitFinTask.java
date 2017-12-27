package sunnyday.channel.thread;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import sunnyday.channel.cache.DataCenter;
import sunnyday.common.model.SmsMessage;
import sunnyday.tools.util.CommonLogFactory;

public class DealSubmitFinTask   implements Callable<Integer>{
	private static Logger log = CommonLogFactory.getCommonLog(DealSubmitFinTask.class);
	private DealRAO redisDeal ;
	private List<SmsMessage> smss ;
	private String submit_sent ;
 
	public DealSubmitFinTask(DealRAO redisDeal ,List<SmsMessage> smss,String submit_sent){
		this.redisDeal = redisDeal ;
		this.smss = smss ;
		this.submit_sent = submit_sent ;
	}
	
	public static void  doSomething(DealRAO redisDeal ,List<SmsMessage> smss,String submit_sent){
		long time1 = System.currentTimeMillis();
		redisDeal.putSubmitFin2Redis(smss, submit_sent);
		long time2 = System.currentTimeMillis();
		 
		if (time2 - time1 >3000) {
			log.info("DealSubmitFinTask too long time [" + (time2 - time1) + "] ms");
		}
	}
	 
	public Integer call() throws Exception {
		try {
			long time1 = System.currentTimeMillis();
			boolean flag = false ;
			try {
				flag = redisDeal.putSubmitFin2Redis(smss, submit_sent);
			} catch (Exception e) {
				log.warn("putSubmitFin2Redis",e);
			}
			long time2 = System.currentTimeMillis();
			if(!flag){
				DataCenter.setRedis_is_ok(false);
				DataCenter.addSendSubmit4FileQueue(smss);
			}else {
				DataCenter.setRedis_is_ok(true);
			}
			if (time2 - time1 >3000) {
				log.info("DealSubmitFinTask too long time [" + (time2 - time1) + "] ms");
			}
		} catch (Exception e) {
			log.error("DealSubmitFinTask",e);
		}
		
		return null;
	}
	 

}
