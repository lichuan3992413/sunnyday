package sunnyday.controller.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.cache.ErrCodeCache;
import sunnyday.controller.cache.MobileCache;
@Component
public class ImpDoCheck_Black implements IDoCheck{
	private List<Set<String>> tmpList = new ArrayList<Set<String>>();
	private List<Set<String>> list = new ArrayList<Set<String>>(); 
	
	public int doCheck(SmsMessage sms) {
		int result = 1;
		String user_id = sms.getUser_id();
		String template_id = sms.getTemplate_id();
		String service_code = sms.getService_code() ;
		String mobile = sms.getMobile();
		if(mobile.startsWith("86")){
			mobile = mobile.substring(2);
		}
		
		String scopeResult = findNumScope(mobile,user_id); 
		sms.setStatus(8);
		if(scopeResult != null){
			 //号段拦截
			 result = 0;
			 sms.setStatus(2);
			 sms.setResponse(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getResponse());
			 sms.setErr(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getErr() + "");
			 sms.setFail_desc(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getFail_desc().replace("$", scopeResult));
		}else if(findBlack(mobile,user_id)){
			//号码拦截
			result = 0;
			sms.setStatus(2);
			sms.setResponse(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getResponse());
			sms.setErr(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getErr() + "");
			sms.setFail_desc(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getFail_desc().replace("$", mobile));
		
		}else if(findTemplateBlack(template_id,mobile)){
			//模板黑名单拦截
			result = 0;
			sms.setStatus(2);
			sms.setResponse(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getResponse());
			sms.setErr(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getErr() + "");
			sms.setFail_desc(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getFail_desc().replace("$",template_id+"|"+ mobile));

		}else if(findServiceBlack(service_code,mobile)){
            //业务黑名单拦截
			result = 0;
			sms.setStatus(2);
			sms.setResponse(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getResponse());
			sms.setErr(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getErr() + "");
			sms.setFail_desc(ErrCodeCache.getErrCode(ErrCode.codeName.blackMobileReject).getFail_desc().replace("$",service_code+"|"+ mobile));
		}else{
			//通过
			result = 1;
			sms.setStatus(1);
		}
		
		
		if(result == 0){
			if(isWhite(user_id, mobile)){
				result = 1;
				sms.setStatus(1);
				sms.setResponse(0);
				sms.setFail_desc("白名单放过");
			}
		}
		
		return result;
	}
	
	private boolean findBlack(String mobile,String user_id) {
		boolean result = false;
		
		if(mobile != null){
			tmpList.add(MobileCache.getUser_black_mobile().get(user_id));
			tmpList.add(MobileCache.getGloble_black_mobile().get("globle"));
			for(Set<String> eachSet : tmpList){
				if(eachSet != null && eachSet.size() > 0){
					result = eachSet.contains(mobile);	
				} 
				if(result){
					break;
				}
			}
			tmpList.clear();
		}
		return result;
	}
	
	private boolean findTemplateBlack(String template_id,String mobile) {
		boolean result = false;
		if(mobile != null){
			list.add(MobileCache.getTemplateBlackMobiles().get(template_id));
			for(Set<String> eachSet : list){
				if(eachSet != null && eachSet.size() > 0){
					result = eachSet.contains(mobile);	
				} 
				if(result){
					break;
				}
			}
			list.clear();
		}
		return result;
	}

	private boolean findServiceBlack(String service_code,String mobile) {
		boolean result = false;
		if(mobile != null){
			Set<String> sets = MobileCache.getServiceBlackMobiles().get(service_code);
			if(sets!=null&&!sets.isEmpty()){
				if(sets != null && sets.size() > 0){
					result = sets.contains(mobile);
				}
			}
		}
		return result;
	}

	/**
	 * 依次校验账户级号段，通道级号段，全局号段。
	 * 返回造成驳回的号段。
	 */
	private String findNumScope(String mobile,String user_id) {
		String result = null;
		
		if(mobile != null){
			tmpList.add(MobileCache.getUser_black_scope().get(user_id));
			tmpList.add(MobileCache.getGloble_black_scope().get("globle"));
			
			for(Set<String> eachSet : tmpList){
				if(eachSet != null && eachSet.size() > 0){
					result = checkNumbScope(mobile, eachSet); 
				} 
				if(result != null){
					break;
				}
			}
			tmpList.clear();
		}
		return result;
	}

	private String checkNumbScope(String mobile, Set<String> eachSet) {
		String result = null;
		if(eachSet != null && eachSet.size() > 0){
			 for(String eachScope : eachSet){
				 if(eachScope != null && eachScope.length() < 11 && mobile.startsWith(eachScope)){
					 result = eachScope;
					 break;
				 }
			 }
		 }
		return result;
	}

	private boolean isWhite(String customer_id, String mobile) {
		boolean result = false;
		
		if(MobileCache.getWhite_mobiles() != null){
			Set<String> whiteList = MobileCache.getWhite_mobiles().get(customer_id);
			if(whiteList != null && whiteList.contains(mobile)){
				result = true;
			}
		}
		return result;
	}

}
