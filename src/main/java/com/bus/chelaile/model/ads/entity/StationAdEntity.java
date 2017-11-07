package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdCard;
import com.bus.chelaile.model.ads.BannerInfo;


public class StationAdEntity extends BaseAdEntity {
	
	private String pic; // 图片URL
	private BannerInfo bannerInfo;
	private AdCard adCard;
	
	// 构造方法
	public StationAdEntity() {
        super(ShowType.STATION_ADV.getValue());
        this.pic = EMPTY_STR;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public BannerInfo getBannerInfo() {
		return bannerInfo;
	}

	public void setBannerInfo(BannerInfo bannerInfo) {
		this.bannerInfo = bannerInfo;
	}

	public AdCard getAdCard() {
		return adCard;
	}

	public void setAdCard(AdCard adCard) {
		this.adCard = adCard;
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.STATION_ADV;
	}

}
