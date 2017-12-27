package sunnyday.controller.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import sunnyday.tools.util.CommonLogFactory;

public class ConfigPropertiesUtil {
	private static final Logger log = CommonLogFactory.getCommonLog("infoLog");
	private static Properties prop = new Properties();
	private static String filePath = "";
	private static String path = "";
	private static String name = "";
	static {
		path =  System.getProperty("user.dir")+ "/config/";
		name = "dealdata.config.properties";
		filePath = path + name;
		loadProperties();
	}

	private static void loadProperties() {
		InputStream inStream = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				boolean isNew = new File(path, name).createNewFile();
				log.debug("path[" + path + "] name[" + name + "] create " + isNew);
			}
			inStream = new FileInputStream(file);
			prop.load(inStream);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				if(inStream != null){
					inStream.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	public static String getPropertie(String key) {
		String value = prop.getProperty(key);
		if(value!=null){
			return value.trim();
		}
		return  null;
	}
	
	public static String getMachineCode() {
		String value = prop.getProperty("machine_code");
		if(value!=null){
			return value.trim();
		}
		return  "1";
	}
}
