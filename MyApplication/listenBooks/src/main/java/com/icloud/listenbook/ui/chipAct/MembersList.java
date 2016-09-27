package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.ui.adapter.PinterestLikeAdapter;
import com.icloud.listenbook.ui.adapter.entity.PinterestLikeItem;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class MembersList extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	View back;
	TextView title;
	ProgressBar progress;
	RecyclerView list;
	StaggeredGridLayoutManager staggeredGridLM;
	PinterestLikeAdapter adapter;
	ArrayList<PinterestLikeItem> data;
	TextView tip;

	@Override
	public void init() {
		staggeredGridLM = new StaggeredGridLayoutManager(2,
				StaggeredGridLayoutManager.VERTICAL);
		data = new ArrayList<PinterestLikeItem>();
		adapter = new PinterestLikeAdapter(this, data);

	}

	@Override
	public int getLayout() {
		return R.layout.act_members_list;
	}

	@Override
	public void getDatas() {
		super.getDatas();
		tip.setVisibility(View.GONE);
		HttpUtils.getMembersList(this, this);
	}

	@Override
	public void setDatas() {
		super.setDatas();
		title.setText(R.string.MembersList);
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		progress = (ProgressBar) findViewById(R.id.progress);
		list = (RecyclerView) findViewById(R.id.list);
		tip = (TextView) findViewById(R.id.tip);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.setHasFixedSize(false);
		list.setLayoutManager(staggeredGridLM);
		list.setAdapter(adapter);
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
		progress.setVisibility(View.GONE);
		if (data.size() == 0)
			tip.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResponse(JSONObject response) {
		progress.setVisibility(View.GONE);

		try {
			int res = response.optInt("result", -1);
			if (res == 0) {
				JSONArray list = response.optJSONArray("list");
				data.clear();
				JSONObject jsonObj;
				PinterestLikeItem item;
				for (int i = 0; i < list.length(); i++) {
					jsonObj = list.optJSONObject(i);
					item = JsonUtils.toPinterestLikeItem(jsonObj);
					data.add(item);
				}
				adapter.upData(data);
				if (data.size() == 0)
					tip.setVisibility(View.VISIBLE);
			}

		} catch (Exception e) {
		}
	}
}
