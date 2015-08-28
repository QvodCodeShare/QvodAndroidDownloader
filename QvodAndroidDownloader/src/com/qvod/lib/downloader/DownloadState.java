package com.qvod.lib.downloader;

/**
 * @author 李理
 * @date 2015年8月19日
 */
public enum DownloadState {

	/**
	 * 错误
	 */
	STATE_ERROR,
	/**
	 * 已暂停
	 */
	STATE_STOP, 
	/**
	 * 任务创建成功
	 */
	STATE_CREATED,
	/**
	 * 已完成
	 */
	STATE_COMPLETED, 
	/**
	 * 排队中
	 */
	STATE_QUEUE,
	/**
	 * 下载准备中
	 * 进行的准备工作包括文件创建、建立网络连接
	 */
	STATE_PREPARE, 
	/**
	 * 开始下载中
	 */
	STATE_DOWNLOAD, 
	/**
	 * 正在暂停
	 */
	STATE_STOP_ING,

}

