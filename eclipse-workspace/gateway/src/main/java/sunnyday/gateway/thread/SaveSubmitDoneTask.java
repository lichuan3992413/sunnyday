package sunnyday.gateway.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import sunnyday.common.model.SubmitBean;
import sunnyday.gateway.dc.DataCenter;
import sunnyday.gateway.util.CommonRAO;
import sunnyday.gateway.util.HSToolCode;
import sunnyday.gateway.util.ParamUtil;
import sunnyday.tools.util.CommonLogFactory;
 
public class SaveSubmitDoneTask implements Callable<Integer>{
	private static Logger  log = CommonLogFactory.getLog(SaveSubmitDoneTask.class);
	private static Logger  receiver_log = CommonLogFactory.getLog("receiver");
	
	private CommonRAO dao ;
	private List<SubmitBean> submitDoneList  ;
	private String name  ;
	public SaveSubmitDoneTask(CommonRAO dao,List<SubmitBean> submitDoneList,String name ){
		this.dao = dao ;
		this.submitDoneList = submitDoneList ;
		this.name = name ;
	}
	 
	public static void addSubmitDoneList(List<SubmitBean> submitDoneList,String name,CommonRAO dao){
		
		if(submitDoneList!=null&&submitDoneList.size()>0){
			Map<String, List<SubmitBean>> map = dealList(submitDoneList,name);
			if(map!=null&&map.size()>0){
				DataCenter.setRedis_is_ok(true);
				boolean is_do_idempotent = HSToolCode.isDoIdempotent();
				int time = HSToolCode.getIdempotentTimeOut();
				List<SubmitBean> data = null;
				List<SubmitBean> tmp = null;
				for(String key : map.keySet() ){
					if(key.equals(name)){//把不区分用户级别的数据写到文件中
						DataCenter.addSubmitListDoneCache(map.get(key));
					}else{
						//首先把数据加入一个set中，后续通过set中的元素，来判断是否有下发数据
						//幂等处理验证
						tmp = map.get(key);
						data = new ArrayList<SubmitBean>();
						if(is_do_idempotent&&tmp!=null){
							try {
								dao.doIdempotent(tmp,time);
							} catch (Exception e) {
								log.warn("doIdempotent",e);
							}
							
							for(SubmitBean bean:tmp){
								Object object = bean.getExtraFields().get("repeat-data");
								Object read_data = bean.getExtraFields().get("read-data");
								if(object==null){
									object = 0;
								}
								if((Integer.parseInt(object.toString()))==1&&read_data==null){
									receiver_log.info("transaction_id: "+bean.getExtraFields().get("transaction_id")+"; mobile: "+HSToolCode.filterMobile(bean.getMobilesString())+", 违反幂等原则（transaction_id重复 ），不进行下发。"); 
								}else {
									data.add(bean);
								}
							}
						}else{
							if(data!=null&&tmp!=null){
								data.addAll(tmp);
							}
						}
						try {
							dao.HSSADD(ParamUtil.REDIS_SET_SUBMIT_KEY, key);
						} catch (Exception e) {
							log.warn("HSSADD",e);
						}
						boolean isAdd = false ;
						if(data!=null&&data.size()>0){
							try {
								isAdd = dao.addSubmitDoneList(key, data);
							} catch (Exception e) {
								log.warn("addSubmitDoneList",e);
							}
							
						}
						if(!isAdd){
							DataCenter.setRedis_is_ok(false);
							//入redais失败，则把该数据写入本地文件中
							DataCenter.addSubmitListDoneCache(map.get(key));
						} 
					}
				}
			
			}	
		
		}
	
	}


	public Integer call() throws Exception {
		 try {
			 addSubmitDoneList(submitDoneList,name,dao);
		} catch (Exception e) {
			DataCenter.setRedis_is_ok(false);
			//入redais失败，则把该数据写入本地文件中
			DataCenter.addSubmitListDoneCache(submitDoneList);
			log.warn("addSubmitDoneList lost sms ["+submitDoneList+"]",e);
		}
		return null;
	}

	/**
	 * 
	 * @param list
	 * @param name
	 * @return
	 */
	public static Map<String, List<SubmitBean>> dealList(List<SubmitBean> list,String name) {
		Map<String, List<SubmitBean>> map = new HashMap<String, List<SubmitBean>>();
		if(list!=null&&list.size()>0){
			for(SubmitBean submitBean : list){
				String key = name+":"+submitBean.getUser_id();
				long size = 0 ;
				if(DataCenter.getSubmit_user_map().containsKey(key)){
					size = DataCenter.getSubmit_user_map().get(key);
				}
				//若redis接收队列超过了50W数据，则接收到的下发数据暂且缓存到本地文件中
//				if(size>SaveSubmitDoneListThread.getRedis_count()){
//					if (!map.containsKey(name)) {
//						map.put(name, new ArrayList<SubmitBean>());
//					}
//					map.get(name).add(submitBean); //待写入文件
//					
//				}else{
//				}
				
				if (!map.containsKey(key)) {
					map.put(key, new ArrayList<SubmitBean>());
				}
				map.get(key).add(submitBean);//待写入redis
			
			}
		}
		return map;
	}
	

}
