package com.bus.chelaile.flow.uc;

public class UcResponse {

	private UcResponseData data;
	
	private int status;
	
	public UcResponseData getData() {
		return data;
	}

	public void setData(UcResponseData data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
