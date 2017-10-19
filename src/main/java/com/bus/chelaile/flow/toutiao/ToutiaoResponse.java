package com.bus.chelaile.flow.toutiao;

import java.util.List;

public class ToutiaoResponse {

	private int ret;
	private String msg;
	private boolean has_more;
	private String req_id;
	private List<ToutiaoResponseData> data;
	
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isHas_more() {
		return has_more;
	}
	public void setHas_more(boolean has_more) {
		this.has_more = has_more;
	}
	public String getReq_id() {
		return req_id;
	}
	public void setReq_id(String req_id) {
		this.req_id = req_id;
	}
	public List<ToutiaoResponseData> getData() {
		return data;
	}
	public void setData(List<ToutiaoResponseData> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ToutiaoResponse [ret=" + ret + ", msg=" + msg + ", has_more=" + has_more + ", req_id=" + req_id
				+ ", data=" + data + "]";
	}
}
