package sunnyday.common.model;

import java.io.Serializable;

/**
 * 白名单模板
 * 
 * @author 1307365
 *
 */
public class WhiteListTemplate  extends BaseBean implements Serializable{
	private static final long serialVersionUID = 5497183766039882696L;
	private int sn ;
	private String template_name ;// '模板名称',
	private String template_content;// '模板内容',
	private int template_type;// '模板类型 0：固定内容 1：模板内容',
	private int level;// '模板级别 0：公共级 1：账户级',
	private String attach_user_id;// '归属账户',
	private String operator; // ; '操作人',
	private String insert_time;// '插入时间',
	private String update_time;// '修改时间',
	
	
	@Override
	public String toString() {
		return "WhiteListTemplate [sn=" + sn + ", template_name=" + template_name + ", template_content="
				+ template_content + ", template_type=" + template_type + ", level=" + level + ", attach_user_id="
				+ attach_user_id + ", operator=" + operator + ", insert_time=" + insert_time + ", update_time="
				+ update_time + "]";
	}
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
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
	 
	 
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getAttach_user_id() {
		return attach_user_id;
	}
	public void setAttach_user_id(String attach_user_id) {
		this.attach_user_id = attach_user_id;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
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
	public int getTemplate_type() {
		return template_type;
	}
	public void setTemplate_type(int template_type) {
		this.template_type = template_type;
	}

}
