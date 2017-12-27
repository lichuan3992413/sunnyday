package sunnyday.channel.thread;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sunnyday.channel.cache.DataCenter;
import sunnyday.channel.model.ControlModel;
import sunnyday.channel.model.ThreadControllerForm;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.MyClassLoader;
import sunnyday.tools.util.ParamUtil;

@Service
public class ControlThread extends Thread {
	
	private  Logger monitor_log = CommonLogFactory.getLog("monitor");
	private  Logger log = CommonLogFactory.getLog(ControlThread.class);
	private boolean running = true;
	@Autowired
	private SendRAO rao = null;
	 
	private String server_Ip = null;
	private Set<String> server_Ip_set_old = null;
	private int count = 0;
	/**
	 * 存放启动的线程
	 */
	private Map<String,IControlService> controlMap = new ConcurrentHashMap<String, IControlService>();
	private Map<String,ControlModel> control = new ConcurrentHashMap<String, ControlModel>();
	
	/**
	 * 记录每个服务器需要启动的 线程
	 */
	private Map<String,List<ThreadControllerForm>> controllerFormMap = new ConcurrentHashMap<String,List<ThreadControllerForm>>();
	 
	public void run(){
		
		if(monitor_log.isInfoEnabled()){
			monitor_log.info("ControlThread start");
		}
		 
		
		while (running) {
			try{
				List<ThreadControllerForm> list = rao.getNewThreadControllerInfoByKey(ParamUtil.REDIS_SET_THREAD_KEY);
				if(list!=null&&list.size()>0){
					controllerFormMap.clear();
					for(ThreadControllerForm form: list){
						String key=form.getServer_ip();
						if(key==null){
							log.debug("异常线程参数： td_code: "+form.getGroup_id()+"; key: "+key+"; name: "+form.getThread_name()+"; sn: "+form.getSn()); 
							continue ;
						}
						if(controllerFormMap.containsKey(key)){
							controllerFormMap.get(key).add(form);
						}else {
							List<ThreadControllerForm> array = new ArrayList<ThreadControllerForm>();
							array.add(form);
							controllerFormMap.put(key, array);
						}
					}
				}
				deal(list);
				if(count>10){
					sleep(60000);
				}else {
					count++;
					sleep(2000);
				}
				
			}catch (Exception e) {
				log.error("", e);
			}
			
			 
		}
		if(monitor_log.isInfoEnabled()){
			monitor_log.info("ControlThread stop");
		}
	}
	private void deal(List<ThreadControllerForm> list){
		if(server_Ip==null){
		    //程序初次启动
			server_Ip = DataCenter.getSend_server_ip_string();
			server_Ip_set_old=DataCenter.getSend_server_ip();
			
			while (server_Ip_set_old.isEmpty()) {
				server_Ip_set_old=DataCenter.getSend_server_ip();
				try {
					sleep(1000);
				} catch (Exception e) {
				}
			}
			 
			dosomething(server_Ip_set_old);
			monitor_log.info("1.dosomething server_Ip_set_old ["+server_Ip_set_old+"]");
		}else {
			if(server_Ip.equals(DataCenter.getSend_server_ip_string())){
				monitor_log.debug("2.dosomething server_Ip_set_old ["+server_Ip_set_old+"]");
				controlThread(server_Ip_set_old);
			}else {
				dosomething(server_Ip_set_old,DataCenter.getSend_server_ip());
				monitor_log.info("3.dosomething server_Ip_set_old ["+server_Ip_set_old+"],["+DataCenter.getSend_server_ip()+"]");
				server_Ip = DataCenter.getSend_server_ip_string();
				server_Ip_set_old=DataCenter.getSend_server_ip();
			}
		}
		
	}
	private void  controlThread(Set<String> ips){
		if(ips!=null&&!ips.isEmpty()){
			for (String ip:ips){
				List<ThreadControllerForm> list = controllerFormMap.get(ip);
				controlThread(list);
			}
		}
		
	}
	private void dosomething(Set<String> old_ips,Set<String> new_ips){
		for(String each:old_ips){
			if(!new_ips.contains(each)){
				dosomething(each,1);
			}
		}
		
		for(String each:new_ips){
			if(!old_ips.contains(each)){
				dosomething(each,0);
			}
		}
		
	}
	
	private void dosomething(Set<String> old_ips){
		for(String each:old_ips){
			dosomething(each,0);
		}
	}
	/**
	 * 
	 * @param ip
	 * @param type 0 开启；1 关闭
	 */
	void dosomething(String ip,int type){
		List<ThreadControllerForm> list = controllerFormMap.get(ip);
		if(list!=null&&list.size()>0){
			for(ThreadControllerForm t:list){
				String key = t.getGroup_id()+"_"+t.getThread_name();
				switch (type) {
				case 0://开启新的
					if(t.getAction()==0){
						ControlModel model = new ControlModel();
						model.setKey(key);
						model.setStart(true);
						model.setStop(false);
						control.put(key, model);
						toStart(t);
						monitor_log.debug("dosomething start["+key+"]");
					}
					break;
                case 1://关闭原有的
                	control.remove(key);
                	toStop(t);
                	monitor_log.debug("dosomething stop["+key+"]");
					break;
				default:
					break;
				}
			}
		}
	}
	
	private void toStop(ThreadControllerForm t){
		try {
			String key = t.getGroup_id()+"_"+t.getThread_name();
			if(controlMap.containsKey(key)){
				//准备关闭
				IControlService controlService = controlMap.get(key);
				if(controlService != null && controlService.checkIsStarted()){
					controlService.doShutDown();
					monitor_log.info(key+" is  stoped . ");
				}
				controlMap.remove(key);
				
			}
		} catch (Exception e) {
			log.error("", e);
		}
		
	}
	
	private void toStart(ThreadControllerForm t){
		try {
			String key = t.getGroup_id()+"_"+t.getThread_name();
			IControlService controlService = null;
			if(!controlMap.containsKey(key)){
				// 线程名字
				String thread_name = t.getThread_name();
				// 加载路径
				String loadURL = "file:" + System.getProperty("user.dir");
				// &.开头代表在lib/com.hskj下加在类,否则在sendApi/下的jar包中加载
				if(thread_name.startsWith("&.")){
					//处理&.
					thread_name = thread_name.substring(2);
					loadURL += "/lib/"+thread_name;
				}else{
//					loadURL += "/sendApi/" + FileUtil.fuzzySearchFile(System.getProperty("user.dir") + "/sendApi", thread_name);
				}
				
				// 类加载器
				@SuppressWarnings("resource")
				MyClassLoader classLoader = new MyClassLoader(new URL[]{new URL(loadURL)});
				// 加载类，得到IControlService对象
				controlService  = (IControlService)classLoader.loadClass(thread_name).newInstance();
				 
				
				// 启动线程
				if (controlService != null) {
					//把thread_id改为group_id避免造成重复
					controlService.doInit(t.getThread_param(), t.getGroup_id());
					controlMap.put(key, controlService);
					controlService.doStart();
					monitor_log.info(key+" is start. ");
				}
				
			}else{
				controlService = controlMap.get(key);
			}
			
			// 如果线程为正常运行，则打印ERROR日志
			if(controlService != null && !controlService.checkIsStarted()){
				monitor_log.error(key+" is not started. ");
			}else {
				//monitor_log.info("now "+key+" is started. ");
			}
		
		} catch (Exception e) {
			log.error("", e);
		}
		
	}
	
	private void controlThread(List<ThreadControllerForm> list) {
		if(list!=null){
			for (ThreadControllerForm t : list) {
				String key = t.getGroup_id()+"_"+t.getThread_name();
				monitor_log.debug("controlThread-> "+t.getServer_ip()+", "+t.getGroup_id()+", action: "+t.getAction());
				switch (t.getAction()) {
				case 0:
					if(control.containsKey(key)){
						ControlModel model = control.get(key);
						/**
						 * 防止重复启动，同时不去启动准备关闭的线程
						 */
						if(!model.isStart()&&!model.isStop()){
							//启动线程
							toStart(t);
						}else {
							//检测线程是否已经正常启动
							IControlService controlService = controlMap.get(key);
							if(controlService==null||!controlService.checkIsStarted()){
								monitor_log.error(key+" is not started. ");
							} 
						}
					}else {
						//首次启动
						ControlModel model = new ControlModel();
						model.setKey(key);
						model.setStart(true);
						model.setStop(false);
						control.put(key, model);
						monitor_log.info("controlThread start["+key+"]");
						toStart(t);
						
					}
					break;
                 case 1:
                	 if(control.containsKey(key)){
                		 ControlModel model = control.get(key);
                		 if(!model.isStop()){
                			 monitor_log.info("controlThread stop["+key+"]");
                			toStop(t);
                			model.setStop(true);
                		 }else {
                			 IControlService controlService = controlMap.get(key);
                			 if(controlService!=null&&controlService.checkIsStarted()){
                				 monitor_log.error(t.getThread_name()+" is not  stoped. ");
                			 }
						}
 						control.remove(key);
                	 }else {
                		 //没有在已经启动的线程中，所以不需要关闭。
					}
					break;

				default:
					break;
				}
			}
		}
	}

	 

	public boolean doStop() {
		
		if(controlMap!=null){
			for(String key:controlMap.keySet()){
				ControlModel model = control.get(key);
				if(model==null){
					monitor_log.info(key+" is null. ");
					continue ;
				}
					
				model.setStop(true);
				IControlService controlService = controlMap.get(key);
				if(controlService != null && controlService.checkIsStarted()){
					controlService.doShutDown();
				}
				controlMap.remove(key);
			}
		}
		running = false;
		interrupt();
		return true;
	}

}
