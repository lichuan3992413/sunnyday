package sunnyday.gateway.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.gateway.threadPool.Task;
@Service
public class GateConfigCache extends  Task {
	
	private static Map<String, String> gateConfig;
	
	public static Map<String, String> getGateConfig() {
		if(gateConfig==null){
			gateConfig = new HashMap<String, String>();
		}
		return gateConfig;
	}


	@Override
	public void reloadCache() {
		try {

//			//载入全局配置文件
//			GateConfigMapper gateConfigMapper = Spring.getApx().getBean(GateConfigMapper.class);
//			dao.putGateConfig(gateConfigMapper);
			//
			 Map<String, String> tmp = dao.getGateConfig();
			 if(tmp!=null&&!tmp.isEmpty()){
				 gateConfig = tmp;
			 }
		} catch (Exception e) {
			log.error("GateConfigCache: ",e);
		}
	}
	
	public static String  getGateConfigValue(String key){
		if(gateConfig!=null){
			return gateConfig.get(key);
		}
		return null;
	}

}
