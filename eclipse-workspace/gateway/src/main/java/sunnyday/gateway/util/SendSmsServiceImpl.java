package sunnyday.gateway.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunmyday.gateway.service.impl.BalanceVerifier;
import sunnyday.common.model.SubmitBean;
import sunnyday.common.model.UserBalanceInfo;
import sunnyday.common.model.UserBean;
import sunnyday.gateway.dc.DataCenter;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.DateUtil;
/**
 * 短信下发
 * @author 1307365
 *
 */
@Service
public class SendSmsServiceImpl  implements ISmsService{
	private static Logger log = CommonLogFactory.getCommonLog(SubmitServiceImpl.class);
	public HSResponse doSomething(CommonParameter param) {
		String corp_id = param.getCorp_id();
		String corp_pwd = param.getCorp_pwd();
		String corp_service = param.getCorp_service();
		String mobiles = param.getMobiles();
		String msg_content = param.getMsg_content();
		String msg_id = param.getMsg_id();
		String ext = param.getExt();//用户提交时的扩展
		String requestIP = param.getRequest_ip();//访问方式,ip
		String type = param.getType();
		int length = 1000 ;
		 
		//String method = param.getMethod();
		//记录客户提交参数及时间，便于数据核对与耗时分析
		//LogUtil.getReceiver_log().debug("user's Parameters: corp_id:"+corp_id+";corp_pwd:"+corp_pwd+";corp_service:"+corp_service+";mobile:"+mobiles+";msg_content:"+msg_content+";corp_msg_id:"+msg_id+";ext:"+ext+";user_ip:"+requestIP+";mothod:"+method);
		
		if(corp_id == null||"".equals(corp_id.trim())){
			LogUtil.writeLogs(corp_id,mobiles,requestIP, ErrorCodeUtil.common_user_id_error, "参数:corp_id未填写");
			return new HSResponse(ErrorCodeUtil.common_user_id_error,"账号参数填写不合法");
		}
		
		
		
		//校验手机号个数及非空
        if(mobiles == null||"".equals(mobiles.trim())){
        	LogUtil.writeLogs(corp_id,mobiles,requestIP, ErrorCodeUtil.common_mobile_info_error, "参数:mobile未填写");
        	return new HSResponse(ErrorCodeUtil.common_mobile_info_error,"手机号参数填写不合法");
        }
        
      // 校验内容长度
		if (msg_content == null || msg_content.length() == 0|| msg_content.length() > length) {
			if (msg_content == null) {
				LogUtil.writeLogs(corp_id,mobiles,requestIP,ErrorCodeUtil.common_msg_content_error,"参数:msg_content未填写");
			} else {
				LogUtil.writeLogs(corp_id,mobiles,requestIP,ErrorCodeUtil.common_msg_content_error,"短信内容长度不合法,msg_content_length:" + msg_content.length());
			}
			return new HSResponse(ErrorCodeUtil.common_msg_content_error, "短信内容参数填写不合法");
		}
        
		
     // 判断用户是否存在
     	UserBean userInfo = DataCenter.getUserBeanMap().get(corp_id.trim());
    	String checkUserResult = SmsServiceFactory.checkUserStatus(userInfo, requestIP, corp_id);
		
    	if(!checkUserResult.equals("")){
			if(ErrorCodeUtil.common_user_id_error.equals(checkUserResult)){
				LogUtil.writeLogs(corp_id,mobiles,requestIP, checkUserResult, "用户["+corp_id+"]不存在或者已经关闭");
			}else {
				LogUtil.writeLogs(corp_id,mobiles,requestIP, checkUserResult, "用户["+corp_id+"]已经关闭");
			}
			return new HSResponse(checkUserResult,"用户不存在或者已经关闭");
		}
     		
        //红树接口需要验证客户的登陆密码
        if(ParamUtil.HTTP_INTERFACE_SMSSEND_HS.equals(type)){
        	if(corp_service == null||"".equals(corp_service.trim())){
    			LogUtil.writeLogs(corp_id,mobiles,requestIP, ErrorCodeUtil.common_corpServiceError_or_statusClose, "参数:corp_service未填写");
    			return new HSResponse(ErrorCodeUtil.common_corpServiceError_or_statusClose,"业务参数填写不合法");
    		}
        	//校验账户密码信息
    		if(corp_pwd == null || !userInfo.getUser_pwd().equals(corp_pwd.trim())){
    			 LogUtil.writeLogs(corp_id,mobiles,requestIP, ErrorCodeUtil.common_user_mima_error, "密码["+corp_pwd+"]填写错误，正确密码为["+userInfo.getUser_pwd()+"]");
    			 return new HSResponse(ErrorCodeUtil.common_user_mima_error,"密码填写错误");
    		}
        }
        
        //百悟接口需要验证客户的MD5code
        if(ParamUtil.HTTP_INTERFACE_SMSSEND_BW.equals(type)){
        	//校验账户密码信息
        	corp_service = Tools.getCustomerYwForm(userInfo.getServiceMap(),corp_service.trim(),userInfo);
    		if (corp_service == null ||"".equals(corp_service) ) {
    			LogUtil.writeLogs(corp_id,mobiles,requestIP, ErrorCodeUtil.common_corpServiceError_or_statusClose, "客户业务验证失败,客户业务列表:"+userInfo.getServiceMap()+",MD5_CODE:"+corp_service);
    			return new HSResponse(ErrorCodeUtil.common_corpServiceError_or_statusClose,"客户业务验证失败");
    		}
        }
         
		
		// 校验用户访问ip
		if (!SmsServiceFactory.checkUserIP(userInfo.getUser_ip(), requestIP, corp_id)) {
			LogUtil.writeLogs(corp_id,mobiles,requestIP, ErrorCodeUtil.common_user_ip_error, "非法IP访问,所允许的IP为["+userInfo.getUser_ip()+"]");
			return new HSResponse(ErrorCodeUtil.common_user_ip_error,"非法IP访问");
		}
			 
		
		//msg_id校验
		if(msg_id == null||msg_id.trim().length() == 0||msg_id.trim().length() > 50){
			msg_id = DateUtil.getMsgID();
		}
		 
		
		if (ext == null) {
			ext="";
		}else {
			try {
				int tmp1 = Integer.parseInt(ext);
				ext = String.valueOf(tmp1);
			} catch (Exception e) {
				ext="";
			}
		}
		
		
      
     	 
        List<String> destMobiles = new ArrayList<String>(Arrays.asList(mobiles.split(",")));
        if(destMobiles.size() > ParamUtil.MOBILE_COUNT_LIMIT){
        	LogUtil.writeLogs(corp_id,mobiles,requestIP, ErrorCodeUtil.common_mobile_info_error, "提交的手机号码数量["+destMobiles.size() +"]大于"+ParamUtil.MOBILE_COUNT_LIMIT+"个");
        	return new HSResponse(ErrorCodeUtil.common_mobile_info_error,"");
        }
        
        
		List<SubmitBean> smsList = new ArrayList<SubmitBean>();
        
        Iterator<String> it =  destMobiles.iterator();
        boolean qunfa = false;
		if(destMobiles.size()>1){
			qunfa = true;
		}
        while(it.hasNext()){
        	String mobile_each = it.next();
        	String[] mobiles_temp = { mobile_each };

			SubmitBean resultBean = new SubmitBean();
			resultBean.setUserSn(userInfo.getSn());
			resultBean.setMobiles(mobiles_temp);
			resultBean.setSp_number(corp_service+ext);
			resultBean.setMsg_id(msg_id);
			resultBean.setMsg_format(8);
			resultBean.setUser_id(userInfo.getUser_id());
//			resultBean.setSubmit_type("SGIP");
			resultBean.setContent(msg_content);

			String ValidResult = null;
			try {
				String is_multi_sign = "1";
				if(userInfo.getParamMap()!=null){
					is_multi_sign =  (String) userInfo.getParamMap().get("is_multi_sign");
					if(is_multi_sign==null||is_multi_sign.trim().equals("")){
						is_multi_sign = "1";
					}
				}
			
				int result = 0;
			if(is_multi_sign.equals("0")){
				
				result = BalanceVerifier.Instance().valid_multi(resultBean);
			}else{
				result = BalanceVerifier.Instance().valid(resultBean);
			}
				ValidResult = SmsServiceFactory.getValidResult(resultBean.getResp(), requestIP, corp_id, corp_service,mobile_each,ext,result);
			} catch (Exception e) {
				ValidResult =ErrorCodeUtil.common_mobile_info_error;
			}
			
		    
			if(ValidResult.equals("")){
				try {
					DataCenter.addSubmitListDone(resultBean);
				} catch (Exception e) {
					log.error("",e);
				}
				smsList.add(resultBean);
			}else{
				if(!qunfa){
					if(ErrorCodeUtil.common_mobile_info_error.equals(ValidResult)){
						LogUtil.writeLogs(corp_id,mobiles,requestIP, ValidResult, "客户手机号码["+mobile_each+"]提交不合法");
						
					}else if(ErrorCodeUtil.common_msg_content_error.equals(ValidResult)){
						LogUtil.writeLogs(corp_id,mobiles,requestIP, ValidResult, "短信内容超过1000字或为空,提交的短信内容为："+msg_content);
						
					}else if(ErrorCodeUtil.common_corpServiceError_or_statusClose.equals(ValidResult)){
						LogUtil.writeLogs(corp_id,mobiles,requestIP, ValidResult, "客户业务匹配失败,客户提交参数:corp_service[" + corp_service + "] ,ext["+ext+"];客户所有业务信息为:"+userInfo.getServiceMapString());
						
					}else  if(ErrorCodeUtil.common_no_before_sign.equals(ValidResult)){
						LogUtil.writeLogs(corp_id,mobiles,requestIP,ValidResult,"内容开头无签名["+msg_content+"]");
					}else  if(ErrorCodeUtil.common_no_after_sign.equals(ValidResult)){
						LogUtil.writeLogs(corp_id,mobiles,requestIP,ValidResult,"内容结尾无签名["+msg_content+"]");
					}else  if(ErrorCodeUtil.common_sign_no_record.equals(ValidResult)){
						LogUtil.writeLogs(corp_id,mobiles,requestIP,ValidResult,"签名没有报备["+msg_content+"]");
					}else if(ErrorCodeUtil.common_balance_not_enough.equals(ValidResult)){
						 Map<String, UserBalanceInfo> map =DataCenter.getUserBalanceMap();
						 UserBalanceInfo userBalanceInfo = map.get(corp_id);
						 if(userBalanceInfo!=null){
							 LogUtil.writeLogs(corp_id,mobiles,requestIP, ValidResult, "客户余额["+userBalanceInfo.getBalance()+"]不足");
						 }else {
							 LogUtil.writeLogs(corp_id,mobiles,requestIP, ValidResult, "客户余额不足");
						}
					}
					
					return new HSResponse(ValidResult,"");
				}else {
					//log.warn("msg_id:"+msg_id+" ;ValidResult:" + ValidResult+"; user_id:"+corp_id+"; mobile_each: "+mobile_each+"; ErrorCode:"+ErrorCodeUtil.common_mobile_info_error);
					continue;
				}
			}
        }

		if (smsList.size() <= 0) {
			LogUtil.writeLogs(corp_id,mobiles,requestIP, ErrorCodeUtil.common_mobile_info_error, "有效手机号码个数为0");
			return new HSResponse(ErrorCodeUtil.common_mobile_info_error,"");
		}
		 
		return new HSResponse("0#"+smsList.size(),"成功发送["+smsList.size()+"]条信息");
	}

	
	
	
	 
	
}
