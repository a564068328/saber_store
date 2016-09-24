package com.icloud.listenbook.ui.chipFrage;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseFragement;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.DownBookAdapter;
import com.icloud.listenbook.ui.adapter.DownBookAdapter.DelListener;
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.ui.popup.QueryPop;
import com.icloud.listenbook.ui.popup.QueryPop.QueryListener;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.down.DownloadManager;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.Down;

public class DownloadFrage extends BaseFragement implements
		RadioGroup.OnCheckedChangeListener, DelListener, QueryListener,
		OnClickListener, OnItemClickListener {
	RadioGroup tabs;
	ListView list;
	DownBookAdapter bookAdapter;
	ArrayList<Article> voiceData;
	ArrayList<Article> bookData;
	ArrayList<Article> vedioData;
	int page;
	/** 下载器 **/
	DownloadManager dlManager;
	QueryPop queryPop;
	View search;

	@Override
	public void init() {
		super.init();
		dlManager = new DownloadManager(act.getContentResolver(),
				act.getPackageName());
		dlManager.setAccessAllDownloads(true);
		bookAdapter = new DownBookAdapter(act);
		page = Configuration.TYPE_VOICE;
		queryPop = new QueryPop(findViewById(R.id.query_l));
	}

	@Override
	public void onResume() {
		super.onResume();
		voiceData = IoUtils.instance().getDownLoadAll(Configuration.TYPE_VOICE);
		bookData = IoUtils.instance().getDownLoadAll(Configuration.TYPE_BOOK);
		vedioData = IoUtils.instance().getDownLoadAll(Configuration.TYPE_VEDIO);
		upData();
	}

	@Override
	public int getLayout() {
		return R.layout.frage_download_page;
	}

	@Override
	public void setListeners() {
		list.setAdapter(bookAdapter);
		bookAdapter.addDelListener(this);
		tabs.setOnCheckedChangeListener(this);
		queryPop.addQueryListener(this);
		search.setOnClickListener(this);
		search.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.setOnItemClickListener(this);
	}

	@Override
	public void findViews() {
		tabs = (RadioGroup) findViewById(R.id.tabs);
		list = (ListView) findViewById(R.id.list);
		search = findViewById(R.id.search);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.music:
			page = Configuration.TYPE_VOICE;
			break;
		case R.id.video:
			page = Configuration.TYPE_VEDIO;
			break;
		case R.id.book:
			page = Configuration.TYPE_BOOK;
			break;

		}
		upData();
	}

	public void upData() {
		switch (page) {
		case Configuration.TYPE_VOICE:
			bookAdapter.upType(voiceData);
			break;
		case Configuration.TYPE_VEDIO:
			bookAdapter.upType(vedioData);
			break;
		case Configuration.TYPE_BOOK:
			bookAdapter.upType(bookData);
			break;

		}
	}

	@Override
	public void del(int postion) {
		ArrayList<Article> list;
		switch (this.page) {
		default:
		case Configuration.TYPE_VOICE:
			list = (voiceData);
			break;
		case Configuration.TYPE_VEDIO:
			list = (vedioData);
			break;
		case Configuration.TYPE_BOOK:
			list = (bookData);
			break;

		}
		if (postion < 0 || postion >= list.size())
			return;
		Article item = list.get(postion);
		DialogManage.showAlertDialog(act, item.getAName(),
				R.string.download_del, R.string.cancel, R.string.del,
				getDeleteClickHandler(page, postion));
	}

	/**
	 * 删除触发
	 */
	private DialogInterface.OnClickListener getDeleteClickHandler(
			final int page, final int postion) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteDownload(page, postion);
			}
		};
	}

	/**
	 * 从数据库 和数据中删除下载对象
	 */
	private void deleteDownload(int page, int postion) {
		ArrayList<Article> list;
		switch (page) {
		default:
		case Configuration.TYPE_VOICE:
			list = (voiceData);
			break;
		case Configuration.TYPE_VEDIO:
			list = (vedioData);
			break;
		case Configuration.TYPE_BOOK:
			list = (bookData);
			break;

		}
		if (postion >= list.size() || postion < 0)
			return;
		Article item = list.get(postion);
		list.remove(postion);
		bookAdapter.upType(list);
		/*** 获取所有删除对象 */

		List<Down> downList = IoUtils.instance()
				.getDownLoadInAid(item.getAId());
		for (Down down : downList) {
			String localUri = dlManager.getLocalUri(down.getDId());
			if (localUri != null) {
				String path = Uri.parse(localUri).getPath();
				if (path.startsWith(Environment.getExternalStorageDirectory()
						.getPath())) {
					try {
						dlManager.markRowDeleted(down.getDId());
					} catch (Exception e) {
						LogUtil.e("tag", e.toString());
					}
				}
			} else {
				dlManager.remove(down.getDId());
			}
		}
		/** 从下载数据库删除 **/
		IoUtils.instance().delDownInAid(item.getAId());
	}

	public ArrayList<Article> getVoiceData(String like) {
		ArrayList<Article> datas = new ArrayList<Article>();
		Article article;
		for (int i = 0; i < voiceData.size(); i++) {
			article = voiceData.get(i);
			if (article != null && !datas.contains(article))
				if (article.getAName().indexOf(like) != -1
						|| article.getAAuthor().indexOf(like) != -1)
					datas.add(article);
		}
		return datas;
	}

	public ArrayList<Article> getVedioData(String like) {
		ArrayList<Article> datas = new ArrayList<Article>();
		Article article;
		for (int i = 0; i < vedioData.size(); i++) {
			article = vedioData.get(i);
			if (article != null && !datas.contains(article))
				if (article.getAName().indexOf(like) != -1
						|| article.getAAuthor().indexOf(like) != -1)
					datas.add(article);
		}
		return datas;
	}

	public ArrayList<Article> getBookData(String like) {
		ArrayList<Article> datas = new ArrayList<Article>();
		Article article;
		for (int i = 0; i < bookData.size(); i++) {
			article = bookData.get(i);
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
		case Configuration.TYPE_VOICE:
			bookAdapter.upType(getVoiceData(query));
			break;
		case Configuration.TYPE_VEDIO:
			bookAdapter.upType(getVedioData(query));
			break;
		case Configuration.TYPE_BOOK:
			bookAdapter.upType(getBookData(query));
			break;

		}
	}

	@Override
	public void End() {
		upData();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
			long arg3) {
		/* 1表示视频，2音频 3文字 默认为 0 表示 推荐首页* */
		Article item = bookAdapter.getItem(postion);
		Intent Intent = new Intent();
		Class<? extends Activity> activity = VedioInfoAct.class;
		if (item.getMedia() == Configuration.TYPE_VEDIO) {
			activity = VedioInfoAct.class;
		} else if (item.getMedia() == Configuration.TYPE_VOICE) {
			activity = VoiceInfoAct.class;
			Intent.putExtra("page", VoiceInfoAct.PAGE_DATA);
		} else if (item.getMedia() == Configuration.TYPE_BOOK) {
			activity = BookInfoAct.class;
		}
		Intent.putExtra("data", JsonUtils.toJson(item));
		LoadingTool.launchActivity(act, activity, Intent);
	}
}
