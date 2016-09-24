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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.ReadingTrackItem;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.DateKit;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

public class BoutiqueTrackAdapter extends BaseAdapter implements
		OnClickListener {
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;
	final static String PAGES_CONUT = "%s停至%d章";
	final static String HEAT_TITLE = "人气:%d";
	ArrayList<ReadingTrackItem> books;
	Activity act;
	Drawable bookIcon;
	Drawable musicIcon;
	Drawable videoIcon;
	OnClickListener list;

	public BoutiqueTrackAdapter(Activity act) {
		this.books = new ArrayList<ReadingTrackItem>();
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

	public void upData(ArrayList<ReadingTrackItem> books) {
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
			view = mInflater.inflate(R.layout.boutique_track_item, null);
			holder = new ViewHolder();
			holder.icon = (NetworkImageView) view.findViewById(R.id.icon);
			holder.title = (TextView) view.findViewById(R.id.title);
			holder.author = (TextView) view.findViewById(R.id.author);
			holder.note = (TextView) view.findViewById(R.id.note);
			holder.heat = (TextView) view.findViewById(R.id.heat);
			holder.del = view.findViewById(R.id.del);
			holder.title.setOnClickListener(this);
			holder.del.setOnClickListener(this);
			holder.del.setOnTouchListener(ViewUtils.instance().onTouchListener);
			holder.icon.setOnClickListener(this);
			holder.icon
					.setOnTouchListener(ViewUtils.instance().onTouchListener);
			holder.icon.setDefaultImageResId(R.drawable.icon_default_voice);
			holder.icon.setErrorImageResId(R.drawable.icon_default_voice);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		ReadingTrackItem item = books.get(position);
		holder.title.setText(item.getAName());
		holder.author.setText(item.getAAuthor());
		holder.heat.setText(String.format(HEAT_TITLE, item.getClickConut()));
		holder.note.setText(String.format(PAGES_CONUT,
				DateKit.friendlyFormat(item.getTime()), item.getPos() + 1));
		holder.del.setTag(position);
		holder.icon.setTag(position);
		holder.title.setTag(position);
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
		View del;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (null != v.getTag()) {
			int position = (Integer) v.getTag();
			switch (id) {
			case R.id.title:
			case R.id.icon: {
				Article item = books.get(position);
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
				break;
			}
			case R.id.del: {
				if (list != null)
					list.onClick(v);

			}
				break;
			}
		}

	}

	public void addDelListener(OnClickListener list) {
		this.list = list;
	}

}
