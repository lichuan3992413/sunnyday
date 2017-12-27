package sunnyday.gateway.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;


import sunnyday.tools.util.CommonLogFactory;

public class PropertiesUtil {
	private static Logger log = CommonLogFactory.getLog(PropertiesUtil.class);
	private static Properties prop = new Properties();
	private static String filePath = "";
	private static String path = "";
	private static String name = "";
	static {
		path = System.getProperty("user.dir") + "/config/";
		name = "receiver.config.properties";
		filePath = path + name;
		loadProperties();
	}

	private static void loadProperties() {
		InputStream inStream = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				boolean flag = new File(path, name).createNewFile();
				if(!flag){
					log.error(path+name+",创建失败！");
				}
			}
			inStream = new FileInputStream(file);
			prop.load(inStream);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			try {
				if(inStream!=null){
					inStream.close();	
				}
			} catch (Exception e) {
			}
		}
	}

	public static String getPropertie(String key) {
		return prop.getProperty(key);
	}
}
