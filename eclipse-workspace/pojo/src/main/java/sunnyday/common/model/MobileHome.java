package sunnyday.common.model;

import java.io.Serializable;


public class MobileHome  extends BaseBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7190819106723941873L;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("mobile: ").append(mobile_no).append(", province: ").append(province).append(", city: ").append(city);
		return sb.toString();
	}
	 
	
	private String mobile_no;
	private String province;
	private String city;

	public String getMobile_no() {
		return mobile_no;
	}
	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
}
