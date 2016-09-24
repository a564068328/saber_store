package com.icloud.listenbook.ui.chipAct;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.OnThreadTask;
import com.icloud.listenbook.base.ThreadTask;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.http.datas.HttpConfig;
import com.icloud.wrzjh.base.net.HttpSender;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;

/** 支付wap网页 **/
@SuppressLint("JavascriptInterface")
public class WapActivity extends BaseActivity implements OnClickListener {
	private ProgressBar progressBar = null;
	private WebView webView = null;
	String formatUrl;

	@Override
	public int getLayout() {
		return R.layout.wap_pay_main;
	}

	@JavascriptInterface
	public void paycancel() {
		Log.d("debug", "user cancel pay !");
		finish();
	}

	String url = "http://xuetong111.com:8083/pay/pay?buyer=%s&param=%s";

	public String formatPayParams() {
		Object[] array = new Object[2];
		array[0] = String.valueOf(UserInfo.instance().getUid());
		array[1] = UserInfo.instance().getToken();

		String str = String.format(url, array);
		return str;
	}

	@JavascriptInterface
	public void close() {
		finish();
	}

	// 0 表示成功 1表示失败 2 表示易宝支付需要等待
	@JavascriptInterface
	public void payresult(String orderid, String paymoney, String errcode,
			String result) {
		LogUtil.e("payresult", "orderid " + orderid + " paymoney " + paymoney
				+ "result " + result);
		if ("0".equals(result.trim())) {
			updateCurrency();
		} else if ("2".equals(result.trim())) {
			updateState(orderid);
		} else {
			close();
		}
	}

	private void updateCurrency() {
		ThreadTask.start(this, "正在刷新,请稍后..", true, new OnThreadTask() {
			@Override
			public String onThreadRun() {
				String content = upDateCallBack();
				try {
					JSONObject res = new JSONObject(content);
					int payResCode = res.optInt("result", 1);
					if (payResCode == 0) {
						updateUserMoneys(res);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public void onAfterUIRun(String res) {
				close();
			}
		});
	}

	private void updateState(final String orderId) {
		ThreadTask.start(this, "正在查询支付状态,请稍后", false, new OnThreadTask() {
			boolean isSucces;
			String pay_result = "支付失败";

			@Override
			public String onThreadRun() {
				for (int i = 0; i < 3; i++) {
					String payRes = userPayStatus(orderId);
					JSONObject ret;
					try {
						ret = new JSONObject(payRes);
						int payResCode = ret.optInt("result", -1);
						if (payResCode == 0) {
							isSucces = true;
							updateUserMoneys(ret);
							pay_result = ret.optString("msg", "支付成功");
							break;
						} else if (payResCode == 1) {
							isSucces = false;
							updateUserMoneys(ret);
							pay_result = ret.optString("msg", "支付失败");
							break;
						} else {
							isSucces = false;
							try {
								Thread.sleep(1000 + i * 2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				return null;
			}

			@Override
			public void onAfterUIRun(String res) {
				Toast.makeText(WapActivity.this, pay_result, Toast.LENGTH_LONG)
						.show();
				close();
			}
		});
	}

	private void updateUserMoneys(JSONObject data) {
		double currency = data.optDouble("currency", UserInfo.instance()
				.getCurrency());
		if (currency >= 0) {
			UserInfo.instance().setCurrency(currency);
		}
	}

	public String userPayStatus(String p2_Order) {
		try {
			JSONObject json = new JSONObject();
			json.put("orderid", p2_Order);
			String content = HttpSender.post(ServerIps.getLoginAddr()
					+ HttpConfig.pay_status, null, json.toString());
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 支付成功记录回调 */
	public String upDateCallBack() {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String content = HttpSender.post(ServerIps.getLoginAddr()
				+ HttpConfig.getCurrency, null, jb.toString());
		return content;
	}

	@Override
	public void init() {
		if (!UserInfo.instance().isLogin())
			LoadingTool.launchResultActivity(this, LoginAct.class, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				String url = formatPayParams();
				this.webView.loadUrl(url);
			}
			break;
		}
	}

	@Override
	public void findViews() {
		this.progressBar = (ProgressBar) findViewById(R.id.progress);
		this.webView = (WebView) findViewById(R.id.web);
	}

	@Override
	public void setListeners() {

		this.webView.getSettings().setAllowFileAccess(true);
		this.webView.getSettings().setJavaScriptEnabled(true);
		this.webView.getSettings().setSupportZoom(true);
		this.webView.getSettings().setBuiltInZoomControls(true);
		this.webView.setScrollBarStyle(0);
		this.webView.addJavascriptInterface(this, "cloudpay");
		formatUrl = formatPayParams();

		this.webView.loadUrl(formatUrl);
		LogUtil.e("url ", formatUrl);
		WebViewClient client = new WebViewClient() {
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (WapActivity.this.webView.getContentHeight() == 0)
					return;
				Log.d("onPageFinished", "web page loaded finish!");
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				WapActivity.this.progressBar.setVisibility(View.VISIBLE);
				Log.d("onPageStarted", "start loading web page!");
				super.onPageStarted(view, url, favicon);
			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("shouldOverrideUrlLoading", url);
				view.loadUrl(url);
				return true;
			}
		};
		WebChromeClient chrome = new WebChromeClient() {
			public void onProgressChanged(WebView view, int newProgress) {
				WapActivity.this.progressBar.setProgress(newProgress);
				super.onProgressChanged(view, newProgress);
				Log.d("webview", Integer.toString(newProgress));
				if (newProgress != 100)
					return;
				WapActivity.this.progressBar.setVisibility(View.GONE);
				WapActivity.this.progressBar.setProgress(0);
			}
		};
		webView.setWebViewClient(client);
		webView.setWebChromeClient(chrome);

		webView.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (event.getAction() == KeyEvent.ACTION_UP) {
						if (webView.canGoBack()) {
							if (keyCode == KeyEvent.KEYCODE_BACK) {
								String currentUrl = webView.getUrl();
								if (currentUrl
										.indexOf("http://xuetong111.com:8083/pay/pay") != -1) {
									webView.goBack();
								} else {
									webView.loadUrl(formatUrl);
									webView.postDelayed(new Runnable() {
										@Override
										public void run() {
											webView.clearHistory();
										}
									}, 500);
								}
							}
						} else {
							finish();
						}
					}
					return true;
				}
				return false;
			}
		});

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		}

	}
}
