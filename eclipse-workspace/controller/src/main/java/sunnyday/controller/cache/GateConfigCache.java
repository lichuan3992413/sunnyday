package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.tools.util.ParamUtil;
@Service
public class GateConfigCache extends Cache {
	private static Map<String, String> gateConfig;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		Map<String, String> tMap = (Map<String, String>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_GATE_CONFIG);
		 if(tMap!=null&&!tMap.isEmpty()){
			 gateConfig =(Map<String, String>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_GATE_CONFIG);
		 }
		return true;
	}

	public static Map<String, String> getGateConfig() {
		if(gateConfig==null){
			gateConfig = new HashMap<String, String>();
			System.out.println("gateConfig is null .");
		}
		return gateConfig;
	}
	 
	public static String getValue(String key ) {
		String result = null; 
		if(gateConfig!=null){
			result=gateConfig.get(key);
		}
		return result;
	}
}
