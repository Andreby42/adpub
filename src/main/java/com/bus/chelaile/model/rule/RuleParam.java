package com.bus.chelaile.model.rule;

import com.bus.chelaile.model.rule.version.VersionEntity;
import com.bus.chelaile.mvc.AdvParam;




public class RuleParam {
    private String userId;
    private String accountId;
    private String cityId;
    private String station;
    private int cityState;
    private String v;
    private int vc;
    private double lng;
    private double lat;
    private String s;  // platform.
    private String firstSrc;
    private String lastSrc;
    private String udid;
    private String lineId;
    private String nw; //网络状态
    
    private VersionEntity version;
    
    public static RuleParam from(AdvParam adv) {
        if (adv == null) {
            return null;
        }
        RuleParam rule = new RuleParam();
        rule.userId = adv.getUserId();
        rule.accountId = adv.getAccountId();
        rule.cityId = adv.getCityId();
        // station
        rule.cityState = adv.getCityState();
        rule.v = adv.getV();
        rule.vc = adv.getVc();
        rule.lng = adv.getLng();
        rule.lat = adv.getLat();
        rule.s = adv.getS();
        rule.firstSrc = adv.getFirst_src();
        rule.lastSrc = adv.getLast_src();
        rule.udid = adv.getUdid();
        rule.nw = adv.getNw();
        
        rule.lineId = adv.getLineId();
        return rule;
    }
    
    public VersionEntity getVersion() {
        synchronized(this) {
            if (version == null) {
                version = VersionEntity.parseVersionStr(v);
            }
        }
        
        return version;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public int getCityState() {
		return cityState;
	}

	public void setCityState(int cityState) {
		this.cityState = cityState;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public int getVc() {
		return vc;
	}

	public void setVc(int vc) {
		this.vc = vc;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public String getFirstSrc() {
		return firstSrc;
	}

	public void setFirstSrc(String firstSrc) {
		this.firstSrc = firstSrc;
	}

	public String getLastSrc() {
		return lastSrc;
	}

	public void setLastSrc(String lastSrc) {
		this.lastSrc = lastSrc;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getNw() {
		return nw;
	}

	public void setNw(String nw) {
		this.nw = nw;
	}

	public void setVersion(VersionEntity version) {
		this.version = version;
	}
}
