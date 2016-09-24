package com.icloud.wrzjh.base.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.wrzjh.base.utils.io.IOUtils;

public class WebImage extends TextView {

	private final static String TAG = "WebImageView";

	public WebImage(Context context) {
		super(context);
	}

	public WebImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param url
	 *            图片的URL地址
	 * @param cacheFile
	 *            图片第一次下载后，存到本地文件，下次不用网络下载
	 */
	public void setImageUrl(final String url, final File cacheFile) {
		LogUtil.v("setImageUrl", url);

		if (url == null) {
			return;
		}

		File dir = cacheFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (cacheFile.exists() && cacheFile.length() > 0) {
			final Drawable bmp = BitmapDrawable.createFromPath(cacheFile
					.getAbsolutePath());
			this.setBackgroundDrawable(bmp);
			return;
		}

		// 判断SD卡是否可用
		if (!cacheFile.getParentFile().exists()) {
			setImageUrl(url);
			return;
		}

		ThreadPoolFactory.getInstance().execute(new Runnable() {
			public void run() {
				InputStream is = null;
				int netLenth = -1;
				try {

					URL mUrl = new URL(url);
					HttpURLConnection conn = null;
					conn = (HttpURLConnection) mUrl.openConnection();
					Log.i("syy", mUrl + " conn:" + conn.getContentLength());
					netLenth = conn.getContentLength();
					is = conn.getInputStream();

					if (netLenth < 60 * 1024) {
						IOUtils.copy(is, new FileOutputStream(cacheFile));
						if (cacheFile.length() > 0) {
							final Drawable bmp = BitmapDrawable
									.createFromPath(cacheFile.getAbsolutePath());
							HandlerUtils.instance().post(new Runnable() {
								public void run() {
									setBackgroundDrawable(bmp);
								}
							});
						}
					} else {
					}

				} catch (Exception e) {
					Log.e(TAG, "setImageUrl error", e);
					if ((netLenth != -1) && (cacheFile.length() != netLenth)) {
						cacheFile.delete();
					}
				} finally {
					IOUtils.closeQuietly(is);
				}
			}
		});
	}

	private void setImageUrl(final String url) {
		LogUtil.v("", "web image url  " + url);
		ThreadPoolFactory.getInstance().execute(new Runnable() {
			public void run() {
				InputStream is = null;
				try {

					URL mUrl = new URL(url);
					HttpURLConnection conn = null;
					conn = (HttpURLConnection) mUrl.openConnection();

					is = conn.getInputStream();

					final Drawable bmp = BitmapDrawable.createFromStream(is,
							"src");

					LogUtil.i(TAG, "draw able ");
					HandlerUtils.instance().post(new Runnable() {
						public void run() {
							setBackgroundDrawable(bmp);
						}
					});
				} catch (Exception e) {
					Log.e(TAG, "setImageUrl error", e);
				} finally {
					IOUtils.closeQuietly(is);
				}
			}
		});
	}

}
