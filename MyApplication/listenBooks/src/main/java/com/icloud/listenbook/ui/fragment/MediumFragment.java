package com.icloud.listenbook.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseFragement;
import com.icloud.listenbook.entity.TabItemInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.recyclerplus.WrapRecyclerView;
import com.icloud.listenbook.ui.adapter.MediumAdapter;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;

import org.json.JSONObject;

import java.util.ArrayList;

public class MediumFragment extends BaseFragement implements Listener<JSONObject>, ErrorListener {
	MediumAdapter adapter;
	ArrayList<TabItemInfo> homeTypes;
	RecyclerView list;
	protected GridLayoutManager gridLM;

	@Override
	public void init() {
		
		homeTypes = IoUtils.instance().readHomeDatas();
//		asdinfo = new ArrayList<Ads>(IoUtils.instance().getAds(0));
		adapter = new MediumAdapter(getActivity(), homeTypes);
		gridLM = new GridLayoutManager(getActivity(), 6);
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

	}

	public void upDada() {
		homeTypes = IoUtils.instance().readHomeDatas();
		if (adapter != null)
			adapter.upData(homeTypes);
	}

	@Override
	public void findViews() {
		list = (WrapRecyclerView) view.findViewById(R.id.list);
	}

	@Override
	public void setListeners() {

	}

	@Override
	public void setData() {
		list.setLayoutManager(gridLM);
		list.setAdapter(adapter);
	}

	@Override
	public void getDatas() {
		/* 1表示视频，2音频 3文字 默认为 0 表示 推荐首页* */
		if (DataUpManage.isUp(getActivity(), "getMediumCache")) {
			HttpUtils.getHome(this, this);
		} else {
			getMediumCache();
		}
	}

	private void getMediumCache() {
		homeTypes = IoUtils.instance().readHomeDatas();
		adapter.upData(homeTypes);
	}


	@Override
	public void onErrorResponse(VolleyError error) {
		if (homeTypes.size() == 0) {
			homeTypes = IoUtils.instance().readHomeDatas();
			adapter.upData(homeTypes);
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