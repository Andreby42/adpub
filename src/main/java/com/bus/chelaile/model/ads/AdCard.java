package com.bus.chelaile.model.ads;


public class AdCard {
	
	private int cardType;
	private String topPic;
	private String logo;
	private String tagPic;
	private String name;
	private String address;
	private Double lng;
	private Double lat;
	private String gpsType;
	private String phoneNum;
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
	public AdCard() {
		super();
	}
	public AdCard(int cardType, String topPic, String logo, String tagPic, String name, String address,
			Double lng, Double lat, String phoneNum) {
		super();
		this.cardType = cardType;
		this.topPic = topPic;
		this.logo = logo;
		this.tagPic = tagPic;
		this.name = name;
		this.address = address;
		this.lng = lng;
		this.lat = lat;
		this.phoneNum = phoneNum;
	}
	public String getTagPic() {
		return tagPic;
	}
	public void setTagPic(String tagPic) {
		this.tagPic = tagPic;
	}
	public String getGpsType() {
		return gpsType;
	}
	public void setGpsType(String gpsType) {
		this.gpsType = gpsType;
	}
	
}
