package sunnyday.channel.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import sunnyday.common.model.DeliverBean;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.UserBean;
import sunnyday.tools.util.CommonLogFactory;
 
public class DataCenter {
	private transient static Logger log = CommonLogFactory.getLog("monitor");
	//账户缓存
	private static Map<String, UserBean> userBeanMap = new  HashMap<String, UserBean>();
 
	//发送完成队列
	private static ArrayBlockingQueue<SmsMessage> sendSubmitMessageRespSuccessQueue = new ArrayBlockingQueue<SmsMessage>(50000);
	
	private static ArrayBlockingQueue<SmsMessage> sendSubmitMessageRespFailQueue = new ArrayBlockingQueue<SmsMessage>(50000);
	
	
	//接收网关 状态报告队列
	private static ArrayBlockingQueue<ReportBean> reportQueue = new ArrayBlockingQueue<ReportBean>(50000);
	
	
	//网关返回的状态报告，二级队列，若放到一级队列时，无法正常放入，则放到二级队列
	private static ArrayBlockingQueue<ReportBean> reportCacheQueue = new ArrayBlockingQueue<ReportBean>(50000);
	
	private transient static final BlockingQueue<Runnable> sentMessageConsumeWorkQueue = new ArrayBlockingQueue<Runnable>(2000);
	
	/**
	 * redis交互失败时，状态报告暂存队列
	 */
	private static ArrayBlockingQueue<ReportBean> report4FileQueue = new ArrayBlockingQueue<ReportBean>(50000);
	 
	/**
	 * redis交互失败时，提交记录暂存队列
	 */
	private static ArrayBlockingQueue<SmsMessage> sendSubmit4FileQueue = new ArrayBlockingQueue<SmsMessage>(50000);
	



	//文件缓存队列
//    private static BlockingQueue<BackFileForm> report4File = new ArrayBlockingQueue<BackFileForm>(50000);

	//文件缓存队列
//    private static BlockingQueue<BackFileForm> sendSubmit4File = new ArrayBlockingQueue<BackFileForm>(50000);
  
    /**
	 * 发送模块redis是否正常
	 */
	private static boolean redis_is_ok = true;
	
	
	public static int getReport4FileQueueSize() {
		return report4FileQueue.size();
	}


	public static int getSendSubmit4FileQueueSize() {
		return sendSubmit4FileQueue.size();
	}

    
    public static boolean isRedis_is_ok() {
		return redis_is_ok;
	}

	public static void setRedis_is_ok(boolean redis_is_ok) {
		DataCenter.redis_is_ok = redis_is_ok;
	}

/*	public static List<BackFileForm> getSendSubmit4File(int limit) {
        	List<BackFileForm> list = new ArrayList<BackFileForm>();
        	BackFileForm bean = null;
    		for(int i=0;i<limit;i++){
    			bean = sendSubmit4File.poll();
    			if(bean!=null){
    				list.add(bean);
    			}else {
    				break;
    			}
    		}
    		return list;
	}*/
	
/*	public static int getSendSubmit4FileSize() {
		return sendSubmit4File.size();
     }

    public static void addSendSubmit4File(BackFileForm bean) {
    	boolean result = false;
		try {
			result = sendSubmit4File.add(bean);
			if(!result){
				log.error("addSendSubmit4File lost: "+bean);
			}
		} catch (Exception e) {
			log.error("addSendSubmit4File lost: "+bean,e);
		}
	}*/

/*	public static void addSendSubmit4File(List<BackFileForm> list) {
		if(list!=null){
			for(BackFileForm bean:list){
				addSendSubmit4File(bean);
			}
		}
	}*/

/*	public static int getReport4FileSize() {
		return report4File.size();
  	}

	public static List<BackFileForm> getReport4File(int limit) {
    	List<BackFileForm> list = new ArrayList<BackFileForm>();
    	BackFileForm bean = null;
		for(int i=0;i<limit;i++){
			bean = report4File.poll();
			if(bean!=null){
				list.add(bean);
			}else {
				break;
			}
		}
		return list;
  	}*/


/*  	public static void addReport4File(BackFileForm bean) {
  		boolean result = false;
		try {
			result = report4File.add(bean);
			if(!result){
				log.error("addReport4File lost: "+bean);
			}
		} catch (Exception e) {
			log.error("addReport4File lost: "+bean,e);
		}
  	}*/
  	
/*	public static void addReport4File(List<BackFileForm> list) {
		if(list!=null){
			for(BackFileForm bean:list){
				addReport4File(bean);
			}
		}
  	}*/
	
	public static List<ReportBean> getReport4FileQueue(int limit) {
		List<ReportBean> list = new ArrayList<ReportBean>();
		ReportBean bean = null;
		for(int i=0;i<limit;i++){
			bean = report4FileQueue.poll();
			if(bean!=null){
				list.add(bean);
			}else {
				break;
			}
		}
		return list;
	}


	public static void addReport4FileQueue(ReportBean bean) {
		boolean result = false;
		try {
			result = report4FileQueue.add(bean);
			if(!result){
				log.error("addReport4FileQueue lost: "+bean);
			}
		} catch (Exception e) {
			log.error("addReport4FileQueue lost: "+bean,e);
		}
	}

	public static void addReport4FileQueue(List<ReportBean> list) {
		if(list!=null){
			for(ReportBean bean:list){
				addReport4FileQueue(bean);
			}
		}
	}

	public static List<SmsMessage> getSendSubmit4FileQueue(int limit) {
		List<SmsMessage> list = new ArrayList<SmsMessage>();
		SmsMessage bean = null;
		for(int i=0;i<limit;i++){
			bean = sendSubmit4FileQueue.poll();
			if(bean!=null){
				list.add(bean);
			}else {
				break;
			}
		}
		return list;
	}


	public static void addSendSubmit4FileQueue(SmsMessage bean) {
		boolean result = false;
		try {
			result = sendSubmit4FileQueue.add(bean);
			if(!result){
				log.error("addSendSubmit4FileQueue lost: "+bean);
			}
		} catch (Exception e) {
			log.error("addSendSubmit4FileQueue lost: "+bean,e);
		}
		 
	}
	
	public static void addSendSubmit4FileQueue(List<SmsMessage> list) {
		if(list!=null){
			for(SmsMessage bean:list){
				addSendSubmit4FileQueue(bean);
			}
		}
	}


	public static BlockingQueue<Runnable> getSentMessageConsumeWorkQueue(){
		return sentMessageConsumeWorkQueue;
	}
	
	
	public static List<Object> getReportListFromCacheQueue(int limit) {
		List<Object> list = new ArrayList<Object>();
		if(reportCacheQueue!=null&&reportCacheQueue.size()>0){
			for(int i=0;i<limit;i++){
				ReportBean bean = reportCacheQueue.poll();
				if(bean!=null){
					list.add(bean);
				}else{
					break ;
				}
			}
		}
		return  list;
	}

	public static void putReportCacheQueue(ReportBean reportBean) {
		if(reportBean!=null){
			try {
				reportCacheQueue.put(reportBean);
			} catch (Exception e) {
				log.error("putReportCacheQueue: "+reportBean,e);
			}
		}
	}

	


	//接收网关 接收上行队列
	private static ArrayBlockingQueue<DeliverBean> deliverQueue = new ArrayBlockingQueue<DeliverBean>(50000);
	
	//发送扫描队列
	private static ConcurrentHashMap<String, ArrayBlockingQueue<SmsMessage>> scanMap = new ConcurrentHashMap<String, ArrayBlockingQueue<SmsMessage>>();
	
	/**
	 * 当线程关闭的时候，用户队列回写
	 */
	private static ConcurrentHashMap<String, ArrayBlockingQueue<SmsMessage>> scanMap_closed = new ConcurrentHashMap<String, ArrayBlockingQueue<SmsMessage>>();
	
	private transient static final BlockingQueue<Runnable> dealDateCenterToRedisWorkQueue = new ArrayBlockingQueue<Runnable>(2000);
	
	public static BlockingQueue<Runnable> getDealDateCenterToRedisWorkQueue(){
		return dealDateCenterToRedisWorkQueue;
	}
	
	
	/**
	 * 由于下发网关不给响应而进行再次下发时，需要把待发数据重新放回待发队列
	 * @param key
	 * @param queue
	 */
	public static void putReSendQueue(String key,SmsMessage sms) {
		boolean flag = false ;
		ArrayBlockingQueue<SmsMessage> queue = null;
		try {
			if(!scanMap_closed.containsKey(key)){
				queue = new  ArrayBlockingQueue<SmsMessage>(10000);
				scanMap_closed.put(key, queue);
			} else {
				queue = scanMap_closed.get(key);
			}
			flag = queue.offer(sms);
		} catch (Exception e) {
			try {
				queue = new  ArrayBlockingQueue<SmsMessage>(10000);
				flag = queue.offer(sms);
				scanMap_closed.put(key, queue);
			} catch (Exception e1) {
				log.error("", e1);
			}
		}
		if(!flag){
			sms.setStatus(1);
			sms.setResponse(1000);
			sms.setFail_desc("重复下发时，由于回写队列已满，重发失败！");
			DataCenter.addSubmitRespMessage(sms);
		}
	}
	
	public static void putScanMap_closed(String key,ArrayBlockingQueue<SmsMessage> queue) {
		if(!scanMap_closed.containsKey(key)){
			scanMap_closed.put(key, queue);
		}else{
			boolean flag = scanMap_closed.get(key).addAll(queue);
			if(!flag){
				if(queue!=null&&queue.size()>0){
					for(SmsMessage sms:queue){
						sms.setStatus(1);
						sms.setResponse(1000);
						sms.setFail_desc("重复下发时，由于回写队列已满，重发失败！");
						DataCenter.addSubmitRespMessage(sms);
					}
				}
			}
		}
		
	}
	
	public static ConcurrentHashMap<String, ArrayBlockingQueue<SmsMessage>> getScanMap_closed() {
		return scanMap_closed;
	}

	 
	
    private transient static BlockingQueue<Runnable> scanMessageWorkQueue = new ArrayBlockingQueue<Runnable>(2000);
	
	
	public static BlockingQueue<Runnable> getScanMessageWorkQueue() {
		return scanMessageWorkQueue;
	}
	public static int   getScanMessageWorkQueueSize() {
		return scanMessageWorkQueue.size();
	}
	public static int   getScanMessageWorkQueueRemainingCapacity() {
		return scanMessageWorkQueue.remainingCapacity();
	}
	
/*	static{
		File file = new File(PathCache.shutdDownCacheDir + "/shutDownCache");
		if(file.exists()){
			for(File f : file.listFiles()){
				try {
					String fileName = f.getName();
					String fieldName = fileName.substring(0,fileName.indexOf("."));
					Field field = DataCenter.class.getDeclaredField(fieldName);
					if(Modifier.isTransient(field.getModifiers())){
						log.info("skip "+fieldName+", del:  "+f.delete());
						continue;
					}
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
					Object value = ois.readObject();
					ois.close();
					field.set(null, value);
					 
				} catch (Exception e) {
					log.error(f.getName()+" ",e);
					boolean deleted = f.delete();
					log.debug("file[{}] delete {}", f.getName(), deleted);
				}
			}
		}
		log.info("---------< DataCenter Init completed >------------");
	}
	
	public static boolean doStop() {
		boolean result = false;
		File file = new File(PathCache.shutdDownCacheDir + "/shutDownCache");
		if (!file.exists()) {
			file.mkdirs();
		}
		
		ObjectOutputStream oos = null;
		for (Field field : DataCenter.class.getDeclaredFields()) {
			try {
				if (Modifier.isTransient(field.getModifiers())) {
					log.info("skip " + field.getName());
					continue;
				}
				File cacheFile = new File(PathCache.shutdDownCacheDir + "/shutDownCache/" + field.getName() + ".dat");
				oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
				oos.writeObject(field.get(null));
				oos.flush();
			} catch (Exception e) {
				log.error(field.getName() + " ", e);
			} finally {
				try {
					if (oos != null) {
						oos.close();
					}
				} catch (IOException e) {
					log.error("", e);
				}
			}

		}
		
		result = true;

		log.info("----------> DataCenter closed <----------");
		return result;
	}*/
	
	/**
	 * 发送短信时，不需要截取掉86
	 */
	private static Set<String> with_86_user =new HashSet<String>();
	private static Set<String> utf8_user_set =new HashSet<String>();
	private static Set<String> send_server_ip =new HashSet<String>();
	private static String  send_server_ip_string = "";
	private static Set<String> use_code_0 =new HashSet<String>();
	public static Set<String> getUse_code_0() {
		if(use_code_0==null){
			use_code_0 =new HashSet<String>();
		}
		return use_code_0;
	}
	public static void setUse_code_0(Set<String> use_code_0) {
		DataCenter.use_code_0 = use_code_0;
	}
	public static String getSend_server_ip_string() {
		if(send_server_ip_string==null){
			send_server_ip_string = "";
		}
		return send_server_ip_string;
	}
	public static void setSend_server_ip_string(String send_server_ip_string) {
		DataCenter.send_server_ip_string = send_server_ip_string;
	}
	public static Set<String> getSend_server_ip() {
		if(send_server_ip==null){
			send_server_ip =new HashSet<String>();
		}
		return send_server_ip;
	}
	public static void setSend_server_ip(Set<String> send_server_ip) {
		DataCenter.send_server_ip = send_server_ip;
	}

	/**
	 * IP号段
	 */
	private static Set<String> other_user_set =new HashSet<String>();

	/**
     * 只需要部分设置成utf-8编码
     * @return
     */
	public static Set<String> getOther_user_set() {
		if(other_user_set==null){
			other_user_set =new HashSet<String>();
		}
		return other_user_set;
	}
	public static void setOther_user_set(Set<String> other_user_set) {
		DataCenter.other_user_set = other_user_set;
	}
	

	public static boolean check_userip(String ip){
		boolean result = false ;
		try {
			result = utf8_user_set.contains(ip);
			if(!result){
				if(other_user_set!=null&&!other_user_set.isEmpty()){
					for(String var: other_user_set){
						Pattern p1=Pattern.compile("^"+var);
						Matcher m1=p1.matcher(ip);
						if(m1.find()){
						return true ;
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	
		return result;
	}
	/**
	 * 为utf-8的客户的user_id 多个客户用英文逗号,隔开
	 * @return
	 */
	public static Set<String> getUtf8_user_set() {
		if(utf8_user_set==null){
			 utf8_user_set =new HashSet<String>();
		}
		return utf8_user_set;
	}
	public static void setUtf8_user_set(Set<String> utf8_user_set) {
		DataCenter.utf8_user_set = utf8_user_set;
	}
    public static Set<String> getWith_86_user() {
    	
    	if(with_86_user==null){
    		with_86_user =new HashSet<String>();
		}
		return with_86_user;
	}
    
	public static void setWith_86_user(Set<String> with_86_user) {
		DataCenter.with_86_user = with_86_user;
	}
	
	/**
	 * 判断用户是否应该把86去掉
	 * @param user_id
	 * @return
	 */
	public static boolean check_user_with86(String user_id){
		boolean result = true ;
		try {
			result = !with_86_user.contains(user_id);
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}
	
	public static void print(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n--------------QueueSize(↓)---------------\n")
		.append("sendSubmitMessageRespSuccessQueue_Size: "+sendSubmitMessageRespSuccessQueue.size()).append("\n")
		.append("sendSubmitMessageRespFailQueue_Size: "+sendSubmitMessageRespFailQueue.size()).append("\n")
		.append("reportQueue_Size: "+reportQueue.size()).append("\n")
		.append("reportCacheQueue_Size: "+reportCacheQueue.size()).append("\n")
		.append("deliverQueue_Size: "+deliverQueue.size()).append("\n")
		.append("scanMessageWorkQueue_Size: "+scanMessageWorkQueue.size()).append("\n")
		.append("dealDateCenterToRedisWorkQueue_Size: "+dealDateCenterToRedisWorkQueue.size()).append("\n")
		.append("-------------- ↑ ---------------\n");
		log.info(sb.toString());
	}
	
	public static boolean addDeliverMessage(DeliverBean deliverMessage){
		try{
			if(deliverMessage != null){
				deliverQueue.put(deliverMessage);
				return true;
			}

		}catch(Exception e){
			log.error("", e);
		}
		return false;
	}
	
	public static boolean addReportMessage(ReportBean reportMessage){
		try{
			if(reportMessage != null){
				return reportQueue.add(reportMessage);
			}

		}catch(Exception e){
			log.error("", e);
		}
		return false;
	}

	public static boolean addReportMessage(List<ReportBean> list) {
		if (list != null) {
			for (ReportBean bean : list) {
				addReportMessage(bean);
			}
		}
		return false;
	}
	
	public static boolean addSubmitRespMessage(SmsMessage sms){
		try{
			if(sms != null){
				if(sms.getMobile().startsWith("86")){
					String tmp =sms.getMobile();
					sms.setMobile(tmp.substring(2, tmp.length()));
				}
				if(sms.getResponse() == 1000){
					sendSubmitMessageRespSuccessQueue.put(sms);
				}else{//提交运营商失败
					sendSubmitMessageRespFailQueue.put(sms);
				}
				return true;
			}
		}catch(Exception e){
			log.error("", e);
		}
		return false;
	}
	public static boolean addSubmitRespMessage(List<SmsMessage> list){
		 if(list!=null){
			 for(SmsMessage sms:list){
				 addSubmitRespMessage(sms);
			 }
		 }
		return true;
	}
	
	
	public static List<Object> querySubmitRespFailMessage(int maxElements){
		List<Object> list = new ArrayList<Object>();
		try{
			SmsMessage tmp = sendSubmitMessageRespFailQueue.poll();
			int count = 0;
			while(tmp != null ){
				list.add(tmp);
				count++;
				if(count >= maxElements){
					break;
				}
				tmp = sendSubmitMessageRespFailQueue.poll();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return list;
	}
	 
	
	public static List<SmsMessage> querySubmitRespSucessMessage(int maxElements){
		List<SmsMessage> list = new ArrayList<SmsMessage>();
		try{
			SmsMessage tmp = sendSubmitMessageRespSuccessQueue.poll();
			int count = 0;
			while(tmp != null ){
				list.add(tmp);
				count++;
				if(count >= maxElements){
					break;
				}
				tmp = sendSubmitMessageRespSuccessQueue.poll();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return list;
	}
	
	public static List<Object> queryReportMessage(int maxElements){
		List<Object> list = new ArrayList<Object>();
		try{
			ReportBean tmp = reportQueue.poll();
			int count = 0;
			while(tmp != null ){
				list.add(tmp);
				count++;
				if(count >= maxElements){
					break;
				}
				tmp = reportQueue.poll();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return list;
	}
	
	public static List<Object> queryDeliverMessage(int maxElements){
		List<Object> list = new ArrayList<Object>();
		try{
			DeliverBean tmp = deliverQueue.poll();
			int count = 0;
			while(tmp != null ){
				list.add(tmp);
				count++;
				if(count >= maxElements){
					break;
				}
				tmp = deliverQueue.poll();
			}
		}catch(Exception e){
			log.error("", e);
		}
		return list;
	}

 
	 
	
	public static ConcurrentHashMap<String, ArrayBlockingQueue<SmsMessage>> getScanMap() {
		return scanMap;
	}

	public static void setScanMap( ConcurrentHashMap<String, ArrayBlockingQueue<SmsMessage>> scanMap) {
		DataCenter.scanMap = scanMap;
	}
 
	public static Map<String, UserBean> getUserBeanMap() {
		if(userBeanMap==null){
			userBeanMap = new HashMap<String, UserBean>();
		}
		return userBeanMap;
	}
	public static void setUserBeanMap(Map<String, UserBean> userBeanMap) {
		DataCenter.userBeanMap = userBeanMap;
	}
 
    
 
	
	
}
