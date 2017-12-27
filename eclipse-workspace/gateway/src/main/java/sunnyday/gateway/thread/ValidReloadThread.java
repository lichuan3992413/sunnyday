package sunnyday.gateway.thread;

import org.springframework.stereotype.Service;

/**
 * 线程表控制加载计费借口
 * @author 1007025
 *
 */
@Service
public class ValidReloadThread  implements IControlService{
	
	private boolean running = false;
	
	public void doInit(String param, String threadID) {
		running = true;
	}
	/**
	 *针对BalanceCenter接口来实例化具体的校验实现类 
	 */
	public void loadJar(){ 
		
	}
	
	
	
	 
	public void doShutDown() {
		running = false;
	}

	public void doStart() {
		loadJar();
		//System.out.println("balanceValidMap: "+DataCenter.balanceValidMap);
	}
	
	public boolean checkIsStarted() {
		return running;
	}

}
