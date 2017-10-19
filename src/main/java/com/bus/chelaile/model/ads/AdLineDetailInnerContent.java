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
public class AdLineDetailInnerContent extends AdInnerContent {
	private String pic; // 广告图片的URL
	private int adMode; // 广告显示控制模式（不同的二进制位表示不同区域的图片显示，详见LineDetailAdMode
	private int silentTime; // 当用户点击不敢兴趣之后，此广告不展示的时间；如果小于等于0，表示未设置；单文：分钟。
	private String iosURL;
	private String androidURL;
	private String androidAudioURL; // 广告的音频URl
	private String iosAudioURL;
	private String androidArrivingAudioURL;// 广告即将到站音频url
	private String iosArrivingAudioURL;
	private String arrivingText;
	private String arrivedText;
	private String aboardText;
	
	@Override
	protected void parseJson(String jsonr) {
		AdLineDetailInnerContent ad = null;
		ad = JSON.parseObject(jsonr, AdLineDetailInnerContent.class);
		if (ad != null) {
			this.pic = ad.pic;
			this.adMode = ad.adMode;
			this.silentTime = ad.silentTime;
			this.iosURL = ad.iosURL;
			this.androidURL = ad.androidURL;
			this.androidAudioURL = ad.androidAudioURL;
			this.iosAudioURL = ad.iosAudioURL;
			this.androidArrivingAudioURL = ad.androidArrivingAudioURL;
			this.iosArrivingAudioURL = ad.iosArrivingAudioURL;
			this.arrivedText = ad.arrivedText;
			this.arrivingText = ad.arrivingText;
			this.aboardText = ad.aboardText;
			// this.rightPushNum = ad.rightPushNum;
		}
	}

	@Override
	public String extractFullPicUrl(String s) {
		if (pic != null && !pic.equals("")) {
			return getFullPicUrl(getPic());
		}
		if (s.equalsIgnoreCase("ios")) {
			return getFullPicUrl(getIosURL());
		} else {
			return getFullPicUrl(getAndroidURL());
		}
	}

	@Override
	// type=0,ArrivingAudioURL; type1,AudioURL
	public String extractAudiosUrl(String s, int type) {
		if (type == 0) {
			if (s.equalsIgnoreCase("ios")) {
				return getFullPicUrl(getIosArrivingAudioURL());
			} else {
				return getFullPicUrl(getAndroidArrivingAudioURL());
			}
		}
		if (s.equalsIgnoreCase("ios")) {
			return getFullPicUrl(getIosAudioURL());
		} else {
			return getFullPicUrl(getAndroidAudioURL());
		}

	}

	public static void main(String[] args) {
		AdLineDetailInnerContent adPush = new AdLineDetailInnerContent();
		adPush.setAndPaseJson("{\"pic\":\"http://cdn.www.chelaile.net.cn/img/subway/line10_pic.png\",\"silentTime\":30, \"c\":1}");
		System.out.println("pic: " + adPush.pic);
		System.out.println("silentTime: " + adPush.silentTime);
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
		this.iosURL = getFullPicUrl(iosURL);
		this.androidURL = getFullPicUrl(androidURL);
		this.androidAudioURL = getFullPicUrl(androidAudioURL); // 音频广告用的这个地方的
																// AdInnerContent
		this.iosAudioURL = getFullPicUrl(iosAudioURL);
		this.androidArrivingAudioURL = getFullPicUrl(androidArrivingAudioURL);
		this.iosArrivingAudioURL = getFullPicUrl(iosArrivingAudioURL);
	}

	/**
	 * 只有当adMode值在[1,63]之间的时候才是有效的值； 如果是其他值表明是投放到旧的版本，不支持adMode。
	 * 
	 * @return
	 */
	public boolean isValidAdMode() {
		if (adMode <= 0 || adMode > 63) {
			return false;
		}

		return true;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public int getAdMode() {
		return adMode;
	}

	public void setAdMode(int adMode) {
		this.adMode = adMode;
	}

	public int getSilentTime() {
		return silentTime;
	}

	public void setSilentTime(int silentTime) {
		this.silentTime = silentTime;
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

	public String getAndroidAudioURL() {
		return androidAudioURL;
	}

	public void setAndroidAudioURL(String androidAudioURL) {
		this.androidAudioURL = androidAudioURL;
	}

	public String getIosAudioURL() {
		return iosAudioURL;
	}

	public void setIosAudioURL(String iosAudioURL) {
		this.iosAudioURL = iosAudioURL;
	}

	public String getAndroidArrivingAudioURL() {
		return androidArrivingAudioURL;
	}

	public void setAndroidArrivingAudioURL(String androidArrivingAudioURL) {
		this.androidArrivingAudioURL = androidArrivingAudioURL;
	}

	public String getIosArrivingAudioURL() {
		return iosArrivingAudioURL;
	}

	public void setIosArrivingAudioURL(String iosArrivingAudioURL) {
		this.iosArrivingAudioURL = iosArrivingAudioURL;
	}

	public String getArrivingText() {
		return arrivingText;
	}

	public void setArrivingText(String arrivingText) {
		this.arrivingText = arrivingText;
	}

	public String getArrivedText() {
		return arrivedText;
	}

	public void setArrivedText(String arrivedText) {
		this.arrivedText = arrivedText;
	}

	public String getAboardText() {
		return aboardText;
	}

	public void setAboardText(String aboardText) {
		this.aboardText = aboardText;
	}
}
