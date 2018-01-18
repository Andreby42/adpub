package com.bus.chelaile.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.util.model.StopInfo;

public class BaseServiceUtils {

	private static final String ALL_LINES_URL = "http://100.98.168.166/baseservice/client/getCityLineList.action?cityId=%s";
	private static final String LINE_DETAIL_URL = "http://100.98.168.166/baseservice/client/getLineAndStopDetails.action?cityId=%s&lineId=%s";
	
	//获取城市所有线路
	public static List<String> getAllLines(String cityId) {
		List<String> allLines = New.arrayList();
		String url = String.format(ALL_LINES_URL, cityId);
		try{
			String response = HttpUtils.get(url, "utf-8");
			JSONObject responseJ = JSONObject.parseObject(response);
			JSONArray linesJ = responseJ.getJSONArray("data");
			for(int i = 0; i < linesJ.size(); i ++) {
				allLines.add(linesJ.getString(i));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return allLines;
	}
	
	
	// 获取线路详情，包括站点信息
	public static List<StopInfo> getStopInfo(String lineId, String cityId) {
		List<StopInfo> allStops = New.arrayList();
		String url = String.format(LINE_DETAIL_URL, cityId, lineId);
		try{
			String response = HttpUtils.get(url, "utf-8");
			JSONObject responseJ = JSONObject.parseObject(response);
			JSONArray stopsJ = responseJ.getJSONObject("data").getJSONArray("stopList");
			for(int i = 0; i < stopsJ.size(); i ++) {
				StopInfo stop = JSON.parseObject(stopsJ.getJSONObject(i).toJSONString(), StopInfo.class);
				allStops.add(stop);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return allStops;
	}
	
}
