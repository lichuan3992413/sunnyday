package sunnyday.controller.check;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.cache.ErrCodeCache;
import sunnyday.controller.cache.KeywordCache;

@Component
public class ImpDoCheck_manualKeyWord implements IDoCheck {
	@Autowired
	private KeywordCache keywordCache;
	private ErrCode.codeName special = ErrCode.codeName.specialKeyWordReject;
	private ErrCode.codeName manual = ErrCode.codeName.manualKeyWordReject;
	
	public int doCheck(SmsMessage sms) {
		String content = sms.getComplete_content();
		int status = 1;
		int result = 1;
		String special_keyword = SmsFilter.filterContentBySplitRgex(content, keywordCache.getKeywords(KeywordCache.SPICAL_KEYWORD));
		if(special_keyword != null){
			//特殊关键词命中
			status = 0;
			result = 0;
			sms.setResponse(ErrCodeCache.getErrCode(special).getResponse());
			sms.setErr(String.valueOf(ErrCodeCache.getErrCode(special).getErr()));
			sms.setFail_desc(ErrCodeCache.getErrCode(special).getFail_desc().replace("$", special_keyword.substring(1)));
		}else{
			String manual_keyword = SmsFilter.filterContentBySplitRgex(content, keywordCache.getKeywords(KeywordCache.COMMON_KEYWORD));
			if(manual_keyword != null){
				//关键词命中
				status = 0;
				result = 0;
				sms.setResponse(ErrCodeCache.getErrCode(manual).getResponse());
				sms.setErr(String.valueOf(ErrCodeCache.getErrCode(manual).getErr()));
				sms.setFail_desc(ErrCodeCache.getErrCode(manual).getFail_desc().replace("$", manual_keyword));
			}
		}
		sms.setStatus(status);
		return result;
	}

}
