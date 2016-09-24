/*
 * Copyright (C) 2011 Patrik Akerfeldt
 * Copyright (C) 2011 Francisco Figueiredo Jr.
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

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.icloud.listenbook.R;

/**
 * A TitlePageIndicator is a PageIndicator which displays the title of left view
 * (if exist), the title of the current select view (centered) and the title of
 * the right view (if exist). When the user scrolls the ViewPager then titles
 * are also scrolled.
 */
public class FixedTitlePageIndicator extends View implements PageIndicator {
	public static final float TITLEPADDINGMAX = Float.MAX_VALUE;
	/**
	 * �ٷְ���ʾ ֵΪ0.25ʱ��ʾ.�����»���Ӧ����ȫ��ʧ�� ������Ļ�����ĺͱ�Ե��
	 */
	private static final float SELECTION_FADE_PERCENTAGE = 0.25f;

	/**
	 * �ٷְ���ʾ ֵΪ0.05ʱ��ʾ �x�е��ı���ԓ���P�] 10%֮������ĺͱ�Ե��
	 */
	private static final float BOLD_FADE_PERCENTAGE = 0.05f;

	/**
	 * ����һ��ö�e���춴����r���[��ģ�� 0 �]�И�ʽ 1 ������ 2 ����
	 * */
	public enum IndicatorStyle {

		None(0), Triangle(1), Underline(2);

		public final int value;

		private IndicatorStyle(int value) {
			this.value = value;
		}

		public static IndicatorStyle fromValue(int value) {
			for (IndicatorStyle style : IndicatorStyle.values()) {
				if (style.value == value) {
					return style;
				}
			}
			return null;
		}
	}

	/**
	 * ��������
	 * */
	ArrayList<RectF> bounds;
	/**
	 * viewPagerҕ�D����
	 * */
	private ViewPager mViewPager;
	/**
	 * viewPage ҳ�滬��������
	 * */
	private ViewPager.OnPageChangeListener mListener;
	/**
	 * biao�����ṩ��
	 * */
	private PagerAdapter mTitleProvider;
	/**
	 * ��ǰҳ��
	 * */
	private int mCurrentPage;
	/**
	 * ƫ����
	 * */
	private int mCurrentOffset;
	/**
	 * ����״̬
	 * */
	private int mScrollState;
	/**
	 * Text�Ļ���
	 * */
	private final Paint mPaintText;
	/**
	 * �Ƿ���w
	 * */
	private boolean mBoldText;
	/**
	 * һ�������ɫ
	 * */
	private int mColorText;
	/**
	 * �x�Еr����ɫ
	 * */
	private int mColorSelected;
	/**
	 * �D�����P
	 * */
	private Path mPath;
	/**
	 * �»��߻��� ҳ�Ż���
	 * */
	private final Paint mPaintFooterLine;
	/**
	 * �α���ʽ��ʽ����
	 * */
	private IndicatorStyle mFooterIndicatorStyle;

	/**
	 * ָʾ������
	 * */
	private final Paint mPaintFooterIndicator;
	/**
	 * ָʾ�����
	 * */
	private float mFooterIndicatorHeight;
	/***
	 * ָʾ������߾���
	 * */
	private float mFooterIndicatorUnderlinePadding;
	/**
	 * ҳ�ŵļ��
	 * */
	private float mFooterPadding;
	/**
	 * ������
	 * */
	private float mTitlePadding;
	/**
	 * �^���g��
	 * */
	private float mTopPadding;
	/** ������ ���� ���� ��ʾ������ */
	private float mClipPadding;
	/**
	 * ����ߵĿ��
	 * */
	private float mFooterLineHeight;
	private boolean isEndSpace;

	/**
	 * ����β���Ƿ��пռ� Ĭ��û�� ֻ���� ������ Fixedģʽ������
	 * */
	public void setIsEndSpace(boolean isEndSpace) {
		this.isEndSpace = isEndSpace;
	}

	/**
	 * ��ȡ�Ƿ���β���ռ�
	 * */
	public boolean getisEndSpace() {
		return isEndSpace;
	}

	public FixedTitlePageIndicator(Context context) {
		this(context, null);
	}

	public FixedTitlePageIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.vpiTitlePageIndicatorStyle);
	}

	public FixedTitlePageIndicator(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		// ����Դ����Ĭ��ֵ
		final Resources res = getResources();
		final int defaultFooterColor = res
				.getColor(R.color.default_title_indicator_footer_color);
		final float defaultFooterLineHeight = res
				.getDimension(R.dimen.default_title_indicator_footer_line_height);
		final int defaultFooterIndicatorStyle = res
				.getInteger(R.integer.default_title_indicator_footer_indicator_style);
		final float defaultFooterIndicatorHeight = res
				.getDimension(R.dimen.default_title_indicator_footer_indicator_height);
		final float defaultFooterIndicatorUnderlinePadding = res
				.getDimension(R.dimen.default_title_indicator_footer_indicator_underline_padding);
		final float defaultFooterPadding = res
				.getDimension(R.dimen.default_title_indicator_footer_padding);
		final int defaultSelectedColor = res
				.getColor(R.color.default_title_indicator_selected_color);
		final boolean defaultSelectedBold = res
				.getBoolean(R.bool.default_title_indicator_selected_bold);
		final int defaultTextColor = res
				.getColor(R.color.default_title_indicator_text_color);
		final float defaultTextSize = res
				.getDimension(R.dimen.default_title_indicator_text_size);
		final float defaultTitlePadding = res
				.getDimension(R.dimen.default_title_indicator_title_padding);
		final float defaultClipPadding = res
				.getDimension(R.dimen.default_title_indicator_clip_padding);
		final float defaultTopPadding = res
				.getDimension(R.dimen.default_title_indicator_top_padding);

		// ������ʽ����
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TitlePageIndicator, defStyle, 0);

		// �z���ɫ ���} ������
		mFooterLineHeight = a.getDimension(
				R.styleable.TitlePageIndicator_footerLineHeight,
				defaultFooterLineHeight);
		mFooterIndicatorStyle = IndicatorStyle.fromValue(a.getInteger(
				R.styleable.TitlePageIndicator_footerIndicatorStyle,
				defaultFooterIndicatorStyle));

		mFooterIndicatorHeight = a.getDimension(
				R.styleable.TitlePageIndicator_footerIndicatorHeight,
				defaultFooterIndicatorHeight);
		mFooterIndicatorUnderlinePadding = a.getDimension(
				R.styleable.TitlePageIndicator_footerIndicatorUnderlinePadding,
				defaultFooterIndicatorUnderlinePadding);
		mFooterPadding = a.getDimension(
				R.styleable.TitlePageIndicator_footerPadding,
				defaultFooterPadding);
		mTopPadding = a.getDimension(R.styleable.TitlePageIndicator_topPadding,
				defaultTopPadding);
		mTitlePadding = a.getDimension(
				R.styleable.TitlePageIndicator_titlePadding,
				defaultTitlePadding);
		mClipPadding = a.getDimension(
				R.styleable.TitlePageIndicator_clipPadding, defaultClipPadding);
		mColorSelected = a.getColor(
				R.styleable.TitlePageIndicator_selectedColor,
				defaultSelectedColor);
		mColorText = a.getColor(
				R.styleable.TitlePageIndicator_android_textColor,
				defaultTextColor);
		mBoldText = a.getBoolean(R.styleable.TitlePageIndicator_selectedBold,
				defaultSelectedBold);
		// ��ȡ ���������ɫ����
		final float textSize = a.getDimension(
				R.styleable.TitlePageIndicator_android_textSize,
				defaultTextSize);
		final int footerColor = a.getColor(
				R.styleable.TitlePageIndicator_footerColor, defaultFooterColor);
		mPaintText = new Paint();
		mPaintText.setTextSize(textSize);
		mPaintText.setAntiAlias(true);
		mPaintFooterLine = new Paint();
		// �O�Ø�ʽ�����+����
		mPaintFooterLine.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintFooterLine.setStrokeWidth(mFooterLineHeight);
		mPaintFooterLine.setColor(footerColor);
		mPaintFooterIndicator = new Paint();
		mPaintFooterIndicator.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintFooterIndicator.setColor(footerColor);

		a.recycle();
	}

	/**
	 * �@ȡ�����ɫ
	 * */
	public int getFooterColor() {
		return mPaintFooterLine.getColor();
	}

	/**
	 * �α���ɫ
	 * */
	public int getmFooterIndicatorColor() {
		return mPaintFooterIndicator.getColor();
	}

	/**
	 * �O������ / ָʾ�� �ɫ
	 * */
	public void setFooterColor(int footerColor) {
		setFooterColor(footerColor, true);
	}

	/**
	 * ָʾ����ɫ
	 * */
	public void setFooterIndicatorColor(int footerColor) {
		mPaintFooterIndicator.setColor(footerColor);
		invalidate();
	}

	/**
	 * �O������ / ָʾ�� �ɫ
	 * */
	public void setFooterColor(int footerColor, boolean IndicatorIsSame) {
		mPaintFooterLine.setColor(footerColor);
		if (IndicatorIsSame)
			mPaintFooterIndicator.setColor(footerColor);
		invalidate();
	}

	/**
	 * �»��ߵĿ��
	 * */
	public float getFooterLineHeight() {
		return mFooterLineHeight;
	}

	/**
	 * �����»��߿��
	 * */
	public void setFooterLineHeight(float footerLineHeight) {
		mFooterLineHeight = footerLineHeight;
		mPaintFooterLine.setStrokeWidth(mFooterLineHeight);
		invalidate();
	}

	/**
	 * ��ȡ ָʾ�����
	 * */
	public float getFooterIndicatorHeight() {
		return mFooterIndicatorHeight;
	}

	/**
	 * ����ָʾ�����
	 * */
	public void setFooterIndicatorHeight(float footerTriangleHeight) {
		mFooterIndicatorHeight = footerTriangleHeight;
		invalidate();
	}

	/**
	 * ��ȡҳ�������ֵľ���
	 * */
	public float getFooterIndicatorPadding() {
		return mFooterPadding;
	}

	/**
	 * ���� ҳ�������ֵľ���
	 * */
	public void setFooterIndicatorPadding(float footerIndicatorPadding) {
		mFooterPadding = footerIndicatorPadding;
		invalidate();
	}

	/**
	 * ��ȡָʾ������ʽ
	 * */
	public IndicatorStyle getFooterIndicatorStyle() {
		return mFooterIndicatorStyle;
	}

	/**
	 * ����ָʾ������ʽ
	 * */
	public void setFooterIndicatorStyle(IndicatorStyle indicatorStyle) {
		mFooterIndicatorStyle = indicatorStyle;
		invalidate();
	}

	/***
	 * ��ȡѡ�е���ɫ
	 **/
	public int getSelectedColor() {
		return mColorSelected;
	}

	/**
	 * ����ѡ�е���ɫ
	 * */
	public void setSelectedColor(int selectedColor) {
		mColorSelected = selectedColor;
		invalidate();
	}

	/**
	 * ��ȡ��ѡ��Ķ����Ƿ����
	 * */
	public boolean isSelectedBold() {
		return mBoldText;
	}

	/**
	 * ����ѡ��������Ƿ����
	 * */
	public void setSelectedBold(boolean selectedBold) {
		mBoldText = selectedBold;
		invalidate();
	}

	/**
	 * ��ȡ������ɫ
	 * */
	public int getTextColor() {
		return mColorText;
	}

	/** ����������ɫ */
	public void setTextColor(int textColor) {
		mPaintText.setColor(textColor);
		mColorText = textColor;
		invalidate();
	}

	/***
	 * ��ȡ������ɫ
	 * */
	public float getTextSize() {
		return mPaintText.getTextSize();
	}

	/**
	 * �������ִ�С
	 * */
	public void setTextSize(float textSize) {
		mPaintText.setTextSize(textSize);
		invalidate();
	}

	/**
	 * ��ȡ����߾�
	 * */
	public float getTitlePadding() {

		return this.mTitlePadding;
	}

	/**
	 * ���ñ���߾� ���� TITLEPADDINGMAX��Ԓ ���N��ֱ�����һ��� ������ ������ײ����
	 **/
	public void setTitlePadding(float titlePadding) {
		mTitlePadding = titlePadding;
		invalidate();
	}

	/**
	 * ��ȡ�ؼ�ͷ������
	 * */
	public float getTopPadding() {
		return this.mTopPadding;
	}

	/**
	 * ����ͷ������
	 * */
	public void setTopPadding(float topPadding) {
		mTopPadding = topPadding;
		invalidate();
	}

	/**
	 * ��ȡ�������� ���Ҽ��
	 * */
	public float getClipPadding() {
		return this.mClipPadding;
	}

	/**
	 * ���������� ���� ���� ��ʾ������
	 * */
	public void setClipPadding(float clipPadding) {
		mClipPadding = clipPadding;
		invalidate();
	}

	private void onDrawIsFixedTitle(Canvas canvas) {
		// ����߽�
		bounds = calculateFixedAllBounds(mPaintText);
		// ��ȡѡ��
		final int count = mViewPager.getAdapter().getCount();
		final float unitNum = (count > 3 ? 4f : count);
		final float halfWidth = getWidth() / 2f;
		final float unitWidth = getWidth() / unitNum;
		final int left = getLeft();
		final int width = getWidth();
		final int height = getHeight();
		final int right = left + width;
		int page = mCurrentPage;
		float offsetPercent;
		if (mCurrentOffset <= halfWidth) {
			offsetPercent = 1.0f * mCurrentOffset / width;
		} else {
			page += 1;
			offsetPercent = 1.0f * (width - mCurrentOffset) / width;
		}

		final float mOffset = unitWidth * (1.0f * mCurrentOffset / width);
		final boolean currentSelected = (offsetPercent <= SELECTION_FADE_PERCENTAGE);
		final boolean currentBold = (offsetPercent <= BOLD_FADE_PERCENTAGE);
		final float selectedPercent = (0.5f - offsetPercent) / 0.5f;

		// ��ǰ����ͼ����
		RectF curPageBound = bounds.get(mCurrentPage);
		final float curPageW = curPageBound.right - curPageBound.left;
		// Now draw views
		for (int i = 0; i < count; i++) {
			// Get the title
			RectF bound = bounds.get(i);
			// Only if one side is visible
			if ((bound.left > left && bound.left < right)
					|| (bound.right > left && bound.right < right)) {
				final boolean currentPage = (i == page);
				// Only set bold if we are within bounds
				mPaintText.setFakeBoldText(currentPage && currentBold
						&& mBoldText);

				// Draw text as unselected
				mPaintText.setColor(mColorText);
				canvas.drawText(mTitleProvider.getPageTitle(i).toString(),
						bound.left, bound.bottom + mTopPadding, mPaintText);

				// ������ѡ���ķ�Χ�ڻ��Ʋ�ͬ���ı�
				if (currentPage && currentSelected) {
					mPaintText.setColor(mColorSelected);
					mPaintText
							.setAlpha((int) ((mColorSelected >>> 24) * selectedPercent));
					canvas.drawText(mTitleProvider.getPageTitle(i).toString(),
							bound.left, bound.bottom + mTopPadding, mPaintText);
				}
			}
		}

		// ��ҳ����
		mPath = new Path();
		mPath.moveTo(0, height - mFooterLineHeight / 2f);
		mPath.lineTo(width, height - mFooterLineHeight / 2f);
		mPath.close();
		canvas.drawPath(mPath, mPaintFooterLine);

		switch (mFooterIndicatorStyle) {
		case Triangle: {

			float TriangleLetf = curPageBound.left
					+ +(curPageBound.right - curPageBound.left) / 2;
			TriangleLetf += mOffset;
			mPath = new Path();
			mPath.moveTo(TriangleLetf, height - mFooterLineHeight
					- mFooterIndicatorHeight);
			mPath.lineTo(TriangleLetf + mFooterIndicatorHeight, height
					- mFooterLineHeight);
			mPath.lineTo(TriangleLetf - mFooterIndicatorHeight, height
					- mFooterLineHeight);
			mPath.close();
			mPaintFooterIndicator.setAlpha((int) (0xFF * selectedPercent));
			canvas.drawPath(mPath, mPaintFooterIndicator);
			mPaintFooterIndicator.setAlpha(0xFF);
			break;
		}
		case Underline: {
			int index = (int) (curPageBound.left / unitWidth);
			final float TriangleLetf = unitWidth * index + mOffset
					+ mFooterIndicatorUnderlinePadding;
			final float TriangleRight = unitWidth * index + mOffset + unitWidth
					- mFooterIndicatorUnderlinePadding;
			mPath = new Path();
			mPath.moveTo(TriangleLetf, height - mFooterLineHeight);
			mPath.lineTo(TriangleRight, height - mFooterLineHeight);
			mPath.lineTo(TriangleRight, height - mFooterLineHeight
					- mFooterIndicatorHeight);
			mPath.lineTo(TriangleLetf, height - mFooterLineHeight
					- mFooterIndicatorHeight);
			mPath.close();

			mPaintFooterIndicator.setAlpha((int) (0xFF * selectedPercent));
			canvas.drawPath(mPath, mPaintFooterIndicator);
			mPaintFooterIndicator.setAlpha(0xFF);
			break;
		}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		onDrawIsFixedTitle(canvas);

	}

	private boolean onFixedTouchEvent(MotionEvent event) {
		if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				return true;
			case MotionEvent.ACTION_UP:
				if (bounds != null && !bounds.isEmpty()) {
					RectF curPageBound = bounds.get(mCurrentPage);
					if (curPageBound != null) {
						float left = event.getX();
						final int width = getWidth();
						final int count = mViewPager.getAdapter().getCount();
						final float UnitWidth = width
								/ (count > 3 ? 4f : count);
						final int clickMultiple = (int) (left / UnitWidth);
						final int curMultiple = (int) (curPageBound.left / UnitWidth);
						final int postion = mCurrentPage - curMultiple
								+ clickMultiple;
						if (postion >= 0 && postion != mCurrentPage
								&& postion < count)
							mViewPager.setCurrentItem(postion);

						return true;
					}

				}

			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return onFixedTouchEvent(event);

	}

	/**
	 * �������ʱ�ı߽�
	 * 
	 * @param curViewBound
	 *            current bounds.
	 * @param curViewWidth
	 *            width of the view.
	 */
	private void clipViewOnTheRight(RectF curViewBound, float curViewWidth,
			int right) {
		curViewBound.right = right - mClipPadding;
		curViewBound.left = curViewBound.right - curViewWidth;
	}

	/**
	 * �������ʱ�ı߽�
	 * 
	 * @param curViewBound
	 *            current bounds.
	 * @param curViewWidth
	 *            width of the view.
	 */
	private void clipViewOnTheLeft(RectF curViewBound, float curViewWidth,
			int left) {
		curViewBound.left = left + mClipPadding;
		curViewBound.right = mClipPadding + curViewWidth;
	}

	private ArrayList<RectF> calculateFixedAllBounds(Paint paint) {
		ArrayList<RectF> list = new ArrayList<RectF>();
		// Ϊÿһ����ͼ����һ������ (���û����ֵ�����һ����)
		final int count = mViewPager.getAdapter().getCount();
		final int width = getWidth();
		int unitNum = count > 3 ? 4 : count;
		final int unitWidth = width / unitNum;
		// ������ҳ��
		final int Page = mCurrentPage + 1;

		int multiple = Page - (unitNum - 1);

		multiple = multiple > 0 ? multiple : 0;

		// �ж��Ƿ� ���ж�����ԭ���� ҳ���Ƿ������ ���һҳ��ǰһҳ
		boolean isMode = Page >= (unitNum - 1);
		/**
		 * ���������׵Ļ� ��ô��ִ����������Ķ���
		 * */
		if (!isEndSpace && Page >= count - 1) {
			// ������ҳ�����
			if (Page == count)
				multiple -= 1;
			isMode = isMode && isEndSpace;
		}
		/*
		 * �Ƿ�����
		 */
		final float offsetPercent = 1.0f * mCurrentOffset / width;

		for (int i = 0; i < count; i++) {
			RectF bounds = calcBounds(i, paint);
			float w = (bounds.right - bounds.left);
			float h = (bounds.bottom - bounds.top);
			bounds.left = (unitWidth * i) + ((unitWidth - w) / 2) - multiple
					* unitWidth;
			if (isMode)
				bounds.left -= offsetPercent * unitWidth;
			bounds.right = bounds.left + w;
			bounds.top = 0;
			bounds.bottom = h;
			list.add(bounds);
		}

		return list;
	}

	/**
	 * ������ͼ�ı߽����ڹ���
	 * 
	 * @param paint
	 * @return
	 */
	private ArrayList<RectF> calculateSlideAllBounds(Paint paint) {
		ArrayList<RectF> list = new ArrayList<RectF>();
		// Ϊÿһ����ͼ����һ������ (���û����ֵ�����һ����)
		final int count = mViewPager.getAdapter().getCount();
		final int width = getWidth();
		final int halfWidth = width / 2;
		for (int i = 0; i < count; i++) {
			RectF bounds = calcBounds(i, paint);
			float w = (bounds.right - bounds.left);
			float h = (bounds.bottom - bounds.top);
			// ��ѡ�еĶ�����Զ�����м�
			bounds.left = (halfWidth) - (w / 2) - mCurrentOffset
					+ ((i - mCurrentPage) * width);
			bounds.right = bounds.left + w;
			bounds.top = 0;
			bounds.bottom = h;
			list.add(bounds);
		}

		return list;
	}

	/**
	 * ����ָ������ı߽�
	 * 
	 * @param index
	 * @param paint
	 * @return
	 */
	private RectF calcBounds(int index, Paint paint) {
		// Calculate the text bounds
		RectF bounds = new RectF();
		bounds.right = paint.measureText(mTitleProvider.getPageTitle(index)
				.toString());
		bounds.bottom = paint.descent() - paint.ascent();
		return bounds;
	}

	@Override
	public void setViewPager(ViewPager view) {
		if (view.getAdapter() == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}
		if (!(view.getAdapter() instanceof PagerAdapter)) {
			throw new IllegalStateException(
					"ViewPager adapter must implement TitleProvider to be used with TitlePageIndicator.");
		}
		mViewPager = view;
		mViewPager.setOnPageChangeListener(this);
		mTitleProvider = (PagerAdapter) mViewPager.getAdapter();
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
	public void onPageScrollStateChanged(int state) {
		mScrollState = state;

		if (mListener != null) {
			mListener.onPageScrollStateChanged(state);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		mCurrentPage = position;
		mCurrentOffset = positionOffsetPixels;
		invalidate();

		if (mListener != null) {
			mListener.onPageScrolled(position, positionOffset,
					positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {
		if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
			mCurrentPage = position;
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
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * View�Ŀ��
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		// ��û�����ô�Сֱ�ӱ���
		if (specMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(getClass().getSimpleName()
					+ " can only be used in EXACTLY mode.");
		}
		result = specSize;
		return result;
	}

	/**
	 * View�Ŀ��
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return ����Լ����ÿ��
	 */
	private int measureHeight(int measureSpec) {
		float result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// ��Ϊ���úõĴ�Сֱ�Ӹ���
			result = specSize;
		} else {
			// �����ı�����
			RectF bounds = new RectF();
			// �����ֻ��� �Ŀ�� ��ȵļ���=�����»���-�����ϻ���
			bounds.bottom = mPaintText.descent() - mPaintText.ascent();
			result = bounds.bottom - bounds.top + mFooterLineHeight
					+ mFooterPadding + mTopPadding;
			// ������û�� ָʾ�� �����ָʾ���Ĵ�С
			if (mFooterIndicatorStyle != IndicatorStyle.None) {
				result += mFooterIndicatorHeight;
			}
		}
		return (int) result;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		mCurrentPage = savedState.currentPage;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPage = mCurrentPage;
		return savedState;
	}

	/**
	 * �α걣����
	 * **/
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

	@Override
	public void notifyDataSetChanged() {
		invalidate();

	}

}
