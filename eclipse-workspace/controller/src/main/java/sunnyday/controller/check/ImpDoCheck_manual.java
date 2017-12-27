package sunnyday.controller.check;

import org.springframework.stereotype.Component;

import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.cache.ErrCodeCache;


/**
 * Description:  全审
 *
 * @author lichuan<lichuan3992413@gmail.com>
 *
 * Create at:   2013-1-10 下午6:21:38 
 */
@Component
public class ImpDoCheck_manual implements IDoCheck {

	public int doCheck(SmsMessage sms) {
		sms.setStatus(0);
		sms.setFail_desc(ErrCodeCache.getErrCode(ErrCode.codeName.manualReject).getFail_desc());
		return 0;
	}

}
