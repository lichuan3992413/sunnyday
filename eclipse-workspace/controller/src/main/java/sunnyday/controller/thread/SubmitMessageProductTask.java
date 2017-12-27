package sunnyday.controller.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import sunnyday.common.model.SmsMessage;
import sunnyday.controller.util.SendRAO;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;

/**
 * 将短信列表写入发送端redis队列(q:send:td_code:user_id)中
 */
public class SubmitMessageProductTask   implements   Callable<Integer>  {
	private Logger info_log = CommonLogFactory.getCommonLog("infoLog");
	private String send_queue_name ;
	private SendRAO rao  ;
	private List<SmsMessage> list ;
	
	public SubmitMessageProductTask (String send_queue_name,SendRAO rao,List<SmsMessage> list){
		this.send_queue_name = send_queue_name;
		this.rao = rao;
		this.list = list;
	}
	  

	public Map<String, List<Object>> dealList(List<SmsMessage> list) {
		Map<String, List<Object>> map = new HashMap<String, List<Object>>();
  		if (list != null && list.size() > 0) {
  			Set<String> td_code_set = new HashSet<String>();
			for (SmsMessage sms : list) {
				String queue = send_queue_name.replace("td_code", sms.getTd_code()+":"+sms.getUser_id());
				td_code_set.add(send_queue_name.replace("td_code", sms.getTd_code()));
				if (!map.containsKey(queue)) {
					map.put(queue, new ArrayList<Object>());
				}
				map.get(queue).add(sms);
			}
			try {
				rao.HSSADD(ParamUtil.REDIS_SET_SEND_KEY, td_code_set);
			} catch (Exception e) {
				info_log.warn("dealList-HSSADD",e);
			}
			
		}
		return map;
	}
	
	public void writeList(Map<String, List<Object>> map) {
		if (map != null && !map.isEmpty()) {
			for (String key : map.keySet()) {
				long time1 = System.currentTimeMillis();
				try {
					rao.HSSADD(ParamUtil.REDIS_SET_SEND_KEY, key);
				} catch (Exception e) {
					info_log.warn("writeList-HSSADD",e);
				}
				boolean result = false ;
				try {
					result = rao.submitMessageToDc(key, map.get(key));
				} catch (Exception e) {
					info_log.warn("writeList-submitMessageToDc",e);
				}
				long time2 = System.currentTimeMillis();
				if (!result) {
					DataCenter_old.setRedis_is_ok(false);
					//放入待写入的文件中
					DataCenter_old.addSubmitListDoneObjectCache(map.get(key));
					info_log.warn("lost sms [" + map.get(key) + "] , 耗时：[" + (time2 - time1) + "]ms");
				}else {
					DataCenter_old.setRedis_is_ok(true);
				}
			}

		}
	}
			
		 
	 
	public Integer call() throws Exception {
		try {
			writeList(dealList(list));
		} catch (Exception e) {
			info_log.error("",e);
		}
		return null;
	}
}
