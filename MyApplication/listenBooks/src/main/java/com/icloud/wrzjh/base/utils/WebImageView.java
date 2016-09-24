package com.icloud.wrzjh.base.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.wrzjh.base.utils.io.IOUtils;

public class WebImageView extends ImageView {

	private final static String TAG = "WebImageView";

	public WebImageView(Context context) {
		super(context);
	}

	public WebImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public static synchronized void getNetImage(final String url,
			final File cacheFile, final ImageView icon) {
		if (url == null)
			return;

		Bitmap bmp = null;
		File dir = cacheFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (cacheFile.exists() && cacheFile.length() > 0) {
			bmp = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
			if (bmp != null) {
				icon.setImageBitmap(bmp);
				return;
			} else {
				try {
					cacheFile.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (!cacheFile.getParentFile().exists()) {
			ThreadPoolFactory.getInstance().execute(new Runnable() {
				public void run() {
					InputStream is = null;
					int netLenth = -1;
					try {
						URL mUrl = new URL(url);
						HttpURLConnection conn = null;
						conn = (HttpURLConnection) mUrl.openConnection();
						netLenth = conn.getContentLength();
						if (netLenth < 60 * 1024) {
							is = conn.getInputStream();
							final Bitmap bmp = BitmapFactory.decodeStream(is);
							icon.setImageBitmap(bmp);
							LogUtil.i(TAG, "draw able ");
						}
					} catch (Exception e) {
						Log.e(TAG, "setImageUrl error", e);
					} finally {
						IOUtils.closeQuietly(is);
					}
				}
			});
		} else {
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
								final Bitmap bmp = BitmapFactory
										.decodeFile(cacheFile.getAbsolutePath());
								icon.setImageBitmap(bmp);
							}
						}
					} catch (Exception e) {
						Log.e(TAG, "setImageUrl error", e);
						if ((netLenth != -1)
								&& (cacheFile.length() != netLenth)) {
							cacheFile.delete();
						}
					} finally {
						IOUtils.closeQuietly(is);
					}
				}
			});
		}
	}

	public void updateImg(Drawable d, File file) {
		try {
			file.deleteOnExit();
			FileOutputStream out = new FileOutputStream(file);

			Bitmap bmp = ((BitmapDrawable) d).getBitmap();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param url
	 *            图片的URL地址
	 * @param cacheFile
	 *            图片第一次下载后，存到本地文件，下次不用网络下载
	 */
	public void setImageUrl(final String url, final File cacheFile) {

		if (url == null) {
			nullrLoad();
			return;
		}

		File dir = cacheFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (cacheFile.exists() && cacheFile.length() > 0) {
			final Drawable bmp = BitmapDrawable.createFromPath(cacheFile
					.getAbsolutePath());
			if (bmp != null) {
				afterLoad(bmp);
				return;
			} else {
				try {
					cacheFile.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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
									afterLoad(bmp);
								}
							});
						}
					} else {
						HandlerUtils.instance().post(new Runnable() {
							public void run() {
								nullrLoad();
							}
						});
					}

				} catch (Exception e) {
					Log.e(TAG, "setImageUrl error", e);
					if ((netLenth != -1) && (cacheFile.length() != netLenth)) {
						cacheFile.delete();
					}
					HandlerUtils.instance().post(new Runnable() {
						public void run() {
							nullrLoad();
						}
					});
				} finally {
					IOUtils.closeQuietly(is);
				}
			}
		});
	}

	private void setImageUrl(final String url) {
		LogUtil.d("", "web image url  " + url);
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
							afterLoad(bmp);
						}
					});

				} catch (Exception e) {
					Log.e(TAG, "setImageUrl error", e);
					HandlerUtils.instance().post(new Runnable() {
						public void run() {
							nullrLoad();
						}
					});
				} finally {
					IOUtils.closeQuietly(is);
				}
			}
		});
	}

	public void nullrLoad() {
		LogUtil.i(TAG, "draw able  null ");
	}

	private void afterLoad(Drawable bmp) {
		setBackgroundDrawable(bmp);
	}

	public void setImageBackground(Drawable bmp) {
		setBackgroundDrawable(bmp);
	}
}
