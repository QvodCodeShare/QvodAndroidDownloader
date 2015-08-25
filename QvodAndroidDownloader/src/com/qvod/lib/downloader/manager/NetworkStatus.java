package com.qvod.lib.downloader.manager;
/*
 * 版    权： 深圳市爱猫新媒体数据科技有限公司
 * 创建人: 李理
 * 创建时间: 2014年8月13日
 */



/**
 * [网络状态]
 * @author 李理 
 */
public enum NetworkStatus {
	NETWORK_NULL,NETWORK_WIFI,NETWORK_3G,NETWORK_2G;
	
	/**
	 * 判断网络连接的类型
	 * 
	 * @param context
	 * @return
	 */
//	public static NetworkStatus getNetworkStatus(Context context) {
//		if (context == null) {
//			return NetworkStatus.NETWORK_NULL;
//		}
//		ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext()
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		if (connectivity == null) {
//			return NetworkStatus.NETWORK_NULL;
//		}
//		NetworkInfo info = connectivity.getActiveNetworkInfo();
//		if (info == null) {
//			return NetworkStatus.NETWORK_NULL;
//		}
//		if (! info.isConnected()) {
//			return NetworkStatus.NETWORK_NULL;
//		}
//		final int type = info.getType();
//		if (type == ConnectivityManager.TYPE_WIFI) {
//			return NetworkStatus.NETWORK_WIFI;
//		} else if (type == ConnectivityManager.TYPE_MOBILE) {
//			if (NetWorkUtils.isNetworkGprs(context)) {
//				return NetworkStatus.NETWORK_2G;
//			} else if (NetWorkUtils.isNetwork3G(context)) {
//				return NetworkStatus.NETWORK_3G;
//			} else {
//				return NetworkStatus.NETWORK_2G;
//			}
//		}
//		return NetworkStatus.NETWORK_NULL;
//	}
}
