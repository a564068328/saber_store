package com.icloud.listenbook.ui.adapter.entity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.icloud.listenbook.R;

public class FooterHolder extends RecyclerView.ViewHolder {
	TextView txt;
	View view;

	public FooterHolder(View view) {
		super(view);
		this.view = view;
		txt = (TextView) view.findViewById(R.id.txt);
		view.setVisibility(View.GONE);
	}

}