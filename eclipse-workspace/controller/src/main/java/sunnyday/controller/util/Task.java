package sunnyday.controller.util;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import sunnyday.controller.DAO.ICacheDAO;
import sunnyday.controller.DAO.IControlDAO;
import sunnyday.tools.util.CommonLogFactory;

public abstract class Task {
	protected Logger info_log = CommonLogFactory.getCommonLog("infoLog");
	protected int intervalTime;
	protected boolean isPerforming = false;
	protected boolean isFished = true;

	@Resource(name="${db.type}_ControlDAO")
	protected IControlDAO dao = null;
	@Resource(name="${db.type}_CacheDAO")
	protected ICacheDAO cacheDAO;
	@Autowired
	protected GateRAO gateRao = null;
	@Autowired
	protected SendRAO sendRao = null;
	@Autowired
	protected DealRAO dealRao = null;
	/**
	 * 需要执行的具体任务
	 * @author 1111182(Doc.)
	 */
	public void perform(){
		isFished = false;
		isPerforming = true;
		long time1 = System.currentTimeMillis() ;
		this.detailTask();
		long time2 = System.currentTimeMillis() ;
		long time3 = time2-time1;
		if(time3>3000){
			info_log.warn(getClass().getName()+" detailTask is too long time ["+time3+"]ms");
		}
		isPerforming = false;
		isFished = true ;
	}
	protected abstract void detailTask();
	 
	 
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
	
	public boolean isPerforming(){
		return isPerforming;
	}
	public boolean isFished(){
		return isFished;
	}
}
