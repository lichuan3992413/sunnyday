package sunnyday.channel.model;

import java.io.Serializable;

import sunnyday.tools.util.DateUtil;

public class MT implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String mobile;
	private String content;
	private String service_code;
	private String ext_code =""; 
	private String user_ext_code="";
	private String sp_number;// service对应的td的sp_number + service对应的ext_code + 客户提交的user_ext_code
	private String msg_id;
	private String channel_id;
	private int msg_total;// 长短信拆分总条数
	private int msg_sequeue;// 长短信序列号（第几条）
	private int msg_split_id;// 长短信唯一标识
	private int msg_format;// 0（英文） 8/15（中文）
	private int cost;
	private double price;
	private int result;
	private String send_sp_number;
	private String port_number;
	private String msg_receive_time = DateUtil.currentTimeToMs();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getUser_ext_code() {
		return user_ext_code;
	}
	public void setUser_ext_code(String user_ext_code) {
		this.user_ext_code = user_ext_code;
	}
	public String getSp_number() {
		return sp_number;
	}
	public void setSp_number(String sp_number) {
		this.sp_number = sp_number;
	}
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	public String getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}
	public int getMsg_total() {
		return msg_total;
	}
	public void setMsg_total(int msg_total) {
		this.msg_total = msg_total;
	}
	public int getMsg_sequeue() {
		return msg_sequeue;
	}
	public void setMsg_sequeue(int msg_sequeue) {
		this.msg_sequeue = msg_sequeue;
	}
	public int getMsg_split_id() {
		return msg_split_id;
	}
	public void setMsg_split_id(int msg_split_id) {
		this.msg_split_id = msg_split_id;
	}
	public int getMsg_format() {
		return msg_format;
	}
	public void setMsg_format(int msg_format) {
		this.msg_format = msg_format;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getSend_sp_number() {
		return send_sp_number;
	}
	public void setSend_sp_number(String send_sp_number) {
		this.send_sp_number = send_sp_number;
	}
	public String getPort_number() {
		return port_number;
	}
	public void setPort_number(String port_number) {
		this.port_number = port_number;
	}
	public String getMsg_receive_time() {
		return msg_receive_time;
	}
	public void setMsg_receive_time(String msg_receive_time) {
		this.msg_receive_time = msg_receive_time;
	}
	
}
