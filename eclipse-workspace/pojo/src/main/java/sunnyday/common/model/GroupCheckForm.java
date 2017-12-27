package sunnyday.common.model;

import java.io.Serializable;

public class GroupCheckForm extends BaseBean implements Serializable{
	@Override
	public String toString() {
		return "GroupCheckForm [sn=" + sn + ", user_sn=" + user_sn
				+ ", content=" + content + ", md5_index=" + md5_index
				+ ", td_code=" + td_code + ", check_sp_number="
				+ check_sp_number + ", count=" + count + ", manual_count="
				+ manual_count + ", status=" + status + ", response="
				+ response + ", fail_desc=" + fail_desc + ", check_user="
				+ check_user + ", check_time=" + check_time + "]";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 655623393548713876L;
	
	private int sn;       
	private int user_sn;
	private String content;   
	private String md5_index; 
	private String td_code;   
	private String check_sp_number;
	private int count;
	private int manual_count;
	private int status;    
	private int response;
	private String fail_desc;
	private String check_user;
	private String check_time;
	
	
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMd5_index() {
		return md5_index;
	}
	public void setMd5_index(String md5_index) {
		this.md5_index = md5_index;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public int getManual_count() {
		return manual_count;
	}
	public void setManual_count(int manual_count) {
		this.manual_count = manual_count;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCheck_user() {
		return check_user;
	}
	public void setCheck_user(String check_user) {
		this.check_user = check_user;
	}
	public String getCheck_time() {
		return check_time;
	}
	public void setCheck_time(String check_time) {
		this.check_time = check_time;
	}
	public void setUser_sn(int user_sn) {
		this.user_sn = user_sn;
	}
	public int getUser_sn() {
		return user_sn;
	}
	public void setResponse(int response) {
		this.response = response;
	}
	public int getResponse() {
		return response;
	}
	public void setFail_desc(String fail_desc) {
		this.fail_desc = fail_desc;
	}
	public String getFail_desc() {
		return fail_desc;
	}
	public void setCheck_sp_number(String check_sp_number) {
		this.check_sp_number = check_sp_number;
	}
	public String getCheck_sp_number() {
		return check_sp_number;
	}
	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}
	public String getTd_code() {
		return td_code;
	}
}
