package com.bus.chelaile.model;

import com.bus.chelaile.model.ads.Station;

public class QueryParam {
	private Station station;
	//	true 旧接口中的加载多个,返回OpenOldAdEntity
	private boolean isOldMany = false;

	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	public boolean isOldMany() {
		return isOldMany;
	}
	public void setOldMany(boolean isOldMany) {
		this.isOldMany = isOldMany;
	}

	
	
	
	
}
