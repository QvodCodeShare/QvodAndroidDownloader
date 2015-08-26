package com.qvod.lib.downloader.manager;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.qvod.lib.downloader.DownloadTaskInfo;
import com.qvod.lib.downloader.concurrent.LinkedBlockingDeque;
import com.qvod.lib.downloader.concurrent.ThreadPoolExecutor;

/**
 * [描述]
 * @author 李理
 * @date 2015年8月24日
 */
public class DownloadTaskPoollExecutor extends ThreadPoolExecutor {

	private LinkedBlockingDeque<Runnable> mLinkedTaskQueue;
	
	public DownloadTaskPoollExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, 
				new LinkedBlockingDeque<Runnable>(), 
				sThreadFactory);
		mLinkedTaskQueue = (LinkedBlockingDeque<Runnable>)workQueue;
	}

	static ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public java.lang.Thread newThread(Runnable r) {
			return new java.lang.Thread(r, "Executor" + mCount.getAndIncrement());
		}
	};
	
	@Override
	public void execute(Runnable command) {
		execute(command, false);
	}
	
	public void execute(Runnable command, boolean isRunTaskTop) {
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
		
		if (isRunning(c)) {
			boolean isSuc = isRunTaskTop ? 
					mLinkedTaskQueue.offerFirst(command) 
					: mLinkedTaskQueue.offerLast(command);
			if (isSuc) {
				int recheck = ctl.get();
				if (!isRunning(recheck) && remove(command))
					reject(command);
				else if (workerCountOf(recheck) == 0)
					addWorker(null, false);
				return;
			}
		}
		reject(command);
	}
	
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
	}

	public static abstract class TaskRunnable implements Runnable {
		DownloadTaskInfo taskInfo;
	}
	
}

