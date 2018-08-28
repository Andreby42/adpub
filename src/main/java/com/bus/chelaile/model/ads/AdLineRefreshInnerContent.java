package com.bus.chelaile.model.ads;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.mvc.AdvParam;

/**
 * 广告的内部的内容， 就是数据之中content的结构化表示。
 * 
 * @author liujh
 * 
 */
public class AdLineRefreshInnerContent extends AdInnerContent {
	private String pic; // 广告图片的URL
    private String tag;	//话题标签名
    private String tagId;	//话题标签id
    private String feedId; //话题详情页id
	
	@Override
	protected void parseJson(String jsonr) {
		AdLineRefreshInnerContent ad = null;
		ad = JSON.parseObject(jsonr, AdLineRefreshInnerContent.class);
		if (ad != null) {
			this.pic = ad.pic;
			if(ad.pic != null && ad.pic.contains("#") && ad.pic.contains(",")) {
				this.pic = ad.pic.split("#")[0];
//				this.setWidth(Integer.parseInt(ad.pic.split("#")[1].split(",")[0]));
//				this.setHeight(Integer.parseInt(ad.pic.split("#")[1].split(",")[1]));
			}
			this.tagId = ad.tagId;
            this.tag = ad.tag;
            this.feedId = ad.feedId;
            
            setCommentContext(ad, this.pic, null);
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
//		LineRefreshInnerContent ad = new LineRefreshInnerContent();
//		ad.setAndPaseJson("{\"feedAdTitle\":\"\",\"feedAdType\":1,\"feedId\":\"642191129076404224\",\"feedTag\":\"广告\",\"icon\":\"\",\"isSetTop\":1,\"likeNum\":0,\"pic\":\"https://image3.chelaile.net.cn/cf08752ed77849afb8f29d6b6abf2f35\",\"slogan\":\"\",\"tagId\":\"\",\"time\":0}");
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
}
