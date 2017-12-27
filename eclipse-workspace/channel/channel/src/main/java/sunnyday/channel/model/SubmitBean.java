package sunnyday.channel.model;

import java.io.Serializable;
import java.util.Arrays;

import com.hskj.adapter.Chargable;

import sunnyday.tools.util.DateUtil;
	
public class SubmitBean implements Serializable, Chargable{
	//private Log log = LogFactory.getLog(this.getClass());
	
	private static final long serialVersionUID = 7506256005612974639L;
	private int userSn;
	private String user_id;
	private String[] mobiles;
	private String content;
	private String service_code;
	private String ext_code ="";
	private String user_ext_code="";
	private String sp_number;//service对应的td的sp_number + service对应的ext_code + 客户提交的user_ext_code
	private String msg_id;//给客户匹配状态报告使用的msg_id
	private String td_code;//service对应的td的td_code
	private int pkTotal;//拆分总条数
	private int pkNumber;//第几条
	private String msgid;
	private int subMsgID;//长短信唯一标识
	private int msg_format;//0_英文 8、15_中文 
	private int charge_count;//接收端校验赋值，根据通道表计费规则
	private double price;//业务表单价
	private int resp;
	private String submit_type;//接口类型(CMPP,SGIP,SMGP)
	private String ori_mobile;//原手机号
	private String country_cn;//国家中文名称
	private String send_sp_number;
	private int yw_code;
	private String signature;//签名信息
	private String operator_signature;//运营商签名信息
	private int with_gate_sign;
	private String sign_ext_code;
	private int check_length;//校验长度
	private String src_number;//原始接入号
	

	

	@Override
	public String toString() {
		return "SubmitBean [ userSn=" + userSn + ", user_id="
				+ user_id + ", mobiles=" + Arrays.toString(mobiles)
				+ ", content=" + content + ", service_code=" + service_code
				+ ", ext_code=" + ext_code + ", user_ext_code=" + user_ext_code
				+ ", sp_number=" + sp_number + ", msg_id=" + msg_id
				+ ", td_code=" + td_code + ", pkTotal=" + pkTotal
				+ ", pkNumber=" + pkNumber + ", msgid=" + msgid + ", subMsgID="
				+ subMsgID + ", msg_format=" + msg_format + ", charge_count="
				+ charge_count + ", price=" + price + ", resp=" + resp
				+ ", submit_type=" + submit_type + ", ori_mobile=" + ori_mobile
				+ ", country_cn=" + country_cn + ", send_sp_number="
				+ send_sp_number + ", yw_code=" + yw_code + ", signature="
				+ signature + ", operator_signature=" + operator_signature
				+ ", with_gate_sign=" + with_gate_sign + ", sign_ext_code="
				+ sign_ext_code + ", check_length=" + check_length
				+ ", src_number=" + src_number + ", msg_receive_time="
				+ msg_receive_time + "]";
	}

	private String msg_receive_time = DateUtil.currentTimeToMs();
	

	public double getCharge_sum() {
		return charge_count * price;
	}
	
	public String getSrc_number() {
		return src_number;
	}
	
	 

	public void setSrc_number(String src_number) {
		this.src_number = src_number;
	}
	public SubmitBean(int userSn, String[] mobiles, String content,
			String spNumber, String msgid , String td_code , int pkTotal , int pkNumber , int subMsgID , int chargeCount, double price, int msgFormat ,String service_code,String id) {
		this.setUserSn(userSn);
		this.mobiles = mobiles;
		this.content = content;
		this.sp_number = spNumber;
		this.setMsg_id(msgid);
		this.setPkTotal(pkTotal);
		this.setPkNumber(pkNumber);
		this.setSubMsgID(subMsgID);
		this.charge_count = chargeCount;
		this.price = price;
		this.msg_format = msgFormat;
		this.service_code = service_code;
		this.user_id = id;
//		this.ext_code = ext_code;
//		this.user_ext_code = user_ext_code;
	}

	public SubmitBean() {
	}
	public String getTd_code() {
		return td_code;
	}

	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}

	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getOri_mobile() {
		return ori_mobile;
	}

	public void setOri_mobile(String ori_mobile) {
		this.ori_mobile = ori_mobile;
	}

	public String getExt_code() {
		return ext_code;
	}

	public void setExt_code(String ext_code) {
		this.ext_code = ext_code;
	}

	public String getCountry_cn() {
		return country_cn;
	}

	public void setCountry_cn(String country_cn) {
		this.country_cn = country_cn;
	}

	public String getSend_sp_number() {
		return send_sp_number;
	}

	public void setSend_sp_number(String send_sp_number) {
		this.send_sp_number = send_sp_number;
	}

	public int getYw_code() {
		return yw_code;
	}

	public void setYw_code(int yw_code) {
		this.yw_code = yw_code;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getOperator_signature() {
		return operator_signature;
	}

	public void setOperator_signature(String operator_signature) {
		this.operator_signature = operator_signature;
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

	public int getCheck_length() {
		return check_length;
	}

	public void setCheck_length(int check_length) {
		this.check_length = check_length;
	}

	 
	public String[] getMobiles() {
		return mobiles;
	}
	public String getMobilesString(){
		StringBuffer sb = new StringBuffer();
		if(mobiles!=null&&mobiles.length>0){
			for(String m: mobiles){
				sb.append(m).append(",");
			}
		}
		return sb.toString();
	}
	public void setMobiles(String[] mobiles) {
		this.mobiles = mobiles;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSp_number() {
		return sp_number;
	}
	public void setSp_number(String spNumber) {
		sp_number = spNumber;
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
	public void setMsg_receive_time(String msg_receive_time) {
		this.msg_receive_time = msg_receive_time;
	}

	public String getMsg_receive_time() {
		return msg_receive_time;
	}

	public int getResp() {
		return resp;
	}

	public void setResp(int resp) {
		this.resp = resp;
	}

	public String getSubmit_type() {
		return submit_type;
	}

	public void setSubmit_type(String submit_type) {
		this.submit_type = submit_type;
	}

	 

	public int getUserSn() {
		return userSn;
	}

	public void setUserSn(int userSn) {
		this.userSn = userSn;
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

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public int getSubMsgID() {
		return subMsgID;
	}

	public void setSubMsgID(int subMsgID) {
		this.subMsgID = subMsgID;
	}
}
