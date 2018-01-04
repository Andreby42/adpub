package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.Tag;

public class FeedAdEntity extends BaseAdEntity {
	
	private String pic;
	private int width;
	private int height;
	private String feedId;
	private Tag tag;
	private FeedAdInfo feedInfo;

	// 构造方法
	public FeedAdEntity() {
        super(ShowType.FEED_ADV.getValue());
        this.pic = EMPTY_STR;
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.FEED_ADV;
	}

	
	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public FeedAdInfo getFeedInfo() {
		return feedInfo;
	}

	public void setFeedInfo(FeedAdInfo feedInfo) {
		this.feedInfo = feedInfo;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
