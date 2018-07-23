package com.bus.chelaile.model.ads.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.model.ShowType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GuideAdEntity extends BaseAdEntity {

	private String pic = EMPTY_STR; // 图片URL
	private String wxMiniProId; // 小程序appId
	private String wxMiniProPath;
	
	private int adType;
	private String title;
	private String iconUrl;
	private String linkUrl;
	private int adserving;
	private long redPointTime;
	@JSONField(serialize = false)
	private int groupId;        // 分组id，  “我的”页面的广告独有
	private String leadContent; // 描述语， “我的”页面的广告独有
	@JSONField(serialize=false)
	private int site;           // 位置 0 首页； 2  我的页面

	public GuideAdEntity() {
		super(ShowType.GUIDE_ADV.getValue());
		this.pic = EMPTY_STR;
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.GUIDE_ADV;
	}
}
