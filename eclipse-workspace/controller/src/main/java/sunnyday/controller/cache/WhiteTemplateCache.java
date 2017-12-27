package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import sunnyday.common.model.WhiteListTemplate;
import sunnyday.controller.util.PatternTool;
import sunnyday.tools.util.ParamUtil;

/**
 * 内容白名单模板缓存信息加载
 * @author 1307365
 *
 */
@Service
public class WhiteTemplateCache extends Cache{
	private static Map<String, Set<Pattern[]>> white_template_user =  new HashMap<String, Set<Pattern[]>>();
	private static Set<Pattern[]> white_template_common =  new HashSet<Pattern[]>();

	
	@Override
	public boolean reloadCache() {
		load_white_user();
		load_white_common();
		return true;
	}
	
	private boolean load_white_common(){
		@SuppressWarnings("unchecked")
		List<WhiteListTemplate> list =  (List<WhiteListTemplate>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_WHITE_TEMPLATE_COMMON);
		white_template_common = PatternTool.complieCommonTemplate(list);
		return true;
	}
	private boolean load_white_user(){
		@SuppressWarnings("unchecked")
		Map<String, List<WhiteListTemplate>> tmpMap = (Map<String, List<WhiteListTemplate>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_WHITE_TEMPLATE_USER);
		white_template_user = PatternTool.complieUserTemplate(tmpMap);
		return true;
	}

	public static Map<String, Set<Pattern[]>> getWhite_template_user() {
		return white_template_user;
	}

	public static Set<Pattern[]> getWhite_template_common() {
		return white_template_common;
	}
	

}
