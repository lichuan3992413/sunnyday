package sunnyday.gateway.util;

import org.slf4j.Logger;

import sunnyday.gateway.cache.GateConfigCache;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 1.加密解密工具，该工具不同的服务器所依赖的key文件不同
 * 使用该工具时，不同的服务器需要配置不同的key文件，现规定
 * 所有的key文件都放置在config目录中<br>
 * 2.密钥的制作方法参考resources文件夹中的word文档
 * @author 1307365
 *
 */
public class HSToolCode {
	private static Logger log = CommonLogFactory.getCommonLog(HSToolCode.class);
	private static final String MOBILE_LETTER = "X" ;
	
	 

	/**
	 * 采用加密工具进行加密，若执行异常，则不进行加密处理
	 * @param key
	 * @return
	 */
	public static EncodeResponse encoded(String key) {
		EncodeResponse response = new EncodeResponse();
		 try {
         	String content = PassWordUtil.encrypt(key);
         	response.setSuccess(true);
			response.setContent(content);
         	return  response;
			} catch (Exception e) {
				response.setSuccess(false);
				response.setContent(key);
				log.warn("加密异常，不进行加密["+key+"]",e);
				return response;
			}

	}

	/**
	 * 采用解密工具进行解密，若执行异常，则不进行解密
	 * @param key
	 * @return
	 */
	public static EncodeResponse decoded(String key) {
		EncodeResponse response = new EncodeResponse();
		try {
			String content = PassWordUtil.decrypt(key);
			response.setSuccess(true);
			response.setContent(content);
			return response;
		} catch (Exception e) {
			response.setSuccess(false);
			response.setContent(key);
			log.warn("解密异常，不进行解密["+key+"]",e);
			return response;
		}

	}
	
	//是否进行日志脱敏
	public static boolean isFilterMobile(){
		String tmp= GateConfigCache.getGateConfigValue(ParamUtil.IS_FILTET_MOBILE);
		if(tmp!=null&&"no".equals(tmp)){
			return false;
		}
		return true;
	}
	//是否进行幂等处理
	public static boolean isDoIdempotent(){
		String tmp= GateConfigCache.getGateConfigValue("is_do_idempotent");
		if(tmp!=null&&"yes".equals(tmp)){
			return true;
		}
		return false;
	}
	
	/**
	 * 普通老接口，是否自动追加下行扩展,默认追加
	 * @return
	 */
	public static boolean isUserAutoExt(){
		String tmp= GateConfigCache.getGateConfigValue("is_user_auto_ext");
		if(tmp!=null&&"no".equals(tmp)){
			return false;
		}
		return true;
	}
	
	/**
	 * 根据模板ID判断改模板是否使用自动扩展
	 * @param template_id
	 * @return
	 */
	public static boolean isTemplateAutoExt(String template_id){
		String tmp= GateConfigCache.getGateConfigValue("is_user_auto_ext");
		if(tmp!=null&&"no".equals(tmp)){
			return false;
		}
		return true;
	}
	
	/**
	 * 获取自动追加TD退订的内容
	 * @param ext
	 * @return
	 */
	public static String getTemplateAutoTDContent(String ext){
		String tmp= GateConfigCache.getGateConfigValue("template_auto_td_content");
		if(tmp==null||"".equals(tmp)){
			tmp ="回复TD${code}退订";
		}
		return tmp.replace("${code}", ext);
	}
	
	/**
	 * 获取幂等过滤时的是时间限制，单位秒
	 * @return
	 */
	public static int getIdempotentTimeOut(){
		int result = 60;
		String tmp= GateConfigCache.getGateConfigValue("idempotent_timeout");
		if(tmp!=null&&!"".equals(tmp)){
			try {
				result = Integer.parseInt(tmp.trim());
			} catch (Exception e) {
			}
		}
		return result;
	}
	
	/**
	 * 获取sign值的有效期
	 * @return
	 */
	public static int getSignExpiryDate(){
		int result = 5;
		String tmp= GateConfigCache.getGateConfigValue("sign_expiry_date");
		if(tmp!=null&&!"".equals(tmp)){
			try {
				result = Integer.parseInt(tmp.trim());
			} catch (Exception e) {
			}
		}
		return result;
	}
	
	public static String filterMobile(final String  mobile) {
		String tmp= mobile;
		if(mobile!=null&&isFilterMobile()){
			if(mobile.length()>=11){
				int end = mobile.length() - 4 ;
				int start = mobile.length() - 8;
				tmp = mobile.substring(0, start)+getLitter(4)+mobile.substring(end, mobile.length());
			}else if (mobile.length()>=8){
				int end = mobile.length() - 3 ;
				int start = mobile.length() - 6;
				tmp = mobile.substring(0, start)+getLitter(3)+mobile.substring(end, mobile.length());
			}else if (mobile.length()>=5){
				int end = mobile.length() - 2 ;
				int start = mobile.length() - 4;
				tmp = mobile.substring(0, start)+getLitter(2)+mobile.substring(end, mobile.length());
			}
		}
		return tmp ;
	}
	
	public static  String getLitter(int count){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<count;i++){
			sb.append(MOBILE_LETTER);
		}
		return sb.toString();
	}
	public static void main(String[] args) {
		// String string = "B88llkF7TXk8qg3AsalHcabeMNxAIzLpEIgNhodi15k=";
		 System.err.println(filterMobile("15210962358"));
	 
		//System.err.println("解密后： "+HSToolCode.decoded(string));
	}
}
