package sunnyday.common.model;

import java.io.Serializable;


/***
 * 上行明细查询记录
 *
 */
public class DeliverMxRecord implements Serializable{
	
	private static final long serialVersionUID = -6122588173094241628L;
	/**主键，自增**/
	private int sn;
	/**账号**/
	private String accno;
	/**手机号码**/
	private String mobile;
	/**最近查询起始位置**/
	private String last_index;
	/**起始时间**/
	private String start_date;
	/**结束时间**/
	private String end_date;
	/**
	 * 插入时间
	 */
	private String  insert_time;
	/**
	 * 更新时间
	 */
	private String  update_time;
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getAccno() {
		return accno;
	}
	public void setAccno(String accno) {
		this.accno = accno;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getLast_index() {
		return last_index;
	}
	public void setLast_index(String last_index) {
		this.last_index = last_index;
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
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	
}
