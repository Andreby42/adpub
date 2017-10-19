package com.bus.chelaile.model.record;

import java.util.Map;

import com.bus.chelaile.flow.model.FlowContent;
/**
 * uc的缓存
 * @author zzz
 *
 */
public class UcApiRecord {
	FlowContent ucContent;
	long time; // 保存时间
	Map<Integer, Integer> map; // key显示的位置,value 展示次数
	public FlowContent getUcContent() {
		return ucContent;
	}
	public void setUcContent(FlowContent ucContent) {
		this.ucContent = ucContent;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public Map<Integer, Integer> getMap() {
		return map;
	}
	public void setMap(Map<Integer, Integer> map) {
		this.map = map;
	}

	
}
