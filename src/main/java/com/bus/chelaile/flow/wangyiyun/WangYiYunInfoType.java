package com.bus.chelaile.flow.wangyiyun;

public enum WangYiYunInfoType {
	ARTICLE("article",0);
	
	private String wangYiType;
	private Integer type;
	private WangYiYunInfoType(String wangYiType, Integer type) {
		this.wangYiType = wangYiType;
		this.type = type;
	}
	public String getWangYiType() {
		return wangYiType;
	}
	public Integer getType() {
		return type;
	}
	
	
}
