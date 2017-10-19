package com.bus.chelaile.util.cities;

public class City {

	private String cityName;
	private String cityId;
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	@Override
	public String toString() {
		return "City [cityName=" + cityName + ", cityId=" + cityId + "]";
	}
}
