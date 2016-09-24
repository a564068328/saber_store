package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.adapter.entity.PPtItem;
import com.icloud.listenbook.unit.LruImageCache;
import com.listenBook.greendao.FreshPush;

public class PPTAdapter extends BaseAdapter {
	ArrayList<PPtItem> datas;
	Activity act;
	ImageLoader imageLoader;
	String urlHead;
	LayoutInflater mInflater;

	public PPTAdapter(Activity act) {
		this.act = act;
		datas = new ArrayList<PPtItem>();
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		mInflater = LayoutInflater.from(act);
	}

	public void upData(ArrayList<PPtItem> datas) {
		this.datas.clear();
		this.datas.addAll(datas);
		this.notifyDataSetChanged();
	}

	public class ItemHolder {
		NetworkImageView icon;
		TextView title;

		public ItemHolder(View view) {
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			title = (TextView) view.findViewById(R.id.title);
			icon.setDefaultImageResId(R.drawable.icon_default_img);
			icon.setErrorImageResId(R.drawable.icon_default_img);
		}

		public void setView(PPtItem item) {
			icon.setImageUrl(urlHead + item.icon, imageLoader);
			icon.setTag(item);
			title.setText(item.name);
		}

	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemHolder headerHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.ppt_item, null);
			headerHolder = new ItemHolder(convertView);
			convertView.setTag(headerHolder);
		} else {
			headerHolder = (ItemHolder) convertView.getTag();
		}
		PPtItem item = datas.get(position);
		headerHolder.setView(item);

		return convertView;
	}

}
