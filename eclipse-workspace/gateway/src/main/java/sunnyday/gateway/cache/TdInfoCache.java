package sunnyday.gateway.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.TdInfo;
import sunnyday.gateway.threadPool.Task;

@Service
public class TdInfoCache  extends Task {
	 
	
	//通道表缓存，key = td_code，value  = td_info
	private static Map<String, TdInfo> td_info = new HashMap<String, TdInfo>();
	
	public synchronized static  TdInfo getTd_info(String td_code) {
		if(td_info!=null){
			return td_info.get(td_code);
		}
		return null;
	}

	@Override
	public void reloadCache() {
		try{
			Map<String, TdInfo> tempMap = dao.getTdInfo();
			if(tempMap != null&&!tempMap.isEmpty()){
				td_info = tempMap;
			}
		}catch(Exception e){
			if(log.isErrorEnabled()){
				log.error("通道信息同步失败", e);
			}
		}
	}

}
