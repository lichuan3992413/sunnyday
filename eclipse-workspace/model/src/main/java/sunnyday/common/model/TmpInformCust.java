package sunnyday.common.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 账户表信息
 */
public class TmpInformCust extends BaseBean implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8503451611636154639L;
	/**
	 * 
	 */
	private long sn;
	private String accno;
	private String cust_name;
	private String prim_flag;
	private String custno;
	private String mobile;
	private int status;
	private int try_times;
	private String send_time;
	
	
	
	public long getSn() {
		return sn;
	}
	public void setSn(long sn) {
		this.sn = sn;
	}
	public String getAccno() {
		return accno;
	}
	public void setAccno(String accno) {
		this.accno = accno;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getPrim_flag() {
		return prim_flag;
	}
	public void setPrim_flag(String prim_flag) {
		this.prim_flag = prim_flag;
	}
	public String getCustno() {
		return custno;
	}
	public void setCustno(String custno) {
		this.custno = custno;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	public String getSend_time() {
		return send_time;
	}
	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}
	@Override
	public String toString() {
		return "TmpInformCust [sn=" + sn + ", accno=" + accno + ", cust_name="
				+ cust_name + ", prim_flag=" + prim_flag + ", custno=" + custno
				+ ", mobile=" + mobile + ", status=" + status + ", try_times="
				+ try_times + ", send_time=" + send_time + "]";
	}
	
	
	
}
