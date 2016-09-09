package com.sy.zhbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 不能左右划的ViewPager
 * 
 * @author Kevin
 * 
 */
public class NoScrollViewPager extends ViewPager {

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollViewPager(Context context) {
		super(context);
	}

	// 表示事件是否拦截, 返回false表示不拦截,将事件给它的子控件
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}

	/**
	 * 重写onTouchEvent事件,使其什么都不用做（为了左右滑不切换页面）
	 */
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}
}
