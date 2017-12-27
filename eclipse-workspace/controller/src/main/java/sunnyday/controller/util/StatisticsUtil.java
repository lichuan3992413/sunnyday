package sunnyday.controller.util;

import java.util.List;

import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.StatisticsHistoryModel;
import sunnyday.common.model.StatisticsModel;
import sunnyday.controller.cache.GateConfigCache;
import sunnyday.controller.thread.DataCenter_old;
/**
 * 短信下发数据统计工具类
 */
public class StatisticsUtil {
	 
	/**
	 * 内存统计入口分布在3个点：
	 * 第一个点：处理拒绝驳回队列时，
	 * 第二个点：状态匹配成功时，
	 * 第三个点：处理redis中超时未匹配的下发短信时
	 * 
	 * 内存匹配完成后进行统计
	 * 1）下发数+1
	 * 2）成功数+1或失败数+1或未知数+1
	 * @param sms
	 * @param type 0:处理未知统计 1:处理匹配统计
	 */
	public static void Statistics4Memory(SmsMessage sms) {
		int count = sms.getCharge_count();
		if(count<1){
			count = 1;
		}
		//天级别统计
		if(getSwitch(1)){
			String key_day = getStatisticsKey(sms, 1);
			if (DataCenter_old.getStatistics_day().containsKey(key_day)) {
				StatisticsHistoryModel model = DataCenter_old.getStatistics_day().get(key_day);
				if(model!=null){
					model.setAmount(model.getAmount()+count);
					DataCenter_old.getStatistics_day().put(key_day, model);
				}else{
					model = new StatisticsHistoryModel();
					model.setUser_sn(sms.getUser_sn());
					model.setTd_code(sms.getTd_code());
					model.setService_code(sms.getService_code());
					model.setResponse(sms.getResponse());
					model.setErr(sms.getErr());
					model.setFail_desc(sms.getFail_desc());
					model.setDest_flag(sms.getDest_flag());
					model.setSend_date(getStatisticsTime(sms,1));
					//必达短信标识和批次号
					
					 
					model.setAmount(count);
					DataCenter_old.getStatistics_day().put(key_day, model);
				}
				
			} else {
				StatisticsHistoryModel model = new StatisticsHistoryModel();
				model.setUser_sn(sms.getUser_sn());
				model.setTd_code(sms.getTd_code());
				model.setService_code(sms.getService_code());
				model.setResponse(sms.getResponse());
				model.setErr(sms.getErr());
				model.setFail_desc(sms.getFail_desc());
				model.setDest_flag(sms.getDest_flag());
				model.setSend_date(getStatisticsTime(sms,1));
			 
				model.setAmount(count);
				DataCenter_old.getStatistics_day().put(key_day, model);
			}
		}
		
		//小时分钟级别统计
		if(getSwitch(2)){
			String key_hour = getStatisticsKey(sms, 2);
			String key_min = getStatisticsKey(sms, 3);
			if (DataCenter_old.getStatistics_hour().containsKey(key_hour)) {
				StatisticsModel model = DataCenter_old.getStatistics_hour().get(key_hour);
				if(model!=null){
					model.setSend_count(model.getSend_count() + count);
					if (sms.getResponse() == 0) {
						model.setSuccess_count(model.getSuccess_count() + count);
					} else if (sms.getResponse() == 1000) {
						model.setUnknown_count(model.getUnknown_count() + count);
					} else {
						model.setFail_count(model.getFail_count() + count);
					}
					DataCenter_old.getStatistics_hour().put(key_hour, model);
				}else{
					model = new StatisticsModel();
					model.setUser_sn(sms.getUser_sn());
					model.setUser_id(sms.getUser_id());
					model.setTd_code(sms.getTd_code());
					model.setSend_time(getStatisticsTime(sms,2));
				 	
					model.setSend_count( count);
					if (sms.getResponse() == 0) {
						model.setSuccess_count(count);
					} else if (sms.getResponse() == 1000) {
						model.setUnknown_count(count);
					} else {
						model.setFail_count(count);
					}
					DataCenter_old.getStatistics_hour().put(key_hour, model);
				
				}
			
			} else {
				StatisticsModel model = new StatisticsModel();
				model.setUser_sn(sms.getUser_sn());
				model.setUser_id(sms.getUser_id());
				model.setTd_code(sms.getTd_code());
				model.setSend_time(getStatisticsTime(sms,2));
				 
				model.setSend_count( count);
				if (sms.getResponse() == 0) {
					model.setSuccess_count(count);
				} else if (sms.getResponse() == 1000) {
					model.setUnknown_count(count);
				} else {
					model.setFail_count(count);
				}
				DataCenter_old.getStatistics_hour().put(key_hour, model);
			}

			if (DataCenter_old.getStatistics_min().containsKey(key_min)) {
				StatisticsModel model = DataCenter_old.getStatistics_min().get(key_min);
				if(model!=null){
					model.setSend_count(model.getSend_count() + count);
					if (sms.getResponse() == 0) {
						model.setSuccess_count(model.getSuccess_count() + count);
					} else if (sms.getResponse() == 1000) {
						model.setUnknown_count(model.getUnknown_count() + count);
					} else {
						model.setFail_count(model.getFail_count() + count);
					}
					DataCenter_old.getStatistics_min().put(key_min, model);
				}else{
					model = new StatisticsModel();
					model.setUser_sn(sms.getUser_sn());
					model.setUser_id(sms.getUser_id());
					model.setTd_code(sms.getTd_code());
					model.setSend_time(getStatisticsTime(sms,3));
				 
					model.setSend_count(count);
					if (sms.getResponse() == 0) {
						model.setSuccess_count(count);
					} else if (sms.getResponse() == 1000) {
						model.setUnknown_count(count);
					} else {
						model.setFail_count(count);
					}
					DataCenter_old.getStatistics_min().put(key_min, model);
				}
				

			} else {
				StatisticsModel model = new StatisticsModel();
				model.setUser_sn(sms.getUser_sn());
				model.setUser_id(sms.getUser_id());
				model.setTd_code(sms.getTd_code());
				model.setSend_time(getStatisticsTime(sms,3));
			 
				model.setSend_count(count);
				if (sms.getResponse() == 0) {
					model.setSuccess_count(count);
				} else if (sms.getResponse() == 1000) {
					model.setUnknown_count(count);
				} else {
					model.setFail_count(count);
				}
				DataCenter_old.getStatistics_min().put(key_min, model);
			}
		}
		

	}
	
	/**
	 * 表匹配成功之后，按天统计 1000状态-1; 小时分钟 未知 -1 成功或失败 +1
	 * send_catch表超时移表时，按天统计 800状态 +1, 1000状态 -1
	 * 
	 * 表匹配完成后加入统计
	 * 1）成功数+1或失败数+1
	 * 2）未知数-1
	 * @param sms
	 */
    public static void  Statistics4DBTable(SmsMessage sms){
    	int count = sms.getCharge_count();
		if(count<1){
			count = 1;
		}
		//天级别统计
		if (getSwitch(1)) {
			String key_day = getStatisticsKey(sms, 1);
			if (DataCenter_old.getStatistics_day().containsKey(key_day)) {
				StatisticsHistoryModel model = DataCenter_old.getStatistics_day().get(key_day);
				if(model!=null){
					model.setAmount(model.getAmount()+count);
					DataCenter_old.getStatistics_day().put(key_day, model);
				}else{
					model = new StatisticsHistoryModel();
					model.setUser_sn(sms.getUser_sn());
					model.setTd_code(sms.getTd_code());
					model.setService_code(sms.getService_code());
					model.setResponse(sms.getResponse());
					model.setErr(sms.getErr());
					model.setFail_desc(sms.getFail_desc());
					model.setDest_flag(sms.getDest_flag());
					model.setSend_date(getStatisticsTime(sms,1));
					 
					model.setAmount(count);
					DataCenter_old.getStatistics_day().put(key_day, model);
				}
				
			} else {
				StatisticsHistoryModel model = new StatisticsHistoryModel();
				model.setUser_sn(sms.getUser_sn());
				model.setTd_code(sms.getTd_code());
				model.setService_code(sms.getService_code());
				model.setResponse(sms.getResponse());
				model.setErr(sms.getErr());
				model.setFail_desc(sms.getFail_desc());
				model.setDest_flag(sms.getDest_flag());
				model.setSend_date(getStatisticsTime(sms,1));
		 
				model.setAmount(count);
				DataCenter_old.getStatistics_day().put(key_day, model);
			}
		}
		//小时分钟级别统计
		if (getSwitch(2)) {
			String key_hour = getStatisticsKey(sms, 2);
			String key_min = getStatisticsKey(sms, 3);
			int  response = sms.getResponse();
			if(response!=800){
				//800为状态报告匹配超时数据，分钟级和小时级别的统计不需要对此进行处理
				if (DataCenter_old.getStatistics_hour().containsKey(key_hour)) {
					StatisticsModel model = DataCenter_old.getStatistics_hour().get(key_hour);
					if(model!=null){
						model.setUnknown_count(model.getUnknown_count() - count);
						if (sms.getResponse() == 0) {
							model.setSuccess_count(model.getSuccess_count() + count);
						} else if (sms.getResponse() != 1000) {
							model.setFail_count(model.getFail_count() + count);
						} 
						DataCenter_old.getStatistics_hour().put(key_hour, model);
					}else {
						model = new StatisticsModel();
						model.setUser_sn(sms.getUser_sn());
						model.setUser_id(sms.getUser_id());
						model.setTd_code(sms.getTd_code());
						model.setSend_time(getStatisticsTime(sms,2));
						 
						model.setUnknown_count(-count);
						if (sms.getResponse() == 0) {
							model.setSuccess_count(count);
						} else if (sms.getResponse() != 1000) {
							model.setFail_count(count);
						} 
						DataCenter_old.getStatistics_hour().put(key_hour, model);
					}
					
				} else {
					StatisticsModel model = new StatisticsModel();
					model.setUser_sn(sms.getUser_sn());
					model.setUser_id(sms.getUser_id());
					model.setTd_code(sms.getTd_code());
					model.setSend_time(getStatisticsTime(sms,2));
					 
					model.setUnknown_count(-count);
					if (sms.getResponse() == 0) {
						model.setSuccess_count(count);
					} else if (sms.getResponse() != 1000) {
						model.setFail_count(count);
					} 
					DataCenter_old.getStatistics_hour().put(key_hour, model);
				}
				
				if (DataCenter_old.getStatistics_min().containsKey(key_min)) {
					StatisticsModel model = DataCenter_old.getStatistics_min().get(key_min);
					if(model!=null){
						model.setUnknown_count(model.getUnknown_count() - count);
						if (sms.getResponse() == 0) {
							model.setSuccess_count(model.getSuccess_count() + count);
						} else if (sms.getResponse() != 1000) {
							model.setFail_count(model.getFail_count() + count);
						} 
						DataCenter_old.getStatistics_min().put(key_min, model);
					}else{
						model = new StatisticsModel();
						model.setUser_sn(sms.getUser_sn());
						model.setUser_id(sms.getUser_id());
						model.setTd_code(sms.getTd_code());
						model.setSend_time(getStatisticsTime(sms,3));
						 
						model.setUnknown_count(-count);
						if (sms.getResponse() == 0) {
							model.setSuccess_count(count);
						} else if (sms.getResponse() !=1000) {
							model.setFail_count(count);
						} 
						DataCenter_old.getStatistics_min().put(key_min, model);
					}
					

				} else {
					StatisticsModel model = new StatisticsModel();
					model.setUser_sn(sms.getUser_sn());
					model.setUser_id(sms.getUser_id());
					model.setTd_code(sms.getTd_code());
					model.setSend_time(getStatisticsTime(sms,3));
					 
					model.setUnknown_count(-count);
					if (sms.getResponse() == 0) {
						model.setSuccess_count(count);
					} else if (sms.getResponse() !=1000) {
						model.setFail_count(count);
					} 
					DataCenter_old.getStatistics_min().put(key_min, model);
				}
				
			}
		}
	}
    
    /**
     * 表匹配成功之后，按天统计 1000状态-1; 小时分钟 未知 -1 成功或失败 +1
	 * send_catch表超时移表时，按天统计 800状态 +1, 1000状态 -1
	 * 
     * @param sms
     * @param response
     */
    public static void  Statistics4DBTable(SmsMessage sms,int response){
    	int count = sms.getCharge_count();
		if(count<1){
			count = 1;
		}
    	//天级别统计
    	if(getSwitch(1)){
    		String key_day = getStatisticsHistoryKey(sms);
    		if (DataCenter_old.getStatistics_day().containsKey(key_day)) {
    			StatisticsHistoryModel model = DataCenter_old.getStatistics_day().get(key_day);
    			if(model!=null){
    				model.setAmount(model.getAmount()-count);
        			DataCenter_old.getStatistics_day().put(key_day, model);
    			}else{
    				model = new StatisticsHistoryModel();
        			model.setUser_sn(sms.getUser_sn());
        			model.setTd_code(sms.getTd_code());
        			model.setService_code(sms.getService_code());
    				model.setResponse(sms.getResponse());
    				model.setErr(sms.getErr());
    				model.setFail_desc(sms.getFail_desc());
    				model.setDest_flag(sms.getDest_flag());
    				model.setSend_date(getStatisticsTime(sms, 1));
    		 
    				model.setAmount(-count);
        			DataCenter_old.getStatistics_day().put(key_day, model);
    			}
    			
    		} else {
    			StatisticsHistoryModel model = new StatisticsHistoryModel();
    			model.setUser_sn(sms.getUser_sn());
    			model.setTd_code(sms.getTd_code());
    			model.setService_code(sms.getService_code());
				model.setResponse(sms.getResponse());
				model.setErr(sms.getErr());
				model.setFail_desc(sms.getFail_desc());
				model.setDest_flag(sms.getDest_flag());
				model.setSend_date(getStatisticsTime(sms, 1));
			 
				model.setAmount(-count);
    			DataCenter_old.getStatistics_day().put(key_day, model);
    		}
    	}
    }
    
    public static void  Statistics4History(List<Object> list){
    	//天级别统计
    	if(getSwitch(1)){
    		if(list!=null&&list.size()>0){
        		for(Object o:list){
        			Statistics4History(o);
        		}
        	}
    	}
    	
    }
    
    /**
     * 统计短信下发到状态报告返回的一个往返延时统计处理
     */
    public static void  Statistics4HistoryByList(List<SmsMessage> list){
    	//天级别统计
    	if(getSwitch(1)){
    		if(list!=null&&list.size()>0){
        		for(Object o:list){
        			Statistics4History(o);
        		}
        	}
    	}
    	
    }
    /**
     * 下发完成后进行历史数据分析统计
     * 1）下发延时
     * 2）状态报告延时
     */
    public static void  Statistics4History(Object object){
    	SmsMessage sms = (SmsMessage) object;
    	int count = sms.getCharge_count();
		if(count<1){
			count = 1;
		}
    	//小时分钟级别统计
    	if(getSwitch(2)){
        	sms.setStat_flag(1);//把下发历史表的统计状态改为已经统计
        	String key_hour = getStatisticsKey(sms, 2);
    		String key_min = getStatisticsKey(sms, 3);
    		int time = DateUtil.subTime(sms.getMsg_receive_time(), sms.getMsg_report_time());
    		StatisticsModel model_hour = null;
    		StatisticsModel model_min = null;
    		if (DataCenter_old.getStatistics_hour().containsKey(key_hour)) {
    			model_hour = DataCenter_old.getStatistics_hour().get(key_hour);
    			if(model_hour==null){
    				model_hour = new StatisticsModel();
        			model_hour.setUser_sn(sms.getUser_sn());
        			model_hour.setUser_id(sms.getUser_id());
        			model_hour.setTd_code(sms.getTd_code());
        			model_hour.setSend_time(getStatisticsTime(sms,2));	
        		 
    			}
    		} else {
    			model_hour = new StatisticsModel();
    			model_hour.setUser_sn(sms.getUser_sn());
    			model_hour.setUser_id(sms.getUser_id());
    			model_hour.setTd_code(sms.getTd_code());
    			model_hour.setSend_time(getStatisticsTime(sms,2));
    		 
    		}
    		
    		if (DataCenter_old.getStatistics_min().containsKey(key_min)) {
    			model_min = DataCenter_old.getStatistics_min().get(key_min);
    			if(model_min==null){
    				model_min = new StatisticsModel();
        			model_min.setUser_sn(sms.getUser_sn());
        			model_min.setUser_id(sms.getUser_id());
        			model_min.setTd_code(sms.getTd_code());
        			model_min.setSend_time(getStatisticsTime(sms,3));
        		 
    			}
    		} else {
    			model_min = new StatisticsModel();
    			model_min.setUser_sn(sms.getUser_sn());
    			model_min.setUser_id(sms.getUser_id());
    			model_min.setTd_code(sms.getTd_code());
    			model_min.setSend_time(getStatisticsTime(sms,3));
    		 
    		}
    		
    		if(sms.getResponse()==800){
    			//若长时间超时未知时，则不需要进行状态报告统计
    			count = 0 ;
    		}
    		
    		if(time<=5){
    			model_hour.setReport_five_count(model_hour.getReport_five_count()+count);
    			model_min.setReport_five_count(model_min.getReport_five_count()+count);
    		}else if(time<=10){
    			model_hour.setReport_ten_count(model_hour.getReport_ten_count()+count);
    			model_min.setReport_ten_count(model_min.getReport_ten_count()+count);
    		}else if(time<=20){
    			model_hour.setReport_twenty_count(model_hour.getReport_twenty_count()+count);
    			model_min.setReport_twenty_count(model_min.getReport_twenty_count()+count);
    		}else if(time<=60){
    			model_hour.setReport_sixty_count(model_hour.getReport_sixty_count()+count);
    			model_min.setReport_sixty_count(model_min.getReport_sixty_count()+count);
    		}else{
				model_hour.setReport_other_count(model_hour.getReport_other_count()+count);
				model_min.setReport_other_count(model_min.getReport_other_count()+count);
    		}
    		DataCenter_old.getStatistics_hour().put(key_hour, model_hour);
    		DataCenter_old.getStatistics_min().put(key_min, model_min);
    	}
    	
   	}
    
	public static String getStatisticsKey(SmsMessage sms,int type) {
		String result   = "";
		String send_time   = sms.getMsg_send_time();
		if(send_time==null||"".equals(send_time)){
			send_time = DateUtil.currentTimeToMs();
		}
		switch (type) {
		case 1://天 yyyy-MM-dd
			result = send_time.substring(0, 10);
			result =sms.getUser_sn()+sms.getTd_code()+sms.getService_code()+sms.getResponse()+sms.getErr()+sms.getFail_desc()+ sms.getDest_flag()+((String)sms.getExtraField("arrive_number"))+result;
			break;
		case 2://小时 yyyy-MM-dd HH
			result = send_time.substring(0, 13);
			result = sms.getUser_id()+sms.getTd_code()+((String)sms.getExtraField("arrive_number"))+result;
			break;
		case 3://分钟 yyyy-MM-dd HH:mm
			result = send_time.substring(0, 16);
			result = sms.getUser_id()+sms.getTd_code()+((String)sms.getExtraField("arrive_number"))+result;
			break;

		default://分钟 yyyy-MM-dd HH:mm
			result = send_time.substring(0, 16);
			result = sms.getUser_id()+sms.getTd_code()+((String)sms.getExtraField("arrive_number"))+result;
			break;//天
		}
		return result;
	}
	
	
	public static String getStatisticsHistoryKey(SmsMessage sms) {
		String result = "";
		String send_time = sms.getMsg_send_time();
		if (send_time == null || "".equals(send_time)) {
			send_time = DateUtil.currentTimeToMs();
		}
		result = send_time.substring(0, 10);
		result =sms.getUser_sn()+sms.getTd_code()+sms.getService_code()+sms.getResponse()+sms.getErr()+sms.getFail_desc()+ sms.getDest_flag()+((String)sms.getExtraField("arrive_number"))+result;
		return result;
	}
	
	public static String getStatisticsTime(SmsMessage sms,int type) {
		String result   = "";
		String send_time   = sms.getMsg_send_time();
		if(send_time==null||"".equals(send_time)){
			send_time = DateUtil.currentTimeToMs();
		}
		switch (type) {
		case 1://天 yyyy-MM-dd
			result = send_time.substring(0, 10);
			break;
		case 2://小时 yyyy-MM-dd HH
			result = send_time.substring(0, 13);
			break;
		case 3://分钟 yyyy-MM-dd HH:mm
			result = send_time.substring(0, 16);
			break;

		default://分钟 yyyy-MM-dd HH:mm
			result = send_time.substring(0, 16);
			break;//天
		}
		return result;
	}
	/**
	 * 获取统计开关
	 * @param type
	 * @return 1 天级别统计 2 小时,分钟级别统计
	 */
	public static boolean getSwitch(int type){
		boolean result = false ;
		switch (type) {
		case 1://天 yyyy-MM-dd
			if("on".equals(GateConfigCache.getValue("statistics_day_switch"))){
				result = true ;
			}
			break;
		case 2://小时 yyyy-MM-dd HH
			if("on".equals(GateConfigCache.getValue("statistics_deal_switch"))){
				result = true ;
			}
			break;

		default://分钟 yyyy-MM-dd HH:mm
			result = false ;
			break;//天
		}
		return result;
	}
	
}
