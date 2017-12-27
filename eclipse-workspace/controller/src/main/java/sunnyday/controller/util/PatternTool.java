package sunnyday.controller.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import sunnyday.common.model.WhiteListTemplate;
import sunnyday.tools.util.CommonLogFactory;

/**
 * File: PatternTool.java
 * <P>
 * Description: -none-
 * <P>
 * <B>Change History :</B>
 * <P>
 * <ul>
 * <li>2008-6-27 Created by vivo</li>
 * <li>2008-6-27 Modified by vivo</li>
 * </ul>
 * 
 * @author GaoBo
 */
public class PatternTool {
	private static Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	
	public static Map<String, Set<Pattern[]>> complieMap(Map<String, Set<String>> tmpMap) {
		Map<String, Set<Pattern[]>> resultMap = new HashMap<String, Set<Pattern[]>>();
		
		if(tmpMap != null && tmpMap.size() > 0){
			for(String key : tmpMap.keySet()){
				Set<String> set = tmpMap.get(key);
				Set<Pattern[]> resultSet = complieCollection(set);
				resultMap.put(key, resultSet);
			}
		}
		return resultMap;
	}
	
	public static Set<Pattern[]> complieCollection(Collection<String> set) {
		Set<Pattern[]> resultSet = new HashSet<Pattern[]>();
		if(set != null && set.size() > 0){
			for(String each : set){
				if(each!=null){
					String [] regxs = each.split("&");
					if(regxs != null && regxs.length > 0){
						Pattern [] regxPatterns = new Pattern[regxs.length];
						for(int i = 0; i < regxs.length; i++){
							String regx = regxs[i];
							/*regx = regx.replace("（", "(");
							regx = regx.replace("）", ")");*/
							try{
								Pattern mark = Pattern.compile(regx.trim());
								regxPatterns[i] = mark;
							}catch(Exception e){
								log.warn("与或关键词不合法：" + each,e);
							}
						}
						resultSet.add(regxPatterns);
					}
				}
			
			}
		}
		return resultSet;
	}
	
	public static Set<Pattern[]> complieCollection(List<WhiteListTemplate> list) {
		Set<Pattern[]> resultSet = new HashSet<Pattern[]>();
		if(list != null && list.size() > 0){
			for(WhiteListTemplate wlp : list){
				if(wlp!=null) {
					
				
				String each = wlp.getTemplate_content();
				String [] regxs = each.split("&");
				if(regxs != null && regxs.length > 0){
					Pattern [] regxPatterns = new Pattern[regxs.length];
					for(int i = 0; i < regxs.length; i++){
						String regx = regxs[i];
						regx = regx.replace("（", "(");
						regx = regx.replace("）", ")");
						try{
							Pattern mark = Pattern.compile(regx.trim());
							regxPatterns[i] = mark;
						}catch(Exception e){
							log.warn("与或关键词不合法：" + each,e);
						}
					}
					resultSet.add(regxPatterns);
				}
			}}
		}
		return resultSet;
	}
	
	
	

	public static Set<Pattern[]> complieCommonTemplate(List<WhiteListTemplate> list) {
		return complieCollection(list);
	}

	public static Map<String, Set<Pattern[]>> complieUserTemplate(Map<String, List<WhiteListTemplate>> tmpMap) {
		Map<String, Set<Pattern[]>> resultMap = new HashMap<String, Set<Pattern[]>>();
		if (tmpMap != null && tmpMap.size() > 0) {
			for (String key : tmpMap.keySet()) {
				List<WhiteListTemplate> list = tmpMap.get(key);
				Set<Pattern[]> resultSet = complieCollection(list);
				if(resultSet!=null&&resultSet.size()>0){
					resultMap.put(key, resultSet);
				}
			}
		}
		return resultMap;
	}
}
