package com.bus.chelaile.model.ads;

public class BannerInfo {

	private int bannerType; //  banner的5种类型：0 商户简称+广告语； 1 商户简称+广告语+标签； 2 商户简称+广告语+标签(图片)；
							//3 商户简称+广告语+按钮； 4 商户简称+广告语+按钮(图片)；5 广告语+标签（图片）+按钮（图片）
							//6 的情况也修改为5
	                        //5 为口碑券专用，6为淘宝客专用
	private String name;
	private String color;
	private String slogan;
	private String sloganColor;
	private AdTagInfo tag;
	private AdButtonInfo button;
	public int getBannerType() {
		return bannerType;
	}
	public void setBannerType(int bannerType) {
		this.bannerType = bannerType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getSlogan() {
		return slogan;
	}
	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}
	public String getSloganColor() {
		return sloganColor;
	}
	public void setSloganColor(String sloganColor) {
		this.sloganColor = sloganColor;
	}
	public AdTagInfo getTag() {
		return tag;
	}
	public void setTag(AdTagInfo tag) {
		this.tag = tag;
	}
	public AdButtonInfo getButton() {
		return button;
	}
	public void setButton(AdButtonInfo button) {
		this.button = button;
	}
	public BannerInfo() {
		super();
	}
	public BannerInfo(int bannerType, String name, String color, String slogan, String sloganColor, AdTagInfo tag,
			AdButtonInfo button) {
		super();
		this.bannerType = bannerType;
		this.name = name;
		this.color = color;
		this.slogan = slogan;
		this.sloganColor = sloganColor;
		this.tag = tag;
		this.button = button;
	}
}
