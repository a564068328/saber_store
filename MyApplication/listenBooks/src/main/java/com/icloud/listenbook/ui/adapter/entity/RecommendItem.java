package com.icloud.listenbook.ui.adapter.entity;

import com.listenBook.greendao.Recommend;

public class RecommendItem extends Recommend {
	public int viewType;

	public RecommendItem() {

	}

	public RecommendItem(Recommend recommend) {
		this.setAAbstract(recommend.getAAbstract());
		this.setAAuthor(recommend.getAAuthor());
		this.setAIcon(recommend.getAIcon());
		this.setAId(recommend.getAId());
		this.setChapterNum(recommend.getChapterNum());
		this.setMedia(recommend.getMedia());
		this.setAName(recommend.getAName());
	}

	public RecommendItem(int viewType) {
		this.viewType = viewType;
		this.setAAbstract("");
		this.setAAuthor("");
		this.setAIcon("");
		this.setAId(-1);
		this.setChapterNum(-1);
		this.setMedia(-1);
		this.setAName("");
	}

	
	
}
