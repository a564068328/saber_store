package com.icloud.listenbook.entity;

import java.sql.Date;
import java.util.Arrays;

import android.R.integer;

public class LessonItem {
	public static final int CHOICE = 1;
	public static final int COMPLETION = CHOICE + 1;
	public static final int SUMBIT = COMPLETION + 1;
	public long id;
	public int position;
	public String title;
	public String issue;
	public String[] option;
	public int type;
	public String date;
	// 正确答案
	public String[] answer;
	// 用户答案
	public String[] user_answer;

	public String chant;

	public boolean iscomplete;
	public int marks;
	public String describe;
	public int right_count;
	@Override
	public String toString() {
		return "LessonItem [position=" + position + ", title=" + title
				+ ", issue=" + issue + ", option=" + Arrays.toString(option)
				+ ", type=" + type + ", date=" + date + ", answer="
				+ Arrays.toString(answer) + ", user_answer="
				+ Arrays.toString(user_answer) + ", chant=" + chant
				+ ", iscomplete=" + iscomplete + ", marks=" + marks
				+ ", describe=" + describe + ", right_count=" + right_count
				+ "]";
	}
	
}
