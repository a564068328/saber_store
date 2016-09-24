package com.icloud.listenbook.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragement extends Fragment implements ICommFragment {
	protected boolean isVisiableToUser = false;
	protected Activity act;
	protected View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//view = inflater.inflate(getLayout(), null);
		view = inflater.inflate(getLayout(), container, false);
		init();
		findViews();
		setListeners();
		setData();
		return view;
	}

	public void setData() {

	}

	@Override
	public View findViewById(int id) {
		if (view != null)
			return view.findViewById(id);
		return null;
	}

	@Override
	public void init() {
		act = this.getActivity();
	}

}
