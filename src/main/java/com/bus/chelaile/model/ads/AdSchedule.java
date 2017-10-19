package com.bus.chelaile.model.ads;



/**
 * @author 李志鹏 2016/04/24 16:06.
 */

public class AdSchedule {

    private int adId; //广告id
    private long st; //开始时间戳
    private long et; //结束时间戳
	public int getAdId() {
		return adId;
	}
	public void setAdId(int adId) {
		this.adId = adId;
	}
	public long getSt() {
		return st;
	}
	public void setSt(long st) {
		this.st = st;
	}
	public long getEt() {
		return et;
	}
	public void setEt(long et) {
		this.et = et;
	}
	
	@Override
	public String toString() {
		return "AdSchedule(adId=" + adId + ", st=" + st + ", et=" + et + ")";
	}
    
    
    
}
