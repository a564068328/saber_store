package com.icloud.listenbook.ui.adapter.entity;

public class Type {
	public static final int TABLE_HEADER = 0;
	public static final int TABLE_TITLE = TABLE_HEADER + 1;
	public static final int TABLE_ITEM = TABLE_TITLE + 1;
	public static final int TABLE_GRID = TABLE_ITEM + 1;
	
	public static final int TABLE_GRID_LEFT = TABLE_GRID + 1;
	public static final int TABLE_GRID_RIGHT = TABLE_GRID_LEFT + 1;
	public static final int TABLE_FOOTER = TABLE_GRID_RIGHT + 1;
	public static final int TABLE_END = TABLE_FOOTER + 1;
	public static final int TABLE_BESTFOOTER = TABLE_END + 1;
	
	
	public static final int TABLE_GRID_L= TABLE_BESTFOOTER + 1;
	public static final int TABLE_GRID_M = TABLE_GRID_L+1;
	public static final int TABLE_GRID_R = TABLE_GRID_M+1;
	
	public static final int TABLE_WORD = TABLE_BESTFOOTER + 1;
	public static final int TABLE_TIME = TABLE_WORD + 1;
	public static final int TABLE_WORK = TABLE_TIME + 1;
	public static final int TABLE_GREAT = TABLE_WORK + 1;
	
	public static final int TABLE_TITLE_WORK = TABLE_GREAT+1;
	
	public static final int TABLE_TITLE_TIME = TABLE_TITLE_WORK+1;
	
	public static final int TABLE_TITLE_WORD = TABLE_TITLE_TIME+1;
	public static final int TABLE_TITLE_CORE = TABLE_TITLE_WORD+1;
	
	
}