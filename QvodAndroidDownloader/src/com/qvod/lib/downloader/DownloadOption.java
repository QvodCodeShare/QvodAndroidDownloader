package com.qvod.lib.downloader;

/**
 * [下载器配置]
 * 
 * @author 李理
 * @date 2015年8月19日
 */
public class DownloadOption {
	
	public static final DownloadOption sDefaultOption = buildDefaultOption();
	
	/**
	 * 一个下载线程的下载Buffer大小
	 * 到达指定buffer大小后内存中的数据才会刷到文件中
	 * buffer大小过大会导致内存的大量占用，过小会导致文件的频繁IO操作
	 */
	public int downloadBuffer;
	
	public int limitRate;
	
	/**
	 * 单任务最大下载线程数
	 */
	public int downloadThreadNum;
	
	/**
	 * 同时下载的最大任务数
	 */
	public int maxDownloadNum;
	
	/**
	 * 线程池的等待任务时间
	 * 超过该时间后会释放等待的线程，等待的线程池会全部被清空
	 */
	public long threadPoolKeepAliveTime;
	
	public static DownloadOption buildDefaultOption() {
		DownloadOption option = new DownloadOption();
		option.downloadBuffer = 1024;
		option.downloadThreadNum = 3;
		option.maxDownloadNum = 3;
		option.threadPoolKeepAliveTime = 60 * 1000;
		
		
		return option;
	}
}

