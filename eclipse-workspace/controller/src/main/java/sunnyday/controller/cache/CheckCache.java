package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import sunnyday.common.model.CheckCacheForm;
import sunnyday.tools.util.ParamUtil;

/**
 * Description:  审核缓存
 */
@Service
public class CheckCache extends Cache{
	
	private Map<String, Set<CheckCacheForm>> cacheMap;
	
	private Map<String, Set<CheckCacheForm>> lastMap;
	
	/**
	 * 采用包含模式处理个性化短信
	 * 加载数据
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		Map<String, Set<CheckCacheForm>> tmp = (Map<String, Set<CheckCacheForm>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_CACHEMAP);
		 if(tmp!=null&&tmp.isEmpty()){
			 cacheMap = tmp ;
		 } 
		 Map<String, Set<CheckCacheForm>> tmp_last = (Map<String, Set<CheckCacheForm>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_LASTMAP); 
		 if(tmp_last!=null&&!tmp_last.isEmpty()){
			 lastMap = tmp_last;
		 }
		return true;
	}
 
	 
	public Map<String, Set<CheckCacheForm>> getCacheMap() {
		if(cacheMap==null){
			cacheMap = new HashMap<String, Set<CheckCacheForm>>();
			//System.out.println("cacheMap is null .");
		}
		return cacheMap;
	}
 

	public Map<String, Set<CheckCacheForm>> getLastMap() {
		if(lastMap==null){
			lastMap = new HashMap<String, Set<CheckCacheForm>>();
			System.out.println("lastMap is null .");
		}
		return lastMap;
	}
}
