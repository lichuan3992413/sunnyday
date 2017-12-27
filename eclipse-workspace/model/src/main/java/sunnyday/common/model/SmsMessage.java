package sunnyday.common.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import sunnyday.tools.util.*;
public class SmsMessage implements Serializable, Cloneable{
	
	private static final long serialVersionUID = 1L;
	//submit参数
	private long sn;
	private long submit_sn;
	private int user_sn;
	private String user_id;
	private String service_code;
	private String ext_code="";
	private String user_ext_code="";
	private String td_code;
	private String sp_number;
	private String area_code;
	private String mobile;
	private String msg_content;
	private int msg_format;
	private String msg_id;//用户提交时的msgid
	private String insert_time;
	private String update_time;
	private int status;
	private int response;
	private String fail_desc;
	private String tmp_msg_id; //不同通道的临时msg_id
	private int stat_flag;
	private int pknumber;
	private int pktotal;
	private int sub_msg_id;
	private int charge_count;
	private double price;
	private int filter_flag;
	private String msg_receive_time;//接收端收到的时间
	private String msg_deal_time;//dealdata处理开始时间
	private String msg_scan_time;//发送端扫描时间
	private String msg_send_time;//数据发送时间
	private String msg_report_time;//状态报告返回时间
	private String dest_flag;//流量走向标识
	private String err;
	private String check_user;
	private String src_number;//原始接入号
	/**
	 * 长短信唯一标识
	 */
	private String msg_guid ;


	
	private int cache_sn;
	
	private String country_cn;//国家中文名称
	private String ori_mobile;//原始手机号码
	
	private String complete_content;
	private String report_fail_desc = "UNDELIV";
	
	private String template_id;
	
	private int try_times;
	
	private String md5_index;
	public int length() {
		return md5_index.length();
	}
	public boolean isEmpty() {
		return md5_index.isEmpty();
	}
	public char charAt(int index) {
		return md5_index.charAt(index);
	}
	public int codePointAt(int index) {
		return md5_index.codePointAt(index);
	}
	public int codePointBefore(int index) {
		return md5_index.codePointBefore(index);
	}
	public int codePointCount(int beginIndex, int endIndex) {
		return md5_index.codePointCount(beginIndex, endIndex);
	}
	public int offsetByCodePoints(int index, int codePointOffset) {
		return md5_index.offsetByCodePoints(index, codePointOffset);
	}
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		md5_index.getChars(srcBegin, srcEnd, dst, dstBegin);
	}
	public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) {
		md5_index.getBytes(srcBegin, srcEnd, dst, dstBegin);
	}
	public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
		return md5_index.getBytes(charsetName);
	}
	public byte[] getBytes(Charset charset) {
		return md5_index.getBytes(charset);
	}
	public byte[] getBytes() {
		return md5_index.getBytes();
	}
	public boolean equals(Object anObject) {
		return md5_index.equals(anObject);
	}
	public boolean contentEquals(StringBuffer sb) {
		return md5_index.contentEquals(sb);
	}
	public boolean contentEquals(CharSequence cs) {
		return md5_index.contentEquals(cs);
	}
	public boolean equalsIgnoreCase(String anotherString) {
		return md5_index.equalsIgnoreCase(anotherString);
	}
	public int compareTo(String anotherString) {
		return md5_index.compareTo(anotherString);
	}
	public int compareToIgnoreCase(String str) {
		return md5_index.compareToIgnoreCase(str);
	}
	public boolean regionMatches(int toffset, String other, int ooffset, int len) {
		return md5_index.regionMatches(toffset, other, ooffset, len);
	}
	public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
		return md5_index.regionMatches(ignoreCase, toffset, other, ooffset, len);
	}
	public boolean startsWith(String prefix, int toffset) {
		return md5_index.startsWith(prefix, toffset);
	}
	public boolean startsWith(String prefix) {
		return md5_index.startsWith(prefix);
	}
	public boolean endsWith(String suffix) {
		return md5_index.endsWith(suffix);
	}
	public int hashCode() {
		return md5_index.hashCode();
	}
	public int indexOf(int ch) {
		return md5_index.indexOf(ch);
	}
	public int indexOf(int ch, int fromIndex) {
		return md5_index.indexOf(ch, fromIndex);
	}
	public int lastIndexOf(int ch) {
		return md5_index.lastIndexOf(ch);
	}
	public int lastIndexOf(int ch, int fromIndex) {
		return md5_index.lastIndexOf(ch, fromIndex);
	}
	public int indexOf(String str) {
		return md5_index.indexOf(str);
	}
	public int indexOf(String str, int fromIndex) {
		return md5_index.indexOf(str, fromIndex);
	}
	public int lastIndexOf(String str) {
		return md5_index.lastIndexOf(str);
	}
	public int lastIndexOf(String str, int fromIndex) {
		return md5_index.lastIndexOf(str, fromIndex);
	}
	public String substring(int beginIndex) {
		return md5_index.substring(beginIndex);
	}
	public String substring(int beginIndex, int endIndex) {
		return md5_index.substring(beginIndex, endIndex);
	}
	public CharSequence subSequence(int beginIndex, int endIndex) {
		return md5_index.subSequence(beginIndex, endIndex);
	}
	public String concat(String str) {
		return md5_index.concat(str);
	}
	public String replace(char oldChar, char newChar) {
		return md5_index.replace(oldChar, newChar);
	}
	public boolean matches(String regex) {
		return md5_index.matches(regex);
	}
	public boolean contains(CharSequence s) {
		return md5_index.contains(s);
	}
	public String replaceFirst(String regex, String replacement) {
		return md5_index.replaceFirst(regex, replacement);
	}
	public String replaceAll(String regex, String replacement) {
		return md5_index.replaceAll(regex, replacement);
	}
	public String replace(CharSequence target, CharSequence replacement) {
		return md5_index.replace(target, replacement);
	}
	public String[] split(String regex, int limit) {
		return md5_index.split(regex, limit);
	}
	public String[] split(String regex) {
		return md5_index.split(regex);
	}
	public String toLowerCase(Locale locale) {
		return md5_index.toLowerCase(locale);
	}
	public String toLowerCase() {
		return md5_index.toLowerCase();
	}
	public String toUpperCase(Locale locale) {
		return md5_index.toUpperCase(locale);
	}
	public String toUpperCase() {
		return md5_index.toUpperCase();
	}
	public String trim() {
		return md5_index.trim();
	}
	public char[] toCharArray() {
		return md5_index.toCharArray();
	}
	public String intern() {
		return md5_index.intern();
	}
	public String getReport_fail_desc() {
		return report_fail_desc;
	}
	public void setReport_fail_desc(String report_fail_desc) {
		this.report_fail_desc = report_fail_desc;
	}
	public String getMd5_index() {
		return md5_index;
	}
	public void setMd5_index(String md5_index) {
		this.md5_index = md5_index;
	}
	public Long getRpt_seq() {
		return rpt_seq;
	}
	public void setRpt_seq(Long rpt_seq) {
		this.rpt_seq = rpt_seq;
	}
	public int getDo_times() {
		return do_times;
	}
	public void setDo_times(int do_times) {
		this.do_times = do_times;
	}
	public int getIs_encode() {
		return is_encode;
	}
	public void setIs_encode(int is_encode) {
		this.is_encode = is_encode;
	}
	private Long rpt_seq; //状态报告和下发历史的唯一关联标识
	private int do_times;
	private int is_encode;//是否进行了加密：1是 ，0否
	
	private Map<String, Object> extraFields = new HashMap<String, Object>();
	
	public String getFullUserSubmitCode(){
		return service_code + ext_code + user_ext_code;
	}
	public String getSrc_number() {
		return src_number;
	}

	public void setSrc_number(String src_number) {
		this.src_number = src_number;
	}
	@Override
	public String toString() {
		return "SmsMessage [sn=" + sn + ", submit_sn=" + submit_sn
				+ ", user_sn=" + user_sn + ", user_id=" + user_id
				+ ", service_code=" + service_code + ", ext_code=" + ext_code
				+ ", user_ext_code=" + user_ext_code + ", td_code=" + td_code
				+ ", sp_number=" + sp_number + ", area_code=" + area_code
				+ ", mobile=" + mobile + ", msg_content=" + msg_content
				+ ", msg_format=" + msg_format + ", msg_id=" + msg_id
				+ ", insert_time=" + insert_time + ", update_time="
				+ update_time + ", status=" + status + ", response=" + response
				+ ", fail_desc=" + fail_desc + ", tmp_msg_id=" + tmp_msg_id
				+ ", stat_flag=" + stat_flag + ", pknumber=" + pknumber
				+ ", pktotal=" + pktotal + ", sub_msg_id=" + sub_msg_id
				+ ", charge_count=" + charge_count + ", price=" + price
				+ ", filter_flag=" + filter_flag + ", msg_receive_time="
				+ msg_receive_time + ", msg_deal_time=" + msg_deal_time
				+ ", msg_scan_time=" + msg_scan_time + ", msg_send_time="
				+ msg_send_time + ", msg_report_time=" + msg_report_time
				+ ", dest_flag=" + dest_flag + ", err=" + err + ", check_user="
				+ check_user + ", cache_sn=" + cache_sn + ", complete_content="
				+ complete_content + ", extraFields=" + extraFields + ". msg_guid= "+msg_guid+"]";
	}

	public void addExtraField(String key, Object value){
		boolean isKeyOK = (key != null && !key.equals(""));
		boolean isValueOK = (value != null);
		if(isKeyOK && isValueOK){
			if(extraFields == null){
				extraFields = new HashMap<String, Object>();;
			}
			extraFields.put(key, value);
		}else{
			System.out.println("附加属性set失败：isKeyOK = " + key + " && isValueOK = " + value);
		}
	}
	
	public Object getExtraField(String key){
		Object result = null;
		boolean isExtraNotNull = this.extraFields != null;
		boolean isKeyOK = key != null && !key.equals("");
		if(isExtraNotNull && isKeyOK){
			result = this.extraFields.get(key);
		}
		return result;
	}
	
	public void updatePropertiesByReport(ReportBean reportBean) {
		this.err = reportBean.getErr();
		if(err != null){
			if(err.trim().equals("000") || err.trim().equals("0")){
				setResponse(0);
			}else{
				setResponse(2);
			}
		}
		this.setFail_desc(reportBean.getFail_desc());
		
		this.setMsg_report_time(DateUtil.currentTimeToMs());
	}
	
	public List<ReportBean> toReportForm(){
		List<ReportBean> list = new ArrayList<ReportBean>();
		String[] msgIds = this.getMsg_id().split(",");
		
		for(String msgId : msgIds){
			ReportBean reportForm = new ReportBean();
			reportForm.setUser_id(this.user_id);
			reportForm.setStatus(status);
			reportForm.setTd_code(this.td_code);
			reportForm.setSp_number(this.getSp_number());
			reportForm.setMobile(this.getMobile());
			reportForm.setFail_desc(report_fail_desc);
			reportForm.setMsg_id(msgId);
			reportForm.setErr(String.valueOf(this.response));
			reportForm.setStat(status);
			reportForm.setSubmitTime(System.currentTimeMillis());//作为report中的submit_time
			
			list.add(reportForm);
		}
		return list;
	}
	
	public List<ReportBean> toReportForm(ReportBean report) {
		List<ReportBean> list = new ArrayList<ReportBean>();
		String[] msgIds = this.getMsg_id().split(",");
		
		for(String msgId : msgIds){
			ReportBean reportForm = new ReportBean();
			reportForm.setUser_id(this.user_id);
			reportForm.setStatus(status);
			reportForm.setSp_number(this.getSp_number());
			reportForm.setTd_code(this.td_code);
			reportForm.setMobile(this.getMobile());
			reportForm.setFail_desc(report_fail_desc);
			reportForm.setMsg_id(msgId);
			reportForm.setErr(report.getErr());
			reportForm.setStat(report.getStat());
			reportForm.setSubmitTime(System.currentTimeMillis());//作为report中的submit_time
			
			list.add(reportForm);
		}
		return list;
	}

	public Object clone(){
		SmsMessage result = null;
		try {
			result = (SmsMessage)super.clone();
		} catch (CloneNotSupportedException e) {
			
		}
		return result;
	}
	
	public long getSubmit_sn() {
		return submit_sn;
	}

	public void setSubmit_sn(long submit_sn) {
		this.submit_sn = submit_sn;
	}

	public String getDest_flag() {
		return dest_flag;
	}
	public String getCountry_cn() {
		return country_cn;
	}

	public void setCountry_cn(String country_cn) {
		this.country_cn = country_cn;
	}

	public String getOri_mobile() {
		return ori_mobile;
	}

	public void setOri_mobile(String ori_mobile) {
		this.ori_mobile = ori_mobile;
	}

	public void setDest_flag(String dest_flag) {
		this.dest_flag = dest_flag;
	}
	public int getUser_sn() {
		return user_sn;
	}
	public void setUser_sn(int userSn) {
		user_sn = userSn;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getSp_number() {
		return sp_number;
	}
	public void setSp_number(String spNumber) {
		sp_number = spNumber;
	}
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msgId) {
		msg_id = msgId;
	}
	public String getMsg_content() {
		return msg_content;
	}
	public void setMsg_content(String msgContent) {
		msg_content = msgContent;
	}
	public String getInsert_time() {
		return insert_time;
	}
	public void setInsert_time(String insertTime) {
		insert_time = insertTime;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String updateTime) {
		update_time = updateTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getResponse() {
		return response;
	}
	public void setResponse(int response) {
		this.response = response;
	}
	public int getPknumber() {
		return pknumber;
	}
	public void setPknumber(int pknumber) {
		this.pknumber = pknumber;
	}
	public int getPktotal() {
		return pktotal;
	}
	public void setPktotal(int pktotal) {
		this.pktotal = pktotal;
	}
	public int getSub_msg_id() {
		return sub_msg_id;
	}
	public void setSub_msg_id(int subMsgId) {
		sub_msg_id = subMsgId;
	}
	public int getMsg_format() {
		return msg_format;
	}
	public void setMsg_format(int msgFormat) {
		msg_format = msgFormat;
	}
	public int getCharge_count() {
		return charge_count;
	}
	public void setCharge_count(int chargeCount) {
		charge_count = chargeCount;
	}
	public void setFail_desc(String fail_desc) {
		this.fail_desc = fail_desc;
	}
	public String getFail_desc() {
		return fail_desc;
	}
	public void setTmp_msg_id(String tmp_msg_id) {
		this.tmp_msg_id = tmp_msg_id;
	}
	public String getTmp_msg_id() {
		return tmp_msg_id;
	}
	public void setStat_flag(int stat_flag) {
		this.stat_flag = stat_flag;
	}
	public int getStat_flag() {
		return stat_flag;
	}
	public void setComplete_content(String complete_content) {
		this.complete_content = complete_content;
	}
	public String getComplete_content() {
		return complete_content;
	}

	public void setExtraFields(Map<String, Object> extraFields) {
		this.extraFields = extraFields;
	}
	public Map<String, Object> getExtraFields() {
		return extraFields;
	}
	public void setMsg_receive_time(String msg_receive_time) {
		this.msg_receive_time = msg_receive_time;
	}

	public String getMsg_receive_time() {
		return msg_receive_time;
	}

	public void setMsg_deal_time(String msg_deal_time) {
		this.msg_deal_time = msg_deal_time;
	}

	public String getMsg_deal_time() {
		return msg_deal_time;
	}

	public void setMsg_scan_time(String msg_scan_time) {
		this.msg_scan_time = msg_scan_time;
	}

	public String getMsg_scan_time() {
		return msg_scan_time;
	}

	public void setMsg_send_time(String msg_send_time) {
		this.msg_send_time = msg_send_time;
	}

	public String getMsg_send_time() {
		return msg_send_time;
	}

	public void setMsg_report_time(String msg_report_time) {
		this.msg_report_time = msg_report_time;
	}

	public String getMsg_report_time() {
		return msg_report_time;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPrice() {
		return price;
	}

	public void setFilter_flag(int filter_flag) {
		this.filter_flag = filter_flag;
	}

	public int getFilter_flag() {
		return filter_flag;
	}

	public void setService_code(String service_code) {
		this.service_code = service_code;
	}

	public String getService_code() {
		return service_code;
	}

	public void setErr(String err) {
		this.err = err;
	}

	public String getErr() {
		return err;
	}

	public void setExt_code(String ext_code) {
		this.ext_code = ext_code;
	}

	public String getExt_code() {
		return ext_code;
	}

	public void setUser_ext_code(String user_ext_code) {
		this.user_ext_code = user_ext_code;
	}

	public String getUser_ext_code() {
		return user_ext_code;
	}

	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}

	public String getTd_code() {
		return td_code;
	}

	public void setSn(long sn) {
		this.sn = sn;
	}

	public long getSn() {
		return sn;
	}

	public void setCheck_user(String check_user) {
		this.check_user = check_user;
	}
	public String getCheck_user() {
		return check_user;
	}
	public void setCache_sn(int cache_sn) {
		this.cache_sn = cache_sn;
	}
	public int getCache_sn() {
		return cache_sn;
	}

	public int getTry_times() {
		return try_times;
	}

	public void setTry_times(int try_times) {
		this.try_times = try_times;
	}

	/**
	 * @param area_code the area_code to set
	 */
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	/**
	 * @return the area_code
	 */
	public String getArea_code() {
		return area_code;
	}
	
	public String getMsg_guid() {
		return msg_guid;
	}
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public void setMsg_guid(String msg_guid) {
		this.msg_guid = msg_guid;
	}

}
