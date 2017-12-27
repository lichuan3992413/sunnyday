package sunnyday.common.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 账户表信息  可以删除
 */
public class AdminUser extends BaseBean implements Serializable{
	
	private static final long serialVersionUID = -8967385860934176346L;
	private long sn;
	private String admin_id;
	private String admin_name;
	private String admin_pwd;
	private String mobile;
	private String email;
	private int status;
	private long psn;
	private long role_sn;
	private String insert_time;
	private String update_time;
	private int is_pwd_encrypt;
	private int create_method;
	private int dept_sn;
	
	
	public long getSn() {
		return sn;
	}
	public void setSn(long sn) {
		this.sn = sn;
	}
	public String getAdmin_id() {
		return admin_id;
	}
	public void setAdmin_id(String admin_id) {
		this.admin_id = admin_id;
	}
	public String getAdmin_name() {
		return admin_name;
	}
	public void setAdmin_name(String admin_name) {
		this.admin_name = admin_name;
	}
	public String getAdmin_pwd() {
		return admin_pwd;
	}
	public void setAdmin_pwd(String admin_pwd) {
		this.admin_pwd = admin_pwd;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getPsn() {
		return psn;
	}
	public void setPsn(long psn) {
		this.psn = psn;
	}
	public long getRole_sn() {
		return role_sn;
	}
	public void setRole_sn(long role_sn) {
		this.role_sn = role_sn;
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
	public int getIs_pwd_encrypt() {
		return is_pwd_encrypt;
	}
	public void setIs_pwd_encrypt(int is_pwd_encrypt) {
		this.is_pwd_encrypt = is_pwd_encrypt;
	}
	public int getCreate_method() {
		return create_method;
	}
	public void setCreate_method(int create_method) {
		this.create_method = create_method;
	}
	public int getDept_sn() {
		return dept_sn;
	}
	public void setDept_sn(int dept_sn) {
		this.dept_sn = dept_sn;
	}
	@Override
	public String toString() {
		return "AdminUser [sn=" + sn + ", admin_id=" + admin_id
				+ ", admin_name=" + admin_name + ", admin_pwd=" + admin_pwd
				+ ", mobile=" + mobile + ", email=" + email + ", status="
				+ status + ", psn=" + psn + ", role_sn=" + role_sn
				+ ", insert_time=" + insert_time + ", update_time="
				+ update_time + ", is_pwd_encrypt=" + is_pwd_encrypt
				+ ", create_method=" + create_method + ", dept_sn=" + dept_sn
				+ "]";
	}
	

	
	
}
