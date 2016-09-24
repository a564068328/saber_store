package com.icloud.listenbook.entity;

import com.listenBook.greendao.Article;
import com.listenBook.greendao.ReadingTrack;

public class ReadingTrackItem extends Article {
	private Long time;
	private Integer pos;

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public ReadingTrackItem(Article article, ReadingTrack track) {
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
		time = track.getTime();
		pos = track.getPos();

	}
}
