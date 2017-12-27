package sunnyday.controller.thread;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.common.model.BackFileForm;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.util.FileUtil;
import sunnyday.tools.util.CommonLogFactory;


/**
 * 下发数据入库较慢或者下发数据入库失败时，需要把本批次的数据暂且写到硬盘文件中
 * 以防止数据丢失和提高整体的数据处理能力
 * @author 1307365
 *
 */
@Service
public class SubmitDoneDataWriteThread  extends Thread{
	private Logger log = CommonLogFactory.getCommonLog("receiver");
	private Logger monitor_log = CommonLogFactory.getLog("monitor");
	private boolean isRunning = true;
	@Value("#{config.filePath}")
	private  String FILE_PATH ="/opt/sms/send_file/";
	@Value("#{config.write2filecount}")
	private int COUNT = 5000;//单次处理最多条数  需要抽取到配置文件
	private int number = 0 ;
	@Override
	public void run() {
		 while (isRunning) {
			 try {
				 int size = DataCenter_old.getsubmitListDoneCacheSize();
				 if(size>0){
					 if(size<1000&&number<100){
						 number ++;
						 sleep(10);
						 continue ;
					 }
					 List<SmsMessage> list = DataCenter_old.getsubmitListDoneCache(COUNT);
						if(list!=null&&list.size()>0){
							writeList(list);
							number = 0 ;
						} else {
							sleep(500);
						}
				 }else{
					 sleep(500);
				 }
			
			} catch (Exception e) {
				log.error("SubmitDoneDataWriteThread",e);
			}finally{
				try {
					sleep(10);
				} catch (Exception e2) {
				}
			}
		}
	}

	private   void writeList(List<SmsMessage> list) {
		BackFileForm result = FileUtil.writeList(FILE_PATH, list,"send_data");
		DataCenter_old.addSubmitDataFileQueue(result);
		monitor_log.info("write sender msg to file: "+result);
	}
	
	public boolean doStop() {
		isRunning = false ;
		return false;
	}
}
