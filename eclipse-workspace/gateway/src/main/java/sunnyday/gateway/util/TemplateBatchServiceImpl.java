package sunnyday.gateway.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunmyday.gateway.service.impl.BalanceVerifier;
import sunnyday.common.model.ServiceInfo;
import sunnyday.common.model.SmsResponse;
import sunnyday.common.model.SmsTemplateInfo;
import sunnyday.common.model.SmsTemplateParam;
import sunnyday.common.model.SubmitBean;
import sunnyday.common.model.UserBalanceInfo;
import sunnyday.common.model.UserBean;
import sunnyday.gateway.cache.ServiceInfoCache;
import sunnyday.gateway.cache.SmsTemplateCache;
import sunnyday.gateway.dc.DataCenter;
import sunnyday.tools.util.CommonLogFactory;
/**
 * 模板短信
 * @author weijun
 *
 */
@Service
public class TemplateBatchServiceImpl implements ISmsService{
	private static Logger log = CommonLogFactory.getCommonLog(TemplateBatchServiceImpl.class);
	public HSResponse doSomething(CommonParameter common_param) {
		
		SendSmsBatchRequest request = (SendSmsBatchRequest) common_param.getOther().get("SendSmsBatchRequest");
		String corp_id = request.getUserId();
		String requestIP = common_param.getRequest_ip();//访问方式,ip
		String templateId =request.getTemplateId();
		/**
		 * 默认使用模板关系中的业务代码，若模板关系中没有配置该业务代码，则使用客户传值的业务代码
		 */
		String corp_service = SmsTemplateCache.getTemplateServices(templateId);
		if(corp_service==null){
			corp_service = request.getServiceCode();
		}
	 
		String ext = request.getExt();
		String tmp_ext = "";
		
		List<BatchParam> params= request.getParam();
		
		if(params==null||params.size()==0){
			HSResponse hs_response = new HSResponse();
			hs_response.setCode(ErrorCodeUtil.ERROR_PARAMS);
			hs_response.setText("批量请求参数错误: 参数内容为空");
			return hs_response;
		}
		List<SmsResponse> result = new ArrayList<SmsResponse>();
		List<SubmitBean> smsList  = new ArrayList<SubmitBean>();
		
		SendSmsRequest tmp_request = null;
		SubmitBean bean = null;
		
		boolean service_auto_ext = true ;
		ServiceInfo info = ServiceInfoCache.getServiceInfo(request.getServiceCode());
		if(info!=null){
			if(info.getIs_template_ext()==1){
				service_auto_ext = false ;
			}
		}else {
			//不存在该业务
		}
		boolean auto_content = true;
		String template_ext = "";
        SmsTemplateInfo tmp_info = SmsTemplateCache.getSmsTemplate(request.getUserId(), request.getTemplateId());
		if(tmp_info!=null){
			if(tmp_info.getIs_add_unsubscribe()==1){
				auto_content = false ;
			}
			template_ext = tmp_info.getTemplate_ext_code();
			if(template_ext!=null){
				try {
					int tmp1 = Integer.parseInt(template_ext);
					template_ext = String.valueOf(tmp1);
				} catch (Exception e) {
					template_ext="";
				}
			}else{
				template_ext="";
			}
		}
		
		for( BatchParam  para:params){
			tmp_request = new SendSmsRequest();
			tmp_request.setUserId(request.getUserId());
			tmp_request.setTemplateId(request.getTemplateId());
			tmp_request.setSmsParam(para.getSmsParam());
			HSResponse base = checkTemplate(tmp_request);
			if(!"ok".equals(base.getCode())){
				SmsResponse hs_response = new SmsResponse();
				hs_response.setCode(base.getCode());
				hs_response.setMobile(para.getMobile());
				hs_response.setMsg(base.getText());
				hs_response.setMsgId(UUID.randomUUID().toString());
            	result.add(hs_response);
				continue;
			}else {
				bean = new SubmitBean();
				String tmp_content = base.getText();
				/**
				 * 业务不自动扩展且模板允许追加内容
				 */
				if(!service_auto_ext&&auto_content){
					tmp_content = tmp_content+HSToolCode.getTemplateAutoTDContent(template_ext);
				}
				String[] tmp_mobiles ={para.getMobile()};
				bean.setContent(tmp_content);
				bean.setUser_id(corp_id);
				bean.setMobiles(tmp_mobiles);
				bean.getExtraFields().put("transaction_id", request.getTransactionId());
				bean.getExtraFields().put("template_id", request.getTemplateId());
				smsList.add(bean);
			}
		}
		
		if(result.size()==params.size()){
			HSResponse hs_result = new HSResponse();
			hs_result.setCode(ErrorCodeUtil.TEMPLATE_ERROR);
			hs_result.setText("提交参数没有通过模板校验: "+result);
			return hs_result;
		}
		
		if(service_auto_ext){
			tmp_ext = template_ext ;
		}
		  
		
		UserBean userInfo = DataCenter.getUserBean(request.getUserId());
		
		if(userInfo==null){
			if(requestIP==null){
				requestIP="";
			}
			LogUtil.writeLogs(corp_id,requestIP, ErrorCodeUtil.common_user_status_close, "账号["+corp_id+"]不存在");
			HSResponse hs_response = new HSResponse();
			hs_response.setCode(ErrorCodeUtil.common_user_status_close);
			hs_response.setText("账号不存在");
			return hs_response ;
		}
		// 校验用户访问ip
		if (requestIP!=null&&!SmsServiceFactory.checkUserIP(userInfo.getUser_ip(), requestIP, corp_id)) {
			LogUtil.writeLogs(corp_id,requestIP, ErrorCodeUtil.common_user_ip_error, "非法IP访问,所允许的IP为["+userInfo.getUser_ip()+"]");
			HSResponse hs_response = new HSResponse();
			hs_response.setCode(ErrorCodeUtil.common_user_ip_error);
			hs_response.setText("非法IP访问");
			return hs_response ;
	     }
		if(requestIP==null){
			requestIP="";
		}
		/**
		 * 业务代码必须填写
		 */
		if(corp_service==null){
			LogUtil.writeLogs(corp_id,requestIP, ErrorCodeUtil.common_corpServiceError_or_statusClose, "业务代码参数尚未填写");
			HSResponse hs_response = new HSResponse();
			hs_response.setCode(ErrorCodeUtil.common_corpServiceError_or_statusClose);
			hs_response.setText("业务代码参数尚未填写");
			return hs_response ;
		}
		  
		/**
		 * 对扩展信息进行处理
		 */
		if(ext==null){
			ext="";
		}else{
			try {
				int tmp2 = Integer.parseInt(ext);
				ext = String.valueOf(tmp2);
			} catch (Exception e) {
				ext="";
			}
		}
		 
		try {
			int tmp3  =  Integer.parseInt(tmp_ext);
			tmp_ext = String.valueOf(tmp3);
		} catch (Exception e) {
			tmp_ext = "";
		}
		ext = tmp_ext+ext;
		// 判断用户是否存在
		if(smsList==null||smsList.isEmpty()){
			//没有提交手机号码
			LogUtil.writeLogs(corp_id,requestIP, ErrorCodeUtil.common_too_less_mobiles, "没提交手机号码");
			HSResponse hs_response = new HSResponse();
			hs_response.setCode(ErrorCodeUtil.common_too_less_mobiles);
			hs_response.setText("没提交手机号码");
			return hs_response ;
		}
		
//		if(smsList.size()>1000){
//			//手机号码数量过多
//			LogUtil.writeLogs(corp_id,requestIP, ErrorCodeUtil.common_too_many_mobiles, "提交的手机号码个数为["+smsList.size()+"]多余1000");
//			HSResponse hs_response = new HSResponse();
//			hs_response.setCode(ErrorCodeUtil.common_too_many_mobiles);
//			hs_response.setText("提交的手机号码个数为["+smsList.size()+"]多余1000");
//			return hs_response ;
//		}
		 
		int count = 0 ;
		for(SubmitBean resultBean:smsList){
			SmsResponse response = new SmsResponse();
			String msg_id = resultBean.getMsg_id();
			//msg_id长度校验
            if(msg_id == null || msg_id.trim().length() > 50||msg_id.trim().length()==0){
            	resultBean.setMsg_id(UUID.randomUUID().toString());
            }
            
            String msg_content = resultBean.getContent();
			String mobile = resultBean.getMobilesString();
            response.setMsgId(resultBean.getMsg_id());
            response.setMobile(mobile);
			
            resultBean.setUserSn(userInfo.getSn());
			resultBean.setMsg_format(8);
			resultBean.setSubmit_type("SGIP");
			resultBean.setUser_id(userInfo.getUser_id());
			resultBean.setSp_number(corp_service+ext);
			
			String ValidResult = null;
			
			try{
				String is_multi_sign = "1";
				if(userInfo.getParamMap()!=null){
					is_multi_sign =  (String) userInfo.getParamMap().get("is_multi_sign");
					if(is_multi_sign==null||is_multi_sign.trim().equals("")){
						is_multi_sign = "1";
					}
				}
			
			if(is_multi_sign.equals("0")){
				BalanceVerifier.Instance().valid_multi(resultBean);
			}else{
				BalanceVerifier.Instance().valid(resultBean);
			}
			}catch(Exception e){
				LogUtil.getReceiver_log().error("计费异常["+resultBean+"]",e);
			}
			
			
        	
            
            String signature = resultBean.getSignature();
			if(resultBean.getWith_gate_sign()==1){
				signature = resultBean.getOperator_signature();
			}
			if(null == signature){
				signature = "";
			}
			
			String msg_content_new = resultBean.getContent()+signature;
			
			 
			
        	//内容长度校验
            if(msg_content.length() == 0 || msg_content_new.length() > 1000){
            	//单条短信不合法
            	LogUtil.getReceiver_log().info("mobile:" + mobile +" msg_content:"+ msg_content_new.length() +" >1000 OR  =0");
            	response.setCode(ErrorCodeUtil.common_msg_content_error_gxh);
            	response.setMsg("短信内容长度不合法,你提交的短息长度是["+msg_content_new.length()+"]");
            	result.add(response);
				continue;
            }
            
             //入库
            ValidResult = SmsServiceFactory.getValidResult(resultBean.getResp(), requestIP, corp_id, corp_service);
			if(ValidResult.equals("")){
				try {
					//LogUtil.getReceiver_log().info("receive user_id:"+corp_id+", pwd:******, corp_service:"+corp_service+ "; mobile:"+mobile+" requestIP:"+requestIP+"; td_code:"+resultBean.getTd_code()+"; method:"+method+"; type:"+type);
					DataCenter.addSubmitListDone(resultBean);
				} catch (Exception e) {
					log.error("",e);
				}
				response.setCode("1000");
				response.setMsg("SUCCESS");
				result.add(response);
			}else{
				response.setCode(ValidResult);
				if ("-1".equals(ValidResult)) {
					response.setMsg("error");
					LogUtil.writeLogs(corp_id, requestIP, ValidResult,"未知错误，请联系研发人员");
				} else if (ErrorCodeUtil.common_corpServiceError_or_statusClose.equals(ValidResult)) {
					response.setMsg("业务匹配失败");
					LogUtil.writeLogs(corp_id,requestIP,ValidResult,"客户业务匹配失败,客户提交参数:corp_service[" + corp_service+ "]; 客户所有业务信息为:"+ userInfo.getServiceMapString());
				} else if (ErrorCodeUtil.hs_balance_not_enough_gxh.equals(ValidResult)) {
					Map<String, UserBalanceInfo> map = DataCenter.getUserBalanceMap();
					UserBalanceInfo userBalanceInfo = map.get(corp_id);
					response.setMsg("余额不足");
					if (userBalanceInfo != null) {
						LogUtil.writeLogs(corp_id, requestIP, ValidResult,"客户余额[" + userBalanceInfo.getBalance() + "]不足");
					} else {
						LogUtil.writeLogs(corp_id, requestIP, ValidResult,"客户余额不足");
					}
				} else if(ErrorCodeUtil.common_mobile_number_error.equals(ValidResult)){
					response.setCode(ErrorCodeUtil.common_mobile_number_error);
					response.setMsg("无效手机号码");
					LogUtil.writeLogs(corp_id,requestIP,ValidResult,"无效手机号码["+mobile+"]");
				}else  if(ErrorCodeUtil.common_umber_match_services_error.equals(ValidResult)){
					response.setCode(ErrorCodeUtil.common_umber_match_services_error);
					response.setMsg("手机号码无法和业务匹配");
					LogUtil.writeLogs(corp_id,requestIP,ValidResult,"手机号码无法和业务匹配["+mobile+"]");
				}else  if(ErrorCodeUtil.common_no_before_sign.equals(ValidResult)){
					response.setCode(ErrorCodeUtil.common_no_before_sign);
					response.setMsg("前置无签名");
					LogUtil.writeLogs(corp_id,requestIP,ValidResult,"["+mobile+"]"+"内容开头无签名["+msg_content+"]");
				}else  if(ErrorCodeUtil.common_no_after_sign.equals(ValidResult)){
					response.setCode(ErrorCodeUtil.common_no_after_sign);
					response.setMsg("后置无签名");
					LogUtil.writeLogs(corp_id,requestIP,ValidResult,"["+mobile+"]"+"内容结尾无签名["+msg_content+"]");
				}else  if(ErrorCodeUtil.common_sign_no_record.equals(ValidResult)){
					response.setCode(ErrorCodeUtil.common_sign_no_record);
					response.setMsg("签名没有报备");
					LogUtil.writeLogs(corp_id,requestIP,ValidResult,"["+mobile+"]"+"签名没有报备["+msg_content+"]");
				}
				else{
					response.setCode(ValidResult);
					response.setMsg("未定义");
					LogUtil.writeLogs(corp_id,requestIP,ValidResult,"未定义["+mobile+"]");
				}
				result.add(response);
				count++;
			}
		}
		
		/**
		 * 全部下发失败
		 */
		if(count==smsList.size()){
			//手机号码和业务无法正确匹配
			HSResponse hs_response = new HSResponse();
			hs_response.setCode(ErrorCodeUtil.common_umber_match_services_error);
			hs_response.setText("所提交的手机号码都无法正常和业务进行匹配");
			LogUtil.writeLogs(corp_id,requestIP, ErrorCodeUtil.common_umber_match_services_error, "所提交的手机号码都无法正常和业务进行匹配");
			return hs_response ;
		}
		
		
		
		return getResponse(result);
	}

	 
	private HSResponse getResponse(List<SmsResponse> list){
		HSResponse result = new HSResponse();
		result.setCode("1000");
		result.setText("SUCCESS");
		result.getOther().put("SmsResponse", list);
		return result;
	}
	
	/**
	 * 若模板验证通过，则返回Code=ok ,Text=拼接后的短信内容
	 * @param request
	 * @return
	 */
	HSResponse checkTemplate(SendSmsRequest request){
		HSResponse hs_response = new HSResponse();
		String templateId =request.getTemplateId();
		if(templateId==null){
			hs_response.setCode(ErrorCodeUtil.TEMPLATE_NULL);
			hs_response.setText("templateId 未进行赋值，当前值为["+templateId+"]");
			return hs_response;
		}
		Map<String, String> param = request.getSmsParam();
		SmsTemplateInfo info = SmsTemplateCache.getSmsTemplate(request.getUserId(), request.getTemplateId());
		if(info==null){
			hs_response.setCode(ErrorCodeUtil.TEMPLATE_NULL);
			hs_response.setText("模板["+templateId+"]不存在");
			return hs_response;
		}
		
		List<SmsTemplateParam> temps = info.getParams();
		if(param==null){
			param = new HashMap<String, String>();
		}
		if(temps==null){
			temps = new ArrayList<SmsTemplateParam>();
		}
		 if(temps.size()!=param.size()){
				//模板参数个数不对
				hs_response.setCode(ErrorCodeUtil.TEMPLATE_ERROR);
				hs_response.setText("templateId: "+templateId+", 提交的参数个数["+param.size()+"]和模板中的参数个数["+temps.size()+"]不匹配");
				return hs_response;
		 }
		  
		
		String tmp_content = info.getTemplateContent();
		for(SmsTemplateParam tp:temps){
			if(!param.containsKey(tp.getParamName())){
				hs_response.setCode(ErrorCodeUtil.TEMPLATE_ERROR);
				hs_response.setText("templateId: "+templateId+", 参数变量["+tp.getParamName()+"]无法匹配。");
				return hs_response;
			}else {
				String tmp = "";
				try {
					tmp = param.get(tp.getParamName());
				} catch (Exception e) {
					hs_response.setCode(ErrorCodeUtil.TEMPLATE_ERROR);
					hs_response.setText("templateId: "+templateId+", 参数变量["+tp.getParamName()+"]的值异常,参数值为：["+ param.get(tp.getParamName())+"]"+e.getMessage());
					return hs_response;
				}
				
				 
				if(tmp.length()>tp.getMaxLength()){
					hs_response.setCode(ErrorCodeUtil.TEMPLATE_ERROR);
					hs_response.setText("templateId: "+templateId+", 参数变量["+tp.getParamName()+"]的值["+tmp+"],超过最大长度["+tp.getMaxLength()+"]的限制。");
					return hs_response;
				}
				tmp_content = tmp_content.replace("${"+tp.getParamName()+"}", tmp);
			}
		}
		hs_response.setCode("ok");
		hs_response.setText(tmp_content);
		return hs_response; 
	}

}
