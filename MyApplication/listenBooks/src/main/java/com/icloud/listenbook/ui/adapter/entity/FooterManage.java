package com.icloud.listenbook.ui.adapter.entity;

import android.view.View;

import com.icloud.listenbook.R;

public class FooterManage {
	public static final int FREE = 0;
	public static final int LOADING = FREE + 1;
	public static final int NULL_DATAS = LOADING + 1;
	public static final int ZERO_DATAS = NULL_DATAS + 1;
	public FooterHolder footer;
	int status;

	public FooterManage() {
		status = FREE;
	}

	public void setStatus(int status) {
		this.status = status;
		if (footer == null)
			return;
		switch (status) {
		default:
		case FREE:
			footer.view.setVisibility(View.GONE);
			break;
		case LOADING:
			footer.view.setVisibility(View.VISIBLE);
			footer.txt.setText(R.string.loading);
			break;
		case NULL_DATAS:
			footer.view.setVisibility(View.VISIBLE);
			footer.txt.setText(R.string.null_datas);
			break;
		case ZERO_DATAS:
			footer.view.setVisibility(View.VISIBLE);
			footer.txt.setText(R.string.zero_datas);
			break;
		}
	}

	public void set() {
		setStatus(status);
	}

	public int getStatus() {
		return status;
	}

}