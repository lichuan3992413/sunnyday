package sunnyday.gateway.threadPool;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import sunnyday.gateway.util.CommonRAO;
import sunnyday.tools.util.CommonLogFactory;

public abstract class Task {
	protected  Logger log = CommonLogFactory.getLog("receiver");
	@Autowired	
	protected CommonRAO dao = null;
	private int intervalTime;
	 
	private boolean isPerforming = false;
	
	public boolean isPerforming() {
		return isPerforming;
	}
	public void perform(){
		isPerforming = true;
		long time1 = System.currentTimeMillis() ;
		this.reloadCache();
		long time2 = System.currentTimeMillis() ;
		long time3 = time2-time1;
		if(time3>500){
			log.info(getClass().getName()+" detailTask is too long time ["+time3+"]ms");
		}
		isPerforming = false;
	}
	/**
	 * 需要执行的具体任务
	 * @author 1111182(Doc.)
	 */
	public abstract void reloadCache();
	/**
	 * 获取该任务执行的间隔时间
	 * @return 每次执行任务的间隔毫秒数
	 */
	public int getIntervalTime(){
		return intervalTime;
	}
	/**
	 * 设置任务执行间隔时间
	 */
	public void setIntervalTime(int millons){
		intervalTime = millons;
	}
}
