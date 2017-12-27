package sunnyday.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TdInfo  implements Serializable{

	@Override
	public String toString() {
		/*return "[td_name=" + td_name + ", td_code=" + td_code
				+ ", status=" + status + ", td_type=" + td_type
				+ ", td_sp_number=" + td_sp_number + ", filter_flag="
				+ filter_flag + ", submit_type=" + submit_type
				+ ", msg_count_cn=" + msg_count_cn + ", long_charge_count_cn="
				+ long_charge_count_cn + ", long_charge_count_pre_cn="
				+ long_charge_count_pre_cn + ", msg_count_en=" + msg_count_en
				+ ", long_charge_count_en=" + long_charge_count_en
				+ ", long_charge_count_pre_en=" + long_charge_count_pre_en
				+ ", msg_count_all_cn=" + msg_count_all_cn
				+ ", msg_count_all_en=" + msg_count_all_en + ", ext=" + ext
				+ ", ext_code=" + ext_code + ", with_gate_sign="
				+ with_gate_sign + ", sign_chs=" + sign_chs + ", sign_eng="
				+ sign_eng + ", sign_type=" + sign_type + "]";*/
		
		return prientString();
	}
	 
	public String prientString() {
		StringBuffer sb = new StringBuffer();
		sb.append("td_code: ").append(td_code).append(", td_type: ").append(td_type).append(", sign_chs: ").append(sign_chs).append(", with_gate_sign: ").append(with_gate_sign).append(", is_support_flash: ").append(is_support_flash).append("\r\n");
		return sb.toString();
	}
	
	private static final long serialVersionUID = -1131873321731692735L;
	
	private int sn;
	private String td_name;
	private String td_code;
	private int status;
	private int td_type;
	private String td_sp_number;
	private int filter_flag;
	private int submit_type;
	
	private int msg_count_cn;
	private int long_charge_count_cn;
	private int long_charge_count_pre_cn;
	private int msg_count_en;
	private int long_charge_count_en;
	private int long_charge_count_pre_en;
	private int msg_count_all_cn;
	private int msg_count_all_en;
	
	private String ext;
	private String ext_code;
	private int with_gate_sign;
	private String sign_chs;
	private String sign_eng;
	private int sign_type;
	private int is_support_flash;
	
	private  Map<String, TdSignInfo> signMap = new HashMap<String, TdSignInfo>();
	
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getTd_name() {
		return td_name;
	}
	public void setTd_name(String td_name) {
		this.td_name = td_name;
	}
	public String getTd_code() {
		return td_code;
	}
	public int getSign_type() {
		return sign_type;
	}
	public void setSign_type(int sign_type) {
		this.sign_type = sign_type;
	}
	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getFilter_flag() {
		return filter_flag;
	}
	public void setFilter_flag(int filter_flag) {
		this.filter_flag = filter_flag;
	}
	/**
	 * 普通中文计费字数
	 * @return
	 */
	public int getMsg_count_cn() {
		return msg_count_cn;
	}
	public void setMsg_count_cn(int msg_count_cn) {
		this.msg_count_cn = msg_count_cn;
	}
	public int getMsg_count_en() {
		return msg_count_en;
	}
	public void setMsg_count_en(int msg_count_en) {
		this.msg_count_en = msg_count_en;
	}
	public int getMsg_count_all_cn() {
		return msg_count_all_cn;
	}
	public void setMsg_count_all_cn(int msg_count_all_cn) {
		this.msg_count_all_cn = msg_count_all_cn;
	}
	public int getMsg_count_all_en() {
		return msg_count_all_en;
	}
	public void setMsg_count_all_en(int msg_count_all_en) {
		this.msg_count_all_en = msg_count_all_en;
	}
	
	/**
	 * 长短信非最后一条计费字数
	 * @return
	 */
	public int getLong_charge_count_pre_cn() {
		return long_charge_count_pre_cn;
	}
	public void setLong_charge_count_pre_cn(int long_charge_count_pre_cn) {
		this.long_charge_count_pre_cn = long_charge_count_pre_cn;
	}
	public int getLong_charge_count_pre_en() {
		return long_charge_count_pre_en;
	}
	public void setLong_charge_count_pre_en(int long_charge_count_pre_en) {
		this.long_charge_count_pre_en = long_charge_count_pre_en;
	}
	/**
	 * 长短信最后一条计费字数
	 * @return
	 */
	public int getLong_charge_count_cn() {
		return long_charge_count_cn;
	}
	public void setLong_charge_count_cn(int long_charge_count_cn) {
		this.long_charge_count_cn = long_charge_count_cn;
	}
	public int getLong_charge_count_en() {
		return long_charge_count_en;
	}
	public void setLong_charge_count_en(int long_charge_count_en) {
		this.long_charge_count_en = long_charge_count_en;
	}
	public void setSubmit_type(int submit_type) {
		this.submit_type = submit_type;
	}
	public int getSubmit_type() {
		return submit_type;
	}
	public void setTd_sp_number(String td_sp_number) {
		this.td_sp_number = td_sp_number;
	}
	public String getTd_sp_number() {
		return td_sp_number;
	}
	public void setTd_type(int td_type) {
		this.td_type = td_type;
	}
	public int getTd_type() {
		return td_type;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getExt_code() {
		return ext_code;
	}
	public void setExt_code(String ext_code) {
		this.ext_code = ext_code;
	}
	public int getWith_gate_sign() {
		return with_gate_sign;
	}
	public void setWith_gate_sign(int with_gate_sign) {
		this.with_gate_sign = with_gate_sign;
	}
	public Map<String, TdSignInfo> getSignMap() {
		return signMap;
	}
	public void setSignMap(Map<String, TdSignInfo> signMap) {
		this.signMap = signMap;
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
	public TdSignInfo toTdSignInfo() {
		TdSignInfo tdSignInfo = new TdSignInfo();
		tdSignInfo.setTd_code(this.td_code);
		tdSignInfo.setExt_code(this.ext_code);
		tdSignInfo.setSign_chs(this.sign_chs);
		tdSignInfo.setSign_eng(this.sign_eng);
		return tdSignInfo;
	}

	public int getIs_support_flash() {
		return is_support_flash;
	}

	public void setIs_support_flash(int is_support_flash) {
		this.is_support_flash = is_support_flash;
	}
}
