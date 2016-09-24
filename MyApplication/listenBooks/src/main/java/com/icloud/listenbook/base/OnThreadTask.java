package com.icloud.listenbook.base;

public abstract class OnThreadTask {
	public String tips;
	public volatile boolean backPressed = false;
	public LoadingBar loadingBar;

	public abstract String onThreadRun();

	public abstract void onAfterUIRun(String result);

	public void onUIBackPressed() {
	};
};
