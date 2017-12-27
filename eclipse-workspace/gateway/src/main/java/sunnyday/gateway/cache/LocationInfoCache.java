package sunnyday.gateway.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.CheckMethod;
import sunnyday.common.model.LocationInfo;
import sunnyday.gateway.threadPool.Task;

@Service
public class LocationInfoCache  extends Task {
	 
	
	
	//地区缓存
	private static Map<String, LocationInfo> locationInfoMap = new  HashMap<String, LocationInfo>();
	public static Map<String, LocationInfo> getLocationInfoMap() {
		return locationInfoMap;
	}

	//正则缓存
	private static List<CheckMethod> checkMethodList = new  ArrayList<CheckMethod>();
	
	public static List<CheckMethod> getCheckMethodList() {
		return checkMethodList;
	}

	@Override
	public void reloadCache() {
		try {
			load_check_method();
			load_locatin_info();
		} catch (Exception e) {
			if(log.isErrorEnabled()){
				log.error("正则地区同步异常",e);
			}
		}
	}
	
	private void load_check_method() {
		List<CheckMethod> temp = dao.getCheckMethod();
		if(null != temp&&!temp.isEmpty()){
			checkMethodList = temp;
		}
	}

	public void load_locatin_info() {
		Map<String, LocationInfo> tempMap = dao.getLocationInfo();
		if(null != tempMap){
			locationInfoMap = tempMap;
		}
	}
}
