package sunnyday.gateway.thread;

import java.io.File;


public class ReadSenderConfig {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File file = new File("config");
		if(file.exists()){
			for(File f : file.listFiles()){
				System.err.println(f.getName()+"_"+f.lastModified());
			//	System.err.println();
			}
				 
		}

	}
 
}
