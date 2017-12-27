package sunnyday.channel.main;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import sunnyday.channel.thread.ControlThread;
import sunnyday.channel.thread.DataSyncThread;
import sunnyday.channel.thread.MessageScanTask;
import sunnyday.channel.thread.ReadFilePropertiesThread;
import sunnyday.channel.thread.SentMessageConsumeThread;

public class ChannelMain {

	private Map<String, Thread> allThreads = new TreeMap<String, Thread>();

	public static void main(String[] args) {

		ChannelMain mainSender = new ChannelMain();
		mainSender.start();
	}

	public void start() {

		ApplicationContext apx = new FileSystemXmlApplicationContext("/config/bean-config.xml");
		sunnyday.tools.util.Spring.setApx(apx);
		
		ControlThread controller = apx.getBean(ControlThread.class);
		controller.setName("ControlThread");
		allThreads.put("ControlThread", controller);
		controller.start();
		
		// 启动数据中心同步线程初始化本地缓存
		DataSyncThread dst = apx.getBean(DataSyncThread.class);
		dst.setName("DataSyncThread");
		allThreads.put("DaOtaSyncThread", dst);
		dst.start();

		MessageScanTask scan = apx.getBean(MessageScanTask.class);
		scan.setName("MessageScanTask");
		allThreads.put("MessageScanTask", scan);
		scan.start();

//		MonitorThread monitor = apx.getBean(MonitorThread.class);
//		monitor.setName("MonitorThread");
//		allThreads.put("MonitorThread", monitor);
//		monitor.start();

		ReadFilePropertiesThread readFilePropertiesThread = apx.getBean(ReadFilePropertiesThread.class);
		readFilePropertiesThread.setName("ReadFilePropertiesThread");
		allThreads.put("ReadFilePropertiesThread", readFilePropertiesThread);
		readFilePropertiesThread.start();

//		WriteBackThread writeBackThread = apx.getBean(WriteBackThread.class);
//		writeBackThread.setName("WriteBackThread");
//		allThreads.put("WriteBackThread", writeBackThread);
//		writeBackThread.start();

//		DealDateCenterToRedisThread dealDateCenterToRedisThread = apx
//				.getBean(DealDateCenterToRedisThread.class);
//		dealDateCenterToRedisThread.setName("dealDateCenterToRedisThread");
//		allThreads.put("DealDateCenterToRedisThread", dealDateCenterToRedisThread);
//		dealDateCenterToRedisThread.start();
//
		SentMessageConsumeThread sentMessageConsumeThread = apx.getBean(SentMessageConsumeThread.class);
		sentMessageConsumeThread.setName("sentMessageConsumeThread");
		allThreads.put("SentMessageConsumeThread", sentMessageConsumeThread);
		sentMessageConsumeThread.start();
//
//		SubmitDoneDataReadThread submitDoneDataReadThread = apx.getBean(SubmitDoneDataReadThread.class);
//		sentMessageConsumeThread.setName("submitDoneDataReadThread");
//		allThreads.put("submitDoneDataReadThread", submitDoneDataReadThread);
//		submitDoneDataReadThread.start();
//
//		SubmitDoneDataWriteThread submitDoneDataWriteThread = apx.getBean(SubmitDoneDataWriteThread.class);
//		sentMessageConsumeThread.setName("submitDoneDataWriteThread");
//		allThreads.put("submitDoneDataWriteThread", submitDoneDataWriteThread);
//		submitDoneDataWriteThread.start();

	}

}