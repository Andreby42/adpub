package com.bus.chelaile.flow.model;

public enum ChannelType {
	UC(0),		//UC
	TOUTIAO(1),	//头条
	CUSTOM(2),	//自定义模块
	WULITOUTIAO(3); // wuli头条
	
	private int type;
	
	ChannelType(int t) {
        setType(t);
    }
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
