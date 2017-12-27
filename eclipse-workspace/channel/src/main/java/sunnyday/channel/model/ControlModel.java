package sunnyday.channel.model;

public class ControlModel {
	
	@Override
	public String toString() {
		return "ControlModel [key=" + key + ", isStart=" + isStart
				+ ", isStop=" + isStop + "]";
	}
	private String key;
	private boolean isStart;
	private boolean isStop;
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	public boolean isStop() {
		return isStop;
	}
	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}
	

}
