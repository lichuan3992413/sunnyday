package sunnyday.common.model;

import java.io.Serializable;

public class NetSwitchedMobileInfo  implements Serializable{

	private static final long serialVersionUID = -7483125418993414478L;
	
	private String mobile; //手机号
	private int dest_td_type; //目标运营商(1 移动  2 联通  3 电信)
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getDest_td_type() {
		return dest_td_type;
	}
	public void setDest_td_type(int dest_td_type) {
		this.dest_td_type = dest_td_type;
	}
}
