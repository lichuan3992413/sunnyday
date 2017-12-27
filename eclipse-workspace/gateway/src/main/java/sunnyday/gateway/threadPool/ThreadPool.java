package sunnyday.gateway.threadPool;

import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 线程池，继承ThreadGroup 
 * @author 1111182(Doc.)
 *
 */
public class ThreadPool extends ThreadGroup {
	private Log log = LogFactory.getLog(this.getClass());
	private boolean isAlive;
	private LinkedList<Task> taskQueue;
	private int threadID;
	private static int threadPoolID;
	
	public ThreadPool(String name, int threadNum) {
		super("ThreadPool - " + threadPoolID++ + " - " + name);
		
		super.setDaemon(true);
		this.isAlive = true;
		this.taskQueue = new LinkedList<Task>();
		for(int i = 0; i < threadNum; i++){
			new PooledThread().start();
		}
	}

	/**
	 * 向线程池中添加任务
	 */
	public synchronized void performTask(Task task){
		if(!this.isAlive){
			throw new IllegalStateException();
		}
		if(task != null){
			this.taskQueue.add(task);
			notify();
		}
	}
	
	/**
	 * 获取待执行的任务
	 * @throws InterruptedException 
	 */
	protected synchronized Task getTask() throws InterruptedException{
		while(this.taskQueue.size() == 0){
			if(!this.isAlive){
				return null;
			}
			wait();
		}
		return (Task) this.taskQueue.removeFirst();
	}
	
	/**
	 * 强行关闭线程池，所有线程停止执行任务
	 */
	public synchronized void close(){
		if(isAlive){
			this.isAlive = false;
			this.taskQueue.clear();
			this.interrupt();
		}
	}
	
	/**
	 * 关闭线程池，无法添加新任务，等待已有任务执行完
	 */
	public void join(){
		//通知其他等待线程该线程池的关闭消息
		synchronized(this){
			isAlive = false;
			notifyAll();
		}
		
		Thread[] activeThreads = new Thread[this.activeCount()];
		
		int count = this.enumerate(activeThreads);
		
		for(int i = 0; i < count; i++){
			try{
				activeThreads[i].join();
			}catch(Exception ie){
				if(log.isErrorEnabled()){
					log.error("",ie);
				}
			}
		}
	}
	
	private class PooledThread extends Thread{
	
		public PooledThread(){
			super(ThreadPool.this, "PooledThread - " + threadID++);
		}
		
		public void run(){
			while(!interrupted()){
				Task task = null;
				try {
					task = getTask();
				} catch (Exception e) {
					log.error("",e);
				}
				
				if(task != null){
					task.perform();
				}else{
					return;
				}
			}
		}
	}
}
