package sunnyday.gateway.dc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import sunnyday.common.model.BackFileForm;
import sunnyday.common.model.DeliverBean;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SubmitBean;
import sunnyday.common.model.UserBalanceInfo;
import sunnyday.common.model.UserBean;
import sunnyday.gateway.thread.IControlService;
import sunnyday.gateway.util.HSResponse;
import sunnyday.gateway.util.ISolver;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.Spring;

public class DataCenter {
	private transient static Logger log = CommonLogFactory.getLog("monitor");
	private transient static Logger log_info = CommonLogFactory.getLog(DataCenter.class);

	private transient static HashMap<String, Long> user_deliver_count = new HashMap<String, Long>();

	private static boolean redis_is_ok = true;
	
	 


	public static HashMap<String, Long> getUser_deliver_count() {
		return user_deliver_count;
	}

	private transient static HashMap<String, Long> user_report_count = new HashMap<String, Long>();

	public static HashMap<String, Long> getUser_report_count() {
		return user_report_count;
	}

	private transient static BlockingQueue<Runnable> saveSubmitDoneListWorkQueue = new ArrayBlockingQueue<Runnable>(
			2000);

	public static BlockingQueue<Runnable> getSaveSubmitDoneListWorkQueue() {
		return saveSubmitDoneListWorkQueue;
	}

	/**
	 * 客户redis中的积压
	 */
	private transient static Map<String, Long> submit_user_map = new ConcurrentHashMap<String, Long>();

	public static Map<String, Long> getSubmit_user_map() {
		return submit_user_map;
	}

	public static void setSubmit_user_map(Map<String, Long> submit_user_map) {
		DataCenter.submit_user_map = submit_user_map;
	}

	
	public static boolean isRedis_is_ok() {
		return redis_is_ok;
	}

	public static void setRedis_is_ok(boolean redis_is_ok) {
		DataCenter.redis_is_ok = redis_is_ok;
	}

	// 用户连接数
	private transient static Map<String, Integer> userMap = new ConcurrentHashMap<String, Integer>();
	// 账户缓存
	private static Map<String, UserBean> userBeanMap = new HashMap<String, UserBean>();
	// 账户余额缓存
	private static Map<String, UserBalanceInfo> userBalanceMap = new ConcurrentHashMap<String, UserBalanceInfo>();

	private transient static Map<Integer, ISolver> httpMap = new HashMap<Integer, ISolver>();
	public final transient static Map<String, IControlService> controlServiceMap = new HashMap<String, IControlService>();

	public static Map<Integer, ISolver> getHttpMap() {
		return httpMap;
	}

	public static ISolver getHttpISolver(Integer key) {
		return httpMap.get(key);
	}

	private static long queue_size = 0;

	public static long getSubmitUserMap() {
		long count = 0;
		for (Long num : submit_user_map.values()) {
			count = count + num;
		}

		return count;
	}

	public static String getSubmitUserSize() {
		StringBuffer sb = new StringBuffer();
		if (submit_user_map != null && submit_user_map.size() > 0) {
			for (String key : submit_user_map.keySet()) {
				if (submit_user_map.get(key) > 0) {
					sb.append(key).append("_Size: ").append(submit_user_map.get(key)).append("\n");
				}
			}
		}
		return sb.toString();
	}

	// 计费接口map
	// public static HashMap<String, BalanceVerifiable> balanceValidMap = new
	// HashMap<String, BalanceVerifiable>();

	// 待扣费队列
	private static ConcurrentLinkedQueue<SubmitBean> submitListCharge = new ConcurrentLinkedQueue<SubmitBean>();
	// 返回响应队列
	private static ConcurrentLinkedQueue<SubmitBean> submitListResp = new ConcurrentLinkedQueue<SubmitBean>();
	// 提交完成队列
	private static ArrayBlockingQueue<SubmitBean> submitListDone = new ArrayBlockingQueue<SubmitBean>(50000);

	// 提交完成二级缓存队列 二级缓存队列中的数据写入文件
	private static ArrayBlockingQueue<SubmitBean> submitListDoneCache = new ArrayBlockingQueue<SubmitBean>(500000);

	// 定时短信缓存队列
	private static ArrayBlockingQueue<SubmitBean> submitTimeCache = new ArrayBlockingQueue<SubmitBean>(100000);

	// 待推送状态队列
	private static Map<String, BlockingQueue<ReportBean>> reportMap = new ConcurrentHashMap<String, BlockingQueue<ReportBean>>();
	// 待推送上行队列
	private static Map<String, BlockingQueue<DeliverBean>> deliverMap = new ConcurrentHashMap<String, BlockingQueue<DeliverBean>>();

	// 上行响应信息队列
	private static ArrayBlockingQueue<DeliverBean> deliverRespList = new ArrayBlockingQueue<DeliverBean>(10000);
	// 状态报告响应队列
	private static ArrayBlockingQueue<ReportBean> reportRespList = new ArrayBlockingQueue<ReportBean>(10000);

	private static Set<String> report_user_set = new HashSet<String>();

	private static Set<String> deliver_user_set = new HashSet<String>();

	private static BlockingQueue<BackFileForm> submitDataFileQueue = new ArrayBlockingQueue<BackFileForm>(100000);

	private static Properties utf8_prop = new Properties();

	public static String getUtf8_Pro_val(String key) {
		if (utf8_prop != null && !utf8_prop.isEmpty()) {
			String tmp = utf8_prop.getProperty(key);
			if (tmp != null) {
				return tmp.trim();
			}
		}
		return null;
	}

	public static void setUtf8_prop(Properties utf8_prop) {
		DataCenter.utf8_prop = utf8_prop;
	}

	/**
	 * redis中积压的待发短信队列的大小
	 * 
	 * @return
	 */
	public static long getQueue_size() {
		return queue_size;
	}

	public static void setQueue_size(long size) {
		queue_size = size;
	}

	public static List<SubmitBean> getsubmitListDoneCache(int limit) {
		List<SubmitBean> list = new ArrayList<SubmitBean>();
		for (int i = 0; i < limit; i++) {
			SubmitBean bean = submitListDoneCache.poll();
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

	public static boolean addSubmitListDoneCache(SubmitBean bean) {
		boolean result = false;
		try {
			submitListDoneCache.put(bean);
			result = true;
		} catch (Exception e) {
			log.error("addSubmitListDoneCache[" + submitListDoneCache.size() + "] lost file " + bean);
		}
		return result;
	}

	
	public static boolean addSubmitListDoneCache(List<SubmitBean> beans) {
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
	 * 获取缓存文件中积压的短信条数
	 * 
	 * @return
	 */
	public static int getSubmitDataFileMobileSize() {
		int result = 0;
		List<BackFileForm> fileForms = new ArrayList<BackFileForm>();
		while (true) {
			BackFileForm form = submitDataFileQueue.poll();
			if (form != null) {
				fileForms.add(form);
			} else {
				break;
			}
		}
		// 重新放回文件积压目录中
		if (fileForms.size() > 0) {
			submitDataFileQueue.addAll(fileForms);
			for (BackFileForm f : fileForms) {
				result = result + getFileSize(f.getFile_path());
			}
		}
		return result;
	}

	private static int getFileSize(String file_path) {
		int count = 0;
		try {
			String name = getFileName(file_path);
			if (name != null) {
				String[] arry = name.split("_");
				count = Integer.parseInt(arry[arry.length - 1].replace(".sms", ""));
			}
		} catch (Exception e) {
			log_info.error("", e);
		}
		return count;
	}

	private static String getFileName(String file_path) {
		String name = null;
		try {
			String[] arry = file_path.split("/");
			name = arry[arry.length - 1];
		} catch (Exception e) {
		}
		return name;
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

	/**
	 * redis中是否有客户的状态报告
	 * 
	 * @param user_id
	 * @return
	 */
	public static boolean hit_report_user_set(String user_id) {
		if (report_user_set != null) {
			return report_user_set.contains(user_id);
		}
		return false;
	}

	/**
	 * 初始化有存在状态报告信息的客户
	 * 
	 * @param set
	 * @param queue_name
	 *            q:report:wj12306
	 */

	public static void init_report_user_set(Set<String> set, String queue_name) {
		Set<String> tmp = new HashSet<String>();
		for (String key : set) {
			tmp.add(key.replace(queue_name, ""));
		}
		report_user_set = tmp;
	}

	/**
	 * redis中是否有客户的上行
	 * 
	 * @param user_id
	 * @return
	 */
	public static boolean hit_deliver_user_set(String user_id) {
		if (deliver_user_set != null) {
			return deliver_user_set.contains(user_id);
		}
		return false;
	}

	/**
	 * 初始化有存在上行信息的客户
	 * 
	 * @param set
	 * @param queue_name
	 *            q:report:wj12306
	 */

	public static void init_deliver_user_set(Set<String> set, String queue_name) {
		Set<String> tmp = new HashSet<String>();
		for (String key : set) {
			tmp.add(key.replace(queue_name, ""));
		}
		deliver_user_set = tmp;
	}

	/**
	 * 圆通接口错误响应
	 */
	private static ArrayBlockingQueue<HSResponse> yto_response = new ArrayBlockingQueue<HSResponse>(10000);

	@Autowired
	private transient static DeliverReportCenter deliverReportCenter = null;

	/**
	 * 发送有错误的，异步响应
	 * 
	 * @param responses
	 */
	public static void putYto_response(HSResponse response) {
		if (DataCenter.yto_response.remainingCapacity() > 0) {
			boolean flag = DataCenter.yto_response.offer(response);
			if (!flag) {
				log.error("丢弃数据：" + response);
			}
		} else {
			log.warn("yto_response 圆通接口错误响应队列空间不足....丢弃点[" + DataCenter.yto_response.poll() + "]");
		}
	}

	/**
	 * 发送有错误的，异步响应
	 * 
	 * @param responses
	 */
	public static void putYto_response(List<HSResponse> responses) {
		if (DataCenter.yto_response.remainingCapacity() >= responses.size()) {
			DataCenter.yto_response.addAll(responses);
		} else {
			for (int i = 0; i < responses.size(); i++) {
				log.warn("yto_response 圆通接口错误响应队列空间不足....丢弃点[" + DataCenter.yto_response.poll() + "]");
			}
		}
	}

	static {
		if (Spring.getApx() == null) {
			Spring.initFileSystemSpring(System.getProperty("user.dir") + "/config/bean-config.xml");
		}
		
		deliverReportCenter = Spring.getApx().getBean(DeliverReportCenter.class);
		File file = new File(System.getProperty("user.dir") + "/shutDownCache");
		if (file.exists()) {
			for (File f : file.listFiles()) {
				ObjectInputStream ois = null;
				FileInputStream fs = null;
				try {
					String fileName = f.getName();
					String fieldName = fileName.substring(0, fileName.indexOf("."));
					Field field = DataCenter.class.getDeclaredField(fieldName);
					if (Modifier.isTransient(field.getModifiers())) {
						log.info("skip " + fieldName + ", del:  " + f.delete());
						continue;
					}
					fs = new FileInputStream(f);
					ois = new ObjectInputStream(fs);
					Object value = ois.readObject();
					field.set(null, value);

				} catch (Exception e) {
					boolean flag = f.delete();
					log.error(f.getName() + " delete: " + flag, e);
				} finally {
					try {
						if (fs != null) {
							fs.close();
						}
					} catch (Exception e2) {
					}
					try {
						if (ois != null) {
							ois.close();
						}
					} catch (Exception e2) {
					}

				}
			}
		}
		log.info("---------< DataCenter Init completed >------------");
	}

	public static boolean doStop() {
		boolean result = false;
		File file = new File(System.getProperty("user.dir") + "/shutDownCache");
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			for (Field field : DataCenter.class.getDeclaredFields()) {
				FileOutputStream fs = null;
				ObjectOutputStream oos = null;
				try {
					if (Modifier.isTransient(field.getModifiers())) {
						log.info("skip " + field.getName());
						continue;
					}
					File cacheFile = new File(System.getProperty("user.dir") + "/shutDownCache/" + field.getName() + ".dat");

					fs = new FileOutputStream(cacheFile);
					oos = new ObjectOutputStream(fs);
					oos.writeObject(field.get(null));
					oos.flush();
					oos.close();
				} catch (Exception e) {
					log.error(field.getName() + " ", e);
				} finally {
					try {
						if (fs != null) {
							fs.close();
						}
					} catch (Exception e2) {
					}
					try {
						if (oos != null) {
							oos.close();
						}
					} catch (Exception e2) {
					}
				}

			}
			result = true;
		} catch (Exception e) {
			log.error(" ", e);
		}
		log.info("----------> DataCenter closed <----------");
		return result;
	}

	/**
	 * 发送短信时，不需要截取掉86
	 */
	private static Set<String> with_86_user = new HashSet<String>();
	private static Set<String> utf8_user_set = new HashSet<String>();
	private static Set<String> send_server_ip = new HashSet<String>();
	private static String send_server_ip_string = "";
	private static Set<String> use_code_0 = new HashSet<String>();

	public static Set<String> getUse_code_0() {
		if (use_code_0 == null) {
			use_code_0 = new HashSet<String>();
		}
		return use_code_0;
	}

	public static void setUse_code_0(Set<String> use_code_0) {
		DataCenter.use_code_0 = use_code_0;
	}

	public static String getSend_server_ip_string() {
		if (send_server_ip_string == null) {
			send_server_ip_string = "";
		}
		return send_server_ip_string;
	}

	public static void setSend_server_ip_string(String send_server_ip_string) {
		DataCenter.send_server_ip_string = send_server_ip_string;
	}

	public static Set<String> getSend_server_ip() {
		if (send_server_ip == null) {
			send_server_ip = new HashSet<String>();
		}
		return send_server_ip;
	}

	public static void setSend_server_ip(Set<String> send_server_ip) {
		DataCenter.send_server_ip = send_server_ip;
	}

	/**
	 * IP号段
	 */
	private static Set<String> other_user_set = new HashSet<String>();

	/**
	 * 只需要部分设置成utf-8编码
	 * 
	 * @return
	 */
	public static Set<String> getOther_user_set() {
		if (other_user_set == null) {
			other_user_set = new HashSet<String>();
		}
		return other_user_set;
	}

	public static void setOther_user_set(Set<String> other_user_set) {
		DataCenter.other_user_set = other_user_set;
	}

	public static boolean check_userip(String ip) {
		boolean result = false;
		try {
			result = utf8_user_set.contains(ip);
			if (!result) {
				if (other_user_set != null && !other_user_set.isEmpty()) {
					for (String var : other_user_set) {
						Pattern p1 = Pattern.compile("^" + var);
						Matcher m1 = p1.matcher(ip);
						if (m1.find()) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
		}

		return result;
	}

	/**
	 * 为utf-8的客户的user_id 多个客户用英文逗号,隔开
	 * 
	 * @return
	 */
	public static Set<String> getUtf8_user_set() {
		if (utf8_user_set == null) {
			utf8_user_set = new HashSet<String>();
		}
		return utf8_user_set;
	}

	public static void setUtf8_user_set(Set<String> utf8_user_set) {
		DataCenter.utf8_user_set = utf8_user_set;
	}

	public static Set<String> getWith_86_user() {

		if (with_86_user == null) {
			with_86_user = new HashSet<String>();
		}
		return with_86_user;
	}

	public static void setWith_86_user(Set<String> with_86_user) {
		DataCenter.with_86_user = with_86_user;
	}

	/**
	 * 判断用户是否应该把86去掉
	 * 
	 * @param user_id
	 * @return true 没有国际业务 false 包含国际业务
	 */
	public static boolean check_user_with86(String user_id) {
		boolean result = true;
		try {
			result = !with_86_user.contains(user_id);
		} catch (Exception e) {
		}
		return result;
	}

	public static void print() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n--------------QueueSize(↓)---------------\n")
				.append("submitListCharge_Size: " + submitListCharge.size()).append("\n")
				.append("submitListResp_Size: " + submitListResp.size()).append("\n")
				.append("submitListDone_Size（一级缓存队列大小）: " + submitListDone.size()).append("\n")
				.append("submitListDoneCache_Size（二级缓存队列大小）: " + submitListDoneCache.size()).append("\n")
				.append("deliverRespList_Size: " + deliverRespList.size()).append("\n")
				.append("reportRespList_Size: " + reportRespList.size()).append("\n")
				.append("submitDataFileQueue_Size（缓存文件的大小）: " + submitDataFileQueue.size()).append("\n")
				.append("MobileInFile_Size（缓存文件中积压的短信数量）: " + getSubmitDataFileMobileSize()).append("\n")
				.append("submitDoneList_Size（redis接收队列大小）: " + getQueue_size()).append("\n")
				.append("saveSubmitDoneListWorkQueue_Size: " + saveSubmitDoneListWorkQueue.size()).append("\n")
				.append(getSubmitUserSize()).append("-------------- ↑ ---------------\n");
		log.info(sb.toString());
	}

	public static void addReportRespMessage(ReportBean bean) {
		try {
			if (bean != null) {
				reportRespList.put(bean);
			}

		} catch (Exception e) {
			log_info.error("", e);
		}
	}

	public static void addReportRespMessage(List<ReportBean> beans) {
		boolean flag = true;
		try {
			while (flag) {
				flag = false;
				if (reportRespList.remainingCapacity() < beans.size()) {
					Thread.sleep(5);
					flag = true;
				} else {
					reportRespList.addAll(beans);
				}
			}
		} catch (Exception e) {
			log_info.error("", e);
		}

	}

	public static List<Object> queryReportRespMessage(int maxElements) {
		List<Object> list = new ArrayList<Object>();
		try {
			ReportBean tmp = reportRespList.poll();
			int count = 0;
			while (tmp != null) {
				list.add(tmp);
				count++;
				if (count >= maxElements) {
					break;
				}
				tmp = reportRespList.poll();
			}
		} catch (Exception e) {
			log_info.error("", e);
		}
		return list;
	}

	public static List<Object> queryDeliverRespMessage(int maxElements) {
		List<Object> list = new ArrayList<Object>();
		try {
			DeliverBean tmp = deliverRespList.poll();
			int count = 0;
			while (tmp != null) {
				list.add(tmp);
				count++;
				if (count >= maxElements) {
					break;
				}
				tmp = deliverRespList.poll();
			}
		} catch (Exception e) {
			log_info.error("", e);
		}
		return list;
	}

	/**
	 * 获取该用户待推动的状态报告
	 * 
	 * @param user_sn
	 * @return
	 */
	public static ReportBean getReportBean(String user_id) {
		ReportBean result = null;
		List<ReportBean> tmp = getReportBean(user_id, 1);
		if (tmp != null && tmp.size() > 0) {
			result = tmp.get(0);
		}
		return result;
	}

	public static List<ReportBean> getReportBean(String user_id, int limit) {
		List<ReportBean> result = deliverReportCenter.getReportList(user_id, limit);
		return result;
	}

	/**
	 * 获取该用户待推送的上行信息
	 * 
	 * @param user_sn
	 * @return
	 */
	public static DeliverBean getDeliverBean(String user_id) {
		DeliverBean result = null;
		List<DeliverBean> tmp = getDeliverBean(user_id, 1);
		if (tmp != null && tmp.size() > 0) {
			result = tmp.get(0);
		}
		return result;
	}

	public static List<DeliverBean> getDeliverBean(String user_id, int limit) {
		List<DeliverBean> result = deliverReportCenter.getDeliverBeanList(user_id, limit);
		return result;
	}

	public static ArrayBlockingQueue<DeliverBean> getDeliverRespList() {
		return deliverRespList;
	}

	public static void setDeliverRespList(ArrayBlockingQueue<DeliverBean> deliverRespList) {
		DataCenter.deliverRespList = deliverRespList;
	}

	public static ArrayBlockingQueue<ReportBean> getReportRespList() {
		return reportRespList;
	}

	public static void setReportRespList(ArrayBlockingQueue<ReportBean> reportRespList) {
		DataCenter.reportRespList = reportRespList;
	}

	public static Map<String, BlockingQueue<ReportBean>> getReportMap() {
		return reportMap;
	}

	public static void setReportMap(Map<String, BlockingQueue<ReportBean>> reportMap) {
		DataCenter.reportMap = reportMap;
	}

	public static Map<String, BlockingQueue<DeliverBean>> getDeliverMap() {
		return deliverMap;
	}

	public static void setDeliverMap(Map<String, BlockingQueue<DeliverBean>> deliverMap) {
		DataCenter.deliverMap = deliverMap;
	}

	public static ConcurrentLinkedQueue<SubmitBean> getSubmitListCharge() {
		return submitListCharge;
	}

	public static void setSubmitListCharge(ConcurrentLinkedQueue<SubmitBean> submitListCharge) {
		DataCenter.submitListCharge = submitListCharge;
	}

	public static ConcurrentLinkedQueue<SubmitBean> getSubmitListResp() {
		return submitListResp;
	}

	public static void setSubmitListResp(ConcurrentLinkedQueue<SubmitBean> submitListResp) {
		DataCenter.submitListResp = submitListResp;
	}
	/*
	 * public static ConcurrentLinkedQueue<SubmitBean> getSubmitListDone() {
	 * return submitListDone; }
	 */

	public static List<SubmitBean> getSubmitListDone(int limit) {
		List<SubmitBean> submitDoneList = new ArrayList<SubmitBean>();
		for (int i = 0; i < limit; i++) {
			SubmitBean submitBean = submitListDone.poll();
			if (submitBean != null) {
				submitDoneList.add(submitBean);
			} else {
				break;
			}
		}
		return submitDoneList;
	}

	/**
	 * 把接收到的短信写入到临时短信中
	 * 
	 * @param bean
	 */
	public static void addSubmitListDone(SubmitBean bean) {
		boolean result = false;
		try {
			result = submitListDone.offer(bean);
			if (!result) {
				result = submitListDoneCache.offer(bean);
				if (!result) {
					log.error("addSubmitListDone lost sms " + bean);
				}
			}
		} catch (Exception e) {
			log.error("addSubmitListDone lost sms " + bean);
		}
	}

	public static void addSubmitListDone(List<SubmitBean> beans) {
		boolean result = false;
		try {
			result = submitListDone.addAll(beans);
			if (!result) {
				result = submitListDoneCache.addAll(beans);
				if (!result) {
					log.error("addSubmitListDone lost sms " + beans);
				}
			}
		} catch (Exception e) {
			log.error("addSubmitListDone lost sms " + beans);
		}
	}

	/*
	 * public static void setSubmitListDone( ConcurrentLinkedQueue<SubmitBean>
	 * submitListDone) { DataCenter.submitListDone = submitListDone; }
	 */
	public static Map<String, UserBean> getUserBeanMap() {
		if (userBeanMap == null) {
			userBeanMap = new HashMap<String, UserBean>();
		}
		return userBeanMap;
	}

	public static UserBean getUserBean(String user_id) {
		if (userBeanMap == null) {
			userBeanMap = new HashMap<String, UserBean>();
		}
		return userBeanMap.get(user_id);
	}

	public static void setUserBeanMap(Map<String, UserBean> userBeanMap) {
		DataCenter.userBeanMap = userBeanMap;
	}

	public static Map<String, Integer> getUserMap() {
		return userMap;
	}

	public static void setUserMap(ConcurrentHashMap<String, Integer> userMap) {
		DataCenter.userMap = userMap;
	}

	public static void setUserBalanceMap(Map<String, UserBalanceInfo> userBalanceMap) {
		DataCenter.userBalanceMap = userBalanceMap;
	}

	public static Map<String, UserBalanceInfo> getUserBalanceMap() {
		return userBalanceMap;
	}

	public static List<SubmitBean> getSubmitTimeCache(int limit) {
		List<SubmitBean> list = new ArrayList<SubmitBean>();
		for (int i = 0; i < limit; i++) {
			SubmitBean bean = submitTimeCache.poll();
			if (bean != null) {
				list.add(bean);
			} else {
				break;
			}
		}
		return list;
	}

	public static boolean addSubmitTimeCache(List<SubmitBean> beans) {
		boolean result = false;
		try {
			while (!result) {
				try {
					result = submitTimeCache.addAll(beans);
				} catch (Exception e) {
				}
				if (!result) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			log.error("addSubmitTimeCache[" + submitTimeCache.size() + "] lost time_sms " + beans);
		}
		return result;
	}

	public static boolean addSubmitTimeCache(SubmitBean bean) {
		boolean result = false;
		try {
			while (!result) {
				try {
					result = submitTimeCache.add(bean);
				} catch (Exception e) {
				}
				if (!result) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			log.error("addSubmitTimeCache[" + submitTimeCache.size() + "] lost time_sms " + bean);
		}
		return result;
	}

}
