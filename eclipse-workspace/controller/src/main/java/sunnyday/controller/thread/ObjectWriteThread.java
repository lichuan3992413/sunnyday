package sunnyday.controller.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

/**
 * 该抽象类按照<i>模板方法模式</i>定义了系统持久化对象的标准化流程，可以把待持久化的对象按照分组条件最多每次1000个持久化到硬盘。
 * 标准化流程描述如下：
 * <p>
 * 每隔一个固定时间执行下面一系列任务，这个时间间隔即在输出对象为null时的休眠时间，子类可以通到在构造器中调用
 * <code>setTimeInterval(long timeInterval)</code>方法来设置这个时间。<br>
 * 每100ms会检查一次内存，如果有超过500ms还没有输出的对象，不管数量多少，保证输出一次<br>
 * 通过<code>getOneMessage()</code>获得一个要持久化的对象<br>
 * 如果可以获取到对象，就通过<code>getGroupKey(Object obj)</code>获取这个对象输出地分组条件，将对象对应分组中<br>
 * 检验该对象对应的分组列表是否满1000条，满1000条的调用一次输出方法<code>doWriteListAndClear</code><br>
 * <p>
 * 子类需要实现实现一下方法：<br>
 * <code>getOneMessage()</code>方法来定义如何获取输出对象<br>
 * <code>getGroupKey(Object obj)</code>方法来定义对象输出的分组条件<br>
 * <code>writeList(List&lt;Object&gt; list)</code>方法来定义对象持久化的具体方法<br>
 * <p>
 * 
 * @author 1111182
 *
 */
public abstract class ObjectWriteThread extends Thread {
	/**
	 * 提供给子类输出日志，如果没有特殊要求，可以直接使用
	 */
	protected Logger log = CommonLogFactory.getCommonLog(ObjectWriteThread.class);
	/**
	 * 标识是否可以运行
	 */
	protected boolean isRunnable = false;
	/**
	 * 标识是否正在运行
	 */
	protected boolean isRunning = false;
	/**
	 * 无输出对象的时候，线程休眠时间，单位ms
	 */
	protected long timeInterval = 100;
	/**
	 * 分组对象缓存map，分组条件为key
	 */
	protected Map<String, List<Object>> smsMap = new HashMap<String, List<Object>>();
	/**
	 * 分组对象的输出时间戳记录map，分组条件为key
	 */
	protected Map<String, Long> writeTimeMap = new HashMap<String, Long>();
	/**
	 * 上次检查超时为输出对象的时间点
	 */
	protected long lastCheckTime = System.currentTimeMillis();
	/**
	 * 对象最长缓存时间，单位毫秒，如果超过这个时间，则会自动通过持久化方法输出
	 */
	protected int MaxWaitTimeMillis = 500;

	public ObjectWriteThread() {
		this.isRunnable = true;
	}

	/**
	 * 定义了标准输出流程
	 */
	public void run() {
		initConfigParam();
		isRunning = true;
		while (isRunnable) {
			try {
				// 100ms 检测一次是否有间隔 MaxWaitTimeMillis 还没写出去的对象
				if ((System.currentTimeMillis() - lastCheckTime) > 100) {
					wirteListIfTimeOut(MaxWaitTimeMillis);
					lastCheckTime = System.currentTimeMillis();
				}
				Object sms = this.getOneMessage();

				if (sms != null) {
					List<Object> smsList = getSubmitList(sms);

					smsList.add(sms);
					if (smsList.size() >= 1000) {
						doWriteListAndClear(smsList);
					}
				} else {
					writeMap();
					Thread.sleep(timeInterval);
				}
			} catch (InterruptedException e) {
				log.error("", e);
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				log.error("", e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					log.error("", e);
				}
			}
		}
		isRunning = false;
	}

	private void doWriteListAndClear(List<Object> smsList) {
		if (smsList != null && smsList.size() > 0) {
			writeList(smsList);
			Object tmp = smsList.get(0);
			String key = this.getGroupKey(tmp);
			updateWriteTimeInMap(key);
			smsList.clear();
		}
	}

	public boolean doStop() {
		this.isRunnable = false;
		this.interrupt();
		while (isRunning) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				log.error("", e);
				Thread.currentThread().interrupt();
			}
		}
		wirteListIfTimeOut(-1);
		return true;
	}

	protected void initConfigParam() {
	}

	protected abstract Object getOneMessage();

	protected abstract String getGroupKey(Object smsObj);

	protected abstract void writeList(List<Object> list);

	private List<Object> getSubmitList(Object sms) {
		List<Object> result = null;
		String key = this.getGroupKey(sms);

		if (!smsMap.containsKey(key)) {
			List<Object> smsList = new ArrayList<Object>();
			smsMap.put(key, smsList);
		}
		result = smsMap.get(key);
		return result;
	}

	private void wirteListIfTimeOut(int timeOut) {
		Set<String> keys = writeTimeMap.keySet();
		for (String key : keys) {
			int timeDiff = (int) (System.currentTimeMillis() - writeTimeMap.get(key));
			if (timeDiff > timeOut) {
				this.doWriteListAndClear(smsMap.get(key));
			}
		}
	}

	private void writeMap() {
		Set<String> keys = smsMap.keySet();
		for (String key : keys) {
			List<Object> eachList = smsMap.get(key);
			this.doWriteListAndClear(eachList);
		}
	}

	private void updateWriteTimeInMap(String key) {
		writeTimeMap.put(key, System.currentTimeMillis());
	}

	/**
	 * 在文件路径不存在的时候创建目录，不包括创建文件
	 * 
	 * @param f
	 */
	protected void buildPathIfNeeded(File f) {
		if (f != null) {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
		}
	}

	protected void setTimeInterval(long timeInterval) {
		this.timeInterval = timeInterval;
	}
}
