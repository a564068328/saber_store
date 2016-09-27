package com.icloud.listenbook.connector;

import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.icloud.listenbook.R;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.unit.LruIcoCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
/*
 * 外部通过instance().downIcon 下载图片     图片下载后这个类通过lruIcoCache.putBitmap(url, response);实现了缓存
 */
public class ImageManage {
	static ImageManage instance;
	LruIcoCache lruIcoCache;
	Context context;
	ImageLoader iconLoader;
	String urlHead;
	RequestQueue mQueue;

	public static ImageManage instance() {
		if (instance == null)
			instance = new ImageManage();
		return instance;
	}

	public ImageManage init(Context context) {
		lruIcoCache = LruIcoCache.instance(context);
		this.context = context.getApplicationContext();
		mQueue = Volley.newRequestQueue(context);
		iconLoader = new ImageLoader(mQueue, lruIcoCache);
		urlHead = ServerIps.getLoginAddr();
		return this;
	}

	public void loadImage() {

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.home_item_tip_00);
		lruIcoCache.putBitmap("/images/vedio.png", bitmap);
		bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.home_item_tip_01);
		lruIcoCache.putBitmap("/images/voice.png", bitmap);
		bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.home_item_tip_02);
		lruIcoCache.putBitmap("/images/book.png", bitmap);
		bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.home_item_00);
		lruIcoCache.putBitmap("/images/fou.png", bitmap);
	}
    /*
     * 通过volley的ImageRequest和RequestQueue下载图片
     */
	public void downIcon(final String url) {
		ImageRequest imageRequest = new ImageRequest(urlHead + url,
				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap response) {
						if (response != null)
							lruIcoCache.putBitmap(url, response);
					}
				}, 0, 0, Config.RGB_565, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		mQueue.add(imageRequest);
		// iconLoader.get(urlHead + url, new ImageListener() {
		// @Override
		// public void onErrorResponse(VolleyError error) {
		//
		// }
		//
		// @Override
		// public void onResponse(ImageContainer c, boolean isImmediate) {
		// c.getBitmap()
		// lruIcoCache.putBitmap(url, c.getBitmap());
		// }
		// });
	}

	public void downImages(ArrayList<String> array) {
		for (String url : array)
			downIcon(url);
	}
}
