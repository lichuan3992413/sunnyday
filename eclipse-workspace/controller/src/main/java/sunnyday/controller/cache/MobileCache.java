package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import sunnyday.tools.util.ParamUtil;
@Service
public class MobileCache extends Cache {
	private static Map<String, Set<String>> white_mobiles = null;
    
	
	private static Map<String, Set<String>> user_black_mobile = null;
	
	private static Map<String, Set<String>> user_black_scope = null;
	
	private static Map<String, Set<String>> globle_black_mobile = null;
	
	private static Map<String, Set<String>> globle_black_scope = null;
	
	private static Map<String, Set<String>> template_black_mobile = null;

	private static Map<String, Set<String>> service_black_mobile = null;
	
	
	
	
	public static Map<String, Set<String>> getWhite_mobiles() {
		if(white_mobiles==null){
			white_mobiles = new HashMap<String, Set<String>>();
		}
		return white_mobiles;
	}
	

	public static Map<String, Set<String>> getUser_black_mobile() {
		if(user_black_mobile==null){
			user_black_mobile = new HashMap<String, Set<String>>();
		}
		return user_black_mobile;
	}

	public static Map<String, Set<String>> getUser_black_scope() {
		if(user_black_scope==null){
			user_black_scope = new HashMap<String, Set<String>>();
		}
		return user_black_scope;
	}

	public static Map<String, Set<String>> getGloble_black_mobile() {
		if(globle_black_mobile==null){
			globle_black_mobile = new HashMap<String, Set<String>>();
		}
		return globle_black_mobile;
	}

	public static Map<String, Set<String>> getGloble_black_scope() {
		if(globle_black_scope==null){
			globle_black_scope = new HashMap<String, Set<String>>();
		}
		return globle_black_scope;
	}

	public static Map<String, Set<String>> getTemplateBlackMobiles() {
		if(template_black_mobile==null){
			template_black_mobile = new HashMap<String, Set<String>>();
		}
		return template_black_mobile;
	}

	public static Map<String, Set<String>> getServiceBlackMobiles() {
		if(service_black_mobile==null){
			service_black_mobile = new HashMap<String, Set<String>>();
		}
		return service_black_mobile;
	}


	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		white_mobiles = (Map<String, Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_WHITE_MOBILES);
		globle_black_scope = (Map<String, Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_GLOBLE_BLACK_SCOPE);
		globle_black_mobile = (Map<String, Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_GLOBLE_BLACK_MOBILE);

		user_black_scope = (Map<String, Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_USER_BLACK_SCOPE);
		user_black_mobile = (Map<String, Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_USER_BLACK_MOBILE) ;
		
		template_black_mobile = (Map<String, Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_TEMPLATE_BLACK_MOBILE) ;
		// 业务级别黑名单
		service_black_mobile = (Map<String, Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_SERVICE_BLACK_MOBILE) ;
		
		return true;
	}

}
