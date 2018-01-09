package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;

public class LineRefreshAdEntity extends BaseAdEntity {
	private String pic;

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	// 构造方法
	public LineRefreshAdEntity() {
		super(ShowType.LINEDETAIL_REFRESH_ADV.getValue());
		this.pic = EMPTY_STR;
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.LINEDETAIL_REFRESH_ADV;
	}
}
