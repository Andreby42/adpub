package com.bus.chelaile.model.ads;

public class Tag {
	
	private String tag;
	private int tagId;
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	public Tag(String tag, String tagId) {
		super();
		this.tag = tag;
		this.tagId = Integer.parseInt(tagId);
	}
	public Tag() {
		super();
	}
}
