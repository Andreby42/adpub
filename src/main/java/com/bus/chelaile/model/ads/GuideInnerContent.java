package com.bus.chelaile.model.ads;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.util.New;

public class GuideInnerContent extends AdInnerContent {
	
	private String pic; // 广告图片的URL
	private String wx_miniPro_id; 
	private String wx_miniPro_path; 
	private String servingPlace;	 // 投放 小程序范围， 逗号分割的字符串，多个
	private List<String> servingPlaceList; // 投放的小程序范围，list
	private int site; // 位置， 0 首页； 1 详情页
	
	private int adPosition;
	private int adType;
	private long lastUpdateTime;
	private String red_point;
	private int sub_type;
	private String desc;
	
	
	@Override
	protected void parseJson(String jsonr) {
		GuideInnerContent ad = null;
		ad = JSON.parseObject(jsonr, GuideInnerContent.class);
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
			this.adPosition = ad.adPosition;
			this.adType = ad.adType;
			this.lastUpdateTime = ad.lastUpdateTime;
			this.red_point = ad.red_point;
			this.sub_type = ad.sub_type;
			if(StringUtils.isNotEmpty(ad.getServingPlace())) {
				this.servingPlaceList = New.arrayList();
				for(String s : ad.getServingPlace().split(",")) {
					this.servingPlaceList.add(s);
				}
			}
			
			setCommentContext(ad, this.pic);
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



    public int getAdPosition() {
        return adPosition;
    }



    public void setAdPosition(int adPosition) {
        this.adPosition = adPosition;
    }



    public int getAdType() {
        return adType;
    }



    public void setAdType(int adType) {
        this.adType = adType;
    }



    public long getLastUpdateTime() {
        return lastUpdateTime;
    }



    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }



    public String getRed_point() {
        return red_point;
    }



    public void setRed_point(String red_point) {
        this.red_point = red_point;
    }



    public int getSub_type() {
        return sub_type;
    }



    public void setSub_type(int sub_type) {
        this.sub_type = sub_type;
    }



    public String getDesc() {
        return desc;
    }



    public void setDesc(String desc) {
        this.desc = desc;
    }

}
