package com.qvod.lib.downloader.manager;
import java.util.ArrayList;
import java.util.HashMap;
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

	private Map<String, DownloadTaskInfo> mDownloadTasks = new HashMap<String, DownloadTaskInfo>();
	
	private DownloadOption mDownloadOption = DownloadOption.sDefaultOption;
	
	private DownloadTaskPoollExecutor mExecutor = 
			new DownloadTaskPoollExecutor(0, mDownloadOption.maxDownloadNum, mDownloadOption.threadPoolKeepAliveTime);
	
	private List<DownloadStateChangeListener> mStateChangeListeners = new ArrayList<DownloadStateChangeListener>();
	
	private NetworkStatus[] mAllowDownloadNetwork;
	
	private ReentrantLock networkLock = new ReentrantLock();
    private final Condition networkCondition = networkLock.newCondition();
    private boolean isWaitNetwork = false;
    
	private Context mContext;
	
	public DownloadTaskManager(Context context) {
		mContext = context;
	}

	@Override
	public void createTask(DownloadParameter parameter) {
		if (mDownloadTasks.containsKey(parameter.id)) {
			Log.e(TAG, "createTask already existing task");
			return;
		}
		DownloadTaskInfo taskInfo = new DownloadTaskInfo();
		taskInfo.downloadParameter = parameter;
		mDownloadTasks.put(parameter.id, taskInfo);
	}

	@Override
	public void runTask(String id, boolean needCutting) {
		DownloadTaskInfo taskInfo = mDownloadTasks.get(id);
		if (taskInfo == null) {
			return;
		}
		
		TaskRunnable runnable = new TaskRunnable(taskInfo);
		mExecutor.execute(runnable);
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
	public void setAllowDownloadNetwork(NetworkStatus[] networkStatus) {
		mAllowDownloadNetwork = networkStatus;
	}

	@Override
	public NetworkStatus getAllowDownloadNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addDownloadStateChangeListener(DownloadStateChangeListener listener) {
		if (listener == null || mStateChangeListeners.contains(listener)) {
			return;
		}
		mStateChangeListeners.add(listener);
	}

	@Override
	public void removeDownloadStateChangeListener(DownloadStateChangeListener listener) {
		mStateChangeListeners.remove(listener);
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
	public void setDownloadOption(DownloadOption option) {
		mDownloadOption = option;
		applyDownloadOption();
	}
	
	private void applyDownloadOption() {
		mExecutor.setKeepAliveTime(mDownloadOption.threadPoolKeepAliveTime, TimeUnit.MILLISECONDS);
		mExecutor.setMaximumPoolSize(mDownloadOption.maxDownloadNum);
		
	}
	
	@Override
	public void release() {

	}

	void notifyDownloadStateChange(DownloadTaskInfo taskInfo) {
		//TODO Refresh DownloadTaskInfo 
		
		for(DownloadStateChangeListener listener : mStateChangeListeners) {
			listener.onDownloadStateChanged(taskInfo);
		}
	}
	
	class TaskRunnable implements Runnable, DownloadStateChangeListener {
		
		DownloadTaskInfo taskInfo;
		
		public TaskRunnable(DownloadTaskInfo taskInfo) {
			this.taskInfo = taskInfo;
		}
		
		@Override
		public void run() {
			do {
				NetworkStatus currentNetworkStatus = NetworkStatus.getNetworkStatus(mContext);
				if (! isAllowDownload(currentNetworkStatus)
						|| currentNetworkStatus == NetworkStatus.NETWORK_NONE) {
					//当前无网络或当前网络类型不允许进行下载，则等待网络恢复
					waitNetwork();
				}
				
				Downloader downloader = new Downloader();
				downloader.setDownloadStateChangeListener(this);
				downloader.setDownloadOption(mDownloadOption);
				DownloadState state = downloader.download(taskInfo.downloadParameter);
				
				if (state == DownloadState.STATE_ERROR) {
					//TODO 需处理网络异常后的保持下载,该处的处理可能不准确
					currentNetworkStatus = NetworkStatus.getNetworkStatus(mContext);
					if (currentNetworkStatus == NetworkStatus.NETWORK_NONE
							&& isAllowDownload(NetworkStatus.NETWORK_NONE))  {
						//下载错误，且当前无网络，且无网络状态下运行进行下载
						continue;
					} else {
						DownloadTaskInfo notifyTaskInfo = downloader.getDownloadTaskInfo();
						notifyDownloadStateChange(notifyTaskInfo);
					}
				}
				break;
			} while(true);
		}
		
		@Override
		public void onDownloadStateChanged(DownloadTaskInfo taskInfo) {
			if (taskInfo.downloadState == DownloadState.STATE_ERROR) {
				return;
			}
			notifyDownloadStateChange(taskInfo);
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
	
	private void waitNetwork() {
		networkLock.lock();
		try {
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

