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
import com.icloud.listenbook.entity.TabItemInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.adapter.entity.ArticleItem;
import com.icloud.listenbook.ui.adapter.entity.PinterestLikeItem;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.BookListAct;
import com.icloud.listenbook.ui.chipAct.PhotoViewAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VedioListAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceListAct;
import com.icloud.listenbook.ui.chipAct.WebAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

public class HomeMediaAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	String TAG=getClass().getSimpleName();
	ArrayList<ArticleItem> data;
	ImageLoader imageLoader;
	String urlHead;
	Activity act;
    private final static String[] Abstract={"视频","音频","文字"};
	public HomeMediaAdapter(Activity act) {
		this.data = new ArrayList<ArticleItem>();
		this.act = act;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);

	}

	public HomeMediaAdapter(Activity act, ArrayList<ArticleItem> data) {
		this.data = data;
		this.act = act;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);

	}

	public void upData(ArrayList<ArticleItem> data) {
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
	public int getSpanSize(int pos) {
		ArticleItem item=null;
		if(pos<data.size()){
		    item = data.get(pos);
		}else{
			return 1;
		}
		switch (item.getViewType()) {
		case Type.TABLE_GRID:
			return 1;		
		case Type.TABLE_TITLE: 
		case Type.TABLE_END:
			return 4;
		}
		return 1;
	}
	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {
		if (viewHolder instanceof ItemHolder) {
			Article item = data.get(pos);
			ItemHolder holder = (ItemHolder) viewHolder;
			holder.setView(item);
		}else if(viewHolder instanceof TitleViewHolder){
			Article item = data.get(pos);
			TitleViewHolder holder = (TitleViewHolder) viewHolder;
			holder.setView(item);
		}
	}
	
	public int getItemViewType(int pos) {
		return data.get(pos).viewType;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		//LogUtil.e(getClass().getSimpleName(), "viewType"+viewType);
		if (viewType == Type.TABLE_GRID) {
			View view = LayoutInflater.from(act).inflate(
					R.layout.homemedia_grid_item, parent, false);
			return new ItemHolder(view);
		} else if(viewType == Type.TABLE_TITLE){
			View view = LayoutInflater.from(act).inflate(
					R.layout.recommen_title_item, parent, false);
			return new TitleViewHolder(view);
		} else{
			View view = LayoutInflater.from(act).inflate(
					R.layout.homemedia_footer_item, parent, false);
			return new FooterViewHolder(view);
		}
	}
	public class FooterViewHolder extends RecyclerView.ViewHolder{

		public FooterViewHolder(View arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}
		
	}
	public class TitleViewHolder extends RecyclerView.ViewHolder {
		TextView tv_title;

		public TitleViewHolder(View view) {
			super(view);
			// TODO Auto-generated constructor stub
			tv_title = (TextView) view.findViewById(R.id.name);
		}

		public void setView(Article item) {
			tv_title.setText(item.getAName());
		}
	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		NetworkImageView icon;
		TextView title;

		public ItemHolder(View view) {
			super(view);
			title = (TextView) view.findViewById(R.id.title);
			title.setOnClickListener(this);
			title.setOnTouchListener(ViewUtils.instance().onTouchListener);
		}

		public void setView(Article item) {
//			LogUtil.e(TAG, item.getAName());
			title.setText(item.getAName());
			title.setTag(item);
		}

		@Override
		public void onClick(View v) {
			ArticleItem item = (ArticleItem) v.getTag();
			if (item != null) {
				Article article = new Article();
				article.setAAbstract(item.getAAbstract());
				article.setAAuthor(item.getAAuthor());
				article.setAIcon(item.getAIcon());
				article.setAId(item.getAId());
				article.setAName(item.getAName());
				article.setClickConut(0);
				article.setChapterNum(item.getChapterNum());
				article.setMedia((int)item.getMedia());
				Intent Intent = new Intent();
				Intent.putExtra("data", JsonUtils.toJson(article));
//				LogUtil.e(TAG, "item.getAId():"+item.getAId());
//                LogUtil.e(TAG, "Abstract:"+item.getAAbstract());
				Class<? extends Activity> activity = VedioInfoAct.class;
				if (item.getAAbstract().startsWith(Abstract[0])) {
//					article.setAAbstract( item.getAAbstract().substring(2));
//					LogUtil.e(TAG, "Abstract:"+item.getAAbstract());
//					LogUtil.e(TAG, "Abstract:"+article.getAAbstract());
					activity = VedioInfoAct.class;
				} else if (item.getAAbstract().startsWith(Abstract[1])) {
//					article.setAAbstract( item.getAAbstract().substring(2));
					activity = VoiceInfoAct.class;
				} else if (item.getAAbstract().startsWith(Abstract[2])) {
//					article.setAAbstract( item.getAAbstract().substring(2));
					activity = BookInfoAct.class;
				}
				LoadingTool.launchActivity(act, activity, Intent);
			}

		}
	}
}
