package com.bus.chelaile.flow.wangyiyun;

public enum WangYiYunErrorCode {
	SUCCESS(0);
	private Integer code;
	private WangYiYunErrorCode(Integer code) {
		this.code = code;
	}
	public Integer getCode() {
		return code;
	}
	
	
}
