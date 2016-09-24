package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.adapter.entity.PinterestLikeItem;
import com.icloud.listenbook.ui.chipAct.PhotoViewAct;
import com.icloud.listenbook.ui.chipAct.WebAct;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;

public class PinterestLikeAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	ArrayList<PinterestLikeItem> data;
	ImageLoader imageLoader;
	String urlHead;
	Activity act;

	public PinterestLikeAdapter(Activity act) {
		this.data = new ArrayList<PinterestLikeItem>();
		this.act = act;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);

	}

	public PinterestLikeAdapter(Activity act, ArrayList<PinterestLikeItem> data) {
		this.data = data;
		this.act = act;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);

	}

	public void upData(ArrayList<PinterestLikeItem> data) {
		if (this.data != data) {
			this.data.clear();
			this.data.addAll(data);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {
		PinterestLikeItem item = data.get(pos);
		ItemHolder holder = (ItemHolder) viewHolder;
		holder.setView(item);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		View view = LayoutInflater.from(act).inflate(
				R.layout.pinteres_grid_item, parent, false);
		return new ItemHolder(view);
	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		NetworkImageView icon;
		TextView title;

		public ItemHolder(View view) {
			super(view);
			title = (TextView) view.findViewById(R.id.title);
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			icon.setDefaultImageResId(R.drawable.icon_default_img);
			icon.setErrorImageResId(R.drawable.icon_default_img);
			icon.setOnClickListener(ItemHolder.this);
		}

		public void setView(PinterestLikeItem item) {
			title.setText(item.title);
			icon.setTag(item);
			icon.setImageUrl(urlHead + item.icon, imageLoader);

		}

		@Override
		public void onClick(View v) {
			PinterestLikeItem item = (PinterestLikeItem) v.getTag();
			if (item != null) {
				if (!TextUtils.isEmpty(item.url)) {
					if (item.url.startsWith("http://")
							|| item.url.startsWith("www.")
							|| item.url.startsWith("https://")) {
						Intent intent = new Intent();
						intent.putExtra("url", item.url);
						LoadingTool.launchActivity(act, WebAct.class, intent);
					}
				} else {
					Intent intent = new Intent();
					intent.putExtra("url", urlHead + item.icon);
					LoadingTool.launchActivity(act, PhotoViewAct.class, intent);
				}
			}

		}
	}
}
