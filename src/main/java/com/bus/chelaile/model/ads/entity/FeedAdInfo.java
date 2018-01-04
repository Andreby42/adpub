package com.bus.chelaile.model.ads.entity;

public class FeedAdInfo {
	private String title;
	private long time;
	private String slogan;
	private String icon;
	private int likeNum;
	private String tag;
	private int isLike;
	private int isSetTop;

	public FeedAdInfo() {
		super();
	}

	public FeedAdInfo(String title, long time, String slogan, String icon, int likeNum, String tag, int isSetTop) {
		super();
		this.title = title;
		this.setTime(time);
		this.slogan = slogan;
		this.icon = icon;
		this.setLikeNum(likeNum);
		this.tag = tag;
		this.isSetTop = isSetTop;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getIsLike() {
		return isLike;
	}

	public void setIsLike(int isLike) {
		this.isLike = isLike;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
	}

	public int getIsSetTop() {
		return isSetTop;
	}

	public void setIsSetTop(int isSetTop) {
		this.isSetTop = isSetTop;
	}
}