package com.bus.chelaile.flow.model;

public class FlowChannel {
	private int id;		//自己定义的渠道id
	private String name;
	private String channelId;	//用来请求接口的渠道id，可能有多个，用&符隔开
	private String title;
	private String picUrl;
	private int channelType; //渠道类型，目前包括uc 0 、toutiao 1、自定义模块 2
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	public FlowChannel() {
		super();
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public int getChannelType() {
		return channelType;
	}
	public void setChannelType(int channelType) {
		this.channelType = channelType;
	}
	@Override
	public String toString() {
		return "UCChannel [id=" + id + ", name=" + name + ", channelId=" + channelId + ", title=" + title + ", picUrl="
				+ picUrl + ", channelType=" + channelType + "]";
	}
}
