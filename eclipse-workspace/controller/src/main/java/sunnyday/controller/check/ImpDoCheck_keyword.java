package sunnyday.controller.check;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.cache.ErrCodeCache;
import sunnyday.controller.cache.KeywordCache;
@Component
public class ImpDoCheck_keyword implements IDoCheck {
	@Autowired
	private KeywordCache keywordCache;
	public int doCheck(SmsMessage sms) {
		int result = 0;
		int status = 0;
		
		String content = sms.getComplete_content();
		if(content != null){
			/**
			 * 过滤去除特别符合如：法**轮**功**
			 */
			content = SmsFilter.handle(content);	
		}
//		System.out.println("基础关键词过滤内容 = " + content);
		
		result = filter_keyword_new(sms,content);
		
		return result;
	}
	
	public int filter_keyword(SmsMessage sms,String content){
		int result = 0;
		int status = 0;
		String keyword = SmsFilter.filterContentBySplitRgex(content, keywordCache.getKeywords(KeywordCache.BASIC_KEYWORD));
		if(keyword != null){
			//关键词命中
			result = 0;
			status = 2;
			sms.setResponse(ErrCodeCache.getErrCode(ErrCode.codeName.basicKeyWordReject).getResponse());
			sms.setErr(String.valueOf(ErrCodeCache.getErrCode(ErrCode.codeName.basicKeyWordReject).getErr()));
			sms.setFail_desc(ErrCodeCache.getErrCode(ErrCode.codeName.basicKeyWordReject).getFail_desc().replace("$", keyword.substring(1)));
		}else{
			//通过
			status = 1;
			result = 1;
		}
		sms.setStatus(status);
		
		
		
		return result;
	}
	
	public int filter_keyword_new(SmsMessage sms,String content){
		int result = 0;
		int status = 0;
		String user_id = sms.getUser_id();
		String keyword = SmsFilter.filterContentBySplitRgex(content, keywordCache.getUserKeywordPattern(user_id));
		if(keyword != null){
			//关键词命中
			result = 0;
			status = 2;
			sms.setResponse(ErrCodeCache.getErrCode(ErrCode.codeName.userKeyWordReject).getResponse());
			sms.setErr(String.valueOf(ErrCodeCache.getErrCode(ErrCode.codeName.userKeyWordReject).getErr()));
			sms.setFail_desc(ErrCodeCache.getErrCode(ErrCode.codeName.userKeyWordReject).getFail_desc().replace("$", keyword.substring(1)));
		}else if((keyword = SmsFilter.filterContentBySplitRgex(content, keywordCache.getGlobalKeywordPattern("global")))!=null){
			//关键词命中
			result = 0;
			status = 2;
			sms.setResponse(ErrCodeCache.getErrCode(ErrCode.codeName.globalKeyWordReject).getResponse());
			sms.setErr(String.valueOf(ErrCodeCache.getErrCode(ErrCode.codeName.globalKeyWordReject).getErr()));
			sms.setFail_desc(ErrCodeCache.getErrCode(ErrCode.codeName.globalKeyWordReject).getFail_desc().replace("$", keyword.substring(1)));
			
		}else{
			//通过
			status = 1;
			result = 1;
		}
		sms.setStatus(status);
		
		return result;
	}
	
	
}
