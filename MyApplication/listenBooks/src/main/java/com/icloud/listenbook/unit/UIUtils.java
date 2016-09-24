package com.icloud.listenbook.unit;

import android.content.Context;

public class UIUtils {
	public static int dip2px(Context context, float dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}
}
