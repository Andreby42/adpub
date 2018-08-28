package com.bus.chelaile.model.ads;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.util.New;

public class AdWXBannerInnerContent extends AdInnerContent {
	
	private String pic; // 广告图片的URL
	private String wx_miniPro_id; 
	private String wx_miniPro_path; 
	private String servingPlace;	 // 投放 小程序范围， 逗号分割的字符串，多个
	private List<String> servingPlaceList; // 投放的小程序范围，list
	private int site; // 位置， 0 首页； 1 详情页
	
	
	@Override
	protected void parseJson(String jsonr) {
		AdWXBannerInnerContent ad = null;
		ad = JSON.parseObject(jsonr, AdWXBannerInnerContent.class);
		if (ad != null) {
			this.pic = ad.pic;
			if(ad.pic != null && ad.pic.contains("#") && ad.pic.contains(",")) {
				this.pic = ad.pic.split("#")[0];
			}
			
			this.site = ad.site;
			if(StringUtils.isNotEmpty(ad.getServingPlace())) {
				this.servingPlaceList = New.arrayList();
				for(String s : ad.getServingPlace().split(",")) {
					this.servingPlaceList.add(s);
				}
			}
			
			setCommentContext(ad, this.pic, null);
		}
	}
	
	
	
	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getWx_miniPro_id() {
		return wx_miniPro_id;
	}

	public void setWx_miniPro_id(String wx_miniPro_id) {
		this.wx_miniPro_id = wx_miniPro_id;
	}

	public String getWx_miniPro_path() {
		return wx_miniPro_path;
	}

	public void setWx_miniPro_path(String wx_miniPro_path) {
		this.wx_miniPro_path = wx_miniPro_path;
	}

	public String getServingPlace() {
		return servingPlace;
	}

	public void setServingPlace(String servingPlace) {
		this.servingPlace = servingPlace;
	}

	public List<String> getServingPlaceList() {
		return servingPlaceList;
	}

	public void setServingPlaceList(List<String> servingPlaceList) {
		this.servingPlaceList = servingPlaceList;
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



    /**
     * @return the site
     */
    public int getSite() {
        return site;
    }



    /**
     * @param site the site to set
     */
    public void setSite(int site) {
        this.site = site;
    }

}
