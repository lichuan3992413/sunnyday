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
 * 把缓存的本地文件转存到数据库中 把缓存文件中的数据写入到redis中时，为了防止数据的大量积压，需要等待redis 队列有足够工具时在写回redis中
 * 
 * @author 1307365
 *
 */
@Service
public class SubmitDoneDataReadThread extends Thread {
	private Logger log = CommonLogFactory.getCommonLog("receiver");
	private Logger monitor_log = CommonLogFactory.getLog("monitor");
	private boolean running = true;
	@Value("#{config.filePath}")
	private String FILE_PATH = "/opt/sms/send_file/";
	@Value("#{config.read_file_count}")
	private int COUNT = 100;
	 

	public void run() {
		int count = 0 ;
		while (running) {
			try {
				// 当前redis交互异常
				if (!DataCenter_old.isRedis_is_ok()) {
					count = -1;
					monitor_log.error("server connect sender's redis error !");
					try {
						Thread.sleep(2 * 1000L);
					} catch (Exception e) {
					}
					continue;
				}else {
					if(count!= 0){
						monitor_log.warn("server connect sender's redis is ok ^_^ before.");
						try {
							Thread.sleep(30*1000L);
						} catch (Exception e) {
						}
						monitor_log.warn("server connect sender's redis is ok ^_^  after !");
						count = 0;
						continue;
					}
				}
				dealLoalFiles();
				List<BackFileForm> list = DataCenter_old.getSubmitDataFileList(COUNT);
				if (list != null && list.size() > 0) {
					for (BackFileForm file : list) {
						List<SmsMessage> message_list = FileUtil.readListFromFile(file.getFile_path(), true);
						monitor_log.info("read send msg from file:" + file + "; msg_size: " + message_list.size());
						if (message_list != null && message_list.size() > 0) {
							// 放到待写入redis的队列
							for(SmsMessage sms:message_list){
								monitor_log.info("read-data: "+sms);
							}
							DataCenter_old.addSubmitMessage(message_list);
						}
					}

				} else {
					sleep(2000);
				}

			} catch (Exception e) {
				log.error("Exception", e);
			} finally {
				try {
					sleep(300);
				} catch (Exception e2) {
				}
			}

		}

	}

	/**
	 * 检测本地是否有遗漏的文件尚未处理，若有的话重新进行处理
	 */
	private void dealLoalFiles() {
		if (DataCenter_old.getSubmitDataFileSize() == 0) {
			// 遍历本地目录是否有遗漏未处理的文件
			List<String> paths = FileUtil.getFilePathList(FILE_PATH);
			if (paths != null && paths.size() > 0) {
				BackFileForm form = null;
				for (String path : paths) {
					form = new BackFileForm(path, 0, "send_data");
					DataCenter_old.addSubmitDataFileQueue(form);
				}
				monitor_log.info("refind  send files from [" + FILE_PATH + "]：" + paths.size());
			}

		}
	}

	public boolean doStop() {
		running = false;
		return true;
	}

}
