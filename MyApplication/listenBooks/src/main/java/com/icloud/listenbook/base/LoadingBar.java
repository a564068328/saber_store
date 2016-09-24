package com.icloud.listenbook.base;

import com.icloud.listenbook.R;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class LoadingBar extends Dialog implements OnPreDrawListener {
	private AnimationDrawable ani;
	private Activity mContext;
	private onCancelListener listener = null;

	public interface onCancelListener {
		public abstract void onCancel();
	}

	public void setOnCancelListener(onCancelListener listener) {
		this.listener = listener;
	}

	public LoadingBar(Activity context) {
		this(context, null);
	}

	public LoadingBar(Activity context, String text) {
		super(context);
		mContext = context;
		if (mContext != null && !mContext.isFinishing()) {
			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);

			LayoutInflater inflater = LayoutInflater.from(context);
			View layout = inflater.inflate(R.layout.loading, null);

			ImageView img = (ImageView) layout.findViewById(R.id.loading_img);
			Animation anim = AnimationUtils.loadAnimation(context,
					R.anim.loadingbar);
			anim.setInterpolator(new LinearInterpolator());
			img.setAnimation(anim);

			TextView textview = (TextView) layout
					.findViewById(R.id.loading_text);
			if (text != null) {
				textview.setVisibility(View.VISIBLE);
				textview.setText(text);
			} else {
				textview.setVisibility(View.GONE);
			}

			setContentView(layout);
			setCancelable(true);

			img.getViewTreeObserver().addOnPreDrawListener(this);
			ani = (AnimationDrawable) img.getDrawable();
		}
	}

	@Override
	public boolean onPreDraw() {
		if (mContext != null && !mContext.isFinishing()) {
			if (ani != null)
				ani.start();
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			if (null != this.listener) {
				this.listener.onCancel();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (mContext != null && !mContext.isFinishing()) {
			if (ani != null)
				ani.stop();
		}
		super.onStop();
	}
}