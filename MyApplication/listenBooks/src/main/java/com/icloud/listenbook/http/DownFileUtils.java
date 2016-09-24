package com.icloud.listenbook.http;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class DownFileUtils {

	//
	public static boolean fileIsExist(String path){
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	// 可用存储大于要下载的大小？
	public static boolean canDownload(Context context, String url) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			long availableSize = availableSize();
			int downloadLength = downloadFileSize(context, url);
			if (availableSize >= downloadLength) 
				return true;
		} else {
			Toast.makeText(context, "SD卡未就绪", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	// 获取外部存储可用大小
	private static long availableSize() {
		// 取得SD卡文件路径
		File file = Environment.getExternalStorageDirectory();
		StatFs fs = new StatFs(file.getPath());
		// 获取单个数据块的大小(Byte)
		int blockSize = fs.getBlockSize();
		// 空闲的数据块的数量
		long availableBlocks = fs.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	static int length = 0;

	// 获取需要下载文件的大小
	private static int downloadFileSize(final Context context, final String path) {

		new Thread() {
			public void run() {
				try {
					URL url = new URL(path); // 创建url对象
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection(); // 建立连接
					conn.setRequestMethod("GET"); // 设置请求方法
					conn.setReadTimeout(5000); // 设置响应超时时间
					conn.setConnectTimeout(5000); // 设置连接超时时间
					conn.connect(); // 发送请求
					int responseCode = conn.getResponseCode(); // 获取响应码
					if (responseCode == 200) { // 响应码是200(固定值)就是连接成功，否者就连接失败
						length = conn.getContentLength(); // 获取文件的大小
					} 
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
		return length;
	}
}
