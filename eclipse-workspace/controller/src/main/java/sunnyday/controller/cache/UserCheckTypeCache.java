package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.UserCheckType;
import sunnyday.tools.util.ParamUtil;
@Service
public class UserCheckTypeCache extends Cache {
	
	private static Map<String, List<UserCheckType>> user_check_mode;
	public static Map<String, List<UserCheckType>> getUser_check_mode() {
		if(user_check_mode==null){
			user_check_mode = new HashMap<String, List<UserCheckType>>();
			System.out.println("user_check_mode is null .");
		}
		return user_check_mode;
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		Map<String, List<UserCheckType>> tmp = (Map<String, List<UserCheckType>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_USER_CHECK_MODE);
		if(tmp!=null&&!tmp.isEmpty()){
			user_check_mode = tmp ;
		}
		return true;
	}

}
