package sunnyday.common.model;

import java.io.Serializable;

/**
 * 网关错误码
 * 
 * @author 71604057
 * @time 2017年6月10日 上午10:55:25
 */
public class GateErrCode extends BaseBean implements Serializable {

	private static final long serialVersionUID = 6387491092914304848L;
	/** 主键 */
	private int sn;
	/** 网关名称 */
	private String gateway;
	/** 网关返回的错误码 */
	private String err_code;
	/** 错误码详细描 */
	private String err_desc;
	/** 0:通用类型；1:运营商类型 */
	private int err_type;
	/** 备注信息*/
	private String remark;
	/** 登记人员 */
	private String operator;
	/** 更新时间 */
	private String update_time;
	/**是否需要记录手机号:0,需要;1,不需要**/
	private String is_record_mobile;
	
	
	@Override
	public String toString() {
		return "GateErrCode [sn=" + sn + ", gateway=" + gateway + ", err_code=" + err_code + ", err_desc=" + err_desc
				+ ", err_type=" + err_type + ", remark=" + remark + ", operator=" + operator + ", update_time="
				+ update_time + ", is_record_mobile=" + is_record_mobile + "]";
	}
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	public String getErr_code() {
		return err_code;
	}
	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}
	public String getErr_desc() {
		return err_desc;
	}
	public void setErr_desc(String err_desc) {
		this.err_desc = err_desc;
	}
	public int getErr_type() {
		return err_type;
	}
	public void setErr_type(int err_type) {
		this.err_type = err_type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getIs_record_mobile() {
		return is_record_mobile;
	}
	public void setIs_record_mobile(String is_record_mobile) {
		this.is_record_mobile = is_record_mobile;
	}
	
 
}
