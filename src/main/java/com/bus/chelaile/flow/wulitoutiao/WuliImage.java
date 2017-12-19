package com.bus.chelaile.flow.wulitoutiao;

public class WuliImage {
	private String url;
	private String gifStatus;
	private String src;
	private int width;
	private int height;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getGifStatus() {
		return gifStatus;
	}
	@Override
	public String toString() {
		return "WuliImage [url=" + url + ", gifStatus=" + gifStatus + ", src=" + src + ", width=" + width + ", height="
				+ height + "]";
	}
	public void setGifStatus(String gifStatus) {
		this.gifStatus = gifStatus;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
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
