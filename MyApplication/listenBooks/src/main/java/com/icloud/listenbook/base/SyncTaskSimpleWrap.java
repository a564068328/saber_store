package com.icloud.listenbook.base;

import android.app.Activity;
import android.os.AsyncTask;

import com.icloud.wrzjh.base.utils.LogUtil;

public final class SyncTaskSimpleWrap extends AsyncTask<Void, Void, String> {

	private Activity context;
	private OnThreadTask thread;
	private boolean enableAbort;

	public SyncTaskSimpleWrap(Activity context, OnThreadTask thread,
			boolean enableAbort) {
		this.context = context;
		this.thread = thread;
		this.enableAbort = enableAbort;
	}

	@Override
	protected void onPreExecute() {
		if (context != null && !context.isFinishing()) {
			if (thread.tips != null && thread.tips.length() == 0) {
				thread.loadingBar = new LoadingBar(context);
				thread.loadingBar.show();
				setCancelable();
			} else if (thread.tips != null && thread.tips.length() > 0) {
				thread.loadingBar = new LoadingBar(context, thread.tips);
				thread.loadingBar.show();
				setCancelable();
			}
		}
	}

	private void setCancelable() {
		if (enableAbort && context != null && !context.isFinishing()) {
			thread.loadingBar.setCancelable(true);
			thread.loadingBar
					.setOnCancelListener(new LoadingBar.onCancelListener() {
						@Override
						public void onCancel() {
							if (context != null && !context.isFinishing()) {
								if (null != SyncTaskSimpleWrap.this.thread.loadingBar) {
									SyncTaskSimpleWrap.this.thread.loadingBar
											.dismiss();
									SyncTaskSimpleWrap.this.thread.loadingBar = null;
								}
								SyncTaskSimpleWrap.this.thread.backPressed = true;
								SyncTaskSimpleWrap.this.thread
										.onUIBackPressed();
							}
						}
					});
		}
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			return thread.onThreadRun();
		} catch (Exception e) {
			LogUtil.e("SyncTaskSimpleWrap", e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(String val) {
		if (!thread.backPressed) {
			if (null != thread.loadingBar && context != null
					&& !context.isFinishing()) {
				try {
					thread.loadingBar.dismiss();
				} catch (Exception e) {
					LogUtil.e("thread.loadingBar", e);
				}

				thread.loadingBar = null;
			}
			thread.onAfterUIRun(val);
		}
	}

}
