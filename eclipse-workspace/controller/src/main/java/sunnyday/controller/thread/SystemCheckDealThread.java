package sunnyday.controller.thread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.hskj.utils.DataCenterIOUtil;

import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.UserBean;
import sunnyday.common.model.UserCheckType;
import sunnyday.controller.cache.GateConfigCache;
import sunnyday.controller.cache.TdInfoCache;
import sunnyday.controller.cache.UserCheckTypeCache;
import sunnyday.controller.cache.UserInfoCache;
import sunnyday.controller.cache.WhiteContentCache;
import sunnyday.controller.check.IDoCheck;
import sunnyday.controller.check.SmsFilter;
import sunnyday.controller.check.ValidationFactory;
import sunnyday.controller.filter.EhcacheRepeatMsgFilter;
import sunnyday.controller.util.Md5Util;
import sunnyday.controller.util.ReportUtil;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 审核过滤处理
 */
@Service
public class SystemCheckDealThread extends Thread{
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	private boolean isRunnable = false;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	public SystemCheckDealThread(){
		EhcacheRepeatMsgFilter.doStart();
	/*	commonFilterMode = new ArrayList<Integer>();
		commonFilterMode.add(ValidationFactory.SUBMIT_REPEAT);
		commonFilterMode.add(ValidationFactory.SP_NUMBER_FILTER);*/
		this.isRunnable = true;
	}
	
	public void run(){
		while(isRunnable){
			try{
				SmsMessage sms = DataCenter_old.getSystemCheckMessage();
				if(sms != null){
					//处理rpt_seq 的值
					if(sms.getRpt_seq()==0l){
						sms.setRpt_seq(ReportUtil.getUniqueSeq());
					}
					checkMsgByUserCheckType(sms);
					String batch_number = (String)sms.getExtraField("batch_number");
					if(StringUtils.isBlank(batch_number)){
						sms.addExtraField("batch_number", sms.getMsg_id());
					}
					if(sms.getStatus()==2){
						DataCenterIOUtil.addIntoDifferentQueue(sms);
					}else{
						boolean isNeedManualCheck = dealManualCheck(sms);
						if(!isNeedManualCheck){
							boolean isNeedFixedTime = DataCenterIOUtil.dealTimingMessage(sms);
							if(!isNeedFixedTime){
								sms.setStatus(1);
								DataCenterIOUtil.addIntoDifferentQueue(sms);
							}
						}
					}
					
					
				}else{
					Thread.sleep(10);
				}
			}catch(Exception e){
				log.error("", e);
				try {
					Thread.sleep(1000);
				} catch (Exception e1) {
					log.error("", e1);
				}
			}
		}
	}



	private boolean dealManualCheck(SmsMessage sms) {
		boolean result = false;
		String is_need_check = (String)sms.getExtraField("is_check");
		if(StringUtils.isNotBlank(is_need_check)){
			if(is_need_check.trim().equals("0")){
				String user_id = sms.getUser_id();
				UserBean userBean = UserInfoCache.getUser_info(user_id);
				if(userBean!=null){
					if(userBean.getParamMap()!=null){
						String check_user = (String)userBean.getParamMap().get("check_user");
						if(StringUtils.isNotBlank(check_user)){
							String[] check_user_array = check_user.trim().split(",");
							if(check_user_array.length>0){
									sms.setCheck_user(check_user_array[0]);
									sms.addExtraField("is_interface_send", 0);
									result = true;
									DataCenter_old.addManualCheckMessage(sms);
								}
							
							}
						}
					
				}
			
				
			}
		}
		return result;
	}

	public boolean doStop() {
		this.isRunnable = false;
		this.interrupt();
		return true;
	}
	


	
	
	/**
	 * 根据账户设置来选择审核模块进行过滤,过滤的结果直接set到传入参数中去，包括status，response等
	 * @param sms
	 */
	private void checkMsgByUserCheckType(SmsMessage sms) {
		try {
			String SubmitCode = sms.getFullUserSubmitCode();
			String user_id = sms.getUser_id();
			String md5_index = Md5Util.Md5_32(user_id+SubmitCode + sms.getComplete_content());//源yw_code加密生成md5
			sms.setMd5_index(md5_index);
			//log.info("befor-SystemCheckDeal: "+sms);
			if(TdInfoCache.getTd_info().containsKey(sms.getTd_code())){
				int filter_flag = TdInfoCache.getTd_info().get(sms.getTd_code()).getFilter_flag();
				sms.setFilter_flag(filter_flag);
			}
			
			//处理接口
			List<Integer> handleModes =  this.getUserCheckMode(sms);
			List<Integer> tmp =  new ArrayList<Integer>();
			tmp.add(ValidationFactory.SP_NUMBER_FILTER);
			tmp.add(ValidationFactory.SUBMIT_REPEAT);
			if(handleModes!=null&&handleModes.size()>0){
				tmp.addAll(handleModes);
			}
			combineHandle(sms, tmp);
		} catch (Exception e) {
			sms.setStatus(2);//如果有异常，将这条状态置为初始
			sms.setResponse(2);
			sms.setFail_desc(e.toString());
			log.error("SystemCheckDeal", e);
		}
	}
	
	private void combineHandle(SmsMessage sms, List<Integer> handleModes) {
		if (log.isDebugEnabled()) {
		log.debug("combineHandle user_id: "+sms.getUser_id()+", "+handleModes);}
		
		if(sms.getStatus() == 2){
			//已经被之前的审核策略拦截，暂时是因为客户重复发送下发导致的拦截
		}else{
			sms.setStatus(1);
			initCount(sms);
			if(handleModes != null){
				for(int mode : handleModes){
					IDoCheck check = ValidationFactory.getValidataMethod(mode);
					if(check != null){
						int result = check.doCheck(sms);
						if (log.isDebugEnabled()) {
						    log.debug("hskj-mode:"+mode+", user_id: "+sms.getUser_id()+", "+check.getClass() + ", mobile: "+sms.getMobile()+",result: " + result);
						}
						if(result == 0){
							//被拦截，跳出
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * 初始化count值  其他地方就不进行非空校验
	 * @param sms
	 */
	private void initCount(SmsMessage sms) {
		if(sms.getExtraField("mass_id") == null){
			sms.addExtraField("mass_id", 1);
		}
	}

	private List<Integer> getUserCheckMode(SmsMessage each) {
		List<Integer> result = null;
		String user_id = each.getUser_id();
		String submitCode = each.getSubserSubmitCode();
		List<UserCheckType> userList = UserCheckTypeCache.getUser_check_mode().get(user_id);
		UserCheckType tmpType = null;
		if(userList != null && userList.size() > 0){
			tmpType = userList.get(0);
			/*for(UserCheckType obj: userList){
				String shortSpNum = obj.getService_code();
				if(shortSpNum != null && !shortSpNum.equals("") && submitCode.startsWith(shortSpNum)){
					if(submitCode.equals(shortSpNum)){
						tmpType=obj;
						break ;
					}
					tmpType = (tmpType == null ? obj: (shortSpNum.length() > ((String)tmpType.getService_code()).length()? obj: tmpType));
				}
			}*/
		}
		if (log.isDebugEnabled()) {
		log.debug("1.user-checkType[user_id="+each.getUser_id()+"]: "+submitCode+" list: "+userList+" type: "+tmpType);
		}
		if(tmpType != null){
			result = CheckUserFastPatternAndGetCheckMode(each, tmpType);
		}else{
			/**
			 * 客户提交时，若不选择审核模式，默认给赋的审核模式
			 * -1:子账户全部拦截，0:通道白名单，1:黑名单，2：基础关键词，3：信审缓存 4：人工审核关键词，5：全审，6：群发监控，7:号段拦截
			 */
		/*	log.warn("user-checkType[user_id="+each.getUser_id()+"]: 默认1,2,3,4; submitCode:"+ submitCode+"; userList:　"+userList);
			result = new ArrayList<Integer>(4);
			result.add(1);
			result.add(2);
			result.add(3);
			result.add(4);*/
		}
		each.addExtraField("checkSpNumber", each.getFullUserSubmitCode());
		
		if(isManualCheckTime() && isManualCheckLevel(each.getUser_id(), result)){
			result = defaultHandleModes(result);
		}
		
		UserBean user= UserInfoCache.getUser_info(user_id);
		if(user!=null){
			//判断客户是否走，白名单模板拦截功能
			String is_white_content ="";
			try {
				//0：是 1：否
				is_white_content = (String)user.getParamMap().get("is_white_content");
			} catch (Exception e) {
			}
			if(is_white_content!=null&&"0".equals(is_white_content)){
				//客户走白名单模板短信拦截
				if(result==null){
					result = new ArrayList<Integer>();
				} 
				result.add(ValidationFactory.WHITE_TEMPLATE_FILTER);
			}
			
		}
		
		if (log.isDebugEnabled()) {
		log.debug("2.user-checkType[user_id="+each.getUser_id()+"]: "+submitCode+" result: "+result);
		}
		return result;
	}
	
	private List<Integer> CheckUserFastPatternAndGetCheckMode(SmsMessage each, UserCheckType tmpType) {
		List<Integer> result = null;
		//比较短信信息和快捷模板，决定实际使用的checkMode
		String completeContent = each.getComplete_content();
		Set<Pattern[]> whiteSet = WhiteContentCache.getUser_white_list().get(tmpType.getUser_id()); 
		if(whiteSet != null && whiteSet.size() > 0){
			String hittedWords = SmsFilter.filterContentBySplitRgex(completeContent, whiteSet);
			if(hittedWords != null){
				result = tmpType.getFastCheckModes();
			}else{
				result = tmpType.getNormalCheckModes();
			}
		}else{
			result = tmpType.getNormalCheckModes();
		}
		result = tmpType.orderMode(result, GateConfigCache.getValue("mode_order"));
		return result;
	}
	
	private boolean isHQCustomer(String user_id) {
		boolean result = false;
		if(UserInfoCache.getUser_info().get(user_id) != null){
			result = UserInfoCache.getUser_info().get(user_id).getIs_direct_user() == 0;
		}
		return result;
	}
	
	private boolean isManualCheckLevel(String user_id, List<Integer> handleModes) {
		boolean result = false;
		if(!isHQCustomer(user_id) && handleModes != null && handleModes.size() > 0){
			result = handleModes.contains(3) || handleModes.contains(4) || handleModes.contains(5);
		}
		return result;
	}
	
	private boolean isManualCheckTime() {
		boolean result = false;
		String curTime = sdf.format(System.currentTimeMillis());	
		String startTime = GateConfigCache.getValue("manual_check_start_time");
		String endTime = GateConfigCache.getValue("manual_check_end_time");
		if (startTime != null && endTime != null) {
			boolean isAfter = curTime.compareTo(startTime) > 0;
			boolean isBefore = curTime.compareTo(endTime) < 0;
			if (isAfter || isBefore) {
				result = true;
			}
		}

		
		return result;
	}

	private List<Integer> defaultHandleModes(List<Integer> handleModes) {
		List<Integer> result = new ArrayList<Integer>();
		if(handleModes != null && handleModes.size() > 0){
			if(handleModes.contains(0)){
				result.add(0);
			}
		}
		result.add(1);
		result.add(2);
		result.add(3);
		result.add(5);
		
		return result;
	}
}
