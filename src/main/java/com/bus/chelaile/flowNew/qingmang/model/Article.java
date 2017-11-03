package com.bus.chelaile.flowNew.qingmang.model;

import java.util.List;

/**
 * 轻芒文章结构体
 * 
 * @author quekunkun
 *
 */
public class Article {

	@Override
	public String toString() {
		return "Article [articleId=" + articleId + ", title=" + title + ", snippet=" + snippet + ", author=" + author
				+ ", covers=" + covers + ", images=" + images + ", videos=" + videos + ", musics=" + musics + ", tags="
				+ tags + ", templateType=" + templateType + ", contentUrl=" + contentUrl + ", contentFormat="
				+ contentFormat + ", content=" + content + "]";
	}

	private String articleId;
	private String title;
	private String snippet;
	private String author;
	private List<Video> covers;
	private List<Video> images;
	private List<Video> videos;
	private List<Music> musics;
	private List<String> tags;
	private String templateType;
	private String contentUrl;
	private String providerName;
	private long publishTimestamp;

	private String contentFormat;
	private String content;

	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public List<Video> getCovers() {
		return covers;
	}

	public void setCovers(List<Video> covers) {
		this.covers = covers;
	}

	public List<Video> getImages() {
		return images;
	}

	public void setImages(List<Video> images) {
		this.images = images;
	}

	public List<Video> getVideos() {
		return videos;
	}

	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}

	public List<Music> getMusics() {
		return musics;
	}

	public void setMusics(List<Music> musics) {
		this.musics = musics;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getContentFormat() {
		return contentFormat;
	}

	public void setContentFormat(String contentFormat) {
		this.contentFormat = contentFormat;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public long getPublishTimestamp() {
		return publishTimestamp;
	}

	public void setPublishTimestamp(long publishTimestamp) {
		this.publishTimestamp = publishTimestamp;
	}

}