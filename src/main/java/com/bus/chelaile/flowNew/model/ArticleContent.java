package com.bus.chelaile.flowNew.model;

import com.bus.chelaile.flowNew.qingmang.model.Video;

public class ArticleContent {

	private int type;
	private String pic;
	private String link;
	private String source;
	private Video video;
	private String music;
	private String channelId;
	private String channelIcon;
	private String articleId;
	private String title;
	private String desc;
	private ReadEntity likeList;

	public ArticleContent() {
		super();
	}

	public ArticleContent(int type, String pic, String link, String source, Video video, String music,
			String channelId, String channelIcon, String articleId, String title, String desc, ReadEntity likeList) {
		super();
		this.type = type;
		this.pic = pic;
		this.link = link;
		this.source = source;
		this.video = video;
		this.music = music;
		this.channelId = channelId;
		this.channelIcon = channelIcon;
		this.articleId = articleId;
		this.title = title;
		this.desc = desc;
		this.likeList = likeList;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public String getMusic() {
		return music;
	}

	public void setMusic(String music) {
		this.music = music;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelIcon() {
		return channelIcon;
	}

	public void setChannelIcon(String channelIcon) {
		this.channelIcon = channelIcon;
	}

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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public ReadEntity getLikeList() {
		return likeList;
	}

	public void setLikeList(ReadEntity likeList) {
		this.likeList = likeList;
	}

}
