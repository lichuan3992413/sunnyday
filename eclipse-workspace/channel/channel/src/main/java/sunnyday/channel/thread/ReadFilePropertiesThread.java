package sunnyday.channel.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunnyday.channel.cache.DataCenter;
import sunnyday.tools.util.CommonLogFactory;


/**
 * 动态读取配置文件信息
 * @author 1307365
 *
 */
@Service
public class ReadFilePropertiesThread extends Thread {
	
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	private final String KEY = "UTF8_USER_IP";
	private final String KEY_WITH_86 = "WITH_86_USER";
	private final String KEY_SEND_SERVER_IP = "send_server_ip";
	private final String KEY_USE_CODE_0 = "use_code_0";
	private boolean running = true;
	private String filePath = "";
	private String path = "";
	private String name = "";
	private long last_load_prop_time = 0;
	private Properties prop = new Properties();

	public ReadFilePropertiesThread() {
		path = System.getProperty("user.dir") + "/config/";
		name = "utf8.users";
		filePath = path+name;
	}

	@Override
	public void run() {
		System.out.println("==========ReadFilePropertiesThread  is start=======");
		while (running) {
			try {
				if (System.currentTimeMillis() - last_load_prop_time >30000L) {
					loadProperties();
					last_load_prop_time = System.currentTimeMillis();
				} else {
					sleep(10000);
				}
			} catch (Exception e) {
				log.error("", e);
			}

		}
	}

	// 每30秒重新加载一次配置文件，方便参数及时生效
	private void loadProperties() {
		InputStream inStream = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				boolean isNew = new File(path,name).createNewFile();
				log.debug("file path[" + path + "] name[" + name + "] create {" + isNew + "}");
			}
			
			inStream = new FileInputStream(file);
			prop.load(inStream);
			initUtf8UserSet(KEY);
			initUserWith86(KEY_WITH_86);
			initSendServerIp(KEY_SEND_SERVER_IP);
			initUseCode0(KEY_USE_CODE_0);
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

	private void initUseCode0(String key) {
		if (prop != null && !prop.isEmpty()) {
			String values = prop.getProperty(key);
			if (values != null && !"".equals(values)) {
				String[] array = values.split(",");
				if (array != null && array.length > 0) {
					Set<String> use_code_0 = new HashSet<String>();
					for (int i = 0; i < array.length; i++) {
						use_code_0.add(array[i]);
					}
					DataCenter.setUse_code_0(use_code_0);
				}
			}
		}
	}
	
	 
	
	private void initSendServerIp(String key) {
		if (prop != null && !prop.isEmpty()) {
			String values = prop.getProperty(key);
			DataCenter.setSend_server_ip_string(values);
			if (values != null && !"".equals(values)) {
				String[] array = values.split(",");
				if (array != null && array.length > 0) {
					Set<String> server_ip = new HashSet<String>();
					for (int i = 0; i < array.length; i++) {
						server_ip.add(array[i]);
					}
					DataCenter.setSend_server_ip(server_ip);
				}
			}
		}
	}

	// 把配置文件中的值转换成所需要的数据类型
	private void initUtf8UserSet(String key) {
		if (prop != null && !prop.isEmpty()) {
			String values = prop.getProperty(key);
			if (values != null &&! "".equals(values)) {
				String[] array = values.split(",");
				if (array != null && array.length > 0) {
					Set<String> tmp_utf8_user = new HashSet<String>();
					Set<String> tmp_utf8_user_other = new HashSet<String>();
					for (int i = 0; i < array.length; i++) {
						if(array[i].contains("*")){
							tmp_utf8_user_other.add(array[i]);
						}else {
							tmp_utf8_user.add(array[i]);
						}
						
					}
					DataCenter.setUtf8_user_set(tmp_utf8_user);
					DataCenter.setOther_user_set(tmp_utf8_user_other);
				}
			}
			
		}
	}

	private void initUserWith86(String key) {
		if (prop != null && !prop.isEmpty()) {
			String values = prop.getProperty(key);
			if (values != null && !"".equals(values)) {
				String[] array = values.split(",");
				if (array != null && array.length > 0) {
					Set<String> with_86_user = new HashSet<String>();
					for (int i = 0; i < array.length; i++) {
						with_86_user.add(array[i]);
					}
					DataCenter.setWith_86_user(with_86_user);
				}
			}
		}

	}
	public void shutDown() {
		running = false;
		interrupt();
	}

	public boolean doStop() {
		running = false;
		interrupt();
		return true ;
	}
}
