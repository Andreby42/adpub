package com.bus.chelaile.model.ads;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.util.New;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AdGuideInnerContent extends AdInnerContent {
	
	private String pic; // 广告图片的URL
	private String wx_miniPro_id; 
	private String wx_miniPro_path; 
	private String servingPlace;	 // 投放 小程序范围， 逗号分割的字符串，多个
	private List<String> servingPlaceList; // 投放的小程序范围，list
	private int site; // 位置， 0 首页； 2 我的页面
	
	private int adserving = 0;
	private int adType;
	private int redPoint; // 是否展示红点 , 0 不展示， 1 展示
	private long redPointTime;
	private String desc;
	private int groupId;
	private int sort; // 排序
	
	
	@Override
	protected void parseJson(String jsonr) {
		AdGuideInnerContent ad = null;
		ad = JSON.parseObject(jsonr, AdGuideInnerContent.class);
		if (ad != null) {
			this.pic = ad.pic;
			if(ad.pic != null && ad.pic.contains("#") && ad.pic.contains(",")) {
				this.pic = ad.pic.split("#")[0];
			}
			
			this.wx_miniPro_id = ad.getWx_miniPro_id();
			this.wx_miniPro_path = ad.getWx_miniPro_path();
			if(this.wx_miniPro_id != null)
                this.wx_miniPro_id = this.wx_miniPro_id.trim();
            if(this.wx_miniPro_path != null)
                this.wx_miniPro_path = this.wx_miniPro_path.trim();
			this.site = ad.site;
			this.adType = ad.adType;
			this.redPoint = ad.redPoint;
			this.redPointTime = ad.redPointTime;
			this.adserving = ad.adserving;
			this.desc = ad.desc;
			this.groupId = ad.groupId;
			this.sort = ad.sort;
			if(StringUtils.isNotEmpty(ad.getServingPlace())) {
				this.servingPlaceList = New.arrayList();
				for(String s : ad.getServingPlace().split(",")) {
					this.servingPlaceList.add(s);
				}
			}
			
			setCommentContext(ad, this.pic);
		}
	}
	
	
	

	
	@Override
	public void completePicUrl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String extractFullPicUrl(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractAudiosUrl(String s, int type) {
		// TODO Auto-generated method stub
		return null;
	}
}
