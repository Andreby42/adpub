package com.bus.chelaile.flow.model;

import java.util.List;

public class ArticleInfo {

	private String title;
	private String author;
	private String desc;
	private String content;
	private String imgRtio;		//图片比例，所有图片组合起来的一个数组
	private String shareDesc;	//分享描述,去content前面的文字，不超过50字
	
	private String url;	//文章链接，用户构建‘推荐相关文章’处
	private List<String> imgUrl; //缩略图片
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "ArticleInfo [title=" + title + ", author=" + author + ", desc=" + desc + ", content=" + content + "]";
	}
	public String getImgRtio() {
		return imgRtio;
	}
	public void setImgRtio(String imgRtio) {
		this.imgRtio = imgRtio;
	}
	public String getShareDesc() {
		return shareDesc;
	}
	public void setShareDesc(String shareDesc) {
		this.shareDesc = shareDesc;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(List<String> imgUrl) {
		this.imgUrl = imgUrl;
	}
}
