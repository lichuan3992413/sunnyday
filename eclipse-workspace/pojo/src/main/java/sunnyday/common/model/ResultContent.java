package sunnyday.common.model;

public class ResultContent {

	public String toString() {
		return "ResultContent [content_id=" + content_id + ", type=" + type
				+ ", content=" + content + ", smsTemplateInfo="
				+ smsTemplateInfo + "]";
	}

	/**
	 * 0-成功 1-失败 3-其他
	 */
	private int content_id;
	
	/**
	 * 0-固定内容 1-模板内容
	 */
	private int type;
	
	/**
	 * 固定内容
	 */
	private String content;
	
	/**
	 * 模板对象
	 */
	private SmsTemplateInfo smsTemplateInfo;

	public int getContent_id() {
		return content_id;
	}

	public void setContent_id(int content_id) {
		this.content_id = content_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public SmsTemplateInfo getSmsTemplateInfo() {
		return smsTemplateInfo;
	}

	public void setSmsTemplateInfo(SmsTemplateInfo smsTemplateInfo) {
		this.smsTemplateInfo = smsTemplateInfo;
	}
	
}
