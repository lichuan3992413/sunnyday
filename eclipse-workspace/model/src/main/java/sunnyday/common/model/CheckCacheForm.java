package sunnyday.common.model;

import java.io.Serializable;

public class CheckCacheForm extends BaseBean implements Serializable{

	 
	private static final long serialVersionUID = 6426567393422129603L;
	private int sn;
	private String td_code;
	private String msg_content;
	private String md5_msg_content;
	private String insert_time;
	private String check_user;
	private int status;//1:审核通过 2：审核驳回
	private int check_status;
	
	@Override
	public String toString(){
		return "CacheForm : [msg_content = " + msg_content + "]" +
						   "[md5_index = " + md5_msg_content + "]" +
						   "[check_user = " + check_user + "]" +
						   "[status = " + status + "]";
	}
	public String getCheck_user() {
		return check_user;
	}
	public String getInsert_time() {
		return insert_time;
	}
	public String getMd5_msg_content() {
		return md5_msg_content;
	}
	public String getMsg_content() {
		return msg_content;
	}
	public int getSn() {
		return sn;
	}
	public int getStatus() {
		return status;
	}
	public String getTd_code() {
		return td_code;
	}
	public void setCheck_user(String checkUser) {
		check_user = checkUser;
	}
	public void setInsert_time(String insertTime) {
		insert_time = insertTime;
	}
	public void setMd5_msg_content(String md5MsgContent) {
		md5_msg_content = md5MsgContent;
	}
	public void setMsg_content(String msgContent) {
		msg_content = msgContent;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setTd_code(String tdCode) {
		td_code = tdCode;
	}
	public void setCheck_status(int check_status) {
		this.check_status = check_status;
	}
	public int getCheck_status() {
		return check_status;
	}
}
