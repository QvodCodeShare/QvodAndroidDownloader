package com.qvod.lib.downloader.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * [修改后的线程池]
 * 
 * 可以在任何workQueue情况下都支持 maximumPoolSize和keepAliveTime 的有效性
 * 这样可以保证同时work的线程数有maximumPoolSize个，在keepAliveTime时间后如果没有更多Task则关闭多余的线程(关闭其只剩下corePoolSize个线程)
 * 
 * 
 * @author 李理
 * @date 2015年8月24日
 */
public class ThreadPoolAlterExecutor extends ThreadPoolExecutor {

	public ThreadPoolAlterExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public ThreadPoolAlterExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

	public ThreadPoolAlterExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	public ThreadPoolAlterExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	/**
	 * [创建一个可伸缩线程的线程池]<BR>
	 * 当线程池中的 空线程数>corePoolSize 且等待 keepAliveTime毫秒后任然没执行新任务，则会从回收释放该线程
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @return
	 */
	public static ThreadPoolExecutor createFlexibleExecutor(
			int corePoolSize, int maximumPoolSize, long keepAliveTime) {
		ThreadPoolAlterExecutor executor = new ThreadPoolAlterExecutor(
				corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, 
				new LinkedBlockingQueue<Runnable>());
		return executor;
	}

	@Override
	public void execute(Runnable command) {
		if (command == null)
			throw new NullPointerException();

		int c = ctl.get();
		if (workerCountOf(c) < corePoolSize) {
			if (addWorker(command, true))
				return;
			c = ctl.get();
		}

		if (workerCountOf(c) < maximumPoolSize) {
			if (!addWorker(command, false)) {
				reject(command);
			}
			return;
		}

		if (isRunning(c) && workQueue.offer(command)) {
			int recheck = ctl.get();
			if (!isRunning(recheck) && remove(command))
				reject(command);
			else if (workerCountOf(recheck) == 0)
				addWorker(null, false);
		} else {
			reject(command);
		}
	}

}
