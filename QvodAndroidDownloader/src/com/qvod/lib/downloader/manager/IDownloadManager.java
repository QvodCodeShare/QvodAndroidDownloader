package com.qvod.lib.downloader.manager;
import java.util.List;

import com.qvod.lib.downloader.DownloadParameter;
import com.qvod.lib.downloader.DownloadState;
import com.qvod.lib.downloader.DownloadStateChangeListener;
import com.qvod.lib.downloader.DownloadTaskInfo;


/**
 * [下载管理器接口]
 * 
 * @author 李理
 * @date 2015年8月25日
 */
public interface IDownloadManager {

	 void createTask(DownloadParameter parameter);
	 
	 void runTask(String id, boolean needCutting);
	 
	 void runAllTask();
	 
	 void pauseTask(String id);
	 
	 void pauseAllTask();
	 
	 void deleteTask(String id);
	 
	 void deleteAllTask();
	 
	 DownloadTaskInfo getDownloadTask(String id);
	 
	 List<DownloadTaskInfo> getAllDownloadTask();
	 
	 List<DownloadTaskInfo> getDownloadTaskByState(DownloadState state);
	 
	 int getDownloadTaskCount();
	 
	 int getDownloadTaskCountByState(DownloadState state);
	 
	 void setAllowDownloadNetwork(NetworkStatus networkStatus);
	 
	 NetworkStatus getAllowDownloadNetwork();
	 
	 void addDownloadStateChangeListener(DownloadStateChangeListener listener);
	 
	 void removeDownloadStateChangeListener(DownloadStateChangeListener listener);
	 
	 void saveAllDownloadState();
	 
	 void resumeAllDownloadState();
	 
	 void release();
}

