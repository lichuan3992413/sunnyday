package sunnyday.gateway.util;

import java.util.List;
import java.util.regex.Matcher;

import org.slf4j.Logger;

import sunnyday.common.model.CheckMethod;
import sunnyday.common.model.UserBean;
import sunnyday.gateway.cache.LocationInfoCache;
import sunnyday.tools.util.CommonLogFactory;

public class CommonUntil {
	private static Logger log = CommonLogFactory.getCommonLog(CommonUntil.class);
	public static boolean  isChinaMobile(String mobile) {
		if(mobile!=null&&mobile.length()>3){
			if(mobile.startsWith("86")){
				mobile = mobile.substring(2,mobile.length());
			}else if (mobile.startsWith("+86")) {
				mobile = mobile.substring(3,mobile.length());
			}
		}
		List<CheckMethod> checkMethodList = LocationInfoCache.getCheckMethodList();
		if (null != checkMethodList && checkMethodList.size() > 0) {
			for (CheckMethod checkMethod : checkMethodList) {
				if (checkMethod.getCheck_code() != 4) {
					Matcher mat = (checkMethod.getPattern()).matcher(mobile);
					if (mat.find()) {
						return true;
					}
				}
			}
		}
		return false  ;
}

	public static String getPassword(UserBean userBean){
		
			int is_encrypt = 1;
			String is_encrypt_tmp = (String) userBean.getParamMap().get("is_encrypt");
			if(is_encrypt_tmp!=null){
				try{
				is_encrypt = Integer.parseInt(is_encrypt_tmp);
				}catch(Exception e){
					log.error("",e);
				}
			}
			EncodeResponse result = null;
			String pwd= userBean.getUser_pwd();
			if(is_encrypt==0){
				result = HSToolCode.decoded(pwd);
				if(result.isSuccess()){
					pwd = result.getContent();
				}
			}
		return pwd;
	}
	
	
	public static void main(String[] args) {
		System.out.println(isChinaMobile("15210962358"));;
	}
}
