package sunnyday.controller.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import sunnyday.common.model.KeywordForm;
import sunnyday.controller.util.PatternTool;
import sunnyday.tools.util.ParamUtil;
@Service
public class KeywordCache extends Cache {
	
	public final static int BASIC_KEYWORD = 0;
	public final static int TD_KEYWORD = 1;
	public final static int COMMON_KEYWORD = 2;
	public final static int SPICAL_KEYWORD = 3;
	
	private Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();//关键词map Integer:level 
	
	private Set<Pattern[]> basickeywords;//基础关键词
	private Set<Pattern[]> tdkeywords;//通道关键词
	private Set<Pattern[]> commonkeywords;//待审核关键词
	private Set<Pattern[]> specialkeywords;//特殊关键
	private Map<String,Set<Pattern[]>> global_keyword_pattern = new HashMap<String, Set<Pattern[]>>();
	private Map<String,Set<Pattern[]>> user_keyword_pattern = new HashMap<String, Set<Pattern[]>>();
	
	public Set<Pattern[]> getGlobalKeywordPattern(String key){
		if(global_keyword_pattern != null){
			return global_keyword_pattern.get(key);
		}
		
		return null;
	}
	
	public Set<Pattern[]> getUserKeywordPattern(String key) {
		if (user_keyword_pattern != null) {
			return user_keyword_pattern.get(key);
		}

		return null;
	}
	
	@Override
	public boolean reloadCache() {
		Map<String,Set<Pattern[]>> global_keyword_pattern_tmp = new HashMap<String, Set<Pattern[]>>();
		Map<String,Set<Pattern[]>> user_keyword_pattern_tmp = new HashMap<String, Set<Pattern[]>>();
		Map<String,Set<String>> global_keyword_map = (Map<String,Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_GLOBAL_KEYWORD);
		Map<String,Set<String>> user_keyword_map = (Map<String,Set<String>>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_USER_KEYWORD);
		if(global_keyword_map!=null&&global_keyword_map.size()>0){
			for(String key:global_keyword_map.keySet()){
				Set<String> wordSet = global_keyword_map.get(key);
				Set<Pattern[]> set = PatternTool.complieCollection(wordSet);
				global_keyword_pattern_tmp.put(key, set);
			}
			global_keyword_pattern = global_keyword_pattern_tmp;
		}
		
		if(user_keyword_map!=null&&user_keyword_map.size()>0){
			for(String key:user_keyword_map.keySet()){
				Set<String> wordSet = user_keyword_map.get(key);
				Set<Pattern[]> set = PatternTool.complieCollection(wordSet);
				user_keyword_pattern_tmp.put(key, set);
			}
			user_keyword_pattern = user_keyword_pattern_tmp;
		}
		
		
		
		return true;
	}
	
	public Set<Pattern[]> getKeywords(int level){
		if(level== KeywordCache.BASIC_KEYWORD){
			return basickeywords;
		}else if(level== KeywordCache.TD_KEYWORD){
			return tdkeywords;
		}else if(level== KeywordCache.COMMON_KEYWORD){
			return commonkeywords;
		}else if(level == KeywordCache.SPICAL_KEYWORD){
			return specialkeywords;
		}else{
			return null;
		}
	}
    
	private Set<Pattern[]> _getKeyWords(int level){
		
		if(map==null)
			return null;
		List<String> list = map.get(level);
		if(list == null)
			return null;
		Set<Pattern[]> set = PatternTool.complieCollection(list);
		return set;
	}
	
	private boolean reload_keyword(){
		@SuppressWarnings("unchecked")
		List<KeywordForm> list = (List<KeywordForm>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_KEYWORD);
		map.clear();
		if(list != null){
			for(KeywordForm each : list){
				int level = each.getLevel();
				
				if(!map.containsKey(level)){
					map.put(level, new ArrayList<String>());
				}
				map.get(level).add(each.getWord());
			}
		}
		basickeywords = _getKeyWords(BASIC_KEYWORD);
		tdkeywords = _getKeyWords(TD_KEYWORD);
		commonkeywords = _getKeyWords(COMMON_KEYWORD);
		specialkeywords = _getKeyWords(SPICAL_KEYWORD);
		return true;
	}
}
