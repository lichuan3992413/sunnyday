package sunnyday.adapter.redis.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

public class Utils {
	private static Logger info = CommonLogFactory.getLog(Utils.class);
 
	public static byte[] toByteArray(Object objValue) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		new ObjectOutputStream(bos).writeObject(objValue);
		byte[] t = bos.toByteArray();
		return t;
	}

	public static Object toObject(byte [] array){
		Object obj = null;
		if (null == array)return null;
		ByteArrayInputStream bis = new ByteArrayInputStream(array);
		try {
			obj = new ObjectInputStream(bis).readObject();
		} catch (IOException e) {
			info.error("",e);
		} catch (ClassNotFoundException e) {
			info.error("",e);
		}
		return obj;
	}
	
	public static String FirstUpperCase(String columnName) {
		String result = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
		return result;
	}
	
	 
		 
}
