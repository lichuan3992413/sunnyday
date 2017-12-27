package sunnyday.controller.thread;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunnyday.common.model.BackFileForm;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.DAO.IMessageDAO;
import sunnyday.controller.util.FileUtil;
import sunnyday.tools.util.CommonLogFactory;
/**
 * 把数据中心submitHistoryCacheQueue缓存在本地文件中的短信对象转存到数据库submit_message_send_histroy中
 * @author 1307365
 *
 */
@Service
public class HistoryDataReadThread  extends Thread {
	private Logger log = CommonLogFactory.getCommonLog("infoLog");
	private boolean running = true;
	@Resource(name="${db.type}_MessageDAO")
	private IMessageDAO dao;
	
	private int COUNT = 1000;
	
	public void run() {
		while(running){
			try {
				List<BackFileForm> list=  DataCenter_old.getSubmitHistoryFileList(COUNT);
				if(list!=null&&list.size()>0){
					for(BackFileForm file:list){
				     List<SmsMessage> message_list = FileUtil.readOneListFromFile(file.getFile_path(),true);
				     boolean flag = dao.insert2SubmitMessageHistory(message_list);
						if(!flag){
							//入库失败，则把该数据写入本地文件中
							DataCenter_old.addSubmitHistoryCacheQueue(message_list);
						}
					}
				}
				
			} catch (Exception e) {
				log.error("Exception",e);
			}finally{
				try {
					sleep(3000);
				} catch (Exception e2) {
				}
			}
			
		}
		
	}
	public static void main(String[] args) {
		String path = "C:/hskj/file/history_data/201605201848426430_1000.sms";
		 System.out.println( FileUtil.readOneListFromFile(path,true));
		//thread.writeList(list);
	}

	public boolean doStop() {
		running = false;
		return true;
	}

}
