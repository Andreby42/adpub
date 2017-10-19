package com.bus.chelaile.model.record;

import java.util.Map;

import com.bus.chelaile.innob.response.ad.NativeResponse;
/**
 * innobe的缓存
 * @author zzz
 *
 */
public class ApiRecord {
	NativeResponse response;
	long time; // 保存时间
	Map<Integer, Integer> map; // key显示的位置,value 展示次数

	public NativeResponse getResponse() {
		return response;
	}

	public void setResponse(NativeResponse response) {
		this.response = response;
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
