package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.yunva.live.sdk.interfaces.logic.model.RoomInfo;

public class TeachVedioAdapter extends BaseAdapter {

	ArrayList<RoomInfo> roomInfo;
	Activity act;
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;

	public TeachVedioAdapter(Activity act) {
		roomInfo = new ArrayList<RoomInfo>();
		this.act = act;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	public void upType(ArrayList<RoomInfo> books) {
		if (roomInfo != books) {
			this.roomInfo.clear();
			this.roomInfo.addAll(books);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return roomInfo.size();
	}

	@Override
	public RoomInfo getItem(int arg0) {
		return roomInfo.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		ViewHolder holder;
		if (view == null) {
			LayoutInflater mInflater = LayoutInflater.from(act);
			view = mInflater.inflate(R.layout.teach_vedio_item, null);
			holder = new ViewHolder();
			holder.icon = (NetworkImageView) view.findViewById(R.id.icon);
			holder.title = (TextView) view.findViewById(R.id.title);
			holder.icon.setDefaultImageResId(R.drawable.icon_default_voice);
			holder.icon.setErrorImageResId(R.drawable.icon_default_voice);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		RoomInfo item = roomInfo.get(position);
		holder.title.setText(item.getRoomName());
		holder.icon.setTag(position);
		holder.icon.setImageUrl(item.getRoomUrl(), imageLoader);

		return view;
	}

	class ViewHolder {
		NetworkImageView icon;
		TextView title;
	}

}
