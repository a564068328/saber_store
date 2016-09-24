package com.listenBook.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table MEDIA.
 */
public class Media {

    private long id;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String icon;

    public Media() {
    }

    public Media(long id) {
        this.id = id;
    }

    public Media(long id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    public String getIcon() {
        return icon;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setIcon(String icon) {
        this.icon = icon;
    }
    @Override
   	public boolean equals(Object o) {
   		if (o instanceof Media) {
   			Media t = (Media) o;
   			return this.id == t.id;
   		}
   		return super.equals(o);
   	}
}