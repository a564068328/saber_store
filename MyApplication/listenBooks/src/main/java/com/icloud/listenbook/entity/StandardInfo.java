package com.icloud.listenbook.entity;


public class StandardInfo {

	public String str;
	public int type;
	public StandardInfo() {
		// TODO Auto-generated constructor stub
	}
	public StandardInfo(String str, int type) {
		super();
		this.str = str;
		this.type = type;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

}
