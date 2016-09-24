package com.icloud.listenbook.base;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity implements ICommAct {
	protected String Tag = this.getClass().getName();

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

	public void setDatas() {
	}

	public void getDatas() {
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
 
}
