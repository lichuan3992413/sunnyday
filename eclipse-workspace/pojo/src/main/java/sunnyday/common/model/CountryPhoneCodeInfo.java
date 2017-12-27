package sunnyday.common.model;

import java.io.Serializable;

public class CountryPhoneCodeInfo extends BaseBean implements Serializable{

	private static final long serialVersionUID = 5904613627965284615L;
	
	private int sn; //主键自增
	private String country_en; //国家英文名称
	private String country_cn; //国家中文名称
	private String short_code; //国家简称
	private String phone_pre; //国际区号
	private String phone_province; //省号
	private double price; //0.00000
	private String user_id; //用户ID

	@Override
	public String toString() {
		return "CountryPhoneCodeInfo [sn=" + sn + ", country_en=" + country_en
				+ ", country_cn=" + country_cn + ", short_code=" + short_code
				+ ", phone_pre=" + phone_pre + ", phone_province="
				+ phone_province + ", price=" + price + ", user_id=" + user_id
				+ "]";
	}
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getCountry_en() {
		return country_en;
	}
	public void setCountry_en(String country_en) {
		this.country_en = country_en;
	}
	public String getCountry_cn() {
		return country_cn;
	}
	public void setCountry_cn(String country_cn) {
		this.country_cn = country_cn;
	}
	public String getShort_code() {
		return short_code;
	}
	public void setShort_code(String short_code) {
		this.short_code = short_code;
	}
	public String getPhone_pre() {
		return phone_pre;
	}
	public void setPhone_pre(String phone_pre) {
		this.phone_pre = phone_pre;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getPhone_province() {
		return phone_province;
	}
	public void setPhone_province(String phone_province) {
		this.phone_province = phone_province;
	}
	
	
	
}
