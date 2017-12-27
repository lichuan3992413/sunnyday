package sunnyday.gateway.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * 
 * 类描述：手机号码工具类 创建人：zqh 创建时间：2017-10-24 17:46
 * 
 * @version V1.0.0.T.1 ----------------------------------------- 修改记录(迭代更新)：zqh-
 *          2017-10-24 17:46---(新建)
 * 
 **/
public class MobileUtil {

	/**
	 * 将含有"," 的手机号字符串，切割成String[]数组
	 * 
	 * @param mobileStr
	 * @return
	 */
	public static String[] changeToStrings(String mobileStr) {
		return mobileStr.split(",");
	}

	/**
	 * 将含有"," 的手机号字符串，切割成Set集合
	 * 
	 * @param mobileStr
	 * @return
	 */
	public static Set<String> changeToSet(String mobileStr) {
		Set<String> set = new HashSet<String>();
		String[] split = mobileStr.split(",");
		for(String mobile : split){
			if(StringUtils.isNotBlank(mobile)){
				set.add(mobile.trim());
			}
		}
		
		return set;
	}

	/**
	 * 校验逗号分隔符的手机号格式
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean checkMobiles(String mobiles) {
		String[] split = mobiles.split(",");
		for (String mobile : split) {
			if (StringUtils.isBlank(mobile) || !checkMobile(mobile.trim())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 校验手机号格式
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean checkMobile(String mobile) {
		return StringUtils.isNotBlank(mobile) && Pattern.matches("\\d{11}", mobile);
	}

}
