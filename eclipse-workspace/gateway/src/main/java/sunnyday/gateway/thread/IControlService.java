package sunnyday.gateway.thread;

public interface IControlService {
	
	public void doShutDown();
	public void doStart();
	public void doInit(String param , String threadID);
	public boolean checkIsStarted();
	
}
