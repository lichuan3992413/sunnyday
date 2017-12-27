package sunnyday.common.model;

/**
 * 审核短信信息表check_msg_batch javaBean
 * 
 * @author 71604057
 * @time 2017年7月19日 上午9:20:17
 */
public class CheckMsgBatch {

	/** 主键 */
	private int sn;
	/** 批次号码 */
	private String batch_number;
	/** 业务账户 */
	private String user_id;
	/** 发送方式 0:相同内容群发 1:个性化模板群发 2:个性化非模板群发 */
	private int send_type;
	/** 批次短信数量 */
	private int batch_count;
	/** 发送批次显示内容 */
	private String batch_content;
	/** 插入时间 */
	private String insert_time;
	/** 修改时间 */
	private String update_time;
	/** 审核时间 */
	private String check_time;
	/** 定时时间 */
	private String timing_time;
	/** 审核人员 */
	private String check_user;
	/** 审核状态 0：等待审核 1：审核通过 2：审核驳回 */
	private int check_status;
	/** 针对content做32位md5唯一索引 */
	private String md5_index;
	/** 驳回原因**/
	private String remark;

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public String getBatch_number() {
		return batch_number;
	}

	public void setBatch_number(String batch_number) {
		this.batch_number = batch_number;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public int getSend_type() {
		return send_type;
	}

	public void setSend_type(int send_type) {
		this.send_type = send_type;
	}

	public int getBatch_count() {
		return batch_count;
	}

	public void setBatch_count(int batch_count) {
		this.batch_count = batch_count;
	}

	public String getBatch_content() {
		return batch_content;
	}

	public void setBatch_content(String batch_content) {
		this.batch_content = batch_content;
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

	public String getCheck_time() {
		return check_time;
	}

	public void setCheck_time(String check_time) {
		this.check_time = check_time;
	}

	public String getTiming_time() {
		return timing_time;
	}

	public void setTiming_time(String timing_time) {
		this.timing_time = timing_time;
	}

	public String getCheck_user() {
		return check_user;
	}

	public void setCheck_user(String check_user) {
		this.check_user = check_user;
	}

	public int getCheck_status() {
		return check_status;
	}

	public void setCheck_status(int check_status) {
		this.check_status = check_status;
	}

	public String getMd5_index() {
		return md5_index;
	}

	public void setMd5_index(String md5_index) {
		this.md5_index = md5_index;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "CheckMsgBatch [sn=" + sn + ", batch_number=" + batch_number
				+ ", user_id=" + user_id + ", send_type=" + send_type
				+ ", batch_count=" + batch_count + ", batch_content="
				+ batch_content + ", insert_time=" + insert_time
				+ ", update_time=" + update_time + ", check_time=" + check_time
				+ ", timing_time=" + timing_time + ", check_user=" + check_user
				+ ", check_status=" + check_status + ", md5_index=" + md5_index
				+ ", remark=" + remark + "]";
	}

	

}
