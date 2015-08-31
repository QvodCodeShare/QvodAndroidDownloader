package com.qvod.lib.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.qvod.downloader.demo.R;
import com.qvod.lib.demo.DownloadItemAdapter.DownloadItem;
import com.qvod.lib.demo.DownloadItemAdapter.OnDownloadClickListener;
import com.qvod.lib.downloader.DownloadParameter;
import com.qvod.lib.downloader.DownloadState;
import com.qvod.lib.downloader.DownloadStateChangeListener;
import com.qvod.lib.downloader.DownloadTaskInfo;
import com.qvod.lib.downloader.concurrent.ThreadPoolAlterExecutor;
import com.qvod.lib.downloader.concurrent.ThreadPoolExecutor;
import com.qvod.lib.downloader.manager.DownloadTaskManager;

/**
 * [描述]
 * @author 李理
 * @date 2015年8月26日
 */
public class DownloadManagerActivity extends Activity implements OnItemLongClickListener, OnItemClickListener{
	
	private final static String TAG = DownloadManagerActivity.class.getSimpleName();

	private final static String PARAM_SAVE_FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QvodDownload"; 
	
	@InjectView(R.id.listview)
	ListView mListView;
	
	private DownloadItemAdapter mAdapter;
	
	private DownloadTaskManager mDownloadManager;
	
	private Handler mHandler;
	private ThreadPoolExecutor mExecutor;
	
	private String[] mSettingUrls;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloadmanager);
		ButterKnife.inject(this);
		mAdapter = new DownloadItemAdapter(this);
		mAdapter.setOnDownloadClickListener(mDownloadClickListener);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemLongClickListener(this);
		mListView.setOnItemClickListener(this);
		
		mDownloadManager = new DownloadTaskManager(getApplicationContext());
		mDownloadManager.addDownloadStateChangeListener(downloadListener);
		
		mHandler = new Handler();
		mExecutor = ThreadPoolAlterExecutor.createFlexibleExecutor(0, 2, 5 * 1000);
		mSettingUrls = getResources().getStringArray(R.array.urls);
	}
	
	DownloadStateChangeListener downloadListener = new DownloadStateChangeListener() {

		@Override
		public void onDownloadStateChanged(final DownloadTaskInfo taskInfo) {
			Log.v(TAG, "onDownloadStateChanged taskInfo " + taskInfo.downloadState);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					DownloadItem item = mAdapter.findDownloadItem(taskInfo.downloadParameter.id);
					DownloadItem refreshItem = convertDownloadItem(item, taskInfo);
					if (item == null) {
						mAdapter.addDownloadItem(refreshItem);
					}
					mAdapter.notifyDataSetChanged();
				}
			});
		}
	};
	
	OnDownloadClickListener mDownloadClickListener = new OnDownloadClickListener() {

		@Override
		public void onSwichClick(final DownloadItem file) {
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {
					if (file.state.ordinal() >= DownloadState.STATE_QUEUE.ordinal()) {
						mDownloadManager.pauseTask(file.id);
					} else if (file.state.ordinal() <= DownloadState.STATE_CREATED.ordinal()) {
						mDownloadManager.runTask(file.id, false);
					}
				}
			});
		}
	};
	
	private void addTask(final String url) {
		mExecutor.execute(new Runnable() {
			public void run() {
				DownloadParameter parameter = new DownloadParameter();
				parameter.id = url;
				parameter.url = url;
				parameter.saveFileDir = PARAM_SAVE_FILE_DIR;
				mDownloadManager.createTask(parameter);
				
			}
		});
	}
	 
	@OnClick(R.id.btn_add_task)
	void onClickAddTask() {
		showAddTaskDialog();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mAdapter.isSelectModel()) {
			return;
		}
		mAdapter.selectItem(position);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mAdapter.isSelectModel()) {
			return false;
		}
		mAdapter.setSelectModel(true);
		mAdapter.selectItem(position);
		return true;
	}

	private void showAddTaskDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
       	final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
 	  	final EditText editText = (EditText)textEntryView.findViewById(R.id.edit_url);
 	  	String hintUrl = mSettingUrls[(int)(Math.random()*mSettingUrls.length)];
 	  	hintUrl= "http://dare-04.yxdown.cn/yxdown.com_Warcraft3FrozenThrone1.26_chs.exe";
 	  	editText.setHint(hintUrl);

 	  	Dialog dialog = new AlertDialog.Builder(this)
           .setTitle("添加下载任务")
           .setView(textEntryView)
           .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int whichButton) {
                	  String url = editText.getText().toString();
                	  if (url.trim().equals("")) {
                		  url = editText.getHint().toString();
                	  }
                	  if (! url.toLowerCase().startsWith("http://")) {
                		  Toast.makeText(getApplicationContext(), "Url不合法", Toast.LENGTH_SHORT).show();
                		  return;
                	  }
                	  addTask(url);
               }
           })
           .setNegativeButton("取消", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int whichButton) {
            	   dialog.dismiss();
               }
           }).create();
 	  	dialog.show();
	}

	private DownloadItem convertDownloadItem(DownloadItem item, DownloadTaskInfo task) {
		if (item == null) {
			item = new DownloadItem();
		}
		item.id = task.downloadParameter.id;
		item.name = task.getSaveFileName();
		item.progress = (int)task.calcProgress();
		item.state = task.downloadState;
		item.size = StringUtil.getFileSize(task.downloadFileLength);
		item.downloadSize = StringUtil.getFileSize(task.currentDownloadSize);
		return item;
	}
}

