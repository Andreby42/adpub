package com.bus.chelaile.util.model;

public class StopInfo {

	private String stationId;
	private String stationName;
	private double lat;
	private double lng;
	private double slng;
	private double slat;
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
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public double getSlng() {
		return slng;
	}
	public void setSlng(double slng) {
		this.slng = slng;
	}
	public double getSlat() {
		return slat;
	}
	public void setSlat(double slat) {
		this.slat = slat;
	}
}
