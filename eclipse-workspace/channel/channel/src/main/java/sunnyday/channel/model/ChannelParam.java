package sunnyday.channel.model;

	public class ChannelParam {
	
	private int id;
	private String thread_name;
	private int thread_id;
	private int status;
	private String thread_param;
	private int thread_type; 
	private int channel_id;
	private String app_name;
	
	public String getCacheKey(){
		StringBuilder sb = new StringBuilder();
		sb.append("threads").append(":").append(":").append(thread_name).append(":");
		return sb.toString();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
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
	
	@Override
	public String toString() {
		return "ThreadControllerForm [ thread_name=" + thread_name + ", thread_id=" + thread_id
				+  ", status=" + status
				+ ", thread_param=" + thread_param + ", thread_type="
				+ thread_type + ", channel_id=" + channel_id + "]";
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getApp_name() {
		return app_name;
	}

}
