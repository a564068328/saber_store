package com.icloud.listenbook.unit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jakewharton.disklrucache.DiskLruCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageDiskCache {
	final String TAG = "ImageDiskCache";
	/**
	 * Default disk cache size 10MB
	 */
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 32;
	private final int appVersion = 1007;
	private DiskLruCache mDiskLruCache;
	private File cacheDir;
	public static final String DEFA_CACHE_NAME = "ImageDiskCache";
	/**
	 * Compression settings when writing images to disk cache
	 */
	private static final CompressFormat COMPRESS_FORMAT = CompressFormat.PNG;
	/**
	 * Image compression quality
	 */
	private static final int COMPRESS_QUALITY = 98;
	/**
	 * Disk cache index to read from
	 */
	private static ImageDiskCache imageDiskCache;
	private static final int DISK_CACHE_INDEX = 0;
	private final Object mDiskCacheLock = new Object();

	public static ImageDiskCache instance(Context context) {
		if (imageDiskCache == null) {
			imageDiskCache = new ImageDiskCache(context);

		}
		return imageDiskCache;
	}

	private ImageDiskCache(Context context) {
		this(context, DEFA_CACHE_NAME);

	}

	public ImageDiskCache(Context context, String FileName) {
		/*
		 * 如果有SD卡则在SD卡中建一个LazyList的目录存放缓存的图片 // 没有SD卡就放在系统的缓存目录中
		 */
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory()
							+ File.separator + context.getPackageName(),
					FileName);
		else
			try {
				cacheDir = File.createTempFile(FileName, null,
						context.getCacheDir());
			} catch (IOException e) {

			}
		if (cacheDir != null && !cacheDir.exists())
			cacheDir.mkdirs();
		synchronized (mDiskCacheLock) {
			if (cacheDir != null || mDiskLruCache == null
					|| mDiskLruCache.isClosed())
				try {

					mDiskLruCache = DiskLruCache.open(cacheDir, appVersion, 1,
							DISK_CACHE_SIZE);
				} catch (IOException e) {
					cacheDir = null;
					mDiskLruCache = null;
				}
			mDiskCacheLock.notifyAll();
		}
	}

	public boolean addBitmapToCache(final String url, final Bitmap bitmap) {
		if (url == null || bitmap == null || cacheDir == null
				|| mDiskLruCache == null)
			return false;

		final String key = String.valueOf(url.hashCode());
		OutputStream out = null;
		synchronized (mDiskCacheLock) {
			try {
				final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
				if (snapshot == null) {
					final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {

						out = editor.newOutputStream(DISK_CACHE_INDEX);
						bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, out);
						editor.commit();

					}
				} else {
					snapshot.getInputStream(DISK_CACHE_INDEX).close();
				}
			} catch (final IOException e) {
				Log.e(TAG, "addBitmapToCache - " + e);
			} finally {
				try {
					if (out != null) {
						out.close();
						out = null;
					}
				} catch (final IOException e) {
					Log.e(TAG, "addBitmapToCache - " + e);
				} catch (final IllegalStateException e) {
					Log.e(TAG, "addBitmapToCache - " + e);
				}
			}
		}
		return true;
	}

	public Bitmap getBitmapFromDiskCache(String url) {
		if (cacheDir == null || mDiskLruCache == null || url == null)
			return null;
		final String key = String.valueOf(url.hashCode());
		InputStream inputStream = null;
		DiskLruCache.Snapshot snapshot;
		try {
			snapshot = mDiskLruCache.get(key);

			if (snapshot != null) {
				inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
				if (inputStream != null) {
					final Bitmap bitmap = BitmapFactory
							.decodeStream(inputStream);
					if (bitmap != null) {
						return bitmap;
					}
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "getBitmapFromDiskCache - " + e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (final IOException e) {
			}
		}
		return null;

	}

	public void clearDiskCache() {
		synchronized (mDiskCacheLock) {

			if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
				try {
					mDiskLruCache.delete();
				} catch (IOException e) {
					Log.e(TAG, "clearCache - " + e);
				}

			}
		}
	}

}