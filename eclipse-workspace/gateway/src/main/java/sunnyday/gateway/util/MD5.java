package sunnyday.gateway.util;

import java.security.MessageDigest;

public class MD5 {
	 public static String convert(String s){ 
	        char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}; 
	        try { 
	            byte[] bytes = s.getBytes(); 
	            MessageDigest md = MessageDigest.getInstance("MD5"); 
	            md.update(bytes); 
	            bytes = md.digest(); 
	            int j = bytes.length; 
	            char[] chars = new char[j * 2]; 
	            int k = 0; 
	            for (int i = 0; i < bytes.length; i++) { 
	                byte b = bytes[i]; 
	                chars[k++] = hexChars[b >>> 4 & 0xf]; 
	                chars[k++] = hexChars[b & 0xf]; 
	            } 
	            return new String(chars); 
	        } 
	        catch (Exception e){ 
	            return null; 
	        } 


	 }
	 public static void main(String [] args){
//		 System.out.println(convert("test66|test66|123456|20100714040605"));
//		 System.out.println("9f63317352ca4e15493d2b7139b4a1f5");
		 System.out.println(MD5.convert( "Sms@yto.net.cn"));
	 }
	 
	 
	 
	
}
