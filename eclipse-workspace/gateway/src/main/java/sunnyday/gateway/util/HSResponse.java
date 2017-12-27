package sunnyday.gateway.util;

import java.util.HashMap;
import java.util.Map;

public class HSResponse {
	@Override
	public String toString() {
		return "HSResponse [code=" + code + ", text=" + text + ", other="
				+ other + "]";
	}

	private String code;
	private String text;
	private Map<String, Object> other = new HashMap<String, Object>() ;

	public HSResponse() {

	}

	public HSResponse(String code, String text) {
		this.code = code;
		this.text = text;
	}

	public HSResponse(String code, String text, Map<String, Object> other) {
		this.code = code;
		this.text = text;
		this.other = other;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Map<String, Object> getOther() {
		return other;
	}

	public void setOther(Map<String, Object> other) {
		this.other = other;
	}
}
