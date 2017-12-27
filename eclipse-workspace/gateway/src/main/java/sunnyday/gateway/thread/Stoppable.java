package sunnyday.gateway.thread;

public interface Stoppable {
	/**
	 * 在线程或服务关闭时候需要执行的操作
	 */
	public boolean doStop();

}
