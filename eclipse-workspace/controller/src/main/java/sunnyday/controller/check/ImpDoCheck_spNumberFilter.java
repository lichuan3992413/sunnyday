package sunnyday.controller.check;

import org.springframework.stereotype.Component;

import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.SpNumberFilterInfo;
import sunnyday.controller.cache.ErrCodeCache;
import sunnyday.controller.cache.SpNumberCache;
@Component
public class ImpDoCheck_spNumberFilter implements IDoCheck {
	private final int TYPE_PASS = 1;
	private final int TYPE_REJECT = 2;

	
	public int doCheck(SmsMessage msg) {
		ErrCode errCode = ErrCodeCache.getErrCode(ErrCode.codeName.ChildCountReject);
		int result = 1;
		String user_id = msg.getUser_id();
		String user_sp_number = msg.getService_code() + msg.getExt_code() + msg.getUser_ext_code();
		if(SpNumberCache.getCacheMap().containsKey(user_id)){
			int filter_type = 0;
			boolean hitted = false;
			for(SpNumberFilterInfo eachSp : SpNumberCache.getCacheMap().get(user_id)){
				filter_type = eachSp.getFilter_type();
				if(user_sp_number.startsWith(eachSp.getSp_number())){
					hitted = true;
				}
			}
			
			if((filter_type == TYPE_PASS && !hitted) || (filter_type == TYPE_REJECT && hitted)){
				result = 0;
				msg.setStatus(errCode.getStatus());
				msg.setResponse(errCode.getResponse());
				msg.setFail_desc(errCode.getFail_desc());
			}
		}
		return result;
	}

}
