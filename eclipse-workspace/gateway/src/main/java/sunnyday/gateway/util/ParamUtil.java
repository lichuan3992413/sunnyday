package sunnyday.gateway.util;

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
	 * 允许提交的手机号码上限
	 */
	public final static int MOBILE_COUNT_LIMIT= 1000;
	/**
	 * 短信交互协议类型
	 */
	public final static String CMPP_TYPE = "CMPP"; 
	public final static String SGIM_TYPE = "SGIP"; 
	public final static String SMGP_TYPE = "SMGP"; 
	public final static String HTTP_TYPE = "HTTP"; 
	
	
	public final static String GATE_CONFIG_REDIS2TDTIME_OUT="report_redis2db_timeout";
	
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
	
	public final static String REDIS_KEY_TD_INFO = "tdInfo"; 
	public final static String REDIS_KEY_NET_SWITCHED_MOBILE= "netSwitchedMobileInfo"; 
	public final static String REDIS_KEY_CHECK_METHOD_INFO= "checkMethod"; 
	public final static String REDIS_KEY_LOCATION_INFO_LIST= "locationInfo"; 
	
	public final static String REDIS_KEY_USER_CHECK_MODE= "user_check_mode"; 
	
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
	 * 全局接口模板
	 */
	public final static String REDIS_KEY_SMS_COMMON_TEMPLATE_COMMON= "sms_common_template"; 
	
	
	/**
	 * 模板ID和业务代码直接的关系
	 */
	public final static String REDIS_KEY_TEMPLATE_SERVICES = "template_services"; 
	/**
	 * 账号级别接口模板
	 */
	public final static String REDIS_KEY_SMS_USER_TEMPLATE_COMMON= "sms_user_template"; 
	/**
	 * 业务信息
	 */
	public final static String REDIS_KEY_SERVICE_INFO= "service_info"; 
	/**
	 * 短信群发接口
	 */
	public final static String HTTP_INTERFACE_QUNFASMSSEND= "QunFaSmsServece"; 
	/**
	 * 短信下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND= "SendSmsService"; 
	/**
	 * 短信下发接口，接口中不进行验证
	 */
	public final static String HTTP_INTERFACE_SMSSEND_SERVICES= "SmsSendServiceImpl"; 
	
	public final static String TEMPLATE_SMS_SERVICES= "TemplateSmsServiceImpl"; 
	
	public final static String TEMPLATE_BATCH_SMS_SERVICES= "TemplateBatchServiceImpl"; 
	/**
	 * 余额查询
	 */
	public final static String HTTP_INTERFACE_POSTBALANCE= "PostBalanceService"; 
	
	/**
	 * 获取上行
	 */
	public final static String INTERFACE_POST_DELIVERS = "PostDeliverServiceImpl"; 
	
	/**
	 * 获取状态报告
	 */
	public final static String INTERFACE_POST_REPORTS= "PostReportServiceImpl";



	/**
	 * 获取南京银行机构表
	 */
	public final static String REDIS_KEY_NJYH_ISSURE_BANK = "njyh_issue_bank";

	/**
	 * 获取南京银行机构手机号码表 redirect_mobile
	 */
	public final static String REDIS_KEY_NJYH_REDIRECT_MOBILE = "njyh_redirect_mobile";

	/**
	 * 获取客户关系手机号码表 note_friend
	 */
	public final static String REDIS_KEY_NOTE_FRIEND_PHONE = "note_friend_phone";


	/**
	 * 红树http协议短信下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_HS= "HsMassSmsSubmitSolver"; 
	/**
	 * 百悟http协议短信下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_BW= "BwMassSmsSubmitSolver"; 
	/**
	 * 百悟圆通下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_YTO= "YTOSmsSubmitSolver"; 

	/**
	 * 同程网下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_TC= "TCSmsSubmitSolver"; 
	
	/**
	 * 盛大下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_SD= "SDSmsSubmitSolver"; 
	
	/**
	 * 南京银行批量下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_NJBANK_BATCH= "NJBankBatchSmsSubmitSolver";


	/**
	 * 南京银行借贷批量下发接口
	 */
	public final static String HTTP_NJYH_HANDLE_TO_LOAND= "NjyhHandleToLoanSolver";

	/**
	 * 互金借贷批量下发接口
	 */
	public final static String HTTP_HUJIN_HANDLE_TO_LOAND= "HujinHandleToLoanSolver";
	
	/**
	 * 南京银行实时下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_NJBANK_REAL= "NJBankRealSmsSubmitSolver";

	/**
	 * 南京银行动账通知接口
	 */
	public final static String HTTP_INTERFACE_MOVING_ACCOUNT_NOTICE= "MovingAccountNoticeSolver";


	/**
	 * 郑州银行批量下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_ZZBANK_BATCH= "ZZBankBatchSmsSubmitSolver";

	/**
	 * 郑州银行实时下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_ZZBANK_REAL= "ZZBankRealSmsSubmitSolver";

	/**
	 * 京东下发接口
	 */
	public final static String WEBSERVICE_INTERFACE_SMSSEND_JD= "JDWebServicesSmsSubmitSolver"; 
	/**
	 * 校验密码
	 */
	public final static String VERIFY_MIMA= "VerifyPassword"; 
	/**
	 * 校验密码和业务代码
	 */
	public final static String VERIFY_MIMA_SERVICE= "VerifyPasswordService";

	/**
	 * 红树http余额查询接口
	 */
	public final static String HTTP_INTERFACE_BALANCE_HS= "HsPostBalanceSolver"; 
	/**
	 * 百悟http余额查询接口
	 */
	public final static String HTTP_INTERFACE_BALANCE_BW= "BwPostBalanceSolver"; 

	/**
	 * 晋中银行下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_JZBANK = "JZBANKSmsSubmitSolver";
	
	/**
	 * 中信建投下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_ZXJT = "ZXJTSmsSubmitSolver"; 
	
	/**
	 * 中信建投-港澳资讯下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_ZXJT_GAOTIME = "ZXJTGAOTimeSmsSubmitSolver"; 
	
	/**
	 * 财富证券下发接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_FORTUNESE = "FortuneSEQunSubmitSolver";
	
	/**
	 * 蘑菇街接口
	 */
	public final static String HTTP_INTERFACE_SMSSEND_MGJ = "MGJSmsSubmitSolver";
	
	
	public final static String REPORT_SERVICE = "ReportServiceImpl";
	
	
	public final static String DELIVER_SERVICE = "DeliverServiceImpl";

	/**
	 * 老版状态报告
	 */
	public final static int REPORT_OLD= 0;
	/**
	 * 新版状态报告
	 */
	public final static int REPORT_NEW= 1; 
	/**
	 * 圆通版状态报告
	 */
	public final static int REPORT_YTO= 3;
	/**
	 * 同程版状态报告
	 */
	public final static int REPORT_TC= 4;
	/**
	 * 盛大版状态报告
	 */
	public final static int REPORT_SD= 5;
	/**
	 * 国美状态报告
	 */
	public final static int REPORT_GOME= 6;
	
	/**
	 * 晋中银行状态报告
	 */
	public final static int REPORT_JZBANK= 7;
	
	/**
	 * 京东版本的状态报告或上行
	 */
	public final static int REPORT_JD= 8;
	/**
	 * 有赞版本的状态报告或上行
	 */
	public final static int REPORT_YZ= 9;
	
	/**
	 * 中信建投状态报告或上行
	 */
	public final static int REPOET_ZXJT = 10;

	/**
	 * 南京银行 行业短信的业务代码
	 */
	public static final String  SERVICE_CODE = "0";

	/**
	 * 南京银行 营销短信的业务代码
	 */
	public static final String  MAKKETING_SERVICE_CODE = "1";
	
	/**
	 * 管理员手机号关联关系Redis缓存Key
	 */
	public final static String REDIS_KEY_ADMIN_ID_MOBILE= "admin_id_mobile";
	
	/**
	 * 管理员业务账号关联关系Redis缓存Key
	 */
	public final static String REDIS_KEY_ADMIN_ID_USERS= "admin_id_users";

	/**
	 * 成功调用短信平台接口
	 */
	public final static String SMS_API_SUCCESS= "SUCCESS";

	/**
	 * 调用短信平台接口失败
	 */
	public final static String SMS_API_FAIL= "FAIL";

}
