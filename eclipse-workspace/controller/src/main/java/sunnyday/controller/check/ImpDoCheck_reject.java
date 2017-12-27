package sunnyday.controller.check;

import org.springframework.stereotype.Component;

import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.cache.ErrCodeCache;

@Component
public class ImpDoCheck_reject implements IDoCheck {
	private ErrCode.codeName codeName = ErrCode.codeName.ChildCountReject;
	
	
	public int doCheck(SmsMessage sms) {
		sms.setStatus(2);
		sms.setResponse(ErrCodeCache.getErrCode(codeName).getResponse());
		sms.setErr(String.valueOf(ErrCodeCache.getErrCode(codeName).getErr()));
		sms.setFail_desc(ErrCodeCache.getErrCode(codeName).getFail_desc());
		return 0;
	}

}
