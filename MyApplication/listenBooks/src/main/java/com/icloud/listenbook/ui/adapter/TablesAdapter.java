package com.icloud.listenbook.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.icloud.listenbook.ui.chipFrage.BoutiqueFrage;
import com.icloud.listenbook.ui.chipFrage.DownloadFrage;
import com.icloud.listenbook.ui.chipFrage.UserInfoFrage;
import com.icloud.listenbook.ui.fragment.MainFragment;

/*
 * 设置主pagerview的适配器
 */
public class TablesAdapter extends FragmentPagerAdapter {
	Fragment[] items = new Fragment[4];

	public TablesAdapter(FragmentManager fm) {
		super(fm);
		items[0] = new MainFragment();
		items[1] = new BoutiqueFrage();
		items[2] = new DownloadFrage();
		items[3] = new UserInfoFrage();
	}

	@Override
	public Fragment getItem(int position) {
		return items[position];
	}

	@Override
	public int getCount() {
		return items.length;
	}

}
