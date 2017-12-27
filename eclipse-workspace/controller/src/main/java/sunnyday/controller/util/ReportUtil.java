package sunnyday.controller.util;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import sunnyday.common.model.DeliverBean;
import sunnyday.common.model.ReportBean;
import sunnyday.controller.cache.GateConfigCache;
import sunnyday.controller.thread.DataCenter_old;
import sunnyday.controller.thread.GateRAO;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.Spring;

public class ReportUtil {
	private static Logger log = CommonLogFactory.getCommonLog("infoLog");
	private static int autoNumber = 0;
	@Autowired
	private static GateRAO gateRao = null;
	private static long report_redis2db_timeout=60l;
	private final  static int LIMIT=100;
	static {
		gateRao =  Spring.getApx().getBean(GateRAO.class);
	} 

	public static void checkSendReporTimeOut(String user_key) {
		try {
			if(gateRao==null){
				gateRao =  Spring.getApx().getBean(GateRAO.class);
			}
			List<Object> reportList = gateRao.getReportByUserId(user_key, LIMIT);
			if (reportList != null && reportList.size() > 0) {
				for (Object object : reportList) {
					ReportBean bean = (ReportBean) object;
					if(bean.getReveive_time()==0){
						bean.setReveive_time(System.currentTimeMillis());
					}
					
					if (checkTime(bean.getReveive_time(), ">")) {
						bean.setSend_status(1);
						bean.setStatus(0);
						try {
							//状态报告信息超时未取，直接入库
							DataCenter_old.addSentReportToDBQueue(bean);
						} catch (Exception e) {
							log.error("lost Report: [" + bean + "]", e);
						}
					} else {
						try {
							// 重新放回到待写入redis的队列中
							DataCenter_old.addSendReportQueue(bean);
						} catch (Exception e) {
							log.error("lost Report: [" + bean + "]", e);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(" ", e);
		}
	}

	public static void checkSendDeliverTimeOut(String user_key) {
		try {
			if(gateRao==null){
				gateRao =  Spring.getApx().getBean(GateRAO.class);
			}
			List<Object> deliverList = gateRao.getDeliverByUserId(user_key,LIMIT);
			if (deliverList != null && deliverList.size() > 0) {
				for (Object object : deliverList) {
					DeliverBean bean = (DeliverBean) object;
					if(bean.getReveive_time()==0){
						bean.setReveive_time(System.currentTimeMillis());
					}
					if (log.isDebugEnabled()) {
					log.debug("[midle bean count= "+ deliverList.size()+ "]bean: "+ bean+ "time: "+ (System.currentTimeMillis() - bean.getReveive_time()) + " ms");
					}
					if (checkTime(bean.getReveive_time(), ">")) {
						bean.setSend_status(1);
						bean.setStatus(0);
						try {
							// 上行信息超时未取，直接入库
							DataCenter_old.addSentDeliverToDBQueue(bean);
						} catch (Exception e) {
							log.error("lost Report: [" + bean + "]", e);
						}
					} else {
						try {
							// 重新放回到redis中
							DataCenter_old.addDealedDeliver(bean);
						} catch (Exception e) {
							log.error("lost Report: [" + bean + "]", e);
						}
					}
				}
			}

		} catch (Exception e) {
		}

	}

	private static boolean checkTime(long old_time,String type){
		try {
			report_redis2db_timeout = Long.parseLong(GateConfigCache.getValue("report_redis2db_timeout"));
		} catch (Exception e) {
		}
		boolean result =false ;
		 
		if(">".equals(type)){
			result = System.currentTimeMillis()-old_time > report_redis2db_timeout*1000;
		}else {
			result = System.currentTimeMillis()-old_time < report_redis2db_timeout*1000;
		}
	    return result ;
	}
	/**
	 * 产生唯一值，用来标记的短信唯一性
	 * @return
	 */
	public static Long getUniqueSeq(){
		Long result = null;
		try {
			String tmp = String.valueOf(System.currentTimeMillis())+ConfigPropertiesUtil.getMachineCode()+String.valueOf(getAutoNumber());
			result = Long.parseLong(tmp);
		} catch (Exception e) {
			result = System.nanoTime();
		}
		return result;
		
	} 
	
	public static String getUUid(){
		return String.valueOf(UUID.randomUUID());
	}
	
	
	
	public static void main(String[] args) {
		int count = 5000;
		Set<Long> set = new HashSet<Long>();
		for(int i=0;i<count;i++){
			System.err.println(getUniqueSeq());
			set.add(getUniqueSeq());
		}
		System.err.println(set.size());
	}
	
	
	private synchronized static  int  getAutoNumber(){
		autoNumber++;
		if(autoNumber>9999){
			autoNumber = 0;
		}
		return autoNumber;
	}
	
	
	@SuppressWarnings("unused")
	private static String getRandomNum(int pwd_len) {
		   // 35是因为数组是从0开始的，26个字母+10个数字
		   final int maxNum = 36;
		   int i; // 生成的随机数
		   int count = 0; // 生成的密码的长度
		   char[] str = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
		     '9' };

		   StringBuffer pwd = new StringBuffer("");
		   Random r = new Random();
		   while (count < pwd_len) {
		    // 生成随机数，取绝对值，防止生成负数，

		    i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1
		   
		    if (i >= 0 && i < str.length) {
		     pwd.append(str[i]);
		     count++;
		    }
		   }

		   return pwd.toString();
		}
}
