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
	private String pullText; // 下拉文案
	private String refreshText; // 刷新文案
	private String openText; // 打开文案
	private String backColor; // 背景色

	@Override
	protected void parseJson(String jsonr) {
		AdLineRefreshOpenInnerContent ad = null;
		ad = JSON.parseObject(jsonr, AdLineRefreshOpenInnerContent.class);
		if (ad != null) {
			this.pic = ad.pic;
			if (ad.pic != null && ad.pic.contains("#") && ad.pic.contains(",")) {
				this.pic = ad.pic.split("#")[0];
				// this.setWidth(Integer.parseInt(ad.pic.split("#")[1].split(",")[0]));
				// this.setHeight(Integer.parseInt(ad.pic.split("#")[1].split(",")[1]));
			}
			this.pullText = ad.pullText;
			this.refreshText = ad.refreshText;
			this.openText = ad.openText;
			this.backColor = ad.backColor;
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
}
