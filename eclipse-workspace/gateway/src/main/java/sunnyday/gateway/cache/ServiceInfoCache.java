package sunnyday.gateway.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunnyday.common.model.ServiceInfo;
import sunnyday.gateway.threadPool.Task;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 业务信息缓存
 * 
 * @author 1307365
 *
 */
@Service
public class ServiceInfoCache extends Task {
	private Logger log = CommonLogFactory.getLog(ServiceInfoCache.class);
	private static Map<String, ServiceInfo> service_info = new HashMap<String, ServiceInfo>();
	private static Map<String, String> add_msg = new ConcurrentHashMap<String, String>();
	
	@Override
	public void reloadCache() {
		load_service_info();
	}

	private boolean load_service_info() {
		try {
			Map<String, ServiceInfo>  tmp = dao.getServiceInfo();
			if(tmp!=null&&!tmp.isEmpty()){
				service_info = tmp ;
			}
		} catch (Exception e) {
		}
		String service_add_msg = null;
		try {
			//service_add_msg : sever1=msg1;server2=msg2
			service_add_msg = GateConfigCache.getGateConfigValue("service_add_msg");
			Map<String, String> tmp = new ConcurrentHashMap<String, String>();
		   if(service_add_msg!=null&&service_add_msg.length()>0){
		  	String[] array = service_add_msg.split(";");
		  	if(array.length>0){
		  		for(String str: array){
					String[] msg = str.split("=");
					if(msg.length==2){
						tmp.put(msg[0],msg[1]);
					}
				}
			}
			   add_msg = tmp ;
		 }
		}catch (Exception e) {
			log.error("service_add_msg: "+service_add_msg,e);
		}
		return true;
	}

	/**
	 * 通过业务代码返回业务信息
	 * 
	 * @param service_code
	 * @return
	 */
	public static ServiceInfo getServiceInfo(String service_code) {
		if(service_info!=null){
			return service_info.get(service_code);
		}
		return null;
	}

	public static  String getServiceAddMsg(String service_code){
		if(service_code!=null&&service_code.length()>0){
			String result = add_msg.get(service_code);
			return  result!=null?result:"";
		}
		return  "";
	}

public static  void main(String[] ar){
	try {
		//service_add_msg : sever1=msg1;server2=msg2
		String service_add_msg = "0=1；1=99";
		Map<String, String> tmp = new ConcurrentHashMap<String, String>();
		if(service_add_msg!=null&&service_add_msg.length()>0){
			String[] array = service_add_msg.split(";");
			if(array.length>0){
				for(String str: array){
					String[] msg = str.split("=");
					if(msg.length>1){
						tmp.put(msg[0],msg[1]);
					}
				}
			}
			add_msg = tmp ;
		}
		System.out.print("add_msg:"+add_msg);
	}catch (Exception e) {
		 e.printStackTrace();
	}
}

}
