package com.icloud.listenbook.entity;


import android.R.integer;

import com.listenBook.greendao.ArticleChapterRecode;

public class DeductCurrencyInfo extends ArticleChapterRecode{
	public static final int TYPE_ABSTRACT = 1;
	public static final int TYPE_TITLE = TYPE_ABSTRACT + 1;
	public static final int TYPE_ITEM = TYPE_TITLE + 1;
	public static final int STATUS_NO_BUY=1;
	public static final int STATUS_YET_BUY=STATUS_NO_BUY+1;
	public static final int STATUS_NO_WATCH=STATUS_YET_BUY+1;
	public static final int STATUS_YET_WATCH=STATUS_NO_WATCH+1;
	public String title;
	public String name;
	public int type;
	public int pos;
	
	public DeductCurrencyInfo(String title, String name, int type) {
		super();
		this.title = title;
		this.name = name;
		this.type = type;
	}

	public DeductCurrencyInfo() {
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
