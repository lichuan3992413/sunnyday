package sunnyday.channel.thread;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import sunnyday.common.model.SmsMessage;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.DateUtil;

public class ScanMessageTask implements Callable<Integer> {
	private boolean finshed = false;
	private ArrayBlockingQueue<SmsMessage> destQueue ;
	private CommonRAO dao ;
	private String yw_code;
	private String queueName;
	/*
	private String max_queue_size;*/
	private String getQueueCount;
	private String msgTime;
	private String msgCount;
	private int per;
	private  Logger log = CommonLogFactory.getLog(ScanMessageTask.class);
	
	public ScanMessageTask(ArrayBlockingQueue<SmsMessage> queue,CommonRAO dao, String yw_code,String queue_name,String msgTime,String msgCount,String getQueueCount,int per){
		this.destQueue = queue ;
		this.dao = dao ;
		this.yw_code = yw_code ;
		this.queueName = queue_name ;
		this.msgTime = msgTime ;
		this.msgCount = msgCount ;
		this.getQueueCount = getQueueCount ;
		this.per = per;
	}

	public boolean isFinshed() {
		return finshed;
	}

	public Integer call() throws Exception {
		long time0 = System.currentTimeMillis() ;
		try {
			int minCapacity = Integer.parseInt(getQueueCount.trim());
			if (destQueue.remainingCapacity()>minCapacity) {
				
				//log.info("yw_code: "+yw_code+"; remainingCapacity: "+destQueue.remainingCapacity()+"; minCapacity: "+minCapacity);
				
				long time1= System.currentTimeMillis();
				List<Object> smsList = dao.getSmsListByYwcode(queueName + yw_code, per);
				if(smsList==null||smsList.size()<1){
					try {
						Thread.sleep(10);
					} catch (Exception e) {
					}
					return 0;
				}
				long time2= System.currentTimeMillis();
				log.info("yw_code: "+yw_code+"; get ["+smsList.size()+"] from redis: "+(time2-time1)+" ms");
				for (Object smsTmp : smsList) {
					SmsMessage sms = (SmsMessage) smsTmp;
					String keyName = sms.getMsg_id() + "_"+ sms.getMobile() + "_"+ sms.getMsg_content();
					//过滤重复下发
					long time3= System.currentTimeMillis();
					int total= sms.getPktotal() ;
//					Object obj = EhcacheRepeatMsgFilter.doFilter(EncodingUtils.MD5(keyName), Integer.parseInt(msgTime.trim()),Integer.parseInt(msgCount.trim())*total);
					long time4= System.currentTimeMillis();
					if(log.isDebugEnabled()){
						log.debug("yw_code: "+yw_code+";msgID: "+sms.getMsg_id()+"; mobile: "+sms.getMobile()+"; FilterSMS: "+(time4-time3)+" ms"+"; scan_time: "+sms.getMsg_scan_time()+";  QueueSize: "+destQueue.size());
					}
					sms.setMsg_scan_time(DateUtil.currentTimeToMs());//添加进扫描队列时间
					 destQueue.put(sms);
//					if (obj == null) {
//						 destQueue.put(sms);
//					} else {
//						sms.setStatus(2);
//						sms.setResponse(116);
//						sms.setErr("116");
//						sms.setMsg_send_time(DateUtil.currentTimeToMs());
//						sms.setFail_desc("UNDELIV_发送端重复发送拦截");
//						DataCenter.addSubmitRespMessage(sms);
//						log.error("yw_code:"+yw_code+" the same message:"+ keyName);
//					}
					
					long send_time = DateUtil.diffTime(sms.getMsg_deal_time(), sms.getMsg_scan_time());
				    if(send_time>5000){
				    	log.warn("[scan_Message_TimeOut] yw_code: "+yw_code+" , 下发扫描延时["+send_time+"] ms, mobile: "+sms.getMobile()+", content: "+sms.getMsg_content());
				    }
				}
				
			}else {
				log.warn("yw_code: "+yw_code+"; QueueSize: "+destQueue.size()+"; getQueueCount: "+getQueueCount+"; 队列剩余空间["+destQueue.remainingCapacity()+"]不足，暂停扫描！");
			}
		} catch (Exception e) {
			log.error("",e);
		}finally{
			finshed = true ;
			long time00 = System.currentTimeMillis() ; 
			if(time00-time0>5000){
				log.warn("yw_code: "+yw_code+"; 扫描耗时过长["+(time00-time0)+"]ms");
			}
		}
		return null;
	}

}
