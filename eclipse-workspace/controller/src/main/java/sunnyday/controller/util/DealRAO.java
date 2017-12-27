package sunnyday.controller.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import sunnyday.adapter.redis.DCAdapter;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.thread.CommonRAO;
import sunnyday.controller.thread.DataCenter_old;
import sunnyday.tools.util.ConnectUtil;
import sunnyday.tools.util.DateUtil;
@Repository
public class DealRAO extends CommonRAO {
	/*private final static int TEAM = 100 ;*/
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	@Resource(name = "redisCli")
	protected DCAdapter dc;
	@Override
	protected DCAdapter getDc() {
		return dc;
	}


	public List<SmsMessage> batchMatchReportFromRedis(List<Object> reportList , String submit_sent){
		long time1 = System.currentTimeMillis();
		List<SmsMessage> list = new ArrayList<SmsMessage>();
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			Pipeline pipeline = jedis.pipelined();
			for(Object o : reportList){
				ReportBean report = (ReportBean)o;
				//计算所在分组
				String team_num = UtilTool.getMobileTeamNum(report.getMobile());
				String hash_key = "hash:"+submit_sent+"_"+team_num;
				String field_key = report.getMobile()+report.getMsg_id();

				pipeline.hget(hash_key.getBytes(), field_key.getBytes());

			}


			List<Object> matchList = pipeline.syncAndReturnAll();
			for(int i = 0 ; i < matchList.size() ; i++){

				if(matchList.get(i) != null){
					SmsMessage tmp = (SmsMessage) ConnectUtil.Byte2Object((byte[])matchList.get(i));
					list.add(tmp);
					ReportBean report = (ReportBean)reportList.get(i);
					String team_num = UtilTool.getMobileTeamNum(report.getMobile());
					//String sms_num =  UtilTool.getSmsTeamNum(tmp.getMsg_send_time());
					//String list_key = "list:"+submit_sent+"_"+sms_num+"_"+team_num;
					String hash_key = "hash:"+submit_sent+"_"+team_num;
					String field_key = report.getMobile()+report.getMsg_id();
					if(log.isDebugEnabled()){
						log.debug("report -> "+field_key+", "+report.getFail_desc()+", "+report.getErr());
					}

					//删除list 暂且不删除该项，此处list中的值，由超时部分来处理
				   // pipeline.lrem(list_key.getBytes(), 0, (byte[])matchList.get(i));
					
					//删除匹配的数据
					pipeline.hdel(hash_key, field_key);
				}else{
					list.add(null);
				}
			}
			pipeline.sync();

		} catch (Exception e) {
			log_info.error("batchMatchReportFromRedis", e);
		} finally {
			getDc().returnJedis(jedis);
		}
		long time2 = System.currentTimeMillis();
		if (time2 - time1 > 3000) {
			log_info.warn("batchMatchReportFromRedis too long time  "+ (time2 - time1) + " ms");
		}
		return list;
	}

	/**
	 * 批量处理超时未匹配的下发记录
	 * @param list
	 * @param submit_sent
	 * @param ps
	 */
	public void dealSubmitFinTimeOut(List<String> list,String submit_sent,int ps) { 
		for(String key:list){
			dealSubmitFinTimeOut(key,submit_sent);
		}
		
	}
	
	public void dealSubmitFinTimeOut(String key_name,String submit_sent) {
		long time1 = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			long len = jedis.llen(key_name.getBytes());
			Pipeline pipeline = jedis.pipelined();
			for(int i =0;i<len;i++){
				pipeline.rpop(key_name.getBytes());
			}
			
			List<Object> timeOutList = pipeline.syncAndReturnAll();
			
			List<SmsMessage> tmp_List = new ArrayList<SmsMessage>();
			for(int i = 0 ; i < timeOutList.size() ; i++){
				byte[] object =  (byte[]) timeOutList.get(i);
				if(object!=null){
					SmsMessage sms = (SmsMessage) ConnectUtil.Byte2Object(object);
					if (sms != null) {
						String team_num = UtilTool.getMobileTeamNum(sms.getMobile());
						String hash_key = "hash:"+submit_sent+"_"+team_num;
						String field_key = sms.getMobile() + sms.getTmp_msg_id();
						tmp_List.add(sms);
						pipeline.hdel(hash_key, field_key);
					}
				}
				 
			}
			
			List<Object> delete_List = pipeline.syncAndReturnAll();
			
			for(int j = 0 ; j < delete_List.size() ; j++){
				if(delete_List.get(j) != null){
					long result = (Long) delete_List.get(j);
					SmsMessage sms = tmp_List.get(j);
					if (result >0) {
						if(sms!=null){
							DataCenter_old.addSentMessageToDBQueue(sms);
							/**
							 * 此处产生未知数据的统计
							 */
							StatisticsUtil.Statistics4Memory(sms);
							if(log_info.isDebugEnabled()){
								log_info.debug("sent-timeOut "+sms);
							}
						}
						
					}else{
						if(log_info.isDebugEnabled()){
							log_info.debug("sms[" + sms + "]没有超时，已经完成了正常匹配[" + result + "]。");
						}
					}
				} 
			}
			
		 
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 3000) {
				log_info.warn("dealSubmitFinTimeOut too long " + (time2 - time1) + " ms;");
			}

		} catch (Exception e) {
			log_info.error("dealSubmitFinTimeOut", e);
		} finally {
			getDc().returnJedis(jedis);
		}

	}
 
	public void dealSubmitFinTimeOut(List<String> list,String submit_sent) {
		long time1 = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			for (String key : list) {
				byte[] tmp = jedis.rpop(key.getBytes());
				if (tmp != null && tmp.length > 0) {
					SmsMessage sms = (SmsMessage) ConnectUtil.Byte2Object(tmp);
					if (sms != null) {
						String[] array = key.split("_");
						//list:submit_sent_2016-05-19-00:13_13000
						String hash_key = "hash:" + submit_sent + "_" + array[3];
						String field_key = sms.getMobile() + sms.getTmp_msg_id();
						// 超时尚未匹配
						long tmp_del = jedis.hdel(hash_key, field_key);
						if (tmp_del > 0) {
							DataCenter_old.addSentMessageToDBQueue(sms);
							StatisticsUtil.Statistics4Memory(sms);
						} else {
							if(log_info.isDebugEnabled()){
								log_info.debug("sms[" + sms + "]没有超时，已经完成了正常匹配[" + tmp_del + "]。");
							}
							
						}

					}
				}
			}
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 3000) {
				log_info.warn("dealSubmitFinTimeOut too long " + (time2 - time1) + " ms;");
			}

		} catch (Exception e) {
			log_info.error("dealSubmitFinTimeOut", e);
		} finally {
			getDc().returnJedis(jedis);
		}

	}

	boolean dealTimeOut(SmsMessage sms,int deal_report_in_redis_timeOut){
		if(sms.getMsg_send_time()!=null){
			try {
				long time = sdf.parse(sms.getMsg_send_time()).getTime();
				 
				if(System.currentTimeMillis() - time > deal_report_in_redis_timeOut){
					return true ;
				}else{
					if(UtilTool.getTimeOutTryTimes()>0&&sms.getDo_times()>=UtilTool.getTimeOutTryTimes()){
						return true ;
					}
				}
			} catch (Exception e) {
				sms.setMsg_send_time(DateUtil.currentTimeToMs());
			}
		}else {
			sms.setMsg_send_time(DateUtil.currentTimeToMs());
		}
		return true ;
	}

	public long  putReport2Redis(List<ReportBean> reports,String queue){
		long result = 0;
		long time1 = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			Pipeline pipeline = jedis.pipelined();
			 for(ReportBean report:reports){
				 pipeline.lpush(queue.getBytes(),ConnectUtil.Object2Byte(report));
			 }
			 pipeline.sync();
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 3000) {
				log_info.warn("putReport2Redis too long time  keys:" + queue + "; "+ (time2 - time1) + " ms");
			}
		} catch (Exception e) {
			log_info.error("putReport2Redis,[key=" + queue + "]", e);
		} finally {
			getDc().returnJedis(jedis);
		}
		return result ;
	}
	public void updateDataCenterChargeTermidMapCache(Map<String, Map<String, String>> chargeTermid) {
		getDc().putCacheData(chargeTermid);
	}
	
	public long incrByAndExpire(String key,long increment,int seconds){
		long result = increment;
		Jedis jedis = null;
		
		try {
			jedis = getDc().getJedis();
			result = jedis.incrBy(key, increment);
			jedis.expire(key.getBytes(), seconds);
		} catch (Exception e) {
			log_info.error("incrByAndExpire,jedis_info", e);
		} finally {
			getDc().returnJedis(jedis);
		}
		return result;
	}

}
