package com.qvod.lib.downloader.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * [描述]
 * @author 李理
 * @date 2015年8月24日
 */
public class DownloadTaskPoollExecutor extends ThreadPoolExecutor {

	public DownloadTaskPoollExecutor(int corePoolSize, int maximumPoolSize, 
			long keepAliveTime, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, workQueue, sThreadFactory);
	}

	static ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public java.lang.Thread newThread(Runnable r) {
			return new java.lang.Thread(r, "Executor" + mCount.getAndIncrement());
		}
	};
	

	public void getWorkerTasks() {
		BlockingQueue<Runnable> queue = getQueue();
		queue.toArray();
	}
	
}

