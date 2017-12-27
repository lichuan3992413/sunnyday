package sunnyday.controller.cache;

import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.tools.util.ParamUtil;

@Service
public class AdminIDMobileCache extends Cache {
	private static Map<String, String> admin_id_mobile = null;

	@SuppressWarnings("unchecked")
	public boolean reloadCache() {
		Map<String, String>  tmp =  (Map<String, String>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_ADMIN_ID_MOBILE);
		if(tmp!=null&&!tmp.isEmpty()){
			admin_id_mobile =  tmp;
		}
		return true;
	}

	public static String getMobileByAdminID(String admin_id){
		if(admin_id_mobile!=null){
		 return  admin_id_mobile.get(admin_id);
		}
		return null;
	}
	public static Map<String, String> getCommandMap(){
		return admin_id_mobile;
	}
}
