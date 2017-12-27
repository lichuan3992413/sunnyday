package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.UserServiceForm;
import sunnyday.tools.util.ParamUtil;

@Service
public class MatchDeliverSpNumberCache extends Cache{

	private static  Map<String,UserServiceForm>  deliverMatchSpNumberCache = null;
	

	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		Map<String,UserServiceForm> tMap =  (Map<String, UserServiceForm>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_MATCH_DELIVER_SPNUMBER);
	    if(tMap!=null&&!tMap.isEmpty()){
	    	deliverMatchSpNumberCache = tMap;
	   }
		return true;
	}
 
	public static Map<String, UserServiceForm> getDeliverMatchSpNumberCache() {
		if(deliverMatchSpNumberCache==null){
			deliverMatchSpNumberCache = new HashMap<String, UserServiceForm>();
			System.out.println("deliverMatchSpNumberCache is null .");
		}
		return deliverMatchSpNumberCache;
	}

}
