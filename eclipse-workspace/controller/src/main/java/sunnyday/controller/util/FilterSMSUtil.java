package sunnyday.controller.util;

import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

/**
 * 过滤短信中特殊字符
 * @author 1307365
 *
 */
public  class FilterSMSUtil {
	
	private static Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	/**
	 * 过滤掉内容中的所有数字
	 * @param content
	 * @return
	 */
	public static  String filterDigit(String content){
		StringBuffer sb = new StringBuffer();
		try {
			char[] array = content.toCharArray();
			for (int i = 0; i < array.length; i++) {
				if((int)array[i]>57||(int)array[i]<48){
					sb.append(array[i]);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return sb.toString();
	}
	
	/**
	 * 过滤掉内容中的所有字母
	 * @param content
	 * @return
	 */
	public static  String filterLetter(String content){
		StringBuffer sb = new StringBuffer();
		try {
			char[] array = content.toCharArray();
			for (int i = 0; i < array.length; i++) {
				if(((int)array[i]>90||(int)array[i]<65)&&((int)array[i]>122||(int)array[i]<97)){
					sb.append(array[i]);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return sb.toString();
	}
	/**
	 * 过滤掉内容中的所有数字和字母
	 * @param content
	 * @return
	 */
	public static  String filterDigitAndLetter(String content){
		StringBuffer sb = new StringBuffer();
		try {
			char[] array = content.toCharArray();
			for (int i = 0; i < array.length; i++) {
				if(((int)array[i]>57||(int)array[i]<48)&&((int)array[i]>90||(int)array[i]<65)&&((int)array[i]>122||(int)array[i]<97)){
					sb.append(array[i]);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return sb.toString();
	}
}
