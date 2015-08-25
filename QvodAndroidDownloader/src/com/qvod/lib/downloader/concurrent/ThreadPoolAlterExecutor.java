package com.qvod.lib.downloader.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
