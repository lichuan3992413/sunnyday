package sunnyday.controller.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.AutoSendMsgConfig;
import sunnyday.common.model.AutoSendMsgContent;
import sunnyday.tools.util.ParamUtil;
@Service
public class AutoSendMsgConfigCache extends Cache {
	
	private static Map<Integer, List<AutoSendMsgConfig>> commandModelMap = new HashMap<Integer, List<AutoSendMsgConfig>>();
	
	private static Map<String, List<AutoSendMsgContent>> autoSendMsgContentMap = new HashMap<String, List<AutoSendMsgContent>>();
	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		List<AutoSendMsgConfig>  tmp =  (List<AutoSendMsgConfig>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_AUTO_SEND_MSG_CONFIG);
		if(tmp!=null&&!tmp.isEmpty()){
			Map<Integer, List<AutoSendMsgConfig>> map = new HashMap<Integer, List<AutoSendMsgConfig>>() ;
			for(AutoSendMsgConfig bean :tmp){
				if(!map.containsKey(bean.getType())){
					List<AutoSendMsgConfig> list = new ArrayList<AutoSendMsgConfig>();
					map.put(bean.getType(), list);
				}
				map.get(bean.getType()).add(bean);
				
			}
			commandModelMap = map ;
		}
		
		List<AutoSendMsgContent>  resultTemp =  (List<AutoSendMsgContent>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_AUTO_SEND_MSG_CONTENT);
		if(tmp!=null&&!tmp.isEmpty()){
			Map<String, List<AutoSendMsgContent>> map = new HashMap<String, List<AutoSendMsgContent>>() ;
			for(AutoSendMsgContent bean : resultTemp){
				if(!map.containsKey(bean.getKeyword())){
					List<AutoSendMsgContent> list = new ArrayList<AutoSendMsgContent>();
					map.put(bean.getKeyword(), list);
				}
				map.get(bean.getKeyword()).add(bean);
				
			}
			autoSendMsgContentMap = map ;
		}
		
		
		return true;
	}

 
	public static Map<Integer, List<AutoSendMsgConfig>> getDeliverCommandInfo(){
		return commandModelMap;
	}
	 
	public static List<AutoSendMsgContent> getAutoSendMsgCotent(String keyword){
		if(autoSendMsgContentMap!=null){
		 return  autoSendMsgContentMap.get(keyword);
		}
		return null;
	}
}
