package sunnyday.gateway.thread;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import sunnyday.gateway.util.LogUtil;

public abstract class ServerNioThread extends Thread implements IControlService {

	private boolean running = false;
	protected Logger log = LogUtil.getCmpp_info();

	public void run() {
		if (log.isInfoEnabled()) {
			log.info("Cmpp20Server start");
		}

		Selector selector = null;
		ExecutorService threadPool = null;
		ServerSocketChannel ssc = null;
		try {
			selector = Selector.open();
			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			ssc.socket().bind(new InetSocketAddress(getPort()));

			/*
			 * 线程池数量 = cpu核心数 *可用核心比率*(1 + (任务阻塞时间/任务计算时间)) 例如: 12 * 1 * (1 +
			 * (0.5ms / 1ms)) = 18
			 * 由于我们的任务已经是在可读和可写的时候创建的,所以任务阻塞时间认为是0.默认使用所有cpu,所以线程池数量计算如下:
			 */
			int poolSize = Runtime.getRuntime().availableProcessors();
			ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1000);
			threadPool = new ThreadPoolExecutor(poolSize, poolSize, 1000, TimeUnit.SECONDS, queue);

			Iterator<SelectionKey> keys = null;
			SelectionKey key = null;

			while (running) {
				try {
					if (selector.select(1) > 0) {

						keys = selector.selectedKeys().iterator();

						while (keys.hasNext()) {
							key = keys.next();
							keys.remove();

							if (key.isAcceptable()) {

								ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
								SocketChannel socketChannel = serverSocketChannel.accept();
								socketChannel.configureBlocking(false);
								socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);

							} else {
								if (key.attachment() == null) {
									key.attach(getCommonBean());
								}

								try {
									if (queue.remainingCapacity() > 1) {
										threadPool.submit(this.getNewSubTask(key));
									} else {
										sleep(20);
									}

								} catch (Exception e) {
									try {
										sleep(1);
									} catch (Exception ee) {
										log.error("", e);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					if (log.isDebugEnabled()) {
						log.debug("", e);
					}
				}
			}

		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				if (ssc != null) {
					ssc.close();
				}
			} catch (Exception e) {
				if (log.isDebugEnabled())
					log.debug("", e);
			}
			try {
				if(selector!=null){
					selector.close();
				}
			} catch (Exception e) {
				if (log.isDebugEnabled())
					log.debug("", e);
			}
			try {
				if(threadPool!=null){
					threadPool.shutdown();
				}
			} catch (Exception e) {
				if (log.isDebugEnabled())
					log.debug("", e);
			}
		}
		log.info("Cmpp20Server stop");
	}

	protected abstract Object getCommonBean();

	protected abstract int getPort();

	protected abstract Callable<Integer> getNewSubTask(SelectionKey key);

	public void doShutDown() {
		running = false;
		interrupt();
	}

	public void doStart() {
		start();
	}

	public void doInit(String param, String threadID) {
		running = true;
	}

	public boolean checkIsStarted() {
		return running;
	}
}
