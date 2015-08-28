package com.qvod.lib.downloader;

/**
 * [基础下载器接口]
 * @author 李理
 * @date 2015年8月17日
 */
public interface IDownloader {

	DownloadState download(DownloadParameter parameter);
	
	void stop();
	
	boolean isDownloading();
	
	DownloadTaskInfo getDownloadTaskInfo();
	
	void setDownloadStateChangeListener(DownloadStateChangeListener listener);
	
	void setDownloadOption(DownloadOption downloadOption);
	
	void setAutoNotifyDownloadEvent(boolean isAutoNotify, long intervalTime);
}

