package sunnyday.common.model;

import java.io.Serializable;

public class UserServiceForm  implements Serializable{

	public String toString() {
		return "UserServiceForm [user_id=" + user_id + ", td_code=" + td_code
				+ ", service_code=" + service_code + ", ext_code=" + ext_code
				+ ", sp_number=" + sp_number + ", td_sp_number=" + td_sp_number
				+ "]";
	}
	private static final long serialVersionUID = 7023675724894389074L;
	//业务表
	private int sn ;
	private int service_sn ;
	private String user_id;
	private String td_code;
	private String service_code;
	private String ext_code;
	private String sp_number;
	private String type;
	private int status;
	private int level;
	private int priority;//-2=主用分流，-1=号段分流，0=主用通道，1,2,3,4,...=各级备用通道
	private String insert_time;
	private String update_time;
	private String operator;
	private String td_type;
	private double price;
	private int ratio;
	private int totalRatio;
	private String td_sp_number;
	
	public int getService_sn() {
		return service_sn;
	}
	public void setService_sn(int service_sn) {
		this.service_sn = service_sn;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getTd_code() {
		return td_code;
	}
	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}
	public String getService_code() {
		return service_code;
	}
	public void setService_code(String service_code) {
		this.service_code = service_code;
	}
	public String getExt_code() {
		return ext_code;
	}
	public void setExt_code(String ext_code) {
		this.ext_code = ext_code;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public void setPrice(String price){
		this.price = Double.parseDouble(price);
	}
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getSp_number() {
		return sp_number;
	}
	public void setSp_number(String sp_number) {
		this.sp_number = sp_number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
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
	public String getTd_type() {
		return td_type;
	}
	public void setTd_type(String td_type) {
		this.td_type = td_type;
	}
	public int getRatio() {
		return ratio;
	}
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}
	public int getTotalRatio() {
		return totalRatio;
	}
	public void setTotalRatio(int totalRatio) {
		this.totalRatio = totalRatio;
	}
	public String getTd_sp_number() {
		return td_sp_number;
	}
	public void setTd_sp_number(String td_sp_number) {
		this.td_sp_number = td_sp_number;
	}
	
}
