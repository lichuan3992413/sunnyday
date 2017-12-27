package sunnyday.gateway.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sunnyday.common.model.UserBean;
import sunnyday.common.model.UserServiceForm;

public class Tools {

	public final static HashMap<String,Long> user_deliver_count = new HashMap<String,Long>();
	public final static HashMap<String,Long> user_report_count = new HashMap<String,Long>();
	public final static HashMap<String,Long> user_balance_count = new HashMap<String,Long>();
	/**
	 * 查询业务号(百悟)
	 * @param serviceMap
	 * @param corp_service
	 * @param userInfo
	 * @return
	 */
	public static String getCustomerYwForm(Map<String, List<UserServiceForm>> serviceMap, String corp_service,UserBean userInfo) {
        if(serviceMap==null || serviceMap.size()<=0){
            return null;
        }
        String pwd = CommonUntil.getPassword(userInfo);
        for(String key : serviceMap.keySet()){
        	for (UserServiceForm model : serviceMap.get(key)) {
                if (corp_service.equalsIgnoreCase(MD5.convert(pwd+model.getService_code()))) {
                    return model.getService_code();
                }
            }
        }
        return null;
    }
	
	
	public static String getCustomerYwForm(Map<String, List<UserServiceForm>> serviceMap) {
        if(serviceMap==null || serviceMap.size()<=0){
            return null;
        }
        for(String key : serviceMap.keySet()){
        	for (UserServiceForm model : serviceMap.get(key)) {
                    return model.getService_code();
            }
        }
        return null;
    }
}
