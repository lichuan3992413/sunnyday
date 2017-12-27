package sunnyday.controller.util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import sunnyday.common.model.CheckMessage;
import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.UserBean;
import sunnyday.controller.cache.GateConfigCache;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;

public class UtilTool {
	private static Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	public final static Lock lock = new ReentrantLock();
	public final static Lock lock2 = new ReentrantLock();
	// private final static int TEAM = 100 ;

	public static String getMobileTeamNum(String mobile) {
		String team="130000";
		if(mobile!=null&&mobile.length()>6){
			team = mobile.substring(0,6);
		}
		return team;
	}
	public static int getClearSetDataInRedis() {
		int result = 10;
		String tmp = GateConfigCache.getValue("clear_set_data_in_redis");
		if(tmp!=null&&tmp.length()>6){
			 try {
				 result = Integer.parseInt(tmp.trim());
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return result;
	}
	
	public static String getSmsTeamNum(String send_time) {
		String team="2016-05-18-04:43";
		if(send_time!=null&&send_time.length()>16){
			team = send_time.substring(0,16).replace(" ", "-");
		}
		return team;
	}
	public static boolean isTimeOut(String time,int time_out){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
		try {
			return System.currentTimeMillis()-sdf.parse(time).getTime()>time_out;
		} catch (Exception e) {
			log.error("", e);
		}
		return false ;
	}
	
	public static String getLocalMac() {
		StringBuffer sb = new StringBuffer();
		try {
			Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
			while (enu.hasMoreElements()) {
				NetworkInterface networkInterface = (NetworkInterface) enu.nextElement();
				if (networkInterface != null) {
					byte[] mac = networkInterface.getHardwareAddress();
					String name = networkInterface.getName();
					if (mac != null && name.startsWith("eth")) {
						sb.append(getLocalMac(mac));
						break;
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return sb.toString();
	}

	private static String getLocalMac(byte[] mac) throws SocketException {
		// 获取网卡，获取地址
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				// sb.append("-");
			}
			// 字节转换为整数
			int temp = mac[i] & 0xff;
			String str = Integer.toHexString(temp);
			if (str.length() == 1) {
				sb.append("0" + str);
			} else {
				sb.append(str);
			}
		}
		return sb.toString().toUpperCase();

	}

	/**
	 * 状态报告，匹配分组数
	 * 
	 * @return
	 */
	public static int getTeamCount() {
		int result = 100;
		String s = GateConfigCache.getValue("team_count");
		if (s != null && !"".equals(s)) {
			try {
				result = Integer.parseInt(s);
			} catch (Exception e) {
			}
		}
		return result;
	}
	
	/**
	 * 状态报告，匹配分组数
	 * 
	 * @return
	 */
	public static boolean isDoFilterTemplateSms() {
		String s = GateConfigCache.getValue("isDoFilterTemplateSms");
		if (s != null && !"".equals(s)) {
			try {
				if("yes".equalsIgnoreCase(s)){
					return true;
				}
			} catch (Exception e) {
			}
		}
		return false;
	}

	/**
	 * 获取处理下发记录超时时长
	 * 
	 * @return
	 */
	public static int getDealSubmitFnInRedisTimeOut() {
		int result = 120000;
		String s = GateConfigCache.getValue("deal_submitFn_in_redis_timeOut");
		if (s != null && !"".equals(s)) {
			try {
				result = Integer.parseInt(s);
			} catch (Exception e) {
			}
		}
		return result;
	}

	/**
	 * 获取处理处理状态报告超时时长
	 * @return
	 */
	public static int getDealReportInRedisTimeOut() {
		int result = 1800000;
		String s = GateConfigCache.getValue("deal_report_in_redis_timeOut");
		if (s != null && !"".equals(s)) {
			try {
				result = Integer.parseInt(s);
			} catch (Exception e) {
			}
		}
		return result;
	}
	
	/**
	 * 获取处理处理状态报告超时时长
	 * @return
	 */
	public static String getBIFilePath() {
		String result = "/hskj/bi_file/sendHistory";
		String s = GateConfigCache.getValue("bi.file_path");
		if (s != null && !"".equals(s)) {
			try {
				result = s;
			} catch (Exception e) {
			}
		}
		return result;
	}
	/**
	 * 输出BI服务器ID
	 * @return
	 */
	public static String getBIServerId() {
		String result = "148";
		String s = GateConfigCache.getValue("bi.serverId");
		if (s != null && !"".equals(s)) {
			try {
				result = s;
			} catch (Exception e) {
			}
		}
		return result;
	}
	/**
	 * 服务器协议类型：CM SG SM cluster
	 * @return
	 */
	public static String getBIProtocal() {
		String result = "cluster";
		String s = GateConfigCache.getValue("bi.protocal");
		if (s != null && !"".equals(s)) {
			try {
				result = s;
			} catch (Exception e) {
			}
		}
		return result;
	}
	
	/**
	 * 	#传输协议版本
	 * @return
	 */
	public static String getBIVersion() {
		String result = "30";
		String s = GateConfigCache.getValue("bi.version");
		if (s != null && !"".equals(s)) {
			try {
				result = s;
			} catch (Exception e) {
			}
		}
		return result;
	}
	/**
	 * BI文件输出频率
	 * @return
	 */
	public static String getBITimeInterval() {
		String result = "2500";
		String s = GateConfigCache.getValue("bi.timeInterval");
		if (s != null && !"".equals(s)) {
			try {
				result = s;
			} catch (Exception e) {
			}
		}
		return result;
	}
	
	/**
	 * 重复过滤使用的模式 redis/memcache
	 * 默认 memcache
	 * @return
	 */
	public static String getFilterType() {
		String result = "memcache";
		String s = GateConfigCache.getValue("filter_type");
		if (s != null && !"".equals(s)) {
			try {
				result = s;
			} catch (Exception e) {
			}
		}
		return result;
	}
	
	/**
	 * 判断超时重试次数
	 * @return
	 */
	public static int getTimeOutTryTimes() {
		int result = 0;
		String s = GateConfigCache.getValue("timeOut_try_times");
		if (s != null && !"".equals(s)) {
			try {
				result = Integer.parseInt(s.trim());
			} catch (Exception e) {
			}
		}
		return result;
	}
	
		
	/**
	 * 是否执行数据加密
	 * true:加密
	 * false：不加密
	 * @return
	 */
	public static boolean isEncode() {
		boolean  result = false ;
		String s = GateConfigCache.getValue("is_encode");
		if (s != null && "1".equals(s)) {
			result = true ;
		}
		return result;
	}
	
	/**
	 * 告警通知使用的URL地址
	 */
	public static String getAlarmUrl() {
		return GateConfigCache.getValue("alarm_url");
	}
	
	/**
	 * 获取全局退订描述
	 * @return
	 */
	public static String getUnsubscribeMsg() {
		String  result = "TD,td" ;
		String s = GateConfigCache.getValue("unsubscribe_msg");
		if (s != null && "".equals(s)) {
			result = s;
		}
		return result;
	}
	 
	/**
	 * 告警账号
	 */
	public static String getAlarmUser() {
		return GateConfigCache.getValue("alarm_user");
	}
	/**
	 * 告警账号的密码
	 */
	public static String getAlarmPwd() {
		return GateConfigCache.getValue("alarm_pwd");
	}
	/**
	 * 告警业务代码
	 */
	public static String getAlarmServiceCode() {
		return GateConfigCache.getValue("alarm_service_code");
	}
	/**
	 * 余额告警内容模板，模板中需要包含${customer_name}[公司名称],${balabce}[余额值]
	 */
	public static String getAlarmBalanceTemplate() {
		return GateConfigCache.getValue("alarm_balance_template");
	}
	/**
	 * 余额恢复通知模板，模板中需要包含${customer_name}[公司名称],${balabce}[余额值]
	 */
	public static String getAlarmBalanceRecoveryTemplate() {
		return GateConfigCache.getValue("alarm_balance_recovery_template");
	}
	/**
	 * 余额告警执行频率(单位秒),默认值60
	 */
	public static int getAlarmBalanceFrequently() {
		int result = 60;
		String s = GateConfigCache.getValue("alarm_balance_frequently");
		if (s != null && !"".equals(s)) {
			try {
				result = Integer.parseInt(s.trim());
			} catch (Exception e) {
			}
		}
		return result;
	}
	 
	
	
	public static String getPassword(UserBean userBean){
		
		int is_encrypt = 1;
		String is_encrypt_tmp = (String) userBean.getParamMap().get("is_encrypt");
		if(is_encrypt_tmp!=null){
			try{
			is_encrypt = Integer.parseInt(is_encrypt_tmp);
			}catch(Exception e){
				log.error("", e);
			}
		}
		EncodeResponse result = null;
		String pwd= userBean.getUser_pwd();
		if(is_encrypt==0){
			result = HSToolCode.decoded(pwd);
			if(result.isSuccess()){
				pwd = result.getContent();
			}
		}
	return pwd;
}
	
	
}
