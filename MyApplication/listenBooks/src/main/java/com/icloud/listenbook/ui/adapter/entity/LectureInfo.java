package com.icloud.listenbook.ui.adapter.entity;


import android.text.TextUtils;

public class LectureInfo implements Comparable<LectureInfo> {
	public String title;
	public int id;
	public String icon;
	public String time;
	public boolean isstart;
	public String topic;
	public String intro;
	public int pptid;
	public int roomid;
	public String uri;

	@Override
	public int compareTo(LectureInfo another) {
		int flag = 0;
		int flag1 = 0;
		int flag2 =0;
		int thisstart=0;
		int anotherstart=0;
		if(this.isstart){
			thisstart=1;
		}
		if(another.isstart){
			anotherstart=1;
		}
		if (TextUtils.isEmpty(this.uri)) {
			flag = TextUtils.isEmpty(another.uri) ? 0 : 1;
			flag1=flag == 0 ?anotherstart-thisstart:flag;
			flag2 = flag1 == 0 ? -1 : flag1;
			return flag2;
		} else {
			flag = TextUtils.isEmpty(another.uri) ? -1 : 0;
			flag1=flag == 0 ?anotherstart-thisstart:flag;
			flag2 = flag1 == 0 ? -1 : flag1;
			return flag2;
		}
	}

	@Override
	public String toString() {
		return "LectureInfo [title=" + title + ", id=" + id + ", icon=" + icon
				+ ", time=" + time + ", isstart=" + isstart + ", topic="
				+ topic + ", intro=" + intro + ", pptid=" + pptid + ", roomid="
				+ roomid + ", uri=" + uri + "]";
	}
}
