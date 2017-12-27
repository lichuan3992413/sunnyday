package sunnyday.controller.check;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.cache.ErrCodeCache;
import sunnyday.controller.cache.GateConfigCache;
import sunnyday.controller.filter.FilterFactory;
import sunnyday.controller.filter.IFilter;
import sunnyday.tools.util.CommonLogFactory;
/**
 * 由于存在客户恶意刷号段下发信息，导致客户投诉，现在加入账户级的号段重复下发过滤机制。
 * 过滤一个账户，一个号段，一段时间内只允许发限定条数
 * @author 1111182
 *
 */
@Component
public class ImpDoCheck_mobileScopeRepeat implements IDoCheck {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	private int timeInterver = 60 * 5;
	private int repeatNum = 50;
	private IFilter filter = null;
	private ErrCode.codeName codeName = ErrCode.codeName.MobileScopeRepeatReject;
	
	public ImpDoCheck_mobileScopeRepeat(){
		filter = FilterFactory.getMsgFilter(FilterFactory.REPEAT_FILTER);
		filter.doStart("mobileScopeRepeatFilter");
	}

	public int doCheck(SmsMessage msg) {
		int result = 1; 
		setLastMonitorConfig(msg);
		
		String mobile = msg.getMobile();
		int user_sn = msg.getUser_sn();
		if(mobile != null){
			mobile = mobile.substring(0, 7);
		}
		
		String key = user_sn + "_" +mobile;
		int multiParam = msg.getPktotal() > 0 ? msg.getPktotal() : 1;
		//System.out.println((repeatNum - 1)*multiParam+"["+key+"-> "+msg.getSub_msg_id()+"_"+msg.getPknumber()+"]");
		Object filterResult = filter.doFilter(key, timeInterver, (repeatNum - 1)*multiParam);
		if(filterResult != null){
			//System.out.println(key+"-> "+msg.getSub_msg_id()+"_"+msg.getPknumber()+"_"+filterResult);
			//判断是号段重复下发
			result = 0;
			msg.setStatus(2);
			msg.setResponse(ErrCodeCache.getErrCode(codeName).getResponse());
			msg.setErr(String.valueOf(ErrCodeCache.getErrCode(codeName).getErr()));
			msg.setFail_desc(ErrCodeCache.getErrCode(codeName).getFail_desc());
		}
		return result;
	}
	
	private void setLastMonitorConfig(SmsMessage msg) {
			try{
				String tmpTime = GateConfigCache.getValue("mobile_scope_time_interval");
				if(tmpTime != null && !tmpTime.equals("")){
					timeInterver = Integer.parseInt(tmpTime);
				}else {
					timeInterver = 60 * 5;
				}
				String tmpNum = GateConfigCache.getValue("mobile_scope_max_repeat_num");
				if(tmpNum != null && !tmpNum.equals("")){
					repeatNum = Integer.parseInt(tmpNum);
				}else {
					repeatNum = 50;
				}
			}catch(Exception e){
				log.error("mobile scope config error", e);
			}
		 
	}
}
