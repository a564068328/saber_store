package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
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
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.BoutiqueCollectAdapter.ViewHolder;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.adapter.entity.ArticleItem;
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.down.DownloadManager;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.Down;

public class DownBookAdapter extends BaseAdapter implements OnClickListener {

	static String HEAT_TITLE = "人气:%d";
	static String PAGES_CONUT = "章节:%d";
	ArrayList<Article> books;
	Activity act;
	Drawable bookIcon;
	Drawable musicIcon;
	Drawable videoIcon;
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;

	DelListener delList;

	public DownBookAdapter(Activity act) {
		books = new ArrayList<Article>();
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

	public void upType(ArrayList<Article> books) {
		this.books.clear();
		this.books.addAll(books);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return books.size();
	}

	@Override
	public Article getItem(int arg0) {
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
			view = mInflater.inflate(R.layout.down_book_item, null);
			holder = new ViewHolder();
			holder.icon = (NetworkImageView) view.findViewById(R.id.icon);
			holder.title = (TextView) view.findViewById(R.id.title);
			holder.author = (TextView) view.findViewById(R.id.author);
			holder.note = (TextView) view.findViewById(R.id.note);
			holder.heat = (TextView) view.findViewById(R.id.heat);
			holder.del = view.findViewById(R.id.del);
			holder.del.setOnClickListener(this);
			holder.del.setOnTouchListener(ViewUtils.instance().onTouchListener);
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
		holder.note
				.setText(String.format(PAGES_CONUT, item.getChapterNum() + 1));
		holder.del.setTag(position);
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
		if (null == v.getTag())
			return;
		int id = v.getId();
		int postion = (Integer) v.getTag();
		switch (id) {
		case R.id.del: {
			if (delList != null)
				delList.del(postion);

			break;
		}

		}
	}

	public void addDelListener(DelListener delList) {
		this.delList = delList;
	}

	public interface DelListener {
		public void del(int postion);
	}
}
