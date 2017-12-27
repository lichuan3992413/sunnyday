package sunnyday.common.model;

	public class ThreadControllerForm {
	
	private int sn;
	private String server_ip;
	private String thread_name;
	private int thread_id;
	private int action;
	private int status;
	private String thread_param;
	private int thread_type; 
	private String group_id;
	private String app_name;
	
	public String getCacheKey(){
		StringBuilder sb = new StringBuilder();
		sb.append("threads").append(":").append(server_ip).append(":").append(thread_name).append(":").append(sn);
		return sb.toString();
	}
	
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getThread_name() {
		return thread_name;
	}
	public void setThread_name(String thread_name) {
		this.thread_name = thread_name;
	}
	public int getThread_id() {
		return thread_id;
	}
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getThread_param() {
		return thread_param;
	}
	public void setThread_param(String thread_param) {
		this.thread_param = thread_param;
	}
	public int getThread_type() {
		return thread_type;
	}
	public void setThread_type(int thread_type) {
		this.thread_type = thread_type;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public int getAction() {
		return action;
	}
	public void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
	}
	public String getServer_ip() {
		return server_ip;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public int getSn() {
		return sn;
	}
	@Override
	public String toString() {
		return "ThreadControllerForm [sn=" + sn + ", server_ip=" + server_ip
				+ ", thread_name=" + thread_name + ", thread_id=" + thread_id
				+ ", action=" + action + ", status=" + status
				+ ", thread_param=" + thread_param + ", thread_type="
				+ thread_type + ", group_id=" + group_id + "]";
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getApp_name() {
		return app_name;
	}

}
