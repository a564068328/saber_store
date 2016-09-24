package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.GreatMasterTabAdapter;
import com.icloud.listenbook.ui.adapter.HomeMediaAdapter;
import com.icloud.listenbook.ui.adapter.HomeMediaTabAdapter;
import com.icloud.listenbook.ui.adapter.PinterestLikeAdapter;
import com.icloud.listenbook.ui.adapter.entity.ArticleItem;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.ui.adapter.entity.PinterestLikeItem;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.MethodUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ThreadPoolUtils;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

public class HomeMediaAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener, OnItemClickListener {
	public static final String[] limitString = { "小学", "初中", "高中" };
	final int dataCompelet = 0;
	String tag = getClass().getName();
	View back;
	TextView title;
	View progress;
	RecyclerView list;
	RecyclerView tabList;
	GridLayoutManager staggeredGridLM;
	LinearLayoutManager lineLM;
	HomeMediaAdapter adapter;
	TextView tip;
	int actType;
	HomeMediaTabAdapter tabAdapter;
	ArrayList<ArticleItem> data[];
	ArrayList<String> tabs;
	ArrayList<Long> Cid;
	long cId;
	int position;
	int tabposition;
	int page = 0;
	String name;
	boolean isTotal = true;
	View go_right;
	private int netpage;
	private int netposition;
	private int totalposition;
	private String verCode;
	@Override
	public void init() {
		actType = (int) getIntent().getLongExtra("type", 4);
		cId = getIntent().getLongExtra("cId", 0);
		name = getIntent().getStringExtra("name");
		tabs = new ArrayList<String>();
		Cid = new ArrayList<Long>();
		staggeredGridLM = new GridLayoutManager(this, 4);
		lineLM = new LinearLayoutManager(this);
		lineLM.setOrientation(LinearLayoutManager.HORIZONTAL);
		adapter = new HomeMediaAdapter(this);
		tabAdapter = new HomeMediaTabAdapter(this, tabs, 0);
		tabAdapter.setOnItemClickListener(this);
		staggeredGridLM
				.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
					@Override
					public int getSpanSize(int pos) {
						return adapter.getSpanSize(pos);

					}
				});
	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.act_home_media;
	}

	@Override
	public void findViews() {
		// TODO Auto-generated method stub
		back = findViewById(R.id.back);
		go_right = findViewById(R.id.go_right);
		// go_right.setVisibility(View.GONE);
		title = (TextView) findViewById(R.id.title);
		progress =  findViewById(R.id.progress);
		list = (RecyclerView) findViewById(R.id.list);
		tabList = (RecyclerView) findViewById(R.id.tabList);
		tip = (TextView) findViewById(R.id.tip);
		if (cId != 0) {
			isTotal = false;
			tabList.setVisibility(View.GONE);
		}
	}

	@Override
	public void setDatas() {
		// TODO Auto-generated method stub
		super.setDatas();
		switch (actType) {
		case 4:
			if (cId > 0) {
				data = new ArrayList[1];
				data[0] = new ArrayList<ArticleItem>();
			} else {
				tabs.add("根本性的字词知识融通");
				tabs.add("小学");
				tabs.add("初中");
				tabs.add("高中");
				tabAdapter.upData(tabs);
				data = new ArrayList[tabs.size()];
				for (int i = 0; i < tabs.size(); i++) {
					data[i] = new ArrayList<ArticleItem>();
				}
			}
			title.setText("根本性的字词知识融通");
			break;
		case 5:
			if (cId > 0) {
				data = new ArrayList[1];
				data[0] = new ArrayList<ArticleItem>();

			} else {
				tabs.add("应变时代的必悟融通概念");
				tabs.add("小学");
				tabs.add("初中");
				tabs.add("高中");
				tabAdapter.upData(tabs);
				data = new ArrayList[tabs.size()];
				for (int i = 0; i < tabs.size(); i++) {
					data[i] = new ArrayList<ArticleItem>();
				}
			}
			title.setText("应变时代的必悟融通概念");
			break;
		case 6:
			if (cId > 0) {
				data = new ArrayList[1];
				data[0] = new ArrayList<ArticleItem>();

			} else {
				tabs.add("中外名著与核心大道经典融通");
				tabs.add("小学");
				tabs.add("初中");
				tabs.add("高中");
				tabAdapter.upData(tabs);
				data = new ArrayList[tabs.size()];
				for (int i = 0; i < tabs.size(); i++) {
					data[i] = new ArrayList<ArticleItem>();
				}
			}
			title.setText("中外名著与核心大道经典融通");
			break;
		default:
			break;
		}
	}

	@Override
	public void getDatas() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<Long> allCid = new ArrayList<Long>();
				if (!isTotal) {
					// data[0].addAll((ArrayList<ArticleItem>)
					// IoUtils.instance()
					// .getVedioArticle(cId, name));
					Cid.add(cId);
					verCode = DataUpManage.getVerCode("readArticle"+cId);
				} else {
					data[0].clear();
					getCid();
					verCode = DataUpManage.getVerCode("readArticle"+actType);
					// for (int i = 0; i < Cid.size(); i++) {
					// cId = Cid.get(i);
					// name = IoUtils.instance().getCidName(cId);
					// data[0].addAll((ArrayList<ArticleItem>) IoUtils
					// .instance().getVedioArticle(cId, name));
					// }
				}
				getAllNewArticle();
				
			}

		}).start();

	}

	private void getAllNewArticle() {
		cId = Cid.get(0);
		netpage = 0;
		netposition = 0;
		totalposition = Cid.size();
		HttpUtils.readNewArticle(cId, page,verCode, this,this);
	}

	

	private void getCid() {
		Cid.clear();
		Cid.addAll((ArrayList<Long>) IoUtils.instance().getCid(actType));
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.setHasFixedSize(false);
		list.setLayoutManager(staggeredGridLM);
		list.setAdapter(adapter);
		go_right.setOnClickListener(this);
		go_right.setOnTouchListener(ViewUtils.instance().onTouchListener);
		tabList.setHasFixedSize(false);
		tabList.setLayoutManager(lineLM);
		tabList.setAdapter(tabAdapter);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		progress.setVisibility(View.GONE);
		getCacheData();
		if (tabs.size() == 0) {
			tip.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onResponse(final JSONObject response) {
		 {
			final int res = response.optInt("result", -1);
			// LogUtil.e("getAllNewArticleResponse", response.toString());
			ThreadPoolUtils.execute(new Runnable() {
				@Override
				public void run() {
					if (res == 0) {
						JSONArray jsonArray = response.optJSONArray("list");
						JSONObject item;
						ArticleItem articleItem;
						if (jsonArray.length() > 0) {
							for (int i = 0; i < jsonArray.length(); i++) {
								articleItem = new ArticleItem();
								item = jsonArray.optJSONObject(i);
								JsonUtils.toArticle(articleItem, item,
										Configuration.TYPE_WORD);
								// 更新文章
								IoUtils.instance().saveArticle(articleItem);
							}
							netpage++;
						} else {
							netpage = 0;
							netposition++;
							if (netposition >= totalposition) {
								getCache(response.optString("verCode", "0"));
								return;
							}
							cId = Cid.get(netposition);
						}
						if (position < totalposition) {
							HttpUtils.readNewArticle(cId, netpage,verCode,HomeMediaAct.this,HomeMediaAct.this);
						}
					}else{
						getCache();
					}
				}
			});

		 }
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.go_right:
			tabposition++;
			if ((tabposition) < data.length) {
				tabList.scrollToPosition(tabposition);
				tabAdapter.setindex(tabposition);
				tabAdapter.notifyItemChanged(tabposition - 1);
				tabAdapter.notifyItemChanged(tabposition);
				adapter.upData(data[tabposition]);
			} else {
				tabposition = 0;
				tabList.scrollToPosition(tabposition);
				tabAdapter.setindex(tabposition);
				tabAdapter.notifyDataSetChanged();
				adapter.upData(data[tabposition]);
			}
			break;
		}
	}

	Handler UIhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progress.setVisibility(View.GONE);
			switch (msg.what) {
			case dataCompelet:
				if (!isTotal) {
//					data[0] = (ArrayList<ArticleItem>) IoUtils.instance()
//							.getVedioArticle(cId, name);
					adapter.upData(data[0]);
				} else {
					getCacheData();
				}
				tip.setVisibility(View.GONE);
				break;
			}
		}
	};
    /*  将文章分为小学等部分*/
	private void getCacheData() {

		for (int i = 0; i < data[0].size(); i++) {
			String Abstract = data[0].get(i).getAAbstract();
			if (!TextUtils.isEmpty(Abstract)) {
				if (Abstract.length() >= 4) {
					Abstract = Abstract.substring(0, 4);
					if (Abstract.contains(limitString[0])) {
						data[1].add(data[0].get(i));
					} else if (Abstract.contains(limitString[1])) {
						data[2].add(data[0].get(i));
					} else if (Abstract.contains(limitString[2])) {
						data[3].add(data[0].get(i));
					}
				}
			}
		}

		if (tabposition == 0) {
			adapter.upData(data[0]);
		} else if ((tabposition) < data.length) {
			tabList.scrollToPosition(tabposition);
			tabAdapter.setindex(tabposition);
			tabAdapter.notifyDataSetChanged();
			adapter.upData(data[tabposition]);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
			long arg3) {

		if ((postion + 1) < data.length)
			tabList.scrollToPosition(postion + 1);
		else
			tabList.scrollToPosition(postion);
		if (postion == 0) {
			tabList.scrollToPosition(postion);
		}
		if (postion >= 0 && postion < data.length) {
			tabposition = postion;
			adapter.upData(data[postion]);
		}
	}

	private void getCache(String verCode) {
		if (!isTotal) {
			 data[0].addAll((ArrayList<ArticleItem>)
			 IoUtils.instance()
			 .getVedioArticle(cId, name));
			 DataUpManage.saveVerCode("readArticle"+cId,verCode);
		} else {
			 for (int i = 0; i < Cid.size(); i++) {
			 cId = Cid.get(i);
			 name = IoUtils.instance().getCidName(cId);
			 data[0].addAll((ArrayList<ArticleItem>) IoUtils
			 .instance().getVedioArticle(cId, name));
			 }
			 DataUpManage.saveVerCode("readArticle"+actType,
						verCode);
		}
		UIhandler.sendEmptyMessage(dataCompelet);
	}
	private void getCache() {
		if (!isTotal) {
			 data[0].addAll((ArrayList<ArticleItem>)
			 IoUtils.instance()
			 .getVedioArticle(cId, name));
		} else {
			 for (int i = 0; i < Cid.size(); i++) {
			 cId = Cid.get(i);
			 name = IoUtils.instance().getCidName(cId);
			 data[0].addAll((ArrayList<ArticleItem>) IoUtils
			 .instance().getVedioArticle(cId, name));
			 }
		}
		UIhandler.sendEmptyMessage(dataCompelet);
	}
}
