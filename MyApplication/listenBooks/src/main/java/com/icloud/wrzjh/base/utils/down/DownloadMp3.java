package com.icloud.wrzjh.base.utils.down;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.R.integer;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.widget.Toast;

public class DownloadMp3 {
	
	Context context;
	int length;

	public DownloadMp3() {
		
	}
	public DownloadMp3(Context pptAct) {
		context=pptAct;
	}
	public void down() {
		// LogUtil.e("下载","" +"Environment.getExternalStorageState()");
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
//			long availableSize = availableSize();
//			int downloadLength = downloadFileSize(Url);
			// 判断手机内存是否够下载文件
//			if (availableSize >= downloadLength) {
//
//				
//			}
		}
	}

	

	

	public boolean downPPT(String url, final Handler handler, final int type) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String strm[] = url.split("/");
			String name = strm[strm.length - 1];
			final String path = Environment.getExternalStorageDirectory()
					+ "/com.icloud.listenbook/" + name;
			File file = new File(path);
			if (file.exists()) {
				Message msg = Message.obtain();
				msg.obj = path;
				msg.what = 1;
				handler.sendMessage(msg);
			} else {
				HttpUtils utils = new HttpUtils();
				utils.download(url, path, true, true,
						new RequestCallBack<File>() {
							@Override
							public void onStart() {
								super.onStart();
								
							}

							public void onLoading(long total, long current,
									boolean isUploading) {
								super.onLoading(total, current, isUploading);
								
							}

							public void onSuccess(ResponseInfo<File> arg0) {
								if (type == 0) {
									Message msg = Message.obtain();
									msg.obj = path;
									msg.what = 1;
									handler.sendMessage(msg);
								} else if (type == 1) {
									Message msg = Message.obtain();
									msg.obj = path;
									msg.what = 2;
									handler.sendMessage(msg);
								} else if (type == 2) {
									Message msg = Message.obtain();
									msg.obj = path;
									msg.what = 3;
									handler.sendMessage(msg);
								}
							}

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
							}
						});
			}
			return true;
		} else {
			handler.sendEmptyMessage(0);
			return false;
		}
	}

}
