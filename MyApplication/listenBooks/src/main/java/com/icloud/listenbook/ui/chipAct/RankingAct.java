package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.RankAdapter;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Rank;

public class RankingAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener, RadioGroup.OnCheckedChangeListener {
	public static final int WEEKS = 1, MONTH = 2, TOTAL = 3;
	ArrayList<Rank> weeksArray;
	ArrayList<Rank> monthArray;
	ArrayList<Rank> totalArray;
	RankAdapter adapter;
	String Tag = this.getClass().getName();
	LinearLayoutManager lineLM;
	View back;
	RecyclerView list;
	RadioGroup tabs;
	int page;

	@Override
	public void init() {
		page = WEEKS;
		weeksArray = new ArrayList<Rank>();
		monthArray = new ArrayList<Rank>();
		totalArray = new ArrayList<Rank>();
		lineLM = new LinearLayoutManager(this);
		lineLM.setOrientation(LinearLayoutManager.VERTICAL);
		adapter = new RankAdapter(this);
	}

	@Override
	public int getLayout() {
		return R.layout.act_rank;
	}

	@Override
	public void findViews() {
		tabs = (RadioGroup) findViewById(R.id.tabs);
		list = (RecyclerView) findViewById(R.id.list);
		back = findViewById(R.id.back);

	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		tabs.setOnCheckedChangeListener(this);
	}

	@Override
	public void setDatas() {
		super.setDatas();
		list.setAdapter(adapter);
		list.setLayoutManager(lineLM);
	}

	@Override
	public void getDatas() {
		super.getDatas();
		getRankDatas();

	}

	private void getRankDatasCache() {
		weeksArray.clear();
		weeksArray = new ArrayList<Rank>(IoUtils.instance().getRanks(WEEKS));
		monthArray.clear();
		monthArray = new ArrayList<Rank>(IoUtils.instance().getRanks(MONTH));
		totalArray.clear();
		totalArray = new ArrayList<Rank>(IoUtils.instance().getRanks(TOTAL));
		ArrayList<Rank> item;
		switch (page) {
		default:
		case WEEKS: {
			item = (weeksArray);
			break;
		}
		case MONTH: {
			item = (monthArray);
			break;
		}
		case TOTAL: {
			item = (totalArray);
			break;
		}
		}
		adapter.upDatas(item);
	}

	public void getRankDatas() {
		ArrayList<Rank> item;
		switch (page) {
		default:
		case WEEKS: {
			item = (weeksArray);
			break;
		}
		case MONTH: {
			item = (monthArray);
			break;
		}
		case TOTAL: {
			item = (totalArray);
			break;
		}
		}
		adapter.upDatas(item);
		if (item.size() > 0)
			return;
		if (DataUpManage.isUp(this, "getRankDatas" + page)) {
			HttpUtils.getRanks(page, "praiseNum", this, this);
		} else {
			getRankDatasCache();
		}
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
		getRankDatasCache();
	}

	protected void saveVerCode(int rankid, String type, String verCode) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("rankid", rankid);
			jb.put("type", type);
			DataUpManage.saveVerCode("getRanks" + jb.toString(), verCode);
		} catch (Exception e) {

		}
	}

	@Override
	public void onResponse(JSONObject response) {
		try {
			int res = response.optInt("result", -1);
			LogUtil.e(Tag, response.toString());
			if (res == 0) {
				JSONArray list = response.optJSONArray("list");
				int rankid = response.optInt("rankid");
				String type = response.optString("type");
				saveVerCode(rankid, type, response.optString("verCode", "0"));
				ArrayList<Rank> ranks = new ArrayList<Rank>();
				Rank ranksItem;
				JSONObject json;
				for (int i = 0; i < list.length(); i++) {
					json = list.getJSONObject(i);
					ranksItem = JsonUtils.toRank(type, page, json);
					if (!ranks.contains(ranksItem)) {
						ranks.add(ranksItem);
					}
				}
				switch (rankid) {
				default:
				case WEEKS:
					weeksArray.clear();
					weeksArray.addAll(ranks);
					break;
				case MONTH:
					monthArray.clear();
					monthArray.addAll(ranks);
					break;
				case TOTAL:
					totalArray.clear();
					totalArray.addAll(ranks);
					break;
				}
				switch (page) {
				default:
				case WEEKS:
					adapter.upDatas(weeksArray);
					break;
				case MONTH:
					adapter.upDatas(monthArray);
					break;
				case TOTAL:
					adapter.upDatas(totalArray);
					break;
				}

				IoUtils.instance().clearRank(rankid);
				IoUtils.instance().saveRank(ranks);
				DataUpManage.save(RankingAct.this, "getRankDatas" + rankid);
			} else {
				getRankDatasCache();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int page;
		switch (checkedId) {
		default:
		case R.id.weeks:
			page = WEEKS;
			break;
		case R.id.month:
			page = MONTH;
			break;
		case R.id.total:
			page = TOTAL;
			break;

		}
		if (this.page != page) {
			this.page = page;
			getRankDatas();
		}
	}
}
