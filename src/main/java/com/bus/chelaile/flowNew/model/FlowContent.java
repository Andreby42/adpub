/**
 * @author quekunkun
 *
 */
package com.bus.chelaile.flowNew.model;

public class FlowContent {
	private int destType; // 跳转目的地类型, 0：话题标签页，1：活动，2：文章，3：能量商城
	private String flowtitle;
	private String flowTag;
	private String flowIcon;
	private String flowTagColor;
	private String tag;
	private String tagId;
	private String channelLink;
	private String activityLink;
	private String duibaLink;

	public FlowContent() {
		super();
	}

	public FlowContent(int destType, String flowtitle, String flowTag, String flowIcon, String flowTagColor,
			String tag, String tagId, String channelLink, String activityLink, String duibaLink) {
		super();
		this.destType = destType;
		this.flowtitle = flowtitle;
		this.flowTag = flowTag;
		this.flowIcon = flowIcon;
		this.flowTagColor = flowTagColor;
		this.tag = tag;
		this.tagId = tagId;
		this.channelLink = channelLink;
		this.activityLink = activityLink;
		this.duibaLink = duibaLink;
	}

	public int getDestType() {
		return destType;
	}

	public void setDestType(int destType) {
		this.destType = destType;
	}

	public String getFlowtitle() {
		return flowtitle;
	}

	public void setFlowtitle(String flowtitle) {
		this.flowtitle = flowtitle;
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

	public String getChannelLink() {
		return channelLink;
	}

	public void setChannelLink(String channelLink) {
		this.channelLink = channelLink;
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

}