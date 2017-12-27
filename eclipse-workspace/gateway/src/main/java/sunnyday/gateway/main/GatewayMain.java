package sunnyday.gateway.main;

import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import sunnyday.gateway.dc.DataCenter;
import sunnyday.gateway.thread.DataScanThread;
import sunnyday.gateway.thread.DataSyncThread;
import sunnyday.gateway.thread.InterfaceControlThread;
import sunnyday.gateway.thread.ReadFilePropertiesThread;
import sunnyday.gateway.thread.Stoppable;
import sunnyday.gateway.thread.SubmitDoneDataReadThread;
import sunnyday.gateway.thread.SubmitDoneDataWriteThread;
import sunnyday.gateway.thread.UserBalanceThread;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.Spring;

public class GatewayMain{
	
//	private Map<String, Thread> allThreads = new TreeMap<String, Thread>();
	private Logger log = CommonLogFactory.getLog("gateway");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Setting configDir=[" + System.getProperty("user.dir") + "]");
//		log = CommonLogFactory.getCommonLog("monitor");
		GatewayMain mainReceiver = new GatewayMain();
		mainReceiver.start();
	}

	public void start() {
		//EhcacheRepeatMsgFilter.doStart();
		System.out.println("Spring config path:[" + System.getProperty("user.dir") + "/config/bean-config.xml]");
		Spring.initFileSystemSpring(System.getProperty("user.dir") + "/config/bean-config.xml");

		//启动数据中心同步线程初始化本地缓存
		DataSyncThread dst = Spring.getApx().getBean(DataSyncThread.class);
		dst.setName("DataSyncThread");
		allThreads.put("DataSyncThread", dst);
		dst.start();

		try {
			Thread.sleep(3000L);
		} catch (Exception e) {
		}

		//启动接收端主控制线程，控制接收线程开闭
		InterfaceControlThread controller = Spring.getApx().getBean(InterfaceControlThread.class);
		controller.setName("ControlThread");
		allThreads.put("ControlThread", controller);
		controller.start();
		
		
/*		HttpGateClient httpClient = new HttpGateClient();
		httpClient.doInit("http", "http");
		httpClient.doStart();*/
		
		/*ReadSubmitFileTask readSubmit = Spring.getApx().getBean(ReadSubmitFileTask.class);
		readSubmit.setName("ReadSubmitFileTask");
		allThreads.put("ReadSubmitFileTask", readSubmit);
		readSubmit.start();*/
		
//		SaveSubmitDoneListThread saveSubmitDoneListThread = (SaveSubmitDoneListThread)Spring.getApx().getBean(SaveSubmitDoneListThread.class);
//	    saveSubmitDoneListThread.setName("SaveSubmitDoneListThread");
//	    allThreads.put("ReadSubmitFileTask", saveSubmitDoneListThread);
//	    saveSubmitDoneListThread.start();
		
//		MonitorThread monitor = Spring.getApx().getBean(MonitorThread.class);
//		monitor.setName("MonitorThread");
//		allThreads.put("MonitorThread", monitor);
//		monitor.start();
		
		ReadFilePropertiesThread readFilePropertiesThread = Spring.getApx().getBean(ReadFilePropertiesThread.class);
		readFilePropertiesThread.setName("ReadFilePropertiesThread");
		allThreads.put("ReadFilePropertiesThread", readFilePropertiesThread);
		readFilePropertiesThread.start();
		
		DataScanThread dataScanThread= Spring.getApx().getBean(DataScanThread.class);
		dataScanThread.setName("DataScanThread");
		allThreads.put("ReportUsersScanThread", dataScanThread);
		dataScanThread.start();
		
		
		UserBalanceThread userBalanceThread= Spring.getApx().getBean(UserBalanceThread.class);
		userBalanceThread.setName("userBalanceThread");
		allThreads.put("userBalanceThread", userBalanceThread);
		userBalanceThread.start();
		
		SubmitDoneDataReadThread submitDoneDataReadThread= Spring.getApx().getBean(SubmitDoneDataReadThread.class);
		submitDoneDataReadThread.setName("SubmitDoneDataReadThread");
		allThreads.put("SubmitDoneDataReadThread", submitDoneDataReadThread);
		submitDoneDataReadThread.start();


		
		SubmitDoneDataWriteThread submitDoneDataWriteThread= Spring.getApx().getBean(SubmitDoneDataWriteThread.class);
		submitDoneDataWriteThread.setName("SubmitDoneDataWriteThread");
		allThreads.put("SubmitDoneDataWriteThread", submitDoneDataWriteThread);
		submitDoneDataWriteThread.start();


	}

}
