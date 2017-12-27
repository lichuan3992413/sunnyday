package sunnyday.gateway.cache;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import sunnyday.common.model.UserBean;
import sunnyday.gateway.dc.DataCenter;
import sunnyday.gateway.threadPool.Task;

@Service
public class UserBeanCache  extends Task {
	//账户缓存
	private static Map<String, UserBean> userBeanMap = new  ConcurrentHashMap<String, UserBean>();
	
	 
	 
	public static Map<String, UserBean> getUserBeanMap() {
		return userBeanMap;
	}
	public static  UserBean  getUserBean(String user_id) {
		if(userBeanMap!=null){
			return userBeanMap.get(user_id.trim());
		}
		return null;
	}

	private static Set<String> delever_user = new  HashSet<String>();
	
	private static Set<String> report_user = new  HashSet<String>();
	/**
	 * 需要上行的客户
	 */
	public static Set<String> getDelever_user() {
		return delever_user;
	}
	/**
	 * 需要状态报告的客户
	 */
	public static Set<String> getReport_user() {
		return report_user;
	}

	public void reloadCache() {
		try{
			Map<String, UserBean> tempMap = dao.getUserInfo();
			if(tempMap != null&&!tempMap.isEmpty()){
				DataCenter.setUserBeanMap(tempMap);
				userBeanMap = tempMap;
				getDelever_user(tempMap);
			}
		}catch(Exception e){
			if(log.isErrorEnabled()){
				log.error("账户信息同步失败", e);
			}
		}
	}
	
	private void getDelever_user(Map<String, UserBean> map){
		Set<String> tmp_deliver = new  HashSet<String>();
		Set<String> tmp_report = new  HashSet<String>();
		if(map!=null){
			for(String key:map.keySet()){
				if(map.get(key).getDeliver_type()!=0){
					tmp_deliver.add(key);
				}
				if(map.get(key).getReport_type()!=0){
					tmp_report.add(key);
				}
			}
		}
		delever_user = tmp_deliver;
		report_user = tmp_report;
	}
}
