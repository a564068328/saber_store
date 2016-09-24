package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

import com.android.volley.VolleyError;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.VedioListAdapter;
import com.icloud.listenbook.ui.adapter.entity.ArticleItem;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

public class VedioListAct extends BaseListAct {
	VedioListAdapter vedioListAdapter;

	@Override
	public void init() {
		super.init();
		vedioListAdapter = new VedioListAdapter(this, datas);
		gridLayoutManager = new GridLayoutManager(this, 2);
		gridLayoutManager
				.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
					@Override
					public int getSpanSize(int pos) {
						return vedioListAdapter.getSpanSize(pos);

					}
				});

	}

	@Override
	public void setListeners() {
		super.setListeners();
		list.setAdapter(vedioListAdapter);
		list.setHasFixedSize(true);
		list.setLayoutManager(gridLayoutManager);
		list.addItemDecoration(vedioListAdapter.getDivider());
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE
						&& lastVisibleItem + 1 == vedioListAdapter
								.getItemCount()) {
					nextPage(page);
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = gridLayoutManager
						.findLastVisibleItemPosition();
			}
		});

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		LogUtil.e(Tag, "onResponse" + error.toString());
		vedioListAdapter.setStatus(FooterManage.NULL_DATAS);
		readArticleCache();
	}

	@Override
	public void onResponse(JSONObject json) {
		try {
			vedioListAdapter.setStatus(FooterManage.FREE);
			int res = json.optInt("result", -1);
			LogUtil.e(Tag, "onResponse" + json.toString());

			if (res == 0) {
				saveVerCode(cId, page, json.optString("verCode", "0"));
				ArrayList<Article> downArticle = new ArrayList<Article>();
				JSONArray jsonArray = json.optJSONArray("list");
				JSONObject item;
				ArticleItem articleItem;
				if (datas.size() == 0 && jsonArray.length() > 0) {
					articleItem = new ArticleItem();
					articleItem.setViewType(Type.TABLE_TITLE);
					articleItem.setAName("视频列表");
					datas.add(articleItem);
				}

				for (int i = 0; i < jsonArray.length(); i++) {
					articleItem = new ArticleItem();
					item = jsonArray.optJSONObject(i);
					JsonUtils.toArticle(articleItem, item,
							Configuration.TYPE_VEDIO);
					articleItem.setViewType(Type.TABLE_GRID);

					if (!datas.contains(articleItem)) {
						datas.add(articleItem);
						if (!downArticle.contains(articleItem))
							downArticle.add(articleItem);

					}

				}

				if (page == 0) {
					IoUtils.instance().removeArticle(cId);
				}
				if (jsonArray.length() > 0) {
					page += 1;
					vedioListAdapter.upDatas(datas);
					IoUtils.instance().saveArticle(downArticle);
					DataUpManage.save(this, "readArticle_" + cId);
				} else {
					vedioListAdapter.setStatus(FooterManage.NULL_DATAS);
				}
			} else {
				readArticleCache();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void nextPage(int page) {
		if (vedioListAdapter.getStatus() == FooterManage.FREE) {
			if (DataUpManage.isUp(this, "readArticle_" + cId) || page != 0) {
				vedioListAdapter.setStatus(FooterManage.LOADING);
				HttpUtils.readArticle(cId, page, this, this);
			} else {
				readArticleCache();
			}
		}
	}

	private void readArticleCache() {
		datas.clear();
		datas.addAll(IoUtils.instance().getVedioArticle(cId, "视频列表"));
		vedioListAdapter.upDatas(datas);
		if (datas.size() > 0) {
			page += 1;
		}
	}

}
