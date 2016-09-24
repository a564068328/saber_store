package com.icloud.listenbook.base;

import android.app.Activity;

public final class ThreadTask {

	/**
	 * 请求异步任务<br/>
	 * 
	 * @param context
	 * @param tips
	 *            loadingBar的显示文字,如果为String tips（int tipsId）为null则不显示loadingbar
	 * @param ott
	 */
	public static void start(Activity context, String tips,
			boolean enableAbort, OnThreadTask ott) {
		ott.tips = tips;
		ott.loadingBar = null;
		new SyncTaskSimpleWrap(context, ott, enableAbort).execute();
	}

	public static void start(Activity context, int tipsId, boolean enableAbort,
			OnThreadTask ott) {
		start(context, context.getString(tipsId), enableAbort, ott);
	}
}
