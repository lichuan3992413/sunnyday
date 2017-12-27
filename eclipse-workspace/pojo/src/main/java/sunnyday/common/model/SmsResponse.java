package sunnyday.common.model;

import java.io.Serializable;

/**
 * 短信下发响应详情实体
 * @author weijun
 *
 */
public class SmsResponse implements Serializable{

	
	private static final long serialVersionUID = -8127761642592807677L;
	
	private String mobile;
	private String msgId;
	private String msg;
	private String code;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public String toString() {
		return "SmsStatus [mobile=" + mobile + ", msgId=" + msgId
				+ ", msg=" + msg + ", code=" + code + "]";
	}


}
