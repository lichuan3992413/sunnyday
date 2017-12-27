package sunnyday.tools.util;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Spring {
	
	private static ApplicationContext apx = null;
	
	public static void initSpring(String configPath){
		if(apx==null){
			apx = new ClassPathXmlApplicationContext(configPath);
		}
	}
	
	public static void initFileSystemSpring(String configPath){
		if(apx==null){
			System.out.println("file:" + configPath);
			apx = new FileSystemXmlApplicationContext("file:" + configPath);
		}
	}
	
	/**
	 * 动态加载注解bean
	 * 
	 * @param classLoader
	 * @param basePackages
	 */
	public static void initSpring(ClassLoader classLoader, String... basePackages) {
		if(classLoader != null && basePackages != null && apx != null){
			if(apx instanceof ClassPathXmlApplicationContext){
				ClassPathXmlApplicationContext springCtx = (ClassPathXmlApplicationContext) apx;
		    	new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) springCtx.getBeanFactory()).scan(basePackages);
		    	springCtx.getBeanFactory().setBeanClassLoader(classLoader);
			}else{
				FileSystemXmlApplicationContext springCtx = (FileSystemXmlApplicationContext) apx;
		    	new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) springCtx.getBeanFactory()).scan(basePackages);
		    	springCtx.getBeanFactory().setBeanClassLoader(classLoader);
			}
		}
	}

	public static ApplicationContext getApx() {
		return apx;
	}

	public static void setApx(ApplicationContext apx) {
		Spring.apx = apx;
	}
}
