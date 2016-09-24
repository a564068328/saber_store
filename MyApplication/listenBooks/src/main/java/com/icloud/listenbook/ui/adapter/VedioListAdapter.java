package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
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
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.listenBook.greendao.Article;

public class VedioListAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> implements
		OnClickListener {
	private ArrayList<ArticleItem> datas;
	Activity act;
	ImageLoader imageLoader;
	String urlHead;
	final FooterManage footerManage;

	public VedioListAdapter(Activity act, ArrayList<ArticleItem> datas) {
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
		TextView aAbstract;

		public GridViewHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			aAbstract = (TextView) view.findViewById(R.id.aAbstract);
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			icon.setOnClickListener(VedioListAdapter.this);
		}

		public void setView(ArticleItem item) {
			if (item.getAId() != -1) {
				name.setVisibility(View.VISIBLE);
				aAbstract.setVisibility(View.GONE);
				icon.setVisibility(View.VISIBLE);
				name.setText(item.getAName());
				aAbstract.setText(item.getAAbstract());
				icon.setTag(item);
				icon.setImageUrl(urlHead + item.getAIcon(), imageLoader);
				icon.setDefaultImageResId(R.drawable.icon_default_img);
				icon.setErrorImageResId(R.drawable.icon_default_img);
			} else {
				name.setVisibility(View.INVISIBLE);
				aAbstract.setVisibility(View.GONE);
				icon.setVisibility(View.INVISIBLE);
			}
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
		NetworkImageView icon;
		TextView name;

		public ItemHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			icon.setOnClickListener(VedioListAdapter.this);
		}

		public void setView(ArticleItem item) {
			name.setText(item.getAName());
			icon.setTag(item);
			icon.setImageUrl(urlHead + item.getAIcon(), imageLoader);
			icon.setDefaultImageResId(R.drawable.icon_default_img);
			icon.setErrorImageResId(R.drawable.icon_default_img);

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
			holder.setView(item);
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
			return 2;
		}
		ArticleItem item = datas.get(pos);
		switch (item.viewType) {
		case Type.TABLE_ITEM:
		case Type.TABLE_TITLE:
			return 2;
		case Type.TABLE_GRID:
			return 1;
		}
		return 1;
	}

	public void setStatus(int status) {
		if (footerManage != null) {
			if (status == FooterManage.NULL_DATAS && datas.size() == 0)
				status = FooterManage.NULL_DATAS;
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
			view = LayoutInflater.from(act).inflate(R.layout.vedio_title_item,
					parent, false);
			return new TitleHolder(view);
		} else if (viewType == Type.TABLE_GRID) {
			view = LayoutInflater.from(act).inflate(R.layout.vedio_grid_item,
					parent, false);
			return new GridViewHolder(view);
		} else {
			view = LayoutInflater.from(act).inflate(R.layout.vedio_line_item,
					parent, false);
			return new ItemHolder(view);
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.icon: {
			if (v.getTag() != null) {
				Article Item = (Article) v.getTag();
				Intent Intent = new Intent();
				Intent.putExtra("data", JsonUtils.toJson(Item));
				LoadingTool.launchActivity(act, VedioInfoAct.class, Intent);
			}
		}
			break;
		}

	}

	class SampleDivider extends RecyclerView.ItemDecoration {

		// 构造函数初始化
		public SampleDivider() {

		}

		@Override
		public void getItemOffsets(Rect outRect, int itemPosition,
				RecyclerView parent) {
			int type = getItemViewType(itemPosition);
			if (type == Type.TABLE_FOOTER) {
				outRect.set(0, 0, 0, 0);
			} else if (type == Type.TABLE_GRID) {

				switch (itemPosition % 2) {
				case 0:
					outRect.set(3, 4, 10, 4);
					break;
				case 1:
					outRect.set(10, 4, 3, 4);
					break;
				}

			} else {
				outRect.set(10, 0, 10, 0);
			}
		}
	}

	public ItemDecoration getDivider() {
		return new SampleDivider();
	}

}
