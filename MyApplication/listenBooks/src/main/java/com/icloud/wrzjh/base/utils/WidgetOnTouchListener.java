package com.icloud.wrzjh.base.utils;

import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 按钮 点击效果
 * 
 * @author Administrator
 */
public class WidgetOnTouchListener implements OnTouchListener {

	/** 按钮 文字初始化颜色 */
	private int tx_color;
	/** 控制按钮文字颜色 */
	private boolean flg_color = true;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		/** 判断是否为按钮 */
		if (v instanceof Button) {
			isButton((Button) v, event);
		} else if (v instanceof ImageButton) {
			isImageButton((ImageButton) v, event);
		} else if (v instanceof ImageView) {
			isImageView((ImageView) v, event);
		} else if (v instanceof TextView) {
			isTextView((TextView) v, event);
		} else if (v instanceof ViewGroup) {
			isLayout((ViewGroup) v, event);
		}
		return false;
	}

	private boolean isLayout(ViewGroup layout, MotionEvent event) {
		Drawable drawable = layout.getBackground();
		if (null == drawable) {
			return false;
		}
		try {
			/** 重新创建一个State以避免共享 */
			drawable.mutate();
		} catch (Exception e) {

		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			/** 按下是按钮效果 */
			drawable.setColorFilter(Color.argb(100, 0, 0, 0), Mode.DST_IN);
			layout.setBackgroundDrawable(drawable);
			break;
		/** 按钮回复 效果 */
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			drawable.clearColorFilter();
			layout.setBackgroundDrawable(drawable);
			break;
		}
		return true;
	}

	/**
	 * Button 事件
	 * 
	 * @param button
	 * @param event
	 * @return
	 */
	private boolean isButton(Button button, MotionEvent event) {
		Drawable drawable = button.getBackground();
		if (null == drawable) {
			return false;
		}
		if (flg_color) {
			tx_color = button.getTextColors().getDefaultColor();
		}
		try {
			/** 重新创建一个State以避免共享 */
			drawable.mutate();
		} catch (Exception e) {

		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			/** 按下是按钮效果 */
			drawable.setColorFilter(Color.argb(100, 0, 0, 0), Mode.DST_IN);
			button.setBackgroundDrawable(drawable);
			button.setTextColor(Color.argb(100, Color.red(tx_color),
					Color.green(tx_color), Color.blue(tx_color)));
			flg_color = false;
			break;
		/** 按钮回复 效果 */
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			drawable.clearColorFilter();
			button.setBackgroundDrawable(drawable);
			button.setTextColor(tx_color);
			flg_color = true;
			break;
		}
		return true;
	}

	/**
	 * ImageButton 事件
	 * 
	 * @param button
	 * @param event
	 * @return
	 */
	private boolean isImageButton(ImageButton imageButton, MotionEvent event) {
		Drawable drawable = imageButton.getBackground();
		Drawable drawable_top = imageButton.getDrawable();
		if (null == drawable || null == drawable_top) {
			return false;
		}
		try {
			/** 重新创建一个State以避免共享 */
			drawable.mutate();
			drawable_top.mutate();
		} catch (Exception e) {

		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			/** 按下是按钮效果 */
			drawable.setColorFilter(Color.argb(100, 0, 0, 0), Mode.DST_IN);
			imageButton.setBackgroundDrawable(drawable);
			drawable_top.setColorFilter(Color.argb(100, 0, 0, 0), Mode.DST_IN);
			imageButton.setImageDrawable(drawable_top);
			break;
		/** 按钮回复 效果 */
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			drawable.clearColorFilter();
			imageButton.setBackgroundDrawable(drawable);
			drawable_top.clearColorFilter();
			imageButton.setImageDrawable(drawable_top);
			break;
		}
		return true;
	}

	/**
	 * ImageView 事件
	 * 
	 * @param button
	 * @param event
	 * @return
	 */
	private boolean isImageView(ImageView imageView, MotionEvent event) {
		boolean flag = false;
		flag = isImageBG(imageView, event);
		if (!flag) {
			flag = isImageSrc(imageView, event);
		}
		return flag;
	}

	/** 有imgsrc **/
	private boolean isImageSrc(ImageView imageView, MotionEvent event) {
		Drawable drawable = imageView.getDrawable();

		if (null == drawable) {
			return false;
		}
		try {
			/** 重新创建一个State以避免共享 */
			drawable.mutate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			/** 按下是按钮效果 */
			drawable.setColorFilter(Color.argb(100, 0, 0, 0), Mode.DST_IN);
			imageView.setImageDrawable(drawable);
			imageView.invalidate();
			break;
		/** 按钮回复 效果 */
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			drawable.clearColorFilter();
			imageView.setImageDrawable(drawable);
			imageView.invalidate();
			break;
		}
		return true;
	}

	/** 有background **/
	private boolean isImageBG(ImageView imageView, MotionEvent event) {
		Drawable drawable = imageView.getBackground();

		if (null == drawable) {
			return false;
		}
		try {
			/** 重新创建一个State以避免共享 */
			drawable.mutate();
		} catch (Exception e) {

		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			/** 按下是按钮效果 */
			drawable.setColorFilter(Color.argb(100, 0, 0, 0), Mode.DST_IN);
			imageView.setBackgroundDrawable(drawable);
			break;
		/** 按钮回复 效果 */
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			drawable.clearColorFilter();
			imageView.setBackgroundDrawable(drawable);
			break;
		}
		return true;
	}

	private boolean isTextView(TextView tv, MotionEvent event) {
		Drawable drawable = tv.getBackground();

		if (null == drawable) {
			return false;
		}

		if (drawable != null) {
			/** 重新创建一个State以避免共享 */
			drawable.mutate();
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (drawable != null) {
				drawable.setColorFilter(Color.argb(100, 0, 0, 0), Mode.DST_IN);
				tv.setBackgroundDrawable(drawable);
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			if (drawable != null) {
				drawable.clearColorFilter();
				tv.setBackgroundDrawable(drawable);
			}
			break;
		}
		return true;
	}
}