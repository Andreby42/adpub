package com.bus.chelaile.flowNew.model;

import java.util.List;

import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.model.ads.entity.FeedAdEntity;

public class FeedContent {

	private String id;
	private int destType;
	private List<Thumbnail> imgs;
	private int imgsType;
	private String feedTitle;
	private String feedDesc;
	private long time;
	private FeedAdEntity ads;
	private String link;
	
	
	public FeedContent() {
		super();
	}
	public FeedContent(String id, int destType, List<Thumbnail> imgs, int imgsType, String feedTitle, String feedDesc, long time,
			FeedAdEntity ads) {
		super();
		this.id = id;
		this.destType = destType;
		this.setImgs(imgs);
		this.imgsType = imgsType;
		this.feedTitle = feedTitle;
		this.feedDesc = feedDesc;
		this.time = time;
		this.ads = ads;
	}
	
	public int getDestType() {
		return destType;
	}
	public void setDestType(int destType) {
		this.destType = destType;
	}
	public int getImgsType() {
		return imgsType;
	}
	public void setImgsType(int imgsType) {
		this.imgsType = imgsType;
	}
	public String getFeedTitle() {
		return feedTitle;
	}
	public void setFeedTitle(String feedTitle) {
		this.feedTitle = feedTitle;
	}
	public String getFeedDesc() {
		return feedDesc;
	}
	public void setFeedDesc(String feedDesc) {
		this.feedDesc = feedDesc;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public FeedAdEntity getAds() {
		return ads;
	}
	public void setAds(FeedAdEntity ads) {
		this.ads = ads;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Thumbnail> getImgs() {
		return imgs;
	}
	public void setImgs(List<Thumbnail> imgs) {
		this.imgs = imgs;
	}
}
