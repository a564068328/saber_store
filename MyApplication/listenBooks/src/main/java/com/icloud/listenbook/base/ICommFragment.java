package com.icloud.listenbook.base;

import android.view.View;

public interface ICommFragment {

	abstract int getLayout();

	abstract void init();

	abstract void findViews();

	abstract View findViewById(int id);

	abstract void setListeners();

	abstract void setData();
}
