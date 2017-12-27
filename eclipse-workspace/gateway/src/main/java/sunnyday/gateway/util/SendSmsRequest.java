package sunnyday.gateway.util;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class SendSmsRequest extends SmsBaseRequest implements Serializable {
	
	private static final long serialVersionUID = -7505216027807155461L;
	private String templateId;
	private Set<String> mobiles;
	private Map<String, String> smsParam;
	private String serviceCode;
	private String ext;
	
	
	 
	
	@Override
	public String toString() {
		return "SendSmsRequest [templateId=" + templateId + ", mobiles=" + mobiles + ", smsParam=" + smsParam
				+ ", serviceCode=" + serviceCode + ", ext=" + ext + ", userId=" + userId + ", transactionId="
				+ transactionId + ", timestamp=" + timestamp + ", sign=" + sign + "]";
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public Set<String> getMobiles() {
		return mobiles;
	}
	public void setMobiles(Set<String> mobiles) {
		this.mobiles = mobiles;
	}
	public Map<String, String> getSmsParam() {
		return smsParam;
	}
	public void setSmsParam(Map<String, String> smsParam) {
		this.smsParam = smsParam;
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
	 
}
