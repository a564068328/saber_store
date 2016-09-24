package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.BookListAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VedioListAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceListAct;
import com.icloud.listenbook.ui.chipAct.WebAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.listenBook.greendao.Ads;
import com.listenBook.greendao.Article;

public class LedPagerAdapter extends PagerAdapter implements OnClickListener {
	private List<NetworkImageView> mImageViewList;
	private Activity context;
	String urlHead;
	ImageLoader imageLoader;

	public LedPagerAdapter(Activity context) {
		this.context = context;
		mImageViewList = new ArrayList<NetworkImageView>();
		RequestQueue mQueue = Volley.newRequestQueue(context);
		LruImageCache lruImageCache = LruImageCache.instance(context
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);

	}

	public void upData(ArrayList<Ads> asdInfo) {
		mImageViewList.clear();
		for (Ads ads : asdInfo) {
			mImageViewList.add(createImgView(ads));
		}
		this.notifyDataSetChanged();
	}

	public NetworkImageView createImgView(Ads asdInfo) {
		NetworkImageView icon = new NetworkImageView(context);
		icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
		icon.setImageUrl(urlHead + asdInfo.getIcon(), imageLoader);
		icon.setDefaultImageResId(R.drawable.icon_default_img);
		icon.setErrorImageResId(R.drawable.icon_default_img);
		icon.setTag(asdInfo);
		icon.setOnClickListener(this);
		return icon;
	}

	@Override
	public int getCount() {
		return mImageViewList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mImageViewList.get(position));
		return mImageViewList.get(position);
	}

	@Override
	public void onClick(View v) {
		if (null == v.getTag())
			return;
		if (v.getTag() instanceof Ads) {
			Ads item = (Ads) v.getTag();
			Intent intent = new Intent();
			Article article = new Article();
			article = new Article();
			try {
				article.setAId(Long.parseLong(item.getUrl()));
			} catch (Exception e) {
			}
			Class<? extends Activity> activity = VedioListAct.class;
			if (item.getMedia() == Configuration.TYPE_VEDIO) {
				activity = VedioInfoAct.class;
				intent.putExtra("isPlay", true);
			} else if (item.getMedia() == Configuration.TYPE_VOICE) {
				activity = VoiceInfoAct.class;
			} else if (item.getMedia() == Configuration.TYPE_BOOK) {
				activity = BookInfoAct.class;
			} else if (item.getMedia() == Configuration.TYPE_WEBVIEW) {
				// activity = WebAct.class;
				// String url = item.getUrl();
				// if (TextUtils.isEmpty(url))
				// return;
				// if (url.startsWith("http://") || url.startsWith("www.")
				// || url.startsWith("https://")) {
				// intent.putExtra("url", url);
				// } else {
				// return;
				// }
				String url = item.getUrl();
				if (TextUtils.isEmpty(url))
					return;
				if (url.startsWith("http://") || url.startsWith("www.")
						|| url.startsWith("https://")) {
					Uri uri = Uri.parse(url);
					intent = new Intent("android.intent.action.VIEW");
					intent.setData(uri);
					context.startActivity(intent);
				}
				return;

			}
			intent.putExtra("data", JsonUtils.toJson(article));
			LoadingTool.launchActivity(context, activity, intent);
		}
	}
}
