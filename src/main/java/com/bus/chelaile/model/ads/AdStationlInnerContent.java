package com.bus.chelaile.model.ads;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.mvc.AdvParam;

/**
 * 推送的广告的内部的内容， 就是数据之中content的结构化表示。
 * 
 * @author liujh
 * 
 */
public class AdStationlInnerContent extends AdInnerContent {
	private String pic; // 广告图片的URL
	private AdCard adCard;
	private BannerInfo bannerInfo;
	private int buyOut;		// 买断， 0 没有买断； 1 买断。 2018-03-29
	
	private String wx_miniPro_id; 
    private String wx_miniPro_path; // 跳转小程序， 2018-04-09
    
    private int apiType;
    private int provider_id; // 广告提供商， 0：自采买广告；8：阅盟；9：Ecoook；10：科大讯飞【其他说明：3 inmobi】
    
    private long autoInterval; // 自动刷新时间
    private int adWeight;    // 轮播权重
    private long mixInterval; // 最小展示时间
    private int backup; // 是否是备选方案
	
	@Override
	protected void parseJson(String jsonr) {
		AdStationlInnerContent ad = null;
		ad = JSON.parseObject(jsonr, AdStationlInnerContent.class);
		if (ad != null) {
			this.pic = ad.pic;
			this.setAdCard(ad.getAdCard());
			if (this.getAdCard() != null)
				this.getAdCard().setGpsType("gcj"); // 默认站点坐标取自高德地图的经纬度
			this.setBannerInfo(ad.getBannerInfo());
			this.adWeight = ad.getAdWeight();
			this.buyOut = ad.getBuyOut();
			this.wx_miniPro_id = ad.getWx_miniPro_id();
			this.wx_miniPro_path = ad.getWx_miniPro_path();
			this.apiType = ad.apiType;
			this.provider_id = ad.getProvider_id();
			this.autoInterval = ad.autoInterval * 1000;
            this.mixInterval = ad.mixInterval * 1000;
            this.backup = ad.backup;
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
		AdStationlInnerContent adPush = new AdStationlInnerContent();
		
		adPush.setAndParseJson("{\"pic\":\"https://image3.chelaile.net.cn/98949248b15141a9b5eb0759097b68eb\",\"bannerInfo\":{\"bannerType\":\"3\",\"name\":\"坚持打卡\",\"color\":\"174, 60, 60, 1\",\"slogan\":\"昨天喜欢你，今天喜欢你，明天看心情\",\"sloganColor\":\"29, 116, 113, 1\",\"tag\":{},\"button\":{\"buttonText\":\"测试\",\"buttonColor\":\"255, 255, 255, 1\",\"buttonBG\":\"84, 85, 25, 1\",\"buttonRim\":\"255, 0, 43, 1\",\"buttonPic\":\"\"}},\"adCard\":{\"open\":\"0\",\"cardType\":\"2\",\"logo\":\"\",\"topPic\":\"\",\"tagPic\":\"\",\"name\":\"\",\"address\":\"\",\"lng\":\"12.1\",\"lat\":\"\",\"phoneNum\":\"\",\"link\":\"\"}}");
		System.out.println("pic: " + adPush.pic);
		System.out.println("adCard: name " + adPush.getAdCard().getName());
		System.out.println("adCard: lng " + adPush.getAdCard().getLng());
		if(adPush.getAdCard().getLng() > 1.0) {
			System.out.println("lng 太大了");
		}
		System.out.println("JsonR: " + adPush.jsonContent);
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

	public AdCard getAdCard() {
		return adCard;
	}

	public void setAdCard(AdCard adCard) {
		this.adCard = adCard;
	}

	public BannerInfo getBannerInfo() {
		return bannerInfo;
	}

	public void setBannerInfo(BannerInfo bannerInfo) {
		this.bannerInfo = bannerInfo;
	}

	public int getAdWeight() {
		return adWeight;
	}

	public void setAdWeight(int adWeight) {
		this.adWeight = adWeight;
	}

	public int getBuyOut() {
		return buyOut;
	}

	public void setBuyOut(int buyOut) {
		this.buyOut = buyOut;
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

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }

    /**
     * @return the autoInterval
     */
    public long getAutoInterval() {
        return autoInterval;
    }

    /**
     * @param autoInterval the autoInterval to set
     */
    public void setAutoInterval(long autoInterval) {
        this.autoInterval = autoInterval;
    }

    /**
     * @return the mixInterval
     */
    public long getMixInterval() {
        return mixInterval;
    }

    /**
     * @param mixInterval the mixInterval to set
     */
    public void setMixInterval(long mixInterval) {
        this.mixInterval = mixInterval;
    }

    /**
     * @return the backup
     */
    public int getBackup() {
        return backup;
    }

    /**
     * @param backup the backup to set
     */
    public void setBackup(int backup) {
        this.backup = backup;
    }

    /**
     * @return the apiType
     */
    public int getApiType() {
        return apiType;
    }

    /**
     * @param apiType the apiType to set
     */
    public void setApiType(int apiType) {
        this.apiType = apiType;
    }
}
