package sunnyday.common.model;

import java.io.Serializable;

public class BalanceNoticeForm implements Serializable{
	private static final long serialVersionUID = 5529939613192012530L;
	/**
	 * 主键递增
	 */
	private long sn;
	/**
	 * 用户ID
	 */
	private String user_id;
	/**
	 * 用户名称
	 */
	private String user_name;
	/**
	 * 预警余额
	 */
	private double notice_count;
	/**
	 * 预警手机号(多个以英文逗号分隔开)
	 */
	private String mobiles;
	/**
	 * 0开启(未预警），1完成达到阀值时第一次预警，2完成达到阀值百分之七十时的第二次预警
	 */
	private int warning_status;
	
	public long getSn() {
		return sn;
	}
	public void setSn(long sn) {
		this.sn = sn;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public double getNotice_count() {
		return notice_count;
	}
	public void setNotice_count(double notice_count) {
		this.notice_count = notice_count;
	}
	public String getMobiles() {
		return mobiles;
	}
	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}
	public int getWarning_status() {
		return warning_status;
	}
	public void setWarning_status(int warning_status) {
		this.warning_status = warning_status;
	}

}
