package sunnyday.common.model;

import java.io.Serializable;

public class LocationInfo  extends BaseBean implements Serializable{
	
	private static final long serialVersionUID = 840775723039653773L;
	
	private int sn;
	private String mobile_scope;
	private String province;
	private String sp_type;
	
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getMobile_scope() {
		return mobile_scope;
	}
	public void setMobile_scope(String mobile_scope) {
		this.mobile_scope = mobile_scope;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getSp_type() {
		return sp_type;
	}
	public void setSp_type(String sp_type) {
		this.sp_type = sp_type;
	}
	
}
