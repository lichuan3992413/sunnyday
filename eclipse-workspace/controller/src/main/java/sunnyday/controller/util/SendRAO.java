package sunnyday.controller.util;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import sunnyday.adapter.redis.DCAdapter;
import sunnyday.common.model.ReportBean;
import sunnyday.controller.thread.CommonRAO;
import sunnyday.tools.util.ConnectUtil;
@Repository
public class SendRAO extends CommonRAO {
	@Resource(name = "redisCli")
	protected DCAdapter dc;
	@Override
	protected DCAdapter getDc() {
		return dc;
	}
	public void updateCacheData(Map<String, ?>  map) {
		getDc().putCacheData(map);
	}
	
	public long  putReport2Redis(ReportBean report,String queue){
		long result = 0;
		long time1 = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = getDc().getJedis();
			result = jedis.lpush(queue.getBytes(),ConnectUtil.Object2Byte(report));
			long time2 = System.currentTimeMillis();
			if (time2 - time1 > 3000) {
				log_info.warn("putReport2Redis too long time  keys:" + queue + "; "+ (time2 - time1) + " ms; result: "+result);
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
}
