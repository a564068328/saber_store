package com.icloud.listenbook.base;

public interface ICommAct {
	abstract void init();

	abstract int getLayout();

	abstract void findViews();

	abstract void setListeners();

	abstract void getDatas();

	abstract void setDatas();
}
