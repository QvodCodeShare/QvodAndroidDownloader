package com.qvod.lib.downloader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [下载任务的描述信息]
 * 
 * @author 李理
 * @date 2015年8月17日
 */
public class DownloadTaskInfo {

	public DownloadParameter downloadParameter;
	
	public DownloadState downloadState;
	
	/**
	 * 文件的保存位置
	 * downloadParameter 参数中的 saveFileName 
	 * 如果被设置，则saveFilePath是 downloadParameter 的路径与文件明拼接出来
	 * 如果未设置，则saveFilePath的文件名自动生成，并且组装出新的saveFilePath
	 */
	public String saveFilePath;
	
	/**
	 * 文件的起始位置
	 * 
	 */
	public long startDownloadPos;
	/**
	 * 文件的下载位置
	 */
	public long currentDownloadPos;
	/**
	 * 文件的下载长度
	 */
	public long downloadFileLength;
	
	/**
	 * 下载任务信息的扩展字段集
	 * 非必要参数都收集在此
	 */
	private Map<String, Object> mExtendsMap;
	
	/**
	 * 服务器返回的Http头信息
	 * 只有在网络连接成功，并且状态为 #State.STATE_START 、 #State.STATE_ERROR 、 #State.STATE_COMPLETED 时候返回
	 */
	public Map<String, String> responseHeader;
	
	public int errorResponseCode;
	
	public static class ResponseCode {
		public final static int RESULT_SUC = 200;
		public final static int RESULT_STOP = -100;
		public final static int RESULT_DOWNLOADING = -101;
		public final static int NETWORK_ERROR = -102;
		public final static int NETWORK_TIME_OUT_ERROR = -103;
		public final static int SDCARD_NOT_MOUNTED_ERROR = -104;
		public final static int NOT_ENOUGH_SPACE_ERROR = -105;
		public final static int RESPONSE_CONTENT_TYPE_CAN_NOT_SUPPORT_ERROR = -106;
		public final static int RESPONSE_CONTENT_LENGTH_ERROR = -107;
		public final static int EMPTY_PARAM_SAVE_PATH_ERROR = -108;
		public final static int OTHER_ERROR = -1000;
		/**
		 * Other response code is http response code
		 */
	}
	
	/**
	 * [本次任务的下载进度]<BR>
	 * 
	 * @return
	 */
	public float calcProgress() {
		if (downloadFileLength == -1) {
			return 0;
		}
		return (currentDownloadPos/(float)downloadFileLength) * 100;
	}
	
	public void setDownloadSegments(List<DownloadSegment> segments) {
		if (segments == null) {
			return;
		}
		if (mExtendsMap == null) {
			mExtendsMap = new HashMap<String, Object>();
		}
		mExtendsMap.put(EXTENDS_DOWNLOAD_SEGMENTS, segments);
	}
	
	public List<DownloadSegment> getDownloadSegments() {
		if (mExtendsMap == null) {
			return null;
		}
		Object obj = mExtendsMap.get(EXTENDS_DOWNLOAD_SEGMENTS);
		if (obj == null) {
			return null;
		}
		return (List<DownloadSegment>)obj;
	}
	
	private static final String EXTENDS_DOWNLOAD_SEGMENTS = "downloadSegments";
	
	
}
