package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

public class BoutiqueCollectAdapter extends BaseAdapter implements
		OnClickListener {
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;
	final static String PAGES_CONUT = "章节:%d";
	final static String HEAT_TITLE = "人气:%d";
	ArrayList<Article> books;
	Activity act;
	Drawable bookIcon;
	Drawable musicIcon;
	Drawable videoIcon;

	public BoutiqueCollectAdapter(Activity act) {
		this.books = new ArrayList<Article>();
		this.act = act;
		bookIcon = getDrawable(R.drawable.icon_item_book);
		musicIcon = getDrawable(R.drawable.icon_item_music);
		videoIcon = getDrawable(R.drawable.icon_item_video);
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	protected Drawable getDrawable(int id) {
		Drawable drawable = act.getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		return drawable;
	}

	public void upData(ArrayList<Article> books) {
		if (this.books != books) {
			this.books.clear();
			this.books.addAll(books);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return books.size();
	}

	@Override
	public Object getItem(int arg0) {
		return books.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			LayoutInflater mInflater = LayoutInflater.from(act);
			view = mInflater.inflate(R.layout.boutique_collect_item, null);
			holder = new ViewHolder();
			holder.icon = (NetworkImageView) view.findViewById(R.id.icon);
			holder.title = (TextView) view.findViewById(R.id.title);
			holder.author = (TextView) view.findViewById(R.id.author);
			holder.note = (TextView) view.findViewById(R.id.note);
			holder.heat = (TextView) view.findViewById(R.id.heat);
			holder.collectItem = view.findViewById(R.id.collect_item);
			holder.collectItem
					.setOnTouchListener(ViewUtils.instance().onTouchListener);
			holder.collectItem.setOnClickListener(this);
			holder.icon.setDefaultImageResId(R.drawable.icon_default_voice);
			holder.icon.setErrorImageResId(R.drawable.icon_default_voice);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Article item = books.get(position);
		holder.title.setText(item.getAName());
		holder.author.setText(item.getAAuthor());
		holder.heat.setText(String.format(HEAT_TITLE, item.getClickConut()));
		holder.note.setText(String.format(PAGES_CONUT, item.getChapterNum()));
		holder.collectItem.setTag(position);
		holder.icon.setImageUrl(urlHead + item.getAIcon(), imageLoader);

		Drawable left;

		switch (item.getMedia()) {
		default:
			left = bookIcon;
			break;
		case Configuration.TYPE_BOOK:
			left = bookIcon;
			break;
		case Configuration.TYPE_VOICE:
			left = musicIcon;
			break;
		case Configuration.TYPE_VEDIO:
			left = videoIcon;
			break;
		}
		holder.title.setCompoundDrawables(left, null, null, null);

		return view;
	}

	class ViewHolder {
		NetworkImageView icon;
		TextView title;
		TextView author;
		TextView note;
		TextView heat;
		View collectItem;
	}

	protected void jump(Article item) {
		/* 1表示视频，2音频 3文字 默认为 0 表示 推荐首页* */
		Class<? extends Activity> activity = VedioInfoAct.class;
		if (item.getMedia() == Configuration.TYPE_VEDIO) {
			activity = VedioInfoAct.class;
		} else if (item.getMedia() == Configuration.TYPE_VOICE) {
			activity = VoiceInfoAct.class;
		} else if (item.getMedia() == Configuration.TYPE_BOOK) {
			activity = BookInfoAct.class;
		}
		Intent Intent = new Intent();
		Intent.putExtra("data", JsonUtils.toJson(item));
		LoadingTool.launchActivity(act, activity, Intent);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (null != v.getTag()) {
			int position = (Integer) v.getTag();
			Article item = books.get(position);
			jump(item);
		}

	}
}
