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
import com.icloud.listenbook.ui.adapter.BookListAdapter;
import com.icloud.listenbook.ui.adapter.entity.ArticleItem;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

public class BookListAct extends BaseListAct {

	protected BookListAdapter adapter;

	@Override
	public void init() {
		super.init();
		adapter = new BookListAdapter(this, datas);
		gridLayoutManager = new GridLayoutManager(this, 3);
		gridLayoutManager
				.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
					@Override
					public int getSpanSize(int pos) {
						return adapter.getSpanSize(pos);

					}
				});

	}

	@Override
	public void setListeners() {
		super.setListeners();
		list.setAdapter(adapter);
		list.setHasFixedSize(true);
		list.setLayoutManager(gridLayoutManager);

		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE
						&& lastVisibleItem + 1 == adapter.getItemCount()) {
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
		adapter.setStatus(FooterManage.NULL_DATAS);
		readArticleCache();
	}

	@Override
	public void onResponse(JSONObject json) {
		try {
			adapter.setStatus(FooterManage.FREE);
			int res = json.optInt("result", -1);
			LogUtil.e(Tag, "onResponse" + json.toString());
			if (res == 0) {
				saveVerCode(cId, page, json.optString("verCode", "0"));
				JSONArray jsonArray = json.optJSONArray("list");
				JSONObject item;
				ArticleItem articleItem;
				ArrayList<Article> down = new ArrayList<Article>();
				for (int i = 0; i < jsonArray.length(); i++) {
					articleItem = new ArticleItem();
					item = jsonArray.optJSONObject(i);
					JsonUtils.toArticle(articleItem, item,
							Configuration.TYPE_BOOK);
					if (datas.size() <= 6) {
						articleItem.setViewType(Type.TABLE_GRID);
					} else {
						articleItem.setViewType(Type.TABLE_ITEM);
					}
					if (!datas.contains(articleItem)) {
						datas.add(articleItem);
						if (!down.contains(articleItem))
							down.add(articleItem);
					}
					if (datas.size() == 6) {
						articleItem = new ArticleItem();
						articleItem.setViewType(Type.TABLE_TITLE);
						articleItem.setAName("文章列表");
						datas.add(articleItem);
					}
				}
				if (page == 0) {
					IoUtils.instance().removeArticle(cId);
				}
				if (jsonArray.length() > 0) {
					page += 1;
					adapter.upDatas(datas);
					IoUtils.instance().saveArticle(down);
					DataUpManage.save(this, "readArticle_" + cId);
				} else {
					adapter.setStatus(FooterManage.NULL_DATAS);
				}
			} else {
				readArticleCache();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void nextPage(int page) {
		if (adapter.getStatus() == FooterManage.FREE) {
			if (DataUpManage.isUp(this, "readArticle_" + cId) || page != 0) {
				adapter.setStatus(FooterManage.LOADING);
				HttpUtils.readArticle(cId, page, this, this);
			} else {
				readArticleCache();
			}
		}
	}

	private void readArticleCache() {
		datas.clear();
		datas.addAll(IoUtils.instance().getVoiceArticle(cId, "文章列表"));
		adapter.upDatas(datas);
		if (datas.size() > 0) {
			page += 1;
		}

	}

}
