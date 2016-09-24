package com.icloud.listenbook.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

public class WokaoWebView extends WebView {

	public WokaoWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void enablecrossdo() {
		if (Build.VERSION.SDK_INT >= 16)
			enablecrossdo16();
		else
			enablecrossdo9();
	}

	public void enablecrossdo9() {
		try {
			Field field = WebView.class.getDeclaredField("mWebViewCore");
			field.setAccessible(true);
			Object webviewcore = field.get(this);
			Method method = webviewcore.getClass().getDeclaredMethod(
					"nativeRegisterURLSchemeAsLocal", String.class);
			method.setAccessible(true);
			method.invoke(webviewcore, "http");
			method.invoke(webviewcore, "https");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void enablecrossdo16() {
		try {
			Field webviewclassic_field = WebView.class
					.getDeclaredField("mProvider");
			webviewclassic_field.setAccessible(true);
			Object webviewclassic = webviewclassic_field.get(this);
			Field webviewcore_field = webviewclassic.getClass()
					.getDeclaredField("mWebViewCore");
			webviewcore_field.setAccessible(true);
			Object mWebViewCore = webviewcore_field.get(webviewclassic);
			Field nativeclass_field = webviewclassic.getClass()
					.getDeclaredField("mNativeClass");
			nativeclass_field.setAccessible(true);
			Object mNativeClass = nativeclass_field.get(webviewclassic);

			Method method = mWebViewCore.getClass().getDeclaredMethod(
					"nativeRegisterURLSchemeAsLocal",
					new Class[] { int.class, String.class });
			method.setAccessible(true);
			method.invoke(mWebViewCore, mNativeClass, "http");
			method.invoke(mWebViewCore, mNativeClass, "https");
		} catch (Exception e) {

		}
	}



}
