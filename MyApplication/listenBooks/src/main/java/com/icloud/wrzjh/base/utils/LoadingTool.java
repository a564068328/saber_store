package com.icloud.wrzjh.base.utils;

import android.app.Activity;
import android.content.Intent;

import com.icloud.listenbook.R;

public class LoadingTool {

	public static void launchActivity(Activity context,
			Class<? extends Activity> activity) {
		launchActivity(context, activity, true);
	}

	public static void launchActivity(Activity context,
			Class<? extends Activity> activity, Intent intent) {
		launchActivity(context, activity, intent, true);
	}

	public static void launchActivity(Activity context,
			Class<? extends Activity> activity, Intent intent, boolean flag) {
		intent.setClass(context, activity);
		context.startActivity(intent);
		if (flag)
			overridePendingTransition(context, R.anim.activity_in,
					R.anim.activity_out);
	}

	public static void launchResultActivity(Activity context,
			Class<? extends Activity> activity, int requestCode) {
		launchResultActivity(context, activity, new Intent(), requestCode, true);
	}

	public static void launchResultActivity(Activity context,
			Class<? extends Activity> activity, Intent intent, int requestCode) {
		launchResultActivity(context, activity, intent, requestCode, true);
	}

	public static void launchResultActivity(Activity context,
			Class<? extends Activity> activity, Intent intent, int requestCode,
			boolean flag) {
		intent.setClass(context, activity);
		context.startActivityForResult(intent, requestCode);
		if (flag)
			overridePendingTransition(context, R.anim.activity_in,
					R.anim.activity_out);
	}

	public static void launchActivity(Activity context,
			Class<? extends Activity> activity, boolean flag) {
		Intent intent = new Intent(context, activity);
		context.startActivity(intent);
		if (flag)
			overridePendingTransition(context, R.anim.activity_in,
					R.anim.activity_out);
	}

	private static void overridePendingTransition(final Activity act,
			final int inAnimation, final int outAnimation) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
			new Object() {
				void execute() {
					act.overridePendingTransition(inAnimation, outAnimation);
				}
			}.execute();
		}
	}
}