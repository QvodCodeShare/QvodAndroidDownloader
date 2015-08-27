package com.qvod.lib.downloader.manager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.util.Log;

import com.qvod.lib.downloader.DownloadOption;
import com.qvod.lib.downloader.DownloadParameter;
import com.qvod.lib.downloader.DownloadState;
import com.qvod.lib.downloader.DownloadStateChangeListener;
import com.qvod.lib.downloader.DownloadTaskInfo;
import com.qvod.lib.downloader.Downloader;


/**
 * [描述]
 * @author 李理
 * @date 2015年8月25日
 */
public class DownloadTaskManager implements IDownloadManager {
	
	private final static String TAG = DownloadTaskManager.class.getSimpleName();

	private Map<String, DownloadTask> mDownloadTasks = new HashMap<String, DownloadTask>();
	
	private DownloadOption mDownloadOption = DownloadOption.sDefaultOption;
	
	private DownloadTaskPoollExecutor mExecutor = 
			new DownloadTaskPoollExecutor(0, mDownloadOption.maxDownloadNum, mDownloadOption.threadPoolKeepAliveTime);
	
	private List<DownloadStateChangeListener> mStateChangeListeners = new ArrayList<DownloadStateChangeListener>();
	
	private NetworkStatus[] mAllowDownloadNetwork;
	
	private Context mContext;
	
	public DownloadTaskManager(Context context) {
		mContext = context;
	}

	@Override
	public synchronized boolean createTask(DownloadParameter parameter) {
		if (mDownloadTasks.containsKey(parameter.id)) {
			Log.e(TAG, "createTask already existing task");
			return false;
		}
		DownloadTaskInfo taskInfo = new DownloadTaskInfo();
		//TODO 填充 taskInfo 的其他字段
		taskInfo.downloadParameter = parameter;
		DownloadTask task = new DownloadTask();
		task.taskInfo = taskInfo;
		mDownloadTasks.put(parameter.id, task);
		setDownloadTaskState(taskInfo, DownloadState.STATE_CREATED);
		return true;
	}

	@Override
	public synchronized void runTask(String id, boolean isRunTaskTop) {
		DownloadTask task = mDownloadTasks.get(id);
		if (task == null) {
			return;
		}
		runTask(task, isRunTaskTop);
	}

	@Override
	public synchronized void runAllTask() {
		Iterator<Map.Entry<String,DownloadTask>> it = mDownloadTasks.entrySet().iterator();
		while(it.hasNext()) {
			DownloadTask task = it.next().getValue();
			runTask(task, false);
		}
	}
	
	void runTask(DownloadTask task, boolean isRunTaskTop) {
		if (task == null) {
			return;
		}
		if (task.taskInfo != null) {
			Log.v(TAG, "runTask 任务已开启  state: " + task.taskInfo.downloadState);
			return;
		}
		
		TaskRunnable runnable = new TaskRunnable(task.taskInfo);
		task.taskRunner = runnable;
		setDownloadTaskState(task.taskInfo, DownloadState.STATE_QUEUE);
		mExecutor.execute(runnable, isRunTaskTop);
	}
	
	@Override
	public synchronized void pauseTask(String id) {
		DownloadTask task = mDownloadTasks.get(id);
		if (task == null) {
			return;
		}
		pauseTask(task, true);
	}

	@Override
	public synchronized void pauseAllTask() {
		Iterator<Map.Entry<String,DownloadTask>> it = mDownloadTasks.entrySet().iterator();
		while(it.hasNext()) {
			DownloadTask task = it.next().getValue();
			pauseTask(task, true);
		}
	}
	
	void pauseTask(DownloadTask task, boolean needChangeState) {
		if (task == null) {
			return;
		}
		if (task.taskRunner == null) {
			Log.v(TAG, "pauseTask 任务未在执行 state: " + task.taskInfo.downloadState);
			return;
		}
		if (needChangeState) {
			setDownloadTaskState(task.taskInfo, DownloadState.STATE_STOP_ING);
		}
		boolean isRemoved = mExecutor.remove(task.taskRunner);
		if (isRemoved) {
			Log.v(TAG, "pauseTask 任务在执行队列中，移除成功");
			if (needChangeState) {
				setDownloadTaskState(task.taskInfo, DownloadState.STATE_STOP);
			}
			return;
		}
		task.taskRunner.stop();
		task.taskRunner = null;
		Log.v(TAG, "pauseTask 任务在执行中，stop");
	}

	@Override
	public synchronized void deleteTask(String id, boolean deleteFile) {
		DownloadTask task = mDownloadTasks.remove(id);
		if (task == null) {
			return;
		}
		pauseTask(task, false);
		if (deleteFile) {
			deleteTaskFile(task.taskInfo);
		}
	} 

	@Override
	public synchronized void deleteAllTask(boolean deleteFile) {
		Iterator<Map.Entry<String,DownloadTask>> it = mDownloadTasks.entrySet().iterator();
		while(it.hasNext()) {
			DownloadTask task = it.next().getValue();
			it.remove();
			pauseTask(task, false);
			if (deleteFile) {
				deleteTaskFile(task.taskInfo);
			}
		}
	}
	
	void deleteTaskFile(DownloadTaskInfo taskInfo) {
		String filePath = taskInfo.getSaveFilePath();
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	} 

	@Override
	public synchronized DownloadTaskInfo getDownloadTask(String id) {
		DownloadTask task = mDownloadTasks.get(id);
		return task.taskInfo.clone();
	}

	@Override
	public synchronized List<DownloadTaskInfo> getAllDownloadTask() {
		return getDownloadTaskByState(null);
	}

	@Override
	public synchronized List<DownloadTaskInfo> getDownloadTaskByState(DownloadState state) {
		List<DownloadTaskInfo> list = new ArrayList<DownloadTaskInfo>(mDownloadTasks.size());
		Iterator<Map.Entry<String,DownloadTask>> it = mDownloadTasks.entrySet().iterator();
		while(it.hasNext()) {
			DownloadTask task = it.next().getValue();
			if (state != null && state != task.taskInfo.downloadState) {
				continue;
			}
			list.add(task.taskInfo.clone());
		}
		return list;
	}

	@Override
	public synchronized int getDownloadTaskCount() {
		return getDownloadTaskCountByState(null);
	}

	@Override
	public synchronized int getDownloadTaskCountByState(DownloadState state) {
		int count = 0;
		Iterator<Map.Entry<String,DownloadTask>> it = mDownloadTasks.entrySet().iterator();
		while(it.hasNext()) {
			DownloadTask task = it.next().getValue();
			if (state != null && state != task.taskInfo.downloadState) {
				continue;
			}
			count++;
		}
		return count;
	}

	@Override
	public synchronized void setAllowDownloadNetwork(NetworkStatus[] networkStatus) {
		mAllowDownloadNetwork = networkStatus;
	}

	@Override
	public synchronized NetworkStatus[] getAllowDownloadNetwork() {
		return mAllowDownloadNetwork;
	}

	@Override
	public synchronized void addDownloadStateChangeListener(DownloadStateChangeListener listener) {
		if (listener == null || mStateChangeListeners.contains(listener)) {
			return;
		}
		mStateChangeListeners.add(listener);
	}

	@Override
	public synchronized void removeDownloadStateChangeListener(DownloadStateChangeListener listener) {
		mStateChangeListeners.remove(listener);
	}

	@Override
	public synchronized void saveAllDownloadState() {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void resumeAllDownloadState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDownloadOption(DownloadOption option) {
		mDownloadOption = option;
		applyDownloadOption();
	}
	
	private void applyDownloadOption() {
		mExecutor.setKeepAliveTime(mDownloadOption.threadPoolKeepAliveTime, TimeUnit.MILLISECONDS);
		mExecutor.setMaximumPoolSize(mDownloadOption.maxDownloadNum);
	}
	
	@Override
	public synchronized void release() {
		deleteAllTask(false);
		mStateChangeListeners.clear();
		mExecutor.clearTaskList();
	}
	
	synchronized void setDownloadTaskState(DownloadTaskInfo taskInfo, DownloadState state) {
		taskInfo.downloadState = state;
		notifyDownloadStateChange(taskInfo.clone());
	}
	
	synchronized void refreshDownloadTaskState(DownloadTaskInfo refreshTaskInfo) {
		DownloadTask task = mDownloadTasks.get(refreshTaskInfo.downloadParameter.id);
		DownloadTaskInfo taskInfo = task.taskInfo;
		taskInfo.downloadState = refreshTaskInfo.downloadState;
		taskInfo.currentDownloadSize = refreshTaskInfo.currentDownloadSize;
		taskInfo.downloadFileLength = refreshTaskInfo.downloadFileLength;
		taskInfo.errorResponseCode = refreshTaskInfo.errorResponseCode;
		taskInfo.responseHeader = refreshTaskInfo.responseHeader;
		taskInfo.saveFileName = refreshTaskInfo.saveFileName;
		taskInfo.startDownloadPos = refreshTaskInfo.startDownloadPos;
		taskInfo.extendsMap = refreshTaskInfo.extendsMap;
		
		if (task.taskRunner != null 
				&& refreshTaskInfo.downloadState.ordinal() <= DownloadState.STATE_CREATED.ordinal()) {
			task.taskRunner = null;
		}
		
		notifyDownloadStateChange(taskInfo.clone());
	}

	void notifyDownloadStateChange(DownloadTaskInfo taskInfo) {
		for(DownloadStateChangeListener listener : mStateChangeListeners) {
			listener.onDownloadStateChanged(taskInfo);
		}
	}
	
	class TaskRunnable implements Runnable, DownloadStateChangeListener {
		
		DownloadTaskInfo taskInfo;
		
		Downloader downloader = new Downloader();
		
		volatile boolean isRun = true;
		
		private ReentrantLock networkLock = new ReentrantLock();
	    private final Condition networkCondition = networkLock.newCondition();
	    private boolean isWaitNetwork = false;
		
		public TaskRunnable(DownloadTaskInfo taskInfo) {
			this.taskInfo = taskInfo;
		}
		
		@Override
		public void run() {
			do {
				NetworkStatus currentNetworkStatus = NetworkStatus.getNetworkStatus(mContext);
				boolean isAllowDownload = !isAllowDownload(currentNetworkStatus);
				if (isAllowDownload) {
					//当前无网络或当前网络类型不允许进行下载，则等待网络恢复
					waitNetwork();
				}
				
				downloader.setDownloadStateChangeListener(this);
				downloader.setDownloadOption(mDownloadOption);
				DownloadState state = downloader.download(taskInfo.downloadParameter);
				
				if (state == DownloadState.STATE_ERROR) {
					//TODO 需处理网络异常后的保持下载,该处的处理可能不准确
					currentNetworkStatus = NetworkStatus.getNetworkStatus(mContext);
					if (currentNetworkStatus == NetworkStatus.NETWORK_NONE
							&& isAllowDownload(NetworkStatus.NETWORK_NONE))  {
						//下载错误，且当前无网络，且无网络状态下运行进行下载
						Log.v(TAG, "TaskRunnable 运行结束，任务在无网络情况下出现下载错误，且当前任务允许无网络下继续下载，等待网络恢复后继续下载");
						continue;
					} else {
						Log.v(TAG, "TaskRunnable 运行结束");
						DownloadTaskInfo notifyTaskInfo = downloader.getDownloadTaskInfo();
						refreshDownloadTaskState(notifyTaskInfo);
					}
				}
				break;
			} while(isRun);
		}
		
		void stop() {
			isRun = false;
			downloader.stop();
			signalNetwork();
		}
		
		@Override
		public void onDownloadStateChanged(DownloadTaskInfo notifyTaskInfo) {
			if (notifyTaskInfo.downloadState == DownloadState.STATE_ERROR) {
				return;
			}
			refreshDownloadTaskState(notifyTaskInfo);
		}

		@Override
		public int hashCode() {
			return taskInfo.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof TaskRunnable)) {
				return false;
			}
			TaskRunnable compare = (TaskRunnable)o;
			if (compare.taskInfo.equals(this.taskInfo)) {
				return true;
			}
			return false;
		}
		
		@Override
		public String toString() {
			return "Runnable " + taskInfo.toString();
		}
		
		private void waitNetwork() {
			networkLock.lock();
			try {
				if (! isRun) {
					Log.d(TAG, "awaitNetwork stoped");
					return;
				}
				Log.d(TAG, "awaitNetwork");
				isWaitNetwork = true;
				networkCondition.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				networkLock.unlock();
			}
		}
		
		private void signalNetwork() {
			networkLock.lock();
			try {
				if (! isWaitNetwork) {
					return;
				}
				Log.v(TAG, "signalNetwork");
				networkCondition.signalAll();
			} finally {
				networkLock.unlock();
			}
		}
	}
	
	private boolean isAllowDownload(NetworkStatus status) {
		if (mAllowDownloadNetwork == null) {
			return true;
		}
		for(NetworkStatus s : mAllowDownloadNetwork) {
			if (s == status) {
				return true;
			}
		}
		return false;
	}
	
	class DownloadTask {
		TaskRunnable taskRunner;
		DownloadTaskInfo taskInfo;
	}

}

