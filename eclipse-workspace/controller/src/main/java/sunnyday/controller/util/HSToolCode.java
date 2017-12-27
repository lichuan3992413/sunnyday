package sunnyday.controller.util;

import org.slf4j.Logger;

import sunnyday.controller.cache.GateConfigCache;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;

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
	public static boolean isFilterMobile(){
		String tmp= GateConfigCache.getValue(ParamUtil.IS_FILTET_MOBILE);
		if(tmp!=null&&"no".equals(tmp)){
			return false;
		}
		return true;
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
		String string = "B88llkF7TXk8qg3AsalHcabeMNxAIzLpEIgNhodi15k=";
		
	 
		System.err.println("解密后： "+HSToolCode.decoded(string));
	}
}
