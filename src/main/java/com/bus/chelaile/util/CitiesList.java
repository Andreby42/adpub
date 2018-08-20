package com.bus.chelaile.util;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.util.cities.City;

public class CitiesList {

	private static final Logger logger = LoggerFactory.getLogger(CitiesList.class);
	static final String urlMorecities = "http://api.chelaile.net.cn:7000/goocity/city!morecities.action?idfa=06DA0F64-6E86-4D26-B66E-5C9CBC351D3D&geo_type=wgs&language=1&secret=6dd7bb5dd8794b2695886680adfe0692&geo_lat=39.994489&geo_lng=116.403792&sv=10.2.1&update=0&deviceType=iPhone6&s=IOS&lchsrc=icon&pushkey=&v=5.27.0&udid=e30647e8cf40370a927c1d9101af467f89ac2cf0&sign=6GdBmwOZOx+KLRVLjEwGDA==&cityId=027&mac=&wifi_open=1&nw=WiFi&geo_lac=207.496463&push_open=1&vc=10320&accountId=899876&userId=";
	static final String urlValidCities = "https://open.uczzd.cn/openiflow/openapi/v2/cities?access_token=%s";

	public static Map<String, String> getAllCities() {

		HashMap<String, String> cities = new HashMap<String, String>();

		// 获取当前 车来了的所有城市
		String response = null;
		try {
			response = HttpUtils.get(urlMorecities, "utf-8");
			if (response != null) {
				String jsonStr = response.substring(6, response.length() - 6);
				JSONObject json = JSON.parseObject(jsonStr);
				String jsonarray = json.getJSONObject("jsonr").getJSONObject("data").getString("cities");

				ArrayList<City> allCities = (ArrayList<City>) JSON.parseArray(jsonarray, City.class);

				for (City city : allCities) {
					cities.put(city.getCityId(), city.getCityName());
				}

			} else {
				logger.error("获取城市出错! ");
				return cities;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 获取支持‘本地’频道的城市
		Set<String> validCities = New.hashSet();
//		 String token =
//		 "1490724601995-0e197f981ae53d42cfa54c4900ac31ac-98c0b111ab9fc418bbb0c9345131fe32";
		String token = (String) CacheUtil.getApiInfo("xishuashuatoken");
		if (token == null) {
			logger.error("洗刷刷token为空 ");
			return null;
		}
		String url = String.format(urlValidCities, token);
		String responseValid = null;
		try {
			responseValid = HttpUtils.get(url, "utf-8");
			if (responseValid != null) {
				JSONObject json = JSON.parseObject(responseValid);
				JSONArray jsonarray = json.getJSONObject("data").getJSONArray("cities");
				for (Object j : jsonarray) {
					validCities.add(((JSONObject) j).getString("name"));
				}

				Iterator<String> keyIt = cities.keySet().iterator();
				while (keyIt.hasNext()) {
					String cityName = cities.get(keyIt.next());
					if (!validCities.contains(cityName)) {
						keyIt.remove();
					}
				}

			} else {
				logger.error("获取城市出错! ");
				return cities;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(cities.size());
		return cities;
	}

	public static void main(String[] args) {
		System.out.println((int)((Math.random()*9+1)*100000));  
//		System.out.println(new scala.util.Random().nextInt(999999));
	}
}
