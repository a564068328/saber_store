package com.icloud.listenbook.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.icloud.listenbook.R;
import com.icloud.listenbook.unit.Helpers;
import com.icloud.listenbook.unit.NavigationUtils;

public abstract class BaseAppCompatActivity extends ATEActivity implements
		ICommAct {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());

		ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
		View parentView = contentFrameLayout.getChildAt(0);
		if (parentView != null && Build.VERSION.SDK_INT == 19) {
			parentView.setFitsSystemWindows(true);
		}

		ActivityUtils.instance().addCurrentAct(this, this);
		init();
		findViews();
		setListeners();
        setDatas();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
//		if (!TimberUtils.hasEffectsPanel(BaseAppCompatActivity.this)) {
//			menu.removeItem(R.id.action_equalizer);
//		}
		ATE.applyMenu(this, getATEKey(), menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				super.onBackPressed();
				return true;
			case R.id.action_settings:
				NavigationUtils.navigateToSettings(this);
				return true;
//			case R.id.action_shuffle:
//				Handler handler = new Handler();
//				handler.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						MusicPlayer.shuffleAll(BaseActivity.this);
//					}
//				}, 80);
//			return true;
			case R.id.action_search:
				NavigationUtils.navigateToSearch(this);
				return true;
//			case R.id.action_equalizer:
//				NavigationUtils.navigateToEqualizer(this);
//				return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Nullable
	@Override
	public String getATEKey() {
		return Helpers.getATEKey(this);
	}



}
