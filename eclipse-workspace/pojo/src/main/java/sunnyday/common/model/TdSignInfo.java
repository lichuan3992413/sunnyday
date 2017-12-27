package sunnyday.common.model;

import java.io.Serializable;

public class TdSignInfo implements Serializable{
	
	private static final long serialVersionUID = 5211179271807449099L;
	
	private int sn;
	private String td_code;
	private String ext_code;
	private String sign_chs;
	private String sign_eng;
	
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getTd_code() {
		return td_code;
	}
	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}
	public String getExt_code() {
		return ext_code;
	}
	public void setExt_code(String ext_code) {
		this.ext_code = ext_code;
	}
	public String getSign_chs() {
		return sign_chs;
	}
	public void setSign_chs(String sign_chs) {
		this.sign_chs = sign_chs;
	}
	public String getSign_eng() {
		return sign_eng;
	}
	public void setSign_eng(String sign_eng) {
		this.sign_eng = sign_eng;
	}
	@Override
	public String toString() {
		return "TdSignInfo [sn=" + sn + ", td_code=" + td_code + ", ext_code="
				+ ext_code + ", sign_chs=" + sign_chs + ", sign_eng="
				+ sign_eng + "]";
	}
	
	
}
