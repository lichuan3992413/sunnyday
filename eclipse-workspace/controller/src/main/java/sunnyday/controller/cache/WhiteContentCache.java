package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import sunnyday.controller.util.PatternTool;
import sunnyday.tools.util.ParamUtil;
@Service
public class WhiteContentCache extends Cache {
	private static Map<String, Set<Pattern[]>> user_white_list = null;
	
	private static Map<String, Set<Pattern[]>> td_white_list = null;
	
	private static Map<String, Set<Pattern[]>> user_filter_content_list = null;
	
	
	public static Map<String, Set<Pattern[]>> getUser_filter_content_list() {
		if(user_filter_content_list==null){
			user_filter_content_list = new HashMap<String, Set<Pattern[]>>();
			System.out.println("user_filter_content_list is null .");
		}
		return user_filter_content_list;
	}
	public static Map<String, Set<Pattern[]>> getUser_white_list() {
		if(user_white_list==null){
			user_white_list = new HashMap<String, Set<Pattern[]>>();
			System.out.println("user_white_list is null .");
		}
		return user_white_list;
	}
	public static Map<String, Set<Pattern[]>> getTd_white_list() {
		if(td_white_list==null){
			td_white_list = new HashMap<String, Set<Pattern[]>>();
			System.out.println("td_white_list is null .");
		}
		return td_white_list;
	}
	@Override
	public boolean reloadCache() {
		load_user_white_list();
		load_td_white_list();
		load_user_filter_content_list();
		return true;
	}
	private boolean load_user_white_list(){
		@SuppressWarnings("unchecked")
		Map<String, Set<String>> tmpMap = (Map<String, Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_USER_WHITE_LIST);
		user_white_list = PatternTool.complieMap(tmpMap);
		return true;
	}
	
	private boolean load_td_white_list(){
		@SuppressWarnings("unchecked")
		Map<String, Set<String>> tmpMap = (Map<String, Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_TD_WHITE_LIST);	
		td_white_list = PatternTool.complieMap(tmpMap);
		return true;
	}
	
	// 用户二级重复拦截模板
	private boolean load_user_filter_content_list(){
		@SuppressWarnings("unchecked")
		Map<String, Set<String>> tmpMap = (Map<String, Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_USER_FILITER_CONTENT_LIST);	
		user_filter_content_list = PatternTool.complieMap(tmpMap);
		return true;
	}
}
