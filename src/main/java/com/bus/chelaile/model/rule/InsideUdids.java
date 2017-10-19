package com.bus.chelaile.model.rule;

public class InsideUdids {
	
	private int type;
	private String udid;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	@Override
	public String toString() {
		return "InsideUdids [type=" + type + ", udid=" + udid + "]";
	}
}
