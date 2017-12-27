package sunnyday.controller.thread;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.DAO.IMessageDAO;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 可以多个节点同时启动，建议只在一个处理节点启动 <br>
 * 将异常号码的数据入库
 * 
 * @author 1307365
 */
@Service
public class ExceptionalMobileToDBThread extends Thread {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
 
	@Resource(name = "${db.type}_MessageDAO")
	private IMessageDAO dao;

	private boolean isRunnable = true;

	public void run() {
		log.info("---------------> 由必达短信数据入库线程启动 <---------------");

		while (isRunnable) {
			try {
				List<SmsMessage> arriveReplyList = DataCenter_old.getExceptionalMobileQueue(1000);
				if (arriveReplyList != null && arriveReplyList.size() > 0) { 
					dao.insert2ExceptionalMobileDeal(arriveReplyList);
				} else {
					sleep(200);
				}
			} catch (Exception e) {
				if (e.getMessage() != null && !e.getMessage().contains("sleep interrupted")) {
					log.error("ExceptionalMobileToDBThread Exception", e);
				}
			}
		}
	}

	public boolean doStop() {
		this.isRunnable = false;
		log.info("--------< 由必达短信回执超过时效范围数据入库线程关闭   >--------");
		interrupt();
		return false;
	}

}
