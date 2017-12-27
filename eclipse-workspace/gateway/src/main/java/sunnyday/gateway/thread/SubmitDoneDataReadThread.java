package sunnyday.gateway.thread;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sunnyday.common.model.BackFileForm;
import sunnyday.common.model.SubmitBean;
import sunnyday.gateway.dc.DataCenter;
import sunnyday.gateway.util.CommonRAO;
import sunnyday.gateway.util.FileUtil;
import sunnyday.tools.util.CommonLogFactory;

/**
 * 把缓存的本地文件转存到数据库中
 * 把缓存文件中的数据写入到redis中时，为了防止数据的大量积压，需要等待redis
 * 队列有足够工具时在写回redis中
 * 
 * @author 1307365
 *
 */
@Service
public class SubmitDoneDataReadThread extends Thread implements Stoppable {
	private Logger log = CommonLogFactory.getCommonLog(SubmitDoneDataReadThread.class);
	private Logger monitor_log = CommonLogFactory.getLog("monitor");
	private boolean running = true;
	@Value("#{config.filePath}")
	private  String FILE_PATH ="/opt/sms/submit_file/";
	@Autowired
	private CommonRAO dao = null;
	@Value("#{config.read_file_count}")
	private int COUNT = 100;
	@Value("#{config.submitQueueName}")
	private String name = "submitDoneList";
	private final int LIMIT = 5000 ;
	public void run() {
		int count = 0 ;
		while (running) {
			try {
				//当前redis交互异常
				if(!DataCenter.isRedis_is_ok()){
					count = -1;
					monitor_log.error("server connect server's redis error !");
					try {
						Thread.sleep(2*1000L);
					} catch (Exception e) {
					}
					continue;
				}else {
					if(count!= 0){
						monitor_log.warn("server connect server's redis is ok ^_^ before.");
						try {
							Thread.sleep(30*1000L);
						} catch (Exception e) {
						}
						monitor_log.warn("server connect server's redis is ok ^_^  after !");
						count = 0;
						continue;
					}
				}
				    dealLoalFiles();
					List<BackFileForm> list = DataCenter.getSubmitDataFileList(COUNT);
					long size = DataCenter.getSubmitUserMap();
					boolean flag = true ;
					while(flag){
						if(size < LIMIT){
							flag = false ;
						}else {
							try {
								sleep(2000);
								monitor_log.info("redis 待发队列仍旧积压["+size+"],休眠2秒。");
								size = DataCenter.getSubmitUserMap();
							} catch (Exception e) {
							}
						}
					}
					
					if (list != null && list.size() > 0) {
						for (BackFileForm file : list) {
							List<SubmitBean> message_list = FileUtil.readOneListFromFile(file.getFile_path(), true);
							monitor_log.info("read from file:"+file+"; msg_size: "+message_list.size());
							if(message_list!=null&&message_list.size()>0){
								for(SubmitBean bean:message_list){
									bean.getExtraFields().put("read-data", true);
									monitor_log.info("read-data:"+bean);
								}
								SaveSubmitDoneTask.addSubmitDoneList(message_list,name,dao);
							}
						}
						
					}else{
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
	private  void dealLoalFiles() {
		if(DataCenter.getSubmitDataFileSize()==0){
			//遍历本地目录是否有遗漏未处理的文件
			List<String> paths  = FileUtil.getFilePathList(FILE_PATH);
			if(paths!=null&&paths.size()>0){
				 BackFileForm form =  null;
				for(String path:paths){
					form =new BackFileForm(path, 0, "submit_data");
					DataCenter.addSubmitDataFileQueue(form);
				}
				monitor_log.info("refind files from ["+FILE_PATH+"]："+paths.size());
			}
			
		}
	}
	

	
	

	public static void main(String[] args) {
	//String path = "C:/hskj/file/history_data/201605201848426430_5000.sms";
		//System.out.println(FileUtil.readOneListFromFile(path, true));
	}

	public boolean doStop() {
		running = false;
		return true;
	}

}
