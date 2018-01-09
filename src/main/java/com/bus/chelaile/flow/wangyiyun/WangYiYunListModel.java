package com.bus.chelaile.flow.wangyiyun;

import java.util.Date;
import java.util.List;

public class WangYiYunListModel {
	private String algInfo;
	private String channelId;
	private Integer imgType;
	private String infoId;
	private String infoType;
	private Boolean hasVideo;
	private String producer;
	private String publishTime;
	private String recId;
	private String source;
	private String title;
	private String updateTime;
	private String summary;
	private Long deliverId;
	private List<Thumbnail> thumbnails;
	private List<Video> videos;
	private Ad ad;
	
	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public Integer getImgType() {
		return imgType;
	}

	public void setImgType(Integer imgType) {
		this.imgType = imgType;
	}

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public List<Thumbnail> getThumbnails() {
		return thumbnails;
	}

	public void setThumbnails(List<Thumbnail> thumbnails) {
		this.thumbnails = thumbnails;
	}

	public static class Thumbnail{
		private Integer height;
		private Integer width;
		private String url;
		public Integer getHeight() {
			return height;
		}
		public void setHeight(Integer height) {
			this.height = height;
		}
		public Integer getWidth() {
			return width;
		}
		public void setWidth(Integer width) {
			this.width = width;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	}
	
	public static class Ad{
		private String producer;
		private String mediumId;
		private String packageName;
		private String adPlacementId;
		private String ip;
	}
	
	private static class Video{
		private String cover;
		private String largeCover;
		private Integer playsize;
		private Integer duration;
		private String mp4SdUrl;
		private String mp4HdUrl;
		private String mp4ShdUrl;
		private String m3u8SdUrl;
		private String m3u8HdUrl;
		private String sdUrl;
		private String hdUrl;
		private String shdUrl;
	}
}
