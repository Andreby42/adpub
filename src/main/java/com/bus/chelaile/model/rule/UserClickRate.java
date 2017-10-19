package com.bus.chelaile.model.rule;

public class UserClickRate {

	private String udid;
	private int advId;
	private double rate;
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public int getAdvId() {
		return advId;
	}
	public void setAdvId(int advId) {
		this.advId = advId;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public UserClickRate(String udid, int advId, double rate) {
		super();
		this.udid = udid;
		this.advId = advId;
		this.rate = rate;
	}
	public UserClickRate() {
		super();
	}
	@Override
	public String toString() {
		return "UserClickRate:[udid=" + udid + ", advId=" + advId + ", rate=" + rate + "]";
	}
	
	
}
