package sunnyday.common.model;

import java.io.Serializable;

/**
 * 上行匹配信息对象
 * @author Administrator
 *
 */
public class DeliverMatchInfo implements Serializable{
	private static final long serialVersionUID = -2921472476960032595L;
	private String  user_id;
	private String  display_number;
	private String  td_sp_number;
	private String  all_ext_code;
	private String  user_ext_code;
	private String  template_ext_code;
	private String  submit_ext_code;
	private String  template_id;
	private boolean is_td = false;
	
	
	@Override
	public String toString() {
		return "DeliverMatchInfo [user_id=" + user_id + ", display_number=" + display_number + ", td_sp_number="
				+ td_sp_number + ", all_ext_code=" + all_ext_code + ", user_ext_code=" + user_ext_code
				+ ", template_ext_code=" + template_ext_code + ", template_id=" + template_id + ", is_td="+is_td+", submit_ext_code="+submit_ext_code+"]";
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getDisplay_number() {
		return display_number;
	}
	public void setDisplay_number(String display_number) {
		this.display_number = display_number;
	}
	public String getTd_sp_number() {
		return td_sp_number;
	}
	public void setTd_sp_number(String td_sp_number) {
		this.td_sp_number = td_sp_number;
	}
	public String getAll_ext_code() {
		return all_ext_code;
	}
	public void setAll_ext_code(String all_ext_code) {
		this.all_ext_code = all_ext_code;
	}
	public String getUser_ext_code() {
		return user_ext_code;
	}
	public void setUser_ext_code(String user_ext_code) {
		this.user_ext_code = user_ext_code;
	}
	public String getTemplate_ext_code() {
		return template_ext_code;
	}
	public void setTemplate_ext_code(String template_ext_code) {
		this.template_ext_code = template_ext_code;
	}
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public boolean isIs_td() {
		return is_td;
	}
	public void setIs_td(boolean is_td) {
		this.is_td = is_td;
	}
	public String getSubmit_ext_code() {
		return submit_ext_code;
	}
	public void setSubmit_ext_code(String submit_ext_code) {
		this.submit_ext_code = submit_ext_code;
	}
	
	 
}
