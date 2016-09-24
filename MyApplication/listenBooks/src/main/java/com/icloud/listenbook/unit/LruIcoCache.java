package com.icloud.listenbook.unit;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.android.volley.toolbox.ImageLoader.ImageCache;
/*
 * 图片缓存    
 */
public class LruIcoCache implements ImageCache {
	private static LruCache<String, Bitmap> mMemoryCache;
	private static final int MEMORY_SCALE = 15;
	// 设置为可用内存的十五分之一
	private static LruIcoCache lruImageCache;
	final int maxMemory;
	ImageDiskCache imageDiskCache;

	private LruIcoCache(Context context) {
		// Get the Max available memory
		maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		mMemoryCache = new LruCache<String, Bitmap>(maxMemory / MEMORY_SCALE) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
		imageDiskCache = ImageDiskCache.instance(context);
	}

	public static LruIcoCache instance(Context context) {
		if (lruImageCache == null) {
			lruImageCache = new LruIcoCache(context);
		}
		return lruImageCache;
	}

	@Override
	public Bitmap getBitmap(String url) {
		Bitmap bitmap = mMemoryCache.get(url);
		if (bitmap == null)
			bitmap = imageDiskCache.getBitmapFromDiskCache(url);
		if (bitmap != null)
			mMemoryCache.put(url, bitmap);
		return bitmap;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// if (getBitmap(url) == null)
		if (!TextUtils.isEmpty(url) && bitmap != null) {
			mMemoryCache.put(url, bitmap);
			imageDiskCache.addBitmapToCache(url, bitmap);

		}

	}
}
