package sunnyday.common.model;

import java.io.Serializable;

public class ServiceInfo  implements Serializable{
	private static final long serialVersionUID = 4852708528551689466L;
	/**业务信息主键，自动递增**/
	private int sn ;
	/**业务代码**/
	private String service_code ;
	/**业务类型 1广告 2聊天 3会员营销 4报价 5混发 6验证码 7通知 **/
	private String service_type ;
	/**备注说明**/
	private String description ;
	/**是否使用模板扩展 0-使用，1-不使用**/
	private int is_template_ext ;
	/**处理时间**/
	private String processing_time ;
	/**操作人**/
	private String operator ;
	/**业务名称 唯一**/
	private String service_name ;
	
	
	
	@Override
	public String toString() {
		return "ServiceInfo [sn=" + sn + ", service_code=" + service_code + ", service_type=" + service_type
				+ ", description=" + description + ", is_template_ext=" + is_template_ext + ", processing_time="
				+ processing_time + ", operator=" + operator + ", service_name=" + service_name + "]";
	}
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getService_code() {
		return service_code;
	}
	public void setService_code(String service_code) {
		this.service_code = service_code;
	}
	public String getService_type() {
		return service_type;
	}
	public void setService_type(String service_type) {
		this.service_type = service_type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getIs_template_ext() {
		return is_template_ext;
	}
	public void setIs_template_ext(int is_template_ext) {
		this.is_template_ext = is_template_ext;
	}
	public String getProcessing_time() {
		return processing_time;
	}
	public void setProcessing_time(String processing_time) {
		this.processing_time = processing_time;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getService_name() {
		return service_name;
	}
	public void setService_name(String service_name) {
		this.service_name = service_name;
	}
  
}
