package com.bus.chelaile.flow.model;

public class Thumbnail{	//缩略图的图片url列表
	private String url;
	private int width;
	private int height;
	private String type;	//jpg, gif.
	
	public Thumbnail() {
		super();
	}
	
	public Thumbnail(String url) {
		super();
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Thumbnail [url=" + url + ", width=" + width + ", height=" + height + ", type=" + type + "]";
	}
}