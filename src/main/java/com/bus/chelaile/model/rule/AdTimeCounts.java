package com.bus.chelaile.model.rule;

/**
 * 每个时间段投放次数
 * @author zzz
 *
 */
public class AdTimeCounts {
	private long count;
	private String time;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
