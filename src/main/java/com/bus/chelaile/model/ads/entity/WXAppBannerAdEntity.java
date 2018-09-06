package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;

public class WXAppBannerAdEntity extends BaseAdEntity {

	private String pic = EMPTY_STR; // 图片URL
	private String wxMiniProId; // 小程序appId
	private String wxMiniProPath;
	private String title; // 标题

	public WXAppBannerAdEntity(ShowType showType) {
		super(showType.getValue());
		this.pic = EMPTY_STR;
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.WECHATAPP_BANNER_ADV;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
