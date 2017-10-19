package com.bus.chelaile.model;

public enum QueueCacheType {
	ARTICLES("articles"),	//文章结构体
	DISPLAY_IDS("display_ids"),	//展示过的信息流id
	REALUSERS("real_users"),	//有效用户
	QRCODE("qrcode");	//领取过优惠券
	
	QueueCacheType(String type) {
		this.setType(type);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String type;

}
