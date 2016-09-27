package com.icloud.listenbook.dialog;

import com.icloud.listenbook.R;
import com.icloud.wrzjh.base.utils.ViewUtils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class BaseDialog extends Dialog implements OnClickListener {
	public Context context;

	public BaseDialog(Context context) {
		super(context, R.style.base_gray_pop);
		this.context = context;
		setContentView(getLayout());
		init();
		findView();
		setListener();
		setData();
		setCanceledOnTouchOutside(true);
	}

	public void addListener(View bt) {
		bt.setOnClickListener(this);
		bt.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	protected abstract int getLayout();

	protected abstract void init();

	protected abstract void setListener();

	protected abstract void findView();

	protected abstract void setData();

}
