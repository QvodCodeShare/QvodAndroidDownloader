package com.qvod.lib.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.qvod.downloader.demo.R;
import com.qvod.lib.demo.widget.UpgradeProgressBar;
import com.qvod.lib.downloader.DownloadState;
import com.qvod.lib.downloader.DownloadStateChangeListener;
import com.qvod.lib.downloader.DownloadTaskInfo;
import com.qvod.lib.downloader.Downloader;

/**
 * [描述]
 
 test asaaaaaaaaaaaaa
 
 * @author 李理
 * @date 2015年8月19日
 */
public class SimpleDownloadActivity extends Activity {

	private final static String TAG = "Downloader";
	private Handler mHandler;
	
	private Downloader mDownloader;
	
	//test you can see?
	private DownloadTaskInfo mRecordTaskInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.simple_download_update_layout);
		ButterKnife.inject(this);
		mHandler = new Handler();
		mDownloader = new Downloader();
		showUpdateUI();
		
//		System.setProperty("http.proxyHost", "10.0.3.2"); 
//		System.setProperty("http.proxyPort", "8888"); 
//		System.setProperty("https.proxyHost", "10.0.3.2");
//		System.setProperty("https.proxyPort", "8888");
		
//		System.setProperty("http.proxyHost", "localhost"); 
//		System.setProperty("http.proxyPort", "8888"); 
//		System.setProperty("https.proxyHost", "localhost");
//		System.setProperty("https.proxyPort", "8888");
	}
	
	@OnClick(R.id.btn_updrade)
	public void onClickUpdate() {
		if (! mDownloader.isDownloading()) {
			startDownload();
		} else {
			stopDownload();
		}
	}
	
	private void stopDownload() {
		showStopUI();
		new Thread() {
			public void run() {
				Log.v(TAG, "stopDownload");
				mDownloader.stop();
			}
		}.start();
	}
	
	private void startDownload() {
		showDownloadUI(); 
		
		mDownloader.setDownloadStateChangeListener(mDownloadStateChangeListener);
		new Thread() { 
			public void run() {
//				testHttps();
//				testHttps2();
				//APK 5MB
				final String url = "http://p.gdown.baidu.com/2f6cc56ab05e862068e9d0fc22cfe70b289ccba7dcd53aa231f0664ac35d558c8f1a4c37102429f3c652cacd2998bfa94d9526b9053614bea543aaf5c1616e3877acd4ee624d53379c75c965392f9dc48718f892f76fe99f4641a0ddb59628d86300c0485aeab794314e4d1a4dc3e210becfad9f3178f6e14cf0b63d722d446ec1885f86be4a67579732149c57216b9a2268761f458cc6db53a8aab307e4ddba76741a8ed9c6e9182edbbbb27c27d4d08e9498344f9aa8f8a896f01f509e84ed030159ab4b797325824153d8380ee876c5a1bf1b0e2348446e122b447dd1309068f468e0e828b5c0b0c0ef21592edb1434524dbf39e308150d8d088fb2419cec52651f73c5d0e63407dc443636e2ff713bd8f9b882089e4bd9de680014f55391c4794e1c5f445786380a560463812034e33c7316735bcd3e0f19eca6b9425b6ace5a4be9681f191feaf13bb491df39a3c7d245b9ed8b4ef4";
				//JPG 
//				final String url =
//						"http://d.hiphotos.baidu.com/image/h%3D200/sign=fe8467f657da81cb51e684cd6267d0a4/2f738bd4b31c8701f2613de4217f9e2f0608ffd9.jpg";
				//exe 700MB Https 302 速度极快
//				final String url = 
//						"https://www.baidu.com/link?url=jaDuPOR2sxjarDFOPJzOeWOOSxEujdRL5mkkdQxwyF-VkVghWxfEWN5aRMpgigMP2hEfomQw5AYoT7VAXLOZ2MzewDuy70z3QY32LACEq8O&wd=&eqid=e5fb891a000a933c0000000555d52b8f";
				//exe 700MB Http 302 速度极快
//				final String url = 
//				"http://w.x.baidu.com/alading/anquan_soft_down_normal/17707";
				
				//exe 700MB 速度极慢
//				final String url = 
//						"http://dare-04.yxdown.cn/yxdown.com_Warcraft3FrozenThrone1.26_chs.exe";
				
				//exe 700MB Url请求带有特殊字符，且需要Cookie，
//				String url = 
//						"http://download.52pk.com:8088/down.php?fileurl=aHR0cDovL2Rvd25sb2FkMS41MnBrLmNvbTo4MDg4L2hlenVvL3dhcjNfY25fY3dnYW1lLjUycGsuZXhl%&aid=58&key=419d15f955cde9a2f172523417e8f277";
//				 url = url.replaceAll("%", "%25");
				
				//exe 700MB 下载速度一般 验证UserAgent，不正确则无法下载
//				String url = 
//						"http://download1.52pk.com:8088/hezuo/war3_cn_cwgame.52pk.exe";
				//apk 236MB 速度很快 
				/*String url = 
						"http://cdn.longtugame.com/channel_bin/520006/apk/4.1.2/520006_397.apk";*/
//				String url = 
//						"https://m.xiaoniu88.com:8477/mobile/s/apk/XNOnline_1.1.0.apk";
//				String url = 
//						"http://m.xiaoniu88.com:8077/mobile/s/apk/XNOnline_1.1.0.apk";
				//普通的Https请求
//				String url = 
//						"https://172.20.20.208:8477/mobile/home.json";
//				String url = 
//						"http://172.20.20.208:8002/mobile/home.json";
				String saveFileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qvodDownloader";
				if(mRecordTaskInfo == null) {
					mDownloader.download(url, saveFileDir);
				} else {
					mDownloader.download(
							mRecordTaskInfo.downloadParameter.url,
							mRecordTaskInfo.downloadParameter.saveFileDir,
							mRecordTaskInfo.currentDownloadSize,
							0L);
				}
			}
		}.start();
	}
	
	DownloadStateChangeListener mDownloadStateChangeListener = new DownloadStateChangeListener() {
		
		@Override
		public void onDownloadStateChanged(final DownloadTaskInfo taskInfo) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					switch (taskInfo.downloadState) {
					case STATE_PREPARE:
						updateDownloadState("文件下载准备中...");
						break;
					case STATE_DOWNLOAD:
						updateDownloadState("文件开始下载...");
						autoRefreshProgress();
						break;
					case STATE_COMPLETED:
						showCompleteUI();
						cacelRefreshProgress();
						break;
					case STATE_STOP:
						showErrorUI("下载被停止");
						cacelRefreshProgress();
						recordDownloadProgress();
						break;
					case STATE_ERROR:
						showErrorUI("下载出错,错误码: " + taskInfo.errorResponseCode);
						cacelRefreshProgress();
						recordDownloadProgress();
						break;
					default:
						break;
					}
				}
			});
			
			if (taskInfo.downloadState == DownloadState.STATE_COMPLETED) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						showFinishUI();
					}
				}, 2000);
			}
		}
	};
	
	private void recordDownloadProgress() {
		mRecordTaskInfo = mDownloader.getDownloadTaskInfo();
		Log.v(TAG, "recordDownloadProgress"
				+ " downloadPos:" + mRecordTaskInfo.currentDownloadSize
				+ " downloadContentLength:" + mRecordTaskInfo.downloadFileLength
				+ " startPos:" + mRecordTaskInfo.startDownloadPos
				+ " pogress:" + mRecordTaskInfo.calcProgress());
	}
	
	private void autoRefreshProgress() {
		mHandler.postDelayed(mRefreshProgressRunnable, 1000);
	}
	
	private void cacelRefreshProgress() {
		mHandler.removeCallbacks(mRefreshProgressRunnable);
	}
	
	Runnable mRefreshProgressRunnable = new Runnable() {
		
		@Override
		public void run() {
			DownloadTaskInfo taskInfo = mDownloader.getDownloadTaskInfo();
			float progress = taskInfo.calcProgress(); 
			showProgress(progress);
			autoRefreshProgress();
			Log.v(TAG, "mRefreshProgressRunnable refreshProgress: " + progress);
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDownloader.stop();
	}

	private void updateDownloadState(String text) {
		tvUpdateTip.setTextColor(getResources().getColor(R.color.white));
		tvUpdateTip.setText(text);
	}
	
	private void showUpdateUI() {
		updradeBtn.setText("检测到有数据更新，请点击更新");
		updradeBtn.setVisibility(View.VISIBLE);
		upgradeProgressBar.setVisibility(View.GONE);
		tvUpdateTip.setVisibility(View.GONE);
	}
	
	private void showStopUI() {
		updradeBtn.setText("下载被暂停\n点击恢复下载");
	}
	
	private void showDownloadUI() {
		updradeBtn.setText("更新中\n点击暂停");
		updradeBtn.setVisibility(View.VISIBLE);
		upgradeProgressBar.setVisibility(View.VISIBLE);
		tvUpdateTip.setVisibility(View.VISIBLE);
		rotateView.setVisibility(View.GONE);
		flickerView.setVisibility(View.GONE);
		updradeBtn.setBackgroundColor(getResources().getColor(R.color.transparent));
	}
	
	private void showProgress(float progress) {
		upgradeProgressBar.setProgress((int)progress);
		tvUpdateTip.setText("当前进度为" + progress +"%");
	}
	
	private void showCompleteUI() {
		updradeBtn.setVisibility(View.GONE);
		tvUpdateTip.setVisibility(View.VISIBLE);
		tvUpdateTip.setText("下载成功，处理中...");
		upgradeProgressBar.setMax(100);
		upgradeProgressBar.setProgress(0);
		upgradeProgressBar
				.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.home_router_upgrade_success));
	}
	
	private void showFinishUI() {
		tvUpdateTip.setText("恭喜您，更新成功!");
		runRotateAnim();
	}
	
	private void runRotateAnim() {
		rotateView.setVisibility(View.VISIBLE);
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setFillAfter(false);
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		rotateAnimation.setDuration(5000);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		rotateView.startAnimation(rotateAnimation);
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				rebootOk();
			}
		}, 5000);
	}
	
	private void rebootOk() {
		rotateView.clearAnimation();
		rotateView.setVisibility(View.GONE);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(400);
		alphaAnimation.setRepeatCount(5);
		flickerView.setVisibility(View.VISIBLE);
		flickerView.startAnimation(alphaAnimation);
	}

	private void showErrorUI(String tip) {
		Log.v(TAG, "showErrorUI " + tip);
		DownloadTaskInfo taskInfo = mDownloader.getDownloadTaskInfo();
		float progress = taskInfo.calcProgress();
		upgradeProgressBar.setProgress((int)progress);
		tvUpdateTip.setTextColor(getResources().getColor(R.color.red));
		tvUpdateTip.setText(tip);
	}

	@InjectView(R.id.upgrade_progress)
	UpgradeProgressBar upgradeProgressBar;
	
	@InjectView(R.id.tv_update_tip)
	TextView tvUpdateTip;
	
	@InjectView(R.id.btn_updrade)
	Button updradeBtn;
	
	@InjectView(R.id.rotateAnim)
	ImageView rotateView;
	
	@InjectView(R.id.flickerAnim)
	ImageView flickerView;
}

