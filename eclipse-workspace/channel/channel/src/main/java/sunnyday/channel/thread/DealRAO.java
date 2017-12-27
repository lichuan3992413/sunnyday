package sunnyday.channel.thread;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import sunnyday.adapter.redis.DCAdapter;
import sunnyday.common.model.SmsMessage;
import sunnyday.tools.util.ConnectUtil;
import sunnyday.tools.util.ParamUtil;
@Repository
public class DealRAO extends CommonRAO {
	@Resource(name = "redisCli")
	protected DCAdapter dc;
	@Override
	protected DCAdapter getDc() {
		return dc;
	}
	
	public boolean putSubmitFin2Redis(List<SmsMessage> smss,String submit_sent){
		Jedis jedis = null;
		boolean result = true ;
		try {
			jedis = getDc().getJedis();
			if(smss!=null&&smss.size()>0){
				Pipeline p = jedis.pipelined();
				for(SmsMessage sms:smss){
					String team_num = UtilTool.getMobileTeamNum(sms.getMobile());
					String sms_num = UtilTool.getSmsTeamNum(sms.getMsg_send_time());
					String list_key = "list:"+submit_sent+"_"+sms_num+"_"+team_num;
					String hash_key = "hash:"+submit_sent+"_"+team_num;
					String field_key = sms.getMobile()+sms.getTmp_msg_id();
					byte[] field_value = ConnectUtil.Object2Byte(sms);
					p.hset(hash_key.getBytes(), field_key.getBytes(), field_value);//A  A与B的未知不可以调换 ：先lpush完成后再进行hset
					p.lpush(list_key.getBytes(), field_value);//B
					p.sadd(ParamUtil.REDIS_SET_SENT_KEY, list_key);
					p.expire(list_key, 5*60*60);
					p.expire(hash_key, 5*60*60);
				}
				p.sync();
			}
		} catch (Exception e) {
			log.error("putSubmitFin2Redis ", e);
			result = false;
		} finally {
			getDc().returnJedis(jedis);
		}
		
		return result;
	}

	

}
