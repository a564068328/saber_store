package com.icloud.wrzjh.base.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.icloud.listenbook.base.GameApp;
import com.icloud.wrzjh.base.net.socket.packet.RC4Http;
import com.icloud.wrzjh.base.utils.APNUtil;
import com.icloud.wrzjh.base.utils.LogUtil;

public class HttpSender {
	public static final String TAG = HttpSender.class.getSimpleName();

	public static final int TIMEOUT = 15000;
	private static HttpClient client = null;
	private static HttpPost postRequest = null;
	private static HttpResponse response = null;
	private static byte[] lock = new byte[0];

	/** PHP服务器 RC4 http 请求 **/
	public static String post(String url, Map<String, String> headers,
			String param) {
		synchronized (lock) {
			if (null == client) {
				HttpParams httpParams = new BasicHttpParams(); // 创建HttpParams以用来设置HTTP参数（这一部分不是必需的）
				HttpConnectionParams
						.setConnectionTimeout(httpParams, 20 * 1000); // 设置连接超时
				HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000); // 设置Socket超时
				HttpConnectionParams.setSocketBufferSize(httpParams, 8 * 1024); // Socket数据缓存默认8K
				HttpConnectionParams.setTcpNoDelay(httpParams, false);
				HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
				HttpClientParams.setRedirecting(httpParams, false);
				client = new DefaultHttpClient(httpParams);
				setProxy(client);
			}
			postRequest = null;
			response = null;
			try {
				// 根据PHP情况设置超时
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);

				postRequest = new HttpPost(url);
				if (null != headers) {
					for (Iterator<Map.Entry<String, String>> ite = headers
							.entrySet().iterator(); ite.hasNext();) {
						Map.Entry<String, String> entry = ite.next();
						postRequest.addHeader(entry.getKey(), entry.getValue());
					}
				}

				LogUtil.i(TAG, "请求参数：" + url + "*" + param);

				byte[] b = param.getBytes();
				RC4Http.RC4Base(b, 0, b.length);
				ByteArrayEntity bae = new ByteArrayEntity(b);

				postRequest.setEntity(bae);
				response = client.execute(postRequest);

				int responseCode = response.getStatusLine().getStatusCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					String retStr;

					HttpEntity entity = response.getEntity();

					byte buf[] = EntityUtils.toByteArray(entity);
					LogUtil.i(TAG, "http length : " + buf.length);
					RC4Http.RC4Base(buf, 0, buf.length);
					retStr = new String(buf);

					LogUtil.i(TAG, "http result : " + retStr);
					return retStr;
				} else {
					String fail = "responseCode  * " + responseCode;
					LogUtil.i(TAG, fail);
					return fail;
				}
			} catch (MalformedURLException e) {
				LogUtil.e(TAG, e);
				e.printStackTrace();
			} catch (ProtocolException e) {
				LogUtil.e(TAG, e);
			} catch (ConnectTimeoutException e) {
				LogUtil.e(TAG, e);
			} catch (Exception e) {
				LogUtil.e(TAG, e);
			} finally {
				if (postRequest != null) {
					postRequest.abort();
				}
			}
		}
		return null;
	}

	private static void setProxy(HttpClient client) {
		Context ctx = GameApp.instance();
		boolean flag = APNUtil.hasProxy(ctx);
		if (flag) {
			String ip = APNUtil.getApnProxy(ctx);

			int port = APNUtil.getApnPortInt(ctx);
			HttpHost host = new HttpHost(ip, port);
			client.getParams()
					.setParameter(ConnRoutePNames.DEFAULT_PROXY, host);
		} else {
			client.getParams()
					.setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
		}
	}

	public static void destroy() {
		// HttpClient的实例不再需要时，降低连接，管理器关闭，以确保立即释放所有系统资源
		if (null != client) {
			client.getConnectionManager().shutdown();
			client = null;
		}
	}
}
