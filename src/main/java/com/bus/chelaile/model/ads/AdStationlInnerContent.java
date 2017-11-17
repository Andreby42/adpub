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
			this.getAdCard().setGpsType("gcj"); // 默认站点坐标取自高德地图的经纬度
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
		
		adPush.setAndPaseJson("{\"pic\":\"https://image3.chelaile.net.cn/98949248b15141a9b5eb0759097b68eb\",\"bannerInfo\":{\"bannerType\":\"3\",\"name\":\"坚持打卡\",\"color\":\"174, 60, 60, 1\",\"slogan\":\"昨天喜欢你，今天喜欢你，明天看心情\",\"sloganColor\":\"29, 116, 113, 1\",\"tag\":{},\"button\":{\"buttonText\":\"测试\",\"buttonColor\":\"255, 255, 255, 1\",\"buttonBG\":\"84, 85, 25, 1\",\"buttonRim\":\"255, 0, 43, 1\",\"buttonPic\":\"\"}},\"adCard\":{\"open\":\"0\",\"cardType\":\"2\",\"logo\":\"\",\"topPic\":\"\",\"tagPic\":\"\",\"name\":\"\",\"address\":\"\",\"lng\":\"12.1\",\"lat\":\"\",\"phoneNum\":\"\",\"link\":\"\"}}");
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
}
