package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;

public class LineRefreshOpenAdEntity extends BaseAdEntity{
	private String pic;
	private String pullText;
	private String refreshText;
	private String openText;
	private String backColor;

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getPullText() {
		return pullText;
	}

	public void setPullText(String pullText) {
		this.pullText = pullText;
	}

	public String getRefreshText() {
		return refreshText;
	}

	public void setRefreshText(String refreshText) {
		this.refreshText = refreshText;
	}

	public String getOpenText() {
		return openText;
	}

	public void setOpenText(String openText) {
		this.openText = openText;
	}

	public String getBackColor() {
		return backColor;
	}

	public void setBackColor(String backColor) {
		this.backColor = backColor;
	}

	public LineRefreshOpenAdEntity() {
		super(ShowType.LINEDETAIL_REFRESH_OPEN_ADV.getValue());
        this.pic = EMPTY_STR;
	}
	
	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.LINEDETAIL_REFRESH_OPEN_ADV;
	}
}
