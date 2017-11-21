package com.bus.chelaile.model.ads;

public class BannerInfo {

	private int bannerType;
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
