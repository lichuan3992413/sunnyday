package sunnyday.common.model;

import java.io.Serializable;

/**
 * 
 * @author 1111182
 */
public class HistoryMessageCountForm extends BaseBean implements Serializable{
	 
	private static final long serialVersionUID = -6941962565524279384L;
	private int sn;
	private int user_sn;
	private String service_code;
	private String td_code;
	private int response;
	private String err = "";
	private String fail_desc = "";
	private String dest_flag = "";
	private String date;
	private int amount;
	
	private long submit_sn;
	
	public String getDest_flag() {
		return dest_flag;
	}
	public void setDest_flag(String dest_flag) {
		if(dest_flag == null){
			this.dest_flag = "";
		}else{
			this.dest_flag = dest_flag;
		}
	}
	public int getUser_sn() {
		return user_sn;
	}
	public void setUser_sn(int user_sn) {
		this.user_sn = user_sn;
	}
	public int getResponse() {
		return response;
	}
	public void setResponse(int response) {
		this.response = response;
	}
	public void setErr(String err) {
		this.err = err;
	}
	public String getErr() {
		if(err==null){
			err ="";
		}
		return err;
	}
	public void setFail_desc(String fail_desc) {
		this.fail_desc = fail_desc;
	}
	public String getFail_desc() {
		return fail_desc;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public int getSn() {
		return sn;
	}
	public void setService_code(String service_code) {
		this.service_code = service_code;
	}
	public String getService_code() {
		return service_code;
	}
	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}
	public String getTd_code() {
		return td_code;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDate() {
		return date;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public void setAmount(String amount){
		this.amount = Integer.parseInt(amount);
	}
	public int getAmount() {
		return amount;
	}
	public void setSubmit_sn(long submit_sn) {
		this.submit_sn = submit_sn;
	}
	public long getSubmit_sn() {
		return submit_sn;
	}
}
