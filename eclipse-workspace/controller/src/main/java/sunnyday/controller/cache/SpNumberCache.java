package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.SpNumberFilterInfo;
import sunnyday.tools.util.ParamUtil;
@Service
public class SpNumberCache extends Cache {
	private static Map<String, List<SpNumberFilterInfo>> cacheMap;
	public static Map<String, List<SpNumberFilterInfo>> getCacheMap() {
		if(cacheMap==null){
			cacheMap = new HashMap<String, List<SpNumberFilterInfo>>();
			//System.out.println("cacheMap is null .");
		}
		return cacheMap;
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		cacheMap = (Map<String, List<SpNumberFilterInfo>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_SP_NUMBER_FILTER);
		return true;
	}

}
