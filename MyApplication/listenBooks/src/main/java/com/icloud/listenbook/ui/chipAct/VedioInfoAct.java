package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.widget.GridLayoutManager;
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
import com.icloud.listenbook.base.view.CusView;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.service.MPManage;
import com.icloud.listenbook.ui.adapter.VedioChapterAdapter;
import com.icloud.listenbook.ui.adapter.VedioChapterAdapter.SelectListener;
import com.icloud.listenbook.ui.adapter.VedioCommentsAdapter;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.MethodUtils;
import com.icloud.listenbook.unit.PlayerUnit;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.listenbook.unit.VedioPlayView;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.down.DownloadManager;
import com.icloud.wrzjh.base.utils.down.Downloads;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.ArticleChapterInfo;
import com.listenBook.greendao.ArticleFeedback;
import com.listenBook.greendao.Collect;

public class VedioInfoAct extends BaseActivity implements OnClickListener,
		RadioGroup.OnCheckedChangeListener, SelectListener {

	public static final String DOWN_PATH = "vedio/";
	public static final int UP_DOWN = 0;
	RadioGroup tabs;
	public Article article;
	/*** 选集 **/
	TextView share, collection, downTxt;
	RecyclerView dataList;
	LinearLayoutManager chaptersGM;
	View dataL;
	public ArrayList<ArticleChapterItem> chapterInfos;
	VedioChapterAdapter chapterAdapter;
	/***/
	/*** 评论 **/
	RecyclerView commlist;
	int commentPage = 0;
	protected int commentLastVisibleItem;
	ArrayList<ArticleFeedback> commInfos;
	LinearLayoutManager commentsLM;
	VedioCommentsAdapter commentsAdapter;

	/***/
	public VedioPlayView vedioPlayView;
	int[] proDrawId = { R.drawable.icon_vedio_down, R.drawable.progress_stop,
			R.drawable.progress_end };

	/** 下载下标 */
	CusView downPos;
	View downL;
	int downLoadPos;
	/** 下载器 **/
	DownloadManager dlManager;
	/** 下载监听队列 **/
	HashMap<Long, Integer> downList;
	private DownloadChangeObserver downloadObserver;
	private CompleteReceiver completeReceiver;
	private long dlUpTimeInterval;
	WakeLock wakeLock;
	Timer timer;

	@Override
	public void init() {
		/** 关闭音乐 */
		MPManage.sendBroadcastToService(this,
				com.icloud.listenbook.unit.Configuration.STATE_STOP,
				new Intent());
		String value = this.getIntent().getStringExtra("data");
		article = JsonUtils.fromJson(value, Article.class);
		// LogUtil.e("tag", "article.getMedia():"+article.getMedia());
		/** 章节 **/
		chaptersGM = new LinearLayoutManager(this);
		chaptersGM.setOrientation(LinearLayoutManager.VERTICAL);
		chapterInfos = new ArrayList<ArticleChapterItem>();
		chapterAdapter = new VedioChapterAdapter(this, chapterInfos, 0);

		/** 评论 */
		commentsLM = new LinearLayoutManager(this);
		commentsLM.setOrientation(LinearLayoutManager.VERTICAL);
		commInfos = new ArrayList<ArticleFeedback>();
		commentsAdapter = new VedioCommentsAdapter(this, article, commInfos);

		/** 下载相关 **/
		// 下载队列
		downList = new HashMap<Long, Integer>();
		// 下载监听
		downloadObserver = new DownloadChangeObserver();
		completeReceiver = new CompleteReceiver();
		// 下载器
		dlManager = new DownloadManager(getContentResolver(), getPackageName());
		dlManager.setAccessAllDownloads(true);
		/*** 视频播放器 */
		vedioPlayView = new VedioPlayView(this);

		boolean isPlay = this.getIntent().getBooleanExtra("isPlay", false);
		vedioPlayView.setAutoPlay(isPlay);
		if (isPlay) {
			vedioPlayView.changeSurfaceSize();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getContentResolver().registerContentObserver(Downloads.CONTENT_URI,
				true, downloadObserver);
		registerReceiver(completeReceiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		setCollectIcon();
		if (null != vedioPlayView)
			vedioPlayView.onResume();
		wakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
						| PowerManager.ON_AFTER_RELEASE, Tag);
		wakeLock.acquire();

	}

	@Override
	protected void onPause() {
		super.onPause();
		getContentResolver().unregisterContentObserver(downloadObserver);
		unregisterReceiver(completeReceiver);
		if (null != vedioPlayView)
			vedioPlayView.onPause();
		if (wakeLock != null) {
			wakeLock.release();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		vedioPlayView.onConfigurationChanged(newConfig);
	}

	@Override
	public int getLayout() {
		return R.layout.act_vedio_info;
	}

	@Override
	public void findViews() {
		tabs = (RadioGroup) findViewById(R.id.tabs);
		/** 章节 **/
		dataList = (RecyclerView) findViewById(R.id.datalist);
		dataL = findViewById(R.id.data_l);
		/** 评论 **/
		commlist = (RecyclerView) findViewById(R.id.commlist);
		/** 下载分享 */
		share = (TextView) findViewById(R.id.share);
		collection = (TextView) findViewById(R.id.collection);
		downTxt = (TextView) findViewById(R.id.down);
		downL = findViewById(R.id.down_l);
		downPos = (CusView) findViewById(R.id.downPos);
	}

	@Override
	public void setListeners() {
		share.setOnClickListener(this);
		collection.setOnClickListener(this);
		downL.setOnClickListener(this);
		tabs.setOnCheckedChangeListener(this);
		chapterAdapter.addSelectList(this);
		share.setOnTouchListener(ViewUtils.instance().onTouchListener);
		collection.setOnTouchListener(ViewUtils.instance().onTouchListener);
		downTxt.setOnTouchListener(ViewUtils.instance().onTouchListener);

		/** 评论滑动加载 **/
		commlist.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE
						&& commentLastVisibleItem + 1 == commentsAdapter
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

	}

	@Override
	public void setDatas() {
		super.setDatas();
		/** 初始化下载进度条 */
		downPos.init(proDrawId);
		/** 视频选择界面 */
		dataList.setLayoutManager(chaptersGM);
		dataList.setAdapter(chapterAdapter);
		/** 评论界面 */
		commlist.setLayoutManager(commentsLM);
		commlist.setAdapter(commentsAdapter);
		dataL.setVisibility(View.VISIBLE);
		commlist.setVisibility(View.GONE);

		/** 收藏初始化 */
		setCollectIcon();

	}

	@Override
	public void getDatas() {
		super.getDatas();
		/** 获取章节信息 **/
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
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.dataBtn:
			dataL.setVisibility(View.VISIBLE);
			commlist.setVisibility(View.GONE);
			break;
		case R.id.commentsBtn:
			dataL.setVisibility(View.GONE);
			commlist.setVisibility(View.VISIBLE);
			break;

		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.share:
			onShare();
			break;
		case R.id.collection:
			/** 收藏 */
			onCollect();
			break;
		case R.id.down_l:
			/** 下载 */
			onDownLoad();
			break;
		}
	}

	/*** 收藏设置 */
	public void setCollectIcon() {
		/** 根据收藏 切换不同图片 */
		int drawableId = R.drawable.icon_collection;
		Drawable icon = this.getResources().getDrawable(drawableId);
		icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
		collection.setCompoundDrawables(icon, null, null, null);
		try {
			if (ToolUtils.isCollect(article.getAId())) {
				drawableId = R.drawable.icon_collection;
				collection.setText(R.string.collect);
			} else {
				drawableId = R.drawable.icon_collection_ing;
				collection.setText(R.string.un_collect);
			}
			icon = this.getResources().getDrawable(drawableId);
			icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
			collection.setCompoundDrawables(icon, null, null, null);
		} catch (Exception e) {
		}
	}

	/** 收藏数据库设置 */
	public void onCollect() {
		ToolUtils.collect(this, article.getAId());
		setCollectIcon();
	}

	/** 保存缓存标示 */
	protected void saveVerCode(long aId, int page, String verCode) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("aId", aId);
			jb.put("page", page);
			DataUpManage.saveVerCode("getArticleChapterInfo" + jb.toString(),
					verCode);
		} catch (Exception e) {

		}
	}

	/**** 获取章节信息 */
	protected void getArticleChapterInfo() {

		HttpUtils.getArticleChapterInfo(article.getAId(), 0,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject json) {
						try {
							LogUtil.e(Tag,
									"getArticleChapterInfo" + json.toString());
							int res = json.optInt("result", -1);
							if (res == 0) {
								saveVerCode(article.getAId(), 0,
										json.optString("verCode", "0"));
								JSONArray jsonArray = json.optJSONArray("list");
								ArrayList<ArticleChapterInfo> saveItem = new ArrayList<ArticleChapterInfo>();
								ArticleChapterItem infoItem;
								JSONObject item;
								for (int i = 0; i < jsonArray.length(); i++) {
									item = jsonArray.getJSONObject(i);
									infoItem = JsonUtils.toArticle(
											article.getAId(), item);
									if (!chapterInfos.contains(infoItem)) {
										chapterInfos.add(infoItem);
										if (!saveItem.contains(infoItem))

											saveItem.add(infoItem);

									}
								}

								chapterAdapter.upDatas(chapterInfos);
								up(chapterAdapter.getSelectIndex());

								IoUtils.instance().delArticleChapterInfo(
										article.getAId());
								IoUtils.instance().saveArticleChapterInfo(
										saveItem);
								DataUpManage.save(VedioInfoAct.this,
										"getArticleChapterInfo");
								/** 获取下载数据 */
								upDownDatas();
								// media小于4的article可能为null
								if (article == null)
									return;
								// 增加网络精选
								addNetSelection();
								chapterAdapter.upDatas(chapterInfos);
							} else {
								getArticleChapterInfoCache();
							}
						} catch (Exception e) {

						}
					}

				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (chapterInfos.size() == 0) {
							getArticleChapterInfoCache();
						}
					}
				});
	}

	/** 更新 下载数据 */
	protected void upDownDatas() {
		IoUtils.instance().upArticleChapterInfo(this, chapterInfos);
		/** 加载下载队列 **/
		addDownList();
		upDownLoadBtn();

	}

	/** 添加下载数据 */
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

	/** 获取详细信息 **/
	public void getArticleInfo() {
		HttpUtils.getArticleInfo(article.getAId(), new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					LogUtil.e(Tag, "getArticleInfo" + response.toString());
					int res = response.optInt("result", -1);
					if (res == 0) {
						JsonUtils
								.toArticle(
										article,
										response,
										com.icloud.listenbook.unit.Configuration.TYPE_VEDIO);
						commentsAdapter.setArticle(article);
						IoUtils.instance().saveArticle(article);
					}
				} catch (Exception e) {
				}

			}
		}, null);
	}

	/*** 获取评论 */
	public void getArticleFeedback() {
		if (commentsAdapter.getStatus() != FooterManage.FREE)
			return;
		commentsAdapter.setStatus(FooterManage.LOADING);
		HttpUtils.getArticleFeedback(article.getAId(), commentPage,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject json) {
						try {
							LogUtil.e(Tag,
									"getArticleFeedback" + json.toString());
							commentsAdapter.setStatus(FooterManage.FREE);
							int res = json.optInt("result", -1);
							if (res == 0) {
								commentPage += 1;
								JSONArray jsonArray = json.optJSONArray("list");
								ArrayList<ArticleFeedback> down = new ArrayList<ArticleFeedback>();
								ArticleFeedback articleFeedback;
								JSONObject item;
								for (int i = 0; i < jsonArray.length(); i++) {
									item = jsonArray.optJSONObject(i);
									articleFeedback = JsonUtils
											.toArticleFeedback(
													article.getAId(), item);
									if (!commInfos.contains(articleFeedback)) {
										commInfos.add(articleFeedback);
										if (!down.contains(articleFeedback))
											down.add(articleFeedback);
									}
								}
								commentsAdapter.upDatas(commInfos);
								IoUtils.instance().saveArticleFeedback(down);
								DataUpManage.save(VedioInfoAct.this,
										"getArticleFeedback");
								if (jsonArray.length() == 0) {
									commentsAdapter
											.setStatus(FooterManage.NULL_DATAS);
								}
							}
						} catch (Exception e) {
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						commentsAdapter.setStatus(FooterManage.FREE);
						if (commInfos.size() == 0) {
							commInfos.addAll(IoUtils.instance()
									.getArticleFeedback(article.getAId()));
							commentsAdapter.upDatas(commInfos);
						}
					}
				});
	}

	/** 获取数据缓存 */
	protected void getArticleChapterInfoCache() {
		chapterInfos.addAll(IoUtils.instance().getArticleChapterInfo(this,
				article.getAId()));
		LogUtil.e("article.getAId()", "id" + article.getAId());
		// 增加网络精选
		article = IoUtils.instance().getArticleInfo(article.getAId());
		// LogUtil.e("TAG", article.toString());

		// LogUtil.e("Cache", "cache"+chapterInfos.size());
		chapterAdapter.upDatas(chapterInfos);
		up(chapterAdapter.getSelectIndex());
		addDownList();
		// LogUtil.e("Cache", "article.getMedia()"+article.getMedia());
		if (article == null)
			return;
		if (article.getMedia() >= com.icloud.listenbook.unit.Configuration.TYPE_WORD) {
			addNetSelection();
			chapterAdapter.upDatas(chapterInfos);
		}
	}

	protected void addNetSelection() {

		if (article.getMedia() >= com.icloud.listenbook.unit.Configuration.TYPE_WORD) {
			ArticleChapterItem item = new ArticleChapterItem();
			item.viewType = com.icloud.listenbook.unit.Configuration.STATE_NON;
			String Abstract = article.getAAbstract();
			Abstract = MethodUtils.delHead(Abstract);
			item.setCpDesc(Abstract);
			item.setCpName(article.getAName());
			chapterInfos.add(item);
		}
	}

	/** 加载评论缓存 **/
	private void getArticleFeedbackCache() {
		commInfos.clear();
		commInfos.addAll(IoUtils.instance()
				.getArticleFeedback(article.getAId()));
		commentsAdapter.upDatas(commInfos);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != vedioPlayView)
			vedioPlayView.onDestroy();
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
		PlayerUnit.instance().releaseView();
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	@Override
	public void up(int postion) {
		this.downLoadPos = postion;
		vedioPlayView.setInfo(postion);
		upDownLoadBtn();
	}

	protected void onShare() {
		if (downLoadPos < 0 || downLoadPos >= chapterInfos.size())
			return;
		ArticleChapterItem item = chapterInfos.get(downLoadPos);
		ToolUtils.shareDefa(this, item.getCpName());
	}

	public void onDownLoad() {
		if (downLoadPos < 0 || downLoadPos >= chapterInfos.size())
			return;
		try {
			ArticleChapterItem item = chapterInfos.get(downLoadPos);
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
						getDeleteClickHandler(downLoadPos));
				break;
			}
			/*** 失败 -->重新下载 */
			case DownloadManager.STATUS_FAILED: {
				dlManager.restartDownload(item.did);
				downList.put(item.did, downLoadPos);
				break;
			}
			/** 没有在下载队列 加入下载 **/
			default: {
				String url = item.getCpUrl();
				item.did = dlManager.down(this, url, DOWN_PATH,
						item.getCpName());
				downList.put(item.did, downLoadPos);
				IoUtils.instance().saveDown(item.getAid(), item.did,
						item.getCpId());
				break;
			}
			}
			/** 更新下载按钮 */
			upDownLoadBtn();
		} catch (Exception e) {
		}
	}

	/** 更新下载界面 */
	protected void upDLdateView() {
		if (downLoadPos < 0 || downLoadPos >= chapterInfos.size())
			return;
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
			}
		}
		if (downList.size() > 0)
			upDownLoadBtn();
	}

	/** 更新下载按钮 */
	protected void upDownLoadBtn() {
		if (downLoadPos < 0 || downLoadPos >= chapterInfos.size())
			return;
		ArticleChapterItem item;
		item = chapterInfos.get(downLoadPos);
		if (item != null) {
			downPos.setProgress(item.pos);
			downPos.setStatus(item.status == DownloadManager.STATUS_RUNNING);
			switch (item.status) {
			/** 下载中 -- */
			case DownloadManager.STATUS_PENDING:
			case DownloadManager.STATUS_RUNNING: {
				downTxt.setText(R.string.download_runing);
				break;
			}
			/*** 暂停 -- */
			case DownloadManager.STATUS_PAUSED: {
				downTxt.setText(R.string.download_pause);
				break;
			}
			/** 完成 -- */
			case DownloadManager.STATUS_SUCCESSFUL: {
				downTxt.setText(R.string.download_succe);
				break;
			}
			/*** 失败 -- */
			case DownloadManager.STATUS_FAILED: {
				downTxt.setText(R.string.download_failed);
				break;
			}
			/** 没有在下载队列 **/
			default: {
				downTxt.setText(R.string.download);
				break;
			}
			}
		}
	}

	/**
	 * 删除触发
	 */
	protected DialogInterface.OnClickListener getDeleteClickHandler(
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
	protected void deleteDownload(int postion) {
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
		/** 更新下载按钮 */
		upDownLoadBtn();
		/** 从下载数据库删除 **/
		IoUtils.instance().delDown(item.getCpId());

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
				/** 更新下载按钮 */
				upDownLoadBtn();
			}
		}
	}

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

	/**
	 * 获取下载链接 由于腾讯云播放问题 导致不可以播放离线视频 暂时屏蔽
	 * */
	public String getDLUrl(long did) {
		// if (dlManager.getStatus(did) == DownloadManager.STATUS_SUCCESSFUL) {
		// return dlManager.getLocalUri(did);
		// }
		return null;
	}

}
