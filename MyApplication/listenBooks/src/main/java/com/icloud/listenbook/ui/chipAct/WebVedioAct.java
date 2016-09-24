package com.icloud.listenbook.ui.chipAct;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.WokaoWebView;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.yaya.live.android.util.Log;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class WebVedioAct extends BaseActivity implements OnClickListener {
	View back;
	WokaoWebView webView;
	public static final String URL_ = "url";
	private String url;
	RelativeLayout main;
	ProgressDialog prDialog;

	@Override
	public void init() {
		Intent intent = getIntent();
		this.url = intent.getStringExtra(URL_);
	}

	@Override
	public int getLayout() {
		return R.layout.act_webview;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		main = (RelativeLayout) findViewById(R.id.main);
		webView = (WokaoWebView) findViewById(R.id.web);
	}

	@Override
	public void setListeners() {
		this.back.setOnClickListener(this);
		this.webView.getSettings().setAllowFileAccess(true);// 允许访问文件
		this.webView.getSettings().setJavaScriptEnabled(true);//设置支持javascript脚本
		this.webView.getSettings().setSupportZoom(false);//是否支持缩放  
		this.webView.getSettings().setBuiltInZoomControls(false);//是否设置显示缩放按钮
		
		this.webView.setScrollBarStyle(0);
		prDialog = ProgressDialog.show(WebVedioAct.this, null,
				"loading, please wait...");
		prDialog.setCancelable(true);
		webView.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int progress) {
				if (progress < 99 && !prDialog.isShowing()) {
					prDialog.show();
				} else if (progress >= 99 && prDialog.isShowing()) {
					prDialog.dismiss();
				}
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (!prDialog.isShowing())
					prDialog.dismiss();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (prDialog.isShowing())
					prDialog.dismiss();
			}
		});

	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {  
            webView.goBack();  
            return true;  
        }  
        return super.onKeyDown(keyCode, event);  
    } 
	public void close() {
		finish();
	}

	@Override
	public void setDatas() {
		super.setDatas();
		if (TextUtils.isEmpty(url))
			return;
		if (url.startsWith("http://") || url.startsWith("www.")
				|| url.startsWith("https://")) {
			webView.enablecrossdo();
			webView.loadUrl(url);
			String TAG=this.getClass().getName();
			LogUtil.d(TAG, url+"\n\n\n\n");

		}
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

	@Override
	protected void onDestroy() {
		try {
			main.removeView(webView);
			webView.stopLoading();
			webView.removeAllViews();
			webView.destroy();
		} catch (Exception e) {
		}

		super.onDestroy();
	}
}
