package sunnyday.common.model;

import java.io.Serializable;


/***
 * 上行触发下发配置表
 * @author 1210284
 *
 */
public class AutoSendMsgConfig  implements Serializable{
	private static final long serialVersionUID = -2483131360778723886L;
	/**主键，自增**/
	private int sn;
	/**上行回复名称**/
	private String deliver_reply_name;
	/**关键词类型 0-自定义  1-添加黑名单  2-解除黑名单   默认0**/
	private int keyword_type;
	/** 上行关键词**/
	private String keyword;
	/** 匹配模式 0-全匹配，1-模板 2-包含 3-任意  默认0**/
	private int type;
	/** 上行匹配作用范围类型 0-全局  1-业务代码 2-业务账号  默认全局**/
	private int match_type;
	/** 匹配业务代码**/
	private String match_service_code;
	/** 触发下发开关 0-触发下发  1-不触发下发  默认0**/
	private int send_switch;
	/** 发送业务账号**/
	private String user_id;
	/** 发送业务代码**/
	private String send_service_code;
	/** 状态  0：开启   1：关闭**/
	private int status;
	/** 插入时间**/
	private String insert_time;
	/** 更新时间**/
	private String update_time;
	/** 操作员ID**/
	private String operator;
	/** 备注**/
	private String remark;
	public int getSn() {
		return sn;
	}
	public String getDeliver_reply_name() {
		return deliver_reply_name;
	}
	public int getKeyword_type() {
		return keyword_type;
	}
	public String getKeyword() {
		return keyword;
	}
	public int getType() {
		return type;
	}
	public int getMatch_type() {
		return match_type;
	}
	public String getMatch_service_code() {
		return match_service_code;
	}
	public int getSend_switch() {
		return send_switch;
	}
	public String getUser_id() {
		return user_id;
	}
	public String getSend_service_code() {
		return send_service_code;
	}
	public int getStatus() {
		return status;
	}
	public String getInsert_time() {
		return insert_time;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public String getOperator() {
		return operator;
	}
	public String getRemark() {
		return remark;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public void setDeliver_reply_name(String deliver_reply_name) {
		this.deliver_reply_name = deliver_reply_name;
	}
	public void setKeyword_type(int keyword_type) {
		this.keyword_type = keyword_type;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setMatch_type(int match_type) {
		this.match_type = match_type;
	}
	public void setMatch_service_code(String match_service_code) {
		this.match_service_code = match_service_code;
	}
	public void setSend_switch(int send_switch) {
		this.send_switch = send_switch;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public void setSend_service_code(String send_service_code) {
		this.send_service_code = send_service_code;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setInsert_time(String insert_time) {
		this.insert_time = insert_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "AutoSendMsgConfig [sn=" + sn + ", deliver_reply_name="
				+ deliver_reply_name + ", keyword_type=" + keyword_type
				+ ", keyword=" + keyword + ", type=" + type + ", match_type="
				+ match_type + ", match_service_code=" + match_service_code
				+ ", send_switch=" + send_switch + ", user_id=" + user_id
				+ ", send_service_code=" + send_service_code + ", status="
				+ status + ", insert_time=" + insert_time + ", update_time="
				+ update_time + ", operator=" + operator + ", remark=" + remark
				+ "]";
	}
	

	
	
}
