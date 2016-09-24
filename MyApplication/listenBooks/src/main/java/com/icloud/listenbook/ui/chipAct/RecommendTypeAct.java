package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.RecommendTypeAdapter;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Recommend;

public class RecommendTypeAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {

	RecommendTypeAdapter adapter;
	ArrayList<Recommend> items;
	RecyclerView list;
	LinearLayoutManager lineLM;
	TextView base_txt;
	View back;
	long media;
	protected int page = 1;
	protected int lastVisibleItem;

	@Override
	public void init() {
		media = this.getIntent().getLongExtra("media", 1);
		items = new ArrayList<Recommend>();
		adapter = new RecommendTypeAdapter(this, items);
		lineLM = new LinearLayoutManager(this);
		lineLM.setOrientation(LinearLayoutManager.VERTICAL);
	}

	@Override
	public int getLayout() {
		return R.layout.act_recommen_type;
	}

	@Override
	public void findViews() {
		base_txt = (TextView) findViewById(R.id.title);
		list = (RecyclerView) findViewById(R.id.list);
		back = findViewById(R.id.back);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE
						&& lastVisibleItem + 1 == adapter.getItemCount()) {
					getRecommend();
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = lineLM.findLastVisibleItemPosition();
			}
		});

	}

	@Override
	public void setDatas() {
		super.setDatas();
		list.setAdapter(adapter);
		list.setLayoutManager(lineLM);
		if (media == Configuration.TYPE_VEDIO) {
			base_txt.setText("视频推荐");
		} else if (media == Configuration.TYPE_VOICE) {
			base_txt.setText("听书推荐");
		} else if (media == Configuration.TYPE_BOOK) {
			base_txt.setText("文章推荐");
		}
	}

	@Override
	public void getDatas() {
		getRecommend();
	}

	private void getRecommendCache() {
		items.clear();
		items.addAll(IoUtils.instance().getRecommend(media));
		adapter.upDatas(items);
		if (items.size() > 0) {
			page += 1;
		}

	}

	public void getRecommend() {
		if (adapter.getStatus() == FooterManage.FREE) {
			/* 1表示视频，2音频 3文字 默认为 0 表示 推荐首页* */
			if (DataUpManage.isUp(this, "getRecommend" + media) || page != 0) {
				HttpUtils.getRecommend(media, page, this, this);
				adapter.setStatus(FooterManage.LOADING);
			} else {
				getRecommendCache();
			}
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
		adapter.setStatus(FooterManage.FREE);
		if (items.size() == 0) {
			items.addAll(IoUtils.instance().getRecommend(media));
			adapter.upDatas(items);
		}
	}

	protected void saveVerCode(int more, String verCode) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("media", media);
			jb.put("more", more);
			DataUpManage.saveVerCode("getRecommend" + jb.toString(), verCode);
		} catch (Exception e) {

		}
	}

	@Override
	public void onResponse(JSONObject response) {
		try {
			int res = response.optInt("result", -1);
			adapter.setStatus(FooterManage.FREE);
			LogUtil.e(Tag, response.toString());
			if (res == 0) {
				page = response.optInt("more", 1);
				saveVerCode(page, response.optString("verCode", "0"));
				JSONObject json = response.optJSONObject("list");
				JSONArray jsonArray = json.optJSONArray(String.valueOf(media));
				Recommend item;
				// 保存使用临时变量
				ArrayList<Recommend> saveItem = new ArrayList<Recommend>();
				if (jsonArray != null && jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						item = new Recommend();
						json = jsonArray.optJSONObject(i);
						JsonUtils.toRecommend(item, json, media);
						if (!items.contains(item)) {
							items.add(item);
							if (!saveItem.contains(saveItem)) {
								saveItem.add(item);
							}
						}
					}
				}
				/** 只有在下标为1 删除缓存 **/
				if (page == 1) {
					IoUtils.instance().clearRecommend(media);
				}
				if (jsonArray.length() > 0) {
					page += 1;
					adapter.upDatas(items);
					IoUtils.instance().saveRecommend(saveItem);
					DataUpManage.save(this, "getRecommend" + media);
				} else {
					adapter.setStatus(FooterManage.NULL_DATAS);
				}

			} else {
				/** 只有下标为1的时候才加载缓存 */
				if (page == 1) {
					getRecommendCache();
				} else {
					page += 1;
				}
			}
		} catch (Exception e) {
			adapter.setStatus(FooterManage.NULL_DATAS);
		}

	}
}
