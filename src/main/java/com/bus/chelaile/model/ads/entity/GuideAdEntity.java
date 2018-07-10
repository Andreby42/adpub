package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;

public class GuideAdEntity extends BaseAdEntity {

	private String pic = EMPTY_STR; // 图片URL
	private String wxMiniProId; // 小程序appId
	private String wxMiniProPath;
	
	private int adType;
	private int subType;
	private String tagId;
	private String tagName;
	private String title;
	private String iconUrl;
//	private String linkUrl;
	private int showRedDot;
	private long updateTime;
	private int site; // 位置 0 首页； 1 个人页

	public GuideAdEntity() {
		super(ShowType.GUIDE_ADV.getValue());
		this.pic = EMPTY_STR;
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.GUIDE_ADV;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getWxMiniProId() {
		return wxMiniProId;
	}

	public void setWxMiniProId(String wxMiniProId) {
		this.wxMiniProId = wxMiniProId;
	}

	public String getWxMiniProPath() {
		return wxMiniProPath;
	}

	public void setWxMiniProPath(String wxMiniProPath) {
		this.wxMiniProPath = wxMiniProPath;
	}

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

//    public String getLinkUrl() {
//        return linkUrl;
//    }
//
//    public void setLinkUrl(String linkUrl) {
//        this.linkUrl = linkUrl;
//    }

    public int getShowRedDot() {
        return showRedDot;
    }

    public void setShowRedDot(int showRedDot) {
        this.showRedDot = showRedDot;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getSite() {
        return site;
    }

    public void setSite(int site) {
        this.site = site;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
