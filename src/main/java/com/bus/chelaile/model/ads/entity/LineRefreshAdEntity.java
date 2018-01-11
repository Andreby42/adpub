package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;

public class LineRefreshAdEntity extends SimpleAdEntity{
	private int duration;

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public LineRefreshAdEntity(int duration) {
		this.showType = ShowType.LINEDETAIL_REFRESH_ADV.getValue();
		this.duration = duration;
	}
	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.LINEDETAIL_REFRESH_ADV;
	}
}
