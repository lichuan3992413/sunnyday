package sunnyday.common.model;

import java.io.Serializable;

public class SpNumberFilterInfo extends BaseBean implements Serializable{
 
	private static final long serialVersionUID = -1132067580308672998L;
	private int sn;
	private String user_id;
	private String sp_number;
	private int filter_type;
	
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getSp_number() {
		return sp_number;
	}
	public void setSp_number(String sp_number) {
		this.sp_number = sp_number;
	}
	public int getFilter_type() {
		return filter_type;
	}
	public void setFilter_type(int filter_type) {
		this.filter_type = filter_type;
	}
}
