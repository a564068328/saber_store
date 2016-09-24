package com.icloud.listenbook.ui.adapter.entity;

public class PinterestLikeItem {
	public String title;
	public int id;
	public String icon;
	public String time;
	public String url;
	public int viewType;
	public PinterestLikeItem() {

	}

	public PinterestLikeItem(String title,int viewType) {
       this.title=title;
       this.viewType=viewType;
	}
}
