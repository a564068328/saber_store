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
import com.icloud.listenbook.ui.adapter.entity.ArticleItem;
import com.icloud.listenbook.ui.adapter.entity.FooterHolder;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.TextUtils;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

public class BookListAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> implements
		OnClickListener {
	private ArrayList<ArticleItem> datas;
	Activity act;
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;
	final FooterManage footerManage;

	public BookListAdapter(Activity act, ArrayList<ArticleItem> datas) {
		this.act = act;
		this.datas = datas;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		footerManage = new FooterManage();

	}

	public void upDatas(ArrayList<ArticleItem> datas) {
		if (this.datas != datas) {
			this.datas.clear();
			this.datas.addAll(datas);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return datas.size() + 1;
	}

	public class GridViewHolder extends RecyclerView.ViewHolder {
		NetworkImageView icon;
		TextView name;

		public GridViewHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			icon.setTag(null);
			icon.setOnClickListener(BookListAdapter.this);
			name.setOnClickListener(BookListAdapter.this);
			name.setOnTouchListener(ViewUtils.instance().onTouchListener);
		}

		public void setView(ArticleItem item, int pos) {
			icon.setTag(item);
			name.setTag(item);
			String NameStr = item.getAName();
			if (pos < 6 && (NameStr.indexOf("《") == -1)
					&& (NameStr.indexOf("》") == -1)) {
				name.setText("《" + NameStr + "》");
			} else {
				name.setText(NameStr);
			}
			icon.setImageUrl(urlHead + item.getAIcon(), imageLoader);
			icon.setDefaultImageResId(R.drawable.icon_default_img);
			icon.setErrorImageResId(R.drawable.icon_default_img);
		}
	}

	public class TitleHolder extends RecyclerView.ViewHolder {
		TextView name;

		public TitleHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
		}

		public void setView(ArticleItem item) {
			name.setText(item.getAName());

		}
	}

	public class ItemHolder extends RecyclerView.ViewHolder {
		public static final String CHAPTER_NUM = "章节:%s";
		public static final String CLIKE_COUNT = "点击:%s";

		NetworkImageView icon;
		TextView name;
		TextView author;
		TextView chapterNum;
		TextView clickConut;
		View go;

		public ItemHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			author = (TextView) view.findViewById(R.id.author);
			chapterNum = (TextView) view.findViewById(R.id.chapterNum);
			clickConut = (TextView) view.findViewById(R.id.clickConut);
			icon.setTag(null);
			icon.setOnClickListener(BookListAdapter.this);
			name.setOnClickListener(BookListAdapter.this);
			name.setOnTouchListener(ViewUtils.instance().onTouchListener);
		}

		public void setView(ArticleItem item) {
			icon.setTag(item);
			name.setTag(item);
			name.setText(item.getAName());
			icon.setImageUrl(urlHead + item.getAIcon(), imageLoader);
			icon.setDefaultImageResId(R.drawable.icon_default_img);
			icon.setErrorImageResId(R.drawable.icon_default_img);

			author.setText(item.getAAuthor());
			chapterNum
					.setText(String.format(CHAPTER_NUM, item.getChapterNum()));
			clickConut.setText(String.format(CLIKE_COUNT,
					TextUtils.conversionNum(item.getClickConut(), 10000)));
		}
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {
		if (isFooter(pos)) {
			footerManage.set();
			return;
		}
		ArticleItem item = datas.get(pos);
		if (viewHolder instanceof TitleHolder) {
			TitleHolder headerHolder = (TitleHolder) viewHolder;
			headerHolder.setView(item);
		} else if (viewHolder instanceof GridViewHolder) {
			GridViewHolder holder = (GridViewHolder) viewHolder;
			holder.setView(item, pos);
		} else if (viewHolder instanceof ItemHolder) {
			ItemHolder holder = (ItemHolder) viewHolder;
			holder.setView(item);
		}
	}

	public boolean isFooter(int pos) {
		return pos >= datas.size() || datas.size() == 0;
	}

	@Override
	public int getItemViewType(int pos) {
		if (isFooter(pos)) {
			return Type.TABLE_FOOTER;
		}
		return datas.get(pos).viewType;
	}

	public int getSpanSize(int pos) {
		if (isFooter(pos)) {
			return 3;
		}
		ArticleItem item = datas.get(pos);
		switch (item.viewType) {
		case Type.TABLE_ITEM:
		case Type.TABLE_TITLE:
			return 3;
		case Type.TABLE_GRID:
			return 1;
		}
		return 1;
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

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == Type.TABLE_FOOTER) {
			view = LayoutInflater.from(act).inflate(R.layout.footer_item,
					parent, false);
			FooterHolder footerHolder = new FooterHolder(view);
			footerManage.footer = footerHolder;
			return footerHolder;
		} else if (viewType == Type.TABLE_TITLE) {
			view = LayoutInflater.from(act).inflate(R.layout.voice_title_item,
					parent, false);
			return new TitleHolder(view);
		} else if (viewType == Type.TABLE_GRID) {
			view = LayoutInflater.from(act).inflate(R.layout.voice_grid_item,
					parent, false);
			return new GridViewHolder(view);
		} else {
			view = LayoutInflater.from(act).inflate(R.layout.voice_line_item,
					parent, false);
			return new ItemHolder(view);
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (v.getTag() == null)
			return;
		Article Item = (Article) v.getTag();
		Intent Intent = new Intent();
		Intent.putExtra("data", JsonUtils.toJson(Item));
		switch (id) {
		case R.id.name: {
			Intent.putExtra("page", BookInfoAct.PAGE_DATA);
		}
		case R.id.icon: {
			LoadingTool.launchActivity(act, BookInfoAct.class, Intent);

		}
			break;
		}

	}
}
