package sunnyday.controller.cache;

import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.MobileHome;
import sunnyday.tools.util.ParamUtil;

@Service
public class MobileAreaCache extends Cache {
	
	private Map<String, MobileHome> mobileMap;

	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		Map<String, MobileHome> tmp = (Map<String, MobileHome>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_MOBILE_AREA);
		if(tmp!=null&&!tmp.isEmpty()){
			mobileMap = tmp;
		}
		return true;
	}

	public Map<String, MobileHome> getMobileArea() {
		if (mobileMap == null || mobileMap.size() == 0) {
			reloadCache();
		}
		return mobileMap;
	}

}
