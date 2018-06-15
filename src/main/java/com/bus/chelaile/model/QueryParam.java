package com.bus.chelaile.model;

import com.bus.chelaile.model.ads.Station;

public class QueryParam {
	private Station station;
	//	true 旧接口中的加载多个,返回OpenOldAdEntity
	private boolean isOldMany = false;
	
    // 是否来自js的请求，来自js的请求，如果下发的是自采买广告，那么不需要技术！
	private boolean isJS = false;

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
    /**
     * @return the isJS
     */
    public boolean isJS() {
        return isJS;
    }
    /**
     * @param isJS the isJS to set
     */
    public void setJS(boolean isJS) {
        this.isJS = isJS;
    }

	
	
	
	
}
