package com.bus.chelaile.flow.model;

import java.util.*;

public class FlowResponse {
	
	private List<FlowContent> ucArticles;
	private String ucIcon;
	private String ucTitle;
	private List<Channel> channels;
	private List<Channel> favChannels;
	private List<Channel> otherChannels;
	private int articleShowType;
	private int hasLineArticles;
	
	
	public List<FlowContent> getUcArticles() {
		return ucArticles;
	}
	public void setUcArticles(List<FlowContent> ucArticles) {
		this.ucArticles = ucArticles;
	}
	public String getUcIcon() {
		return ucIcon;
	}
	public void setUcIcon(String ucIcon) {
		this.ucIcon = ucIcon;
	}
	public String getUcTitle() {
		return ucTitle;
	}
	public void setUcTitle(String ucTitle) {
		this.ucTitle = ucTitle;
	}
	public List<Channel> getChannels() {
		return channels;
	}
	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}
	public List<Channel> getFavChannels() {
		return favChannels;
	}
	public void setFavChannels(List<Channel> favChannels) {
		this.favChannels = favChannels;
	}
	public List<Channel> getOtherChannels() {
		return otherChannels;
	}
	public void setOtherChannels(List<Channel> otherChannels) {
		this.otherChannels = otherChannels;
	}
	public int getArticleShowType() {
		return articleShowType;
	}
	public void setArticleShowType(int articleShowType) {
		this.articleShowType = articleShowType;
	}
	public int getHasLineArticles() {
		return hasLineArticles;
	}
	public void setHasLineArticles(int hasLineArticles) {
		this.hasLineArticles = hasLineArticles;
	}
	
}
