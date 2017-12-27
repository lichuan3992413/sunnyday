package sunnyday.common.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import sunnyday.adapter.redis.Chargable;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.DateUtil;


/**
 * 下发信息实例
 * @author dear
 *
 */
/**
 * @author 1007025
 *
 */
public class SubmitBean implements Serializable, Chargable{
	private Logger log = CommonLogFactory.getCommonLog(this.getClass());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7506256005612974639L;
	private int userSn;
	private String user_id;
	private String[] mobiles;
	private String sign_ext_code;
	private int yw_code;
	public int getYw_code() {
		return yw_code;
	}
	public void setYw_code(int yw_code) {
		this.yw_code = yw_code;
	}

	private int check_length;//校验长度
	public int getCheck_length() {
		return check_length;
	}
	public void setCheck_length(int check_length) {
		this.check_length = check_length;
	}

	private int with_gate_sign;
	private String send_sp_number;
	public String getSend_sp_number() {
		return send_sp_number;
	}
	public void setSend_sp_number(String send_sp_number) {
		this.send_sp_number = send_sp_number;
	}
	public int getWith_gate_sign() {
		return with_gate_sign;
	}
	public void setWith_gate_sign(int with_gate_sign) {
		this.with_gate_sign = with_gate_sign;
	}
	public String getSign_ext_code() {
		return sign_ext_code;
	}
	public void setSign_ext_code(String sign_ext_code) {
		this.sign_ext_code = sign_ext_code;
	}

	private String submit_type;//接口类型(CMPP,SGIP,SMGP)
	public String getSubmit_type() {
		return submit_type;
	}
	public void setSubmit_type(String submit_type) {
		this.submit_type = submit_type;
	}

	private int resp;
	public int getResp() {
		return resp;
	}
	public void setResp(int resp) {
		this.resp = resp;
	}

	private String content;
	private String service_code;
	private String ext_code;
	private String user_ext_code;
	private String sp_number;//service对应的td的sp_number + service对应的ext_code + 客户提交的user_ext_code
	private String msg_id;//给客户匹配状态报告使用的msg_id
	private String td_code;//service对应的td的td_code
	private int pkTotal;//拆分总条数
	private int pkNumber;//第几条
	private int subMsgID;//用户自定义的长短信中同样内容的消息id,http无对应值，0
	private int msg_format;//http无对应值,默认8
	private int charge_count;//接收端校验赋值，根据通道表计费规则
	private double price;//业务表单价
	private String country_cn;//国家中文名称
	private String ori_mobile;//原始手机号码
	private String msg_receive_time = DateUtil.currentTimeToMs();
	private String signature;//签名信息
	private String src_number;//原始接入号
	private String operator_signature;// 运营商签名信息
	
	/** 动态字段扩展 */
	private Map<String, Object> extraFields = new HashMap<String, Object>();
	
	public void setExtraFields(Map<String, Object> extraFields) {
		this.extraFields = extraFields;
	}
	public Map<String, Object> getExtraFields() {
		return extraFields;
	}
	
	public String getMobilesString(){
		StringBuffer sb = new StringBuffer();
		if(mobiles!=null&&mobiles.length>0){
			for(String m: mobiles){
				sb.append(m).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	public void addExtraField(String key, Object value){
		boolean isKeyOK = (key != null && !key.equals(""));
		boolean isValueOK = (value != null);
		if(isKeyOK && isValueOK){
			if(extraFields == null){
				extraFields = new HashMap<String, Object>();
			}
			extraFields.put(key, value);
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

	public String getOperator_signature() {
		return operator_signature;
	}

	public void setOperator_signature(String operator_signature) {
		this.operator_signature = operator_signature;
	}

	public String getSrc_number() {
		return src_number;
	}

	public void setSrc_number(String src_number) {
		this.src_number = src_number;
	}

	public double getCharge_sum() {
		return charge_count * price;
	}
	
	 

	@Override
	public String toString() {
		return "SubmitBean [user_id=" + user_id + ", mobiles="
				+ Arrays.toString(mobiles) + ", content=" + content
				+ ", service_code=" + service_code + ", ext_code=" + ext_code
				+ ", user_ext_code=" + user_ext_code + ", sp_number="
				+ sp_number + ", msg_id=" + msg_id + ", td_code=" + td_code
				+ ", pkTotal=" + pkTotal + ", pkNumber=" + pkNumber
				+ ", subMsgID=" + subMsgID + ", msg_format=" + msg_format
				+ ", charge_count=" + charge_count + ", price=" + price
				+ ", country_cn=" + country_cn + ", ori_mobile=" + ori_mobile
				+ ", msg_receive_time=" + msg_receive_time + ", signature="
				+ signature + ", src_number=" + src_number
				+ ", operator_signature=" + operator_signature + "]";
	}
	
	public SubmitBean() {
		
	}

	public SubmitBean(int userSn, String[] mobiles, String content,
			String spNumber, String msgid , String td_code , int pkTotal , int pkNumber , int subMsgID , int chargeCount, double price, int msgFormat, String service_code, String id) {
		this.setUserSn(userSn);
		this.user_id = id;
		this.service_code = service_code;
		this.mobiles = mobiles;
		this.content = content;
		this.sp_number = spNumber;
		this.setMsg_id(msgid);
		this.td_code = td_code;
		this.setPkTotal(pkTotal);
		this.setPkNumber(pkNumber);
		this.setSubMsgID(subMsgID);
		this.charge_count = chargeCount;
		this.price = price;
		this.msg_format = msgFormat;
	}

	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void log() {
		if (log.isDebugEnabled()) {
			log.debug(toString());
		}
	}
	public String[] getMobiles() {
		return mobiles;
	}
	public void setMobiles(String[] mobiles) {
		this.mobiles = mobiles;
	}
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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

	public String getSp_number() {
		return sp_number;
	}
	public void setSp_number(String spNumber) {
		sp_number = spNumber;
	}
	public String getTd_code() {
		return td_code;
	}

	public void setTd_code(String td_code) {
		this.td_code = td_code;
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

	public void setCharge_count(int charge_count) {
		this.charge_count = charge_count;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPrice() {
		return price;
	}

	public void setService_code(String service_code) {
		this.service_code = service_code;
	}

	public String getService_code() {
		return service_code;
	}

	public void setUserSn(int userSn) {
		this.userSn = userSn;
	}

	public int getUserSn() {
		return userSn;
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

	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}

	public String getMsg_id() {
		return msg_id;
	}

	public int getPkTotal() {
		return pkTotal;
	}

	public void setPkTotal(int pkTotal) {
		this.pkTotal = pkTotal;
	}

	public int getPkNumber() {
		return pkNumber;
	}

	public void setPkNumber(int pkNumber) {
		this.pkNumber = pkNumber;
	}

	public int getSubMsgID() {
		return subMsgID;
	}

	public void setSubMsgID(int subMsgID) {
		this.subMsgID = subMsgID;
	}

	public void setMsg_receive_time(String msg_receive_time) {
		this.msg_receive_time = msg_receive_time;
	}

	public String getMsg_receive_time() {
		return msg_receive_time;
	}
}
