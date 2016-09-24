package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.listenbook.entity.TabItemInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.recyclerplus.WrapRecyclerView;
import com.icloud.listenbook.ui.adapter.MediumAdapter;
import com.icloud.listenbook.ui.adapter.entity.RecommendItem;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Ads;
import com.listenBook.greendao.Media;
import com.listenBook.greendao.Recommend;

public class MediumAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	MediumAdapter adapter;
	ArrayList<TabItemInfo> homeTypes;
	View advertising;
	RecyclerView list;
	protected GridLayoutManager gridLM;
	View back;
	ArrayList<Ads> asdinfo;

	@Override
	public void init() {
		
		homeTypes = IoUtils.instance().readHomeDatas();
		asdinfo = new ArrayList<Ads>(IoUtils.instance().getAds(0));
		adapter = new MediumAdapter(this, homeTypes, asdinfo);
		gridLM = new GridLayoutManager(this, 6);
		gridLM.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int pos) {
				return adapter.getSpanSize(pos);

			}
		});
	}

	@Override
	public int getLayout() {
		return R.layout.act_recommen;
	}

	@Override
	public void onResume() {
		super.onResume();
		upDada();
		Runnable LedRun = adapter.getLedRun();
		if (LedRun != null) {
			HandlerUtils.removeCallbacks(LedRun);
			HandlerUtils.postDelayed(LedRun, 5000L);
		}
	}

	public void upDada() {
		homeTypes = IoUtils.instance().readHomeDatas();
		asdinfo = new ArrayList<Ads>(IoUtils.instance().getAds(0));
		if (adapter != null)
			adapter.upData(homeTypes, asdinfo);
	}

	@Override
	public void findViews() {
		advertising = findViewById(R.id.advertising);
		list = (WrapRecyclerView) findViewById(R.id.list);
		back = findViewById(R.id.back);
	}

	@Override
	public void setListeners() {
		advertising.setOnClickListener(this);
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	@Override
	public void setDatas() {
		super.setDatas();
		
		list.setLayoutManager(gridLM);
		       
        list.setAdapter(adapter);
	}

	@Override
	public void getDatas() {
		/* 1表示视频，2音频 3文字 默认为 0 表示 推荐首页* */
		if (DataUpManage.isUp(this, "getMediumCache")) {
			HttpUtils.getHome(this, this);
		} else {
			getMediumCache();
		}
	}

	private void getMediumCache() {
		homeTypes = IoUtils.instance().readHomeDatas();
		adapter.upData(homeTypes, asdinfo);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (homeTypes.size() == 0) {
			homeTypes = IoUtils.instance().readHomeDatas();
			adapter.upData(homeTypes, asdinfo);
		}
	}

	@Override
	public void onResponse(JSONObject json) {
		try {
			int res = json.optInt("result", -1);
			LogUtil.e(Tag, json.toString());

			if (res == 0) {
				IoUtils.instance().saveMedia(json);
				long verCode = json.optLong("verCode", 0);
				SharedPreferenceUtil.saveHomeverCode(verCode);
			}
			getMediumCache();
		} catch (Exception e) {
		}

	}

}