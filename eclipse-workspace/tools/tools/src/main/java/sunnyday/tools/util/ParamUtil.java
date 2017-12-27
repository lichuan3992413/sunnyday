package sunnyday.tools.util;

public class ParamUtil {
	
	public final static String IS_FILTET_MOBILE = "log_is_filte_mobile";
	/**
	 * 客户不需要上行或状态报告
	 */
	public final static int NO_NEED = 0; 
	
	/**
	 * 用户自取上行或者状态报告
	 */
	public final static int USER_GET = 1; 
	
	/**
	 * 平台推送上行或状态报告
	 */
	public final static int PLAT_SEND = 2; 
	
	
	/**
	 * 短信交互协议类型
	 */
	public final static String CMPP_TYPE = "CMPP"; 
	public final static String SGIM_TYPE = "SGIP"; 
	public final static String SMGP_TYPE = "SMGP"; 
	public final static String HTTP_TYPE = "HTTP"; 
	public final static String REDIS_KEY_AUTO_SEND_MSG_CONFIG= "auto_send_msg_config"; 
	public final static String REDIS_KEY_AUTO_SEND_MSG_CONTENT= "auto_send_msg_content"; 
	public final static String REDIS_KEY_ADMIN_ID_MOBILE= "admin_id_mobile"; 
	public final static String REDIS_KEY_CHARGETERMID = "chargeTermid"; 
	public final static String REDIS_KEY_CHANNEL_ROUTE= "channel_route"; 
	/**
	/**
	 * 过滤短信内容的全局模板
	 */
	public final static String REDIS_KEY_SMS_FILTER_SMS_TEMPLATE= "filter_sms_template"; 
	
	/**
	 * 全局接口模板
	 */
	public final static String REDIS_KEY_SMS_COMMON_TEMPLATE_COMMON= "sms_common_template"; 
	/**
	 * 账号级别接口模板
	 */
	public final static String REDIS_KEY_SMS_USER_TEMPLATE_COMMON = "sms_user_template";
	/**
	 * 通道原始接入代码
	 */
	public final static String REDIS_KEY_TD_SPNUMBER= "td_spnumber"; 
	/** 网关错误码 */
	public final static String REDIS_KEY_GATE_ERROR_CODE = "gate_error_code"; 
	public final static String REDIS_KEY_USER_KEYWORD = "user_keyword_info";
	public final static String REDIS_KEY_USER_FILITER_CONTENT_LIST= "user_filter_content_list"; 
	//公共级别的白名单
	public final static String REDIS_KEY_WHITE_TEMPLATE_COMMON= "white_template_common"; 
	// 手机归属
	public final static String REDIS_KEY_MOBILE_AREA = "mobile_area"; 
	public final static String GATE_CONFIG_REDIS2TDTIME_OUT="report_redis2db_timeout";
	public final static String REDIS_KEY_GLOBAL_KEYWORD = "global_keyword_info";
	public final static String REDIS_KEY_GATE_CONFIG = "gateConfig"; 
	public final static String REDIS_KEY_CACHEMAP = "cache_message_cacheMap"; 
	public final static String REDIS_KEY_LASTMAP = "cache_message_lastMap"; 
	public final static String REDIS_KEY_ERROR_CODE = "error_code"; 
	public final static String REDIS_KEY_KEYWORD = "keyword_info"; 
	public final static String REDIS_KEY_MATCH_DELIVER_SPNUMBER = "match_deliver_spnumber"; 
	public final static String REDIS_KEY_WHITE_MOBILES = "white_mobiles"; 
	public final static String REDIS_KEY_GLOBLE_BLACK_SCOPE = "globle_black_scope"; 
	public final static String REDIS_KEY_GLOBLE_BLACK_MOBILE = "globle_black_mobile"; 
	public final static String REDIS_KEY_USER_BLACK_SCOPE = "user_black_scope"; 
	public final static String REDIS_KEY_USER_BLACK_MOBILE = "user_black_mobile"; 
	public final static String REDIS_KEY_TD_BLACK_SCOPE = "td_black_scope"; 
	public final static String REDIS_KEY_TD_BLACK_MOBILE = "td_black_mobile"; 
	public final static String REDIS_KEY_SP_NUMBER_FILTER = "sp_number_filter"; 
	//用户级别的白名单
	public final static String REDIS_KEY_WHITE_TEMPLATE_USER= "white_template_user"; 
	public final static String REDIS_KEY_TD_INFO = "tdInfo"; 
	public final static String REDIS_KEY_ChARGE_TERMID = "chargeTermid";
	public final static String REDIS_KEY_NET_SWITCHED_MOBILE= "netSwitchedMobileInfo"; 
	public final static String REDIS_KEY_CHECK_METHOD_INFO= "checkMethod"; 
	public final static String REDIS_KEY_LOCATION_INFO_LIST= "locationInfo"; 
	
	public final static String REDIS_KEY_USER_CHECK_MODE= "user_check_mode"; 
	
	//业务级别黑名单
	public final static String REDIS_KEY_SERVICE_BLACK_MOBILE = "service_black_mobile";
	public final static String REDIS_KEY_TEMPLATE_BLACK_MOBILE = "template_black_mobile";
	
	public final static String REDIS_KEY_USER_INFO= "userInfo"; 
	public final static String REDIS_KEY_USER_SERVICE_INFO= "user_service_info"; 
	public final static String REDIS_KEY_USER_SIGN_INFO= "user_sign_info"; 
	public final static String REDIS_KEY_COUNTRY_PHONE_CODE_MAP= "countryPhoneCodeMap"; 
	
	public final static String REDIS_KEY_USER_WHITE_LIST= "user_white_list"; 
	public final static String REDIS_KEY_TD_WHITE_LIST= "td_white_list"; 
	
	public final static String REDIS_KEY_GATE= "gate"; 
	public final static String REDIS_KEY_SEND= "send"; 
	
	/**
	 * 待下发
	 */
	public final static  String REDIS_SET_SUBMIT_KEY= "set:submit"; 
	
	/**
	 * 状态报告待推送
	 */
	public final static  String REDIS_SET_REPORT_KEY= "set:report"; 
	
	/**
	 * 上行待推送
	 */
	public final static  String REDIS_SET_DELIVER_KEY= "set:deliver"; 
	
	/**
	 * 待提到运营商
	 */
	public final static  String REDIS_SET_SEND_KEY= "set:send"; 
	
	/**
	 * 待进行状态报告匹配
	 */
	public final static  String REDIS_SET_SENT_KEY= "set:sent"; 
	

	/**
	 * 发送线程
	 */
	public final static  String REDIS_SET_THREAD_KEY= "set:thread"; 
	 
}
