package com.icloud.listenbook.ui.adapter.entity;

import com.icloud.wrzjh.base.utils.down.DownloadManager;
import com.listenBook.greendao.ArticleChapterInfo;
import com.listenBook.greendao.Down;

public class ArticleChapterItem extends ArticleChapterInfo {

	/** 状态 */
	public int status;
	/***
	 * 进度 0-100
	 * */
	public int pos;
	/*** 下载ID */
	public long did;

	public int viewType=0;
	
	public ArticleChapterItem() {
		reset();
	}

	public void init(ArticleChapterInfo item) {
		this.setAid(item.getAid());
		this.setCpDesc(item.getCpDesc());
		this.setCpIcon(item.getCpIcon());
		this.setCpId(item.getCpId());
		this.setCpName(item.getCpName());
		this.setCpSize(item.getCpSize());
		this.setCpUrl(item.getCpUrl());
		this.setDateline(item.getDateline());
		this.setMCurrency(item.getMCurrency());
		reset();
	}

	public void reset() {
		pos = 0;
		did = -1;
		status = -1;
	}

	public void upDown(Down down, int[] status) {
		did = down.getDId();
		this.status = status[2];
		pos = com.icloud.wrzjh.base.utils.TextUtils.getProgressValue(status[1],
				status[0]);
	}

	public ArticleChapterItem(ArticleChapterInfo item) {
		init(item);
	}

}