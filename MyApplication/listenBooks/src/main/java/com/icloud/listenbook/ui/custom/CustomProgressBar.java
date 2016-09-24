package com.icloud.listenbook.ui.custom;

import com.icloud.listenbook.R;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ThreadPoolUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class CustomProgressBar extends View {
	/**
	 * 第一圈的颜色
	 */
	private int mFirstColor;
	/**
	 * 第二圈的颜色
	 */
	private int mSecondColor;
	/**
	 * 第三圈的颜色
	 */
	private int mThirdColor;
	/**
	 * 内部圈的宽度
	 */
	private int mInternalCircleRaudius;
	/**
	 * 外部圈的宽度
	 */
	private int mExternalCircleRaudius;
	/**
	 * 画笔
	 */
	private Paint mPaint;
	/**
	 * 当前进度
	 */
	private int mProgress;

	/**
	 * 速度
	 */
	private int mSpeed = 20;
	/**
	 * 圆环背景颜色
	 */
	private int mCircleBg;
	/**
	 * 圆环动作颜色
	 */
	private int mCircleAct;
	private int position;
	/**
	 * 是否应该开始下一个
	 */
	private boolean isNext = false;
	private boolean threadSwith = true;

	private int centre;
	private RectF oval;

	public CustomProgressBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.CustomProgressBar, defStyleAttr, 0);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = array.getIndex(i);
			switch (attr) {
			case R.styleable.CustomProgressBar_internalCircleRaudius:
				mInternalCircleRaudius = array.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_PX, 20, getResources()
										.getDisplayMetrics()));
				break;
			case R.styleable.CustomProgressBar_externalCircleRaudius:
				mExternalCircleRaudius = array.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_PX, 20, getResources()
										.getDisplayMetrics()));
				break;
			case R.styleable.CustomProgressBar_firstColor:
				mFirstColor = array.getColor(attr, Color.WHITE);
				break;
			case R.styleable.CustomProgressBar_secondColor:
				mSecondColor = array.getColor(attr, Color.WHITE);
				break;
			case R.styleable.CustomProgressBar_thirdColor:
				mThirdColor = array.getColor(attr, Color.WHITE);
				break;
			case R.styleable.CustomProgressBar_speed:
				mSpeed = array.getInt(attr, 20);
				break;
			}
		}
		array.recycle();
		mPaint = new Paint();
		position = 0;
		mCircleBg = mFirstColor;
		mCircleAct = mSecondColor;
		getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						centre = getWidth() / 2;
						oval = new RectF(centre - mExternalCircleRaudius,
								centre - mExternalCircleRaudius, centre
										+ mExternalCircleRaudius, centre
										+ mExternalCircleRaudius);
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});

		// 绘图线程
		ThreadPoolUtils.execute(new Runnable() {
			@Override
			public void run() {
				while (threadSwith) {
					mProgress++;
					if (mProgress == 360) {
						mProgress = 0;
						position++;
						isNext = !isNext;
					}
					postInvalidate();
					try {
						Thread.sleep(mSpeed);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public CustomProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomProgressBar(Context context) {
		this(context, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (oval == null)
			return;
		int radius = mExternalCircleRaudius - mInternalCircleRaudius;// 线宽
		mPaint.setStrokeWidth(radius); // 设置线宽
		mPaint.setAntiAlias(true); // 消除锯齿
		mPaint.setStyle(Paint.Style.STROKE); // 设置空心

		if (!isNext) {
			setColor(canvas, centre, radius, oval);
		} else {
			isNext = false;
			switch (position) {
			case 0:
				mCircleBg = mFirstColor;
				mCircleAct = mSecondColor;
				break;
			case 1:
				mCircleBg = mSecondColor;
				mCircleAct = mThirdColor;
				break;
			case 2:
				mCircleBg = mThirdColor;
				mCircleAct = mFirstColor;
				break;
			default:
				position = 0;
				mCircleBg = mFirstColor;
				mCircleAct = mSecondColor;
				break;
			}
			setColor(canvas, centre, radius, oval);
		}
	}

	private void setColor(Canvas canvas, int centre, int radius, RectF oval) {
		mPaint.setColor(mCircleBg); // 设置圆环的颜色
		canvas.drawCircle(centre, centre, mExternalCircleRaudius, mPaint); // 画出圆环
		mPaint.setColor(mCircleAct); // 设置圆环的颜色
		canvas.drawArc(oval, -90, mProgress, false, mPaint); // 根据进度画圆弧
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility != View.VISIBLE)
			threadSwith = false;
		else {
			threadSwith = true;
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (getVisibility() != View.VISIBLE)
			threadSwith = false;
		else {
			threadSwith = true;
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		threadSwith = false;
	}
}
