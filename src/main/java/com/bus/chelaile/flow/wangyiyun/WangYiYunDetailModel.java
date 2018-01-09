package com.bus.chelaile.flow.wangyiyun;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.flow.wangyiyun.WangYiYunListModel.Ad;
import com.bus.chelaile.flowNew.qingmang.model.Video;
import com.bus.chelaile.util.New;



public class WangYiYunDetailModel {
	private String 	infoId;
	private String 	infoType;
	private String 	category;
	private String 	title;
	private String producer;
	private String 	publishTime;
	private String 	source;
	private String 	sourceLink;
	private String 	content;
	private String 	tag;
	private List<Img> imgs= new LinkedList<>();
	private List<Video> videos;
	private Ad ad;
	private AppInfo appInfo;
	private String updateTime;
	
	
	public ArrayList<Thumbnail> createImgs(List<com.bus.chelaile.flow.wangyiyun.WangYiYunListModel.Thumbnail> imgsList) {
		List<Thumbnail> pics = New.arrayList();
		if (imgsList != null && imgsList.size() > 0) {
			int count = 0;
			for (com.bus.chelaile.flow.wangyiyun.WangYiYunListModel.Thumbnail img : imgsList) {
				Thumbnail th = new Thumbnail(img.getUrl(), img.getWidth(), img.getHeight());
				pics.add(th);
				if (++count >= 3) {
					break;
				}
			}
		}
		return (ArrayList<Thumbnail>) pics;
	}
	
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourceLink() {
		return sourceLink;
	}

	public void setSourceLink(String sourceLink) {
		this.sourceLink = sourceLink;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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

	public AppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Img> getImgs() {
		return imgs;
	}

	public void setImgs(List<Img> imgs) {
		this.imgs = imgs;
	}

	public static class AppInfo{
		private String appkey;
		private String name;
		private String iconUrl;
		private String iosDownUrl;
		private String androidDownUrl;
	}
	
	public static class Img{
		private String url;
		private Integer  height;
		private Integer width;
		private Integer size;
		private String type;
		private String note;
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
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
		public Integer getSize() {
			return size;
		}
		public void setSize(Integer size) {
			this.size = size;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getNote() {
			return note;
		}
		public void setNote(String note) {
			this.note = note;
		}
		
	}
}
