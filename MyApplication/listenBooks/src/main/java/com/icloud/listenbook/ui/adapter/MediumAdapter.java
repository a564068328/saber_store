package com.icloud.listenbook.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
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
import com.icloud.listenbook.entity.TabItemInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.ui.chipAct.BookListAct;
import com.icloud.listenbook.ui.chipAct.GreatMaster;
import com.icloud.listenbook.ui.chipAct.HomeMediaAct;
import com.icloud.listenbook.ui.fragment.Msecond;
import com.icloud.listenbook.ui.chipAct.VedioListAct;
import com.icloud.listenbook.ui.chipAct.VoiceListAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.ViewUtils;

import java.util.ArrayList;

public class MediumAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	Activity act;
	Resources res;
//	HeadViewHolder headViewHolder;
	LruIcoCache lruIcoCache;
	private ArrayList<TabItemInfo> datasetSizes;
//	ArrayList<Ads> asdInfo;
	int TitleIconWH;
	int ItemIconWH;
	String urlHead;
	ImageLoader imageLoader;
	View foot_btn;

	public MediumAdapter(Activity act, ArrayList<TabItemInfo> sizes) {
//		this.asdInfo = asdInfo;
		this.datasetSizes = sizes;
		this.act = act;
		res = act.getResources();
		lruIcoCache = LruIcoCache.instance(act.getApplicationContext());
		TitleIconWH = ViewUtils.dip2px(18, act);
		ItemIconWH = ViewUtils.dip2px(24, act);
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);

	}

	public void upData(ArrayList<TabItemInfo> sizes) {
		if (this.datasetSizes != sizes) {
			datasetSizes.clear();
			datasetSizes.addAll(sizes);
			this.notifyDataSetChanged();
		}
	}

	public void setDatasetSizes(ArrayList<TabItemInfo> datasetSizes) {
		this.datasetSizes = datasetSizes;
	}

	@Override
	public int getItemViewType(int pos) {
		return datasetSizes.get(pos).viewType;
	}

	public int getSpanSize(int pos) {
		TabItemInfo item = null;
		if (pos < datasetSizes.size()) {
			item = datasetSizes.get(pos);
		} else {
			// item = new TabItemInfo(0,null,null,0,0);
			// item.viewType=Type.TABLE_BESTFOOTER;
			return 3;
		}
		switch (item.viewType) {
		case Type.TABLE_HEADER:
		case Type.TABLE_TITLE:
		case Type.TABLE_FOOTER:
			return 6;
		case Type.TABLE_GRID_LEFT:
		case Type.TABLE_GRID_RIGHT:
		case Type.TABLE_BESTFOOTER: // 底部VIEW
			return 3;
		case Type.TABLE_GRID_L:
		case Type.TABLE_GRID_M:
		case Type.TABLE_GRID_R:
			return 2;
		}
		return 2;
	}

	@Override
	public int getItemCount() {
		return datasetSizes.size();
	}

	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		View view;
		if (viewType == Type.TABLE_FOOTER) {
			view = LayoutInflater.from(act).inflate(R.layout.home_end_item,
					parent, false);
			// view=null;
			return new EndHolder(view);
		}
		else if (viewType == Type.TABLE_TITLE) {
			view = LayoutInflater.from(act).inflate(R.layout.home_grid_header,
					parent, false);
			return new TitleViewHolder(view);
		} else if (viewType == Type.TABLE_GRID_LEFT
				|| viewType == Type.TABLE_GRID_L) {
			view = LayoutInflater.from(act).inflate(
					R.layout.home_grid_left_item, parent, false);
			return new ViewHolder(view);
		} else if (viewType == Type.TABLE_GRID_M) {
			view = LayoutInflater.from(act).inflate(R.layout.home_grid_m_item,
					parent, false);
			return new ViewHolder(view);
		} else if (viewType == Type.TABLE_GRID_RIGHT
				|| viewType == Type.TABLE_GRID_R) {
			view = LayoutInflater.from(act).inflate(
					R.layout.home_grid_right_item, parent, false);
			return new ViewHolder(view);
		} else {
			view = LayoutInflater.from(act).inflate(R.layout.home_grid_item,
					parent, false);
			return new ViewHolder(view);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int pos) {

		TabItemInfo item = datasetSizes.get(pos);
		if (viewHolder instanceof TitleViewHolder) {
			TitleViewHolder headerHolder = (TitleViewHolder) viewHolder;
			headerHolder.setView(item);
		} else if (viewHolder instanceof ViewHolder) {
			ViewHolder holder = (ViewHolder) viewHolder;
			holder.setView(item);
		} else if (viewHolder instanceof EndHolder) {
			EndHolder endHolder = (EndHolder) viewHolder;
			endHolder.Enter();
		}
	}

	public class EndHolder extends RecyclerView.ViewHolder {
		public EndHolder(View view) {
			super(view);
			foot_btn = view.findViewById(R.id.foot_btn);
		}

		public void Enter() {
			foot_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(act, Msecond.class);
					act.startActivity(intent);
				}
			});
		}
	}

	public class TitleViewHolder extends RecyclerView.ViewHolder {
		public TextView title;

		public TitleViewHolder(View v) {
			super(v);
			title = (TextView) v.findViewById(R.id.txt);
		}

		public void setView(TabItemInfo item) {
			title.setText(item.name);
			Drawable icon = null;
			if (!TextUtils.isEmpty(item.icon)) {
				Bitmap bit = lruIcoCache.getBitmap(item.icon);
				if (bit != null) {
					icon = new BitmapDrawable(bit);
					// 必须设置图片大小，否则不显示
					icon.setBounds(0, 0, TitleIconWH, TitleIconWH);
				}
			}
			title.setCompoundDrawables(icon, null, null, null);
		}
	}



	public class ViewHolder extends RecyclerView.ViewHolder implements
			OnClickListener {

		public TextView title;
		NetworkImageView icon;

		public ViewHolder(View v) {
			super(v);
			title = (TextView) v.findViewById(R.id.txt);
			icon = (NetworkImageView) v.findViewById(R.id.icon);
			icon.setDefaultImageResId(R.drawable.icon_default_img);
			icon.setErrorImageResId(R.drawable.icon_default_img);
			title.setOnClickListener(this);
			title.setTag(null);
		}


		public void setView(TabItemInfo item) {

			title.setText(item.name);
			title.setTag(item);
			if (TextUtils.isEmpty(item.name)) {
				icon.setVisibility(View.GONE);
			} else {
				icon.setVisibility(View.VISIBLE);

			}
			icon.setImageUrl(urlHead + item.icon, imageLoader);
		}

		@Override
		public void onClick(View v) {
			TabItemInfo item = (TabItemInfo) v.getTag();
			if (item != null) {
				Intent Intent = new Intent();
				// if(item.type == Configuration.TYPE_GREAT)
				Intent.putExtra("cId", item.id);
				Intent.putExtra("name", item.name);
				// LogUtil.e(getClass().getSimpleName(), "type"+ item.type);
				Intent.putExtra("type", item.type);
				Class<? extends Activity> activity = VedioListAct.class;
				if (item.type == Configuration.TYPE_VEDIO) {
					activity = VedioListAct.class;
				} else if (item.type == Configuration.TYPE_VOICE) {
					activity = VoiceListAct.class;
				} else if (item.type == Configuration.TYPE_BOOK) {
					activity = BookListAct.class;
				} else if (item.type == Configuration.TYPE_GREAT) {
					activity = GreatMaster.class;
				} else {
					activity = HomeMediaAct.class;
				}
				LoadingTool.launchActivity(act, activity, Intent);
			}

		}
	}



}
