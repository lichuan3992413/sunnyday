package sunnyday.controller.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import sunnyday.common.model.ChannelRoute;
import sunnyday.common.model.CheckMethod;
import sunnyday.common.model.TdInfo;
import sunnyday.tools.util.ParamUtil;
import sunnyday.controller.util.*;
@Service
public class TdInfoSyncTask extends Task{

	protected void detailTask() {
	//通道表缓存，key = td_code，value  = td_info
	Map<String, Map<String, TdInfo>> tdInfo = new HashMap<String, Map<String, TdInfo>>();
	Map<String, TdInfo> tdInfo_map = load_td_info() ;
	if(tdInfo_map!=null&&!tdInfo_map.isEmpty()){
		tdInfo.put(ParamUtil.REDIS_KEY_TD_INFO,tdInfo_map);
		gateRao.updateDataCenterTdInfoCache(tdInfo);	
	}
	
	
 
	Map<String, Map<String, String>> chargeTermid = new HashMap<String, Map<String, String>>();
	Map<String, String> chargeTermid_map = load_chargeTermidMap();
	if(chargeTermid_map!=null&&!chargeTermid_map.isEmpty()){
		chargeTermid.put(ParamUtil.REDIS_KEY_CHARGETERMID, chargeTermid_map);
		sendRao.updateDataCenterChargeTermidMapCache(chargeTermid);
	}
	
 
	
	
	// 正则表达式
	Map<String, List<CheckMethod>> check_method_info = new ConcurrentHashMap<String,List<CheckMethod>>();
	List<CheckMethod> check_method_info_list = load_check_method() ;
	if(check_method_info_list!=null&&check_method_info_list.size()>0){
		check_method_info.put(ParamUtil.REDIS_KEY_CHECK_METHOD_INFO,check_method_info_list);
		gateRao.updateCacheData(check_method_info);
	}
	
	
	
	// 发送线程路由表
	Map<String, List<ChannelRoute>> channel_route = new ConcurrentHashMap<String,List<ChannelRoute>>();
	List<ChannelRoute>  channel_route_list = load_channel_route() ;
	if(channel_route_list!=null&&!channel_route_list.isEmpty()){
		channel_route.put(ParamUtil.REDIS_KEY_CHANNEL_ROUTE,channel_route_list);
		gateRao.updateCacheData(channel_route);
	}
	
	}
	
	  
	
	

	private List<ChannelRoute> load_channel_route() {
		List<ChannelRoute> temp =  cacheDAO.loadChannelRouteList();
		 return temp;
	}

	 
 


	public Map<String, TdInfo> load_td_info() {
		Map<String, TdInfo> tmp = new ConcurrentHashMap<String, TdInfo>();
		List<TdInfo> tdInfoList = cacheDAO.load_td_full_info();
		if(null!=tdInfoList && tdInfoList.size()>0){
			for(TdInfo each : tdInfoList){
				String key = each.getTd_code();
				String ext_code = each.getExt_code();
				if(null==ext_code){
					ext_code = "";
				}
				if(!tmp.containsKey(key)){
					tmp.put(key, each);
				}
				tmp.get(key).getSignMap().put(each.getTd_sp_number()+""+ext_code, each.toTdSignInfo());
			}
		}
		 return tmp;
	}
	
	public Map<String, String> load_chargeTermidMap() {
		 return cacheDAO.load_chargeTermidMap();
	}
	
	
	private List<CheckMethod> load_check_method() {
		List<CheckMethod> temp =  cacheDAO.loadCheckMethodList();
		if(null!=temp && temp.size()>0){
			for(CheckMethod each : temp){
				each.setPattern(Pattern.compile(each.getCheck_method()));
			}
		}
		 return temp;
	}





	public void reloadCache() {
		
	}
}
