package sunnyday.controller.check;

import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.cache.ErrCodeCache;
import sunnyday.controller.cache.WhiteContentCache;
@Component
public class ImpDoCheck_White implements IDoCheck {
	private ErrCode.codeName codeName = ErrCode.codeName.whiteFilterReject;
	
	
	public int doCheck(SmsMessage sms) {
		int result = 0;
		int status = 0;
		String completeContent = sms.getComplete_content();
//		long t = System.currentTimeMillis();
 	
		Set<Pattern[]> whiteSet = WhiteContentCache.getTd_white_list().get(sms.getTd_code()); 
		if(whiteSet != null && whiteSet.size() > 0){
			String hittedWords = SmsFilter.filterContentBySplitRgex(completeContent, whiteSet);
			
			if(hittedWords == null){
				//白名单审核不通过
				result = 0;
				status = 2;
				sms.setResponse(ErrCodeCache.getErrCode(codeName).getResponse());
				sms.setErr(String.valueOf(ErrCodeCache.getErrCode(codeName).getErr()));
				sms.setFail_desc(ErrCodeCache.getErrCode(codeName).getFail_desc());
			}else{
				//通过
				result = 1;
				status = 1;
			}
		}else{
			//通过
			result = 1;
			status = 1;
		}
		 
		
//		System.out.println(hittedWords);
		sms.setStatus(status);
//		System.out.println("白名单过滤耗时： " + (System.currentTimeMillis() - t));
		return result;
	}
}
