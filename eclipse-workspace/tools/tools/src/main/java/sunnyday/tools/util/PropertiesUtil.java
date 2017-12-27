package sunnyday.tools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

public class PropertiesUtil {
	
	private static Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	private static Properties prop = new Properties();
	private static String filePath = "";
	private static String path = "";
	private static String name = "";
	
	private static final String MASTER_SLAVE_PROP_FILENAME = "masterSlave.properties";
	
	static {
		path = System.getProperty("user.dir") + "/config/";
		name = "db.properties";
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
	
	public static Properties loadMasterSlaveProperties() {
		return loadProperties(MASTER_SLAVE_PROP_FILENAME);
	}
	
	public static Properties loadProperties(String propFileName) {
		Properties prop = new Properties();
		InputStream inStream = null;
		try {
			File file = new File(path + propFileName);
			if (file.exists()) {
				inStream = new FileInputStream(file);
				prop.load(inStream);
			}
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
		
		return prop;
	}

	public static String getPropertie(String key) {
		return prop.getProperty(key);
	}
}
