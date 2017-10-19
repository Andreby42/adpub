package com.bus.chelaile.flow.model;

public class StreamFormData {
	private String newsId;
	private int newsType;

	public StreamFormData() {
	}

	public String getNewsId() {
		return newsId;
	}

	public void setNewsId(String newsId) {
		this.newsId = newsId;
	}

	public int getNewsType() {
		return newsType;
	}

	public void setNewsType(int newsType) {
		this.newsType = newsType;
	}

	@Override
	public String toString() {
		return "StreamFormData [newsId=" + newsId + ", newsType=" + newsType + "]";
	}

	public StreamFormData(String newsId, int newsType) {
		super();
		this.newsId = newsId;
		this.newsType = newsType;
	}
	
}