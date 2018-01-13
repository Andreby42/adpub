package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.Tag;

public class SimpleAdEntity extends BaseAdEntity {
	private String pic;
	private Tag tag;
	private String feedId;

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	// 构造方法
	public SimpleAdEntity() {
		super(ShowType.H5_LINEBANNER_ADV.getValue());
		this.pic = EMPTY_STR;
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.H5_LINEBANNER_ADV;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
}
