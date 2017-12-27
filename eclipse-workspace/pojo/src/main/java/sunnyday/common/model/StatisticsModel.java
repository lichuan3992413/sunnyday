package sunnyday.common.model;

import java.io.Serializable;

public class StatisticsModel extends BaseBean implements Serializable{
	private static final long serialVersionUID = 5644764277752770072L;
	private int sn;
	private int user_sn;
	private String user_id;
	private String td_code;
	private String send_time;
	private int send_count;
	private int success_count;
	private int fail_count;
	private int unknown_count;
	
	private int report_five_count;
	private int report_ten_count;
	private int report_twenty_count;
	private int report_sixty_count;
	private int report_other_count;
	
 
	
	private String update_time;
	
	 
 

	public String getStatisticsKey(){
		return user_id+td_code+send_time;
	}
	
	@Override
	public String toString() {
		return "StatisticsModel [sn=" + sn + ", user_sn=" + user_sn
				+ ", user_id=" + user_id + ", td_code=" + td_code
				+ ", send_time=" + send_time + ", send_count=" + send_count
				+ ", success_count=" + success_count + ", fail_count="
				+ fail_count + ", unknown_count=" + unknown_count
				+ ", report_five_count=" + report_five_count
				+ ", report_ten_count=" + report_ten_count
				+ ", report_twenty_count=" + report_twenty_count
				+ ", report_sixty_count=" + report_sixty_count
				+ ", report_other_count=" + report_other_count
				+ ", update_time=" + update_time   + "]";
	}
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public int getUser_sn() {
		return user_sn;
	}
	public void setUser_sn(int user_sn) {
		this.user_sn = user_sn;
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
	public String getSend_time() {
		return send_time;
	}
	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}
	public int getSend_count() {
		return send_count;
	}
	public void setSend_count(int send_count) {
		this.send_count = send_count;
	}
	public int getSuccess_count() {
		return success_count;
	}
	public void setSuccess_count(int success_count) {
		this.success_count = success_count;
	}
	public int getFail_count() {
		return fail_count;
	}
	public void setFail_count(int fail_count) {
		this.fail_count = fail_count;
	}
	public int getUnknown_count() {
		return unknown_count;
	}
	public void setUnknown_count(int unknown_count) {
		this.unknown_count = unknown_count;
	}
	public int getReport_five_count() {
		return report_five_count;
	}
	public void setReport_five_count(int report_five_count) {
		this.report_five_count = report_five_count;
	}
	public int getReport_ten_count() {
		return report_ten_count;
	}
	public void setReport_ten_count(int report_ten_count) {
		this.report_ten_count = report_ten_count;
	}
	public int getReport_twenty_count() {
		return report_twenty_count;
	}
	public void setReport_twenty_count(int report_twenty_count) {
		this.report_twenty_count = report_twenty_count;
	}
	public int getReport_sixty_count() {
		return report_sixty_count;
	}
	public void setReport_sixty_count(int report_sixty_count) {
		this.report_sixty_count = report_sixty_count;
	}
	public int getReport_other_count() {
		return report_other_count;
	}
	public void setReport_other_count(int report_other_count) {
		this.report_other_count = report_other_count;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	 
}
