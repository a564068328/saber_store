package com.icloud.listenbook.entity;

public class TabItemInfo {
	public long id;
	public String name;
	public int viewType;
	public String icon;
	public long type;
    /*
     * @id
     * 
     */
	public TabItemInfo(long id, String name, String icon, long type, int viewType) {
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.type = type;
		this.viewType = viewType;
	}

}
