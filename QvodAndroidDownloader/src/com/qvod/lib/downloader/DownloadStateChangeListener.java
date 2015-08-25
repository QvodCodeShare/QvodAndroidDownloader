package com.qvod.lib.downloader;

/**
 * [下载回调事件]
 * @author 李理
 * @date 2015年8月17日
 */
public interface DownloadStateChangeListener {

	void onDownloadStateChanged(DownloadTaskInfo taskInfo);

}

