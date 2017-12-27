package sunnyday.gateway.thread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunnyday.gateway.dc.DataCenter;
import sunnyday.gateway.util.FileUtil;
import sunnyday.gateway.util.SmsServiceFactory;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 动态控制启动哪些额外接口
 * @author 1307365
 *
 */
@Service
public class InterfaceControlThread extends Thread implements Stoppable{
	private Logger log = CommonLogFactory.getLog(InterfaceControlThread.class);
	private Logger monitor_log = CommonLogFactory.getLog("monitor");
	/**
	 * 外部接口动态加载启动
	 */
	private Map<String, Long> fileInfo = new HashMap<String, Long>();
	
	private boolean running = false;
    private String path =  System.getProperty("user.dir")+ "/sendApi" ;
	
	public InterfaceControlThread(){
		running = true;
	}
	
	public void run(){
		while(running){
			 try {
				 getFie();
			} catch (Exception e) {
			 	e.printStackTrace();
			}finally{
				try {
					sleep(10000L);//每5秒更新一次
				} catch (Exception e2) {
				}
			}
			 
		}
		
	}
	
	
	void getFie(){
		List<File> list = FileUtil.getFileList(path);
		for(File f:list){
			if(!f.getName().endsWith(".jar")){
				monitor_log.debug(f.getName()+" 非jar文件");
				continue;
			}
			String key = getFieKeyName(f);
			IControlService service = null;
			if(!fileInfo.containsKey(key)){
				//新的协议包加入时，启动该协议
				fileInfo.put(key, f.lastModified()) ;
			
				if(!DataCenter.controlServiceMap.containsKey(key)){
					service = SmsServiceFactory.inintServices(path,f.getName());
					service.doInit(key, key);
					service.doStart();
					DataCenter.controlServiceMap.put(key, service);
					monitor_log.info(key+" 接口启动！");
				}else {
					service = DataCenter.controlServiceMap.get(key);
					if(!service.checkIsStarted()){
						monitor_log.warn(key+" 接口尚未启动！");
					}
				}
			}else {
				if(f.lastModified()!=fileInfo.get(key)){
					//文件有更新，先关闭后更新
					if(DataCenter.controlServiceMap.containsKey(key)){
						fileInfo.put(key, f.lastModified()) ;
						service = DataCenter.controlServiceMap.get(key);
						service.doShutDown();
						DataCenter.controlServiceMap.remove(key);
						monitor_log.warn("版本变动，"+key+" 接口先进行关闭！");
						try {
							Thread.sleep(3000);
						} catch (Exception e) {
						} 
						service =SmsServiceFactory.inintServices(path,f.getName());
						service.doInit(key, key);
						service.doStart();
						DataCenter.controlServiceMap.put(key, service);
						monitor_log.info(key+" 接口重新启动！");
					}else{
						monitor_log.error(key+" 接口启动异常！");
					}
				}
			}
		
		}
	}
	
	private String getFieKeyName(File f){
		String tmp = f.getName();
		try {
			tmp =  tmp.split("-")[0];
		} catch (Exception e) {
			log.error("tmp: "+tmp,e) ;
		}
		return tmp ;
	}
	
	public static void main(String[] args) {
		/*HttpReceiveControlThread thread = new HttpReceiveControlThread() ;
		thread.getFie() ;*/
		
		 
	}
	
	public void shutDown(){
		running = false;
		interrupt();
	}

	public boolean doStop() {
		running = false;
		interrupt();
		return false;
	}

}
