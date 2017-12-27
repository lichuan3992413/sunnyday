package sunnyday.common.model;

import java.io.Serializable;

public class StatisticsHistoryModel extends BaseBean implements Serializable{
	private static final long serialVersionUID = -1670387247529150722L;
	private int sn;
	private int user_sn;
	private String td_code;
	private String service_code;
	private int response;
	private String err="";
	private String fail_desc="";
	private String dest_flag="" ;
	private String send_date="";
	private int amount;
	private String update_time;
	 
 

	public String getStatisticsKey(){
		return user_sn+td_code+service_code+response+err+fail_desc+dest_flag+send_date;
	}
	
	@Override
	public String toString() {
		return "StatisticsHistoryModel [sn=" + sn + ", user_sn=" + user_sn
				+ ", td_code=" + td_code + ", service_code=" + service_code
				+ ", response=" + response + ", err=" + err + ", fail_desc="
				+ fail_desc + ", send_date=" + send_date + ", amount=" + amount
				+ ", dest_flag=" + dest_flag  + "]";
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
	public int getResponse() {
		return response;
	}
	public void setResponse(int response) {
		this.response = response;
	}
	public String getErr() {
		if(err==null){
			err ="";
		}
		return err;
	}
	public void setErr(String err) {
		if(err==null){
			err ="";
		}
		this.err = err;
	}
	public String getFail_desc() {
		if(fail_desc==null){
			fail_desc ="";
		}
		return fail_desc;
	}
	public void setFail_desc(String fail_desc) {
		if(fail_desc==null){
			fail_desc ="";
		}
		this.fail_desc = fail_desc;
	}
	public String getSend_date() {
		return send_date;
	}
	public void setSend_date(String send_date) {
		this.send_date = send_date;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getDest_flag() {
		if(dest_flag==null){
			dest_flag ="";
		}
		return dest_flag;
	}

	public void setDest_flag(String dest_flag) {
		if(dest_flag==null){
			dest_flag ="";
		}
		this.dest_flag = dest_flag;
	}

}
