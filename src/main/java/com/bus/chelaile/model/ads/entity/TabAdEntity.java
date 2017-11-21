package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;


public class TabAdEntity extends BaseAdEntity {
	
	private String pic; // 图片URL
	private int activityType;
	private int tagId;
	private String tag;
	private String feedId;
//	private  聊天室
	
	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.TAB_ADV;
	}
	
	public TabAdEntity() {
		super(ShowType.TAB_ADV.getValue());
        this.pic = EMPTY_STR;
	}
	
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public int getActivityType() {
		return activityType;
	}
	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}
	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getFeedId() {
		return feedId;
	}
	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

}
