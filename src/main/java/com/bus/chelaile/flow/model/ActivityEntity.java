package com.bus.chelaile.flow.model;

import com.bus.chelaile.flow.model.chatRoom.TopicInfo;

public class ActivityEntity {

//	private int id;
	private int type;
//	private String title;
	private String imageUrl;
	private String linkUrl;
	private int tagId;
	private String tag;
	private String feedId;		//feed id，string类型
	private int chatRoomId;
	private int total;  //在线聊天人数
	private TopicInfo topic;
	private int openType;// 打开方式
	
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	
	public String getFeedId() {
		return feedId;
	}
	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
	public int getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(int chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public TopicInfo getTopic() {
		return topic;
	}
	public void setTopic(TopicInfo topic) {
		this.topic = topic;
	}
	@Override
	public String toString() {
		return "ActivityEntity [type=" + type + ", imageUrl=" + imageUrl + ", linkUrl=" + linkUrl + ", tagId=" + tagId
				+ ", tag=" + tag + ", feedId=" + feedId + ", chatRoomId=" + chatRoomId + ", total=" + total
				+ ", topic=" + topic + "]";
	}
	public int getOpenType() {
		return openType;
	}
	public void setOpenType(int openType) {
		this.openType = openType;
	}
}
