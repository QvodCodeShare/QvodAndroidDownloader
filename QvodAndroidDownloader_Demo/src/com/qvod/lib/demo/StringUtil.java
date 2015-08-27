package com.qvod.lib.demo;

import java.text.DecimalFormat;

/**
 * [描述]
 * @author 李理
 * @date 2015年8月26日
 */
public class StringUtil {

	public static String getFileSize(long length) {
		if (length == 0) {
			return "0M";
		}

		String[] syn = { "B", "KB", "MB", "GB" };
		int i = 0;
		float f = length;
		while (f >= 1024) {
			if (i >= syn.length - 1) {
				break;
			}
			f = f / 1024;
			i++;
		}
		String pattern = "";
		pattern = (f - (int) f) > 0 ? "####.0" : "####";
		DecimalFormat format = new DecimalFormat(pattern);
		String size = format.format(f) + syn[i];
		return size;
	}
}

