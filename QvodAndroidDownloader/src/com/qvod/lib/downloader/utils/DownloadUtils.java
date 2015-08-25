package com.qvod.lib.downloader.utils;

import java.io.File;
import java.util.regex.Pattern;

import android.os.StatFs;

/**
 * [描述]
 * @author 李理
 * @date 2015年8月18日
 */
public class DownloadUtils {

	/**
	 * 是否为空
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		int strLen;
		if (value == null || (strLen = value.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isNumber(String str) {
		if (str == null || str.length() < 1) {
			return false;
		}

		Pattern pattern = Pattern.compile("[0-9]*.[0-9]*");
		return pattern.matcher(str).matches();
	}
	
	/**
	 * 获取可用空间
	 * 
	 * @param pathStr
	 * @return
	 */
	public static long getAvailaleSize(String pathStr) {
		File path = new File(pathStr); // 取得sdcard文件路径
		if (!path.exists()) {
			return -1;
		}
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}
	
	public static String getFileName(String path) {
		if (path == null) {
			return null;
		}
		int index = -1;
		index = path.lastIndexOf("/");
		return path.substring(index + 1);
	}
}

