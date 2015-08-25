package com.qvod.lib.downloader.manager;
import java.util.HashSet;
import java.util.List;

import com.qvod.lib.downloader.DownloadParameter;
import com.qvod.lib.downloader.DownloadState;
import com.qvod.lib.downloader.DownloadStateChangeListener;
import com.qvod.lib.downloader.DownloadTaskInfo;


/**
 * [描述]
 * @author 李理
 * @date 2015年8月25日
 */
public class DownloadTaskManager implements IDownloadManager {

	private HashSet<DownloadTaskInfo> mDownloadTasks = new HashSet<DownloadTaskInfo>();
	
	public DownloadTaskManager() {
	}

	@Override
	public void createTask(DownloadParameter parameter) {
	}

	@Override
	public void runTask(String id, boolean needCutting) {
		// TODO Auto-generated method stub

	}

	@Override
	public void runAllTask() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pauseTask(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pauseAllTask() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteTask(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllTask() {
		// TODO Auto-generated method stub

	}

	@Override
	public DownloadTaskInfo getDownloadTask(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DownloadTaskInfo> getAllDownloadTask() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DownloadTaskInfo> getDownloadTaskByState(DownloadState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDownloadTaskCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDownloadTaskCountByState(DownloadState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAllowDownloadNetwork(NetworkStatus networkStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public NetworkStatus getAllowDownloadNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addDownloadStateChangeListener(DownloadStateChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDownloadStateChangeListener(DownloadStateChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveAllDownloadState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resumeAllDownloadState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

}

