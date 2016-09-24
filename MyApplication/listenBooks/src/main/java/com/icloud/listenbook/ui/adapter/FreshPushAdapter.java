package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.FreshPush;

public class FreshPushAdapter extends BaseAdapter {
	ArrayList<FreshPush> datas;
	Activity act;
	ImageLoader imageLoader;
	String urlHead;
	LayoutInflater mInflater;

	public FreshPushAdapter(Activity act) {
		this.act = act;
		datas = new ArrayList<FreshPush>();
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		mInflater = LayoutInflater.from(act);
	}

	public void upData(ArrayList<FreshPush> datas) {
		this.datas.clear();
		this.datas.addAll(datas);
		this.notifyDataSetChanged();
	}

	public class ItemHolder implements OnClickListener {
		NetworkImageView icon;
		TextView name;
		public ItemHolder(View view) {
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			name = (TextView) view.findViewById(R.id.name);
			icon.setOnClickListener(this);
			icon.setDefaultImageResId(R.drawable.icon_default_img);
			icon.setErrorImageResId(R.drawable.icon_default_img);
		}

		public void setView(FreshPush item) {
			icon.setImageUrl(urlHead + item.getAIcon(), imageLoader);
			icon.setTag(item);
			name.setText(item.getAName());
		}

		@Override
		public void onClick(View v) {
			if (v.getTag() != null) {
				FreshPush item = (FreshPush) v.getTag();
				Article article = new Article();
				article.setAAbstract(item.getAAbstract());
				article.setAAuthor(item.getAAuthor());
				article.setAIcon(item.getAIcon());
				article.setAId(item.getAId());
				article.setAName(item.getAName());
				article.setClickConut(0);
				article.setChapterNum(item.getChapterNum());
				Intent Intent = new Intent();
				Intent.putExtra("data", JsonUtils.toJson(article));
				/* 1表示视频，2音频 3文字 默认为 0 表示 推荐首页* */
				Class<? extends Activity> activity = VedioInfoAct.class;
				if (item.getMedia() == Configuration.TYPE_VEDIO) {
					activity = VedioInfoAct.class;
				} else if (item.getMedia() == Configuration.TYPE_VOICE) {
					activity = VoiceInfoAct.class;
				} else if (item.getMedia() == Configuration.TYPE_BOOK) {
					activity = BookInfoAct.class;
				}
				LoadingTool.launchActivity(act, activity, Intent);
			}
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
			convertView = mInflater.inflate(R.layout.fresh_push_item, null);
			headerHolder = new ItemHolder(convertView);
			convertView.setTag(headerHolder);
		} else {
			headerHolder = (ItemHolder) convertView.getTag();
		}
		FreshPush item = datas.get(position);
		headerHolder.setView(item);

		return convertView;
	}

}
