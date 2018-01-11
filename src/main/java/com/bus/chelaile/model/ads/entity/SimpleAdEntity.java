package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;

public class SimpleAdEntity extends BaseAdEntity {
	private String pic;

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
}
