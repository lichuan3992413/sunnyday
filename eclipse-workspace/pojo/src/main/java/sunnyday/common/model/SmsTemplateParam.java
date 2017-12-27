package sunnyday.common.model;

import java.io.Serializable;

/**
 * 模板变量实体类
 * @author weijun
 *
 */
public class SmsTemplateParam implements Serializable{
	
	private static final long serialVersionUID = 7414348655418518510L;
	private int sn;
	private String templateId;
	private String paramName;
	private String paramDesc;
	private int maxLength;
	
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamDesc() {
		return paramDesc;
	}
	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	@Override
	public String toString() {
		return "TemplateParam [templateId="+templateId+"; paramName=" + paramName + ", paramDesc="
				+ paramDesc + ", maxLength=" + maxLength + "]";
	}


}
