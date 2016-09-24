package com.icloud.listenbook.ui.popup;

import com.icloud.wrzjh.base.utils.LogUtil;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public abstract class myBasePopupWindow extends PopupWindow {
	protected Activity ctx;
	protected View popView;

	public myBasePopupWindow(Activity ctx) {
		this.ctx = ctx;
		getPopupWindow();
		findViews();
		setListeners();
	}

	protected void init() {
		LayoutInflater mLayoutInflater = (LayoutInflater) ctx
				.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
		popView = mLayoutInflater.inflate(getLayout(), null);

		this.setContentView(popView);
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 这个是为了点击“返回Back”也能使其消失
		this.setBackgroundDrawable(new BitmapDrawable());
		// 使其聚集
		this.setFocusable(true);
		// 设置允许在外点击消失
		this.setOutsideTouchable(true);

	}

	protected abstract void setListeners();

	protected void findViews() {

	}

	protected View findViewById(int id) {
		return popView != null ? popView.findViewById(id) : null;
	}

	protected abstract int getLayout();

    
	protected void getPopupWindow() {
		if (null != this) {
			this.dismiss();
			return;
		} else {
			init();
		}
	}
}
