package com.icloud.listenbook.entity;

import java.io.Serializable;

public class ResultItemInfo {

	public boolean gong;
	public boolean guo;
	public String masterTextView;
	public String event;
	public String except;
	public ResultItemInfo(boolean gong, boolean guo, String masterTextView,
			String event, String except) {
		super();
		this.gong = gong;
		this.guo = guo;
		this.masterTextView = masterTextView;
		this.event = event;
		this.except = except;
	}
	
	
}
