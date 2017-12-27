package sunnyday.gateway.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import sunnyday.gateway.threadPool.Task;

/**
 * 缓存admin_id和业务账号关系
 * 
 * @author 71604057
 * @date 2017年10月15日 下午6:31:19
 */
@Service
public class AdminCache extends  Task {
	
	private static Map<String, List<String>> adminIdUsers = null;

	@Override
	public void reloadCache() {
		Map<String, String> tmp = (Map<String, String>) dao.getAdminIdMobile();
		if (tmp != null && !tmp.isEmpty()) {
			Map<String, List<String>> adminIdUsersMap = (Map<String, List<String>>) dao.getAdminIdUsers();
			if(adminIdUsersMap!=null && adminIdUsersMap.size()>0){
				Set<Map.Entry<String, String>> entrySet = tmp.entrySet();
				if(entrySet != null && entrySet.size() > 0){
					Iterator<Map.Entry<String, String>> iter = entrySet.iterator();
					adminIdUsers = new HashMap<String, List<String>>();
					while(iter.hasNext()){
						Map.Entry<String, String> entry = iter.next();
						if(entry != null){
							String adminId = entry.getKey();
							adminIdUsers.put(adminId, adminIdUsersMap.get(adminId));
						}
					}
				}
			}
		}
	}

	public static Map<String, List<String>> getCacheMap(){
		if(adminIdUsers == null){
			adminIdUsers = new HashMap<String, List<String>>();
		}
		
		return adminIdUsers;
	}
	
}
