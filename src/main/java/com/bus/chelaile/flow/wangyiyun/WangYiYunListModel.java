package com.bus.chelaile.flow.wangyiyun;

import java.util.ArrayList;
import java.util.List;

import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.util.New;

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
	
	public ArrayList<com.bus.chelaile.flow.model.Thumbnail> createImgs(List<Thumbnail> imgsList) {
		List<com.bus.chelaile.flow.model.Thumbnail> pics = New.arrayList();
		if (imgsList != null && imgsList.size() > 0) {
			int count = 0;
			for (Thumbnail img : imgsList) {
				com.bus.chelaile.flow.model.Thumbnail th = new com.bus.chelaile.flow.model.Thumbnail(img.getUrl(), img.getWidth(), img.getHeight());
				pics.add(th);
				if (++count >= 3) {
					break;
				}
			}
		}
		return (ArrayList<com.bus.chelaile.flow.model.Thumbnail>) pics;
	}
	
	
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
	public String getAlgInfo() {
		return algInfo;
	}

	public void setAlgInfo(String algInfo) {
		this.algInfo = algInfo;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public Boolean getHasVideo() {
		return hasVideo;
	}

	public void setHasVideo(Boolean hasVideo) {
		this.hasVideo = hasVideo;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getRecId() {
		return recId;
	}

	public void setRecId(String recId) {
		this.recId = recId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Long getDeliverId() {
		return deliverId;
	}

	public void setDeliverId(Long deliverId) {
		this.deliverId = deliverId;
	}

	public List<Video> getVideos() {
		return videos;
	}

	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}

	public Ad getAd() {
		return ad;
	}

	public void setAd(Ad ad) {
		this.ad = ad;
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
