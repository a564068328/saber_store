package com.icloud.wrzjh.base.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.icloud.listenbook.base.HandlerUtils;

public abstract class PagedListAdapter<T> extends BaseAdapter {
	/**
	 * 业务数据
	 */
	protected List<T> mDatas = new ArrayList<T>();
	/**
	 * 当前加载的页
	 */
	public int nextPageNo = 1;

	protected LayoutInflater mInflater;

	protected Context mContext;

	Handler handler = HandlerUtils.instance();

	public PagedListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	public abstract void setListView(ListView listView);

	/**
	 * 追加业务数据
	 * 
	 * @param datas
	 */
	protected void addData(final Collection<T> datas) {
		if (datas != null) {
			mDatas.addAll(datas);
			notifyDataSetChanged();
		}
	}

	public List<T> getDatas() {
		return mDatas;
	}

	public int getCount() {
		return mDatas.size();
	}

	public T getItem(int i) {
		return mDatas.get(i);
	}

	public long getItemId(int i) {
		return i;
	}

	public abstract View getView(int position, View convertView,
			ViewGroup parent);
}
