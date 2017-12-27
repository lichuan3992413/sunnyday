package sunnyday.controller.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.thread.DataCenter_old;
import sunnyday.controller.util.DealRAO;
import sunnyday.controller.util.DeliverUtil;
import sunnyday.controller.util.StatisticsUtil;
import sunnyday.controller.util.UtilTool;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.DateUtil;

/**
 * 下发短信和状态报告进行匹配，从redis(q:recvReport)和redis(hash:submit_sent_team_nu)取出数据进行匹配<br>
 * 匹配失败且状态报告超时时状态报告入数据中心unmatchedReportToDBQueue队列；未超时时重新入redis-q:recvReport队列;<br>
 * 匹配成功时状态报告入数据中心dealedReportToCacheQueue队列；根据短信的状态报告推送类型状态报告入数据中心sendReportQueue队列或者sendReportToDBQueue,
 * 同时短信入数据中心submitHistoryQueue队列
 */
public class RecReportTask  implements Callable<Integer>{
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	private List<Object> reportList ;
	private DealRAO redisDeal ; 
	private String submit_sent ;
	private String recv_report_queue_name ;
	private int deal_report_in_redis_timeOut;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	 
	
	public RecReportTask(DealRAO redisDeal,  List<Object> reportList,String submit_sent, String recv_report_queue_name,int deal_report_in_redis_timeOut) {
		this.redisDeal = redisDeal;
		this.reportList = reportList;
		this.submit_sent = submit_sent;
		this.recv_report_queue_name = recv_report_queue_name;
		this.deal_report_in_redis_timeOut = deal_report_in_redis_timeOut;
	}
	 
	
	/**
	 * 
	 * @param report
	 * @param deal_report_in_redis_timeOut 毫秒
	 * @return
	 */
 
	
	boolean dealTimeOut(ReportBean report,int deal_report_in_redis_timeOut){
		if(report.getRpt_return_time()!=null){
			try {
				long time = sdf.parse(report.getRpt_return_time()).getTime();
				 
				if(System.currentTimeMillis() - time > deal_report_in_redis_timeOut){
					return true ;
				}else{
					if(UtilTool.getTimeOutTryTimes()>0&&report.getDo_times()>=UtilTool.getTimeOutTryTimes()){
						return true ;
					}
				}
			} catch (Exception e) {
				report.setRpt_return_time(DateUtil.currentTimeToMs());
			}
		}else {
			report.setRpt_return_time(DateUtil.currentTimeToMs());
		}
		return false ;
	}

	public Integer call() throws Exception {
		long time1 = System.currentTimeMillis() ;
		//根据手机号+msg_id批量从已发送队列里获取已发送信息
		batchMatchReport(reportList);

		long time2 = System.currentTimeMillis() ;
		if(time2-time1>3000){
			log.info("RecReportTask too long time ["+(time2-time1)+"] ms "  );
		}
		return null;
	}
	
	private  void batchMatchReport(List<Object> reportList){
		long time1 =System.currentTimeMillis(); 
		List<SmsMessage> list = redisDeal.batchMatchReportFromRedis(reportList, submit_sent);
		long time2 =System.currentTimeMillis(); 
	     if(log.isDebugEnabled()){
	    	 log.debug("batchMatchReportFromRedis: " +reportList.size()+", ["+(time2-time1)+"] ms");
	     }
		List<ReportBean> tmp =  new ArrayList<ReportBean>();
		try {
			//进行拼装
			for(int i = 0 ; i < list.size() ; i++){ 
				try {
					
					SmsMessage sms = list.get(i);
					ReportBean report = (ReportBean)reportList.get(i);
					if(sms!=null){
						//匹配成功 产生状态报告 状态报告入匹配成功的队列 
						//log.info("hit sms->  mobile: "+sms.getMobile()+" tmp_id: "+sms.getTmp_msg_id()+", msg_id"+sms.getMsg_id());
						long time21 = System.currentTimeMillis() ;
						DataCenter_old.putDealedReportToCacheQueue(report);
						
						long time22 = System.currentTimeMillis() ;
						  if(log.isDebugEnabled()){
							  log.debug("putDealedReportToCacheQueue: " +reportList.size()+", ["+(time22-time21)+"] ms");
						     }
						DeliverUtil.matchReportCommonOperation(sms, report);
						long time23 = System.currentTimeMillis() ;
						  if(log.isDebugEnabled()){
							  log.debug("matchReportCommonOperation: " +reportList.size()+", ["+(time23-time22)+"] ms");
						     }
						StatisticsUtil.Statistics4Memory(sms);
						long time24 = System.currentTimeMillis() ;
						  if(log.isDebugEnabled()){
							  log.debug("StatisticsUtil: " +reportList.size()+", ["+(time24-time23)+"] ms");
						     }
						 
					}else {
						//没有匹配成功，判断是否超时，超时部分放回队列中
						if(dealTimeOut(report,deal_report_in_redis_timeOut)){
							DataCenter_old.addUnmatchedReportToDBQueue(report);
							if(log.isDebugEnabled()){
								log.debug("time_out_report -> "+report.getMsg_id() +", "+report.getFail_desc()+", "+report.getErr());
							}
						}else {
							report.setDo_times(report.getDo_times()+1);
							tmp.add(report);
						}
						
					}
				} catch (Exception e) {
					log.error("",e);
				}
				
			
			}
		} catch (Exception e) {
			log.error("",e);
		}
	
		if(tmp!=null&&tmp.size()>0){
			long time3 = System.currentTimeMillis() ;
			redisDeal.putReport2Redis(tmp,recv_report_queue_name);
			long time4 = System.currentTimeMillis() ;
			 
			if(log.isDebugEnabled()){
				log.debug("putReport2Redis: " +reportList.size()+", ["+(time4-time3)+"] ms");	
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		List<SmsMessage> list    =  new ArrayList<SmsMessage>();
		for(int i=0; i<100000;i++){
			SmsMessage sms  = new SmsMessage();
			list.add(sms);
		}
		
	}

}
