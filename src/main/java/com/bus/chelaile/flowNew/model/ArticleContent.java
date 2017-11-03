package com.bus.chelaile.flowNew.model;

import java.util.HashMap;

import com.bus.chelaile.flowNew.FlowStaticContents;
import com.bus.chelaile.flowNew.qingmang.model.Video;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.New;

public class ArticleContent {

	private int type;	// 0:无图模式，1:有图模式
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
	private long publishTimestamp;
	private ReadEntity readList;

	public ArticleContent() {
		super();
	}

	public ArticleContent(int type, String pic, String link, String source, Video video, String music,
			String channelId, String channelIcon, String articleId, String title, String desc, ReadEntity readList) {
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
		this.readList = readList;
	}
	
	/*
	 * 从缓存中获取对象，构造返回给客户端的myAr
	 */
	public void creatMyArticle(String channelId, ArticleContent ar, AdvParam param) {
		// 文章被哪些人阅读过
		// 这个过程是动态辩护的
		this.setArticleId(ar.getArticleId());
		this.setType(ar.getType());
		this.setPic(ar.getPic());
		this.setChannelIcon(ar.getChannelIcon());
		this.setDesc(ar.getDesc());
		this.setPublishTimestamp(ar.getPublishTimestamp());
		this.setSource(ar.getSource());
		this.setTitle(ar.getTitle());
		this.setVideo(ar.getVideo());
		this.setMusic(ar.getMusic());
		
		ReadEntity read = new ReadEntity();
		read.setReadNumber(FlowStaticContents.getReadArticleNum(channelId, this.getArticleId()));
		read.setReadPics(FlowStaticContents.getRandomFakePics());
		this.setReadList(read);		// 用户头像有部分是‘无法显示’
		
		HashMap<String, String> paramsMap = New.hashMap();
		paramsMap.put("udid", param.getUdid());	//放在最外层的参数，未编码进link参数里面，如果跳转是ad，最终页面会丢失这个参数
		paramsMap.put("gse", "1");		
		paramsMap.put("wse", "1");	
		
		this.setLink(AdvUtil.buildRedirectLink(ar.getLink(), paramsMap, param.getUdid(), null,
				null, false, true, 0));	//参数1，决定使用redirect； 0，决定使用ad);
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

	public ReadEntity getReadList() {
		return readList;
	}

	public void setReadList(ReadEntity readList) {
		this.readList = readList;
	}

	public long getPublishTimestamp() {
		return publishTimestamp;
	}

	public void setPublishTimestamp(long publishTimestamp) {
		this.publishTimestamp = publishTimestamp;
	}

}
