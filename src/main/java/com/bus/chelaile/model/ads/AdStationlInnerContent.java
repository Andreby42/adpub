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
	
	
	
	@Override
	protected void parseJson(String jsonr) {
		AdStationlInnerContent ad = null;
		ad = JSON.parseObject(jsonr, AdStationlInnerContent.class);
		if (ad != null) {
			this.pic = ad.pic;
			this.setAdCard(ad.getAdCard());
			this.setBannerInfo(ad.getBannerInfo());
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
		
		adPush.setAndPaseJson("{\"adCard\":{\"address\":\"银河系，惠中路5号，B座，22层\",\"cardType\":2,\"lat\":39.994642,\"lin"
				+ "k\":\"http://www.baidu.com\",\"lng\":116.403931,\"logo\":\"http://pic1.chelaile.net.cn/adv/brandIcon"
				+ "1170620170823.png\",\"name\":\"路边野店\",\"phoneNum\":\"15072435749\",\"tagPic\":\"http://pic1.chela"
				+ "ile.net.cn/adv/brandIcon1170620170823.png\",\"topPic\":\"http://pic1.chelaile.net.cn/adv/ios67326f0f-ebeb-47e0-bce"
				+ "3-99cb78cc02aa.jpg\"},\"bannerInfo\":{\"bannerType\":3,\"button\":{\"buttonBG\":\"139,43,43,1\",\"but"
				+ "tonColor\":\"255,255,255,1\",\"buttonPic\":\"http://pic1.chelaile.net.cn/adv/brandIcon1187320170922.png\",\"butto"
				+ "nRim\":\"118,89,89,1\",\"buttonText\":\"查看\"},\"color\":\"255,255,255,1\",\"name\":\"路边野店\",\"slogan\":\"来路边野店，找童"
				+ "年的味道\",\"sloganColor\":\"255,255,255,1\"},\"pic\":\"http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png\"}");
		System.out.println("pic: " + adPush.pic);
		System.out.println("adCard: name " + adPush.getAdCard().getName());
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
}
