package com.bus.chelaile.util;



public class YGLocation {
	public double longitude ;
	public double latitude;
	
	public YGLocation(double longitude,double latitude){
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public YGLocation(String longitude,String latitude){
		this.longitude = Double.parseDouble(longitude);
		this.latitude = Double.parseDouble(latitude);
	}
}
