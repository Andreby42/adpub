package com.bus.chelaile.model.ads;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.mvc.AdvParam;

/**
 * 推送的广告的内部的内容， 就是数据之中content的结构化表示。
 * 
 * @author linzi
 * 
 */
public class AdLineRefreshOpenInnerContent extends AdInnerContent {
	private String pic; // 图片
	private String dropDownDoc; // 下拉文案
	private String freshDoc; // 刷新文案
	private String openDoc; // 打开文案
	private String bgColor; // 背景色

	@Override
	protected void parseJson(String jsonr) {
		AdLineRefreshOpenInnerContent ad = null;
		ad = JSON.parseObject(jsonr, AdLineRefreshOpenInnerContent.class);
		if (ad != null) {
			this.pic = ad.pic;
			if (ad.pic != null && ad.pic.contains("#") && ad.pic.contains(",")) {
				this.pic = ad.pic.split("#")[0];
			}
			this.dropDownDoc = ad.dropDownDoc;
			this.freshDoc = ad.freshDoc;
			this.openDoc = ad.openDoc;
			this.bgColor = ad.bgColor;
			
			setCommentContext(ad, this.pic);
		}
	}

	@Override
	public String extractFullPicUrl(String s) {
		return null;
	}

	@Override
	public String extractAudiosUrl(String s, int type) {
		return null;
	}

	public static void main(String[] args) {
		// LineRefreshInnerContent ad = new LineRefreshInnerContent();
		// ad.setAndPaseJson("{\"feedAdTitle\":\"\",\"feedAdType\":1,\"feedId\":\"642191129076404224\",\"feedTag\":\"广告\",\"icon\":\"\",\"isSetTop\":1,\"likeNum\":0,\"pic\":\"https://image3.chelaile.net.cn/cf08752ed77849afb8f29d6b6abf2f35\",\"slogan\":\"\",\"tagId\":\"\",\"time\":0}");
	}

	@Override
	public void fillAdEntity(AdEntity adEntity, AdvParam param, int stindex) {
		if (adEntity == null) {
			return;
		}
	}

	public void completePicUrl() {
		this.pic = getFullPicUrl(pic);
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getDropDownDoc() {
		return dropDownDoc;
	}

	public void setDropDownDoc(String dropDownDoc) {
		this.dropDownDoc = dropDownDoc;
	}

	public String getFreshDoc() {
		return freshDoc;
	}

	public void setFreshDoc(String freshDoc) {
		this.freshDoc = freshDoc;
	}

	public String getOpenDoc() {
		return openDoc;
	}

	public void setOpenDoc(String openDoc) {
		this.openDoc = openDoc;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

}
