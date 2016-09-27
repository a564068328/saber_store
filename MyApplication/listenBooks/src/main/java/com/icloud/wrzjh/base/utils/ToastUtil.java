package com.icloud.wrzjh.base.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.ActivityUtils;
import com.icloud.listenbook.base.GameApp;

public class ToastUtil {

	private static Resources res = GameApp.instance().getResources();

	public static void showToast(String text, int gravity, int duration) {
		TextView tv = new TextView(GameApp.instance());
		tv.setTextColor(Color.WHITE);
		tv.setBackgroundResource(R.drawable.toast_bg);
		tv.setTextSize(18);
		tv.setPadding(12, 6, 12, 6);
		tv.setGravity(Gravity.CENTER);

		Toast toast = Toast.makeText(GameApp.instance(), text, duration);
		toast.setView(tv);
		if (gravity != -1) {
			toast.setGravity(gravity, 0, 10);
		} else {
			toast.setGravity(Gravity.BOTTOM, 0, 100);
		}
		tv.setText(text);
		toast.show();
	}

	public static void showMessage(final String text) {
		Activity act = ActivityUtils.instance().getCurrAct();
		if (act != null && !act.isFinishing()) {
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showToast(text, -1, Toast.LENGTH_SHORT);
				}
			});
		}
	}

	public static void showMessage(int resId) {
		String s = res.getString(resId);
		showMessage(s);
	}
}