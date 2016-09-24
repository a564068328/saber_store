package com.icloud.listenbook.ui.adapter.entity;

import com.listenBook.greendao.ChipFeedback;
import com.listenBook.greendao.Feedback;

public class FeedbackItem extends ChipFeedback {
	public int viewType;
	public boolean isShowEdit = false;
	public FeedbackItem(Feedback feedback) {
		this.setDateline(feedback.getDateline());
		this.setFid(feedback.getFid());
		this.setMsg(feedback.getMsg());
	}

	public FeedbackItem(ChipFeedback chipFeedback) {
		this.setDateline(chipFeedback.getDateline());
		this.setFid(chipFeedback.getFid());
		this.setMsg(chipFeedback.getMsg());
		this.setUid(chipFeedback.getUid());
	}
}
