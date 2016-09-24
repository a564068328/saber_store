package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.unit.LruImageCache;

public class GreatMasterTabAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	ArrayList<String> data;
	ImageLoader imageLoader;
	String urlHead;
	Activity act;
	int index;
	OnItemClickListener ItemClick;

	public void setOnItemClickListener(OnItemClickListener list) {
		this.ItemClick = list;
	}

	public GreatMasterTabAdapter(Activity act, ArrayList<String> data, int index) {
		this.data = data;
		this.act = act;
		this.index = index;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);

	}

	public void upData(ArrayList<String> data) {
		if (this.data != data) {
			data.clear();
			data.addAll(data);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {

		ItemHolder holder = (ItemHolder) viewHolder;
		holder.setView(pos);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		View view = LayoutInflater.from(act).inflate(
				R.layout.great_master_tab_item, parent, false);
		return new ItemHolder(view);
	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		RadioButton title;

		public ItemHolder(View view) {
			super(view);
			title = (RadioButton) view.findViewById(R.id.title);
			title.setOnClickListener(this);

		}

		public void setView(int postion) {
			String tip = data.get(postion);
			title.setText(tip);
			title.setTag(postion);
			title.setChecked(postion == index ? true : false);
		}

		@Override
		public void onClick(View v) {
			int postion = (Integer) v.getTag();
			index = postion;
			if (ItemClick != null)
				ItemClick.onItemClick(null, v, postion, v.getId());
			GreatMasterTabAdapter.this.notifyDataSetChanged();
		}
	}
	
	public void setindex(int tabposition) {
		// TODO Auto-generated method stub
		index=tabposition;
	}
}
