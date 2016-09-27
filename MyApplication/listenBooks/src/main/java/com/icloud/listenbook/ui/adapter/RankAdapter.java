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
import com.icloud.wrzjh.base.utils.TextUtils;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.Rank;

public class RankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	ArrayList<Rank> items;
	Activity act;
	String urlHead;
	ImageLoader imageLoader;

	public RankAdapter(Activity act) {
		this.act = act;
		this.items = new ArrayList<Rank>();
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

	public void upDatas(ArrayList<Rank> items) {
		if (this.items != items) {
			this.items.clear();
			this.items.addAll(items);
		}
		this.notifyDataSetChanged();

	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {
		Rank item = items.get(pos);
		ItemHolder holder = (ItemHolder) viewHolder;
		holder.setView(item);
	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		public static final String CLIKE_COUNT = "点击:%s";
		NetworkImageView icon;
		TextView name;
		TextView aAuthor;
		TextView heat;
		View lay;

		public ItemHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			aAuthor = (TextView) view.findViewById(R.id.aAuthor);
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			heat = (TextView) view.findViewById(R.id.heat);
			lay = view.findViewById(R.id.lay);
			lay.setOnClickListener(this);

		}

		public void setView(Rank item) {
			lay.setTag(item);
			name.setText(item.getAName());

			aAuthor.setText(item.getAAuthor());
			icon.setImageUrl(urlHead + item.getAIcon(), imageLoader);
			icon.setDefaultImageResId(R.drawable.icon_default_img);
			icon.setErrorImageResId(R.drawable.icon_default_img);
			heat.setText(String.format(CLIKE_COUNT,
					TextUtils.conversionNum(item.getNum())));
		}

		@Override
		public void onClick(View v) {
			if (v.getTag() != null) {
				Rank item = (Rank) v.getTag();
				Article article = new Article();
				article.setAAbstract(item.getAAbstract());
				article.setAAuthor(item.getAAuthor());
				article.setAIcon(item.getAIcon());
				article.setAId(item.getAId());
				article.setAName(item.getAName());
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
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(act).inflate(R.layout.rank_item,
				parent, false);
		return new ItemHolder(view);

	}

}
