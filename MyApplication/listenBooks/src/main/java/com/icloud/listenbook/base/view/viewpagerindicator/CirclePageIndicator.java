/*
 * Copyright (C) 2011 Patrik Akerfeldt
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icloud.listenbook.base.view.viewpagerindicator;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.icloud.listenbook.R;

/**
 * Draws circles (one for each view). The current view position is filled and
 * others are only stroked.
 */
public class CirclePageIndicator extends View implements PageIndicator {
	private static final int INVALID_POINTER = -1;
	/**
	 * 半径
	 * */
	private float mRadius;
	/**
	 * 间距
	 * */
	private float spacing = 0;
	private final Paint mPaintPageFill = new Paint(ANTI_ALIAS_FLAG);
	/**
	 * 
	 * 外框画笔
	 * */
	private final Paint mPaintStroke = new Paint(ANTI_ALIAS_FLAG);
	/**
	 * 填充的画笔
	 * */
	private final Paint mPaintFill = new Paint(ANTI_ALIAS_FLAG);
	/**
	 * ViewPaget对象
	 * */
	private ViewPager mViewPager;
	/**
	 * ViewPaget 的监听
	 * */
	private ViewPager.OnPageChangeListener mListener;

	/**
	 * 当前页数
	 * */
	private int mCurrentPage;
	/**
	 * 
	 * 临时页面
	 * */
	private int mSnapPage;
	/**
	 * 偏移的像素点
	 * */
	private float mPageOffset;
	/**
	 * 滑动的状态
	 * */
	private int mScrollState;
	/**
	 * 方向
	 * */
	private int mOrientation;
	/**
	 * 是否居中
	 * */
	private boolean mCentered;
	/**
	 * 是否 不绘制动画
	 * */
	private boolean mSnap;

	private int mTouchSlop;
	private float mLastMotionX = -1;
	private int mActivePointerId = INVALID_POINTER;
	private boolean mIsDragging;

	public CirclePageIndicator(Context context) {
		this(context, null);
	}

	public CirclePageIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.vpiCirclePageIndicatorStyle);
	}

	public CirclePageIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode())
			return;

		// 加载默认资源
		final Resources res = getResources();
		final int defaultPageColor = res
				.getColor(R.color.default_circle_indicator_page_color);
		final int defaultFillColor = res
				.getColor(R.color.default_circle_indicator_fill_color);
		final int defaultOrientation = res
				.getInteger(R.integer.default_circle_indicator_orientation);
		final int defaultStrokeColor = res
				.getColor(R.color.default_circle_indicator_stroke_color);
		final float defaultStrokeWidth = res
				.getDimension(R.dimen.default_circle_indicator_stroke_width);
		final float defaultRadius = res
				.getDimension(R.dimen.default_circle_indicator_radius);
		final boolean defaultCentered = res
				.getBoolean(R.bool.default_circle_indicator_centered);
		final boolean defaultSnap = res
				.getBoolean(R.bool.default_circle_indicator_snap);

		// 检索样式属性
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CirclePageIndicator, defStyle, 0);

		mCentered = a.getBoolean(R.styleable.CirclePageIndicator_centered,
				defaultCentered);
		mOrientation = a.getInt(
				R.styleable.CirclePageIndicator_android_orientation,
				defaultOrientation);
		mPaintPageFill.setStyle(Style.FILL);
		mPaintPageFill.setColor(a.getColor(
				R.styleable.CirclePageIndicator_pageColor, defaultPageColor));
		mPaintStroke.setStyle(Style.STROKE);
		mPaintStroke.setColor(a
				.getColor(R.styleable.CirclePageIndicator_strokeColor,
						defaultStrokeColor));
		mPaintStroke.setStrokeWidth(a
				.getDimension(R.styleable.CirclePageIndicator_strokeWidth,
						defaultStrokeWidth));
		mPaintFill.setStyle(Style.FILL);
		mPaintFill.setColor(a.getColor(
				R.styleable.CirclePageIndicator_fillColor, defaultFillColor));
		mRadius = a.getDimension(R.styleable.CirclePageIndicator_radius,
				defaultRadius);
		mSnap = a.getBoolean(R.styleable.CirclePageIndicator_snap, defaultSnap);

		Drawable background = a
				.getDrawable(R.styleable.CirclePageIndicator_android_background);
		if (background != null) {
			setBackgroundDrawable(background);
		}

		a.recycle();

		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat
				.getScaledPagingTouchSlop(configuration);
	}

	/**
	 * 设置是否居中
	 * */
	public void setCentered(boolean centered) {
		mCentered = centered;
		invalidate();
	}

	/**
	 * 是否居中
	 * */
	public boolean isCentered() {
		return mCentered;
	}

	/**
	 * 设置圆心颜色
	 * */
	public void setPageColor(int pageColor) {
		mPaintPageFill.setColor(pageColor);
		invalidate();
	}

	/**
	 * 获取圆心颜色
	 **/
	public int getPageColor() {
		return mPaintPageFill.getColor();
	}

	public void setFillColor(int fillColor) {
		mPaintFill.setColor(fillColor);
		invalidate();
	}

	public int getFillColor() {
		return mPaintFill.getColor();
	}

	/**
	 * 设置方向 0 HORIZONTAL 横向 1 VERTICAL 纵向
	 * */
	public void setOrientation(int orientation) {
		switch (orientation) {
		case HORIZONTAL:
		case VERTICAL:
			mOrientation = orientation;
			requestLayout();
			break;

		default:
			throw new IllegalArgumentException(
					"Orientation must be either HORIZONTAL or VERTICAL.");
		}
	}

	/**
	 * 
	 * 获取方向
	 **/
	public int getOrientation() {
		return mOrientation;
	}

	/***
	 * 设置外框颜色
	 * 
	 */
	public void setStrokeColor(int strokeColor) {
		mPaintStroke.setColor(strokeColor);
		invalidate();
	}

	/**
	 * 获取外框颜色
	 * */
	public int getStrokeColor() {
		return mPaintStroke.getColor();
	}

	/**
	 * 设置外框 宽度
	 * */
	public void setStrokeWidth(float strokeWidth) {
		mPaintStroke.setStrokeWidth(strokeWidth);
		invalidate();
	}

	/**
	 * 获取外框 宽度
	 * */
	public float getStrokeWidth() {
		return mPaintStroke.getStrokeWidth();
	}

	/**
	 * 设置半径
	 * */
	public void setRadius(float radius) {
		mRadius = radius;
		invalidate();
	}

	/***
	 * 获取半径
	 * */
	public float getRadius() {
		return mRadius;
	}

	/**
	 * 设置是否不绘制移动效果
	 * */
	public void setSnap(boolean snap) {
		mSnap = snap;
		invalidate();
	}

	/**
	 * 是没有动画效果的么
	 * */
	public boolean isSnap() {
		return mSnap;
	}

	public void setSpacing(float spacing) {
		this.spacing = spacing;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mViewPager == null) {
			return;
		}
		final int count = mViewPager.getAdapter().getCount();
		if (count == 0) {
			return;
		}

		if (mCurrentPage >= count) {
			setCurrentItem(count - 1);
			return;
		}

		int longSize;
		int longPaddingBefore;
		int longPaddingAfter;
		int shortPaddingBefore;
		if (mOrientation == HORIZONTAL) {
			longSize = getWidth();
			longPaddingBefore = getPaddingLeft();
			longPaddingAfter = getPaddingRight();
			shortPaddingBefore = getPaddingTop();
		} else {
			longSize = getHeight();
			longPaddingBefore = getPaddingTop();
			longPaddingAfter = getPaddingBottom();
			shortPaddingBefore = getPaddingLeft();
		}
		// 之所以是 3 因为 若为1的话 会有一半重叠 ，若为2则没有空隙
		final float threeRadius = (mRadius) * 3 + spacing;
		final float shortOffset = shortPaddingBefore + mRadius;
		float longOffset = longPaddingBefore + threeRadius / 2;
		if (mCentered) {
			// 计算居中应该在的起始点
			longOffset += ((longSize - longPaddingBefore - longPaddingAfter) / 2.0f)
					- ((count * threeRadius) / 2.0f);
		}

		float dX;
		float dY;

		float pageFillRadius = mRadius;
		if (mPaintStroke.getStrokeWidth() > 0) {
			pageFillRadius -= mPaintStroke.getStrokeWidth() / 2.0f;
		}

		// 绘制外圆 每个间隔三倍半径的距离
		for (int iLoop = 0; iLoop < count; iLoop++) {
			float drawLong = longOffset + (iLoop * threeRadius);
			// 根据方向 确定 改变的是X轴 还是Y轴
			if (mOrientation == HORIZONTAL) {
				dX = drawLong;
				dY = shortOffset;
			} else {
				dX = shortOffset;
				dY = drawLong;
			}
			// 如果不是完全透明的 只涂料填
			if (mPaintPageFill.getAlpha() > 0) {
				canvas.drawCircle(dX, dY, pageFillRadius, mPaintPageFill);
			}

			// 如果笔划宽度是零
			if (pageFillRadius != mRadius) {
				canvas.drawCircle(dX, dY, mRadius, mPaintStroke);
			}
		}

		/*
		 * 看是否绘制移动 动画 ,是的话 用上一个页面起点 用当前 页数 X点的距离
		 */
		float cx = (mSnap ? mSnapPage : mCurrentPage) * threeRadius;
		// 有动画效果
		if (!mSnap) {
			// 偏移度
			cx += mPageOffset * threeRadius;
		}
		if (mOrientation == HORIZONTAL) {
			dX = longOffset + cx;
			dY = shortOffset;
		} else {
			dX = shortOffset;
			dY = longOffset + cx;
		}
		// 绘制圆心
		canvas.drawCircle(dX, dY, mRadius, mPaintFill);
	}

	public boolean onTouchEvent(android.view.MotionEvent ev) {
		if (super.onTouchEvent(ev)) {
			return true;
		}
		if ((mViewPager == null) || (mViewPager.getAdapter().getCount() == 0)) {
			return false;
		}

		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_DOWN:

			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			mLastMotionX = ev.getX();
			break;

		case MotionEvent.ACTION_MOVE: {
			final int activePointerIndex = MotionEventCompat.findPointerIndex(
					ev, mActivePointerId);
			final float x = MotionEventCompat.getX(ev, activePointerIndex);
			final float deltaX = x - mLastMotionX;

			if (!mIsDragging) {
				if (Math.abs(deltaX) > mTouchSlop) {
					mIsDragging = true;
				}
			}

			if (mIsDragging) {
				mLastMotionX = x;
				if (mViewPager.isFakeDragging() || mViewPager.beginFakeDrag()) {
					mViewPager.fakeDragBy(deltaX);
				}
			}

			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:

			if (!mIsDragging) {
				final int count = mViewPager.getAdapter().getCount();
				final int width = getWidth();
				final float halfWidth = width / 2f;
				final float sixthWidth = width / 6f;
				/*
				 * 按下的位置小于一个 圆心往左边一个半圆的位置 则是向着左边 .反之向右。
				 * 
				 * 主要是防止 选中在中点 而在中点的选中范围内点击
				 */
				if ((mCurrentPage > 0) && (ev.getX() < halfWidth - sixthWidth)) {
					if (action != MotionEvent.ACTION_CANCEL) {
						mViewPager.setCurrentItem(mCurrentPage - 1);
					}
					return true;
				} else if ((mCurrentPage < count - 1)
						&& (ev.getX() > halfWidth + sixthWidth)) {
					if (action != MotionEvent.ACTION_CANCEL) {
						mViewPager.setCurrentItem(mCurrentPage + 1);
					}
					return true;
				}
			}

			mIsDragging = false;
			mActivePointerId = INVALID_POINTER;
			if (mViewPager.isFakeDragging())
				mViewPager.endFakeDrag();
			break;

		case MotionEventCompat.ACTION_POINTER_DOWN: {
			final int index = MotionEventCompat.getActionIndex(ev);
			mLastMotionX = MotionEventCompat.getX(ev, index);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			break;
		}

		case MotionEventCompat.ACTION_POINTER_UP:
			final int pointerIndex = MotionEventCompat.getActionIndex(ev);
			final int pointerId = MotionEventCompat.getPointerId(ev,
					pointerIndex);
			if (pointerId == mActivePointerId) {
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mActivePointerId = MotionEventCompat.getPointerId(ev,
						newPointerIndex);
			}
			mLastMotionX = MotionEventCompat.getX(ev,
					MotionEventCompat.findPointerIndex(ev, mActivePointerId));
			break;
		}

		return true;
	}

	/**
	 * 设置绑定ViewPager 初始化页面大小
	 * */
	@Override
	public void setViewPager(ViewPager view) {
		if (mViewPager == view) {
			return;
		}
		if (mViewPager != null) {
			mViewPager.setOnPageChangeListener(null);
		}
		if (view.getAdapter() == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}
		mViewPager = view;
		mCurrentPage = view.getCurrentItem();
		mViewPager.setOnPageChangeListener(this);
		invalidate();
	}

	@Override
	public void setViewPager(ViewPager view, int initialPosition) {
		setViewPager(view);
		setCurrentItem(initialPosition);
	}

	@Override
	public void setCurrentItem(int item) {
		if (mViewPager == null) {
			throw new IllegalStateException("ViewPager has not been bound.");
		}
		mViewPager.setCurrentItem(item);
		mCurrentPage = item;
		invalidate();
	}

	@Override
	public void notifyDataSetChanged() {
		invalidate();
	}
	public int getScrollState(){
		return mScrollState ;
	}
	/**
	 * 滾動狀態的變化
	 * */
	@Override
	public void onPageScrollStateChanged(int state) {
		mScrollState = state;

		if (mListener != null) {
			mListener.onPageScrollStateChanged(state);
		}
	}

	/**
	 * 滚动进行时
	 * */
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		mCurrentPage = position;
		mPageOffset = positionOffset;
		invalidate();

		if (mListener != null) {
			mListener.onPageScrolled(position, positionOffset,
					positionOffsetPixels);
		}
	}

	/**
	 * 滑动结束
	 * */
	@Override
	public void onPageSelected(int position) {
		// 状态为没有动画
		if (mSnap || mScrollState == ViewPager.SCROLL_STATE_IDLE) {
			mCurrentPage = position;
			mSnapPage = position;
			invalidate();
		}

		if (mListener != null) {
			mListener.onPageSelected(position);
		}
	}

	@Override
	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
		mListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mOrientation == HORIZONTAL) {
			setMeasuredDimension(measureLong(widthMeasureSpec),
					measureShort(heightMeasureSpec));
		} else {
			setMeasuredDimension(measureShort(widthMeasureSpec),
					measureLong(heightMeasureSpec));
		}
	}

	/**
	 * 决定这个视图的宽度
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return 视图的宽度
	 */
	private int measureLong(int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
			// We were told how big to be
			result = specSize;
		} else {
			float spacing = this.spacing;
			if (mOrientation != HORIZONTAL)
				spacing = 0;
			// 根据点的数量计算宽度
			final int count = mViewPager.getAdapter().getCount();
			result = (int) (getPaddingLeft() + getPaddingRight()
					+ (count * 2 * (spacing + mRadius)) + (count - 1)
					* (spacing + mRadius) + 1);
			// Respect AT_MOST value if that was what is called for by
			// measureSpec
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * 决定这个视图的高度
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return 视图的高度, 从 measureSpec的约束中获取
	 */
	private int measureShort(int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		// 若模式为精确尺寸则直接赋值
		if (specMode == MeasureSpec.EXACTLY) {
			// 我们被告知多少
			result = specSize;
		} else {
			float spacing = this.spacing;
			if (mOrientation != VERTICAL)
				spacing = 0;
			// 否则 计算为 半径的宽度
			result = (int) (2 * (spacing / 2 + mRadius) + getPaddingTop()
					+ getPaddingBottom() + 1);
			// 若模式是设置了最大尺寸的话 返回尺寸和它进行判断 取小的
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * 恢复页面状态
	 * */
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		mCurrentPage = savedState.currentPage;
		mSnapPage = savedState.currentPage;
		// 请求重新布局
		requestLayout();
	}

	/**
	 * 保存选中终端
	 * */
	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPage = mCurrentPage;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currentPage;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPage = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPage);
		}

		@SuppressWarnings("UnusedDeclaration")
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
