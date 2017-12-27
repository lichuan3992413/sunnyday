package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import sunnyday.common.model.UserBean;
import sunnyday.common.model.UserServiceForm;
import sunnyday.common.model.UserSignForm;
import sunnyday.tools.util.ParamUtil;
@Service
public class UserInfoCache extends Cache {
	
	//用户基础信息
	private static Map<String, UserBean> user_info;
	
	//根据用户微服务ID获取用户基础信息
	private static Map<String, UserBean> pid_user_info = new HashMap<String, UserBean>();
	//用户业务信息
	private static Map<String, List<UserServiceForm>> user_service_info;
	 
	private static Map<String, List<UserSignForm>> user_sign_info;
	

	
	public static Map<String, List<UserSignForm>> getUser_sign_info() {
		if(user_sign_info==null){
			user_sign_info = new HashMap<String, List<UserSignForm>>();
			System.out.println("user_sign_info is null .");
		}
		return user_sign_info;
	}

	public static Map<String, UserBean> getUser_info() {
		if(user_info==null){
			user_info = new HashMap<String, UserBean>();
			System.out.println("user_info is null .");
		}
		return user_info;
	}
	
	public static UserBean getUser_info(String user_id) {
		if(user_info==null){
			user_info = new HashMap<String, UserBean>();
			System.out.println("user_info is null .");
		}
		return user_info.get(user_id);
	}
	
	/**
	 * 通过客户的微服务标识，获取用户信息
	 * @param pid
	 * @return
	 */
	public static UserBean getPidUser(String pid) {
		if(pid_user_info!=null&&!pid_user_info.isEmpty()){
			return pid_user_info.get(pid);
		}
		return null;
	}
	

	public static Map<String, List<UserServiceForm>> getUser_service_info() {
		if(user_service_info==null){
			user_service_info = new HashMap<String, List<UserServiceForm>>();
			System.out.println("user_service_info is null .");
		}
		return user_service_info;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		Map<String, UserBean> tmp1 =  (Map<String, UserBean>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_USER_INFO);
		if(tmp1!=null&&!tmp1.isEmpty()){
			user_info = tmp1;
		}
		Map<String, List<UserServiceForm>> tmp2 = (Map<String, List<UserServiceForm>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_USER_SERVICE_INFO);
		if(tmp2!=null&&!tmp2.isEmpty()){
			user_service_info = tmp2;
		}
		
		Map<String, List<UserSignForm>> tmp3 =  (Map<String, List<UserSignForm>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_USER_SIGN_INFO) ;
		if(tmp3!=null&&!tmp3.isEmpty()){
			user_sign_info = tmp3 ;	
		}
		
		load_pid_user();
		return true;
	}


	private void load_pid_user() {
		if (user_info != null&&!user_info.isEmpty()) {
			Map<String, UserBean> tmp = new HashMap<String, UserBean>();
			for (UserBean u : user_info.values()) {
				if(u.getParamMap() != null){
					String key = u.getParamMap().get("provider_id_pre")!=null?(String)u.getParamMap().get("provider_id_pre"):"";
					if(!"".equals(key)){
						tmp.put(key,  u);
					}
				}
			}
			pid_user_info = tmp ;
		}

	}

}
