package sunnyday.tools.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

public class CommonLogFactory {
	static{
		String myDir = System.getProperty("my.dir");
		if(myDir == null || "".equals(myDir.trim())){
			myDir = System.getProperty("user.dir");
		}
		String path= myDir + "/config/logback.xml";
		 File logbackFile = new File(path);
	        if (logbackFile.exists()) {
	            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
	            JoranConfigurator configurator = new JoranConfigurator();
	            configurator.setContext(lc);
	            lc.reset();
	            try {
	                configurator.doConfigure(logbackFile);
	            }
	            catch (Exception e) {
	                e.printStackTrace(System.err);
	                System.exit(-1);
	            }
	        }else {
	        	 System.out.println("日志配置文件["+path+"]不存在!");
			}
	
	}

	public static Logger getLog(Class<?> clazz){
		return LoggerFactory.getLogger(clazz);
	}

	public static Logger getLog(String name){
		
		return LoggerFactory.getLogger(name);
	}
	
	public static Logger getCommonLog(Class<?> clazz){
		return LoggerFactory.getLogger(clazz);
	}

	public static Logger getCommonLog(String name){
		return LoggerFactory.getLogger(name);
	}


}
