package com.listenBook.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ARTICLE.
 */
public class Article {

	private long aId;
	private long cId;
	/** Not-null value. */
	private String aIcon;
	/** Not-null value. */
	private String aName;
	/** Not-null value. */
	private String aAbstract;
	/** Not-null value. */
	private String aAuthor;
	private float price;
	private int chapterNum;
	private String aDesc;
	private Float v1Price;
	private Float v2Price;
	private Float v3Price;
	private Float v4Price;
	private Float otherPrice;
	private Float salePrice;
	private int status;
	private Integer clickConut;
	/** Not-null value. */
	private String subCaterory;
	/** Not-null value. */
	private String broadAuthor;
	/** Not-null value. */
	private String dateline;
	private Integer media;

	public Article() {
	}

	public Article(long aId) {
		this.aId = aId;
		
	}
    
	public Article(long aId, long cId, String aIcon, String aName,
			String aAbstract, String aAuthor, float price, int chapterNum,
			String aDesc, Float v1Price, Float v2Price, Float v3Price,
			Float v4Price, Float otherPrice, Float salePrice, int status,
			Integer clickConut, String subCaterory, String broadAuthor,
			String dateline, Integer media) {
		this.aId = aId;
		this.cId = cId;
		this.aIcon = aIcon;
		this.aName = aName;
		this.aAbstract = aAbstract;
		this.aAuthor = aAuthor;
		this.price = price;
		this.chapterNum = chapterNum;
		this.aDesc = aDesc;
		this.v1Price = v1Price;
		this.v2Price = v2Price;
		this.v3Price = v3Price;
		this.v4Price = v4Price;
		this.otherPrice = otherPrice;
		this.salePrice = salePrice;
		this.status = status;
		this.clickConut = clickConut;
		this.subCaterory = subCaterory;
		this.broadAuthor = broadAuthor;
		this.dateline = dateline;
		this.media = media;
	}

	public long getAId() {
		return aId;
	}

	public void setAId(long aId) {
		this.aId = aId;
	}

	public long getCId() {
		return cId;
	}

	public void setCId(long cId) {
		this.cId = cId;
	}

	/** Not-null value. */
	public String getAIcon() {
		return aIcon;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setAIcon(String aIcon) {
		this.aIcon = aIcon;
	}

	/** Not-null value. */
	public String getAName() {
		return aName;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setAName(String aName) {
		this.aName = aName;
	}

	/** Not-null value. */
	public String getAAbstract() {
		return aAbstract;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setAAbstract(String aAbstract) {
		this.aAbstract = aAbstract;
	}

	/** Not-null value. */
	public String getAAuthor() {
		return aAuthor;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setAAuthor(String aAuthor) {
		this.aAuthor = aAuthor;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getChapterNum() {
		return chapterNum;
	}

	public void setChapterNum(int chapterNum) {
		this.chapterNum = chapterNum;
	}

	public String getADesc() {
		return aDesc;
	}

	public void setADesc(String aDesc) {
		this.aDesc = aDesc;
	}

	public Float getV1Price() {
		return v1Price;
	}

	public void setV1Price(Float v1Price) {
		this.v1Price = v1Price;
	}

	public Float getV2Price() {
		return v2Price;
	}

	public void setV2Price(Float v2Price) {
		this.v2Price = v2Price;
	}

	public Float getV3Price() {
		return v3Price;
	}

	public void setV3Price(Float v3Price) {
		this.v3Price = v3Price;
	}

	public Float getV4Price() {
		return v4Price;
	}

	public void setV4Price(Float v4Price) {
		this.v4Price = v4Price;
	}

	public Float getOtherPrice() {
		return otherPrice;
	}

	public void setOtherPrice(Float otherPrice) {
		this.otherPrice = otherPrice;
	}

	public Float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getClickConut() {
		return clickConut;
	}

	public void setClickConut(Integer clickConut) {
		this.clickConut = clickConut;
	}

	/** Not-null value. */
	public String getSubCaterory() {
		return subCaterory;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setSubCaterory(String subCaterory) {
		this.subCaterory = subCaterory;
	}

	/** Not-null value. */
	public String getBroadAuthor() {
		return broadAuthor;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setBroadAuthor(String broadAuthor) {
		this.broadAuthor = broadAuthor;
	}

	/** Not-null value. */
	public String getDateline() {
		return dateline;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public Integer getMedia() {
		return media;
	}

	public void setMedia(Integer media) {
		this.media = media;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Article) {
			Article t = (Article) o;
			return this.aId == t.aId;
		}
		return super.equals(o);
	}

	@Override
	public String toString() {
		return "Article [aId=" + aId + ", cId=" + cId + ", aIcon=" + aIcon
				+ ", aName=" + aName + ", aAbstract=" + aAbstract
				+ ", aAuthor=" + aAuthor + ", price=" + price + ", chapterNum="
				+ chapterNum + ", aDesc=" + aDesc + ", v1Price=" + v1Price
				+ ", v2Price=" + v2Price + ", v3Price=" + v3Price
				+ ", v4Price=" + v4Price + ", otherPrice=" + otherPrice
				+ ", salePrice=" + salePrice + ", status=" + status
				+ ", clickConut=" + clickConut + ", subCaterory=" + subCaterory
				+ ", broadAuthor=" + broadAuthor + ", dateline=" + dateline
				+ ", media=" + media + "]";
	}
}