package sunnyday.gateway.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sunnyday.common.model.UserBalanceForm;
import sunnyday.common.model.UserBalanceInfo;
import sunnyday.common.model.UserBean;
import sunnyday.gateway.dc.DataCenter;
import sunnyday.gateway.util.CommonRAO;
import sunnyday.tools.util.CommonLogFactory;
@Service
public class UserBalanceThread extends Thread implements Stoppable {
	private  Logger log = CommonLogFactory.getLog("receiver");
	@Autowired
	private CommonRAO rao = null;
	private boolean runnig = false;
	public UserBalanceThread(){
		runnig =  true ; 
	}
	@Override
	public void run() {
		while (runnig) {
			try {
				reloadCache() ;
				sleep(1000*5L);
			} catch (Exception e) {
			}
			
		}
	}
	public  void  reloadCache() {
		try{
			long time1= System.currentTimeMillis();
			Map<String, UserBalanceForm> curTcInfo = rao.getCurrentDcBalanceMap();//redis中所有的余额信息
			long time2= System.currentTimeMillis();
			Map<String, UserBalanceInfo> localBalanceCache = DataCenter.getUserBalanceMap();//本地缓存中的所有余额信息
			int local_size = localBalanceCache.size();
			int remote_size = curTcInfo.size();
			List<String> more_user = new ArrayList<String>();
			if(local_size!=remote_size){
				log.warn("local_size["+local_size+"] not equals remote_size["+remote_size+"],time: "+(time2-time1)+" ms");
			   if(local_size>remote_size){
				   for(String user_id:localBalanceCache.keySet()){
					   if(!curTcInfo.containsKey(user_id)){
						   more_user.add(user_id);
					   }
				   }
				   if(more_user.size()>0){
					   for(String user:more_user){
						   log.warn("本地缓存账号多余远程账号["+user+"],"+localBalanceCache.remove(user));
					   }
					   
				   }
			   }
			}
			
			if (curTcInfo != null && curTcInfo.size() > 0) {
				for (String eachUser : curTcInfo.keySet()) {
					if (eachUser == null || "".equals(eachUser)) {
						log.warn("redis user  is null. [" + eachUser + "]");
						continue;
					}
				
					UserBalanceForm form = curTcInfo.get(eachUser);
					double last_balance = form.getLast_balance();
					double diff_balance = form.getDiffBalance();

					if (localBalanceCache.containsKey(eachUser)) {// 已经在本地缓存中，则进行同步扣费，若不在进入缓存中
						UserBean userinfo = DataCenter.getUserBeanMap().get(eachUser);
						if(userinfo!=null){
							/**
							 * 判断用户的计费类型，若非预付费的无须进行余额同步
							 */
							if(userinfo.getUser_type()==1){
								continue;
							}
						}
						UserBalanceInfo lubi = localBalanceCache.get(eachUser);// 本地缓存中的余额信息
						double debt = lubi.getDebt_cache();
						double tmp = lubi.getBalance();
						if(tmp==last_balance&&debt==0.0){
							if(log.isDebugEnabled()){
								log.debug("["+eachUser+"]客户余额没有发生变化，无须同步！");
							}
							continue;
						} 
						lubi.setBalance(last_balance);
						lubi.SyncDebt(debt);
						// 本地和redis中的数据完成了同步，此时把新增的扣费同步到redis中
						String key = "balance:" + eachUser;
						String field_diffBalance = "diffBalance";
						String field_cur_balance = "cur_balance";
						double result = rao.HSHINCRBYFLOAT(key, field_diffBalance, debt);
						long flag = rao.HSHSet(key, field_cur_balance, lubi.getBalance());
						if(log.isDebugEnabled()){
							log.debug("["+flag+"]本地余额 "+lubi);
							log.debug("redis余额 "+form+",变化后："+result);	
						}
						
					} else {
						UserBalanceInfo info = new UserBalanceInfo();
						info.setBalance(last_balance);
						info.setCharge_sum(diff_balance);
						info.setUser_id(eachUser);
						info.setCharge_sum(0l);
						localBalanceCache.put(eachUser, info);
						log.info("新增同步用户余额信息["+eachUser+"]"+info);
					}
				}

			}else {
				 log.warn("remoteBalanceCache_size is 0 . ; time: "+(time2-time1)+" ms");
			}
			
		
			 
		}catch(Exception e){
				log.error("计费同步异常",e);
		}
	}
	
/*	private void resetBalance(String user_id){
		try {
			DataCenter.getUserBalanceMap().remove(user_id);
			//log.warn("resetBalance_从本地缓存中移除缓存账号：user_id="+user_id);
		} catch (Exception e) {
			log.error("resetBalance",e);
		}
		
	}*/

	public boolean doStop() {
		runnig = false;
		return true;
	}
}
