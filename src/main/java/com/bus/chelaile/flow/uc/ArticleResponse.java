package com.bus.chelaile.flow.uc;

public class ArticleResponse {
	
	private ArticleResponseData data;
	
	private int status;

	public ArticleResponseData getData() {
		return data;
	}

	public void setData(ArticleResponseData data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
