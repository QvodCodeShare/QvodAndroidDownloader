package com.qvod.lib.downloader.multithread;

import java.util.concurrent.CopyOnWriteArrayList;

import com.qvod.lib.downloader.DownloadParameter;
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
public class BlockDownloadRunnable implements Runnable {
	private static final String TAG = BlockDownloadRunnable.class.getSimpleName();
	private DownloadParameter mBolckParameter;
	private IDownloader mDownloader;
	private CopyOnWriteArrayList<DownloadState> mDownloadStateList;
	public BlockDownloadRunnable(DownloadParameter parameter,DownloadStateChangeListener downloadStateChangeListener) {
		this.mBolckParameter = parameter;
		mDownloader = new Downloader();
		mDownloader.setDownloadStateChangeListener(downloadStateChangeListener);
	}

	@Override
	public void run() {
		mDownloader.download(mBolckParameter);
	}

	public void stop() {
		mDownloader.stop();
	}
	
	public long getCurDownloadSize() {
		if (mDownloader == null || getBlockDownloadTaskInfo() == null) {
			return 0;
		}
		return getBlockDownloadTaskInfo().currentDownloadSize;
	}
	
	public DownloadTaskInfo getBlockDownloadTaskInfo() {
		if (mDownloader == null) {
			return null;
		}
		return mDownloader.getDownloadTaskInfo();
	}
}