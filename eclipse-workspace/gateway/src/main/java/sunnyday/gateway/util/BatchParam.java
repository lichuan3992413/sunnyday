package com.hskj.form;

import java.io.Serializable;
import java.util.Map;

public class BatchParam implements Serializable{
	private static final long serialVersionUID = 6653581646911408982L;
	
	private String mobile;
	private Map<String, String> smsParam;
	
	
	
	@Override
	public String toString() {
		return "BatchParam [mobile=" + mobile + ", smsParam=" + smsParam + "]";
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Map<String, String> getSmsParam() {
		return smsParam;
	}
	public void setSmsParam(Map<String, String> smsParam) {
		this.smsParam = smsParam;
	}
}
