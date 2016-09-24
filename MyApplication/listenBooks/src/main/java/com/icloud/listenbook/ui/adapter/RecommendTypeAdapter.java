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
import com.icloud.listenbook.ui.adapter.entity.FooterHolder;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.ui.adapter.entity.Type;
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
import com.listenBook.greendao.Recommend;

public class RecommendTypeAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	ArrayList<Recommend> items;
	Activity act;
	String urlHead;
	ImageLoader imageLoader;
	final FooterManage footerManage;

	public RecommendTypeAdapter(Activity act, ArrayList<Recommend> items) {
		this.act = act;
		this.items = items;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		footerManage = new FooterManage();
	}

	@Override
	public int getItemCount() {
		return items.size() + 1;
	}

	@Override
	public int getItemViewType(int pos) {
		if (isFooter(pos)) {
			return Type.TABLE_FOOTER;
		}
		return Type.TABLE_ITEM;
	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		NetworkImageView icon;
		TextView name;
		TextView aAuthor;
		TextView chapter;
		View lay;

		public ItemHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			aAuthor = (TextView) view.findViewById(R.id.aAuthor);
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			chapter = (TextView) view.findViewById(R.id.chapter);
			lay = view.findViewById(R.id.lay);
			icon.setOnClickListener(this);
			lay.setOnClickListener(this);
			lay.setOnTouchListener(ViewUtils.instance().onTouchListener);

		}

		public void setView(Recommend item) {
			icon.setTag(item);
			lay.setTag(item);
			name.setText(item.getAName());
			aAuthor.setText(item.getAAuthor());
			icon.setImageUrl(urlHead + item.getAIcon(), imageLoader);
			icon.setDefaultImageResId(R.drawable.icon_default_img);
			icon.setErrorImageResId(R.drawable.icon_default_img);
			chapter.setText(String.format(
					act.getString(R.string.chapterNum_to),
					TextUtils.conversionNum(item.getChapterNum())));
		}

		@Override
		public void onClick(View v) {
			if (v.getTag() != null) {
				Recommend item = (Recommend) v.getTag();
				Article article = new Article();
				article.setAAbstract(item.getAAbstract());
				article.setAAuthor(item.getAAuthor());
				article.setAIcon(item.getAIcon());
				article.setAId(item.getAId());
				article.setAName(item.getAName());
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

	public void setStatus(int status) {
		if (footerManage != null) {
			footerManage.setStatus(status);
		}
	}

	public int getStatus() {
		if (footerManage != null) {
			return footerManage.getStatus();
		}
		return FooterManage.FREE;
	}

	public boolean isFooter(int pos) {
		return pos >= items.size() || items.size() == 0;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {
		if (isFooter(pos)) {
			footerManage.set();
			return;
		}
		Recommend item = items.get(pos);
		ItemHolder holder = (ItemHolder) viewHolder;
		holder.setView(item);

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == Type.TABLE_FOOTER) {
			view = LayoutInflater.from(act).inflate(R.layout.footer_item,
					parent, false);
			FooterHolder footerHolder = new FooterHolder(view);
			footerManage.footer = footerHolder;
			return footerHolder;
		} else {
			view = LayoutInflater.from(act).inflate(R.layout.recommen_item,
					parent, false);
		}
		return new ItemHolder(view);

	}

	public void upDatas(ArrayList<Recommend> items) {
		if (this.items != items) {
			this.items.clear();
			this.items.addAll(items);
		}
		this.notifyDataSetChanged();

	}

}
