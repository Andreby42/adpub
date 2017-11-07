package com.bus.chelaile.model.ads;

import java.util.List;

public class AdCard {
	
	private int cardType;
	private String topPic;
	private String logo;
	private List<AdTagInfo> tags;
	private String name;
	private String address;
	private Double lng;
	private Double lat;
	private String phoneNum;
	private String link;
	public int getCardType() {
		return cardType;
	}
	public void setCardType(int cardType) {
		this.cardType = cardType;
	}
	public String getTopPic() {
		return topPic;
	}
	public void setTopPic(String topPic) {
		this.topPic = topPic;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public List<AdTagInfo> getTags() {
		return tags;
	}
	public void setTags(List<AdTagInfo> tags) {
		this.tags = tags;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public AdCard() {
		super();
	}
	public AdCard(int cardType, String topPic, String logo, List<AdTagInfo> tags, String name, String address,
			Double lng, Double lat, String phoneNum, String link) {
		super();
		this.cardType = cardType;
		this.topPic = topPic;
		this.logo = logo;
		this.tags = tags;
		this.name = name;
		this.address = address;
		this.lng = lng;
		this.lat = lat;
		this.phoneNum = phoneNum;
		this.link = link;
	}
	
}
