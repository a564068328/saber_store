package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.entity.DeductCurrencyInfo;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.BuyCourseAdapter;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.adapter.entity.ArticleItem;
import com.icloud.listenbook.unit.AccUtils;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.DateKit;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ThreadPoolUtils;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class BuyCourseAct extends BaseActivity implements OnClickListener,
		Response.Listener<JSONObject>, Response.ErrorListener {
	private static final int startItemNum = 2;
	private long cid;
	private int page = 0;
	private String pageTitle;
	private View back;
	private TextView titleTxt;
	private NetworkImageView icon;
	private TextView tv_title;
	private TextView tv_buydate;
	private TextView tv_price;
	private TextView tv_buy;// 立即购买
	private RecyclerView list;
	ImageLoader imageLoader;
	String url;
	String mAbstract;
	private View progress;
	private View tip;
	private List<ArticleItem> articleList;
	private ArrayList<ArticleChapterItem> chapterInfos;
	private ArrayList<DeductCurrencyInfo> deductCurrencyInfos;
	private LinearLayoutManager manager;
	private BuyCourseAdapter adapter;
	private int count = 0;
	private int price = 0;
	private View rl_top;
	private int deductPosition = 0;
	private DeductCurrencyInfo deductCurrencyInfo;
	private int queryPayedPosition = 0;
	private Date buyDate;

	@Override
	public void init() {
		cid = getIntent().getLongExtra("cid", 85);
		pageTitle = getIntent().getStringExtra("title");
		url = getIntent().getStringExtra("icon");
		mAbstract = getIntent().getStringExtra("intro");
		RequestQueue mQueue = Volley.newRequestQueue(this);
		LruImageCache lruImageCache = LruImageCache.instance(this
				.getApplicationContext());
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		articleList = new ArrayList<ArticleItem>();
		chapterInfos = new ArrayList<ArticleChapterItem>();
		deductCurrencyInfos = new ArrayList<DeductCurrencyInfo>();
		adapter = new BuyCourseAdapter(this);
		manager = new LinearLayoutManager(this);
		deductCurrencyInfos.add(new DeductCurrencyInfo("课程简介",
				mAbstract.trim(), DeductCurrencyInfo.TYPE_ABSTRACT));
		deductCurrencyInfos.add(new DeductCurrencyInfo("课程列表", "课程列表",
				DeductCurrencyInfo.TYPE_TITLE));
	}

	@Override
	public int getLayout() {
		return R.layout.act_buy_course;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		icon = (NetworkImageView) findViewById(R.id.icon);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_buydate = (TextView) findViewById(R.id.tv_buydate);
		tv_buy = (TextView) findViewById(R.id.tv_buy);
		list = (RecyclerView) findViewById(R.id.list);
		progress = findViewById(R.id.progress);
		rl_top = findViewById(R.id.rl_top);
		tip = findViewById(R.id.tip);
		list.setLayoutManager(manager);
		list.setAdapter(adapter);
		list.setHasFixedSize(true);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		tv_price.setOnClickListener(this);
		tv_price.setOnTouchListener(ViewUtils.instance().onTouchListener);
		tv_buy.setOnClickListener(this);
		tv_buy.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	@Override
	public void setDatas() {
		titleTxt.setText(pageTitle);
		icon.setDefaultImageResId(R.drawable.icon_default_img);
		icon.setErrorImageResId(R.drawable.icon_default_img);
		icon.setImageUrl(ServerIps.getLoginAddr() + url, imageLoader);
		tv_title.setText(pageTitle);
		tv_buy.setText("立即购买");
	}

	@Override
	public void getDatas() {
		HttpUtils.readNewArticle(cid, page, "0", this, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		// 彩蛋
		case R.id.tv_price:

			break;
		case R.id.tv_buy:
			if (price == 0) {
				ToastUtil.showMessage("该课程已全部购买～");
				break;
			}
			if (price > 0) {
				if (UserInfo.instance().isLogin()) {
					if (UserInfo.instance().getCurrency() < price) {
						ArticleChapterItem articleChapterItem = new ArticleChapterItem();
						articleChapterItem.setCpName(pageTitle);
						articleChapterItem.setMCurrency(price);
						AccUtils.showTip(BuyCourseAct.this, articleChapterItem);
						return;
					} else {
						deductCurrencyInfo = deductCurrencyInfos
								.get(deductPosition);
						HttpUtils.deductCurrency(deductCurrencyInfo.getAid(),
								deductCurrencyInfo.getCpid(),
								new DeductCurrency());
					}
				} else {
					LoadingTool.launchActivity(BuyCourseAct.this,
							LoginAct.class);
					return;
				}
			}

			break;
		default:
			break;
		}
	}

	private class DeductCurrency implements Response.Listener<JSONObject> {
		@Override
		public void onResponse(JSONObject response) {
			try {
				int res = response.getInt("result");
				if (res == 0) {
					String msg = response.getString("msg");
					if (msg.equals("扣除金币成功")) {
						deductCurrencyInfo
								.setStatus(DeductCurrencyInfo.STATUS_YET_BUY);
						IoUtils.instance().saveArticleChapterTtack(
								deductCurrencyInfo);
						double currency = response.optDouble("currency",
								UserInfo.instance().getCurrency());
						UserInfo.instance().setCurrency(currency);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				deductPosition++;
				if (deductPosition < deductCurrencyInfos.size()) {
					deductCurrencyInfo = deductCurrencyInfos
							.get(deductPosition);
					HttpUtils.deductCurrency(deductCurrencyInfo.getAid(),
							deductCurrencyInfo.getCpid(), new DeductCurrency());
				} else {
					deductPosition = 0;
					adapter.up(deductCurrencyInfos);
				}
			}
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		RequestError();
	}

	private void RequestError() {
		tip.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
	}

	@Override
	public void onResponse(final JSONObject response) {
		final int res = response.optInt("result", -1);
		LogUtil.e("getAllNewArticleResponse", response.toString());
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
									Configuration.TYPE_VEDIO);
							articleList.add(articleItem);
							// 更新文章
							IoUtils.instance().saveArticle(articleItem);
						}
						page++;
						HttpUtils.readNewArticle(cid, page, "0",
								BuyCourseAct.this, BuyCourseAct.this);
					} else {
						// 完成文章部分数据请求
						getChapterInfo();

					}
				} else {
					RequestError();
				}
			}
		});
	}

	// 得到文章相关信息
	private void getChapterInfo() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(articleList.size()==0){
					tip.setVisibility(View.VISIBLE);
					progress.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		for (final ArticleItem info : articleList) {
			HttpUtils.getArticleChapterInfo(info.getAId(), 0,
					new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							try {
								LogUtil.e(Tag, "getArticleChapterInfo"
										+ response.toString());
								int res = response.optInt("result", -1);
								if (res == 0) {
									JSONArray jsonArray = response
											.optJSONArray("list");
									ArticleChapterItem infoItem;
									JSONObject item;

									for (int i = 0; i < jsonArray.length(); i++) {
										item = jsonArray.getJSONObject(i);
										infoItem = JsonUtils.toArticle(
												info.getAId(), item);
										if (!chapterInfos.contains(infoItem)) {
											chapterInfos.add(infoItem);
										}
									}
									if (count == (articleList.size() - 1)) {
										ArrayList<DeductCurrencyInfo> chapterTtack = IoUtils
												.instance()
												.getArticleChapterTtack(
														chapterInfos,
														cid,
														DeductCurrencyInfo.TYPE_ITEM);
										// 得到购买时间
										buyDate = chapterTtack.get(0)
												.getDateline();
										for (DeductCurrencyInfo info : chapterTtack) {
											if (info.getDateline() != null
													&& info.getStatus() != DeductCurrencyInfo.STATUS_NO_BUY) {
												if (info.getDateline().after(
														buyDate))
													buyDate = info
															.getDateline();
												// LogUtil.e(Tag,
												// DateKit.friendlyFormat(buyDate));
											}
										}

										deductCurrencyInfos
												.addAll(chapterTtack);
										if (deductCurrencyInfos.size() <= startItemNum)
											return;
										// 判断文章是否购买过
										queryPayedPosition = startItemNum;
										isPayed();
									}
								} else {
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							} finally {
								count++;
							}
						}
					}, null);
		}
	}

	protected void isPayed() {
		deductCurrencyInfo = deductCurrencyInfos.get(queryPayedPosition);
		if (deductCurrencyInfo.getCpid() > 0) {
			HttpUtils.isPay(deductCurrencyInfo.getAid(),
					deductCurrencyInfo.getCpid(), new IsPayInfo());
		}

	}

	private void setView() {
		rl_top.setVisibility(View.VISIBLE);
		tv_price.setVisibility(View.VISIBLE);
		tv_price.setText("购买所需金币为：" + price);
		adapter.up(deductCurrencyInfos);
		progress.setVisibility(View.GONE);
		if (price == 0) {
			tv_buy.setText("已全部购买");
			tv_buydate.setText("购买时间为：" + DateKit.friendlyFormat(buyDate));
		} else {
			tv_buydate.setText("尚未全部购买");
		}
	}

	private class IsPayInfo implements Response.Listener<JSONObject> {
		@Override
		public void onResponse(JSONObject response) {
			LogUtil.e(Tag, "IsPayInfo" + response.toString());
			try {
				int res = response.optInt("result", -1);
				if (res == 0) {
					int isPayed = response.getInt("isPayed");
					// 返回参数 isPayed=1 表示已购买 0表示未购买
					if (isPayed == 1) {
						deductCurrencyInfo
								.setStatus(DeductCurrencyInfo.STATUS_YET_BUY);
						IoUtils.instance().saveArticleChapterTtack(
								deductCurrencyInfo);
						deductCurrencyInfos.remove(queryPayedPosition);
						deductCurrencyInfos.add(queryPayedPosition,
								deductCurrencyInfo);
					} else {
						price += deductCurrencyInfo.getPrice();
					}
					if (queryPayedPosition == deductCurrencyInfos.size() - 1) {
						setView();
					} else {
						++queryPayedPosition;
						isPayed();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {

			}
		}
	}
}
