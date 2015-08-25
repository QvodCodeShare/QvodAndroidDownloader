package com.qvod.lib.downloader;

import java.util.List;
import java.util.Map;

/**
 * [下载参数]
 * 
 * @author 李理
 * @date 2015年8月17日
 */
public class DownloadParameter {

	public String id;
	
	public String url;
	
	public String method = "GET";
	
	public byte[] postContent;
	
	public Map<String, String> httpHeader;
	
	public String saveFileDir;
	
	public String saveFileName;
	
	public int connectTimeout = 30 * 1000;
	
	public int readTimeout = 30 * 1000;
	
	public Object tag;
	
	/**
	 * 下载区块的分段信息
	 * 只有需要多线程下载的时候才需要此值
	 */
	public List<DownloadSegment> downloadSegments;
}

