package sunnyday.common.model;

import java.io.Serializable;

public class UserSignForm implements Serializable{

	private static final long serialVersionUID = -8571613183411462103L;
	
	private int sn;
	private String user_id;
	private String gate_sp_number;
	private String td_code;
	private String sign_chs;
	private String sign_eng;
	private int status;
	private String insert_time;
	private String update_time;
	private String operator;
	private int flag;
	private String add_chs_msg;
	private String add_eng_msg;
	private int is_add_msg;
	private int is_add_msg_use;
	private String unsubscribe_msg;
	
	
 
	public String toString() {
		return "UserSignForm [user_id=" + user_id + ", gate_sp_number="
				+ gate_sp_number + ", td_code=" + td_code + ", sign_chs="
				+ sign_chs + ", status=" + status + ", add_chs_msg="
				+ add_chs_msg + ", is_add_msg=" + is_add_msg
				+ ", is_add_msg_use=" + is_add_msg_use + ", unsubscribe_msg="
				+ unsubscribe_msg + "];  ";
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getGate_sp_number() {
		return gate_sp_number;
	}
	public void setGate_sp_number(String gate_sp_number) {
		this.gate_sp_number = gate_sp_number;
	}
	public String getTd_code() {
		return td_code;
	}
	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}
	public String getSign_chs() {
		return sign_chs;
	}
	public void setSign_chs(String sign_chs) {
		this.sign_chs = sign_chs;
	}
	public String getSign_eng() {
		return sign_eng;
	}
	public void setSign_eng(String sign_eng) {
		this.sign_eng = sign_eng;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getAdd_chs_msg() {
		return add_chs_msg;
	}
	public void setAdd_chs_msg(String add_chs_msg) {
		this.add_chs_msg = add_chs_msg;
	}
	public String getAdd_eng_msg() {
		return add_eng_msg;
	}
	public void setAdd_eng_msg(String add_eng_msg) {
		this.add_eng_msg = add_eng_msg;
	}
	public int getIs_add_msg() {
		return is_add_msg;
	}
	public void setIs_add_msg(int is_add_msg) {
		this.is_add_msg = is_add_msg;
	}
	public int getIs_add_msg_use() {
		return is_add_msg_use;
	}
	public void setIs_add_msg_use(int is_add_msg_use) {
		this.is_add_msg_use = is_add_msg_use;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public int getSn() {
		return sn;
	}
	
	public String getUnsubscribe_msg() {
		return unsubscribe_msg;
	}
	public void setUnsubscribe_msg(String unsubscribe_msg) {
		this.unsubscribe_msg = unsubscribe_msg;
	}
	
}
