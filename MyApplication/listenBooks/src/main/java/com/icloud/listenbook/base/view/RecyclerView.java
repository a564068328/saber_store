package com.icloud.listenbook.base.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class RecyclerView extends android.support.v7.widget.RecyclerView {
	public class FixedViewInfo extends RecyclerView.ViewHolder {
		private final View view;

		public FixedViewInfo(View view) {
			super(view);
			this.view = view;
		}

	}

	private RecyclerView.Adapter mAdapter;
	RecyclerView.FixedViewInfo mHeaderViewInfo;
	RecyclerView.FixedViewInfo mFooterViewInfo;

	public RecyclerView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);

	}

	public RecyclerView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}

	public RecyclerView(Context arg0) {
		super(arg0);
	}

	public void addHeaderView(View v) {
		final FixedViewInfo info = new FixedViewInfo(v);
		mHeaderViewInfo = (info);
	}

	public void addFootView(View v) {
		final FixedViewInfo info = new FixedViewInfo(v);
		mFooterViewInfo = (info);
	}

	@Override
	public void setAdapter(Adapter adapter) {
		if (!(adapter instanceof HeaderViewListAdapter)) {
			adapter = new HeaderViewListAdapter(mHeaderViewInfo,
					mFooterViewInfo, adapter);
		}
		this.mAdapter = adapter;
		super.setAdapter(mAdapter);
	}
	
	public void setFootAdapter(Adapter adapter) {
		if (!(adapter instanceof HeaderViewListAdapter)) {
			adapter = new HeaderViewListAdapter(mHeaderViewInfo,
					mFooterViewInfo, adapter);
		}
		this.mAdapter = adapter;
		super.setAdapter(mAdapter);
	}
}
