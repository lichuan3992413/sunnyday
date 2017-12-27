package sunnyday.controller.check;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.cache.CheckCache;

@Component
public class ImpDoCheck_cache implements IDoCheck {
	
	@Autowired
	private CheckCache checkCache;
	/**
	 * 缓存审核 命中返回0 不命中返回1 
	 * @param message
	 * @return
	 */
	public int doCheck(SmsMessage sms) {
		int result = 1;
		result = MessageCheckUtil.checkFromMap(sms, checkCache.getCacheMap(), ErrCode.codeName.manualCacheReject);
		return result;
	}

	
}
