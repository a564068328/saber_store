package com.icloud.listenbook.base;

import android.app.Application;
import android.content.Context;

import com.icloud.wrzjh.base.utils.WidgetOnTouchListener;

public class BaseGameApp extends Application {
	protected static final String TAG = GameApp.class.getSimpleName();
	public static final int MSG_LOOP_THREAD = 5;
	protected static GameApp instance;
	public static String APP_NAME = "千王AAA";
	public static boolean DEBUG = true;
	public static boolean AccTip = true;
	public WidgetOnTouchListener onTouchListener;

	public BaseGameApp() {

	}

	public static GameApp instance() {
		return instance;
	}

	public static Context getContext() {
		return instance.getApplicationContext();
	}

}
