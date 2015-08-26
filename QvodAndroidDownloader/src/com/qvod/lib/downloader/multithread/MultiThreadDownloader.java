package com.qvod.lib.downloader.multithread;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

import com.qvod.lib.downloader.DownloadOption;
import com.qvod.lib.downloader.DownloadParameter;
import com.qvod.lib.downloader.DownloadSegment;
import com.qvod.lib.downloader.DownloadState;
import com.qvod.lib.downloader.DownloadStateChangeListener;
import com.qvod.lib.downloader.DownloadTaskInfo;
import com.qvod.lib.downloader.Downloader;
import com.qvod.lib.downloader.IDownloader;

/**
 * [描述]
 * 
 * @author xj
 * @date 2015年8月21日
 */
public class MultiThreadDownloader implements IDownloader {

	private static final String TAG = MultiThreadDownloader.class
			.getSimpleName();
	private static final int DEFAULT_THREAD_NUM = 1;
	private DownloadOption mDownloadOption;
	private AtomicBoolean mIsRun = new AtomicBoolean(false);
	private long mFileSize;
	private long mBlock;
	private String mFileName;
	private DownloadStateChangeListener mListener;
	private List<DownloadParameter> mBlockDownloadParameter;
    private DownloadState mCurrentState;
	private int mErrorResponseCode;
	private DownloadParameter mDownloadParameter;
	private Map<String, String> mResponseHeader = new HashMap<String, String>();
	private CopyOnWriteArrayList<DownloadState> mDownloadStateList;
	private List<BlockDownloadRunnable> mDownloadThreadList;
	private List<DownloadSegment> mDownloadSegments;
	
	@Override
	public DownloadState download(DownloadParameter parameter) {
		if (mIsRun.get()) {
			return null;
		}
		mIsRun.set(true);
		mDownloadParameter = parameter;
		
		updateDownloadTaskInfo(DownloadState.STATE_PREPARE,0);
		
		mDownloadStateList = new CopyOnWriteArrayList<DownloadState>();
		mBlockDownloadParameter = new ArrayList<DownloadParameter>();
		// 获取下载文件的信息包括长度，文件名，以及分块下载的大小
		int downloadInfoCode = getDownloadFileInfo(parameter);
		if (downloadInfoCode != 0) {
			updateDownloadTaskInfo(DownloadState.STATE_ERROR,downloadInfoCode);
			return null;
		}

		int threadNum = getDownloadOpotin().downloadThreadNum;
		mDownloadSegments = parameter.downloadSegments;

		if (mDownloadSegments == null || mDownloadSegments.size() != threadNum) {
			mDownloadSegments = createBlockSegment(threadNum);
		}

		createBlockParameter(threadNum, mDownloadSegments, parameter);
		startDownload(mBlockDownloadParameter);
		
		return null;
	}
	
	private List<DownloadSegment> createBlockSegment(int threadNum) {
		List<DownloadSegment> downloadSegments = new ArrayList<DownloadSegment>(threadNum);
		for (int i = 0; i < threadNum; i++) {
			DownloadSegment downloadSegment = new DownloadSegment();
			downloadSegment.startPos = i * mBlock;
			downloadSegment.endPos = i == threadNum - 1 ? mFileSize : (i + 1) * mBlock;
			downloadSegment.downloadPos = 0;
			downloadSegments.add(downloadSegment);
		}
		return downloadSegments;
	}

	private void createBlockParameter(int threadNum, List<DownloadSegment> downloadSegments, DownloadParameter parameter) {
		for (int i = 0; i < threadNum; i++) {
			DownloadParameter blockDownloadParameter = new DownloadParameter();
			blockDownloadParameter.id = parameter.id;
			blockDownloadParameter.url = parameter.url;
			blockDownloadParameter.method = parameter.method;
			blockDownloadParameter.postContent = parameter.postContent;
			blockDownloadParameter.httpHeader = parameter.httpHeader;
			blockDownloadParameter.saveFileDir = parameter.saveFileDir;
			blockDownloadParameter.saveFileName = parameter.saveFileName;
			blockDownloadParameter.connectTimeout = parameter.connectTimeout;
			blockDownloadParameter.readTimeout = parameter.readTimeout;
			blockDownloadParameter.tag = parameter.tag;
			blockDownloadParameter.downloadSegments = downloadSegments;
			mBlockDownloadParameter.add(blockDownloadParameter);
		}
	}
	
	private void updateDownloadTaskInfo(DownloadState state, int errorReason) {
		mCurrentState = state;
		mErrorResponseCode = errorReason;
		DownloadTaskInfo taskInfo = getDownloadTaskInfo();
		if (mListener == null) {
			return;
		}
		if (state == DownloadState.STATE_STOP) {
			mListener.onDownloadStateChanged(taskInfo);
		} else if (mIsRun.get()) {
			mListener.onDownloadStateChanged(taskInfo);
		} 
	}

	private void startDownload(List<DownloadParameter> blockDownloadParameters) {
		mDownloadThreadList = new ArrayList<BlockDownloadRunnable>();
		for (DownloadParameter blockParameter : blockDownloadParameters) {
			// 线程池？
			BlockDownloadRunnable blockRunnable = new BlockDownloadRunnable(blockParameter,mDownloadStateChangeListener);
			mDownloadThreadList.add(blockRunnable);
			new Thread(blockRunnable).start();
		}
	}
	
	private DownloadStateChangeListener mDownloadStateChangeListener = new DownloadStateChangeListener() {
		
		@Override
		public void onDownloadStateChanged(DownloadTaskInfo taskInfo) {
			DownloadState state = taskInfo.downloadState;
			int errorResponseCode = taskInfo.errorResponseCode;
			Log.i(TAG, "state:" + state);
			if (state == DownloadState.STATE_ERROR || state == DownloadState.STATE_STOP ) {
				stop();
			}
			if ((state == DownloadState.STATE_ERROR || state == DownloadState.STATE_STOP || state == DownloadState.STATE_DOWNLOAD) && !mDownloadStateList.contains(state)) {
				mDownloadStateList.add(state);
				updateDownloadTaskInfo(state, errorResponseCode);
				return;
			}
			
			if (state == DownloadState.STATE_COMPLETED && getDownloadSize() == mFileSize) {
				updateDownloadTaskInfo(state, errorResponseCode);
				return;
			}
		}
	};
	
	@Override
	public void stop() {
		mIsRun.set(false);
		if (mDownloadThreadList == null) {
			return;
		}
		for(BlockDownloadRunnable blockRunnable : mDownloadThreadList) {
			blockRunnable.stop();
		}
		/*mDownloadStateList = null;
		mBlockDownloadParameter = null;
		mDownloadThreadList = null;
		mDownloadSegments = null;*/
	}

	@Override
	public boolean isDownloading() {
		return mIsRun.get();
	}

	@Override
	public DownloadTaskInfo getDownloadTaskInfo() {
		DownloadTaskInfo taskInfo = new DownloadTaskInfo();
		taskInfo.downloadState = mCurrentState;
		taskInfo.downloadParameter = mDownloadParameter;
		taskInfo.startDownloadPos = 0;
		taskInfo.currentDownloadSize = getDownloadSize();
		taskInfo.downloadFileLength = mFileSize;
		taskInfo.errorResponseCode = mErrorResponseCode;
		taskInfo.responseHeader = mResponseHeader;
		if (getDownloadSegment() != null) {
			taskInfo.setDownloadSegments(getDownloadSegment());
		}
		return taskInfo;
	}
	
	private List<DownloadSegment> getDownloadSegment() {
		List<DownloadSegment> downloadSegment = null;
		if (mDownloadThreadList == null) {
			return null;
		}
		
		downloadSegment = new ArrayList<DownloadSegment>();
		int size = mDownloadThreadList.size();
		for (int i = 0;i<size;i++) {
			BlockDownloadRunnable thread = mDownloadThreadList.get(i);
			DownloadTaskInfo info = thread.getBlockDownloadTaskInfo();
			if (info == null) {
				return null;
			}
			DownloadSegment segment = new DownloadSegment();
			segment.downloadPos = info.currentDownloadSize;
			segment.startPos = info.startDownloadPos;
			segment.endPos = info.downloadFileLength;
			downloadSegment.add(segment);
		}
		
		return downloadSegment;
	}
	
	private long getDownloadSize() {
		if (mDownloadThreadList == null) {
			return 0;
		}
		long downloadSize = 0;
		int size = mDownloadThreadList.size();
		for (int i = 0;i<size;i++) {
			BlockDownloadRunnable thread = mDownloadThreadList.get(i);
			downloadSize += thread.getCurDownloadSize();
		}
		return downloadSize;
	}

	@Override
	public void setDownloadStateChangeListener(
			DownloadStateChangeListener listener) {
		this.mListener = listener;
	}

	@Override
	public void setDownloadOption(DownloadOption downloadOption) {
		this.mDownloadOption = downloadOption;
	}

	private DownloadOption getDownloadOpotin() {
		if (mDownloadOption == null) {
			mDownloadOption = new DownloadOption();
			mDownloadOption.downloadBuffer = -1;
			mDownloadOption.downloadThreadNum = DEFAULT_THREAD_NUM;
			mDownloadOption.limitRate = -1;
		}
		return mDownloadOption;
	}

	private int getDownloadFileInfo(DownloadParameter parameter) {
		try {
			URL url = new URL(parameter.url);
			HttpURLConnection conn = getConnection(url, parameter.method,
					parameter.httpHeader, parameter.connectTimeout,
					parameter.readTimeout);
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK
					&& responseCode != 206) {
				return responseCode;
			}
			mFileSize = conn.getContentLength();
			int threadNum = getDownloadOpotin().downloadThreadNum;
			mBlock = (mFileSize % threadNum) == 0 ? mFileSize / threadNum
					: mFileSize / threadNum + 1;
			mFileName = parameter.url
					.substring(parameter.url.lastIndexOf("/") + 1);

			Log.i(TAG, "fileSize:" + mFileSize + "block:" + mBlock
					+ "fileName:" + mFileName);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	private HttpURLConnection getConnection(URL url, String method,
			Map<String, String> httpHead, int connectTimeOut, int readTimeout)
			throws IOException {

		HttpURLConnection conn = null;
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setConnectTimeout(connectTimeOut);
		conn.setReadTimeout(readTimeout);
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded;charset="
						+ Downloader.DEFAULT_CHARSET);
		conn.setRequestProperty("connection", "Keep-Alive");

		if (httpHead != null) {
			Iterator<String> it = httpHead.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = httpHead.get(key);
				if (key != null && value != null) {
					conn.setRequestProperty(key, value);
				}
			}
		}

		return conn;
	}
}
