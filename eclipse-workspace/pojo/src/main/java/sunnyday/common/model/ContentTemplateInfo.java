package sunnyday.common.model;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * content_template_info表对应 JavaBean实体类
 * 
 * @author 71604057
 * @time 2017年7月24日 上午9:43:39
 */
public class ContentTemplateInfo implements Serializable{

	private static final long serialVersionUID = -2427276162754302771L;
	
	/** 主键 */
	private int sn;
	/** 模板ID */
	private String template_id;
	/** 模板名称 */
	private String template_name;
	/** 模板内容 */
	private String template_content;
	/** 模板类型 */
	private int template_type;
	/** 模板归属ID */
	private String template_attr_id;
	/** 创建人 */
	private String creator;
	/** 插入时间 */
	private String insert_time;
	/** 更新时间 */
	private String update_time;
	/** 备注 */
	private String comment;
	
	/** 模板内容正则编译对象  */
	private Pattern pattern;
	
	public ContentTemplateInfo() {
		super();
	}
	
	public ContentTemplateInfo(String template_id) {
		super();
		this.template_id = template_id;
	}

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getTemplate_name() {
		return template_name;
	}

	public void setTemplate_name(String template_name) {
		this.template_name = template_name;
	}

	public String getTemplate_content() {
		return template_content;
	}

	public void setTemplate_content(String template_content) {
		this.template_content = template_content;
	}

	public int getTemplate_type() {
		return template_type;
	}

	public void setTemplate_type(int template_type) {
		this.template_type = template_type;
	}

	public String getTemplate_attr_id() {
		return template_attr_id;
	}

	public void setTemplate_attr_id(String template_attr_id) {
		this.template_attr_id = template_attr_id;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getInsert_time() {
		return insert_time;
	}

	public void setInsert_time(String insert_time) {
		this.insert_time = insert_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void compile(){
		if(StringUtils.isNotBlank(template_content)){
			pattern = Pattern.compile(template_content);
		}
	}
	
	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public String toString() {
		return "ContentTemplateInfo [sn=" + sn + ", template_id=" + template_id + ", template_name=" + template_name + ", template_content=" + template_content + ", template_type="
				+ template_type + ", template_attr_id=" + template_attr_id + ", creator=" + creator + ", insert_time=" + insert_time + ", update_time=" + update_time + ", comment="
				+ comment + "]";
	}

}
