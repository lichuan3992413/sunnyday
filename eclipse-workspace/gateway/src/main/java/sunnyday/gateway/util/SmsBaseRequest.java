package sunnyday.gateway.util;

import java.io.Serializable;

public class SmsBaseRequest implements Serializable{
	
	private static final long serialVersionUID = 496458138037503114L;
	protected String userId;
	protected String transactionId;
	protected Long timestamp;
	protected String sign;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	 
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "SmsBaseRequest [userId=" + userId + ", transactionId=" + transactionId + ", timestamp=" + timestamp
				+ ", sign=" + sign + "]";
	}
	
	
}
