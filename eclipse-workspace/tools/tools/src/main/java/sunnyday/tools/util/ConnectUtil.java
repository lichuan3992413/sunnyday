package sunnyday.tools.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;


public class ConnectUtil {
	
	private static final Logger log = CommonLogFactory.getLog("infoLog");
	
	/**
	 * 对象序列化成String
	 * @param object
	 * @return
	 */
	public static String Object2String(Object object) {
		String result = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			new ObjectOutputStream(bos).writeObject(object);
			byte[] t = bos.toByteArray();
			bos.flush();
			bos.reset();
			result = new String(t);
		} catch (Exception e) {
			log.error("", e);
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
			log.error("", e);
		}finally{
			try {
				bos.close();
			} catch (IOException e) {
				log.error("", e);
			}
		}
		return null;
	}
	
	public static byte[][] Object2Byte(List<Object> list) {
		byte[][] total = new byte[list.size()][];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				for(int i = 0; i < list.size(); i++){
					Object obj = list.get(i);
					try {
						new ObjectOutputStream(bos).writeObject(obj);
						byte[] t = bos.toByteArray();
						bos.flush();
						bos.reset();
						total[i] = t;
					} catch (IOException e) {
						log.error("", e);
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}finally{
				try {
					bos.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		return total;
	}
	
	public static Object Byte2Object(byte[] array){
		Object obj = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(array);
		try {
			obj = new ObjectInputStream(bis).readObject();
			 
		} catch (IOException e) {
			log.error("", e);
		} catch (ClassNotFoundException e) {
			log.error("", e);
		}finally{
			try {
				bis.close();
			} catch (IOException e) {
				log.error("", e);
			}
		}
		return obj;
	}
	
	public static Object Byte2Object(List<byte[]> list){
		List<Object> result = new ArrayList<Object>();
		 if(list!=null&&list.size()>0){
			 for(byte[] array:list){
				 Object object=Byte2Object(array);
				 if(object!=null){
					result.add(object);
				 }
			 }
		 }
		return result;
	}
	
	
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

//	public static String getNowIp(String user_id) {
//		String ip = "";
//		UserBean user = UserBeanCache.getUserBean(user_id);
//		if(null != user){
//			ip = user.getUser_ip();
//		}
//		return ip;
//	}

}
