package sunnyday.controller.check;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import sunnyday.controller.filter.FilterFactory;
import sunnyday.controller.filter.IFilter;

@Component
public class ValidationFactory {
	public final static int SUBMIT_REPEAT = -3;
	public final static int SP_NUMBER_FILTER = -2;
	
 	public final static int ALL_REJECT = -1;
	public final static int WHITE_LIST = 0;
	public final static int BLACK_LIST = 1;
	public final static int KEYWORD = 2;
	public final static int CACHE = 3;
	public final static int MANUAL_KEYWORD = 4;
	public final static int MANUAL = 5;//全审
	public final static int MASS_MONITOR = 6;
	public final static int MOBILE_SCOPE_MONITOR = 7;
	
	public final static int WHITE_TEMPLATE_FILTER = 8;//内容白名单模板拦截
	
	private static IDoCheck impDocheckSubmitRepeat;
	private static IDoCheck impDocheckSpNumberFilter;
 	private static IDoCheck impDocheck_reject;
	private static IDoCheck impDocheckWhite;
	private static IDoCheck impDocheckBlack;
	private static IDoCheck impDoCheckKeyword;
	private static IDoCheck impDoCheck_cache;
	private static IDoCheck impDoCheck_manualKeyWord;
	private static IDoCheck impDoCheck_manual;
	private static IDoCheck impDoCheck_mass;
	private static IDoCheck impDoCheck_mobileScopeRepeat;
	private static IDoCheck ImpDoCheck_WhiteTemplate;
	private static Map<Integer,IFilter> filter_map = new HashMap<Integer, IFilter>();
	
	public static IDoCheck getValidataMethod(int checkType){
		switch(checkType){
			case SUBMIT_REPEAT: return impDocheckSubmitRepeat;
			case SP_NUMBER_FILTER: return impDocheckSpNumberFilter;
 		    case ALL_REJECT: return impDocheck_reject; 
			case WHITE_LIST: return impDocheckWhite;
			case BLACK_LIST: return impDocheckBlack;
			case KEYWORD: return impDoCheckKeyword;
			case CACHE: return impDoCheck_cache;
			case MANUAL: return impDoCheck_manual;
			case MASS_MONITOR: return impDoCheck_mass;
			case MOBILE_SCOPE_MONITOR: return impDoCheck_mobileScopeRepeat;
			case MANUAL_KEYWORD: return impDoCheck_manualKeyWord;
			case WHITE_TEMPLATE_FILTER: return ImpDoCheck_WhiteTemplate;
			default: return null;
		}
	}
	
	public static IFilter getIFilter(int type,String name){
		if(!filter_map.containsKey(type)){
			IFilter filter = FilterFactory.getMsgFilter(type);
			filter.doStart(name);
			filter_map.put(type, filter);
		}
		return filter_map.get(type);
	} 
	
//	@Resource(name="impDoCheck_submitRepeat")
//	public void setImpDoCheck_submitRepeat(IDoCheck impDocheck_submitRepeat) {
//		ValidationFactory.impDocheckSubmitRepeat = impDocheck_submitRepeat;
//	}
	@Resource(name="impDoCheck_spNumberFilter")
	public void setImpDoCheck_spNumberFilter(IDoCheck impDocheck_spNumberFilter) {
		ValidationFactory.impDocheckSpNumberFilter = impDocheck_spNumberFilter;
	}
	@Resource(name="impDoCheck_mobileScopeRepeat")
	public void setImpDoCheck_mobileScopeRepeat(IDoCheck impDoCheck_mobileScopeRepeat) {
		ValidationFactory.impDoCheck_mobileScopeRepeat = impDoCheck_mobileScopeRepeat;
	}
 	@Resource(name="impDoCheck_reject")
 	public void setImpDocheck_reject(IDoCheck impDocheck_reject) {
 		ValidationFactory.impDocheck_reject = impDocheck_reject;
 	}
 	@Resource(name="impDoCheck_White")
	public void setImpDocheckWhite(IDoCheck impDocheckWhite) {
		ValidationFactory.impDocheckWhite = impDocheckWhite;
	}
	@Resource(name="impDoCheck_Black")
	public void setImpDocheckBlack(IDoCheck impDocheckBlack) {
		ValidationFactory.impDocheckBlack = impDocheckBlack;
	}
	@Resource(name="impDoCheck_keyword")
	public void setImpDoCheckKeyword(IDoCheck impDoCheckKeyword) {
		ValidationFactory.impDoCheckKeyword = impDoCheckKeyword;
	}
	@Resource(name="impDoCheck_cache")
	public void setImpDoCheck_cache(IDoCheck impDoCheck_cache) {
		ValidationFactory.impDoCheck_cache = impDoCheck_cache;
	}
	@Resource(name="impDoCheck_manualKeyWord")
	public void setImpDoCheck_manualKeyWord(
			IDoCheck impDoCheck_manualKeyWord) {
		ValidationFactory.impDoCheck_manualKeyWord = impDoCheck_manualKeyWord;
	}
	@Resource(name="impDoCheck_manual")
	public void setImpDoCheck_manual(IDoCheck impDoCheck_manual) {
		ValidationFactory.impDoCheck_manual = impDoCheck_manual;
	}
//	@Resource(name="impDoCheck_mass")
//	public void setImpDoCheck_mass(IDoCheck impDoCheck_mass) {
//		ValidationFactory.impDoCheck_mass = impDoCheck_mass;
//	}
	
	@Resource(name="impDoCheck_WhiteTemplate")
	public void setImpDoCheck_WhiteTemplate(IDoCheck impDoCheck_mass) {
		ValidationFactory.ImpDoCheck_WhiteTemplate = impDoCheck_mass;
	}
	
}
