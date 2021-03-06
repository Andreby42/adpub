package com.bus.chelaile.flowNew.qingmang.model;

public class Video {

	private String url;
	private double duration;
	private String width;
	private String height;

	@Override
	public String toString() {
		return "Video [url=" + url + ", duration=" + duration + ", width=" + width + ", height=" + height + "]";
	}

	public Video() {
		super();
	}

	public Video(String url, double duration, String width, String height) {
		super();
		this.url = url;
		this.duration = duration;
		this.width = width;
		this.height = height;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

}
