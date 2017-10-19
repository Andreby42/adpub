package com.bus.chelaile.flow.toutiao;

public class ToutiaoEvent {

	private String category;
	private String tag;
	private String is_ad_event;
	private String label;
	private long value;
	private String log_extra;
	private long nt;
	private long client_at;
	private long show_time;
	private String datetime;
	private String ip;
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	public long getNt() {
		return nt;
	}
	public void setNt(long nt) {
		this.nt = nt;
	}
	public long getClient_at() {
		return client_at;
	}
	public void setClient_at(long client_at) {
		this.client_at = client_at;
	}
	public long getShow_time() {
		return show_time;
	}
	public void setShow_time(long show_time) {
		this.show_time = show_time;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getIs_ad_event() {
		return is_ad_event;
	}
	public void setIs_ad_event(String is_ad_event) {
		this.is_ad_event = is_ad_event;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLog_extra() {
		return log_extra;
	}
	public void setLog_extra(String log_extra) {
		this.log_extra = log_extra;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public ToutiaoEvent() {
		super();
	}
	public ToutiaoEvent(String category, String tag, String is_ad_event, String label, long value, String log_extra,
			long nt, long client_at, long show_time, String ip) {
		super();
		this.category = category;
		this.tag = tag;
		this.is_ad_event = is_ad_event;
		this.label = label;
		this.value = value;
		this.log_extra = log_extra;
		this.nt = nt;
		this.client_at = client_at;
		this.show_time = show_time;
		this.ip = ip;
	}
	public ToutiaoEvent(String category, String tag, String label, long value, String datetime) {
		super();
		this.category = category;
		this.tag = tag;
		this.label = label;
		this.value = value;
		this.datetime = datetime;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
}
