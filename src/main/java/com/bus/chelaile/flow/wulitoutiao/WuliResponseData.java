/**
 * 
 */
/**
 * @author quekunkun
 *
 */
package com.bus.chelaile.flow.wulitoutiao;

import java.util.ArrayList;
import java.util.List;

import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.util.New;

public class WuliResponseData {
	
	private String messageId;
	private String messageType;
	private String title;
	private String summary;
	private List<WuliImage> imageDTOList;
	private String origin;
	private List<String> tags;
	private String shareLink;
	private long publishTime;
	private long createTime;
	
	
	public FlowContent dealDate(String channelId, WuliImage oriImages) throws InterruptedException {
		FlowContent content = new FlowContent();
		content.setId(String.valueOf(this.messageId));
		content.setTitle(this.title);
		content.setTime(this.publishTime);	//此处与uc不一样，不能用发布时间
		content.setRecoid(System.currentTimeMillis() + "");	 // 用时间戳作recoid，后续存入zset时作为score
		content.setDesc(this.origin);
		content.setUrl(this.shareLink);
		
		List<Thumbnail> images = New.arrayList();
//		images.add(new Thumbnail(oriImages.getUrl().replace("http", "https").split("\\?")[0], oriImages.getWidth(), oriImages.getHeight()));
		images.add(new Thumbnail(oriImages.getUrl().split("\\?")[0].replace("http", "https"), oriImages.getWidth(), oriImages.getHeight()));
		content.setImgs((ArrayList<Thumbnail>)images);
		content.setImgsType(0);	//单图，大图
		
		Thread.sleep(2);
		return content;
	}
	
	
	
	@Override
	public String toString() {
		return "WuliResponseData [messageId=" + messageId + ", messageType=" + messageType + ", title=" + title
				+ ", summary=" + summary + ", imageDTOList=" + imageDTOList + ", origin=" + origin + ", tags=" + tags
				+ ", shareLink=" + shareLink + ", publishTime=" + publishTime + ", createTime=" + createTime + "]";
	}



	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public List<WuliImage> getImageDTOList() {
		return imageDTOList;
	}
	public void setImageDTOList(List<WuliImage> imageDTOList) {
		this.imageDTOList = imageDTOList;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getShareLink() {
		return shareLink;
	}
	public void setShareLink(String shareLink) {
		this.shareLink = shareLink;
	}
	public long getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(long publishTime) {
		this.publishTime = publishTime;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
}