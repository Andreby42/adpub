package com.bus.chelaile.util.model;

public class StopInfo {

	private String stationId;
	private String stationName;
	private String lat;
	private String lng;
	private String slng;
	private String slat;
	private long distance;
	private int order;
	
	public String getStationId() {
		return stationId;
	}
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getSlng() {
		return slng;
	}
	public void setSlng(String slng) {
		this.slng = slng;
	}
	public String getSlat() {
		return slat;
	}
	public void setSlat(String slat) {
		this.slat = slat;
	}
}
