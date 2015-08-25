package com.qvod.lib.downloader;

/**
 * @author 李理
 * @date 2015年8月19日
 */
public enum DownloadState {

	/**
	 * 下载准备中
	 * 进行的准备工作包括文件创建、建立网络连接
	 */
	STATE_PREPARE, 
	STATE_DOWNLOAD, 
	STATE_STOP, 
	STATE_COMPLETED, 
	STATE_ERROR,

}

