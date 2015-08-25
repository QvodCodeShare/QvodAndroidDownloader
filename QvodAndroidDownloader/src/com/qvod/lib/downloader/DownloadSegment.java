package com.qvod.lib.downloader;

/**
 * [下载数据区块的描述]
 * 
 * @author 李理
 * @date 2015年8月17日
 */
public class DownloadSegment {

	/**
	 * 文件的下载起始位置
	 * 单线程下载情况下，startPos总为0
	 */
	public long startPos;
	
	public long endPos;
	
	public long downloadPos;
}

