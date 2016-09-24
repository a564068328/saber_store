package com.icloud.listenbook.ui.chipAct;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.view.viewpagerindicator.TabPageIndicator;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.ui.adapter.GreatMasterDetailPager;
import com.icloud.listenbook.ui.adapter.GreatMasterTabAdapter;
import com.icloud.listenbook.ui.adapter.PinterestLikeAdapter;
import com.icloud.listenbook.ui.adapter.entity.PinterestLikeItem;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class GreatMaster extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener, OnPageChangeListener {
	View back;
	TextView title;
	ProgressBar progress;
	ArrayList<String> tabs;
	ArrayList<GreatMasterDetailPager> pagerlist;
	TextView tip;
	String name;
	long type;
	long id;
	View go_right;
	int tabposition;
	TabPageIndicator tabList;
	private ViewPager viewPager;
	ArrayList<ArrayList<PinterestLikeItem>> data;
	private int position = 0;

	@Override
	public void init() {
		id = getIntent().getLongExtra("cId", 0);
		name = getIntent().getStringExtra("name");
		if (name != null)
			name = name.trim();
		type = getIntent().getLongExtra("type", 0);
		tabs = new ArrayList<String>();
		data = new ArrayList<ArrayList<PinterestLikeItem>>();
		pagerlist = new ArrayList<GreatMasterDetailPager>();
	}

	@Override
	public int getLayout() {
		return R.layout.act_greatmaster_list;
	}

	@Override
	public void getDatas() {
		super.getDatas();
		tip.setVisibility(View.GONE);
		HttpUtils.getGreatMaster(this, this);
	}

	@Override
	public void setDatas() {
		super.setDatas();
		title.setText(R.string.GreatMaster);
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		progress = (ProgressBar) findViewById(R.id.progress);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		tabList = (TabPageIndicator) findViewById(R.id.tabList);
		tip = (TextView) findViewById(R.id.tip);
		go_right = findViewById(R.id.go_right);

		// 滑动监听需要设置给Indicator
		tabList.setOnPageChangeListener(this);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		go_right.setOnClickListener(this);
		go_right.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.go_right:
			int currentItem = viewPager.getCurrentItem();
			if (position > currentItem) {
				position = 0;
				currentItem = 0;
				viewPager.setCurrentItem(currentItem);
			} else {
				viewPager.setCurrentItem(++currentItem);
				position = currentItem;
			}
			break;
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		progress.setVisibility(View.GONE);
		if (tabs.size() == 0)
			tip.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResponse(JSONObject response) {
		progress.setVisibility(View.GONE);

		try {
			int res = response.optInt("result", -1);
			LogUtil.e(getClass().getSimpleName(), response.toString());
			JSONObject jsonObj;
			JSONObject chipJsonObj;
			PinterestLikeItem item;
			String nameTab;
			if (res == 0) {
				JSONArray dataJson = response.optJSONArray("data");
				// LogUtil.e(getClass().getName(), "type"+type);
				if (id == 0) {
					tabs.clear();
					for (int i = 0; i < dataJson.length(); i++) {
						ArrayList<PinterestLikeItem> itemlist = new ArrayList<PinterestLikeItem>();
						jsonObj = dataJson.optJSONObject(i);
						nameTab = jsonObj.optString("name");
						tabs.add(nameTab);
						JSONArray list = jsonObj.optJSONArray("list");
						for (int j = 0; j < list.length(); j++) {
							chipJsonObj = list.optJSONObject(j);
							item = JsonUtils.toPinterestLikeItem(chipJsonObj);
							itemlist.add(item);
						}
						data.add(itemlist);
					}
					refreshPage();
				} else {
					tabs.clear();
					ArrayList<PinterestLikeItem> itemlist = new ArrayList<PinterestLikeItem>();
					for (int i = 0; i < dataJson.length(); i++) {
						jsonObj = dataJson.optJSONObject(i);
						nameTab = jsonObj.optString("name").trim();
						if (!nameTab.equals(name))
							continue;
						// LogUtil.e(Tag, "nameTab"+nameTab);
						tabs.add(nameTab);
						JSONArray list = jsonObj.optJSONArray("list");
						for (int j = 0; j < list.length(); j++) {
							chipJsonObj = list.optJSONObject(j);
							item = JsonUtils.toPinterestLikeItem(chipJsonObj);
							itemlist.add(item);
						}
						data.add(itemlist);
					}
					go_right.setVisibility(View.GONE);
					refreshPage();
				}
			}
		} catch (Exception e) {
		}
	}

	private void refreshPage() {
		if (tabs.size() == 0) {
			tip.setVisibility(View.VISIBLE);
			return;
		}
		for (ArrayList<PinterestLikeItem> item : data) {
			GreatMasterDetailPager pager = new GreatMasterDetailPager(this,
					item);
			pagerlist.add(pager);
		}
		tabList.setVisibility(View.VISIBLE);
		viewPager.setAdapter(new GreatMasterDetailAdapter());
		tabList.setViewPager(viewPager);
	}

	class GreatMasterDetailAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pagerlist.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabs.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			GreatMasterDetailPager pager = pagerlist.get(position);
			container.addView(pager.mRootView);
			pager.upData(data.get(position));
			return pager.mRootView;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}
}
