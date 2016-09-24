package com.icloud.listenbook.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class BaseFragmentActivity extends FragmentActivity implements
		ICommAct {

	protected final String Tag=getClass().getSimpleName();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		ActivityUtils.instance().addCurrentAct(this, this);
		init();
		findViews();
		setListeners();
		setDatas();
		getDatas();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ActivityUtils.instance().addCurrentAct(this, this);
	}

	@Override
	protected void onDestroy() {
		ActivityUtils.instance().removeAct(this, this);
		super.onDestroy();
	}

	public void setDatas() {

	}

	public void getDatas() {

	}
}