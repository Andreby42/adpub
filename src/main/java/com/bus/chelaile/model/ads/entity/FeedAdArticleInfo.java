package com.bus.chelaile.model.ads.entity;

import java.util.List;

public class FeedAdArticleInfo {
	private String title;
	private long time;
	private String tag;
	private List<String> imgs;
	private String adName;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getTime() {
		return time;
	}
	
	public FeedAdArticleInfo(String title, long time, String tag, List<String> imgs, String adName) {
		super();
		this.title = title;
		this.time = time;
		this.tag = tag;
		this.imgs = imgs;
		this.adName = adName;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public List<String> getImgs() {
		return imgs;
	}
	public void setImgs(List<String> imgs) {
		this.imgs = imgs;
	}
	public String getAdName() {
		return adName;
	}
	public void setAdName(String adName) {
		this.adName = adName;
	}
}
