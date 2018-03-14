package com.bus.chelaile.model.ads;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.GpsUtils;
import com.bus.chelaile.util.config.PropertiesUtils;


/**
 * 双栏广告的内部内容， 需要解析之后在转储到返回给调用者的对象之中
 * @author liujh
 *
 */
public class AdDoubleInnerContent extends AdInnerContent {
    
    private String brandIcon;
    private String brandName;
    private String promoteTitle;
    private int showDistance; //是否显示距离，0不显示1显示
    private String barColor;
    private String head;
    private String subhead;
    private int buttonType; //按钮类型，0普通按钮，1icon
    private String buttonIcon;
    private String buttonTitle;
    private String buttonColor;
    
    private String iosURL;
    private String androidURL;
    
    //新增加的属性
    private double lng = -1.0;
    private double lat = -1.0;
    
    private String tag;	//话题标签名
    private String tagId;	//话题标签id
    private String feedId; //话题详情页id
    
   /**
    * 站级别位置，双栏广告的显示位置：第n位， 0表示第一条线前面（首位），
    * 1表示第一条线后面，2表示第二条线后面，等，而-1表示最后一条线后面（末位）。
    */
    private int position = Constants.NULL_POSITION; 
    
    @Override
    public void parseJson(String jsonr) {
        AdDoubleInnerContent ad = null;
        ad = JSON.parseObject(jsonr, AdDoubleInnerContent.class);
        if (ad != null) {
            this.brandIcon = ad.brandIcon;
            this.brandName = ad.brandName;
            this.promoteTitle = ad.promoteTitle;
            this.showDistance = ad.showDistance;
            this.barColor = ad.barColor;
            this.head = ad.head;
            this.subhead = ad.subhead;
            this.buttonType = ad.buttonType;
            this.buttonIcon = ad.buttonIcon;
            this.buttonTitle = ad.buttonTitle;
            this.buttonColor = ad.buttonColor;
            
            this.lng = ad.lng;
            this.lat = ad.lat;
            this.position = ad.position;
            
            this.tag = ad.tag;
            this.tagId = ad.tagId;
            this.feedId = ad.feedId;
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

    @Override
    public void fillAdEntity(AdEntity adEntity, AdvParam param, int stindex) {
        if (adEntity == null) {
            return;
        }
        
        adEntity.setBrandIcon(this.brandIcon);
        adEntity.setBrandName(this.brandName);
        adEntity.setPromoteTitle(this.promoteTitle);
        
        if (this.showDistance == 1 && this.lng > 0 && this.lat > 0 && param != null) {
            double dist = GpsUtils.geo_distance(this.lng, this.lat, param.getLng(), param.getLat());
            adEntity.setDistance((int)(dist * 1000));
        } else {
            adEntity.setDistance(-1);
        }
        
        adEntity.setBarColor(this.barColor);
        adEntity.setHead(this.head);
        adEntity.setSubhead(this.subhead);
        adEntity.setButtonType(this.buttonType);
        adEntity.setButtonIcon(this.buttonIcon);
        adEntity.setButtonTitle(nullToEmpty(this.buttonTitle));
        adEntity.setButtonColor(nullToEmpty(this.buttonColor));
        adEntity.setFeedId(this.getFeedId());
        if(this.tag != null && this.tagId != null) {
        	adEntity.setTag(new Tag(this.tag, this.getTagId()));
        }
        
		if (param.getlSize() == -1) { // 这个参数控制版本，Constants.PLATFORM_LOG_ANDROID_0326 之前的老版本
			if (position != Constants.NULL_POSITION) {
				adEntity.setSindex(position == -1 ? (stindex + 1) : (position == 0 ? 0 : 1));
			} else {
				adEntity.setSindex(getStationLevelDefaultPosition());
			}
		} else {
			if(position == -1 || position > param.getlSize()) {
				adEntity.setSindex(param.getlSize());
			} else if(position <= param.getlSize()) {
				adEntity.setSindex(position);
			} else {
				adEntity.setSindex(getStationLevelDefaultPosition());
			}
		}
        adEntity.setLindex(0);
    }
    
    private String nullToEmpty(String str) {
        return str == null ? "" : str;
    }
    public static int getStationLevelDefaultPosition() {
    	return Integer.parseInt( PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "adv.station.level.default.pos","1") );
        //return PropertiesReaderWrapper.readInt("adv.station.level.default.pos", 1);
    }
    
    public void completePicUrl(){
        this.brandIcon = getFullPicUrl(brandIcon);
        this.buttonIcon = getFullPicUrl(buttonIcon);
    }

	public String getBrandIcon() {
		return brandIcon;
	}

	public void setBrandIcon(String brandIcon) {
		this.brandIcon = brandIcon;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getPromoteTitle() {
		return promoteTitle;
	}

	public void setPromoteTitle(String promoteTitle) {
		this.promoteTitle = promoteTitle;
	}

	public int getShowDistance() {
		return showDistance;
	}

	public void setShowDistance(int showDistance) {
		this.showDistance = showDistance;
	}

	public String getBarColor() {
		return barColor;
	}

	public void setBarColor(String barColor) {
		this.barColor = barColor;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getSubhead() {
		return subhead;
	}

	public void setSubhead(String subhead) {
		this.subhead = subhead;
	}

	public int getButtonType() {
		return buttonType;
	}

	public void setButtonType(int buttonType) {
		this.buttonType = buttonType;
	}

	public String getButtonIcon() {
		return buttonIcon;
	}

	public void setButtonIcon(String buttonIcon) {
		this.buttonIcon = buttonIcon;
	}

	public String getButtonTitle() {
		return buttonTitle;
	}

	public void setButtonTitle(String buttonTitle) {
		this.buttonTitle = buttonTitle;
	}

	public String getButtonColor() {
		return buttonColor;
	}

	public void setButtonColor(String buttonColor) {
		this.buttonColor = buttonColor;
	}

	public String getIosURL() {
		return iosURL;
	}

	public void setIosURL(String iosURL) {
		this.iosURL = iosURL;
	}

	public String getAndroidURL() {
		return androidURL;
	}

	public void setAndroidURL(String androidURL) {
		this.androidURL = androidURL;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
}
