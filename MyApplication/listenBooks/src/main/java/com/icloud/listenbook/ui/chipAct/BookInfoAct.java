package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.view.TxTView;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.BookChapterAdapter;
import com.icloud.listenbook.ui.adapter.BookCommentsAdapter;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.unit.AccUtils;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.io.FileUtils;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.ArticleChapterInfo;
import com.listenBook.greendao.ArticleFeedback;

public class BookInfoAct extends BaseActivity implements OnClickListener,
		RadioGroup.OnCheckedChangeListener {
	public static final int PAGE_MSG = 1;
	public static final int PAGE_DATA = PAGE_MSG + 1;
	protected View back;
	protected TextView title;
	protected RecyclerView list;
	public Article article;
	RadioGroup tabs;
	BookCommentsAdapter voiceCommentsAdapter;
	LinearLayoutManager commentsLM;
	int commentPage = 0;
	protected int commentLastVisibleItem;
	ArrayList<ArticleFeedback> datas;

	/*** 章节界面 **/
	View chapter_l;
	RecyclerView chapterList;
	BookChapterAdapter chapterInfoAdapter;
	public ArrayList<ArticleChapterItem> chapterInfos;
	LinearLayoutManager chaptersLM;
	int chapterPage = 0;
	protected int chapterLastVisibleItem;
	View listL;
	RadioButton dataBtn;
	int viewPage = 0;

	@Override
	public void init() {
		String value = this.getIntent().getStringExtra("data");
		viewPage = this.getIntent().getIntExtra("page", PAGE_MSG);
		article = JsonUtils.fromJson(value, Article.class);
		datas = new ArrayList<ArticleFeedback>();
		voiceCommentsAdapter = new BookCommentsAdapter(this, article, datas);
		commentsLM = new LinearLayoutManager(this);
		commentsLM.setOrientation(LinearLayoutManager.VERTICAL);

		chapterInfos = new ArrayList<ArticleChapterItem>();
		chapterInfoAdapter = new BookChapterAdapter(this, chapterInfos);
		chaptersLM = new LinearLayoutManager(this);
		chaptersLM.setOrientation(LinearLayoutManager.VERTICAL);
	}

	@Override
	public int getLayout() {
		return R.layout.act_book_info;
	}

	@Override
	public void findViews() {
		list = (RecyclerView) findViewById(R.id.list);
		back = findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		tabs = (RadioGroup) findViewById(R.id.tabs);
		back = findViewById(R.id.back);

		chapter_l = findViewById(R.id.chapter_l);
		chapterList = (RecyclerView) findViewById(R.id.chapter_list);
		listL = findViewById(R.id.list_l);
		dataBtn = (RadioButton) findViewById(R.id.dataBtn);
	}

	@Override
	public void setListeners() {
		// dataTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE
						&& commentLastVisibleItem + 1 == voiceCommentsAdapter
								.getItemCount()) {
					getArticleFeedback();
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				commentLastVisibleItem = commentsLM
						.findLastVisibleItemPosition();
			}
		});
		chapterList.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE
						&& chapterLastVisibleItem + 1 == chapterInfoAdapter
								.getItemCount()) {
					getArticleChapterInfo();
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				chapterLastVisibleItem = chaptersLM
						.findLastVisibleItemPosition();
			}
		});
		tabs.setOnCheckedChangeListener(this);
		voiceCommentsAdapter.addGotoListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.goPlay:
			viewPage = PAGE_DATA;
			upPage();
			break;

		}

	}

	protected void getTxtValueCache(ArticleChapterItem dataValue) {
		String value = (FileUtils.readPrivateContent(dataValue.getCpName()));
		if(!AccUtils.checkPay(this,dataValue)){
			return;
		}
		if (!TextUtils.isEmpty(value)) {
			// dataTxt.setText(value, BufferType.SPANNABLE);
		} else {
			getTxtValue(dataValue);
		}
	}

	protected void getTxtValue(final ArticleChapterItem dataValue) {
		final String url = dataValue.getCpUrl();
		if (!TextUtils.isEmpty(url)) {
			if(!AccUtils.checkPay(this,dataValue)){
				return;
			}
			HttpUtils.get(url, new Listener<String>() {
				@Override
				public void onResponse(String response) {
					try {
						String value = new String(response
								.getBytes("iso8859-1"), "UTF-8");
						if (!TextUtils.isEmpty(value)) {
							// dataTxt.setText(value, BufferType.SPANNABLE);
							if (FileUtils.writePrivateContent(
									dataValue.getCpName(), value)) {
								DataUpManage.save(BookInfoAct.this, url);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, null);

		}
	}

	protected void showChapterInfoView() {
		// if (chapterInfos.size() == 1) {
		// listL.setVisibility(View.INVISIBLE);
		// dataTxt.setVisibility(View.VISIBLE);
		// addSize.setVisibility(View.VISIBLE);
		// reduceSize.setVisibility(View.VISIBLE);
		// currencyTxt.setVisibility(View.VISIBLE);
		// dataTxt.setText("");
		// dataBtn.setText("内容");
		// final ArticleChapterItem dataValue = chapterInfos.get(0);
		// final String url = dataValue.getCpUrl();
		// currencyTxt.setText(this.getString(R.string.currency_tip_book,
		// dataValue.getMCurrency()));
		// if (DataUpManage.isUp(this, url)) {
		// getTxtValue(dataValue);
		// } else {
		// getTxtValueCache(dataValue);
		// }
		// IoUtils.instance().saveReadingTrack(article.getAId(), 0);
		// } else
		{
			listL.setVisibility(View.VISIBLE);
			// dataTxt.setVisibility(View.INVISIBLE);
			// addSize.setVisibility(View.INVISIBLE);
			// reduceSize.setVisibility(View.INVISIBLE);
			// currencyTxt.setVisibility(View.INVISIBLE);
			dataBtn.setText("章节列表");
		}
	}

	/*** 获取集数 */
	public void getArticleChapterInfo() {
		if (chapterInfoAdapter.getStatus() != FooterManage.FREE)
			return;
		chapterInfoAdapter.setStatus(FooterManage.LOADING);
		HttpUtils.getArticleChapterInfo(article.getAId(), chapterPage,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject json) {
						try {
							LogUtil.e(Tag,
									"getArticleChapterInfo" + json.toString());
							chapterInfoAdapter.setStatus(FooterManage.FREE);
							int res = json.optInt("result", -1);
							if (res == 0) {
								chapterPage += 1;
								JSONArray jsonArray = json.optJSONArray("list");
								ArrayList<ArticleChapterInfo> downInfos = new ArrayList<ArticleChapterInfo>();
								ArticleChapterItem infoItem;
								JSONObject item;
								for (int i = 0; i < jsonArray.length(); i++) {
									item = jsonArray.getJSONObject(i);
									infoItem = JsonUtils.toArticle(
											article.getAId(), item);
									if (!chapterInfos.contains(infoItem)) {
										chapterInfos.add(infoItem);
										if (!downInfos.contains(infoItem))
											downInfos.add(infoItem);
									}
								}
								chapterInfoAdapter.upDatas(chapterInfos);
								if (jsonArray.length() == 0) {
									chapterInfoAdapter
											.setStatus(FooterManage.NULL_DATAS);
								}
								showChapterInfoView();

								IoUtils.instance().saveArticleChapterInfo(
										downInfos);
							}
						} catch (Exception e) {

						}
					}

				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						chapterInfoAdapter.setStatus(FooterManage.FREE);
						if (chapterInfos.size() == 0) {
							getArticleChapterInfoCache();

						}
					}
				});
	}

	/*** 获取评论 */
	public void getArticleFeedback() {
		if (voiceCommentsAdapter.getStatus() != FooterManage.FREE)
			return;
		voiceCommentsAdapter.setStatus(FooterManage.LOADING);
		HttpUtils.getArticleFeedback(article.getAId(), commentPage,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject json) {
						try {
							LogUtil.e(Tag,
									"getArticleFeedback" + json.toString());
							voiceCommentsAdapter.setStatus(FooterManage.FREE);
							int res = json.optInt("result", -1);
							if (res == 0) {
								commentPage += 1;
								JSONArray jsonArray = json.optJSONArray("list");
								ArticleFeedback articleFeedback;
								ArrayList<ArticleFeedback> down = new ArrayList<ArticleFeedback>();
								JSONObject item;
								for (int i = 0; i < jsonArray.length(); i++) {
									item = jsonArray.optJSONObject(i);
									articleFeedback = JsonUtils
											.toArticleFeedback(
													article.getAId(), item);
									if (!datas.contains(articleFeedback)) {
										datas.add(articleFeedback);
										if (!down.contains(articleFeedback))
											down.add(articleFeedback);
									}
								}
								voiceCommentsAdapter.upDatas(datas);
								IoUtils.instance().saveArticleFeedback(down);
								if (jsonArray.length() == 0) {
									voiceCommentsAdapter
											.setStatus(FooterManage.NULL_DATAS);
								}
							}
						} catch (Exception e) {

						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						voiceCommentsAdapter.setStatus(FooterManage.FREE);
						if (datas.size() == 0) {
							datas.addAll(IoUtils.instance().getArticleFeedback(
									article.getAId()));
							voiceCommentsAdapter.upDatas(datas);
						}
					}
				});
	}

	/**** 获取详细信息 */
	public void getArticleInfo() {
		HttpUtils.getArticleInfo(article.getAId(), new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					LogUtil.e(Tag, "getArticleInfo" + response.toString());
					int res = response.optInt("result", -1);
					if (res == 0) {
						JsonUtils.toArticle(article, response,
								Configuration.TYPE_BOOK);
						voiceCommentsAdapter.setArticle(article);
						if (!TextUtils.isEmpty(article.getAName())) {
							title.setText(article.getAName());
						}
						IoUtils.instance().saveArticle(article);

					}
				} catch (Exception e) {

				}

			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
	}

	@Override
	public void getDatas() {
		super.getDatas();
		getArticleInfo();
		if (DataUpManage.isUp(this, "getArticleFeedback")) {
			getArticleFeedback();
		} else {
			getArticleFeedbackCache();
		}
		if (DataUpManage.isUp(this, "getArticleChapterInfo")) {
			getArticleChapterInfo();
		} else {
			getArticleChapterInfoCache();
		}

	}

	/** 加载评论缓存 **/
	private void getArticleFeedbackCache() {
		datas.clear();
		datas.addAll(IoUtils.instance().getArticleFeedback(article.getAId()));
		voiceCommentsAdapter.upDatas(datas);
	}

	@Override
	public void setDatas() {
		list.setVisibility(View.VISIBLE);
		chapter_l.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(article.getAName())) {
			title.setText(article.getAName());
		}
		list.setLayoutManager(commentsLM);
		list.setAdapter(voiceCommentsAdapter);

		chapterList.setLayoutManager(chaptersLM);
		chapterList.setAdapter(chapterInfoAdapter);
		upPage();
	}

	/** 加载章节缓存 **/
	private void getArticleChapterInfoCache() {
		chapterInfos.addAll(IoUtils.instance().getArticleChapterInfo(this,
				article.getAId()));
		chapterInfoAdapter.upDatas(chapterInfos);
		showChapterInfoView();
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		switch (checkedId) {
		case R.id.msgBtn:
			viewPage = PAGE_MSG;
			break;
		case R.id.dataBtn:
			viewPage = PAGE_DATA;
			break;
		}
		upPage();
	}

	/** 修改选中页 */
	protected void upPage() {
		switch (viewPage) {
		case PAGE_MSG:
			tabs.check(R.id.msgBtn);
			list.setVisibility(View.VISIBLE);
			chapter_l.setVisibility(View.GONE);
			break;
		case PAGE_DATA:
			tabs.check(R.id.dataBtn);
			list.setVisibility(View.GONE);
			chapter_l.setVisibility(View.VISIBLE);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
