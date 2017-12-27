package sunnyday.gateway.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.NetSwitchedMobileInfo;
import sunnyday.gateway.threadPool.Task;
@Service
public class NetSwitchedMobileCache  extends Task {
	
	//携号转网缓存
	private static Map<String, NetSwitchedMobileInfo> numberPortabilityMap = new HashMap<String, NetSwitchedMobileInfo>();
	
	public static Map<String, NetSwitchedMobileInfo> getNumberPortabilityMap() {
		if(numberPortabilityMap==null){
			numberPortabilityMap = new HashMap<String, NetSwitchedMobileInfo>();
		}
		return numberPortabilityMap;
	}

	@Override
	public void reloadCache() {
		try{
			Map<String, NetSwitchedMobileInfo> tempMap = dao.getNetSwitchedMobileInfo();
			if(tempMap != null){
				numberPortabilityMap = tempMap;
			}
		}catch(Exception e){
			if(log.isErrorEnabled()){
				log.error("账户信息同步失败", e);
			}
		}
	}
}
