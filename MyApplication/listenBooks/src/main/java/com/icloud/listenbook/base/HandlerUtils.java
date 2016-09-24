package com.icloud.listenbook.base;

import android.os.Handler;

public class HandlerUtils {
	private static HandlerUtils instance;
	Handler handler;

	public static void init() {
		instance();
	}

	public static Handler instance() {
		if (instance == null)
			instance = new HandlerUtils();
		return instance.handler;

	}

	public static boolean postDelayed(Runnable r, long delayMillis) {
		return instance().postDelayed(r, delayMillis);
	}

	public static void removeCallbacks(Runnable r) {
		instance().removeCallbacks(r);
	}

	private HandlerUtils() {
		handler = new Handler();
	}

}
