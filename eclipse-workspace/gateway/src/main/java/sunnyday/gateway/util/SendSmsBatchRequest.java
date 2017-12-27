package sunnyday.gateway.util;

import java.io.Serializable;
import java.util.List;

public class SendSmsBatchRequest extends SmsBaseRequest implements Serializable {
	
	private static final long serialVersionUID = -7505216027807155461L;
	private String templateId;
	private List<BatchParam> param ;
	private String serviceCode;
	private String ext;
	
	public String toString() {
		return "SendSmsRequest [templateId=" + templateId + ", smsParam=" + param 
				+ ", serviceCode=" + serviceCode + ", ext=" + ext + ", userId=" + userId + ", transactionId="
				+ transactionId + ", timestamp=" + timestamp + ", sign=" + sign + "]";
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	 
	 
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public List<BatchParam> getParam() {
		return param;
	}
	public void setParam(List<BatchParam> param) {
		this.param = param;
	}
	 
}
