package sunnyday.controller.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunnyday.common.model.StatisticsHistoryModel;
import sunnyday.common.model.StatisticsModel;
import sunnyday.controller.DAO.IDataStatisticsDAO;
import sunnyday.controller.cache.GateConfigCache;
import sunnyday.controller.util.StatisticsUtil;
import sunnyday.tools.util.CommonLogFactory;

/**
 * Description:  提交内存统计的相关数据
 *
 * @author 1307365
 *
 * Create at:   2016-03-21 10:39
 */
@Service
public class SmsStatisticsThread extends Thread {
	private Logger log = CommonLogFactory.getCommonLog("monitor");
	
	

	private final int DEFAULT_STATISTIC_INTERVAL = 60;//毫秒秒
	boolean running = true;
	boolean isFished = false;
	boolean isdoStop = false;
	@Resource(name="${db.type}_DataStatisticsDAO")
	private IDataStatisticsDAO dataStatisticsDAO;
	private long last_time = 0;
	
	@Override
	public void run() {
		log.info("SmsStatisticsThread is start ");
		while(running&&!isdoStop){
			isFished = false ;
			long time1 = System.currentTimeMillis();
			try {
				int statistic_interval = getStatisticInterval();
				if((System.currentTimeMillis()- last_time >= (statistic_interval * 1000))&&!isdoStop){
					last_time = System.currentTimeMillis();
					//天级别统计
					if(StatisticsUtil.getSwitch(1)){
						dealStatisticsHistory();
					}
					//小时分钟级别统计
					if(StatisticsUtil.getSwitch(2)){
						dataStatisticsDAO.updateStatisticsModel(statistics_hour(), "statistics_hour");
						dataStatisticsDAO.updateStatisticsModel(statistics_min(), "statistics_min");
					}
					long time2 = System.currentTimeMillis();
					log.info("SmsStatisticsThread 耗时： ["+(time2-time1)+"] ms .");
				}else {
					sleep(1000);
				}
			} catch (Exception e) {
			}finally{
				isFished =  true ;
			}
		}
		isFished =  true ;
	}

	private int getStatisticInterval() {
		int interval = DEFAULT_STATISTIC_INTERVAL; 
		try{
			String statistic_interval = GateConfigCache.getValue("statistic_interval");
			if(null != statistic_interval && !statistic_interval.equals("")){
				interval = Integer.parseInt(statistic_interval);
			}
		}catch(Exception e){
			log.error("statistics interval config err", e);
		}
		return interval;
	}
	
	private void dealStatisticsHistory(){
		List<StatisticsHistoryModel> list = statistics_day();
		if(list!=null&&list.size()>0){
			for(StatisticsHistoryModel model:list){
				int sn =dataStatisticsDAO.isExist(model);
				if(sn!=-1){//存在，需要修改
					dataStatisticsDAO.updateStatistics(model, sn);
				}else {
					//tmp_list.add(model);
					dataStatisticsDAO.insertStatisticsHistoryByBatch(model);
				}
			}
		}
	}
	private List<StatisticsHistoryModel> statistics_day() {
		List<StatisticsHistoryModel> list = new ArrayList<StatisticsHistoryModel>();
		Iterator<Map.Entry<String, StatisticsHistoryModel>> it  = DataCenter_old.getStatistics_day().entrySet().iterator();
		while (it.hasNext()) {
			StatisticsHistoryModel model = DataCenter_old.getStatistics_day().remove(it.next().getKey());
			list.add(model);
		}
		return list ;
	}

	private List<StatisticsModel> statistics_hour() {
		List<StatisticsModel> list = new ArrayList<StatisticsModel>();
		Iterator<Map.Entry<String, StatisticsModel>> it  = DataCenter_old.getStatistics_hour().entrySet().iterator();
		while (it.hasNext()) {
			StatisticsModel model = DataCenter_old.getStatistics_hour().remove(it.next().getKey());
			list.add(model);
		}
		return list ;
	}

	private List<StatisticsModel> statistics_min() {
		List<StatisticsModel> list = new ArrayList<StatisticsModel>();
		Iterator<Map.Entry<String, StatisticsModel>> it  = DataCenter_old.getStatistics_min().entrySet().iterator();
		while (it.hasNext()) {
			StatisticsModel model = DataCenter_old.getStatistics_min().remove(it.next().getKey());
			list.add(model);
		}
		return list ;
	}


	public boolean doStop() {
		isdoStop = true ;
		while (!isFished) {
			try {
				Thread.sleep(3000);
				log.warn("等待下发统计关闭！！！！");
				DataCenter_old.printStatisticsSize();
			} catch (Exception e) {
				log.error("", e);
			}
		}
		running=false;
		return true;
	}
}
