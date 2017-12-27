package sunnyday.gateway.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sunnyday.common.model.UserBean;
import sunnyday.gateway.cache.UserBeanCache;
import sunnyday.tools.util.CommonLogFactory;

public class ConnectUtil {
	private static Logger log = CommonLogFactory.getCommonLog(ConnectUtil.class);
	public static boolean isRequestIpError(String ip, String request_ip) {
		boolean result = true;
		if(ip != null){
			if(ip.trim().equals("")){
				result = false;
			}else{
				String[] ips = ip.trim().split(",");//绑定多个ip用英文逗号分隔开
				for(String ip_every : ips){
					ip_every = ip_every.replace(".", "\\.").replace("*", "[0-9]{1,3}");
					if( request_ip.matches(ip_every)){
						result = false;
						break;
					}
				}
			}
		}
		return result;
	}

	public static String getReqIp(SelectionKey key) {
		String request_ip = "";
		SocketChannel socketChannel = (SocketChannel)key.channel();
		if(null!=socketChannel){
			request_ip = socketChannel.socket().getInetAddress().getHostAddress();
		}
		return request_ip;
	}

	public static String getNowIp(String user_id) {
		String ip = "";
		UserBean user = UserBeanCache.getUserBean(user_id);
		if(null != user){
			ip = user.getUser_ip();
		}
		return ip;
	}

	// Base64加密
	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("utf-8");
		} catch (Exception e) {
			log.error("",e);
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		return s;
	}

	// Base64解密
	public static String getFromBase64(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				log.error("",e);
			}
		}
		return result;
	}
	
	/**
	 * 对象序列化成Byte
	 * @param object
	 * @return
	 */
	public static byte[] Object2Byte(Object object) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			new ObjectOutputStream(bos).writeObject(object);
			byte[] t = bos.toByteArray();
			bos.flush();
			bos.reset();
			return t ;
		} catch (Exception e) {
			log.error("",e);
		}finally{
			try {
				bos.close();
			} catch (IOException e) {
				log.error("",e);
			}
		}
		return null;
	}
	
	public static Object Byte2Object(byte[] array){
		Object obj = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(array);
		try {
			obj = new ObjectInputStream(bis).readObject();
			 
		} catch (IOException e) {
			log.error("",e);
		} catch (ClassNotFoundException e) {
			log.error("",e);
		}finally{
			try {
				bis.close();
			} catch (IOException e) {
				log.error("",e);
			}
		}
		return obj;
	}
}
