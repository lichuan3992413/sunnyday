package sunnyday.controller.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunnyday.common.model.LocationInfo;
import sunnyday.common.model.NetSwitchedMobileInfo;
import sunnyday.common.model.TdInfo;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;

@Service
public class TdInfoCache extends Cache{
	
	private static final Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	
	//通道表缓存，td_code为key，td_info为value
	private static Map<String, TdInfo> td_info;//--------
	
	
	//携号转网缓存
	private static Map<String, NetSwitchedMobileInfo> numberPortabilityMap;
		
	//地区缓存
	private static Map<String, LocationInfo> locationInfoMap;
	
	private static List<String> sp_number_list;
	
	public static List<String> getTdSpnumberList() {
		if(sp_number_list==null){
			sp_number_list = new ArrayList<String>();
		}
		return sp_number_list;
	}
	
	public static void setTd_Info(Map<String, TdInfo> tdInfos){
		td_info = tdInfos;
	}
	
	public static Map<String, TdInfo> getTd_info() {
		if(td_info==null){
			td_info = new HashMap<String, TdInfo>();
			log.warn("td_info is null .");
		}
		return td_info;
	}

	public synchronized static  TdInfo  getTd_info(String td_code) {
		if(td_info==null){
			td_info = new HashMap<String, TdInfo>();
		}
		return td_info.get(td_code);
	}
	 

	public static Map<String, NetSwitchedMobileInfo> getNumberPortabilityMap() {
		if(numberPortabilityMap==null){
			numberPortabilityMap = new HashMap<String, NetSwitchedMobileInfo>();
			log.warn("numberPortabilityMap is null .");
		}
		return numberPortabilityMap;
	}

	public static Map<String, LocationInfo> getLocationInfoMap() {
		if(locationInfoMap==null){
			locationInfoMap = new HashMap<String, LocationInfo>();
			log.warn("locationInfoMap is null .");
		}
		return locationInfoMap;
	}

	 

	public static Set<String> getSubmit_type() {
		if(submit_type==null){
			submit_type = new HashSet<String>();
			log.warn("submit_type is null .");
		}
		return submit_type;
	}

	/**
	 * 支持整条提交的通道代码
	 */
	private static Set<String> submit_type ;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		Map<String, TdInfo> tmp = (Map<String, TdInfo>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_TD_INFO);
		if(tmp!=null&&!tmp.isEmpty()){
			td_info = tmp;
			load_locatin_info();
			load_switched_mobile();
			load_td_submit_type(td_info);
			load_td_spnumber();
		}
		return true;
	}
		
	@SuppressWarnings("unchecked")
	private void load_td_spnumber() {
		sp_number_list =  (List<String>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_TD_SPNUMBER);
	}

	public void load_td_submit_type(Map<String, TdInfo> td_info) {
		Set<String> tmp  = new HashSet<String>();
		 if(td_info!=null){
			 for(String key:td_info.keySet()){
				 if(td_info.get(key).getSubmit_type()==2){
					 tmp.add(td_info.get(key).getTd_code());
				 }
			 }
		 }
		 
		 submit_type = tmp;
	}
	
 
	
	@SuppressWarnings("unchecked")
	public void load_switched_mobile() {
		numberPortabilityMap = (Map<String, NetSwitchedMobileInfo>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_NET_SWITCHED_MOBILE);
	}
	
 

	@SuppressWarnings("unchecked")
	public void load_locatin_info() {
		locationInfoMap = (Map<String, LocationInfo>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_LOCATION_INFO_LIST) ;
	}
}
