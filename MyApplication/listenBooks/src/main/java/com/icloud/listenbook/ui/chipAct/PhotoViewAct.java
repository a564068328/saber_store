package com.icloud.listenbook.ui.chipAct;

import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.photoview.PhotoView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.unit.LruImageCache;

public class PhotoViewAct extends BaseActivity {
	PhotoView ivPhoto;
	String url;
	ImageLoader imageLoader;

	@Override
	public void init() {
		url = this.getIntent().getStringExtra("url");
		RequestQueue mQueue = Volley.newRequestQueue(this);
		LruImageCache lruImageCache = LruImageCache.instance(this
				.getApplicationContext());

		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	@Override
	public int getLayout() {
		return R.layout.act_photoview;
	}

	@Override
	public void findViews() {
		ivPhoto = (PhotoView) findViewById(R.id.iv_photo);
	}

	@Override
	public void setListeners() {
		ivPhoto.setDefaultImageResId(R.drawable.icon_default_img);
		ivPhoto.setErrorImageResId(R.drawable.icon_default_img);
		if (!TextUtils.isEmpty(url)) {
			ivPhoto.setImageUrl(url, imageLoader);
		}
	}

}
