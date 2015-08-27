package com.qvod.lib.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qvod.downloader.demo.R;
import com.qvod.lib.downloader.DownloadState;

public class DownloadItemAdapter extends BaseAdapter {

	private List<DownloadItem> mList;

	private LayoutInflater mInflater;
	
	private OnDownloadClickListener mListener;
	
	private boolean mIsSelectModel;

	public DownloadItemAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	public void setDownloadList(List<DownloadItem> list) {
		this.mList = list;
	}
	
	public void setSelectModel(boolean b) {
		this.mIsSelectModel = b;
	}
	
	public boolean isSelectModel() {
		return this.mIsSelectModel;
	}
	
	public void setAllSelectStatus(boolean b) {
		if (mList == null) {
			return;
		}
		for(DownloadItem item : mList) {
			item.selected = b;
		}
	}
	
	public boolean isChooseAllItems() {
		if (mList == null) {
			return false;
		}
		for(DownloadItem item : mList) {
			if (! item.selected) {
				return false;
			}
		}
		return true;
	}
	
	public List<DownloadItem> getSelectedItem() {
		if (mList == null) {
			return null;
		}
		List<DownloadItem> selectList = new ArrayList<DownloadItem>();
		for(DownloadItem item : mList) {
			if (! item.selected) {
				continue;
			}
			selectList.add(item);
		}
		return selectList;
	}
	
	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public DownloadItem removeDownloadItem(String gid) {
		if (mList == null || gid == null) {
			return null;
		}
		Iterator<DownloadItem> it = mList.iterator();
		while(it.hasNext()) {
			DownloadItem item = it.next();
			if (gid.equals(item.id)) {
				it.remove();
				return item;
			}
		}
		return null;
	}
	
	public DownloadItem findDownloadItem(String id) {
		if (mList == null || id == null) {
			return null;
		}
		for(DownloadItem item : mList) {
			if (item.id == id) {
				return item;
			}
		}
		return null;
	}
	
	public void selectItem(int position) {
		if (mList == null) {
			return;
		}
		DownloadItem item = mList.get(position);
		item.selected = !item.selected;
		notifyDataSetChanged();
	}
	
	public void addDownloadItem(DownloadItem item) {
		if (mList == null) {
			mList = new ArrayList<DownloadItem>();
		}
		mList.add(item);
	}
	
	public void clearDownloadItem() {
		if (mList == null) {
			return;
		}
		mList.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.ihome_router_download_item, parent, false);
			viewHolder.iconImg = (ImageView) convertView
					.findViewById(R.id.iv_icon);
			viewHolder.nameTxt = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewHolder.infoTxt = (TextView) convertView
					.findViewById(R.id.tv_subTitle);
			viewHolder.statusTxt = (TextView) convertView
					.findViewById(R.id.tv_status);
			viewHolder.downloadProgress = (ProgressBar) convertView
					.findViewById(R.id.progress);
			viewHolder.switchBtn = (ImageView) convertView
					.findViewById(R.id.switch_btn);
			viewHolder.selectBtn = (ImageView) convertView
					.findViewById(R.id.select_btn);
			viewHolder.switchBtn.setOnClickListener(mClickListener);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		DownloadItem file = mList.get(position);
		viewHolder.nameTxt.setText(file.name);
		viewHolder.infoTxt.setText(file.size);
		String statusText = getDownloadStatusText(file.state);
		if (statusText != null) {
			boolean isStatusNormal = file.state == DownloadState.STATE_ERROR ? false : true;
			viewHolder.statusTxt.setSelected(!isStatusNormal);
			viewHolder.statusTxt.setText(statusText);
			viewHolder.statusTxt.setVisibility(View.VISIBLE);
		} else {
			viewHolder.statusTxt.setText("");
			viewHolder.statusTxt.setVisibility(View.GONE);
		}
		
		if (file.state != DownloadState.STATE_COMPLETED && file.state != DownloadState.STATE_ERROR) {
			viewHolder.downloadProgress.setProgress(file.progress);
			viewHolder.downloadProgress.setVisibility(View.VISIBLE);
		} else {
			viewHolder.downloadProgress.setVisibility(View.GONE);
		}
	 	
		if (file.fileIconResId != 0) { 
			viewHolder.iconImg.setImageResource(file.fileIconResId);
		}
		if (! mIsSelectModel) {
			int statusIcon = getSwichStatusIcon(file.state);
			if (statusIcon == -1 || file.state == DownloadState.STATE_ERROR) {
				viewHolder.switchBtn.setVisibility(View.GONE);
			} else {
				viewHolder.switchBtn.setVisibility(View.VISIBLE);
				viewHolder.switchBtn.setImageResource(statusIcon);
			}
			viewHolder.selectBtn.setVisibility(View.GONE);
		} else {
			viewHolder.selectBtn.setSelected(file.selected);
			viewHolder.selectBtn.setVisibility(View.VISIBLE);
			viewHolder.switchBtn.setVisibility(View.GONE);
		}
		viewHolder.switchBtn.setTag(file);
		return convertView;
	}
	
	OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			DownloadItem file = (DownloadItem) v.getTag();
			if (v.getId() == R.id.switch_btn) {
				if (mListener == null) {
					return;
				}
				mListener.onSwichClick(file);
			}
		}
	};

	public void setOnDownloadClickListener(OnDownloadClickListener l) {
		this.mListener = l;
	}
 	
	public interface OnDownloadClickListener {
		public void onSwichClick(DownloadItem file);
	}

	public String getDownloadStatusText(DownloadState state) {
		if (state == DownloadState.STATE_COMPLETED) {
			return null;
		}
		if (state == DownloadState.STATE_QUEUE) {
			return "等待中";
		}
		if (state == DownloadState.STATE_STOP) {
			return "已暂停";
		}
		if (state == DownloadState.STATE_STOP_ING) {
			return "正在暂停";
		}
		if (state == DownloadState.STATE_ERROR) {
			return "下载错误";
		}
		return null;
	}
	
	public int getSwichStatusIcon(DownloadState status) {
		if (status == DownloadState.STATE_COMPLETED) {
			return -1;
		}
		if (status.ordinal() > DownloadState.STATE_CREATED.ordinal()) {
			return R.drawable.ibox_file_icon_pause;
		} else if (status == DownloadState.STATE_STOP) {
			return R.drawable.ibox_file_icon_continue;
		}
		return R.drawable.ibox_file_icon_continue;
	}
	
	class ViewHolder {
		ImageView iconImg;
		ProgressBar downloadProgress;
		TextView nameTxt;
		TextView infoTxt;
		TextView statusTxt;
		ImageView switchBtn;
		ImageView selectBtn;
	}

	public static class DownloadItem {
		public String id;
		public String name;
		public String size;
		public DownloadState state;
		public int progress;
		public int fileIconResId;
		public boolean selected;
	}
}