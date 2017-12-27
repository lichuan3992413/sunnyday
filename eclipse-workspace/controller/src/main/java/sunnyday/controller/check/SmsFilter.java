package sunnyday.controller.check;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

/**
 *
 * Description:  短信过滤器
 *
 * @author Administrator<lichuan3992413@gmail.com>
 *
 * Create at:   2014-10-12 下午05:23:02 
 */
public class SmsFilter {
	
	private static Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	private Set<String> keyWords;//关键词

	public SmsFilter(Set<String> keyWords){
		this.keyWords = keyWords;
	}
	public Set<String> getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(Set<String> keyWords) {
		this.keyWords = keyWords;
	}
	
	/**
	 * 判断短信是否包含关键词
	 * @param msg_content
	 * @param model true:包含模式 false:与或模式
	 * @return
	 */
	public String containKeyWord(String msg_content,boolean model){
		
		String filter_msg_cotent = handle(msg_content); 
		
		for(String keyWord:keyWords){
			
			if(model){
				
				if(filter_msg_cotent.contains(keyWord)){
//					System.out.println("短信内容： "+msg_content+" 处理后短信内容："+filter_msg_cotent);
//					System.out.println("关键词 "+keyWord);
					return keyWord;
				}
			}else{
				return containKeyWordByBasic(msg_content);
			}
		}
		return null;
	}
	

	
	public String containKeyWordByBasic(String msg_content){
		
		String filter_msg_cotent = handle(msg_content); 
		
		for(String keyWord:keyWords){
			
			if(validate(filter_msg_cotent, keyWord)){
				//System.out.println("短信内容： "+msg_content+" 处理后短信内容："+filter_msg_cotent);
				//System.out.println("关键词 "+keyWord);
				return keyWord;
			}
		}
		return null;
	}
	
	private static boolean validate(String content,String regex){

		return content.matches(regex);
	}
	

	public static String filterKeyWordIfContain(String msg_content, Set<String> keywordSet){
		if(msg_content != null && keywordSet != null){
			
			for(String keyWord: keywordSet){
				if(msg_content.contains(keyWord)){
//				System.out.println("短信内容： "+msg_content+" 处理后短信内容："+filter_msg_cotent);
//				System.out.println("关键词 "+keyWord);
					return keyWord;
				}
			}
		}
		return null;
	}
	
	/**
	 * 000000000
	 * @param msg_content
	 * @param conditionSet
	 * @return
	 */
	public static String filterContentBySplitRgex(String msg_content, Set<Pattern[]> conditionSet){
		String result = null;
		if(msg_content != null && conditionSet != null && conditionSet.size() > 0){
			for(Pattern[] conditions: conditionSet){
//				System.out.println("keyword: " + condition);
				boolean CheckResult = false;
				StringBuilder sb = new StringBuilder();
				
				for(Pattern regxPattern : conditions){
					if(regxPattern != null){
						try{
							Matcher markMatcher = regxPattern.matcher(msg_content);
							if(markMatcher.find()){
							  //记录被命中的模板
								sb.append("_").append(regxPattern.pattern());
//							System.out.println("命中" + sb.toString());
								CheckResult = true;
							}else{
//							System.out.println("不命中");
								CheckResult = false;
								break;
							}
						}catch(Exception e){
							log.error("", e);
							CheckResult = false;
							break;
						}
					}
				}
				if(CheckResult){
					result = sb.toString();
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 返回命中的正则
	 * @param msg_content
	 * @param conditionSet
	 * @return
	 */
	public static String filterContentBySplitRgex4Content(String msg_content, Set<Pattern[]> conditionSet){
		String result = null;
		
		if(msg_content != null && conditionSet != null && conditionSet.size() > 0){
			for(Pattern[] conditions: conditionSet){
//				System.out.println("keyword: " + condition);
				boolean CheckResult = false;
				StringBuilder sb = new StringBuilder();
				
				for(Pattern regxPattern : conditions){
					if(regxPattern != null){
						try{
							Matcher markMatcher = regxPattern.matcher(msg_content);
							if(markMatcher.find()){
								sb.append("_").append(regxPattern.pattern());
 						       // System.out.println("命中" + regxPattern.pattern());
								CheckResult = true;
							}else{
//							System.out.println("不命中");
								CheckResult = false;
								break;
							}
						}catch(Exception e){
							log.error("", e);
							CheckResult = false;
							break;
						}
					}
				}
				if(CheckResult){
					result = sb.toString();
					break;
				}
			}
		}
		return result;
	}
	
	public static String handle(String str){
		str = str.replaceAll("[\\W&&[^\u4e00-\u9fa5]]","");//非汉字的特殊字符过滤掉
		return str;
	}
}
