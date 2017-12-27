package sunnyday.gateway.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

public class MyClassLoader extends URLClassLoader{
	private static Logger log = CommonLogFactory.getLog(MyClassLoader.class);
	public MyClassLoader() {
		super(new URL[0]);
	}
	
	public MyClassLoader(URL[] urls) {
		super(urls);
	}

	public void addJar(URL url) {
		this.addURL(url);
	}
	
	public void loadJar(String str){
		try {
			URL url = new URL(str);
			addURL(url);
		} catch (MalformedURLException e) {
			log.error("",e);
		}
	}
	
	public Class<?> findClass(String name) throws ClassNotFoundException{
		return super.findClass(name);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return super.loadClass(name);
	}
}