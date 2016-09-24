/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icloud.listenbook.base.view;

import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filterable;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;

/**
 * ListAdapter used when a ListView has header views. This ListAdapter wraps
 * another one and also keeps track of the header views and their associated
 * data objects.
 * <p>
 * This is intended as a base class; you will probably not need to use this
 * class directly in your own code.
 */
public class HeaderViewListAdapter extends RecyclerView.Adapter {
	public static final int ITEM_VIEW_TYPE_HEADER = -1;
	public static final int ITEM_VIEW_TYPE_FOOTER = ITEM_VIEW_TYPE_HEADER - 1;
	private final RecyclerView.Adapter mAdapter;
	RecyclerView.FixedViewInfo mHeaderViewInfo;
	RecyclerView.FixedViewInfo mFooterViewInfo;

	private final boolean mIsFilterable;

	public HeaderViewListAdapter(RecyclerView.FixedViewInfo headerViewInfos,
			RecyclerView.FixedViewInfo footerViewInfos,
			RecyclerView.Adapter adapter) {
		mAdapter = adapter;
		mIsFilterable = adapter instanceof Filterable;

		if (headerViewInfos != null) {
			mHeaderViewInfo = headerViewInfos;
		}

		if (footerViewInfos != null) {
			mFooterViewInfo = footerViewInfos;
		}

	}

	public int getHeadersCount() {
		return mHeaderViewInfo != null ? 1 : 0;
	}

	public int getFootersCount() {
		return mFooterViewInfo != null ? 1 : 0;
	}

	public boolean removeHeader() {
		mHeaderViewInfo = null;

		return true;
	}

	public boolean removeFooter() {
		mFooterViewInfo = null;
		return true;
	}

	@Override
	public int getItemCount() {
		if (mAdapter != null) {
			return getFootersCount() + getHeadersCount()
					+ mAdapter.getItemCount();
		} else {
			return getFootersCount() + getHeadersCount();
		}
	}

	@Override
	public long getItemId(int position) {
		int numHeaders = getHeadersCount();
		if (mAdapter != null && position >= numHeaders) {
			int adjPosition = position - numHeaders;
			int adapterCount = mAdapter.getItemCount();
			if (adjPosition < adapterCount) {
				return mAdapter.getItemId(adjPosition);
			}
		}
		return -1;
	}

	@Override
	public int getItemViewType(int position) {
		int numHeaders = getHeadersCount();
		if (mAdapter != null && position >= numHeaders) {
			int adjPosition = position - numHeaders;
			int adapterCount = mAdapter.getItemCount();
			if (adjPosition < adapterCount) {
				return mAdapter.getItemViewType(adjPosition);
			} else
				return ITEM_VIEW_TYPE_FOOTER;
		}

		return ITEM_VIEW_TYPE_HEADER;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		int numHeaders = getHeadersCount();
		if (position < numHeaders) {
			return;
		}
		int adjPosition = position - numHeaders;
		int adapterCount = 0;
		if (mAdapter != null) {
			adapterCount = mAdapter.getItemCount();
			if (adjPosition < adapterCount) {
				mAdapter.onBindViewHolder(holder, adjPosition);
				return;
			} else {
			}

		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		if (viewType == ITEM_VIEW_TYPE_HEADER) {
			return mHeaderViewInfo;
		} else if (viewType == ITEM_VIEW_TYPE_FOOTER) {
			return mHeaderViewInfo;
		}
		return mAdapter.onCreateViewHolder(parent, viewType);
	}

}
