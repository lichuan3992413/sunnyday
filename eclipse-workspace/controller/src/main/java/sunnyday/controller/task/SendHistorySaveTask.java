package sunnyday.controller.task;
import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.DAO.IMessageDAO;
import sunnyday.controller.cache.GateErrCodeCache;
import sunnyday.controller.thread.DataCenter_old;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 数据中心submitHistoryQueue队列中短信保存至数据库中submit_message_send_history表中
 */
public class SendHistorySaveTask implements Callable<Integer> {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	private IMessageDAO dao;
	private List<SmsMessage> list;

	public SendHistorySaveTask(IMessageDAO dao, List<SmsMessage> list) {
		this.dao = dao;
		this.list = list;

	}


	@Override
	public Integer call() throws Exception {
		try {
			if (list != null && list.size() > 0) {
				long time1 =System.currentTimeMillis();
				StatisticsUtil.Statistics4HistoryByList(list);
				if(UtilTool.isEncode()){
					for(SmsMessage sms:list){
						try {
							
							 if(GateErrCodeCache.isExceptionalCode(sms.getFail_desc())){
								 DataCenter_old.addExceptionalMobileQueue(sms);
							 }
							if(sms.getIs_encode()==0){
								EncodeResponse rep = HSToolCode.encoded(sms.getComplete_content());
								if(rep.isSuccess()){
									sms.setIs_encode(1);
									sms.setComplete_content(rep.getContent());
									sms.setMsg_content(HSToolCode.encoded(sms.getMsg_content()).getContent());
							    }
							}
						} catch (Exception e) {
							log.error("", e);
						}
						
					}
				}
				boolean flag = dao.insert2SubmitMessageHistory(list);
				if(!flag){
					//入库失败，则把该数据写入本地文件中
					DataCenter_old.addSubmitHistoryCacheQueue(list);
				}
				long time =System.currentTimeMillis()-time1  ;
				if(time>3000){
					log.warn("insertIntoSubmitMessageHistory ["+list.size()+"]too long time ["+time+"]ms ");
				}
			}
			
		} catch (Exception e) {
			log.error("insert2SubmitMessageHistory lost sms " + list,e);
		}
		return null;
	}

}
