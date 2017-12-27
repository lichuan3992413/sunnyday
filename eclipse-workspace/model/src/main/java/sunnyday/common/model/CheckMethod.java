package sunnyday.common.model;

import java.io.Serializable;
import java.util.regex.Pattern;

public class CheckMethod  implements Serializable{
	
	private static final long serialVersionUID = -4449101936303590614L;
	
	private int sn;
	private int check_id;
	private int check_code;
	private String check_name;
	private String check_method;
	private Pattern pattern;
	
	public int getSn() {
		return sn;
	}
	public int getCheck_code() {
		return check_code;
	}
	public void setCheck_code(int check_code) {
		this.check_code = check_code;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public int getCheck_id() {
		return check_id;
	}
	public void setCheck_id(int check_id) {
		this.check_id = check_id;
	}
	public String getCheck_name() {
		return check_name;
	}
	public void setCheck_name(String check_name) {
		this.check_name = check_name;
	}
	public String getCheck_method() {
		return check_method;
	}
	public void setCheck_method(String check_method) {
		this.check_method = check_method;
	}
	public Pattern getPattern() {
		return pattern;
	}
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
	

}
