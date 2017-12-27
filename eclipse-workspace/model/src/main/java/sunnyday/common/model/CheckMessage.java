package sunnyday.common.model;

import org.apache.commons.lang.StringUtils;


/**
 * 审核短信下发表check_message javaBean
 * 
 * @author 71604057
 * @time 2017年7月19日 上午9:22:36
 */
public class CheckMessage {

	/** 主键 */
	private int sn;
	/** 批次号码 */
	private String batch_number;
	/** 业务账户id */
	private String user_id;
	/** 业务代码 */
	private String service_code;
	/** 手机号 */
	private String mobile;
	/** 短信内容 */
	private String msg_content;
	/** 信息唯一标识 */
	private String msg_id;
	/** 提交的扩展码 */
	private String submit_ext_code;
	/** 插入时间 */
	private String insert_time;
	/** 更新时间 */
	private String update_time;
	/** 下发时间 */
	private String send_time;
	/** 定时时间 */
	private String timing_time;
	/** 审核状态 0：初始状态 1：审核成功 2：审核驳回 100：待审核状态 */
	private int check_status;
	/** 审核人员 */
	private String check_user;
	/** 0：初始状态 1：可以移表状态 */
	private int move_flag;
	/** 是否是接口提交的短信 0:是接口提交的    1:是界面提交的*/
	private int is_interface_send;
	/** 要下发的短信对象*/
	private SmsMessage sms_message;
	/** 驳回原因**/
	private String remark;

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public String getBatch_number() {
		return batch_number;
	}

	public void setBatch_number(String batch_number) {
		this.batch_number = batch_number;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getService_code() {
		return service_code;
	}

	public void setService_code(String service_code) {
		this.service_code = service_code;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMsg_content() {
		return msg_content;
	}

	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
	}

	public String getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}

	public String getSubmit_ext_code() {
		return submit_ext_code;
	}

	public void setSubmit_ext_code(String submit_ext_code) {
		this.submit_ext_code = submit_ext_code;
	}

	public String getInsert_time() {
		return insert_time;
	}

	public void setInsert_time(String insert_time) {
		this.insert_time = insert_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getSend_time() {
		return send_time;
	}

	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}

	public String getTiming_time() {
		return timing_time;
	}

	public void setTiming_time(String timing_time) {
		this.timing_time = timing_time;
	}

	public int getCheck_status() {
		return check_status;
	}

	public void setCheck_status(int check_status) {
		this.check_status = check_status;
	}

	public String getCheck_user() {
		return check_user;
	}

	public void setCheck_user(String check_user) {
		this.check_user = check_user;
	}

	public int getMove_flag() {
		return move_flag;
	}

	public void setMove_flag(int move_flag) {
		this.move_flag = move_flag;
	}

	public int getIs_interface_send() {
		return is_interface_send;
	}

	public void setIs_interface_send(int is_interface_send) {
		this.is_interface_send = is_interface_send;
	}

	public SmsMessage getSms_message() {
		return sms_message;
	}
	public void setSms_message(SmsMessage smsMessage){
		this.sms_message = smsMessage;
	}
	
	public void setSms_message(String smsStr) {
		SmsMessage sms_message = null;
//		if(StringUtils.isNotBlank(smsStr)){
//			sms_message = JSON.parseObject(smsStr,SmsMessage.class);
//		}
		this.sms_message = sms_message;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "CheckMessage [sn=" + sn + ", batch_number=" + batch_number
				+ ", user_id=" + user_id + ", service_code=" + service_code
				+ ", mobile=" + mobile + ", msg_content=" + msg_content
				+ ", msg_id=" + msg_id + ", submit_ext_code=" + submit_ext_code
				+ ", insert_time=" + insert_time + ", update_time="
				+ update_time + ", send_time=" + send_time + ", timing_time="
				+ timing_time + ", check_status=" + check_status
				+ ", check_user=" + check_user + ", move_flag=" + move_flag
				+ ", is_interface_send=" + is_interface_send + ", sms_message="
				+ sms_message + ", remark=" + remark + "]";
	}

}
