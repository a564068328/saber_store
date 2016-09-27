package com.icloud.listenbook.base.view;

/**
 * @author Vyshakh, Rahul
 *
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class CusView extends View {
	private int color = 0xffee6744;
	private int bgColor = 0xffC3CAD7;
	private Paint myPaint;
	private Paint myFramePaint;
	public TextView value;
	private float startAngle;
	public float temp;
	float sweepAngle;
	RectF rect;
	int pix = 200;
	Drawable[] ProDrawables = new Drawable[3];
	boolean isPlay;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			invalidate();
		}

	};

	public CusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setProgressImage(Drawable[] ProDrawables) {
		this.ProDrawables = ProDrawables;
		this.requestLayout();
	}

	private void init() {
		myPaint = new Paint();
		myPaint.setAntiAlias(true);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setColor(color);
		myPaint.setStrokeWidth(2);
		myFramePaint = new Paint();
		myFramePaint.setAntiAlias(true);
		myFramePaint.setStyle(Paint.Style.STROKE);
		myFramePaint.setColor(bgColor);
		myFramePaint.setStrokeWidth(1);
		reset();
	}

	public void setRect() {
		float startx = (float) (pix * 0.05);
		float endx = (float) (pix * 0.95);
		float starty = (float) (pix * 0.05);
		float endy = (float) (pix * 0.95);
		rect = new RectF(startx, starty, endx, endy);
		for (Drawable drawable : ProDrawables) {
			if (drawable != null) {
				drawable.setBounds(0, 0, pix, pix);
			}
		}
	}

	public void setProgress(int progress) {
		sweepAngle = (float) (progress * 3.6);
		handler.obtainMessage().sendToTarget();
		// this.invalidate();
	}

	private void getWH() {
		for (Drawable drawable : ProDrawables) {
			if (drawable != null) {
				if (pix > drawable.getMinimumHeight())
					pix = drawable.getMinimumHeight();

				if (pix > drawable.getMinimumWidth())
					pix = drawable.getMinimumWidth();

			}
		}
	}

	public void reset() {
		isPlay = false;
		sweepAngle = 0;
		startAngle = -90;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		getWH();
		int desiredWidth = pix;
		int desiredHeight = pix;
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(desiredWidth, widthSize);
		} else {
			width = desiredWidth;
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(desiredHeight, heightSize);
		} else {
			height = desiredHeight;
		}
		if (width != height) {
			if (height > width) {
				height = width;
			} else {
				width = height;
			}
		}
		pix = width;
		setRect();
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		/** 下载完成状态 */
		if (sweepAngle == 360) {
			if (ProDrawables[2] != null) {
				ProDrawables[2].draw(canvas);
			}
		} else
		/** 开始状态 */
		if (sweepAngle != 0 && sweepAngle != 360 || isPlay) {
			if (ProDrawables[1] != null && !isPlay && sweepAngle != 360)
				ProDrawables[1].draw(canvas);
			canvas.drawArc(rect, 0, 360, false, myFramePaint);
			canvas.drawArc(rect, startAngle, sweepAngle, false, myPaint);
		} else
		/** 未下载状态 **/
		if (sweepAngle == 0) {
			if (ProDrawables[0] != null) {
				ProDrawables[0].draw(canvas);
			}
		}

	}

	public void setStatus(boolean isPlay) {
		this.isPlay = isPlay;
	}

	public void init(int[] drawId) {
		Drawable[] proDraw = new Drawable[drawId.length];
		for (int i = 0; i < drawId.length; i++) {
			proDraw[i] = getResources().getDrawable(drawId[i]);
		}
		this.ProDrawables = proDraw;
		this.requestLayout();
		setProgress(0);
		setStatus(false);
	}
}
