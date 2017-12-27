package sunnyday.channel.model;

import java.io.Serializable;

public class MO implements Serializable{
	
	private static final long serialVersionUID = 3944486259837633258L;
	
	private String user_id;
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}
	public String getSp_number() {
		return sp_number;
	}
	public void setSp_number(String sp_number) {
		this.sp_number = sp_number;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getTry_times() {
		return try_times;
	}
	public void setTry_times(int try_times) {
		this.try_times = try_times;
	}
	public int getSub_msg_id() {
		return sub_msg_id;
	}
	public void setSub_msg_id(int sub_msg_id) {
		this.sub_msg_id = sub_msg_id;
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
	public int getMsg_format() {
		return msg_format;
	}
	public void setMsg_format(int msg_format) {
		this.msg_format = msg_format;
	}
	public long getReceive_time() {
		return receive_time;
	}
	public void setReceive_time(long receive_time) {
		this.receive_time = receive_time;
	}
	public long getSubmit_time() {
		return submit_time;
	}
	public void setSubmit_time(long submit_time) {
		this.submit_time = submit_time;
	}
	public String getExt_code() {
		return ext_code;
	}
	public void setExt_code(String ext_code) {
		this.ext_code = ext_code;
	}
	public int getLong_msg_id() {
		return long_msg_id;
	}
	public void setLong_msg_id(int long_msg_id) {
		this.long_msg_id = long_msg_id;
	}
	public int getLong_msg_count() {
		return long_msg_count;
	}
	public void setLong_msg_count(int long_msg_count) {
		this.long_msg_count = long_msg_count;
	}
	public int getLong_msg_sub_sn() {
		return long_msg_sub_sn;
	}
	public void setLong_msg_sub_sn(int long_msg_sub_sn) {
		this.long_msg_sub_sn = long_msg_sub_sn;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String channel_id;
	private String sp_number;
	private String mobile;
	private String content;
	private int status;
	private int try_times;
	private int sub_msg_id;
	private int msg_total;
	private int msg_sequeue; 
	private int msg_format;
	private long receive_time;
	private long submit_time;
	private String ext_code;
	private int long_msg_id;
	private int long_msg_count;
	private int long_msg_sub_sn;
	
}
