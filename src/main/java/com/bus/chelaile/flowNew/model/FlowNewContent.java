/**
 * @author quekunkun
 *
 */
package com.bus.chelaile.flowNew.model;

import java.util.HashMap;

import com.bus.chelaile.flow.model.ActivityContent;
import com.bus.chelaile.flow.model.ActivityEntity;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.New;

public class FlowNewContent {
	private String id;
	private int destType; // 跳转目的地类型, 0：话题标签页，1：活动，2：文章，3：能量商城，4：能量倌首页
	private String flowTitle;
	private String flowTag;
	private String flowIcon;
	private String flowTagColor;
	private String tag;
	private String tagId;
	private String articleUrl;	//文章链接
	private String activityLink;
	private String duibaLink;
	private ActivityEntity activityEntity;
	private String flowDesc;
	private String pic; // 图片
	private String link; // 链接
	private String feedId; // 话题id

	public FlowNewContent() {
		super();
	}

	public FlowNewContent(int destType, String flowTitle, String flowTag, String flowIcon, String flowTagColor,
			String tag, String tagId, String articleUrl, String activityLink, String duibaLink) {
		super();
		this.destType = destType;
		this.flowTitle = flowTitle;
		this.flowTag = flowTag;
		this.flowIcon = flowIcon;
		this.flowTagColor = flowTagColor;
		this.tag = tag;
		this.tagId = tagId;
		this.setArticleUrl(articleUrl);
		this.activityLink = activityLink;
		this.duibaLink = duibaLink;
	}
	
	public void deal(FlowNewContent flowContent, AdvParam param) {
		this.destType = flowContent.getDestType();
		this.flowTitle = flowContent.getFlowTitle();
		this.flowTag = flowContent.getFlowTag();
		this.flowIcon = flowContent.getFlowIcon();
		this.flowTagColor = flowContent.getFlowTagColor();
		this.tag = flowContent.getTag();
		this.tagId = flowContent.getTagId();

		this.link = flowContent.getLink();
		this.pic = flowContent.getPic();
		this.flowDesc = flowContent.getFlowDesc();
		this.activityEntity = flowContent.getActivityEntity();
		this.duibaLink = flowContent.getDuibaLink();
		this.feedId = flowContent.getFeedId();
		
//		HashMap<String, String> paramsMap = New.hashMap();
//		paramsMap.put("udid", param.getUdid());	//放在最外层的参数，未编码进link参数里面，如果跳转是ad，最终页面会丢失这个参数
//		paramsMap.put("gse", "1");		
//		paramsMap.put("wse", "1");	
//		this.articleUrl = AdvUtil.buildRedirectLink(flowContent.getArticleUrl(), paramsMap, param.getUdid(), null,
//				null, false, true, 0);	//参数1，决定使用redirect； 0，决定使用ad);
	}
	
	
	/*
	 * 填充活动页内容
	 * 返回值表明 是否填充成功
	 */
	public boolean fillActivityInfo(ActivityContent activityContent) {
		if (activityContent == null) {
			return false;
		}
		this.activityEntity = new ActivityEntity();
		
		this.destType = 1; // 信息流是活动的时候，type是1
		this.flowTitle = activityContent.getTitle();
		this.id = activityContent.getActivity_id() + "";
		this.pic = activityContent.getPic();
		this.link = activityContent.getLink();
		this.flowTagColor = activityContent.getTag_color();

		this.activityEntity.setType(activityContent.getType());
		this.activityEntity.setImageUrl(activityContent.getPic());
		this.activityEntity.setLinkUrl(activityContent.getLink());
		this.activityEntity.setTagId(activityContent.getTag_id());
		this.activityEntity.setTag(activityContent.getTag_name());
		this.activityEntity.setFeedId(activityContent.getFeed_id());

		return true;
	}
	
	
	public int getDestType() {
		return destType;
	}

	public void setDestType(int destType) {
		this.destType = destType;
	}

	public String getFlowTag() {
		return flowTag;
	}

	public void setFlowTag(String flowTag) {
		this.flowTag = flowTag;
	}

	public String getFlowIcon() {
		return flowIcon;
	}

	public void setFlowIcon(String flowIcon) {
		this.flowIcon = flowIcon;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}


	public String getActivityLink() {
		return activityLink;
	}

	public void setActivityLink(String activityLink) {
		this.activityLink = activityLink;
	}

	public String getDuibaLink() {
		return duibaLink;
	}

	public void setDuibaLink(String duibaLink) {
		this.duibaLink = duibaLink;
	}

	public String getFlowTagColor() {
		return flowTagColor;
	}

	public void setFlowTagColor(String flowTagColor) {
		this.flowTagColor = flowTagColor;
	}

	public String getFlowTitle() {
		return flowTitle;
	}

	public void setFlowTitle(String flowTitle) {
		this.flowTitle = flowTitle;
	}

	public String getArticleUrl() {
		return articleUrl;
	}

	public void setArticleUrl(String articleUrl) {
		this.articleUrl = articleUrl;
	}

	public String getFlowDesc() {
		return flowDesc;
	}

	public void setFlowDesc(String flowDesc) {
		this.flowDesc = flowDesc;
	}

	public ActivityEntity getActivityEntity() {
		return activityEntity;
	}

	public void setActivityEntity(ActivityEntity activityEntity) {
		this.activityEntity = activityEntity;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
}