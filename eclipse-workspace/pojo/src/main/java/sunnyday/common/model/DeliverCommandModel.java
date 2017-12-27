package sunnyday.common.model;

import java.util.Map;


public class DeliverCommandModel {
	
	private long sn;
	
	private String keyword;
	/**
	 * 上行指令
	 */
	private String command ;
	
	private int status;
	

	@Override
	public String toString() {
		return "DeliverCommandModel [sn=" + sn + ", keyword=" + keyword
				+ ", status=" + status +", send_switch=" + send_switch + ", keyword_type="
				+ keyword_type + ", match_type=" + match_type + ", type="
				+ type + ", user_id=" + user_id + ", send_service_code="
				+ send_service_code + ", match_service_code="
				+ match_service_code + "]";
	}

	/**
	 * 上行时的手机号码
	 */
	private String deliver_mobile ;
	
	/**
	 * 下发内容组合 0-成功 1-失败
	 */
	private Map<Integer, ResultContent> result_map; 
	
	/**
	 * 0-触发 1-不触发
	 */
	private int send_switch;
	
	
	/**关键词类型 0-自定义  1-添加黑名单  2-解除黑名单   默认0**/
	private int keyword_type;

	public long getSn() {
		return sn;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setSn(long sn) {
		this.sn = sn;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/** 上行匹配作用范围类型 0-全局  1-业务代码 2-业务账号  默认全局**/
	private int match_type;
	/** 匹配模式 0-全匹配 1-模板匹配  2-包含匹配 3-任意匹配，默认全匹配**/
	private int type;
	
	private String user_id;
	
	private String send_service_code;
	
	private String match_service_code;
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getDeliver_mobile() {
		return deliver_mobile;
	}

	public void setDeliver_mobile(String deliver_mobile) {
		this.deliver_mobile = deliver_mobile;
	}

	public Map<Integer, ResultContent> getResult_map() {
		return result_map;
	}

	public void setResult_map(Map<Integer, ResultContent> result_map) {
		this.result_map = result_map;
	}

	public int getSend_switch() {
		return send_switch;
	}

	public void setSend_switch(int send_switch) {
		this.send_switch = send_switch;
	}

	public int getKeyword_type() {
		return keyword_type;
	}

	public void setKeyword_type(int keyword_type) {
		this.keyword_type = keyword_type;
	}

	public int getMatch_type() {
		return match_type;
	}

	public void setMatch_type(int match_type) {
		this.match_type = match_type;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getSend_service_code() {
		return send_service_code;
	}

	public String getMatch_service_code() {
		return match_service_code;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setSend_service_code(String send_service_code) {
		this.send_service_code = send_service_code;
	}

	public void setMatch_service_code(String match_service_code) {
		this.match_service_code = match_service_code;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	
}
