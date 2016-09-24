package com.icloud.wrzjh.base.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;

public class ViewUtils {
	protected static ViewUtils instance;
	public WidgetOnTouchListener onTouchListener;

	public static Bitmap createRoundConerImage(Bitmap source, int w, int h,
			float rx, float ry) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rect, 50, 50, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	public static ViewUtils instance() {
		if (instance == null)
			instance = new ViewUtils();
		return instance;
	}

	public static int[] getWH(Activity context) {
		int[] wd = { 0, 0 };
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		wd[0] = dm.widthPixels;
		wd[1] = dm.heightPixels;
		return wd;
	}

	public static int dip2px(float dpValue, Context ctx) {
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static float dip2pxF(float dpValue, Context ctx) {
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (dpValue * scale);
	}

	public static int px2dip(float pxValue, Context ctx) {
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	private ViewUtils() {
		onTouchListener = new WidgetOnTouchListener();
	}

	public static int getWindowStateBarHeight(Activity ctx) {
		Rect frame = new Rect();
		ctx.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		return frame.top;
	}

}
