package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
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
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

public class SearchAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	ArrayList<Article> items;
	Activity act;
	String urlHead;
	ImageLoader imageLoader;

	public SearchAdapter(Activity act) {
		this.act = act;
		this.items = new ArrayList<Article>();
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		NetworkImageView icon;
		TextView name;
		TextView aAuthor;
		TextView note;
		TextView heat;
		View go;
		View del;
		View collection;

		public ItemHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			aAuthor = (TextView) view.findViewById(R.id.aAuthor);
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			note = (TextView) view.findViewById(R.id.note);
			heat = (TextView) view.findViewById(R.id.heat);
			go = view.findViewById(R.id.go);
			icon.setOnClickListener(this);
			icon.setOnTouchListener(ViewUtils.instance().onTouchListener);
			go.setOnClickListener(this);
			go.setOnTouchListener(ViewUtils.instance().onTouchListener);

		}

		public void setView(Article item) {
			icon.setTag(item);
			go.setTag(item);
			name.setText(item.getAName());
			aAuthor.setText(item.getAAuthor());
			icon.setImageUrl(urlHead + item.getAIcon(), imageLoader);
			icon.setDefaultImageResId(R.drawable.icon_default_img);
			icon.setErrorImageResId(R.drawable.icon_default_img);
		}

		@Override
		public void onClick(View v) {
			if (v.getTag() != null) {
				Article item = (Article) v.getTag();
				Article article = new Article();
				article.setAAbstract(item.getAAbstract());
				article.setAAuthor(item.getAAuthor());
				article.setAIcon(item.getAIcon());
				article.setAId(item.getAId());
				article.setAName(item.getAName());
				article.setChapterNum(item.getChapterNum());
				Intent Intent = new Intent();
				Intent.putExtra("data", JsonUtils.toJson(article));
//				LogUtil.e("TAG", "Configuration="+item.getMedia());
//				LogUtil.e("TAG", "item.getAId()="+item.getAId());
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
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {

		Article item = items.get(pos);
		ItemHolder holder = (ItemHolder) viewHolder;
		holder.setView(item);

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(act).inflate(R.layout.search_item,
				parent, false);

		return new ItemHolder(view);

	}

	public void upDatas(ArrayList<Article> items) {
		if (this.items != items) {
			this.items.clear();
			this.items.addAll(items);
		}
		this.notifyDataSetChanged();

	}

}
