
package sunnyday.gateway.util;

import java.util.HashMap;
import java.util.Map;


public class ErrorCodeUtil {
	public final static Map<String, String> allErrorMap = new HashMap<String, String>();
	//统一的成功失败标志
	public final static String response_success_code="success";
	public final static String response_fail_code = "error";
	//获取状态报告、上行接口错误码
	public final static String get_report_user_status_close = "-11";//账号关闭
	public final static String get_report_send_style_error = "-15";//不支持get方式
	public final static String get_report_user_id_error = "-16";//用户名不存在
	public final static String get_report_user_mima_error = "-17";//密码错误
	public final static String get_report_fast_error = "-19";//200毫秒访问一次
	public final static String get_report_no_root = "-18";//用户不支持主动获取
	public final static String get_report_user_ip_error = "108";//ip地址错误
	public final static String get_report_no_msg = "0";//尚未产生上行或者状态信息
	
	
	//个性化接口错误码
	public final static String hs_balance_not_enough_gxh = "100";//余额不足
	public final static String hs_user_status_close_gxh = "101";//账号关闭
	public final static String hs_msg_content_error_gxh = "102";//短信内容超过500字或为空
	public final static String hs_user_id_error_gxh = "106";//用户名不存在
	public final static String hs_user_mima_error_gxh = "107";//密码错误
	public final static String hs_user_ip_error_gxh = "108";//ip地址错
	public final static String hs_corpServiceError_or_statusClose_gxh = "109";//业务不存在
	public final static String hs_ext_code_error_gxh = "110";//小号不合法
	public final static String hs_send_param_error_gxh = "112";//send_param拆分拼凑错误
	public final static String hs_send_style_error_gxh = "114";//不支持get方式
	public final static String hs_total_count_err_gxh = "115";//total_count与实际短信条数无法匹配
	public final static String hs_mobile_info_error_gxh = "116";//手机号码超过200个或合法手机号为空
	public final static String hs_timing_error_gxh = "117";//定时短信，定时日期格式不合法
	public final static String hs_timing_range_error_gxh = "118";//定时短信，定时日期超出范围20分钟后三天内
	public final static String hs_timing_batch_gxh= "119";//定时短信，定时批次编号不合法
	public final static String hs_no_authority_gxh= "120";//该用户无权调用下发短信接口
	
	
	//红树普通短信接口错误码
	public final static String hs_balance_not_enough = "100";//余额不足
	public final static String hs_user_status_close = "101";//账号关闭
	public final static String hs_msg_content_error = "102";//短信内容超过500字或为空
	public final static String hs_mobile_info_error = "103";//手机号超过200个或者合法手机号为空
	public final static String hs_msg_id_long = "104";//下发自定义的msg_id超过50位
	public final static String hs_send_style_error = "105";//不支持get方式调用
	public final static String hs_user_id_error = "106";//用户名不存在
	public final static String hs_user_mima_error = "107";//密码错误
	public final static String hs_user_ip_error = "108";//ip地址错
	public final static String hs_corpServiceError_or_statusClose = "109";//业务不存在
	public final static String hs_ext_code_error = "110";//小号不合法
	public final static String hs_format_error = "111";//计费编码不符
	public final static String hs_timing_error = "112";//定时短信，定时日期格式不合法
	public final static String hs_timing_range_error = "113";//定时短信，定时日期超出范围20分钟后三天内
	public final static String hs_timing_batch= "114";//定时短信，定时批次编号不合法
	public final static String hs_no_authority= "115";//该用户无权调用下发短信接口
	
	
	//红树余额查询接口错误码
	public final static String hs_user_status_close_balance = "101";//账号关闭
	public final static String hs_send_style_error_balance = "105";//不支持get方式调用
	public final static String hs_balance_fast_error = "104";//访问过快
	public final static String hs_user_id_error_balance = "106";//用户名不存在
	public final static String hs_user_mima_error_balance = "107";//密码错误
	public final static String hs_user_ip_error_balance = "108";//ip地址错
	
	
	
	//百悟普通短信接口错误码
	public final static String bw_balance_not_enough = "-10";//余额不足
	public final static String bw_user_status_close = "-11";//账号关闭
	public final static String bw_msg_content_error = "-12";//短信内容超过1000字或为空
	public final static String bw_mobile_info_error = "-13";//手机号超过200或手机号不合法
	public final static String bw_msg_id_long_error = "-14";//自定义msg_id超长
	public final static String bw_send_style_error = "-15";//不支持get方式
	public final static String bw_user_id_error = "-16";//用户名不存在
	public final static String bw_user_ip_error = "-18";//ip地址错误
	public final static String bw_corpServiceError_or_statusClose = "-19";//密码错误或业务关闭
	public final static String bw_ext_code_error = "-20";//小号指定错误
	public final static String bw_md5_error = "-21";//ip地址错误
	public final static String bw_format_error = "-22";//计费编码不符
	//百悟余额查询返回错误码
	public final static String bw_balance_fast_error = "104";
	public final static String bw_balance_param_error = "105";
	public final static String bw_balance_user_error ="106";
	public final static String bw_balance_user_close ="101";
	public final static String bw_balance_mima_error ="107";
	public final static String bw_balance_ip_error = "108";
	
	
	//备用
	public final static String bw_sp_number_error = "-21";
	public final static String bw_send_param_error = "-22";
	public final static String bw_sendParam_totalCount_error = "-23";
	public final static String bw_deliver_or_report_NotSupprt = "-24";
	public final static String bw_total_count_err = "-25";
	public final static String Number_Format_ERROR = "-50";
	public final static String ARRAYIndex_OutOfBounds_ERROR = "-51";
	public final static String SYSTEM_ERROR = "-60";
	public final static String hs_sp_number_error = "111";
	public final static String hs_deliver_or_report_NotSupprt = "177";
	
	
	// 个性化接口错误码
	public final static String common_success = "1000";// 提叫成功
	public final static String common_balance_not_enough_gxh = "100";// 余额不足
	public final static String common_user_status_close_gxh = "101";// 账号关闭
	public final static String common_msg_content_error_gxh = "102";// 短信内容超过500字或为空
	public final static String common_user_id_error_gxh = "106";// 用户名不存在
	public final static String common_user_mima_error_gxh = "107";// 密码错误
	public final static String common_user_ip_error_gxh = "108";// ip地址错
	public final static String common_corpServiceError_or_statusClose_gxh = "109";// 业务不存在
	public final static String common_ext_code_error_gxh = "110";// 小号不合法
	public final static String common_send_param_error_gxh = "112";// send_param拆分拼凑错误
	public final static String common_send_style_error_gxh = "114";// 不支持get方式
	public final static String common_total_count_err_gxh = "115";// total_count与实际短信条数无法匹配
	public final static String common_mobile_info_error_gxh = "116";// 手机号码超过200个或合法手机号为空
	public final static String common_fast_error = "30001";// 访问频率过高
	public final static String common_get_report_no_root = "30002";// 不支持状态报告自取
	public final static String common_get_deliver_no_root = "30003";// 不支上行自取
	public final static String common_mobile_number_error = "30004";// 无效手机号码
	public final static String common_umber_match_services_error = "30005";//手机号码无法和业务匹配
	public final static String common_no_before_sign = "61";// 无前置签名
	public final static String common_no_after_sign = "62";// 无后置签名
	public final static String common_sign_no_record = "63";//签名未报备
	
	
	// 普通短信接口错误码
	public final static String common_balance_not_enough = "100";// 余额不足
	public final static String common_user_status_close = "101";// 账号关闭
	public final static String common_msg_content_error = "102";// 短信内容超过500字或为空
	public final static String common_mobile_info_error = "103";// 手机号超过200个或者合法手机号为空
	public final static String common_msg_id_long = "104";// 下发自定义的msg_id超过50位
	public final static String common_send_style_error = "105";// 不支持get方式调用
	public final static String common_user_id_error = "106";// 用户名不存在
	public final static String common_user_mima_error = "107";// 密码错误
	public final static String common_user_ip_error = "108";// ip地址错
	public final static String common_corpServiceError_or_statusClose = "109";// 业务不存在
	public final static String common_ext_code_error = "110";// 小号不合法
	public final static String common_submit_success = "1000";// 成功发送
	public final static String common_code_error = "111";// 编码错误
	public final static String common_common_error = "112";// 无效执行命令
	public final static String common_customerNo_error = "113";// 缺少短信平台的 ID
	public final static String common_format_error = "115";// 计费编码不符
	public final static String common_param_error = "116";// 参数提交不合法
	public final static String common_param_fail = "117";// 参数解析错误
	public final static String common_send_fail = "118";// 发送失败
	public final static String common_too_many_mobiles = "1119";// 提交的手机号码多余1000
	public final static String common_too_less_mobiles = "1120";// 没有提交手机号码
	
	public final static String TEMPLATE_NULL = "1121";// 模板参数没有填写
	public final static String TEMPLATE_ERROR = "1122";// 模板不存在
	public final static String TEMPLATE_PARMS_ERROR = "1123";// 模板参数无法匹配
	public static final String SGIN_ERROR = "1124";//签名错误
	public static final String SGIN_OVERDUE = "1125";//签名失效
	public static final String TIMESTAMP_ERROR = "1126";//时间戳非法
	public static final String TRANSACTION_ID_ERROR = "1127";//事务ID非法
	public static final String NO_REPORTS = "1128";//尚未有状态报告产生
	public static final String NO_DELIVERS = "1129";//尚未有上行信息产生
	public static final String NO_PERMISSIONS = "1130";//没有调用接口的权限
	public static final String ERROR_PARAMS = "1131";//参数批量参数填写错误
	 
	
	 
	
	// 余额查询返回错误码
	public final static String common_balance_fast_error = "104";// 访问频率过高
	public final static String common_balance_param_error = "105";// 参数错误
	public final static String common_balance_user_error = "106";// 非法用户
	public final static String common_balance_user_close = "101";// 用户关闭
	public final static String common_balance_mima_error = "107";// 密码错误
	public final static String common_balance_ip_error = "108";// 非法IP地址
	
	
	// 圆通短信接口错误码
	public final static String yto_send_success_code = "000";
	public final static String yto_send_success_text = "DELIVRD";

	public final static String yto_send_unsuccess_code = "500";
	public final static String yto_send_unsuccess_text = "UNDELIV";

	public final static String yto_send_errcommon_code = "100";
	public final static String yto_send_errcommon_text = "YT:0102"; // 无效操作命令

	public final static String yto_send_errmobile_code = "100";
	public final static String yto_send_errmobile_text = "YT:0115"; // 电话号码不正确

	public final static String yto_send_errip_code = "300";
	public final static String yto_send_errip_text = "YT:0303"; // 无效 IP 地址

	public final static String yto_send_errcontent_code = "200";
	public final static String yto_send_errcontent_text = "YT:0226"; // 内容处理错误

	public final static String yto_send_errID_code = "100";
	public final static String yto_send_errID_text = "YT:0104"; // 无效ID

	public final static String yto_send_errMoney_code = "200";
	public final static String yto_send_errMoney_text = "YT:0250"; // 配额不足

	public final static String yto_send_errYW_code = "400";
	public final static String yto_send_errYW_text = "YT:0401"; // 源号码与通道号不匹配
	
	public final static String yto_send_errRejectd_code = "600";
	public final static String yto_send_errRejectd_text= "REJECTD"; // 接口消息被拒绝
	
	public final static String yto_send_errCode_code = "100";
	public final static String yto_send_errCode_text= "YT:0117"; // 编码错误
	
	public final static String yto_send_errmima_code = "100";
	public final static String yto_send_errmima_text= "YT:0106"; // 密码错误
	
	public final static String yto_send_errCustomerNo_code = "100";
	public final static String yto_send_errCustomerNo_text= "YT:0103"; // 缺少短信平台的 ID
	
	
	//REJECTD 600 API 接口消息被拒绝
		
	
	public final static String yto_response_success_code = "success";
	public final static String yto_response_fail_code = "error";
	//YT:0401 400 源号码与通道号不匹配
	
	//盛大接口
	public final static String sd_response_success_code = "success";
	public final static String sd_response_fail_code = "error";
	
	/**
	 * 同程网接口
	 */
	public final static String tc_send_AllSuccess_code= "01" ; // 批量短信提交成功
	public final static String tc_send_IpError_code= "02" ; // IP限制
	public final static String tc_send_OneSuccess_code= "03" ; // 单条短信提交成功
	public final static String tc_send_UserError_code= "04" ; // 用户名错误
	public final static String tc_send_MIMAERROR_code= "05" ; // 密码错误
	public final static String tc_send_MoneyError_code= "06" ; // 剩余条数不足
	public final static String tc_send_AllError_code= "10" ; //  批量下限不足
	public final static String tc_send_UnSuccess_code= "11" ; // 短信发送失败
	public final static String tc_send_ParamError_code= "97" ; // 短信参数有误
	public final static String tc_send_ParamFail_code= "99" ; // 短信参数无法解析
	/*
	00	　批量短信提交成功（批量短信待审批）
	01	　批量短信提交成功（批量短信跳过审批环节）
	07	   信息内容中含有限制词(违禁词)
	08	　信息内容为黑内容
	09	　该用户的该内容 受同天内，内容不能重复发 限制
	10	  
	11	   
	97	　 
	98	　防火墙无法处理这种短信
	99	　
	*/
	
	/**
	 * 盛大接口
	 */
	public final static String sd_send_OneSuccess_code= "0" ; // 短信提交成功
	public final static String sd_send_UserError_code= "-1" ; // 参数[client_id]未填写
	public final static String sd_mobile_is_null_code = "-2"; // 参数[phone]未填写
	public final static String sd_msg_content_is_null_code= "-3" ; // 参数[msg]未填写
	public final static String sd_seq_is_null_code = "-4"; //  参数[seq]未填写
	public final static String sd_smsNoExError_code = "-5"; // 参数[smsNoEx]非数字
	public final static String sd_mobile_is_more_code = "-6"; //手机号超过200个或者合法手机号为空
	public final static String sd_msg_content_error= "-7" ; // 无待发短信
	public final static String sd_user_status_close= "-8" ; // 账号关闭
	public final static String sd_send_IpError_code= "-9" ; // 非法IP访问
	public final static String sd_msg_content_error_code= "-10" ; // 短信内容不合法（超过1000或空）
	public final static String sd_send_MoneyError_code= "-11" ; // 余额不足
	public final static String sd_corpServiceError_or_statusClose = "-12";//业务匹配失败
	public final static String sd_mobile_number_error= "-13" ; // 无效手机号码
	public final static String sd_unkown_Error = "-14"; //未知错误
	
	
	
	
	
	/**
	 * 郑州银行接口
	 */
	public final static String zzBank_send_success_code= "0" ; // 短信提交成功
	public final static String zzBank_send_gateConfigErr_code= "601" ; //gate_config参数未配置
	public final static String zzBank_send_mobileNullErr_code= "602" ; // 参数mobile未填写
	public final static String zzBank_send_mobileErr_code = "603"; //手机号超过1000个
	public final static String zzBank_send_msg_contentNullErr_code = "604"; //参数msg_content未填写
	public final static String zzBank_send_msg_contentErr_code= "605" ; // 短信内容超长或者短信内容为空
	public final static String zzBank_send_batchFileNameNull_code= "606" ; // 参数file_name未填写
	public final static String zzBank_send_batchLoginFail_code= "607" ; // 登陆ftp服务器失败
	public final static String zzBank_send_batchFileNotExist_code= "608" ; // 批发文件不存在或者下载失败
	public final static String zzBank_send_batchFileErr_code= "609" ; // 批发文件格式错误
	public final static String zzBank_send_extErr_code = "610"; // 自定义扩展码非法
	public final static String zzBank_send_serviceErr_code = "700"; // 服务端异常
	public final static String zzBank_send_ftpServiceErr_code = "701"; // FTP服务器异常

	
	
	
	

	 //京东错误码
	 public final static String jd_userID_error = "-10001";	 //用户名不存在
	 public final static String jd_mima_error = "-10001";	 //密码错误
	 public final static String jd_user_closed = "-10007";   //用户已经关闭
	 public final static String jd_ip_error = "-3";			//ip地址错误
	 public final static String jd_no_money = "-10003";		//余额不足
	 public final static String jd_params_error = "-1";		//参数填写或解析错误
	 public final static String jd_mobiles_error = "-12";	//与业务匹配的手机号数为零
	 public final static String jd_mobiles_too_more = "-14";//实际号码个数或电话号码个数超过200个
	 public final static String jd_mobiles_not_equals_count = "-13";	//填写手机号码数与实际下发数量不符
	 public final static String jd_content_error = "-10011";	//短信内容不合法
	 public final static String jd_other_error = "-200";	//其他错误
	 public final static String jd_web_error = "-999";		//服务器内部错误
	 public final static String jd_no_services = "-10029";	//没有可用业务
	 public final static String jd_ext_err = "-10034";		//扩展不合法
	 public final static String jd_fast_error = "-10035";		//访问频率过高
	 public final static String jd_get_report_no_root = "-10036";		//不支持状态报告自取
	 public final static String jd_get_deliver_no_root = "-10037";		//不支上行自取
	  
	 static {
		    //初始查询京东错误码
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_balance_not_enough_gxh, jd_no_money);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_user_status_close_gxh, jd_user_closed);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_msg_content_error_gxh, jd_content_error);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_user_id_error_gxh, jd_userID_error);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_user_mima_error_gxh, jd_mima_error);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_user_ip_error_gxh, jd_ip_error);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_corpServiceError_or_statusClose_gxh, jd_no_services);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_ext_code_error_gxh, jd_ext_err);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_send_param_error_gxh, jd_params_error);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_send_style_error_gxh, jd_web_error);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_total_count_err_gxh, jd_mobiles_not_equals_count);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_mobile_info_error_gxh, jd_mobiles_error);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_mobile_info_error, jd_mobiles_too_more);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_fast_error, jd_fast_error);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_get_report_no_root, jd_get_report_no_root);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_get_deliver_no_root, jd_get_deliver_no_root);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_umber_match_services_error, jd_mobiles_error);
		  allErrorMap.put(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD+"_"+common_mobile_number_error, jd_mobiles_error);
		  
		  
	
	 }
	


	
	
	/*
	00	　批量短信提交成功（批量短信待审批）
	01	　批量短信提交成功（批量短信跳过审批环节）
	07	   信息内容中含有限制词(违禁词)
	08	　信息内容为黑内容
	09	　该用户的该内容 受同天内，内容不能重复发 限制
	10	  
	11	   
	97	　 
	98	　防火墙无法处理这种短信
	99	　
	*/
	
	public static HSResponse getErrorCode(String common_code){
		HSResponse result = new HSResponse();
		if(common_balance_not_enough_gxh.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errMoney_code);
			result.setText(yto_send_errMoney_text);
		}else if(common_user_ip_error.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errip_code);
			result.setText(yto_send_errip_text);
		} else if(common_balance_user_error.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errID_code);
			result.setText(yto_send_errID_text);
		}else if(common_msg_content_error.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errcontent_code);
			result.setText(yto_send_errcontent_text);
		}else if(common_mobile_info_error.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errmobile_code);
			result.setText(yto_send_errmobile_text);
		}else if(common_balance_mima_error.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errmima_code);
			result.setText(yto_send_errmima_text);
		}else if(yto_send_errcommon_code.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errcommon_code);
			result.setText(yto_send_errcommon_text);
		}else if(common_corpServiceError_or_statusClose.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errRejectd_code);
			result.setText(yto_send_errRejectd_text);
		}else  if(common_user_mima_error.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errmima_code);
			result.setText(yto_send_errmima_text);
		}else if(common_common_error.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errcommon_code);
			result.setText(yto_send_errcommon_text);
		}else if(common_customerNo_error.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errCustomerNo_code);
			result.setText(yto_send_errCustomerNo_text);
		} else if(common_mobile_number_error.equalsIgnoreCase(common_code)){
			result.setCode(yto_send_errmobile_code);
			result.setText(yto_send_errmobile_text);
		} else{
			result.setCode(yto_send_errRejectd_code);
			result.setText(yto_send_errRejectd_text);
		}
		return result ; 
	}
	
	public static String getErrorCodeFromMap(String common_code,String type){
		String result = null;
		result = allErrorMap.get(type+"_"+common_code);
		if(result==null){
			 return common_code ;
		}else {
			 return result ;
		}
	}
	 
	
	
	public static String getErrorCode(String common_code,String type){
		String result = common_code;
		if(ParamUtil.HTTP_INTERFACE_SMSSEND_HS.equals(type)){
			if(common_balance_not_enough.equals(common_code)){
				return hs_balance_not_enough;
			}else if(common_user_status_close.equals(common_code)){
				return hs_user_status_close;
			}else if(common_msg_content_error.equals(common_code)){
				return hs_msg_content_error;
			}else if(common_mobile_info_error.equals(common_code)){
				return hs_mobile_info_error;
			}else if(common_msg_id_long.equals(common_code)){
				return hs_msg_id_long;
			}else if(common_send_style_error.equals(common_code)){
				return hs_send_style_error;
			}else if(common_user_id_error.equals(common_code)){
				return hs_user_id_error;
			}else if(common_user_mima_error.equals(common_code)){
				return hs_user_mima_error;
			}else if(common_user_ip_error.equals(common_code)){
				return hs_user_ip_error;
			}else if(common_corpServiceError_or_statusClose.equals(common_code)){
				return hs_corpServiceError_or_statusClose;
			}else if(common_ext_code_error.equals(common_code)){
				return hs_ext_code_error;
			}else if(common_submit_success.equals(common_code)){
				return common_submit_success;
			}else {
				return common_code;
			}
		}else if(ParamUtil.HTTP_INTERFACE_SMSSEND_BW.equals(type)){
			if(common_balance_not_enough.equals(common_code)){
				return bw_balance_not_enough;
			}else if(common_user_status_close.equals(common_code)){
				return bw_user_status_close;
			}else if(common_msg_content_error.equals(common_code)){
				return bw_msg_content_error;
			}else if(common_mobile_info_error.equals(common_code)){
				return bw_mobile_info_error;
			}else if(common_msg_id_long.equals(common_code)){
				return bw_msg_id_long_error;
			}else if(common_send_style_error.equals(common_code)){
				return bw_send_style_error;
			}else if(common_user_id_error.equals(common_code)){
				return bw_user_id_error;
			}else if(common_user_ip_error.equals(common_code)){
				return bw_user_ip_error;
			}else if(common_corpServiceError_or_statusClose.equals(common_code)){
				return bw_corpServiceError_or_statusClose;
			}else if(common_ext_code_error.equals(common_code)){
				return bw_ext_code_error;
			}else if(common_submit_success.equals(common_code)){
				return common_submit_success;
			}else {
				return common_code;
			}
		}else if(ParamUtil.HTTP_INTERFACE_BALANCE_HS.equals(type)){
			if(common_balance_fast_error.equals(common_code)){
				return hs_balance_fast_error;
			}else if(common_balance_param_error.equals(common_code)){
				return hs_user_status_close_balance;
			}else if(common_balance_user_error.equals(common_code)){
				return hs_user_status_close_balance;
			}else if(common_balance_user_close.equals(common_code)){
				return hs_user_status_close_balance;
			}else if(common_balance_mima_error.equals(common_code)){
				return hs_user_mima_error_balance;
			}else if(common_balance_ip_error.equals(common_code)){
				return hs_user_ip_error_balance;
			} else{
				return common_code;
			}
		}else if(ParamUtil.HTTP_INTERFACE_BALANCE_BW.equals(type)){
			if(common_balance_fast_error.equals(common_code)){
				return bw_balance_fast_error;
			}else if(common_balance_param_error.equals(common_code)){
				return bw_balance_param_error;
			}else if(common_balance_user_error.equals(common_code)){
				return bw_balance_user_error;
			}else if(common_balance_user_close.equals(common_code)){
				return bw_balance_user_close;
			}else if(common_balance_mima_error.equals(common_code)){
				return bw_balance_mima_error;
			}else if(common_balance_ip_error.equals(common_code)){
				return bw_balance_ip_error;
			} else{
				return common_code;
			}
		}else if(ParamUtil.HTTP_INTERFACE_SMSSEND_TC.equals(type)){
			if(common_balance_not_enough.equals(common_code)){
				return tc_send_MoneyError_code;
			}else if(common_user_status_close.equals(common_code)){
				return tc_send_UserError_code;
			}else if(common_user_id_error.equals(common_code)){
				return tc_send_UserError_code;
			}else if(common_user_mima_error.equals(common_code)){
				return tc_send_MIMAERROR_code;
			}else if(common_user_ip_error.equals(common_code)){
				return tc_send_IpError_code;
			}else if(common_ext_code_error.equals(common_code)){
				return tc_send_ParamError_code;
			}else if(common_submit_success.equals(common_code)){
				return tc_send_AllSuccess_code;
			}else if(common_param_error.equals(common_code)){
				return tc_send_ParamError_code;
			}else if(common_param_fail.equals(common_code)){
				return tc_send_ParamFail_code;
			}else if(common_send_fail.equals(common_code)){
				return tc_send_UnSuccess_code;
			}else {
				return common_code;
			}
		}else if(ParamUtil.HTTP_INTERFACE_SMSSEND_SD.equals(type)){
			if("1000".equals(common_code)){
				return sd_send_OneSuccess_code;
			}else if(common_user_id_error.equals(common_code)){
				return sd_send_UserError_code;
			}else if(common_user_status_close.equals(common_code)){
				return sd_user_status_close;
			}else if(common_user_ip_error.equals(common_code)){
				return sd_send_IpError_code;
			}else if(common_msg_content_error_gxh.equals(common_code)){
				return sd_msg_content_error_code;
			}else if(common_balance_not_enough.equals(common_code)){
				return sd_send_MoneyError_code;
			}else if(common_corpServiceError_or_statusClose.equals(common_code)){
				return sd_corpServiceError_or_statusClose;
			}else if(common_mobile_number_error.equals(common_code)){
					return sd_mobile_number_error;
			}else if("-1".equals(common_code)){
					return sd_unkown_Error;
			}else if(common_mobile_info_error.equals(common_code)){
					return sd_mobile_is_more_code;
			}else {
				return common_code;
			}
		}else if(ParamUtil.WEBSERVICE_INTERFACE_SMSSEND_JD.equals(type)){
			if("1000".equals(common_code)){
				return sd_send_OneSuccess_code;
			}else if(common_user_id_error.equals(common_code)){
				return sd_send_UserError_code;
			}else if(common_user_status_close.equals(common_code)){
				return sd_user_status_close;
			}else if(common_user_ip_error.equals(common_code)){
				return sd_send_IpError_code;
			}else if(common_msg_content_error_gxh.equals(common_code)){
				return sd_msg_content_error_code;
			}else if(common_balance_not_enough.equals(common_code)){
				return sd_send_MoneyError_code;
			}else if(common_corpServiceError_or_statusClose.equals(common_code)){
				return sd_corpServiceError_or_statusClose;
			}else if(common_mobile_number_error.equals(common_code)){
					return sd_mobile_number_error;
			}else if("-1".equals(common_code)){
					return sd_unkown_Error;
			}else if(common_mobile_info_error.equals(common_code)){
					return sd_mobile_is_more_code;
			}else {
				return common_code;
			}
		}else if (ParamUtil.HTTP_INTERFACE_SMSSEND_JZBANK.equals(type)) {
			if ("1000".equals(common_code)|| common_code == null || "".equals(common_code)) {
				return jzbank_send_AllSuccess_code;
			} else if (common_user_id_error.equals(common_code)) {
				return jzbank_error_userMIMA_code;
			} else if (common_user_mima_error.equals(common_code)) {
				return jzbank_error_userMIMA_code;
			} else if (common_user_status_close.equals(common_code)) {
				return jzbank_error_userMIMA_code;
			} else if (common_msg_id_long.equals(common_code)) {
				return jzbank_unkown_code;
			} else if (common_user_ip_error.equals(common_code)) {
				return jzbank_fail_ipRight_code;
			} else if (common_msg_content_error_gxh.equals(common_code)) {
				return jzbank_null_content_code;
			} else if (common_balance_not_enough.equals(common_code)) {
				return jzbank_less_balance_code;
			} else if (common_corpServiceError_or_statusClose
					.equals(common_code)) {
				return jzbank_error_type_code;
			} else if (common_mobile_number_error.equals(common_code)) {
				return jzbank_mobileError_code;
			} else if ("-1".equals(common_code)) {
				return jzbank_unkown_code;
			} else if (common_mobile_info_error.equals(common_code)) {
				return jzbank_outof_mobile_code;
			} else if (get_report_fast_error.equals(common_code)) {
				return jzbank_quicker_frequence_code;
			} else if (common_umber_match_services_error.equals(common_code)) {
				return jzbank_error_type_code;
			} else if ("15".equals(common_code)) {
				return jzbank_sensitive_word_code;
			} else if ("55".equals(common_code)) {
				return jzbank_black_code;
			} else if ("2".equals(common_code)) {
				return jzbank_send_fail_code;
			} else {
				return common_code;
			}
		}else if (ParamUtil.HTTP_INTERFACE_SMSSEND_FORTUNESE.equals(type)) {
			if (common_code.startsWith("1000")|| common_code == null || "".equals(common_code)) {
				return fortuneSE_send_AllSuccess_code;
			} else if (common_code.startsWith(common_msg_id_long)) {
				return fortuneSE_msgError_code;
			} else if (common_code.startsWith(common_msg_content_error_gxh)) {
				return fortuneSE_contentError_code;
			} else if (common_code.startsWith(common_corpServiceError_or_statusClose)) {
				return fortuneSE_ywError_code;
			} else if (common_code.startsWith(common_balance_not_enough)) {
				return fortuneSE_MoneyError_code;
			} else if (common_code.startsWith(common_umber_match_services_error)) {
				return fortuneSE_loadError_code;
			} else {
				return common_code;
			}
		}
		
		
		return result;
	}
	/**
	 * 晋中银行返回码1
	 */
	public final static String jzbank_response_success_code = "success";
	public final static String jzbank_response_fail_code = "error";
	/**
	 * 晋中银行返回码2
	 */
	public final static String jzbank_send_AllSuccess_code = "r:000"; // 批量短信提交成功
	public final static String jzbank_black_code = "r:001"; // 手机号码是黑名单号码
	public final static String jzbank_mobileError_code = "r:002"; // 手机号码不正确，非法手机号
	public final static String jzbank_noRightError_code = "r:003"; // 账户没有本接口权限，短信平台没有为该用户设置webservice权限
	public final static String jzbank_send_fail_code = "r:004"; // 短信发送失败（由于营运商网关给出了失败的状态报告，所以短信的最终发送状态为发送失败）
	public final static String jzbank_unkown_code = "r:999"; // 未知错误
	public final static String jzbank_null_content_code = "p:001"; // 输入参数错误,短信内容为空
	public final static String jzbank_null_mobiles_code = "p:002"; // 手机号码数组为空
	public final static String jzbank_error_userMIMA_code = "p:003"; // 帐号或者密码不正确
	public final static String jzbank_less_balance_code = "p:004"; // 帐号余额不足
	public final static String jzbank_outof_send_code = "p:005"; // 超出每日限制发送量
	public final static String jzbank_outof_mobile_code = "p:006"; // 一次提交号码数量超过1000
	public final static String jzbank_error_type_code = "p:007"; // 业务类型不正确
	public final static String jzbank_fail_ipRight_code = "p:008"; // ip鉴权失败（需要ip鉴权时有效）
	public final static String jzbank_sensitive_word_code = "p:009"; // 短信内容含有敏感词
	public final static String jzbank_quicker_frequence_code = "p:010"; // 访问频率太快,访问间隔不能小于30秒（获取上行，获取状态报告，获取账户余额时有该限制，发短信则无该限制）
	public final static String jzbank_error_xml_code = "p:011"; // 输入参数格式不正确，必须是文档中规定的xml格式。
	
	private static Map<String,String> jzBankMap = new HashMap<String,String>();
	public static Map<String, String> getJzBankMap() {
		return jzBankMap;
	}
	private static Map<String,String> mgjMap = new HashMap<String,String>();
	public static Map<String, String> getMgjMap() {
		return mgjMap;
	}

	public static void setMgjMap(Map<String, String> mgjMap) {
		ErrorCodeUtil.mgjMap = mgjMap;
	}
	static {
		//晋中银行map
		 jzBankMap.put("r:000","发送成功");
		 jzBankMap.put("r:001","手机号码是黑名单号码");
		 jzBankMap.put("r:002","手机号码不正确，非法手机号");
		 jzBankMap.put("r:003","账户没有本接口权限，短信平台没有为该用户设置webservice权限");
		 jzBankMap.put("r:004","短信发送失败（由于营运商网关给出了失败的状态报告，所以短信的最终发送状态为发送失败）");
		 jzBankMap.put("r:999","未知错误");
		 jzBankMap.put("p:001","输入参数错误,短信内容为空");
		 jzBankMap.put("p:002","手机号码数组为空");
		 jzBankMap.put("p:003","帐号或者密码不正确");
		 jzBankMap.put("p:004","帐号余额不足");
		 jzBankMap.put("p:005","超出每日限制发送量");
		 jzBankMap.put("p:006","一次提交号码数量超过1000");
		 jzBankMap.put("p:007","业务类型不正确");
		 jzBankMap.put("p:008","ip鉴权失败（需要ip鉴权时有效）");
		 jzBankMap.put("p:009","短信内容含有敏感词");
		 jzBankMap.put("p:010","访问频率太快,访问间隔不能小于30秒");
		 jzBankMap.put("p:011", "输入参数格式不正确，必须是文档中规定的xml格式。");
		 jzBankMap.put("109", "业务代码不存在或者通道关闭。");
		 jzBankMap.put("110", "扩展号不合法。");
		 jzBankMap.put("9", "访问地址不存在。");
		 //蘑菇街map
		 mgjMap.put("1000","提交成功");
		 mgjMap.put("0","提交成功");
		 mgjMap.put("100","余额不足");
		 mgjMap.put("106","用户名不存在");
		 mgjMap.put("107","密码错误");
		 mgjMap.put("108","指定访问的IP错误");
		 mgjMap.put("109","业务不存在");
		 mgjMap.put("114","接口提交应为POST，不支持GET");
		 mgjMap.put("115","total_count 与实际短信条数无法匹配");
		 mgjMap.put("116","个性化短信提交个数超过1000条");
		 mgjMap.put("404","访问地址不存在");
		 mgjMap.put("2","正常发送时，发送失败");
		 mgjMap.put("-11","账户关闭");                                       
		 mgjMap.put("-16","用户名错误或用户名不存在");                       
		 mgjMap.put("-17","密码错误");                                       
		 mgjMap.put("-18","不支持客户主动获取");                             
		 mgjMap.put("-19","用户访问超过我方限制频率（间隔200毫秒访问一次）");

	}
	
	
	
	/**
	 * 财富证券返回码
	 */
	public final static String fortuneSE_send_AllSuccess_code = "1000"; // 批量短信提交成功
	public final static String fortuneSE_msgError_code = "-20"; // 小号或msg_id使用错误
	public final static String fortuneSE_contentError_code = "-12"; // 短信内容为空或超过1000字符
	public final static String fortuneSE_ywError_code = "-18"; // 业务信息填写为空
	public final static String fortuneSE_mobileError_code = "-13"; // 没有可发送的手机号码或者超过200
	public final static String fortuneSE_userIdError_code = "-16"; // 企业id或用户账号为空
	public final static String fortuneSE_userStateError_code = "-11"; // 账户状态关闭
	public final static String fortuneSE_userMIMAError_code = "-17"; // 　　密码填写错误
	public final static String fortuneSE_cacheError_code = "-19"; // 缓存5分钟更新加载业务不存在
	public final static String fortuneSE_loadError_code = "-14"; // 加载业务不存在
	public final static String fortuneSE_MoneyError_code = "-10"; // 账户余额不足
	public final static String fortuneSE_staticsError_code = "-24"; // 计费失败

}
