package sunnyday.controller.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

public class Md5Util {
	private static Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	public static String Md5(String src){
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(src.getBytes());
			byte [] dest = md.digest();
			return new String(dest);
			
		} catch (Exception e) {
			log.error("", e);
			return null;
		} 
	}
	public static String md5(byte b[])throws NoSuchAlgorithmException{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(b, 0, b.length);
		return byteArrayToHexString(md5.digest());
	}

	public static byte[] md5(String data)throws NoSuchAlgorithmException, UnsupportedEncodingException{
	    MessageDigest md5 = MessageDigest.getInstance("MD5");
	   return md5.digest(data.getBytes("UTF-8"));
	}
	
	public static String Md5_32(String plainText) {
	    try {
	    	MessageDigest md = MessageDigest.getInstance("MD5");
	    	md.update(plainText.getBytes());
	    	byte b[] = md.digest();
	    	int i;
	    	StringBuffer buf = new StringBuffer("");
	    	for (int offset = 0; offset < b.length; offset++) {
	    		i = b[offset];
	    		if (i < 0)
	    			i += 256;
	    		if (i < 16)
	    			buf.append("0");
	    		buf.append(Integer.toHexString(i));
	    	}
//          System.out.println("result: " + buf.toString());// 32位的加密
//	    	return buf.toString().substring(8, 24);// 16位的加密
	    	return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error("", e);
		}
		return "";
	}
	private static String byteArrayToHexString(byte b[]){
	    StringBuffer sb = new StringBuffer();
	    for(int i = 0; i < b.length; i++)
	        sb.append(byteToHexString(b[i]));
	
	    return sb.toString();
	}
	
	private static String byteToHexString(byte b){
	    int n = b;
	    if(n < 0)
	        n = 256 + n;
	    int d1 = n / 16;
	    int d2 = n % 16;
	    return hexDigits[d1] + hexDigits[d2];
	}
	
	private static String hexDigits[] = {
	    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
	    "a", "b", "c", "d", "e", "f"
	};
}
