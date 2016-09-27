package com.icloud.listenbook.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.wrzjh.base.utils.LogUtil;

public class DownloaderTask implements Runnable {

	/**
	 * 标识用户是否取消了该下载任务
	 */
	private volatile boolean isCancel = false;

	private Context mContext;
	private DownloadBean dBean;
	/**
	 * 标识sd卡是否存在
	 */
	private boolean sdCardExist = false;

	public DownloaderTask(Context ctx, DownloadBean db) {
		this.mContext = ctx;
		this.dBean = db;
	}

	/**
	 * 取消该下载任务(关闭下载线程)
	 */
	public void cancel() {
		isCancel = true;
	}

	public void run() {
		sdCardExist = false;

		isCancel = false;

		HttpParams httpParams = new BasicHttpParams(); // 创建HttpParams以用来设置HTTP参数（这一部分不是必需的）
		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000); // 设置连接超时
		HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000); // 设置Socket超时
		HttpConnectionParams.setSocketBufferSize(httpParams, 8 * 1024); // Socket数据缓存默认8K
		HttpConnectionParams.setTcpNoDelay(httpParams, false);
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
		HttpClientParams.setRedirecting(httpParams, true);
		HttpClient client = new DefaultHttpClient(httpParams);

		OutputStream out = null;
		InputStream is = null;

		try {
			File f = new File(dBean.getSaveDir());
			File parentDir = f.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}

			if (!f.exists()) {
				f.createNewFile();
				sdCardExist = true;
			} else {
				f.delete();
				f.createNewFile();
			}

			// if (f.length() == dBean.getTotal_size()) {
			// f.delete();
			// f.createNewFile();
			// }
			/* 如果文件存在，则创建可追加的文件流(斷点续传) */
			out = new FileOutputStream(f, true);

			HttpGet getRequest = new HttpGet(dBean.getUrl());
			getRequest.addHeader("Range", "bytes=" + (f.length()) + "-");

			HttpResponse response = client.execute(getRequest);

			LogUtil.d("", "返回 * " + response.getProtocolVersion().toString()
					+ response.getStatusLine().getStatusCode());
			for (int i = 0; i < (response.getAllHeaders()).length; i++) {
				LogUtil.d("",
						"返回 * " + (response.getAllHeaders())[i].toString());
			}

			long total;
			if (response.getEntity().getContentLength() > 0) {
				LogUtil.d("", "剩余内容 -- "
						+ response.getEntity().getContentLength());
				total = response.getEntity().getContentLength() + f.length();
			} else {
				total = dBean.getTotal_size();
			}
			LogUtil.d("", "DownloaderTask  total size total&&& " + total);
			LogUtil.i("文件 total is  ***    " + total);
			if (total <= 0) {
				HandlerUtils.instance().post(new Runnable() {
					public void run() {
						// ToastUtil.showMessage(R.string.dmr_file_not_exsit);
					}
				});
			}

			is = response.getEntity().getContent();
			long current = f.length();

			int progress = (int) ((current * 100) / total);

			long step = total / 100;
			byte buf[] = new byte[4 * 1024];
			do {
				/* 判断用户是否已取消下载 */
				if (isCancel) {
					return;
				}
				/* 如果下载过程中用户删除文件，删除数据库记录,停止下载 */
				if (!f.exists()) {
					return;
				}

				int numread = is.read(buf);
				if (numread <= 0) {
					break;
				}
				current += numread;
				out.write(buf, 0, numread);
				/* 每个1%进度的最后一字节更新一次进度 */
				if (current % step < 1024) {
					progress = (int) (current * 100 / total);
					dBean.progress = progress;
				}
			} while (true);

			if (current >= total) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File(dBean.getSaveDir())),
						"application/vnd.android.package-archive");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				GameApp.instance().startActivity(intent);
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				/* 当出现莫名其妙出现下载异常时如果下载没有完成 */
				if (is != null) {
					is.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public DownloadBean getdBean() {
		return dBean;
	}

	public void setdBean(DownloadBean dBean) {
		this.dBean = dBean;
	}

}
