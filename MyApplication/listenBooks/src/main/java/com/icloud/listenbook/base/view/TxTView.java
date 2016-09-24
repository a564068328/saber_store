package com.icloud.listenbook.base.view;

import com.icloud.listenbook.text.JustifiedEditText;
import com.icloud.listenbook.unit.JustifyTextView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.EditText;

public class TxTView extends JustifyTextView {
	static final float MIN_SIZE = 18f;
	static final float MAX_SIZE = 32f;
	private float mScaleFactor = .5f;
	float textSize;

	// private ScaleGestureDetector mScaleDetector;

	protected void init() {
		textSize = MIN_SIZE;
		this.setBackgroundResource(com.icloud.listenbook.R.color.txt_bg);
		this.setTextIsSelectable(true);
		this.setTextSize(MIN_SIZE);
		// mScaleDetector = new ScaleGestureDetector(this.getContext(),
		// new ScaleListener());
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			// scale change since
			// previous event
			mScaleFactor *= detector.getScaleFactor();
			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 1.0f));
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			TxTView.this.setTextSize(MAX_SIZE * mScaleFactor);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// mScaleDetector.onTouchEvent(event);

		return super.onTouchEvent(event);
	}

	public TxTView(Context context) {
		super(context);
		init();
	}

	public TxTView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TxTView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected boolean getDefaultEditable() {
		return false;
	}

	public void addTextSize() {
		mScaleFactor *= 1.2f;
		mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 1.0f));
		this.setTextSize(MAX_SIZE * mScaleFactor);
	}

	public void reduceTextSize() {
		mScaleFactor *= 0.9f;
		mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 1.0f));
		this.setTextSize(MAX_SIZE * mScaleFactor);
	}
}