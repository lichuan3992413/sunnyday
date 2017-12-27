package sunnyday.controller.check;

import java.util.Map;
import java.util.Set;

import sunnyday.common.model.CheckCacheForm;
import sunnyday.common.model.ErrCode;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.cache.ErrCodeCache;

public class MessageCheckUtil{
	
	/**
	 * 缓存审核
	 * @param message
	 * @param set
	 * @param flag
	 * 	
	 * @return
	 * true equals 
	 * false contains
	 */
	private static CheckCacheForm cacheCheck(SmsMessage message, Set<CheckCacheForm> set, boolean flag){
		
		if(set == null)
			return null;
		
		String content = message.getComplete_content();
		for(CheckCacheForm each:set){
			String msg_content = each.getMsg_content();
			if(flag){
				
				if(content.equals(msg_content)){
					return each;
				}
			}else{
				
				if(content.contains(msg_content)){
					return each;
				}
			}
		}
		return null;
	}
	/**
	 * 命中返回0 不命中返回1
	 * @param message
	 * @param map
	 * @param codeName
	 * @return
	 */
	public static int checkFromMap(SmsMessage message, Map<String, Set<CheckCacheForm>> map, ErrCode.codeName codeName) {
		String td_code = message.getTd_code();
		int result = 1;
		
		if(map != null){
			Set<CheckCacheForm> caches = map.get(td_code);
			CheckCacheForm form = null;
			if(codeName == ErrCode.codeName.manualCacheReject){
				form = MessageCheckUtil.cacheCheck(message, caches, false);
			}else{
				form = MessageCheckUtil.cacheCheck(message, caches, true);
			}
			
			if(form != null){
				
				message.addExtraField("check_user", form.getCheck_user());
				message.addExtraField("cache_sn", form.getSn());
				int status = form.getStatus();
				message.setStatus(status);
				//自定义response，标示缓存处理，与check_time字段结合，可以标示指定缓存，进而分析缓存命中率，优化缓存
				message.setResponse(ErrCodeCache.getErrCode(codeName).getResponse());
				message.setErr(String.valueOf(ErrCodeCache.getErrCode(codeName).getErr()));
				if(status == 1){//审核通过
					message.setFail_desc(ErrCodeCache.getErrCode(codeName).getFail_desc() + "-->" + "缓存通过");
				}else if(status == 2){//审核驳回
					message.setFail_desc(ErrCodeCache.getErrCode(codeName).getFail_desc() + "-->" + "缓存驳回");
				}
				
				result = 0;
			}else{
				result = 1;
			}
		}
		return result;
	}
}
