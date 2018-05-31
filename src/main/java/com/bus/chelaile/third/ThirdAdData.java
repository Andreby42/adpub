package com.bus.chelaile.third;

public class ThirdAdData {
	//	发送数据,有数据传送方式为post
	private String data;
	//	是否需要压缩
	private boolean isGzip;
	
	private String url;
	//	上报监控url是否需要等待200ok
	private boolean isOk;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isGzip() {
		return isGzip;
	}

	public void setGzip(boolean isGzip) {
		this.isGzip = isGzip;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isOk() {
		return isOk;
	}

	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}
	
	
	
}
