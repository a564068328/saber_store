package com.icloud.listenbook.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

import com.icloud.listenbook.base.view.HeaderViewListAdapter;
import com.icloud.listenbook.ui.adapter.entity.Type;

public class HomeSampleDivider extends RecyclerView.ItemDecoration {
	RecyclerView.Adapter adapter;

	// 构造函数初始化
	public HomeSampleDivider(com.icloud.listenbook.base.view.RecyclerView list) {
		if (list != null)
			adapter = list.getAdapter();
	}

	@Override
	public void getItemOffsets(Rect outRect, int itemPosition,
			RecyclerView parent) {
		if (adapter == null)
			return;
		int type = adapter.getItemViewType(itemPosition);
		if (itemPosition == adapter.getItemCount() - 1) {
			outRect.set(0, 0, 0, 40);
		} else if (type != HeaderViewListAdapter.ITEM_VIEW_TYPE_FOOTER
				&& type != HeaderViewListAdapter.ITEM_VIEW_TYPE_HEADER) {
			if (type == Type.TABLE_GRID) {
				switch (itemPosition % 10) {
				case 2:
				case 6:
					outRect.set(10, 0, 0, 0);
					break;
				case 5:
				case 9:
					outRect.set(0, 0, 10, 0);
					break;
				}

			} else
				outRect.set(10, 0, 10, 0);
		} else {
			outRect.set(0, 0, 0, 0);
		}
	}
}
