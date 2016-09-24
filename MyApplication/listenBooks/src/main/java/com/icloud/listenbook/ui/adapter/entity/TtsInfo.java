package com.icloud.listenbook.ui.adapter.entity;

public class TtsInfo {
	public static final int dateType=1;
	public static final int bodyType=dateType+1;
    public String body;
    public String title;
	public TtsInfo(String title, String body) {
		this.title = title;
		this.body = body;
	}
}
