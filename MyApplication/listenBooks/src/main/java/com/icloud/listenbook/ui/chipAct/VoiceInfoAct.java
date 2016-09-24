package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.service.MPManage;
import com.icloud.listenbook.ui.adapter.VoiceChapterAdapter;
import com.icloud.listenbook.ui.adapter.VoiceChapterAdapter.ViewPlayListner;
import com.icloud.listenbook.ui.adapter.VoiceCommentsAdapter;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.adapter.entity.DownLoadListener;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.VoicePlayView;
import com.icloud.listenbook.unit.VoicePlayView.MusicListener;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.down.DownloadManager;
import com.icloud.wrzjh.base.utils.down.Downloads;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.ArticleChapterInfo;
import com.listenBook.greendao.ArticleFeedback;
import com.yaya.live.android.util.Log;

public class VoiceInfoAct extends BaseActivity implements OnClickListener,
		RadioGroup.OnCheckedChangeListener, DownLoadListener, ViewPlayListner,
		MusicListener {
	public static final int PAGE_MSG = 1;
	public static final int PAGE_DATA = PAGE_MSG + 1;
	public static final int UP_DOWN = 0;
	protected View back;
	protected TextView title;
	protected RecyclerView list;
	public Article article;
	RadioGroup tabs;
	VoiceCommentsAdapter voiceCommentsAdapter;
	LinearLayoutManager commentsLM;
	int commentPage = 0;
	protected int commentLastVisibleItem;
	ArrayList<ArticleFeedback> datas;

	/*** 章节界面 **/
	View chapter_l;
	RecyclerView chapterList;
	VoiceChapterAdapter chapterInfoAdapter;
	public ArrayList<ArticleChapterItem> chapterInfos;
	LinearLayoutManager chaptersLM;
	int chapterPage = 0;
	protected int chapterLastVisibleItem;
	/** 音乐播放控制视图 **/
	VoicePlayView voicePlayView;
	/** 下载器 **/
	DownloadManager dlManager;
	/** 下载监听队列 **/
	HashMap<Long, Integer> downList;
	private DownloadChangeObserver downloadObserver;
	private CompleteReceiver completeReceiver;
	private long dlUpTimeInterval;
	int viewPage = 0;
	boolean isVoicePlayLoad;

	@Override
	protected void onResume() {
		super.onResume();
		getContentResolver().registerContentObserver(Downloads.CONTENT_URI,
				true, downloadObserver);
		registerReceiver(completeReceiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		if (null != voicePlayView)
			voicePlayView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		getContentResolver().unregisterContentObserver(downloadObserver);
		unregisterReceiver(completeReceiver);
		if (null != voicePlayView)
			voicePlayView.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != voicePlayView)
			voicePlayView.onStop();
		if (null != voicePlayView)
			voicePlayView.onDestroy();
	}

	@Override
	public void init() {

		/*** 页面数据 */
		String value = this.getIntent().getStringExtra("data");
		article = JsonUtils.fromJson(value, Article.class);
		viewPage = this.getIntent().getIntExtra("page", PAGE_MSG);
		datas = new ArrayList<ArticleFeedback>();
		/** 评论相关 */
		voiceCommentsAdapter = new VoiceCommentsAdapter(this, article, datas);
		commentsLM = new LinearLayoutManager(this);
		commentsLM.setOrientation(LinearLayoutManager.VERTICAL);
		/** 章节数据 */
		chapterInfos = new ArrayList<ArticleChapterItem>();
		chapterInfoAdapter = new VoiceChapterAdapter(this, chapterInfos);
		chaptersLM = new LinearLayoutManager(this);
		chaptersLM.setOrientation(LinearLayoutManager.VERTICAL);

		/** 下载相关 */
		downList = new HashMap<Long, Integer>();
		dlManager = new DownloadManager(getContentResolver(), getPackageName());
		dlManager.setAccessAllDownloads(true);
		downloadObserver = new DownloadChangeObserver();
		completeReceiver = new CompleteReceiver();
		/** 音乐播放 */
		voicePlayView = new VoicePlayView(this);
		isVoicePlayLoad = true;

	}

	/** 获取是否下载 */
	public String getDLUrl(long did) {
		if (dlManager.getStatus(did) == DownloadManager.STATUS_SUCCESSFUL) {
			return dlManager.getLocalUri(did);
		}
		return null;
	}

	@Override
	public int getLayout() {
		return R.layout.act_voice_info;
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
	}

	@Override
	public void setListeners() {
		chapterInfoAdapter.addDownList(this);
		chapterInfoAdapter.addVoiceManage(this);
		voicePlayView.addMusicListener(this);
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
	public void onBackPressed() {
		super.onBackPressed();
		MPManage.sendBroadcastToService(this,
				com.icloud.listenbook.unit.Configuration.STATE_PAUSE,
				new Intent());
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			/** 停止音乐 */
			MPManage.sendBroadcastToService(this,
					com.icloud.listenbook.unit.Configuration.STATE_PAUSE,
					new Intent());
			this.finish();
			break;
		case R.id.goPlay:
			viewPage = PAGE_DATA;
			upPage();
			break;
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
								} else if (isVoicePlayLoad) {
									isVoicePlayLoad = false;
									voicePlayView.load();
								}
								IoUtils.instance().saveArticleChapterInfo(
										downInfos);
								DataUpManage.save(VoiceInfoAct.this,
										"getArticleChapterInfo");
								chapterInfoAdapter.setSelectIndex(voicePlayView
										.getPlayIndex());
								UpDownDatas();
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

	/** 更新下载队列 */
	protected void UpDownDatas() {
		IoUtils.instance().upArticleChapterInfo(this, chapterInfos);
		chapterInfoAdapter.notifyDataSetChanged();
		/** 加载下载队列 **/
		addDownList();
	}

	/** 添加到到下载队列 */
	protected void addDownList() {
		ArticleChapterItem item;
		for (int i = 0; i < chapterInfos.size(); i++) {
			item = chapterInfos.get(i);
			if (item.did != -1
					&& item.status != DownloadManager.STATUS_SUCCESSFUL) {
				downList.put(item.did, i);
			}
		}
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
								DataUpManage.save(VoiceInfoAct.this,
										"getArticleFeedback");
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
							getArticleFeedbackCache();
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
						JsonUtils.toArticle(article, response);
						if (!TextUtils.isEmpty(article.getAName())) {
							title.setText(article.getAName());
						}
						voiceCommentsAdapter.setArticle(article);
						IoUtils.instance().saveArticle(article);
					}
				} catch (Exception e) {
				}
			}
		}, null);
	}

	@Override
	public void getDatas() {
		super.getDatas();
		getArticleInfo();
		/** 数据 */
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

	/** 加载评论缓存 **/
	private void getArticleFeedbackCache() {
		datas.clear();
		datas.addAll(IoUtils.instance().getArticleFeedback(article.getAId()));
		voiceCommentsAdapter.upDatas(datas);
	}

	/** 加载章节缓存 **/
	private void getArticleChapterInfoCache() {
		chapterInfos.clear();
		chapterInfos.addAll(IoUtils.instance().getArticleChapterInfo(this,
				article.getAId()));
		chapterInfoAdapter.upDatas(chapterInfos);
		if (isVoicePlayLoad) {
			isVoicePlayLoad = false;
			voicePlayView.load();
		}
		chapterInfoAdapter.setSelectIndex(voicePlayView.getPlayIndex());

		addDownList();
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
	public void down(int postion) {
		try {
			ArticleChapterItem item = chapterInfos.get(postion);
			item.status = dlManager.getStatus(item.did);
			switch (item.status) {
			/** 下载中 --> 暂停 */
			case DownloadManager.STATUS_PENDING:
			case DownloadManager.STATUS_RUNNING: {
				dlManager.pauseDownload(item.did);
				break;
			}
			/*** 暂停 --> 继续下载 */
			case DownloadManager.STATUS_PAUSED: {
				dlManager.resumeDownload(item.did);
				break;
			}
			/** 完成 --> 询问是否删除 */
			case DownloadManager.STATUS_SUCCESSFUL: {
				DialogManage.showAlertDialog(this, item.getCpName(),
						R.string.download_del, R.string.cancel, R.string.del,
						getDeleteClickHandler(postion));
				break;
			}
			/*** 失败 -->重新下载 */
			case DownloadManager.STATUS_FAILED: {
				dlManager.restartDownload(item.did);
				downList.put(item.did, postion);
				break;
			}
			/** 没有在下载队列 加入下载 **/
			default: {
				startDownload(item, postion, !HttpUtils.isWifi(this));
				break;
			}
			}
		} catch (Exception e) {
		}
	}

	protected void startDownload(ArticleChapterItem item, int postion,
			boolean isShow) {
		if (isShow) {
			DialogManage.showAlertDialog(this, item.getCpName(),
					R.string.download_no_wifi, R.string.cancel,
					R.string.confirm, getDownClickHandler(item, postion));
		} else {
			String url = item.getCpUrl();
			String path = url.substring(0, url.lastIndexOf("/") + 1);
			item.did = dlManager.down(this, url, path, item.getCpName());
			downList.put(item.did, postion);
			IoUtils.instance()
					.saveDown(item.getAid(), item.did, item.getCpId());
		}
	}

	public void upDLdateView() {
		/** 获取用户信息更新 **/
		ArticleChapterItem item;

		for (Entry<Long, Integer> entry : downList.entrySet()) {
			Integer index = entry.getValue();
			if (index != null && index >= 0 && index < chapterInfos.size()) {
				item = chapterInfos.get(index);
				/** 获取下载信息 */
				int[] status = dlManager.getBytesAndStatus(entry.getKey());
				item.status = status[2];
				item.pos = com.icloud.wrzjh.base.utils.TextUtils
						.getProgressValue(status[1], status[0]);
				LogUtil.e("sdadasd", item.status + ":" + item.pos);
			}
		}
		chapterInfoAdapter.upDatas(chapterInfos);
	}

	Handler upHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UP_DOWN:
				upDLdateView();
				break;
			}
		}
	};

	/** 下载变化监听 **/
	class DownloadChangeObserver extends ContentObserver {
		public DownloadChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			long time = System.currentTimeMillis();
			upHandler.removeMessages(UP_DOWN);
			if ((time - dlUpTimeInterval) > (1 * 250)) {
				dlUpTimeInterval = time;
				upHandler.obtainMessage(UP_DOWN).sendToTarget();
			} else {
				upHandler.sendMessageDelayed(upHandler.obtainMessage(UP_DOWN),
						(1 * 200));
			}
		}

	}

	/** 下载完成监听 **/
	class CompleteReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			long completeDownloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (completeDownloadId != -1) {
				Integer index = downList.get(completeDownloadId);
				if (index != null && index >= 0 && index < chapterInfos.size()) {
					ArticleChapterItem item = chapterInfos.get(index);
					/** 获取下载信息 */
					int[] status = dlManager
							.getBytesAndStatus(completeDownloadId);
					item.status = status[2];
					item.pos = com.icloud.wrzjh.base.utils.TextUtils
							.getProgressValue(status[1], status[0]);
					chapterInfoAdapter.notifyDataSetChanged();
					/*** 下载失败 删除下载监听队列 */
					if (item.status == DownloadManager.STATUS_FAILED) {
						ToastUtil.showMessage(item.getCpName() + "\t下载失败,请重试");
						downList.remove(item.did);
					} else
					/*** 下载完成 删除下载监听队列 */
					if (item.status == DownloadManager.STATUS_SUCCESSFUL) {
						downList.remove(item.did);
					}
				}
			}
		}
	};

	/** 下载触发 */
	private DialogInterface.OnClickListener getDownClickHandler(
			final ArticleChapterItem item, final int postion) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startDownload(item, postion, false);
			}
		};
	}

	/**
	 * 删除触发
	 */
	private DialogInterface.OnClickListener getDeleteClickHandler(
			final int postion) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteDownload(postion);
			}
		};
	}

	/**
	 * 从数据库 和数据中删除下载对象
	 */
	private void deleteDownload(int postion) {
		if (postion >= chapterInfos.size() || chapterInfos.size() < 0)
			return;
		ArticleChapterItem item = chapterInfos.get(postion);
		long downloadId = item.did;
		int status = dlManager.getStatus(downloadId);
		boolean isComplete = status == DownloadManager.STATUS_SUCCESSFUL
				|| status == DownloadManager.STATUS_FAILED;
		String localUri = dlManager.getLocalUri(downloadId);
		if (isComplete && localUri != null) {
			String path = Uri.parse(localUri).getPath();
			if (path.startsWith(Environment.getExternalStorageDirectory()
					.getPath())) {
				try {
					dlManager.markRowDeleted(downloadId);
				} catch (Exception e) {
					LogUtil.e("tag", e.toString());
				}
			}
		} else {
			dlManager.remove(downloadId);
		}
		item.reset();
		chapterInfoAdapter.upDatas(chapterInfos);
		/** 从下载数据库删除 **/
		IoUtils.instance().delDown(item.getCpId());

	}

	@Override
	public void play(int postion) {
		chapterInfoAdapter.setSelectIndex(postion);
	}

	@Override
	public void playIn(int position) {
		voicePlayView.palyTip(position);

	}
}
