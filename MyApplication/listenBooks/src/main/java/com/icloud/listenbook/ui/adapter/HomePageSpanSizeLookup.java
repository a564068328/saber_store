package com.icloud.listenbook.ui.adapter;

import android.support.v7.widget.GridLayoutManager;

import com.icloud.listenbook.base.view.HeaderViewListAdapter;
import com.icloud.listenbook.base.view.RecyclerView;
import com.icloud.listenbook.ui.adapter.entity.Type;

public class HomePageSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
	RecyclerView.Adapter mAdapter;

	public HomePageSpanSizeLookup(RecyclerView list) {
		if (list != null)
			mAdapter = list.getAdapter();
	}
	//setSpanSizeLookup为设置RecyclerView的列数，通过自动调用getSpanSize(int postion)方法
	@Override
	public int getSpanSize(int postion) {
		if (mAdapter != null) {
			return getSpanSize(mAdapter, postion);
		}
		return 0;

	}
	
	private int getSpanSize(RecyclerView.Adapter mAdapter, int postion) {
		switch (mAdapter.getItemViewType(postion)) {
		case HeaderViewListAdapter.ITEM_VIEW_TYPE_HEADER:
		case HeaderViewListAdapter.ITEM_VIEW_TYPE_FOOTER:
		case Type.TABLE_HEADER:
		case Type.TABLE_TITLE:
		case Type.TABLE_FOOTER:
		case Type.TABLE_END:
			return 60;
		case Type.TABLE_GRID:
		case Type.TABLE_WORK:
		case Type.TABLE_GREAT:
		case Type.TABLE_TITLE_WORK:
			return 15;	
		case Type.TABLE_TIME:			
		case Type.TABLE_TITLE_TIME:
		case Type.TABLE_TITLE_CORE:
			return 12;
		case Type.TABLE_WORD:
		case Type.TABLE_TITLE_WORD:	
			return 10;
		default:
			return 10;
		}
	}
}
