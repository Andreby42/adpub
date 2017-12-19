package com.bus.chelaile.flow.wulitoutiao;

import java.util.List;

public class WuliResponse {

	private String code;
	private String requestId;
	private long requestStartTime;
	private long requestEndTime;
	private List<WuliResponseData> data;
	
	public String getCode() {
		return code;
	}
	@Override
	public String toString() {
		return "WuliResponse [code=" + code + ", requestId=" + requestId + ", requestStartTime=" + requestStartTime
				+ ", requestEndTime=" + requestEndTime + ", data=" + data + "]";
	}
	public void setCode(String code) {
		this.code = code;
	}
	public long getRequestStartTime() {
		return requestStartTime;
	}
	public void setRequestStartTime(long requestStartTime) {
		this.requestStartTime = requestStartTime;
	}
	public long getRequestEndTime() {
		return requestEndTime;
	}
	public void setRequestEndTime(long requestEndTime) {
		this.requestEndTime = requestEndTime;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public List<WuliResponseData> getData() {
		return data;
	}
	public void setData(List<WuliResponseData> data) {
		this.data = data;
	}
}
