package sunnyday.common.model;

import java.io.Serializable;

public class ErrCode extends BaseBean implements Serializable{
	 
	private static final long serialVersionUID = -6934101267999034869L;
	private int sn;
	private String code_name;
	private int response;
	private int status;
	private int stat;
	private int err;
	private String report_fail_desc;
	private String fail_desc;
	private String comment;
	public enum codeName {
		 submitErr           ,
		 submitNoResponse    ,
		 basicKeyWordReject  ,
		 userKeyWordReject,
		 globalKeyWordReject,
		 accountKeyWordReject,
		 illegalReject		 ,
		 specialKeyWordReject,
		 manualKeyWordReject ,
		 manualCacheReject   ,
		 massCacheReject     ,
		 longSpliceFail      ,
		 ClientRepeatFilter  ,
		 RepeatFilterContent,
		 PlatRepeatFilter    ,
		 ChildCountReject    ,
		 blackMobileReject   ,
		 whiteFilterReject   ,
		 manualReject        ,
		 massManualReject    ,
		 whiteTemplateReject,
		 MobileScopeRepeatReject,
		 sendModeNoMatchReject,
		 disturbSmsReject,
		 massReject 		 ;
	}
	
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public int getResponse() {
		return response;
	}
	public void setResponse(int response) {
		this.response = response;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getStat() {
		return stat;
	}
	public void setStat(int stat) {
		this.stat = stat;
	}
	public int getErr() {
		return err;
	}
	public void setErr(int err) {
		this.err = err;
	}
	public String getFail_desc() {
		return fail_desc;
	}
	public void setFail_desc(String fail_desc) {
		this.fail_desc = fail_desc;
	}
	
	public String getReport_fail_desc() {
		return report_fail_desc;
	}
	public void setReport_fail_desc(String report_fail_desc) {
		this.report_fail_desc = report_fail_desc;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCode_name() {
		return code_name;
	}
	public void setCode_name(String code_name) {
		this.code_name = code_name;
	}
	
	
}
