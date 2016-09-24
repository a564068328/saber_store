package com.icloud.listenbook.ui.chipFrage;

import java.util.ArrayList;

import org.json.JSONObject;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseFragement;
import com.icloud.listenbook.entity.ReadingTrackItem;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.datas.Login;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.BoutiqueCollectAdapter;
import com.icloud.listenbook.ui.adapter.BoutiqueTrackAdapter;
import com.icloud.listenbook.ui.popup.QueryPop;
import com.icloud.listenbook.ui.popup.QueryPop.QueryListener;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

public class BoutiqueFrage extends BaseFragement implements
		RadioGroup.OnCheckedChangeListener, OnClickListener, QueryListener {
	public static final int COLLECT = 1;
	public static final int HISTORY = COLLECT + 1;
	RadioGroup tabs;
	ImageView advertising;
	View delAll;
	ListView list;
	BoutiqueCollectAdapter collectAdapter;
	BoutiqueTrackAdapter trackAdapter;
	ArrayList<Article> collectArticle;
	ArrayList<ReadingTrackItem> readingTrack;
	View search;
	QueryPop queryPop;
	int page;
	View progress, progressTip;

	@Override
	public int getLayout() {
		return R.layout.frage_boutique_page;
	}

	@Override
	public void init() {
		super.init();
		collectArticle = new ArrayList<Article>();
		readingTrack = new ArrayList<ReadingTrackItem>();
		collectAdapter = new BoutiqueCollectAdapter(act);
		trackAdapter = new BoutiqueTrackAdapter(act);
		trackAdapter.addDelListener(this);
		queryPop = new QueryPop(findViewById(R.id.query_l));
	}

	@Override
	public void onResume() {
		super.onResume();
		getCollectArticle();
		readingTrack.clear();
		readingTrack.addAll(IoUtils.instance().getReadingTrack());
		upPage();
	}

	/** 获取收藏列表 */
	protected void getCollectArticle() {
		collectArticle.clear();
		/** 只有登录的用户才有收藏 */
		if (UserInfo.instance().isLogin()) {
			collectArticle.addAll(IoUtils.instance().getCollects(
					UserInfo.instance().getUid()));
			showProgress();
		}

	}

	@Override
	public void setListeners() {
		list.setAdapter(collectAdapter);
		delAll.setOnClickListener(this);
		delAll.setOnTouchListener(ViewUtils.instance().onTouchListener);
		advertising.setOnClickListener(this);
		tabs.setOnCheckedChangeListener(this);
		search.setOnClickListener(this);
		search.setOnTouchListener(ViewUtils.instance().onTouchListener);
		queryPop.addQueryListener(this);

	}

	@Override
	public void findViews() {
		tabs = (RadioGroup) findViewById(R.id.tabs);
		list = (ListView) findViewById(R.id.list);
		advertising = (ImageView) findViewById(R.id.advertising);
		delAll = findViewById(R.id.del_all);
		search = findViewById(R.id.search);
		progress = findViewById(R.id.progress);
		progressTip = findViewById(R.id.progressTip);

	}

	/** 判断是否显示加载 */
	protected void showProgress() {
		/** 收藏页 用户登陆 且 收藏有数据 不是属于收藏加载状态 */
		if (page == R.id.enshrine && UserInfo.instance().isLogin()
				&& collectArticle.size() == 0 && !Login.isgetCollectList) {
			progress.setVisibility(View.VISIBLE);
			progressTip.setVisibility(View.VISIBLE);
		} else {
			progress.setVisibility(View.INVISIBLE);
			progressTip.setVisibility(View.INVISIBLE);
		}
	}

	protected void upPage() {
		advertising.setVisibility(View.GONE);
		delAll.setVisibility(View.GONE);
		switch (page) {
		case R.id.enshrine:
			collectAdapter.upData(collectArticle);
			list.setAdapter(collectAdapter);
			showProgress();
			break;
		case R.id.history:
			trackAdapter.upData(readingTrack);
			list.setAdapter(trackAdapter);
			if (readingTrack.size() > 0) {
				delAll.setVisibility(View.VISIBLE);
			} else {
				delAll.setVisibility(View.GONE);
			}
			break;

		}
	}

	@Override
	public void setData() {
		super.setData();

		readingTrack.addAll(IoUtils.instance().getReadingTrack());
		trackAdapter.upData(readingTrack);

		page = R.id.enshrine;
		tabs.check(page);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		page = checkedId;
		upPage();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.del: {
			int position = (Integer) v.getTag();
			if (position < 0 || position > readingTrack.size())
				return;
			ReadingTrackItem item = readingTrack.get(position);
			readingTrack.remove(position);
			trackAdapter.upData(readingTrack);
			IoUtils.instance().delReadingTrack(item.getAId());
			if (readingTrack.size() > 0) {
				delAll.setVisibility(View.VISIBLE);
			} else {
				delAll.setVisibility(View.GONE);
			}
		}
			break;
		case R.id.del_all:
			delAll.setVisibility(View.GONE);
			readingTrack.clear();
			trackAdapter.upData(readingTrack);
			IoUtils.instance().delReadingTrack();
			break;
		case R.id.search:
			queryPop.show();
			break;
		}
	}

	/** 获取收藏列表 **/
	public ArrayList<Article> getCollects(String like) {
		ArrayList<Article> datas = new ArrayList<Article>();
		Article article;
		for (int i = 0; i < collectArticle.size(); i++) {
			article = collectArticle.get(i);
			if (article != null && !datas.contains(article))
				if (article.getAName().indexOf(like) != -1
						|| article.getAAuthor().indexOf(like) != -1)
					datas.add(article);
		}
		return datas;
	}

	public ArrayList<ReadingTrackItem> getReadingTrack(String like) {
		ArrayList<ReadingTrackItem> datas = new ArrayList<ReadingTrackItem>();
		ReadingTrackItem article;
		for (int i = 0; i < readingTrack.size(); i++) {
			article = readingTrack.get(i);
			if (article != null && !datas.contains(article))
				if (article.getAName().indexOf(like) != -1
						|| article.getAAuthor().indexOf(like) != -1)
					datas.add(article);
		}
		return datas;
	}

	@Override
	public void Query(String query) {
		if (TextUtils.isEmpty(query))
			return;
		switch (page) {
		case R.id.enshrine:
			collectAdapter.upData(getCollects(query));
			list.setAdapter(collectAdapter);
			// advertising.setVisibility(View.VISIBLE);
			break;
		case R.id.history:
			trackAdapter.upData(getReadingTrack(query));
			list.setAdapter(trackAdapter);
			if (readingTrack.size() > 0) {
				delAll.setVisibility(View.VISIBLE);
			} else {
				delAll.setVisibility(View.GONE);
			}
			break;

		}
	}

	@Override
	public void End() {
		upPage();
	}

}
