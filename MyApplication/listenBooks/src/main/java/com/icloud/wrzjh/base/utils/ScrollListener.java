package com.icloud.wrzjh.base.utils;

import android.widget.AbsListView;
import android.widget.ListView;

public class ScrollListener implements AbsListView.OnScrollListener {
	ListView listView;
	ListOnScroll listOnScroll;

	public ScrollListener(ListView listView, ListOnScroll listOnScroll) {
		this.listView = listView;
		this.listOnScroll = listOnScroll;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
			LogUtil.d("onScrollStateChanged", "SCROLL_STATE_FLING");
			break;
		case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
			LogUtil.d("onScrollStateChanged", "SCROLL_STATE_IDLE");
			break;
		case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			break;
		default:
			break;
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (listOnScroll != null) {
			listOnScroll.ListisOnScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}
	}

	public interface ListOnScroll {
		public void ListisOnScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount);
	};
}
