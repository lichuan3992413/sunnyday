package sunnyday.common.model;

/**
 * 定时短信批次
 * @author 1307365
 *
 */
public class BatchModel {
	private String batch_number ;
	private String send_time ;
	private int success_count ;
	private int fail_count ;
	private int send_status ;
	private int batch_count ;
	
	
	
	@Override
	public String toString() {
		return "BatchModel [batch_number=" + batch_number + ", send_time="
				+ send_time + ", success_count=" + success_count
				+ ", fail_count=" + fail_count + ", send_status=" + send_status
				+ ", batch_count=" + batch_count + "]";
	}
	public String getBatch_number() {
		return batch_number;
	}
	public void setBatch_number(String batch_number) {
		this.batch_number = batch_number;
	}
	public int getSuccess_count() {
		return success_count;
	}
	public void setSuccess_count(int success_count) {
		this.success_count = success_count;
	}
	public int getFail_count() {
		return fail_count;
	}
	public void setFail_count(int fail_count) {
		this.fail_count = fail_count;
	}
	public int getSend_status() {
		return send_status;
	}
	public void setSend_status(int send_status) {
		this.send_status = send_status;
	}
	public String getSend_time() {
		return send_time;
	}
	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}
	public int getBatch_count() {
		return batch_count;
	}
	public void setBatch_count(int batch_count) {
		this.batch_count = batch_count;
	}

}
