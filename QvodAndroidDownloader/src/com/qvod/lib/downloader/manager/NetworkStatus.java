package com.qvod.lib.downloader.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/*
 * 版    权： 深圳市爱猫新媒体数据科技有限公司
 * 创建人: 李理
 * 创建时间: 2014年8月13日
 */
import android.telephony.TelephonyManager;



/**
 * [网络状态]
 * @author 李理 
 */
public enum NetworkStatus {
	NETWORK_NONE,NETWORK_WIFI,NETWORK_3G,NETWORK_2G;
	
	/**
	 * 判断网络连接的类型
	 * 
	 * @param context
	 * @return
	 */
	public static NetworkStatus getNetworkStatus(Context context) {
		if (context == null) {
			return NetworkStatus.NETWORK_NONE;
		}
		ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return NetworkStatus.NETWORK_NONE;
		}
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info == null) {
			return NetworkStatus.NETWORK_NONE;
		}
		if (! info.isConnected()) {
			return NetworkStatus.NETWORK_NONE;
		}
		final int type = info.getType();
		if (type == ConnectivityManager.TYPE_WIFI) {
			return NetworkStatus.NETWORK_WIFI;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			if (isNetworkGprs(context)) {
				return NetworkStatus.NETWORK_2G;
			} else if (isNetwork3G(context)) {
				return NetworkStatus.NETWORK_3G;
			} else {
				return NetworkStatus.NETWORK_2G;
			}
		}
		return NetworkStatus.NETWORK_NONE;
	}
	
	/**
	 * 判断是否GPRS
	 * 
	 * @param context
	 * @return true表示是GPRS，否则不是GPRS
	 */
	public static boolean isNetworkGprs(Context context) {
		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telMgr == null) {
			return false;
		}
		/*
		 * telMgr.getNetworkCountryIso();//获取电信网络国别
		 * telMgr.getPhoneType();//获得行动通信类型 telMgr.getNetworkType();//获得网络类型
		 */
		// Log.i(TAG, "NetworkType" + telMgr.getNetworkType());
		int networkType = telMgr.getNetworkType();
		if (networkType == TelephonyManager.NETWORK_TYPE_GPRS
				|| networkType == TelephonyManager.NETWORK_TYPE_EDGE) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否3G网络
	 * 
	 * @param context
	 * @return true表示是3G，否则不是3G
	 */
	public static boolean isNetwork3G(Context context) {
		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telMgr == null) {
			return false;
		}
		int networkYype = telMgr.getNetworkType();
		// NETWORK_TYPE_EDGE 是为GPRS到第三代移动通信的过渡性技术方案(GPRS俗称2.5G， EDGE俗称2.75G）
		if (networkYype != TelephonyManager.NETWORK_TYPE_GPRS
				&& networkYype != TelephonyManager.NETWORK_TYPE_UNKNOWN
				&& networkYype != TelephonyManager.NETWORK_TYPE_EDGE) {
			return true;
		}
		return false;
	}
}
