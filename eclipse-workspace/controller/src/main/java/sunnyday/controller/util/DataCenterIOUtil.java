package sunnyday.controller.util;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import sunnyday.common.model.CheckMessage;
import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.SmsTemplateInfo;
import sunnyday.common.model.TdInfo;
import sunnyday.common.model.UserBean;
import sunnyday.controller.cache.ErrCodeCache;
import sunnyday.controller.cache.GateConfigCache;
import sunnyday.controller.cache.SmsTemplateCache;
import sunnyday.controller.cache.TdInfoCache;
import sunnyday.controller.cache.UserInfoCache;
import sunnyday.controller.thread.DataCenter_old;
import sunnyday.tools.util.CommonLogFactory;


public class DataCenterIOUtil {
	private static Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	/**
	 * 通道切换处理 数据初始化处理
	 * @param eachMsg
	 */
	private static void beforeHandleForTest(SmsMessage eachMsg){
		eachMsg.setStatus(8);
		eachMsg.setResponse(0);
		//初步给完整短信内容赋值
		eachMsg.setComplete_content(eachMsg.getMsg_content());
		eachMsg.setMsg_scan_time(DateUtil.currentTimeToMs());
		eachMsg.setIstest("test_message");
//		eachMsg.setMsg_deal_time(DateUtil.currentTimeToMs());
	}
	
	public static void beforeHandle(SmsMessage eachMsg){
		eachMsg.setStatus(8);
		eachMsg.setResponse(0);
		//初步给完整短信内容赋值
		eachMsg.setComplete_content(eachMsg.getMsg_content());
		eachMsg.setMsg_scan_time(DateUtil.currentTimeToMs());
//		eachMsg.setMsg_deal_time(DateUtil.currentTimeToMs());
	}
	/**
	 * 处理通道切换, 设置Sms对象部分初始值, 根据信息类型加入到不同的处理队列中.
	 * @param eachMsg
	 */
	public static void InitAndHandOutSms(SmsMessage eachMsg) {
		if(eachMsg != null){

			String type = "";
			int pktotal = eachMsg.getPktotal();
			int charge_count = eachMsg.getCharge_count();

			if (charge_count == 1 && (pktotal == 0 || pktotal == 1)) {
				eachMsg.setMsg_guid(ReportUtil.getUUid());
				// 标记短信的唯一值
				eachMsg.setRpt_seq(ReportUtil.getUniqueSeq());
				// 普通短短信-----add into sys-check queue if not full;
				SetSignatureUtil.setSignature(eachMsg);
				type = "普通短短信";
				filterDisturb(eachMsg, "SystemCheckQueue");
				// What to do if queue is full?
			} else if (charge_count == 1 && pktotal > 1) {
				// 长短信，由于需拼接所以无需再重新赋值
				// 分条长短信-----
				type = "分条长短信";
				// Each_message add into long_message_in_DB queue if not full;
				filterDisturb(eachMsg, "LongMessageInsertDBQueue");
				// What to do if queue is full?
			} else if (charge_count > 1 && pktotal < 2) {
				// 整条长短信----- splitList add into sys-check queue if not full;
				type = "整条长短信";
				eachMsg.setMsg_guid(ReportUtil.getUUid());
				TdInfo td = TdInfoCache.getTd_info().get(
						String.valueOf(eachMsg.getTd_code()));
				if (td != null && td.getSubmit_type() == 1) {
					// submit td is not support complete content in one message.
					List<SmsMessage> splitList = LongMessageHandler
							.splitLongMessageByRules(eachMsg);
					if (charge_count != splitList.size()) {
						StringBuffer sb = new StringBuffer();
						sb.append(
								"拆分结果与 charge_count 不符[charge_count = "
										+ charge_count + "][split_count = "
										+ splitList.size() + "]").append("\n");
						int count = 1;
						for (SmsMessage sms : splitList) {
							sms.setFail_desc("拆分结果与计费条数不符");
							sms.setResponse(2);
							sb.append(count + ". " + sms).append("\n");
							count++;
						}
						log.error(sb.toString());
						DataCenter_old.addRejectMessage(splitList);
					} else {
						filterDisturbs(splitList, "SystemCheckQueue");
					}
					// what to do if queue is full?
				} else {
					// 标记短信的唯一值
					eachMsg.setMsg_guid(ReportUtil.getUUid());
					eachMsg.setRpt_seq(ReportUtil.getUniqueSeq());
					// submit td is support complete content in one message.
//					SetSignatureUtil.setSignature(eachMsg);
					filterDisturb(eachMsg, "SystemCheckQueue");
				}
			}
			makeSomeLog(type, eachMsg);
			
			
		
		}
	}
	
	private static void filterDisturbs(List<SmsMessage> msgList, String queueName) {
		if(msgList != null){
			int size = msgList.size();
			for(int i = 0; i < size; i ++){
				filterDisturb(msgList.get(i), queueName);
			}
		}
	}
	
	private static void filterDisturb(SmsMessage eachMsg, String queueName) {
		if(eachMsg == null){
			return ;
		}
		String[] times = null;
		boolean isDisturb = false;
		String user_id = eachMsg.getUser_id();
		UserBean userBean = UserInfoCache.getUser_info().get(user_id);
		
		if(userBean!=null){
			if(userBean.getParamMap()!=null){
				String is_avoid_disturb = (String)userBean.getParamMap().get("is_avoid_disturb");
				if(is_avoid_disturb!=null&&is_avoid_disturb.trim().equals("0")){
					String disturbPeridTime = (String)userBean.getParamMap().get("disturb_perid_time");
					if(disturbPeridTime!=null){
						if(StringUtils.isNotBlank(disturbPeridTime) && Pattern.matches("\\d{2}:\\d{2}\\-\\d{2}:\\d{2}", disturbPeridTime.trim())){
							times = disturbPeridTime.split("\\-");
							if(!canSendInDisturbPeridTime(times)){
								//不在允许下发的时间段内
								isDisturb = true;
							}
						}
					}
					
				}
				
				
			}
		}
			
		if(!isDisturb){
			String templateId = eachMsg.getTemplate_id();
			
			if(StringUtils.isNotBlank(templateId)){
				SmsTemplateInfo smsTemplateInfo = SmsTemplateCache.getSmsTemplate(eachMsg.getUser_id(), templateId);
				if(smsTemplateInfo != null){
					if(smsTemplateInfo.getIsAvoidDisturb() == 1){
						//防扰开启
						String disturbPeridTime = smsTemplateInfo.getDisturbPeridTime();
						if(StringUtils.isNotBlank(disturbPeridTime) && Pattern.matches("\\d{2}:\\d{2}\\-\\d{2}:\\d{2}", disturbPeridTime.trim())){
							times = disturbPeridTime.split("\\-");
							if(!canSendInDisturbPeridTime(times)){
								//不在允许下发的时间段内
								int disturb_deal_strategy = smsTemplateInfo.getDisturb_deal_strategy();
								if(disturb_deal_strategy==1){
									eachMsg.setStatus(2);
									eachMsg.setResponse(ErrCodeCache.getErrCode(ErrCode.codeName.disturbSmsReject).getResponse());
									eachMsg.setErr(ErrCodeCache.getErrCode(ErrCode.codeName.disturbSmsReject).getErr() + "");
									eachMsg.setFail_desc(ErrCodeCache.getErrCode(ErrCode.codeName.disturbSmsReject).getFail_desc());
									 DataCenterIOUtil.addIntoDifferentQueue(eachMsg);
									return;
								}else{
									isDisturb = true;
								}
								
							}
						}
					}
				}
			}
		}
		
		

		
		if(isDisturb){
			//属于打扰短信，准备入打扰表
			eachMsg.addExtraField("queue_name", queueName);
			eachMsg.addExtraField("disturb_start_time", times[0]);
			eachMsg.addExtraField("disturb_end_time", times[1]);
			DataCenter_old.addAvoidDisturbMessage(eachMsg);
			log.info("message【user_id=" + eachMsg.getUser_id() + ", service_code=" + eachMsg.getService_code() + ", mobile=" + eachMsg.getMobile() + "】 is distrubed, dest queue is " + queueName);
		}else{
			//不属于打扰短信，正常下发
			if("LongMessageInsertDBQueue".equals(queueName)){
				DataCenter_old.addInsertDBLongMessage(eachMsg);
			}else{
				DataCenter_old.addSystemCheckMessage(eachMsg);
			}
			
			if(log.isDebugEnabled()){
				log.debug("message【user_id=" + eachMsg.getUser_id() + ", service_code=" + eachMsg.getService_code() + ", mobile=" + eachMsg.getMobile() + "】 is not distrubed, dest queue is " + queueName);
			}
		}
	}

	private static boolean canSendInDisturbPeridTime(String[] times) {
		boolean acrossDay = false;
		if (times[0].compareTo(times[1]) > 0) {
			acrossDay = true;
		}

		String currentHourMin = DateUtil.currentHourMin();
		if (!acrossDay) {
			return times[0].compareTo(currentHourMin) <= 0 && times[1].compareTo(currentHourMin) >= 0;
		} else {
			boolean result = times[1].compareTo(currentHourMin) >= 0 || times[0].compareTo(currentHourMin) <= 0;
			//存入数据库中的小时数 +24
			times[1] = DateUtil.addHour(times[1], 24);
			return result;
		}
	}

	public static void InitAndHandOutSmsForTest(SmsMessage eachMsg) {
		if(eachMsg != null){
			String type = "";
			//log.info("eachMsg:　"+eachMsg);
			beforeHandleForTest(eachMsg);
			int pktotal = eachMsg.getPktotal();
			int charge_count = eachMsg.getCharge_count();

			if(charge_count == 1 && (pktotal == 0||pktotal == 1)){
				//标记短信的唯一值
				eachMsg.setRpt_seq(ReportUtil.getUniqueSeq());
				//普通短短信-----add into sys-check queue if not full;
				type = "普通短短信";
				DataCenter_old.addSystemCheckMessage(eachMsg);
				//What to do if queue is full?
			}else if (charge_count == 1 && pktotal > 1 ){
				//标记短信的唯一值
				eachMsg.setRpt_seq(ReportUtil.getUniqueSeq());
				//分条长短信-----
				type = "分条长短信";
				//Each_message add into long_message_in_DB queue if not full;
				DataCenter_old.addInsertDBLongMessage(eachMsg);
				//What to do if queue is full?
			}else if (charge_count > 1 && pktotal < 2){
				//整条长短信----- splitList add into sys-check queue if not full;
				type = "整条长短信";
				TdInfo td = TdInfoCache.getTd_info().get(String.valueOf(eachMsg.getTd_code()));
//				System.out.println("cache data:td_info = " + td + " - msg_yw_code = " + eachMsg.getTd_code() + " - td =  " + td);
				//log.info("TdInfo:　"+td);
				if(td != null && td.getSubmit_type() == 1){//拆分提交
					//submit td is not support complete content in one message.
					List<SmsMessage> splitList = LongMessageHandler.splitLongMessageByRules(eachMsg);
					if(charge_count != splitList.size()){
						   StringBuffer sb = new StringBuffer();
	               		    sb.append("拆分结果与 charge_count 不符[charge_count = " + charge_count + "][split_count = " + splitList.size() + "]").append("\n");
	               		    int count = 1;
	               		    for(SmsMessage sms:splitList){
	               		    	sms.setFail_desc("拆分结果与计费条数不符");
	               		    	sms.setResponse(2);
	               		    	sb.append(count+". "+sms).append("\n");
	               				count++;
	               			}
	               		    log.error(sb.toString());
	                       DataCenter_old.addRejectMessage(splitList);
					}else{
						DataCenter_old.addSystemCheckMessage(splitList);
					}
					//what to do if queue is full?
				}else{
					//标记短信的唯一值
					eachMsg.setRpt_seq(ReportUtil.getUniqueSeq());
					//submit td is support complete content in one message.
					DataCenter_old.addSystemCheckMessage(eachMsg);
				}
			}
			makeSomeLog(type, eachMsg);
		}
	}
	private static void makeSomeLog(String type, SmsMessage eachMsg) {
		log.info("message-type: " + type + "; td_code: "+eachMsg.getTd_code()+"; MsgID: " + eachMsg.getMsg_id()+"; mobile: "+HSToolCode.filterMobile(eachMsg.getMobile())+"; user_id: "+eachMsg.getUser_id()+"; Pktotal: "+eachMsg.getPktotal()+"; Pknumber: "+eachMsg.getPknumber()+"; ChargeCount: "+eachMsg.getCharge_count());
	}
	
	public static void addRejectMessage(SmsMessage sms){
		DataCenter_old.addRejectMessage(sms);
	}
	public static void addRejectMessage(List<SmsMessage> sms){
		DataCenter_old.addRejectMessage(sms);
	}
	public static void addSubmitHistoryQueue(SmsMessage sms) {
		DataCenter_old.addSubmitHistoryQueue(sms);
	}
	public static boolean isWriteBiFile() {
		boolean result = false;
			String s = GateConfigCache.getValue("bi.file_switch");
			if(s != null && (s.equals("0") || s.equals("open"))){
				result = true;
			}
		return result;
	}
	
	public static void addIntoDifferentQueue(SmsMessage sms) {
		int status = sms.getStatus();
		sms.setMsg_deal_time(DateUtil.currentTimeToMs());
		log. debug("after system check sms = " + sms);
		switch (status){
		case 0:flashSmsHandle(sms); DataCenter_old.addManualCheckMessage(sms); break;
		case 1: flashSmsHandle(sms); 
			if(!DataCenter_old.addSubmitMessage(sms)){
				log.warn("fail_into_SubmitMessage:　"+sms);
		     }
			break; 
		case 2:	DataCenterIOUtil.addRejectMessage(sms); break;
		default:
			DataCenterIOUtil.addRejectMessage(sms); break;
		}
		
	}
	

	//闪信编码转换
	public static void flashSmsHandle(SmsMessage sms){
		try{
		String user_id = sms.getUser_id();
		String td_code = sms.getTd_code();
		UserBean userInfo = UserInfoCache.getUser_info().get(user_id);
		TdInfo tdInfo = TdInfoCache.getTd_info(td_code);
		if(tdInfo!=null&&userInfo!=null){
			String is_flash_sms = "1";
			if(userInfo.getParamMap()!=null){
				is_flash_sms = (String) userInfo.getParamMap().get("is_flash_sms");
				if(is_flash_sms==null||is_flash_sms.trim().equals("")){
					is_flash_sms="1";
				}
			}
			int is_support_flash = tdInfo.getIs_support_flash();
			if(is_flash_sms.equals("0")&&is_support_flash==0){
				if(sms.getMsg_format()==0||sms.getMsg_format()==0x10){
					sms.setMsg_format(0x10);
				}else {
					sms.setMsg_format(0x18);
				}
			}
		}
	
		}catch(Exception e){
			log.error("flashSmsHandle Exception",e);
		}
	}
	
	public static boolean dealTimingMessage(SmsMessage eachMsg) {
		boolean isTiming = false;
		String timing_time = (String)eachMsg.getExtraField("timing_time");
		if(StringUtils.isNotBlank(timing_time)){
			try{
				long timing_ms = DateUtil.dateFormatToMs(timing_time);
				long current_ms = System.currentTimeMillis();
				if(current_ms<timing_ms){
					isTiming = true;
				}
			}catch(Exception e){
				
			}
			
		}
		
		if(isTiming){
			SendMessageTiming smt = new SendMessageTiming();
			smt.setBatch_number((String)eachMsg.getExtraField("batch_number"));
			smt.setUser_id(eachMsg.getUser_id());
			smt.setIs_interface_send(0);
			smt.setMobile(eachMsg.getMobile());
			smt.setMsg_content(eachMsg.getMsg_content());
			smt.setMsg_id(eachMsg.getMsg_id());
			smt.setSend_time(timing_time);
			smt.setService_code(eachMsg.getService_code());
			smt.setRece_time(eachMsg.getMsg_receive_time());
			smt.setSms_message(eachMsg);
			DataCenter_old.addFixedTimeMessage(smt);
		}
		// TODO Auto-generated method stub
		return isTiming;
	}
	
	public static boolean dealTimingMessage(CheckMessage eachMsg) {
		boolean isTiming = false;
		String timing_time = eachMsg.getTiming_time();
		if(StringUtils.isNotBlank(timing_time)){
			try{
				long timing_ms = DateUtil.dateFormatToMs(timing_time);
				long current_ms = System.currentTimeMillis();
				if(current_ms<timing_ms){
					isTiming = true;
				}
			}catch(Exception e){
				
			}
			
		}
		
		return isTiming;
	}

//	public static void putToFixedQueue(CheckMessage eachMsg) {
//		SendMessageTiming smt = new SendMessageTiming();
//		smt.setBatch_number(eachMsg.getBatch_number());
//		smt.setUser_id(eachMsg.getUser_id());
//		smt.setIs_interface_send(eachMsg.getIs_interface_send());
//		smt.setMobile(eachMsg.getMobile());
//		smt.setMsg_content(eachMsg.getMsg_content());
//		smt.setMsg_id(eachMsg.getMsg_id());
//		smt.setSend_time(eachMsg.getTiming_time());
//		smt.setService_code(eachMsg.getService_code());
//		SmsMessage sms = eachMsg.getSms_message();
//		if(sms!=null){
//			smt.setRece_time(sms.getMsg_receive_time());
//		}else{
//			smt.setRece_time(eachMsg.getInsert_time());
//		}
//		
//		smt.setSms_message(eachMsg.getSms_message());
//		DataCenter_old.addFixedTimeMessage(smt);
//	}
}
