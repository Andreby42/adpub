package com.bus.chelaile.flow.wangyiyun;

import java.util.List;

public class WangYiYunChannelDto {
	
	List<WangYiYunChannel> channels;
		
	
public List<WangYiYunChannel> getChannels() {
		return channels;
	}


	public void setChannels(List<WangYiYunChannel> channels) {
		this.channels = channels;
	}


public static class WangYiYunChannel{
	private String channelId;
	private String channelName;
	private Integer channelOrder;
	private Integer channelType;
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public Integer getChannelOrder() {
		return channelOrder;
	}
	public void setChannelOrder(Integer channelOrder) {
		this.channelOrder = channelOrder;
	}
	public Integer getChannelType() {
		return channelType;
	}
	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}
}
}
