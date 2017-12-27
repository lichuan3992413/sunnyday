package sunnyday.controller.check;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.cache.ErrCodeCache;
import sunnyday.controller.cache.WhiteTemplateCache;
import sunnyday.tools.util.CommonLogFactory;
@Component
public class ImpDoCheck_WhiteTemplate implements IDoCheck {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	public int doCheck(SmsMessage sms) {
		int result = 0;
		int status = 0;
		String content = sms.getComplete_content();
		String user_id = sms.getUser_id() ;
		String keyword = SmsFilter.filterContentBySplitRgex(content, WhiteTemplateCache.getWhite_template_common());
		if(keyword != null){
			//命中公共级白名单，放过
			status = 1;
			result = 1;
		}else{
		    keyword = SmsFilter.filterContentBySplitRgex(content, WhiteTemplateCache.getWhite_template_user().get(user_id));
		    if(keyword!=null){
				//命中客户级别白名单模板，放过
				status = 1;
				result = 1;
			}else {
				//白名单模板不匹配，拦截
				result = 0;
				status = 2;
				sms.setResponse(ErrCodeCache.getErrCode(ErrCode.codeName.whiteTemplateReject).getResponse());
				sms.setErr(String.valueOf(ErrCodeCache.getErrCode(ErrCode.codeName.whiteTemplateReject).getErr()));
				sms.setFail_desc(ErrCodeCache.getErrCode(ErrCode.codeName.whiteTemplateReject).getFail_desc());
			}
		}
		sms.setStatus(status);
		log.info("手机号码： "+sms.getMobile()+", 短信内容："+sms.getComplete_content()+", 命中的模板："+keyword);
		return result;
	}
	public static void main(String[] args) {
		SmsMessage sms = new SmsMessage();
		sms.setResponse(ErrCodeCache.getErrCode(ErrCode.codeName.whiteTemplateReject).getResponse());
	}
}
