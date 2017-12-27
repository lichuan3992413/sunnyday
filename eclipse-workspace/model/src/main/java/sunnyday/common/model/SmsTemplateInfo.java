
package sunnyday.common.model;

import java.io.Serializable;
import java.util.List;
/**
 * 模板实体类
 * @author weijun
 *
 */
public class SmsTemplateInfo implements Serializable{

	
	private static final long serialVersionUID = -5234851995637535292L;
	
	private int sn;
	private String templateId;
	private String templateName;
	private String templateContent;
	private String attachUserId;
	private int type;
	/** 防扰开关 0-关闭 1-开启 */
	private int isAvoidDisturb;
	/** 允许下发时间段，如：08:00-20:00 */
	private String disturbPeridTime;
	/**
	 * 模板对应的扩展代码，若无扩展按照"" 空字符串处理
	 */
	private String template_ext_code ;
	
	/**是否追加退订内容 0-是，1-否**/
	private int is_add_unsubscribe ;
	
	/** 防干扰处理策略，0：到了可发送时间发送出去， 1：拦截， 默认0**/
	private int disturb_deal_strategy;
	private List<SmsTemplateParam> params;
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getTemplateContent() {
		return templateContent;
	}
	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}
	public String getAttachUserId() {
		return attachUserId;
	}
	public void setAttachUserId(String attachUserId) {
		this.attachUserId = attachUserId;
	}
	/**
	 * 级别 0-平台级；1-账户级
	 * @return
	 */
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getIsAvoidDisturb() {
		return isAvoidDisturb;
	}
	public void setIsAvoidDisturb(int isAvoidDisturb) {
		this.isAvoidDisturb = isAvoidDisturb;
	}
	public String getDisturbPeridTime() {
		return disturbPeridTime;
	}
	public void setDisturbPeridTime(String disturbPeridTime) {
		this.disturbPeridTime = disturbPeridTime;
	}
	public String getTemplate_ext_code() {
		return template_ext_code;
	}
	public void setTemplate_ext_code(String template_ext_code) {
		this.template_ext_code = template_ext_code;
	}
	public List<SmsTemplateParam> getParams() {
		return params;
	}
	public void setParams(List<SmsTemplateParam> params) {
		this.params = params;
	}
	
	public int getIs_add_unsubscribe() {
		return is_add_unsubscribe;
	}
	public void setIs_add_unsubscribe(int is_add_unsubscribe) {
		this.is_add_unsubscribe = is_add_unsubscribe;
	}
	public int getDisturb_deal_strategy() {
		return disturb_deal_strategy;
	}
	public void setDisturb_deal_strategy(int disturb_deal_strategy) {
		this.disturb_deal_strategy = disturb_deal_strategy;
	}
	@Override
	public String toString() {
		return "SmsTemplateInfo [sn=" + sn + ", templateId=" + templateId + ", templateName=" + templateName
				+ ", templateContent=" + templateContent + ", attachUserId=" + attachUserId + ", type=" + type
				+ ", isAvoidDisturb=" + isAvoidDisturb + ", disturbPeridTime=" + disturbPeridTime + ", template_ext_code="+template_ext_code+ ", disturb_deal_strategy="+disturb_deal_strategy+", params=" + params + "]";
	}
	
	 


}
