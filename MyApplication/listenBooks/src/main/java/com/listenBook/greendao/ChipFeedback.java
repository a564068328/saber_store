package com.listenBook.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CHIP_FEEDBACK.
 */
public class ChipFeedback {

	private Long id;
	private long fid;
	private long uid;
	private String dateline;
	private String msg;

	public ChipFeedback() {
	}

	public ChipFeedback(Long id) {
		this.id = id;
	}

	public ChipFeedback(Long id, long fid, long uid, String dateline, String msg) {
		this.id = id;
		this.fid = fid;
		this.uid = uid;
		this.dateline = dateline;
		this.msg = msg;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getFid() {
		return fid;
	}

	public void setFid(long fid) {
		this.fid = fid;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ChipFeedback) {
			ChipFeedback t = (ChipFeedback) o;
			return this.id == t.id;
		}
		return super.equals(o);
	}
}
