package com.icloud.listenbook.http;

import java.io.Serializable;

/**
 * 下载对象
 */
public class DownloadBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String url = "";
	private String saveDir = "";
	private String version_name = "";
	private String updateTxt = "";
	private int current_size;
	private int total_size;

	public int progress;// 下载进度

	public String getVersion_name() {
		return version_name;
	}

	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}

	public String getUpdateTxt() {
		return updateTxt;
	}

	public void setUpdateTxt(String updateTxt) {
		this.updateTxt = updateTxt;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}

	public int getCurrent_size() {
		return current_size;
	}

	public void setCurrent_size(int current_size) {
		this.current_size = current_size;
	}

	public int getTotal_size() {
		return total_size;
	}

	public void setTotal_size(int total_size) {
		this.total_size = total_size;
	}

}
