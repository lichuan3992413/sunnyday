package sunnyday.common.model;

import java.io.Serializable;

public class AutoSendMsgContent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7565892851978714849L;
	/** 主键**/
	private long sn;
	/** 上行关键词**/
	private String keyword;
	/** 内容编号**/
	private int content_id;
	/** 回复内容类型**/
	private int msg_content_type;
	/** 下发内容**/
	private String msg_content;
	/** 插入时间**/
	private String insert_time;
	/** 更新时间**/
	private String update_time;
	/** 操作员ID**/
	private String operator;
	public long getSn() {
		return sn;
	}
	public String getKeyword() {
		return keyword;
	}
	public int getContent_id() {
		return content_id;
	}
	public int getMsg_content_type() {
		return msg_content_type;
	}
	public String getMsg_content() {
		return msg_content;
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
	public void setSn(long sn) {
		this.sn = sn;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public void setContent_id(int content_id) {
		this.content_id = content_id;
	}
	public void setMsg_content_type(int msg_content_type) {
		this.msg_content_type = msg_content_type;
	}
	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
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
	@Override
	public String toString() {
		return "AutoSendMsgContent [sn=" + sn + ", keyword=" + keyword
				+ ", content_id=" + content_id + ", msg_content_type="
				+ msg_content_type + ", msg_content=" + msg_content
				+ ", insert_time=" + insert_time + ", update_time="
				+ update_time + ", operator=" + operator + "]";
	}
	
	
}
