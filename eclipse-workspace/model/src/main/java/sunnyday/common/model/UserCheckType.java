package sunnyday.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

public class UserCheckType implements Serializable{
	
	private static final Logger log = CommonLogFactory.getCommonLog("infoLog");
	 
	private static final long serialVersionUID = -8262597312066814329L;
	private int user_sn;
	private String user_id;
	private String service_code;
	private int type;
	private String mode;
	private String fast_mode;
	@Override
	public String toString() {
		return "UserCheckType [user_sn=" + user_sn + ", user_id=" + user_id
		+ ", service_code=" + service_code + ", type=" + type + ", mode="
		+ mode + ", fast_mode=" + fast_mode + "]";
	}
	public List<Integer> orderMode(List<Integer> disorderModes, String sampleOrder){
		if(sampleOrder == null){
			sampleOrder = "-1,0,1,2,6,3,4,5";
		}
		List<Integer> result = new ArrayList<Integer>(); 
		String[] modes = sampleOrder.split(",");
		for(String mode : modes){
			Integer iMode = Integer.parseInt(mode);
			if(disorderModes != null && disorderModes.contains(iMode)){
				result.add(iMode);
			}
		}
		return result;
	}
	private List<Integer> getCheckModes(String mode){
		List<Integer> result = new ArrayList<Integer>(5);
		if(mode != null){
			String[] arr = mode.split(",");
			if(arr != null){
				for(String each:arr){
					try {
						if(!each.equals("")){
							Integer i = Integer.valueOf(each);
							result.add(i);
						}
					} catch (Exception e) {
						log.error("", e);
						continue;
					}
				}
			}
		}
		return result;
	}
	
	public List<Integer> getNormalCheckModes(){
		return this.getCheckModes(mode);
	}
	
	public List<Integer> getFastCheckModes(){
		return this.getCheckModes(fast_mode);
	}
	public int getUser_sn() {
		return user_sn;
	}
	public void setUser_sn(int user_sn) {
		this.user_sn = user_sn;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getService_code() {
		return service_code;
	}
	public void setService_code(String service_code) {
		this.service_code = service_code;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getFast_mode() {
		return fast_mode;
	}
	public void setFast_mode(String fast_mode) {
		this.fast_mode = fast_mode;
	}
}