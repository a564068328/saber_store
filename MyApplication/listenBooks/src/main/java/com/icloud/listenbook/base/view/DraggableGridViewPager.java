package com.icloud.listenbook.base.view;

/*
 The MIT License (MIT)

 Copyright (c) 2014 justin

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in
 the Software without restriction, including without limitation the rights to
 use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 the Software, and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Scroller;

/**
 * Zaker style grid view pager, support dragging & rearrange, using as zaker's
 * main screen.
 */
public class DraggableGridViewPager extends ViewGroup {
	private static final String TAG = "DraggableGridViewPager";
	private static final boolean DEBUG = false;
	private static final boolean USE_CACHE = false;

	private static void DEBUG_LOG(String msg) {
		if (DEBUG) {
			Log.v(TAG, msg);
		}
	}

	// layout
	private static final int DEFAULT_COL_COUNT = 2;
	private static final int DEFAULT_ROW_COUNT = 4;
	private static final int DEFAULT_GRID_GAP = 8; // gap between grids (dips)

	private static final int MAX_SETTLE_DURATION = 600; // ms
	private static final int MIN_DISTANCE_FOR_FLING = 25; // dips
	private static final int MIN_FLING_VELOCITY = 400; // dips
	private static final int CLOSE_ENOUGH = 2; // dp

	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	private static final int INVALID_POINTER = -1;
	/**
	 * 滾動空閒
	 * **/
	public static final int SCROLL_STATE_IDLE = 0;
	/**
	 * 拖拽中
	 * */
	public static final int SCROLL_STATE_DRAGGING = 1;
	/**
	 * 滚动完成
	 * */
	public static final int SCROLL_STATE_SETTLING = 2;

	private static final long LONG_CLICK_DURATION = 1000; // ms
	private static final long ANIMATION_DURATION = 150; // ms

	private static final int EDGE_LFET = 0;
	private static final int EDGE_RIGHT = 1;

	private static final long EDGE_HOLD_DURATION = 1200; // ms
	/**
	 * 列
	 * */
	private int mColCount = DEFAULT_COL_COUNT;
	/***
	 * 行
	 **/
	private int mRowCount = DEFAULT_ROW_COUNT;
	/**
	 * 頁大小
	 * */
	private int mPageSize = mColCount * mRowCount;
	/**
	 * view 间隔
	 * */
	private int mGridGap;

	private int mPageCount;
	private int mGridWidth;
	private int mGridHeight;
	/**
	 * 最大滚动大小
	 * */
	private int mMaxOverScrollSize;
	private int mEdgeSize;

	// internal paddings
	private int mPaddingLeft;
	private int mPaddingTop;
	private int mPaddingRight;
	private int mPaddingButtom;
	/***
	 * 当前显示页面的索引
	 * */
	private int mCurItem; // Index of currently displayed page.
	private Adapter mAdapter;
	private final DataSetObserver mDataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			dataSetChanged();
		}

		@Override
		public void onInvalidated() {
			dataSetChanged();
		}
	};
	/***
	 * 滚动操作类
	 * */
	private Scroller mScroller;
	/**
	 * 滚动缓存
	 * */
	private boolean mScrollingCacheEnabled;

	private boolean mIsBeingDragged;
	private boolean mIsUnableToDrag;
	/**
	 * 触发移动事件的最短距离
	 * */
	private int mTouchSlop;

	private float mLastMotionX;
	private float mLastMotionY;
	private float mInitialMotionX;
	private float mInitialMotionY;
	private int mActivePointerId = INVALID_POINTER;
	/**
	 * 主要用跟踪触摸屏事件（flinging事件和其他gestures手势事件）的速率
	 * **/
	private VelocityTracker mVelocityTracker;
	private int mMinimumVelocity;
	/**
	 * fling手势动作的最大速度值
	 * */
	private int mMaximumVelocity;
	private int mFlingDistance;
	private int mCloseEnough;

	// click & long click
	/***
	 * 选中对象下标
	 * */
	private int mLastPosition = -1;
	private long mLastDownTime = Long.MAX_VALUE;

	// rearrange
	/**
	 * 被拖动对象
	 * */
	private int mLastDragged = -1;
	private int mLastTarget = -1;

	// edge holding
	private int mLastEdge = -1;
	private long mLastEdgeTime = Long.MAX_VALUE;
	/**
	 * 新坐标集
	 * */
	private ArrayList<Integer> newPositions = new ArrayList<Integer>();

	private boolean mCalledSuper;

	private OnPageChangeListener mOnPageChangeListener;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;
	private OnRearrangeListener mOnRearrangeListener;

	private final Runnable mEndScrollRunnable = new Runnable() {
		public void run() {
			setScrollState(SCROLL_STATE_IDLE);
		}
	};

	private int mScrollState = SCROLL_STATE_IDLE;

	/**
	 * Callback interface for responding to changing state of the selected page.
	 */
	public interface OnPageChangeListener {

		/**
		 * This method will be invoked when the current page is scrolled, either
		 * as part of a programmatically initiated smooth scroll or a user
		 * initiated touch scroll.
		 * 
		 * @param position
		 *            Position index of the first page currently being
		 *            displayed. Page position+1 will be visible if
		 *            positionOffset is nonzero.
		 * @param positionOffset
		 *            Value from [0, 1) indicating the offset from the page at
		 *            position.
		 * @param positionOffsetPixels
		 *            Value in pixels indicating the offset from position.
		 */
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels);

		/**
		 * This method will be invoked when a new page becomes selected.
		 * Animation is not necessarily complete.
		 * 
		 * @param position
		 *            Position index of the new selected page.
		 */
		public void onPageSelected(int position);

		/**
		 * Called when the scroll state changes. Useful for discovering when the
		 * user begins dragging, when the pager is automatically settling to the
		 * current page, or when it is fully stopped/idle.
		 * 
		 * @param state
		 *            The new scroll state.
		 * @see DraggableGridViewPager#SCROLL_STATE_IDLE
		 * @see DraggableGridViewPager#SCROLL_STATE_DRAGGING
		 * @see DraggableGridViewPager#SCROLL_STATE_SETTLING
		 */
		public void onPageScrollStateChanged(int state);
	}

	/**
	 * Simple implementation of the {@link OnPageChangeListener} interface with
	 * stub implementations of each method. Extend this if you do not intend to
	 * override every method of {@link OnPageChangeListener}.
	 */
	public static class SimpleOnPageChangeListener implements
			OnPageChangeListener {
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// This space for rent
		}

		@Override
		public void onPageSelected(int position) {
			// This space for rent
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// This space for rent
		}
	}

	public interface OnRearrangeListener {
		public abstract void onRearrange(int oldIndex, int newIndex);
	}

	public DraggableGridViewPager(Context context) {
		super(context);
		initDraggableGridViewPager();
	}

	public DraggableGridViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDraggableGridViewPager();
	}

	public DraggableGridViewPager(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initDraggableGridViewPager();
	}

	private void initDraggableGridViewPager() {
		// 设置调用OnDraw绘画
		setWillNotDraw(false);
		// 设置焦点先分发给Child View进行处理，如果所有的Child View都没有处理，则自己再处理
		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		// 设置可获取焦点
		setFocusable(true);
		// 啟動子元素排序 用於改變子元素的繪製順序
		setChildrenDrawingOrderEnabled(true);

		final Context context = getContext();
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		final float density = context.getResources().getDisplayMetrics().density;

		mGridGap = (int) (DEFAULT_GRID_GAP * density);

		// internal paddings
		mPaddingLeft = getPaddingLeft();
		mPaddingTop = getPaddingTop();
		mPaddingRight = getPaddingRight();
		mPaddingButtom = getPaddingBottom();
		super.setPadding(0, 0, 0, 0);

		mScroller = new Scroller(context, sInterpolator);
		mTouchSlop = ViewConfigurationCompat
				.getScaledPagingTouchSlop(configuration);
		mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
		mCloseEnough = (int) (CLOSE_ENOUGH * density);
	}

	/**
	 * View 从可视窗口被移除的时候 也就是被销毁的时候
	 * **/
	@Override
	protected void onDetachedFromWindow() {
		// 取消注册的线程
		removeCallbacks(mEndScrollRunnable);
		// 取消注册的监听器
		if (mAdapter != null) {
			try {
				mAdapter.unregisterDataSetObserver(mDataSetObserver);
			} catch (Exception e) {
			}
		}
		super.onDetachedFromWindow();
	}

	/***
	 * 获列
	 * */
	public int getColCount() {
		return mColCount;
	}

	/***
	 * 設置列
	 * */
	public void setColCount(int colCount) {
		if (colCount < 1) {
			colCount = 1;
		}
		mColCount = colCount;
		mPageSize = mColCount * mRowCount;
		requestLayout();
	}

	/***
	 * 获取行
	 * */
	public int getRowCount() {
		return mRowCount;
	}

	/***
	 * 設置行
	 * */
	public void setRowCount(int rowCount) {
		if (rowCount < 1) {
			rowCount = 1;
		}
		mRowCount = rowCount;
		mPageSize = mColCount * mRowCount;
		requestLayout();
	}

	/***
	 * 获取间距
	 * */
	public int getGridGap() {
		return mGridGap;
	}

	/**
	 * 设置间距
	 * **/
	public void setGridGap(int gridGap) {
		if (gridGap < 0) {
			gridGap = 0;
		}
		mGridGap = gridGap;
		requestLayout();
	}

	/**
	 * 获取页数
	 * */
	public int getPageCount() {
		return (getChildCount() + mPageSize - 1) / mPageSize;
	}

	private void measureScrapChild(int position, int widthSpec, int heightSpec,
			int[] measuredDimension) {
		if (position < getChildCount()) {
			View child = getChildAt(position);

			if (child != null) {
				ViewGroup.LayoutParams p = (ViewGroup.LayoutParams) child
						.getLayoutParams();

				int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
						getPaddingLeft() + getPaddingRight(), p.width);
				int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
						getPaddingTop() + getPaddingBottom(), p.height);
				child.measure(childWidthSpec, childHeightSpec);
				measuredDimension[0] = child.getMeasuredWidth();
				measuredDimension[1] = child.getMeasuredHeight();

			}

		}
	}

	private int[] mMeasuredDimension = new int[2];

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
		int width = 0;
		int height = 0;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			measureScrapChild(i, View.MeasureSpec.makeMeasureSpec(i,
					View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(i,
							View.MeasureSpec.UNSPECIFIED), mMeasuredDimension);
			if (i == 0) {
				width = mMeasuredDimension[0];
			} else if (i % mColCount == 0 && mColCount != 1) {
				width += mMeasuredDimension[0];
			}
			if (i == 0) {
				height = mMeasuredDimension[1];
			} else if (i % mRowCount == 0 && mRowCount != 1) {
				height += mMeasuredDimension[1];
			}

		}

		switch (widthMode) {
		case View.MeasureSpec.EXACTLY:
			width = widthSize;
		case View.MeasureSpec.AT_MOST:
		case View.MeasureSpec.UNSPECIFIED:
		}

		switch (heightMode) {
		case View.MeasureSpec.EXACTLY:
			height = heightSize;
		case View.MeasureSpec.AT_MOST:
		case View.MeasureSpec.UNSPECIFIED:
		}
		setMeasuredDimension(width, height);
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childCount = getChildCount();
		mPageCount = (childCount + mPageSize - 1) / mPageSize;
		mGridWidth = (getWidth() - mPaddingLeft - mPaddingRight - (mColCount - 1)
				* mGridGap)
				/ mColCount;
		mGridHeight = (getHeight() - mPaddingTop - mPaddingButtom - (mRowCount - 1)
				* mGridGap)
				/ mRowCount;

		mMaxOverScrollSize = mGridWidth / 2;
		mEdgeSize = mGridWidth / 2;
		newPositions.clear();
		// 设置大小 添加拖动下标标记
		for (int i = 0; i < childCount; i++) {
			final View child = getChildAt(i);
			final Rect rect = getRectByPosition(i);
			child.measure(MeasureSpec.makeMeasureSpec(rect.width(),
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					rect.height(), MeasureSpec.EXACTLY));
			DEBUG_LOG("child.layout position=" + i + ", rect=" + rect);
			child.layout(rect.left, rect.top, rect.right, rect.bottom);
			newPositions.add(-1);
		}
		// 设置当前页码
		if (mCurItem > 0 && mCurItem < mPageCount) {
			final int curItem = mCurItem;
			mCurItem = 0;
			setCurrentItem(curItem);
		}
	}

	/****
	 * 设置滚动状态
	 * */
	private void setScrollState(int newState) {
		if (mScrollState == newState) {
			return;
		}
		mScrollState = newState;
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(newState);
		}
	}

	public int getCurrentItem() {
		return mCurItem;
	}

	/**
	 * 设置当前选中页
	 * **/
	public void setCurrentItem(int item) {
		setCurrentItemInternal(item, false, false);
	}

	public void nextPage(boolean smoothScroll) {
		int position = getCurrentItem() + 1;
		if (position >= mPageCount)
			position = 0;
		setCurrentItem(position, smoothScroll);
	}

	/**
	 * 设置当前选中页
	 * 
	 * @param smoothScroll
	 *            是否滚动
	 * **/
	public void setCurrentItem(int item, boolean smoothScroll) {
		setCurrentItemInternal(item, smoothScroll, false);
	}

	/****
	 * 设置当前选中页
	 * */
	void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
		setCurrentItemInternal(item, smoothScroll, always, 0);
	}

	/**
	 * 设置当前选中页
	 * */
	void setCurrentItemInternal(int item, boolean smoothScroll, boolean always,
			int velocity) {
		if (mPageCount <= 0) {
			setScrollingCacheEnabled(false);
			return;
		}
		if (!always && mCurItem == item) {
			setScrollingCacheEnabled(false);
			return;
		}

		if (item < 0) {
			item = 0;
		} else if (item >= mPageCount) {
			item = mPageCount - 1;
		}
		final boolean dispatchSelected = mCurItem != item;
		mCurItem = item;
		scrollToItem(item, smoothScroll, velocity, dispatchSelected);
	}

	/****
	 * 滚动到对应页数
	 * */
	private void scrollToItem(int item, boolean smoothScroll, int velocity,
			boolean dispatchSelected) {
		final int destX = getWidth() * item;
		if (smoothScroll) {
			smoothScrollTo(destX, 0, velocity);
			if (dispatchSelected && mOnPageChangeListener != null) {
				mOnPageChangeListener.onPageSelected(item);
			}
		} else {
			if (dispatchSelected && mOnPageChangeListener != null) {
				mOnPageChangeListener.onPageSelected(item);
			}
			completeScroll(false);
			scrollTo(destX, 0);
			pageScrolled(destX);
		}
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mOnPageChangeListener = listener;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		mOnItemLongClickListener = listener;
	}

	public void setOnRearrangeListener(OnRearrangeListener listener) {
		mOnRearrangeListener = listener;
	}

	/**
	 * We want the duration of the page snap animation to be influenced by the
	 * distance that // the screen has to travel, however, we don＇t want this
	 * duration to be effected in a // purely linear fashion. Instead, we use
	 * this method to moderate the effect that the distance // of travel has on
	 * the overall snap duration.
	 **/
	float distanceInfluenceForSnapDuration(float f) {
		f -= 0.5f; // center the values about 0.
		f *= 0.3f * Math.PI / 2.0f;
		return (float) Math.sin(f);
	}

	void smoothScrollTo(int x, int y) {
		smoothScrollTo(x, y, 0);
	}

	/***
	 * 触发滚动动画
	 * 
	 * @param velocity
	 *            速度
	 * */
	void smoothScrollTo(int x, int y, int velocity) {
		if (getChildCount() == 0) {
			// Nothing to do.
			setScrollingCacheEnabled(false);
			return;
		}
		int sx = getScrollX();
		int sy = getScrollY();
		int dx = x - sx;
		int dy = y - sy;
		if (dx == 0 && dy == 0) {
			completeScroll(false);
			setScrollState(SCROLL_STATE_IDLE);
			return;
		}

		setScrollingCacheEnabled(true);
		setScrollState(SCROLL_STATE_SETTLING);

		final int width = getWidth();
		final int halfWidth = width / 2;
		// 利用公式计算出滑动时间
		final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
		final float distance = halfWidth + halfWidth
				* distanceInfluenceForSnapDuration(distanceRatio);

		int duration = 0;
		velocity = Math.abs(velocity);
		if (velocity > 0) {
			duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
		} else {
			final float pageDelta = (float) Math.abs(dx) / width;
			duration = (int) ((pageDelta + 1) * 100);
		}
		duration = Math.min(duration, MAX_SETTLE_DURATION);
		// 单独调用无效 主要是为了触发 computeScroll
		mScroller.startScroll(sx, sy, dx, dy, duration);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
			int oldX = getScrollX();
			int oldY = getScrollY();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();

			if (oldX != x || oldY != y) {
				scrollTo(x, y);
				// 若页面无法滚动 则停止滚动动画
				if (!pageScrolled(x)) {
					mScroller.abortAnimation();
					scrollTo(0, y);
				}
			}

			// 重绘
			ViewCompat.postInvalidateOnAnimation(this);
			return;
		}

		// 完成滚动,清理状态。
		completeScroll(true);
	}

	/**
	 * 頁面滾動
	 * */
	private boolean pageScrolled(int xpos) {
		// 触发页面滚动监听
		if (mPageCount <= 0) {
			mCalledSuper = false;
			onPageScrolled(0, 0, 0);
			if (!mCalledSuper) {
				throw new IllegalStateException(
						"onPageScrolled did not call superclass implementation");
			}
			return false;
		}
		final int width = getWidth();
		final int currentPage = xpos / width;
		final int offsetPixels = xpos - currentPage * width;
		final float pageOffset = (float) offsetPixels / (float) width;

		mCalledSuper = false;
		onPageScrolled(currentPage, pageOffset, offsetPixels);
		if (!mCalledSuper) {
			throw new IllegalStateException(
					"onPageScrolled did not call superclass implementation");
		}
		return true;
	}

	/**
	 * This method will be invoked when the current page is scrolled, either as
	 * part of a programmatically initiated smooth scroll or a user initiated
	 * touch scroll. If you override this method you must call through to the
	 * superclass implementation (e.g. super.onPageScrolled(position, offset,
	 * offsetPixels)) before onPageScrolled returns.
	 * 
	 * @param position
	 *            Position index of the first page currently being displayed.
	 *            Page position+1 will be visible if positionOffset is nonzero.
	 * @param offset
	 *            Value from [0, 1) indicating the offset from the page at
	 *            position.
	 * @param offsetPixels
	 *            Value in pixels indicating the offset from position.
	 */
	protected void onPageScrolled(int position, float offset, int offsetPixels) {
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener
					.onPageScrolled(position, offset, offsetPixels);
		}
		mCalledSuper = true;
	}

	/**
	 * 移动完成
	 * */
	private void completeScroll(boolean postEvents) {
		//
		if (mScrollState == SCROLL_STATE_SETTLING) {
			// Done with scroll, no longer want to cache view
			// drawing.完成了滚动,不再想绘图缓存视图。
			setScrollingCacheEnabled(false);
			// 停止动画
			mScroller.abortAnimation();
			// 如果位置没有对应上 滚动到对应位置
			int oldX = getScrollX();
			int oldY = getScrollY();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			if (oldX != x || oldY != y) {
				scrollTo(x, y);
			}

			if (postEvents) {
				// postOnAnimation 用于在系统进行下一次动画操作时，运行重置状态线程 的线程
				ViewCompat.postOnAnimation(this, mEndScrollRunnable);
			} else {
				mEndScrollRunnable.run();
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/*
		 * This method JUST determines whether we want to intercept the motion.
		 * If we return true, onMotionEvent will be called and we do the actual
		 * scrolling there.
		 */

		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

		// Always take care of the touch gesture being complete.
		// 重置释放 拖动变量
		if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_UP) {
			// Release the drag.
			DEBUG_LOG("Intercept done!");
			mIsBeingDragged = false;
			mIsUnableToDrag = false;
			mActivePointerId = INVALID_POINTER;
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			return false;
		}

		// Nothing more to do here if we have decided whether or not we
		// are dragging.
		/*
		 * 这里没有更多的事情要做,如果我们决定我们是否 拖动。
		 */
		if (action != MotionEvent.ACTION_DOWN) {
			if (mIsBeingDragged || mLastDragged >= 0) {
				DEBUG_LOG("Intercept returning true!");
				return true;
			}
			if (mIsUnableToDrag) {
				DEBUG_LOG("Intercept returning false!");
				return false;
			}
		}

		switch (action) {
		case MotionEvent.ACTION_MOVE: {
			/*
			 * mIsBeingDragged == false, otherwise the shortcut would have
			 * caught it. Check whether the user has moved far enough from his
			 * original down touch.
			 */

			/*
			 * Locally do absolute value. mLastMotionY is set to the y value of
			 * the down event.
			 */
			final int activePointerId = mActivePointerId;
			if (activePointerId == INVALID_POINTER) {
				// If we don't have a valid id, the touch down wasn't on
				// content.
				break;
			}

			final int pointerIndex = MotionEventCompat.findPointerIndex(ev,
					activePointerId);
			final float x = MotionEventCompat.getX(ev, pointerIndex);
			final float dx = x - mLastMotionX;
			final float xDiff = Math.abs(dx);
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			final float yDiff = Math.abs(y - mInitialMotionY);
			DEBUG_LOG("***Moved to " + x + "," + y + " diff=" + xDiff + ","
					+ yDiff);

			if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff) {
				DEBUG_LOG("***Starting drag!");
				mIsBeingDragged = true;
				requestParentDisallowInterceptTouchEvent(true);
				setScrollState(SCROLL_STATE_DRAGGING);
				mLastMotionX = dx > 0 ? mInitialMotionX + mTouchSlop
						: mInitialMotionX - mTouchSlop;
				mLastMotionY = y;
				setScrollingCacheEnabled(true);
			} else if (yDiff > mTouchSlop) {
				// The finger has moved enough in the vertical
				// direction to be counted as a drag... abort
				// any attempt to drag horizontally, to work correctly
				// with children that have scrolling containers.
				DEBUG_LOG("***Unable to drag!");
				mIsUnableToDrag = true;
			}
			if (mIsBeingDragged) {
				// Scroll to follow the motion event
				if (performDrag(x)) {
					ViewCompat.postInvalidateOnAnimation(this);
				}
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			/*
			 * Remember location of down touch. ACTION_DOWN always refers to
			 * pointer index 0.
			 */
			// 记录触摸位置
			mLastMotionX = mInitialMotionX = ev.getX();
			mLastMotionY = mInitialMotionY = ev.getY();
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			mIsUnableToDrag = false;

			mScroller.computeScrollOffset();
			/*
			 * 若是 mScroller 调用滚动进行时 一般是翻页 再次点击停止滚动动作 触发拖动动作
			 */
			if (mScrollState == SCROLL_STATE_SETTLING
					&& Math.abs(mScroller.getFinalX() - mScroller.getCurrX()) > mCloseEnough) {
				// Let the user 'catch' the pager as it animates.
				mScroller.abortAnimation();
				mIsBeingDragged = true;
				requestParentDisallowInterceptTouchEvent(true);
				setScrollState(SCROLL_STATE_DRAGGING);
			} else {
				completeScroll(false);
				mIsBeingDragged = false;
			}

			DEBUG_LOG("***Down at " + mLastMotionX + "," + mLastMotionY
					+ " mIsBeingDragged=" + mIsBeingDragged
					+ " mIsUnableToDrag=" + mIsUnableToDrag);
			mLastDragged = -1;
			break;
		}

		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		// 添加移动事件用于计算速率
		mVelocityTracker.addMovement(ev);
		/*
		 * 唯一一次我们想要拦截是如果我们在运动事件拖动模式。
		 */
		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		return mIsBeingDragged;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
			// Don't handle edge touches immediately -- they may actually belong
			// to one of our
			// descendants.
			return false;
		}

		if (mPageCount <= 0) {
			// Nothing to present or scroll; nothing to touch.
			return false;
		}
		// 添加记录 滚动轨迹
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		boolean needsInvalidate = false;

		switch (action & MotionEventCompat.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			// 停止滚动动画
			mScroller.abortAnimation();
			// Remember where the motion event started
			// 记录开始坐标
			mLastMotionX = mInitialMotionX = ev.getX();
			mLastMotionY = mInitialMotionY = ev.getY();
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);

			DEBUG_LOG("Down at " + mLastMotionX + "," + mLastMotionY
					+ " mIsBeingDragged=" + mIsBeingDragged
					+ " mIsUnableToDrag=" + mIsUnableToDrag);
			// 若是在滚动空闲中 获取点击到的 下标
			if (!mIsBeingDragged && mScrollState == SCROLL_STATE_IDLE) {
				mLastPosition = getPositionByXY((int) mLastMotionX,
						(int) mLastMotionY);
			} else {
				mLastPosition = -1;
			}

			// 记录按下时间
			if (mLastPosition >= 0) {
				mLastDownTime = System.currentTimeMillis();
			} else {
				mLastDownTime = Long.MAX_VALUE;
			}
			DEBUG_LOG("Down at mLastPosition=" + mLastPosition);
			mLastDragged = -1;
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev,
					mActivePointerId);
			final float x = MotionEventCompat.getX(ev, pointerIndex);
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			// 子控件拖动中
			if (mLastDragged >= 0) {
				// change draw location of dragged visual
				// 获取选中对象 移动位置
				final View v = getChildAt(mLastDragged);
				final int l = getScrollX() + (int) x - v.getWidth() / 2;
				final int t = getScrollY() + (int) y - v.getHeight() / 2;
				v.layout(l, t, l + v.getWidth(), t + v.getHeight());

				// check for new target hover
				// 检查新的目标
				if (mScrollState == SCROLL_STATE_IDLE) {
					final int target = getTargetByXY((int) x, (int) y);
					// 若位置部位本身 运行替换动画
					if (target != -1 && mLastTarget != target) {
						animateGap(target);
						mLastTarget = target;
						DEBUG_LOG("Moved to mLastTarget=" + mLastTarget);
					}
					// edge holding
					/**
					 * 获取在哪个边缘 记录边缘时间
					 * */
					final int edge = getEdgeByXY((int) x, (int) y);
					if (mLastEdge == -1) {
						if (edge != mLastEdge) {
							mLastEdge = edge;
							mLastEdgeTime = System.currentTimeMillis();
						}
					} else {
						// 若在同一個邊緣呆的時間超過 1.2秒 則執行翻頁
						if (edge != mLastEdge) {
							mLastEdge = -1;
						} else {
							if ((System.currentTimeMillis() - mLastEdgeTime) >= EDGE_HOLD_DURATION) {
								// 启动震动反馈
								performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
								triggerSwipe(edge);
								mLastEdge = -1;
							}
						}
					}
				}
			}
			// 若不在拖動中
			else if (!mIsBeingDragged) {
				final float xDiff = Math.abs(x - mLastMotionX);
				final float yDiff = Math.abs(y - mLastMotionY);
				DEBUG_LOG("Moved to " + x + "," + y + " diff=" + xDiff + ","
						+ yDiff);
				// 大于最小移动距离 等条件的话修改状态为拖动中
				if (xDiff > mTouchSlop && xDiff > yDiff) {
					DEBUG_LOG("Starting drag!");
					mIsBeingDragged = true;
					requestParentDisallowInterceptTouchEvent(true);
					mLastMotionX = x - mInitialMotionX > 0 ? mInitialMotionX
							+ mTouchSlop : mInitialMotionX - mTouchSlop;
					mLastMotionY = y;
					setScrollState(SCROLL_STATE_DRAGGING);
					setScrollingCacheEnabled(true);
				}
			}
			// Not else! Note that mIsBeingDragged can be set above.

			if (mIsBeingDragged) {
				// Scroll to follow the motion event
				// 滚动运动事件
				needsInvalidate |= performDrag(x);
			}
			// 若没有发生页面滚动事件的话 按下時候選中的下標被选中
			else if (mLastPosition >= 0) {
				final int currentPosition = getPositionByXY((int) x, (int) y);
				DEBUG_LOG("Moved to currentPosition=" + currentPosition);
				// 选中下标没有移动位置
				if (currentPosition == mLastPosition) {

					// 选中时间大于1S才算选中
					if ((System.currentTimeMillis() - mLastDownTime) >= LONG_CLICK_DURATION) {
						if (onItemLongClick(currentPosition)) {
							performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
							mLastDragged = mLastPosition;
							requestParentDisallowInterceptTouchEvent(true);
							mLastTarget = -1;
							animateDragged();
							mLastPosition = -1;
						}
						mLastDownTime = Long.MAX_VALUE;
					}
				} else {
					mLastPosition = -1;
				}
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			DEBUG_LOG("Touch up!!!");
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev,
					mActivePointerId);
			final float x = MotionEventCompat.getX(ev, pointerIndex);
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			// 若是排列过 则重新排列
			if (mLastDragged >= 0) {
				rearrange();
			} else if
			// 若是拖动 计算移动距离并启动动画
			(mIsBeingDragged) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(
						velocityTracker, mActivePointerId);
				final int width = getWidth();
				final int scrollX = getScrollX();
				final int currentPage = scrollX / width;
				final int offsetPixels = scrollX - currentPage * width;
				final float pageOffset = (float) offsetPixels / (float) width;
				final int totalDelta = (int) (x - mInitialMotionX);
				int nextPage = determineTargetPage(currentPage, pageOffset,
						initialVelocity, totalDelta);
				setCurrentItemInternal(nextPage, true, true, initialVelocity);

				mActivePointerId = INVALID_POINTER;
				endDrag();
			} else if (mLastPosition >= 0) {
				final int currentPosition = getPositionByXY((int) x, (int) y);
				DEBUG_LOG("Touch up!!! currentPosition=" + currentPosition);
				if (currentPosition == mLastPosition) {
					onItemClick(currentPosition);
				}
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL:
			DEBUG_LOG("Touch cancel!!!");
			// 若有排列过 重置 排列
			if (mLastDragged >= 0) {
				rearrange();
			} else
			// 若拖动过恢复界面
			if (mIsBeingDragged) {
				scrollToItem(mCurItem, true, 0, false);
				mActivePointerId = INVALID_POINTER;
				endDrag();
			}
			break;
		case MotionEventCompat.ACTION_POINTER_DOWN: {
			// 第二个触点按下 切换触点切换位置
			final int index = MotionEventCompat.getActionIndex(ev);
			final float x = MotionEventCompat.getX(ev, index);
			mLastMotionX = x;
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			break;
		}
		case MotionEventCompat.ACTION_POINTER_UP:
			// 一个触点离开 切换触点
			onSecondaryPointerUp(ev);
			mLastMotionX = MotionEventCompat.getX(ev,
					MotionEventCompat.findPointerIndex(ev, mActivePointerId));
			break;
		}
		if (needsInvalidate) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
		return true;
	}

	/***
	 * 设置是否拦截子控件的触发
	 * */
	private void requestParentDisallowInterceptTouchEvent(
			boolean disallowIntercept) {
		final ViewParent parent = getParent();
		if (parent != null) {
			parent.requestDisallowInterceptTouchEvent(disallowIntercept);
		}
	}

	/***
	 * 执行拖动
	 * */
	private boolean performDrag(float x) {
		boolean needsInvalidate = false;
		// 计算原点和拖动点间距
		final float deltaX = mLastMotionX - x;
		// 修改原点为拖动点
		mLastMotionX = x;

		// 計算拖動長度
		float oldScrollX = getScrollX();
		float scrollX = oldScrollX + deltaX;
		final int width = getWidth();

		float leftBound = width * 0;
		float rightBound = width * (mPageCount - 1);

		// 限制邊界
		if (scrollX < leftBound) {
			final float over = Math
					.min(leftBound - scrollX, mMaxOverScrollSize);
			scrollX = leftBound - over;
		} else if (scrollX > rightBound) {
			final float over = Math.min(scrollX - rightBound,
					mMaxOverScrollSize);
			scrollX = rightBound + over;
		}
		// Don't lose the rounded component
		// 加上精度
		mLastMotionX += scrollX - (int) scrollX;
		scrollTo((int) scrollX, getScrollY());
		// 触发页面滚动监听
		pageScrolled((int) scrollX);

		return needsInvalidate;
	}

	/**
	 * 確定目標頁
	 * **/
	private int determineTargetPage(int currentPage, float pageOffset,
			int velocity, int deltaX) {
		int targetPage;
		if (Math.abs(deltaX) > mFlingDistance
				&& Math.abs(velocity) > mMinimumVelocity) {
			targetPage = velocity > 0 ? currentPage : currentPage + 1;
		} else {
			final float truncator = currentPage >= mCurItem ? 0.4f : 0.6f;
			targetPage = (int) (currentPage + pageOffset + truncator);
		}
		return targetPage;
	}

	/**
	 * 多点触控 一个点离开时切换到另一个点
	 * **/
	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
			mActivePointerId = MotionEventCompat.getPointerId(ev,
					newPointerIndex);
			if (mVelocityTracker != null) {
				mVelocityTracker.clear();
			}
		}
	}

	/**
	 * 结束拖动
	 * **/
	private void endDrag() {
		mIsBeingDragged = false;
		mIsUnableToDrag = false;

		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	/***
	 * 设置 子View 是否开启 缓存 有利于绘图速度
	 * */
	private void setScrollingCacheEnabled(boolean enabled) {
		if (mScrollingCacheEnabled != enabled) {
			mScrollingCacheEnabled = enabled;
			if (USE_CACHE) {
				final int size = getChildCount();
				for (int i = 0; i < size; ++i) {
					final View child = getChildAt(i);
					if (child.getVisibility() != GONE) {
						child.setDrawingCacheEnabled(enabled);
					}
				}
			}
		}
	}

	/***
	 * 數據改變重置界面
	 * */
	private void dataSetChanged() {
		DEBUG_LOG("dataSetChanged");
		for (int i = 0; i < getChildCount() && i < mAdapter.getCount(); i++) {
			final View child = getChildAt(i);
			final View newChild = mAdapter.getView(i, child, this);
			if (newChild != child) {
				removeViewAt(i);
				addView(newChild, i);
			}
		}
		for (int i = getChildCount(); i < mAdapter.getCount(); i++) {
			final View child = mAdapter.getView(i, null, this);
			addView(child);
		}
		while (getChildCount() > mAdapter.getCount()) {
			removeViewAt(getChildCount() - 1);
		}
	}

	public void setAdapter(Adapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
			removeAllViews();
			mCurItem = 0;
			scrollTo(0, 0);
		}
		mAdapter = adapter;
		if (mAdapter != null) {
			mAdapter.registerDataSetObserver(mDataSetObserver);
			for (int i = 0; i < mAdapter.getCount(); i++) {
				final View child = mAdapter.getView(i, null, this);
				addView(child);
			}
		}
	}

	/***
	 * 获取坐标矩陣
	 **/
	private Rect getRectByPosition(int position) {
		final int page = position / mPageSize;
		final int col = (position % mPageSize) % mColCount;
		final int row = (position % mPageSize) / mColCount;
		final int left = getWidth() * page + mPaddingLeft + col
				* (mGridWidth + mGridGap);
		final int top = mPaddingTop + row * (mGridHeight + mGridGap);
		return new Rect(left, top, left + mGridWidth, top + mGridHeight);
	}

	/***
	 * 获取被点击的位置的下標
	 * */
	private int getPositionByXY(int x, int y) {
		final int col = (x - mPaddingLeft) / (mGridWidth + mGridGap);
		final int row = (y - mPaddingTop) / (mGridHeight + mGridGap);
		if (x < mPaddingLeft
				|| x >= (mPaddingLeft + col * (mGridWidth + mGridGap) + mGridWidth)
				|| y < mPaddingTop
				|| y >= (mPaddingTop + row * (mGridHeight + mGridGap) + mGridHeight)
				|| col < 0 || col >= mColCount || row < 0 || row >= mRowCount) {
			// touch in padding
			return -1;
		}
		final int position = mCurItem * mPageSize + row * mColCount + col;
		if (position < 0 || position >= getChildCount()) {
			// empty item
			return -1;
		}
		return position;
	}

	/**
	 * 替换目标
	 * */
	private int getTargetByXY(int x, int y) {
		// 获取选中对象
		final int position = getPositionByXY(x, y);
		if (position < 0) {
			return -1;
		}
		// 获取对应矩阵
		final Rect r = getRectByPosition(position);
		final int page = position / mPageSize;
		// 让矩阵的判断范围变小 不那么容易被替换
		r.inset(r.width() / 4, r.height() / 4);
		// 偏移对应的页数
		r.offset(-getWidth() * page, 0);
		// 若不包含 不返回下标
		if (!r.contains(x, y)) {
			return -1;
		}
		return position;
	}

	private void onItemClick(int position) {
		DEBUG_LOG("onItemClick position=" + position);
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(null, getChildAt(position),
					position, position / mColCount);
		}
	}

	private boolean onItemLongClick(int position) {
		DEBUG_LOG("onItemLongClick position=" + position);
		if (mOnItemLongClickListener != null) {
			return mOnItemLongClickListener.onItemLongClick(null,
					getChildAt(position), position, position / mColCount);
		}
		return false;
	}

	/**
	 * 返回要绘制的子元素在当前迭代中的索引。如果你需要修改子元素的绘制顺序，可以覆写此方法。默认情况下返回参数i值。
	 * 　注意：为使此方法被调用，你必须先通过调用setChildrenDrawingOrderEnabled(boolean)启用子元素排序
	 **/
	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (mLastDragged == -1) {
			return i;
		} else if (i == childCount - 1) {
			return mLastDragged;
		} else if (i >= mLastDragged) {
			return i + 1;
		}
		return i;
	}

	/***
	 * 启动选中动画
	 * **/
	private void animateDragged() {
		if (mLastDragged >= 0) {
			final View v = getChildAt(mLastDragged);

			final Rect r = new Rect(v.getLeft(), v.getTop(), v.getRight(),
					v.getBottom());
			r.inset(-r.width() / 20, -r.height() / 20);
			v.measure(MeasureSpec.makeMeasureSpec(r.width(),
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					r.height(), MeasureSpec.EXACTLY));
			v.layout(r.left, r.top, r.right, r.bottom);

			AnimationSet animSet = new AnimationSet(true);
			ScaleAnimation scale = new ScaleAnimation(0.9091f, 1, 0.9091f, 1,
					v.getWidth() / 2, v.getHeight() / 2);
			scale.setDuration(ANIMATION_DURATION);
			AlphaAnimation alpha = new AlphaAnimation(1, .5f);
			alpha.setDuration(ANIMATION_DURATION);

			animSet.addAnimation(scale);
			animSet.addAnimation(alpha);
			animSet.setFillEnabled(true);
			animSet.setFillAfter(true);

			v.clearAnimation();
			v.startAnimation(animSet);
		}
	}

	/**
	 * 移动动画
	 * */
	private void animateGap(int target) {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			// 忽视拖动对象
			if (i == mLastDragged) {
				continue;
			}
			// 新坐标
			int newPos = i;
			// 如替换 坐标在 当前坐标后面 的话 替换位置为上一个 否则为下一个
			if (mLastDragged < target && i >= mLastDragged + 1 && i <= target) {
				newPos--;
			} else if (target < mLastDragged && i >= target && i < mLastDragged) {
				newPos++;
			}
			// 旧坐標
			int oldPos = i;
			if (newPositions.get(i) != -1) {
				oldPos = newPositions.get(i);
			}

			if (oldPos == newPos) {
				continue;
			}

			// animate
			// 位移动画
			DEBUG_LOG("animateGap from=" + oldPos + ", to=" + newPos);
			final Rect oldRect = getRectByPosition(oldPos);
			final Rect newRect = getRectByPosition(newPos);
			oldRect.offset(-v.getLeft(), -v.getTop());
			newRect.offset(-v.getLeft(), -v.getTop());

			TranslateAnimation translate = new TranslateAnimation(oldRect.left,
					newRect.left, oldRect.top, newRect.top);
			translate.setDuration(ANIMATION_DURATION);
			translate.setFillEnabled(true);
			translate.setFillAfter(true);
			v.clearAnimation();
			v.startAnimation(translate);

			newPositions.set(i, newPos);
		}
	}

	/***
	 * 重新排列
	 * 
	 * */
	private void rearrange() {

		/**
		 * 若有移动过 替换对象位置 重置布局
		 * **/
		if (mLastDragged >= 0) {
			for (int i = 0; i < getChildCount(); i++) {
				getChildAt(i).clearAnimation();
			}
			if (mLastTarget >= 0 && mLastDragged != mLastTarget) {
				final View child = getChildAt(mLastDragged);
				removeViewAt(mLastDragged);
				addView(child, mLastTarget);
				if (mOnRearrangeListener != null) {
					mOnRearrangeListener.onRearrange(mLastDragged, mLastTarget);
				}
			}
			mLastDragged = -1;
			mLastTarget = -1;
			requestLayout();
			invalidate();
		}
	}

	/****
	 * 获取在哪个边缘
	 * */
	private int getEdgeByXY(int x, int y) {
		if (x < mEdgeSize) {
			return EDGE_LFET;
		} else if (x >= (getWidth() - mEdgeSize)) {
			return EDGE_RIGHT;
		}
		return -1;
	}

	/***
	 * 根据方向进行翻页
	 * */
	private void triggerSwipe(int edge) {
		if (edge == EDGE_LFET && mCurItem > 0) {
			setCurrentItem(mCurItem - 1, true);
		} else if (edge == EDGE_RIGHT && mCurItem < mPageCount - 1) {
			setCurrentItem(mCurItem + 1, true);
		}
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		mCurItem = savedState.curItem;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.curItem = mCurItem;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int curItem;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			curItem = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(curItem);
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

	public void nextPage() {
		// TODO Auto-generated method stub

	}

}
