package sunnyday.controller.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import sunnyday.common.model.BackFileForm;
import sunnyday.common.model.DeliverBean;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.StatisticsHistoryModel;
import sunnyday.common.model.StatisticsModel;
import sunnyday.tools.util.CommonLogFactory;
 /**
  * 处理短信发送过程中使用到的各个队列
  * @author 1307365
  *
  */
public class DataCenter_old{	
	
	private static transient Logger log = CommonLogFactory.getCommonLog("monitor");
	private static   BlockingQueue<SmsMessage> SystemCheckQueue = new ArrayBlockingQueue<SmsMessage>(10000);  
	private static   BlockingQueue<SmsMessage> LongMessageInsertDBQueue = new ArrayBlockingQueue<SmsMessage>(10000); 
	private static   BlockingQueue<SmsMessage> submitMessageQueue = new ArrayBlockingQueue<SmsMessage>(10000);  
	private static   BlockingQueue<SmsMessage> rejectMessageQueue = new ArrayBlockingQueue<SmsMessage>(10000); 
	private static   BlockingQueue<SmsMessage> manualCheckMessageQueue = new ArrayBlockingQueue<SmsMessage>(10000);
	private static   BlockingQueue<SmsMessage> avoidDisturbMessageQueue = new ArrayBlockingQueue<SmsMessage>(10000);  
	
	private static   BlockingQueue<SmsMessage> submitHistoryQueue = new ArrayBlockingQueue<SmsMessage>(50000);
	//若一级缓存队列已满，则放入二级缓存队列，二级缓存队列的数据将被写入到本地文件中
    private static   BlockingQueue<SmsMessage> submitHistoryCacheQueue = new ArrayBlockingQueue<SmsMessage>(50000);
	//文件缓存队列
	private static   BlockingQueue<BackFileForm> submitHistoryFileQueue = new ArrayBlockingQueue<BackFileForm>(50000);
	
	// 提交完成二级缓存队列 二级缓存队列中的数据写入文件
	private static ArrayBlockingQueue<SmsMessage> submitListDoneCache = new ArrayBlockingQueue<SmsMessage>(500000);

	
	private static   Map<String, SmsMessage> catchMessageMap = new ConcurrentHashMap<String, SmsMessage>(50000);
	private static   Map<String, Long> catchMessageTimeMap = new ConcurrentHashMap<String, Long>(50000);
	private static   BlockingQueue<SmsMessage> sentMessageToDBQueue = new ArrayBlockingQueue<SmsMessage>(50000);
	private static   BlockingQueue<ReportBean> receiveReportQueue = new ArrayBlockingQueue<ReportBean>(50000);
	private static   BlockingQueue<ReportBean> dealedReportToCacheQueue = new ArrayBlockingQueue<ReportBean>(50000);
	private static   BlockingQueue<ReportBean> sendReportQueue = new ArrayBlockingQueue<ReportBean>(50000);
	private static   BlockingQueue<ReportBean> unmatchedReportToDBQueue = new ArrayBlockingQueue<ReportBean>(50000);
	private static   BlockingQueue<ReportBean> sentReportToDBQueue = new ArrayBlockingQueue<ReportBean>(50000);
	private static   BlockingQueue<DeliverBean> receiveDeliverQueue = new ArrayBlockingQueue<DeliverBean>(50000);
	private static   BlockingQueue<DeliverBean> dealedDeliverQueue = new ArrayBlockingQueue<DeliverBean>(50000);
	private static   BlockingQueue<DeliverBean> sentDeliverToDBQueue = new ArrayBlockingQueue<DeliverBean>(50000);
	
	private static   Map<String, StatisticsHistoryModel> statistics_day = new ConcurrentHashMap<String, StatisticsHistoryModel>(50000);
	private static   Map<String, StatisticsModel> statistics_hour = new ConcurrentHashMap<String, StatisticsModel>(50000);
	private static   Map<String, StatisticsModel> statistics_min = new ConcurrentHashMap<String, StatisticsModel>(50000);
	
	/**存放异常号码数据**/
	private static   BlockingQueue<SmsMessage> exceptionalMobileQueue = new ArrayBlockingQueue<SmsMessage>(50000); 
	
	//public transient static final BlockingQueue<Runnable> sentMessageConsumeWorkQueue = new ArrayBlockingQueue<Runnable>(2000);
	private transient static final BlockingQueue<Runnable> submitMessageProductWorkQueue = new ArrayBlockingQueue<Runnable>(2000);
	private transient static final BlockingQueue<Runnable> receiveReportConsumeWorkQueue = new ArrayBlockingQueue<Runnable>(2000);
	private transient static final BlockingQueue<Runnable> arriveReplyListInDbWorkQueue = new ArrayBlockingQueue<Runnable>(2000);
	
	private transient static final BlockingQueue<Runnable> dataToDbWorkQueue = new ArrayBlockingQueue<Runnable>(4000);
	private transient static final ExecutorService dataToDbPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors()+2, 60,TimeUnit.SECONDS, dataToDbWorkQueue);
	
	private transient static final Map<String, Long> queue_set_size = new ConcurrentHashMap<String, Long>(50000);
	
	private static BlockingQueue<BackFileForm> submitDataFileQueue = new ArrayBlockingQueue<BackFileForm>(100000);
	
	public static List<SmsMessage> getsubmitListDoneCache(int limit) {
		List<SmsMessage> list = new ArrayList<SmsMessage>();
		for (int i = 0; i < limit; i++) {
			SmsMessage bean = submitListDoneCache.poll();
			if (bean != null) {
				list.add(bean);
			} else {
				break;
			}
		}
		return list;
	}
	/**
	 * 获取二级缓存队列的大小
	 * 
	 * @return
	 */
	public static int getsubmitListDoneCacheSize() {
		return submitListDoneCache.size();
	}
	
	public static boolean addSubmitListDoneCache(SmsMessage bean) {
		boolean result = false;
		try {
			submitListDoneCache.put(bean);
			result = true;
		} catch (Exception e) {
			log.error("addSubmitListDoneCache[" + submitListDoneCache.size() + "] lost file " + bean);
		}
		return result;
	}


	public static boolean addSubmitListDoneCache(List<SmsMessage> beans) {
		boolean result = false;
		try {
			try {
				result = submitListDoneCache.addAll(beans);
			} catch (Exception e) {
			}

			while (!result) {
				try {
					result = submitListDoneCache.addAll(beans);
					if (!result) {
						log.warn("二级缓存队列空间使用大小：[" + submitListDoneCache.size() + ", 待插入数据大小：" + beans.size()
								+ ", 暂停处理500毫秒。");
						Thread.sleep(500);
					}
				} catch (Exception e) {

				}

			}

		} catch (Exception e) {
			log.error("addSubmitListDoneCache[" + submitListDoneCache.size() + "] lost file " + beans, e);
		}

		return result;
	}
 
	public static boolean addSubmitListDoneObjectCache(List<Object> beans) {
		if(beans!=null){
			for(Object o:beans){
				SmsMessage bean =(SmsMessage) o;
				addSubmitListDoneCache(bean);
			 }
		}

		return true;
	}
	
	
	public static List<BackFileForm> getSubmitDataFileList(int limit) {
		List<BackFileForm> list = new ArrayList<BackFileForm>();
		for (int i = 0; i < limit; i++) {
			BackFileForm sms = submitDataFileQueue.poll();
			if (sms != null) {
				list.add(sms);
			} else {
				break;
			}
		}
		return list;
	}
	public static void addSubmitDataFileQueue(List<BackFileForm> list) {
		boolean flag = false;
		try {
			flag = submitDataFileQueue.addAll(list);
		} catch (Exception e) {
			log.error("addSubmitDataFileQueue[" + submitDataFileQueue.size() + "] lost file " + list);
		}
		if (!flag) {
			log.error("addSubmitDataFileQueue[" + submitDataFileQueue.size() + "] lost file " + list);
		}
	}
	
	public static void addSubmitDataFileQueue(BackFileForm sms) {
		boolean result = false;
		try {
			result = submitDataFileQueue.offer(sms);
			if (!result) {
				log.error("addSubmitDataFileQueue[" + submitDataFileQueue.size() + "] add file fail " + sms);
			}
		} catch (Exception e) {
			log.error("addSubmitDataFileQueue[" + submitDataFileQueue.size() + "] add file fail " + sms, e);
		}
	}
	
	/**
	 * 缓存文件的大小
	 * 
	 * @return
	 */
	public static int getSubmitDataFileSize() {
		return submitDataFileQueue.size();
	}
	
	/**
	 * 发送模块redis是否正常
	 */
	private static boolean redis_is_ok = true;
	
	public static boolean isRedis_is_ok() {
		return redis_is_ok;
	}

	public static void setRedis_is_ok(boolean redis_is_ok) {
		DataCenter_old.redis_is_ok = redis_is_ok;
	}


	/**
	 * 需要走重复过滤拦截的短信
	 */
	private static BlockingQueue<SmsMessage> repeatMessageQueue = new ArrayBlockingQueue<SmsMessage>(100000);
	
	public static BlockingQueue<Runnable> getSubmitMessageProductWorkQueue(){
		return submitMessageProductWorkQueue;
	}
	
	public static BlockingQueue<Runnable> getReceiveReportConsumeWorkQueue(){
		return receiveReportConsumeWorkQueue;
	}
	
	public static BlockingQueue<Runnable> getArriveReplyListInDbWorkQueue(){
		return arriveReplyListInDbWorkQueue;
	}
	
	public static BlockingQueue<Runnable> getDataToDbWorkQueue(){
		return dataToDbWorkQueue;
	}
	
	public static ExecutorService getDataToDbPool(){
		return dataToDbPool;
	}
	
	public static void AddRepeatMessageQueue(SmsMessage sms) {
		try {
			repeatMessageQueue.add(sms);
		} catch (Exception e) {
			log.error("lost sms " + sms);
		}
	}
	
	public static List<SmsMessage> getRepeatMessageQueue(int limit) {
		List<SmsMessage> list = new ArrayList<SmsMessage>();
		SmsMessage sms = null;
		for(int i=0;i<limit;i++){
			sms = repeatMessageQueue.poll();
			if(sms!=null){
				list.add(sms);
			}else{
				break;
			}
		}
		return list ;
	}

	public static Long  getQueueSetSize(String key){
		if(queue_set_size.containsKey(key)){
			long tmp = queue_set_size.get(key)+1L;
			queue_set_size.put(key, tmp);
			return tmp;
		}else {
			queue_set_size.put(key, 1L);
			return 1L ;
		}
	}
	
	public static void  RemoveQueueSet(String key){
		if(queue_set_size.containsKey(key)){
			queue_set_size.remove(key);
		} 
	}
	
	public static void  SetQueueSet(String key){
		queue_set_size.put(key, 1L);
	}
	
	
	static {
		File file = new File(System.getProperty("user.dir") + "/shutDownCache");
		if(file.exists()){
			for(File f : file.listFiles()){
				try {
					String fileName = f.getName();
					String fieldName = fileName.substring(0,fileName.indexOf("."));
					Field field = DataCenter_old.class.getDeclaredField(fieldName);
					
					if(Modifier.isTransient(field.getModifiers())){
						 log.info("skip "+f.getName()+",resut: "+f.delete());
						 continue;
					 }
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
					Object value = ois.readObject();
					ois.close();
					field.set(null, value);
				} catch (Exception e) {
					log.error("fieldName: "+f.getName(),e);
					boolean deleted = f.delete();
					log.debug("file[{}] delete {}", f.getName(), deleted);
				} 
			}
		}
		log.info("---------< DataCenter Init completed >------------");
	}
	public static void printStatisticsSize(){
		StringBuffer sb =new StringBuffer();
		 sb.append("\r\n---------<StatisticsSize- print ↓ size>------------\r\n")
		 .append("statistics_day_size: "+statistics_day.size()).append("\r\n")
		 .append("statistics_hour_size: "+statistics_hour.size()).append("\r\n")
		 .append("statistics_min_size: "+statistics_min.size()).append("\r\n")
		 .append("---------<print ↑ size>------------").append("\r\n");
		 log.info(sb.toString());
	}
	public static void printSize(){
	 
		StringBuffer sb =new StringBuffer();
		 sb.append("\r\n---------<DataCenter_old- print ↓ size>------------\r\n")
		.append("SystemCheckQueue_size: "+SystemCheckQueue.size()).append("\r\n")
		.append("LongMessageInsertDBQueue_size: "+LongMessageInsertDBQueue.size()).append("\r\n")
		.append("submitMessageQueue_size: "+submitMessageQueue.size()).append("\r\n")
		.append("rejectMessageQueue_size: "+rejectMessageQueue.size()).append("\r\n")
		.append("manualCheckMessageQueue_size: "+manualCheckMessageQueue.size()).append("\r\n")
		.append("avoidDisturbMessageQueue_size: "+avoidDisturbMessageQueue.size()).append("\r\n")
		.append("submitHistoryQueue_size: "+submitHistoryQueue.size()).append("\r\n")
		.append("submitHistoryCacheQueue_size: "+submitHistoryCacheQueue.size()).append("\r\n")
		.append("sentMessageToDBQueue_size(没有完成redis状态匹配的下发数据): "+sentMessageToDBQueue.size()).append("\r\n")
		.append("receiveReportQueue_size: "+receiveReportQueue.size()).append("\r\n")
		.append("sendReportQueue_size: "+sendReportQueue.size()).append("\r\n")
		.append("submitMessageProductWorkQueue_size: "+submitMessageProductWorkQueue.size()).append("\r\n")
		.append("unmatchedReportToDBQueue_size: "+unmatchedReportToDBQueue.size()).append("\r\n")
		.append("submitHistoryFileQueue_size(下发完成数据入库较慢，造成该队列数据增加): "+submitHistoryFileQueue.size()).append("\r\n")
		.append("sentReportToDBQueue_size: "+sentReportToDBQueue.size()).append("\r\n")
		.append("receiveDeliverQueue_size: "+receiveDeliverQueue.size()).append("\r\n")
		.append("dealedDeliverQueue_size: "+dealedDeliverQueue.size()).append("\r\n")
		.append("sentDeliverToDBQueue_size: "+sentDeliverToDBQueue.size()).append("\r\n")
		.append("dealedReportToCacheQueue_size(已经完成状态匹配的状态队列): "+dealedReportToCacheQueue.size()).append("\r\n")
		.append("catchMessageMap_size: "+catchMessageMap.size()).append("\r\n")
		.append("catchMessageTimeMap_size: "+catchMessageTimeMap.size()).append("\r\n")
		.append("dataToDbWorkQueue_size: "+dataToDbWorkQueue.size()).append("\r\n")
		.append("submitListDoneCache_Size（二级缓存队列大小）: " + submitListDoneCache.size()).append("\n")
		.append("submitDataFileQueue_Size（缓存文件的大小）: " + submitDataFileQueue.size()).append("\n")
		.append("exceptionalMobileQueue_size: "+exceptionalMobileQueue.size()).append("\r\n")
		
		.append("---------<print ↑ size>------------").append("\r\n");
		log.info(sb.toString());
	}
	
	public static  void addExceptionalMobileQueue(List<SmsMessage> list ){
		  boolean  flag = false;
			try {
				flag = exceptionalMobileQueue.addAll(list);
			} catch (Exception e) {
				log.error("addExceptionalMobileQueue["+exceptionalMobileQueue.size()+"] lost file "+list);
			}
			if(!flag){
				log.error("addExceptionalMobileQueue["+exceptionalMobileQueue.size()+"] lost file "+list);
			}
	}
	
	public static  void addExceptionalMobileQueue(SmsMessage sms ){
		  boolean  flag = false;
			try {
				flag = exceptionalMobileQueue.add(sms);
			} catch (Exception e) {
				log.error("addExceptionalMobileQueue["+exceptionalMobileQueue.size()+"] lost file "+sms);
			}
			if(!flag){
				log.error("addExceptionalMobileQueue["+exceptionalMobileQueue.size()+"] lost file "+sms);
			}
	}
	
    public static  List<SmsMessage>  getExceptionalMobileQueue(int limit ){
    	List<SmsMessage> result = new ArrayList<SmsMessage>();
    	for(int i=0;i<limit;i++){
    		SmsMessage sms =exceptionalMobileQueue.poll();
    		if(sms!=null){
    			result.add(sms);
			}else{
				break ;
			}
    	}
    	 return result ;
    }
	
	 
	
	
	public static  void addSubmitHistoryFileQueue(List<BackFileForm> list) {
	     boolean  flag = false;
		try {
			flag = submitHistoryFileQueue.addAll(list);
		} catch (Exception e) {
			log.error("addSubmitHistoryFileQueue["+submitHistoryFileQueue.size()+"] lost file "+list);
		}
		if(!flag){
			log.error("addSubmitHistoryFileQueue["+submitHistoryFileQueue.size()+"] lost file "+list);
		}
	}
	public static void  addSubmitHistoryFileQueue(BackFileForm sms) {
		boolean result = false ;
		try {
			result = submitHistoryFileQueue.offer(sms);
			if(!result){
				log.error("addSubmitHistoryFileQueue["+submitHistoryFileQueue.size()+"] lost file "+sms);
			}
		} catch (Exception e) {
			log.error("addSubmitHistoryFileQueue["+submitHistoryFileQueue.size()+"] lost file "+sms,e);
		}
	}
	
	public static List<BackFileForm> getSubmitHistoryFileList(int limit) {
		List<BackFileForm> list = new ArrayList<BackFileForm>();
		for(int i=0;i<limit;i++){
			BackFileForm sms = submitHistoryFileQueue.poll();
			if(sms!=null){
				list.add(sms);
			}else{
				break ;
			}
		}
		return list ;
	}
	
	
	
	public static boolean doStop(){
		boolean result = false;
		File file = new File(System.getProperty("user.dir") + "/shutDownCache");
		if(!file.exists()){
			file.mkdirs();
		}
		
        try {
        	dataToDbPool.shutdown();
		} catch (Exception e) {
			log.error("", e);
		}
		
		try {
			for(Field field : DataCenter_old.class.getDeclaredFields()){
				 if(!Modifier.isTransient(field.getModifiers())){
					 File cacheFile = new File(System.getProperty("user.dir") + "/shutDownCache/" + field.getName() + ".dat");
						ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
						oos.writeObject(field.get(null));
						oos.flush();
						oos.close();
				 }
			}
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}  
		log.info("----------> DataCenter closed <----------");
		return result;
	}
	
	public static boolean addSentDeliverToDBQueue(DeliverBean sms){
		boolean result = false;
		try{
		  sentDeliverToDBQueue.put(sms);
		  result = true ;
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addSentDeliverToDBQueue(List<DeliverBean> smsList){
		boolean result = false;
		try{
			if(smsList != null){
				while (sentDeliverToDBQueue.remainingCapacity()<smsList.size()) {
					try {
						Thread.sleep(500);
						log.warn("sentDeliverToDBQueue space ["+sentDeliverToDBQueue.remainingCapacity()+"] is less than sms'size ["+smsList.size()+"]");
					} catch (Exception e) {
					}
				}
				result = sentDeliverToDBQueue.addAll(smsList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static DeliverBean getSentDeliverToDB(){
		DeliverBean result = null;
		try{
			if(!sentDeliverToDBQueue.isEmpty()){
				result = sentDeliverToDBQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	public static boolean addSentReportToDBQueue(ReportBean sms){
		boolean result = false;
		try{
			if(sms != null){
				sentReportToDBQueue.put(sms);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addSentReportToDBQueue(List<ReportBean> smsList){
		boolean result = false;
		try{
			if(smsList != null){
				int capacity =sentReportToDBQueue.remainingCapacity();
				while (capacity<smsList.size()) {
					try {
						Thread.sleep(1000);
						capacity =sentReportToDBQueue.remainingCapacity();
						log.warn("sentReportToDBQueue space ["+capacity+"] is less than sms'size ["+smsList.size()+"]");
					} catch (Exception e) {
						log.error("", e);
					}
				}
				result = sentReportToDBQueue.addAll(smsList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static ReportBean getSentReportToDB(){
		ReportBean result = null;
		try{
			if(!sentReportToDBQueue.isEmpty()){
				result = sentReportToDBQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	/**
	 * 能取到则放入结果，不能取到结果，则直接break.
	 * @param limit
	 * @return
	 */
	public static List<ReportBean>  getSentReportToDB(int limit ){
		List<ReportBean> result = new ArrayList<ReportBean>();
		try{
			for(int i=0;i<limit;i++){
				ReportBean bean = sentReportToDBQueue.poll();
				if(bean!=null){
					result.add(bean);
				}else{
					break ;
				}
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}

	
	public static boolean addSentMessageToDBQueue(SmsMessage sms){
		boolean result = false;
		try{
			if(sms != null){
				sentMessageToDBQueue.put(sms);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addSentMessageToDBQueue(List<SmsMessage> smsList){
		boolean result = false;
		try{
			if(smsList != null){
				while (sentMessageToDBQueue.remainingCapacity()<smsList.size()) {
					try {
						Thread.sleep(500);
						log.warn("sentMessageToDBQueue space ["+sentMessageToDBQueue.remainingCapacity()+"] is less than sms'size ["+smsList.size()+"]");
					} catch (Exception e) {
					}
				}
				result = sentMessageToDBQueue.addAll(smsList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static SmsMessage getSentMessageToDB(){
		SmsMessage result = null;
		try{
			if(!sentMessageToDBQueue.isEmpty()){
				result = sentMessageToDBQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
  
	public static boolean addDealedDeliver(DeliverBean deliver){
		boolean result = false;
		try{
			if(deliver != null){
				dealedDeliverQueue.put(deliver);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addDealedDeliver(List<DeliverBean> deliverList){
		boolean result = false;
		try{
			if(deliverList != null){
				while (dealedDeliverQueue.remainingCapacity()<deliverList.size()) {
					try {
						Thread.sleep(500);
						log.warn("dealedDeliverQueue space ["+dealedDeliverQueue.remainingCapacity()+"] is less than sms'size ["+deliverList.size()+"]");
					} catch (Exception e) {
					}
				}
				result = dealedDeliverQueue.addAll(deliverList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static DeliverBean getDealedDeliver(){
		DeliverBean result = null;
		try{
			if(!dealedDeliverQueue.isEmpty()){
				result = dealedDeliverQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	public static boolean addReceiveDeliver(DeliverBean deliver){
		boolean result = false;
		try{
			if(deliver != null){
				receiveDeliverQueue.put(deliver);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addReceiveDeliver(List<DeliverBean> deliverList){
		boolean result = false;
		try{
			if(deliverList != null){
				while (receiveDeliverQueue.remainingCapacity()<deliverList.size()) {
					try {
						Thread.sleep(500);
						log.warn("receiveDeliverQueue space ["+receiveDeliverQueue.remainingCapacity()+"] is less than sms'size ["+deliverList.size()+"]");
					} catch (Exception e) {
					}
				}
				result = receiveDeliverQueue.addAll(deliverList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static DeliverBean getReceiveDeliver(){
		DeliverBean result = null;
		try{
			if(!receiveDeliverQueue.isEmpty()){
				result = receiveDeliverQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addAvoidDisturbMessage(SmsMessage smsMessage){
		boolean result = false;
		try{
			if(smsMessage != null){
				avoidDisturbMessageQueue.put(smsMessage);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static SmsMessage getAvoidDisturbMessage(){
		SmsMessage result = null;
		try{
			if(!avoidDisturbMessageQueue.isEmpty()){
				result = avoidDisturbMessageQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addSystemCheckMessage(SmsMessage smsMessage){
		boolean result = false;
		try{
			if(smsMessage != null){
				SystemCheckQueue.put(smsMessage);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addSystemCheckMessage(List<SmsMessage> smsList){
		boolean result = false;
		try{
			if(smsList != null){
				while (SystemCheckQueue.remainingCapacity()<smsList.size()) {
					try {
						Thread.sleep(500);
						log.warn("SystemCheckQueue space ["+SystemCheckQueue.remainingCapacity()+"] is less than sms'size ["+smsList.size()+"]");
					} catch (Exception e) {
					}
				}
				result = SystemCheckQueue.addAll(smsList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static SmsMessage getSystemCheckMessage(){
		SmsMessage result = null;
		try{
			if(!SystemCheckQueue.isEmpty()){
				result = SystemCheckQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addInsertDBLongMessage(SmsMessage smsMessage){
		boolean result = false;
		try{
			if(smsMessage != null){
				LongMessageInsertDBQueue.put(smsMessage);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addInsertDBLongMessage(List<SmsMessage> smsList){
		boolean result = false;
		try{
			if(smsList != null){
				while (LongMessageInsertDBQueue.remainingCapacity()<smsList.size()) {
					try {
						Thread.sleep(500);
						log.warn("LongMessageInsertDBQueue space ["+LongMessageInsertDBQueue.remainingCapacity()+"] is less than sms'size ["+smsList.size()+"]");
					} catch (Exception e) {
					}
				}
				result = LongMessageInsertDBQueue.addAll(smsList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static SmsMessage getInsertDBLongMessage(){
		SmsMessage result = null;
		try{
			if(!LongMessageInsertDBQueue.isEmpty()){
				result = LongMessageInsertDBQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}

	public static boolean addSubmitMessage(SmsMessage smsMessage){
		boolean result = false;
		try{
			if(smsMessage != null){
				 submitMessageQueue.put(smsMessage);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addSubmitMessage(List<SmsMessage> smsList){
		boolean result = false;
		try{
			if(smsList != null){
				if(smsList.size()>10000){
					spicSmsList(smsList);
				}else {
					while (submitMessageQueue.remainingCapacity()<smsList.size()) {
						try {
							Thread.sleep(500);
							spicSmsList(smsList);
							log.warn("submitMessageQueue space ["+submitMessageQueue.remainingCapacity()+"] is more than sms'size ["+smsList.size()+"]");
						} catch (Exception e) {
						}
					}
					result = submitMessageQueue.addAll(smsList);
				}
				
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	private static void spicSmsList(List<SmsMessage> smsList){
		List<SmsMessage> result= new ArrayList<SmsMessage>();
		if(smsList!=null&&smsList.size()>0){
			for(SmsMessage sms:smsList){
				result.add(sms);
				if(result.size()>1000){
					addSubmitMessage(result);
					result= new ArrayList<SmsMessage>();
				}
			}
		}
		if(result.size()>1){
			addSubmitMessage(result);
		}
	} 
	
	public static boolean addReceiveReportQueue(List<ReportBean> smsList){
		boolean result = false;
		try{
			if(smsList != null){
				while (receiveReportQueue.remainingCapacity()<smsList.size()) {
					try {
						Thread.sleep(500);
						log.warn("receiveReportQueue space ["+receiveReportQueue.remainingCapacity()+"] is less than sms'size ["+smsList.size()+"]");
					} catch (Exception e) {
					}
				}
				result = receiveReportQueue.addAll(smsList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addReceiveReportQueue(ReportBean sms){
		boolean result = false;
		try{
			if(sms != null){
				receiveReportQueue.put(sms);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static ReportBean getReceiveReport(){
		ReportBean result = null;
		try{
			if(!receiveReportQueue.isEmpty()){
				result = receiveReportQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}

	public static SmsMessage getSubmitMessage(){
		SmsMessage result = null;
		try{
			if(!submitMessageQueue.isEmpty()){
				result = submitMessageQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static List<SmsMessage> getSubmitMessage(int limit){
		 List<SmsMessage>  list = new ArrayList<SmsMessage>(); 
		 for(int i=0;i<limit;i++){
			 try {
				 SmsMessage  sms =  submitMessageQueue.poll();
				 if(sms!=null){
					 list.add(sms);
				 }else{
					 break;
				 }
			} catch (Exception e) {
			}
		 }
		return list;
	}
	
	public static boolean addRejectMessage(SmsMessage smsMessage){
		boolean result = false;
		try{
			if(smsMessage != null){
				rejectMessageQueue.put(smsMessage);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addRejectMessage(List<SmsMessage> smsList){
		boolean result = false;
		try{
			if(smsList != null){
				while (rejectMessageQueue.remainingCapacity()<smsList.size()) {
					try {
						Thread.sleep(500);
						log.warn("rejectMessageQueue space ["+rejectMessageQueue.remainingCapacity()+"] is less than sms'size ["+smsList.size()+"]");
					} catch (Exception e) {
					}
				}
				
				result = rejectMessageQueue.addAll(smsList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static SmsMessage getRejectMessage(){
		SmsMessage result = null;
		try{
			if(!rejectMessageQueue.isEmpty()){
				result = rejectMessageQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addManualCheckMessage(SmsMessage smsMessage){
		boolean result = false;
		try{
			if(smsMessage != null){
				manualCheckMessageQueue.put(smsMessage);
				result =  true ;
			}
		}catch(Exception e){
			log.error("addManualCheckMessage exception", e);
		}
		return result;
	}
	
	public static SmsMessage getManualCheckMessage(){
		SmsMessage result = null;
		try{
			if(!manualCheckMessageQueue.isEmpty()){
				result = manualCheckMessageQueue.remove();
			}
		}catch(Exception e){
			log.error("getManualCheckMessage exception", e);
		}
		return result;
	}
	
	/**
	 * 把下发完成的历史数据放到待入库的队列中
	 * 若一级缓存队列满后，转到存到二级缓存队列，二级缓存队列中
	 * 的数据将被写入本地文件中
	 * @param smsList
	 * @return
	 */
	public static boolean addSubmitHistoryQueue(List<SmsMessage> smsList) {
		boolean result = false;
		if (smsList != null) {
			result = submitHistoryQueue.addAll(smsList);
			if(!result){
				result =submitHistoryCacheQueue.addAll(smsList);
				if(!result){
					log.error("addSubmitHistoryQueue["+submitHistoryCacheQueue.size()+"] lost sms "+smsList);
				}
			}
		}
		return result;
	}
	
	/**
	 * 把下发完成的历史数据放到待入库的队列中
	 * 若一级缓存队列满后，转到存到二级缓存队列，二级缓存队列中
	 * 的数据将被写入本地文件中
	 * @param smsList
	 * @return
	 */
	public static boolean addSubmitHistoryQueue(SmsMessage smsMessage){
		boolean result = false;
		try{
			if(smsMessage != null){
				result = submitHistoryQueue.offer(smsMessage);
				if(!result){
					result =submitHistoryCacheQueue.offer(smsMessage);
					if(!result){
						log.error("addSubmitHistoryQueue["+submitHistoryCacheQueue.size()+"] lost sms "+smsMessage);
					}
				}
			}
		}catch(Exception e){
			log.error("addSubmitHistoryQueue["+submitHistoryCacheQueue.size()+"] lost sms "+smsMessage);
		}
		return result;
	}
	
	public static boolean addSubmitHistoryCacheQueue(List<SmsMessage> list){
		boolean result = false;
		try{
			result = submitHistoryCacheQueue.addAll(list);
		}catch(Exception e){
			log.error("submitHistoryCacheQueue["+submitHistoryCacheQueue.size()+"] lost sms "+list);
		}
		return result;
	}
	
	
	
	 
	
	public static List <SmsMessage> getSubmitHistory(int limit ){
		List <SmsMessage> list = new ArrayList<SmsMessage>();
		for(int i=0;i<limit;i++){
			SmsMessage sms =submitHistoryQueue.poll();
			if(sms!=null){
				list.add(sms);
			}else{
				break ;
			}
		}
		return list;
	}
	
	public static List <SmsMessage> submitHistoryCache(int limit ){
		List <SmsMessage> list = new ArrayList<SmsMessage>();
		for(int i=0;i<limit;i++){
			SmsMessage sms =submitHistoryCacheQueue.poll();
			if(sms!=null){
				list.add(sms);
			}else{
				break ;
			}
		}
		return list;
	}
	
	public static boolean addSendReportQueue(List<ReportBean> smsList){
		boolean result = false;
		try{
			if(smsList != null){
				while (sendReportQueue.remainingCapacity()<smsList.size()) {
					try {
						Thread.sleep(500);
						log.warn("sendReportQueue space ["+sendReportQueue.remainingCapacity()+"] is less than sms'size ["+smsList.size()+"]");
					} catch (Exception e) {
					}
				}
				result = sendReportQueue.addAll(smsList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addSendReportQueue(ReportBean smsMessage){
		boolean result = false;
		try{
			if(smsMessage != null){
				sendReportQueue.put(smsMessage);
				result =  true ;
			}
		}catch(Exception e){
			log.error(smsMessage.toString(),e);
		}
		return result;
	}
	
	public static ReportBean getSendReport(){
		ReportBean result = null;
		try{
			if(!sendReportQueue.isEmpty()){
				result = sendReportQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static boolean addUnmatchedReportToDBQueue(List<ReportBean> smsList){
		boolean result = false;
		try{
			if(smsList != null){
				while (unmatchedReportToDBQueue.remainingCapacity()<smsList.size()) {
					try {
						Thread.sleep(500);
						log.warn("unmatchedReportToDBQueue space ["+unmatchedReportToDBQueue.remainingCapacity()+"] is less than sms'size ["+smsList.size()+"]");
					} catch (Exception e) {
					}
				}
				result = unmatchedReportToDBQueue.addAll(smsList);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	
	public static boolean addUnmatchedReportToDBQueue(ReportBean smsMessage){
		boolean result = false;
		try{
			if(smsMessage != null){
				unmatchedReportToDBQueue.put(smsMessage);
				result =  true ;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}
	
	public static ReportBean getUnmatchedReport(){
		ReportBean result = null;
		try{
			if(!unmatchedReportToDBQueue.isEmpty()){
				result = unmatchedReportToDBQueue.remove();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return result;
	}

	public static void setCatchMessageMap(Map<String, SmsMessage> catchMessageMap) {
		DataCenter_old.catchMessageMap = catchMessageMap;
	}

	public static Map<String, SmsMessage> getCatchMessageMap() {
		return catchMessageMap;
	}

	public static void setCatchMessageTimeMap(Map<String, Long> catchMessageTimeMap) {
		DataCenter_old.catchMessageTimeMap = catchMessageTimeMap;
	}

	public static Map<String, Long> getCatchMessageTimeMap() {
		return catchMessageTimeMap;
	}

	public static void setDealedReportToCacheQueue(BlockingQueue<ReportBean> dealedReportToCacheQueue) {
		DataCenter_old.dealedReportToCacheQueue = dealedReportToCacheQueue;
	}

	public static BlockingQueue<ReportBean> getDealedReportToCacheQueue() {
		return dealedReportToCacheQueue;
	}

	public static void putDealedReportToCacheQueue(ReportBean report){
		try {
			DataCenter_old.dealedReportToCacheQueue.put(report);
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	public static List<ReportBean>  getDealedReportToCacheQueue(int limit){
		List<ReportBean> list = new ArrayList<ReportBean>();
		for(int i=0;i<limit;i++){
			ReportBean bean = dealedReportToCacheQueue.poll();
			if(bean!=null){
				list.add(bean);
			}else{
				break;
			}
		}
		return list;
	}
	public static void putDealedReportToCacheQueue(List<ReportBean> reports){
		try {
			boolean flag = false;
			if(reports==null||reports.size()<1){
				return;
			}else {
				while (!flag) {
					try {
						flag = DataCenter_old.dealedReportToCacheQueue.addAll(reports);
					} catch (Exception e) {
						flag = false;
					}
					if(!flag){
						Thread.sleep(1000);
						log.warn("dealedReportToCacheQueue space ["+dealedReportToCacheQueue.remainingCapacity()+"] is less than sms'size ["+reports.size()+"]");
					}
					
				}
			}
			
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public static int getSystemCheckMessageSize() {
		return SystemCheckQueue.size();
	}
	
	public static int getLongMessageMessageSize() {
		return LongMessageInsertDBQueue.size();
	}


	public static Map<String, StatisticsHistoryModel> getStatistics_day() {
		return statistics_day;
	}


	public static Map<String, StatisticsModel> getStatistics_hour() {
		return statistics_hour;
	}
	

	public static Map<String, StatisticsModel> getStatistics_min() {
		return statistics_min;
	}
	
	
}
