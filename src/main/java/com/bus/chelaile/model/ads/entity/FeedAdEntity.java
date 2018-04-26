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
	private FeedAdArticleInfo articleInfo;
	private int feedAdType;    // feed流广告类型，0 话题样式， 1 透视样式， 2 文章样式， 3 图片样式信息流
	private int isSetTop;	// 是否置顶
	private int imgsType; // 图片样式  

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

	public FeedAdArticleInfo getArticleInfo() {
		return articleInfo;
	}

	public void setArticleInfo(FeedAdArticleInfo articleInfo) {
		this.articleInfo = articleInfo;
	}

	public int getFeedAdType() {
		return feedAdType;
	}

	public void setFeedAdType(int feedAdType) {
		this.feedAdType = feedAdType;
	}

	public int getIsSetTop() {
		return isSetTop;
	}

	public void setIsSetTop(int isSetTop) {
		this.isSetTop = isSetTop;
	}

    public int getImgsType() {
        return imgsType;
    }

    public void setImgsType(int imgsType) {
        this.imgsType = imgsType;
    }

}
