package com.icloud.listenbook.ui.adapter.entity;

import com.listenBook.greendao.Article;

public class ArticleItem extends Article {
	public int viewType;

	public ArticleItem(Article article) {
		super();
		this.setAId(article.getAId());
		this.setCId(article.getCId());
		this.setAIcon(article.getAIcon());
		this.setAName(article.getAName());

		this.setAAbstract(article.getAAbstract());
		this.setADesc(article.getADesc());
		this.setAAuthor(article.getAAuthor());
		this.setPrice(article.getPrice());
		this.setChapterNum(article.getChapterNum());
		this.setStatus(article.getStatus());
		this.setClickConut(article.getClickConut());
		this.setMedia(article.getMedia());
	}

	public ArticleItem() {
		super();
	}

	public int getViewType() {
		return viewType;
	}

	public void setViewType(int viewType) {
		this.viewType = viewType;
	}

	public Article getArticle() {
		return this;
	}
}
