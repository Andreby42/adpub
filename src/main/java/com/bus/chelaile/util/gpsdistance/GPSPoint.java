package com.bus.chelaile.util.gpsdistance;



/**
 * Created by Yang on 2015/2/5.
 */
public class GPSPoint {

	public double lon;
	public double lat;

	public GPSPoint(double lon,double lat){
		this.lon=lon;
		this.lat=lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}
	
	
}
