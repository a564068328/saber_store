package com.icloud.listenbook.ui.popup;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

public abstract class BasePopupWindow extends PopupWindow {
	protected Activity ctx;
	protected View popView;

	public BasePopupWindow(Activity ctx) {
		this.ctx = ctx;
		init();
		findViews();
		setListeners();
	}

	protected abstract void setListeners();

	protected void init() {
		LayoutInflater mLayoutInflater = (LayoutInflater) ctx
				.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
		popView = mLayoutInflater.inflate(getLayout(), null);

		this.setContentView(popView);
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);

	}

	protected void findViews() {

	}

	protected View findViewById(int id) {
		return popView != null ? popView.findViewById(id) : null;
	}

	protected abstract int getLayout();
	
	public void setPosition(View parent, int x, int y){
		this.showAtLocation(parent, Gravity.RIGHT, x, y); 
	}
}
