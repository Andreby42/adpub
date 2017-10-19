package com.bus.chelaile.model.ads.entity;

public class OpenOldAdEntity extends OpenAdEntity{

    private long st; //开始时间戳
    private long et; //结束时间戳
	
	public OpenOldAdEntity(int showType) {
		super(showType);
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

	
}
