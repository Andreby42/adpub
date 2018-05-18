/**
 * 
 */
/**
 * @author linzi
 *
 */
package com.bus.chelaile.kafka;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.service.RecordManager;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.service.UserHelper;
import com.bus.chelaile.thread.Queue;
import com.bus.chelaile.thread.model.QueueObject;
import com.bus.chelaile.util.New;

public class InfoStreamHelp {
	public static final Logger logger = LoggerFactory.getLogger(InfoStreamHelp.class);
	
	
	/*
	 * 解析广告点击日志, 记录点击数目，控制点击量
	 */
	public static void analysisClick(String line) {

		try {
//			String line = URLDecoder.decode(msg, "utf-8");
			Map<String, String> parameterMap = New.hashMap();
//			System.out.println(line.split("\\|#")[12]);
			try {
				if(line.contains(Constants.REDIRECT_DOMAIN_NAME) || line.contains("dev.ad.chelaile.net.cn"))
					parameterMap = arrayToMap(line.split("\\|#")[3].trim().split(" ")[1].replace("?", "").replace("/", "").split("&"), "=");
				else if(line.split("\\|#").length > 3 && line.split("\\|#")[12].trim().split(" ").length > 1)
					parameterMap = arrayToMap(line.split("\\|#")[12].trim().split(" ")[1].replace("?", "").replace("/", "").split("&"), "=");
				else
					parameterMap = arrayToMap(line.split("\\|#")[12].trim().split(" ")[0].replace("?", "").replace("/", "").split("&"), "=");
			} catch(Exception e) {
				logger.error("广告 解析点击日志出错,line={}", line);
				e.printStackTrace();
				logger.error(e.getMessage(), e);
				return;
			}
			String advId = parameterMap.get("advId");
			String udid = parameterMap.get("udid");
			if(udid == null || advId == null) {
				logger.info("广告为空 line={}", line);
				return;
			}
			logger.info("点击日志解析结果： advId={}, udid={}", advId, udid);
//			System.out.println(advId);
//			System.out.println(udid);
			if(StaticAds.allAds.get(advId) == null) {
				if(! Constants.ISTEST) {	// 线上需要打印这种情况，测试无需
					logger.error("缓存中未发现广告,advId={}, line={}", advId, line);
				}
				return;
			}
			
			// 广告总点击次数
			QueueObject queueobj = new QueueObject();
			queueobj.setRedisIncrKey(AdvCache.getTotalClickPV(advId));
			Queue.set(queueobj);
			
			// 存储用户点击广告到ocs中
			setClickToRecord(advId, udid);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
    public static void analysisWXAppClick(String line) {
        // TODO Auto-generated method stub

    }

	/*
	 * 将点击记录，存储到缓存中
	 */
	public static void setClickToRecord(String advId, String udid) {
		AdPubCacheRecord cacheRecord = null;
		// 放缓存的时候除了线路详情就是双栏
		String showType = StaticAds.allAds.get(advId).getShowType();
		if (showType.equals(ShowType.LINE_DETAIL.getType())) {
			cacheRecord = AdvCache.getAdPubRecordFromCache(udid, ShowType.LINE_DETAIL.getType());
		} else {
			cacheRecord = AdvCache.getAdPubRecordFromCache(udid, ShowType.DOUBLE_COLUMN.getType());
		}
		if (cacheRecord == null) {
			cacheRecord = new AdPubCacheRecord();
		}
		
		cacheRecord.buildAdPubCacheRecord(Integer.parseInt(advId), true);
		
		if (showType.equals(ShowType.LINE_DETAIL.getType())) {
			RecordManager.recordAdd(udid, showType, cacheRecord);
		} else {
			RecordManager.recordAdd(udid, ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
		}
	}
	
	/*
	 * 解析车辆详情页信息
	 */
	private static void analysisLineDetail(String line) {
		String udid = null;
		int beginIndex = line.indexOf("udid=");
		if(beginIndex == -1) {
			return;
		}
		int endIndex = line.substring(beginIndex + 5).indexOf("&");
		if(endIndex == -1) {
			udid = line.substring(beginIndex + 5).split(" ")[0];
		} else {
			udid = line.substring(beginIndex + 5).substring(0, endIndex);
		}
		
		if(UserHelper.isNewUser(udid, null, null)) {		// 7天内新增用户的访问详情页记录
			String key = AdvCache.getBusesDetailKey(udid);
			if(CacheUtil.get(key) == null) {
				CacheUtil.setToCommonOcs(key, Constants.SEVEN_DAY_TIME, System.currentTimeMillis());
			}
		}
	}

	
	/*
	 * 将日志参数临时转为map
	 */
	private static Map<String, String> arrayToMap(String[] array, String splitS) {
		Map<String, String> map = new HashMap<String, String>();
		for (String k_vS : array) {
			String k_v[] = k_vS.trim().split(splitS);
			if (k_vS.trim().split(" ").length > 1) { // 出现类似 |# adv_type:1 |#
														// adv_id:2401 HTTP/1.1
														// |# 200 的情况
				k_v = k_vS.trim().split(" ")[0].split(splitS);
			}
			if (k_v.length < 2) {
				continue;
			}

			if (k_v[1].trim().contains(",")) {
				map.put(k_v[0].trim(), k_v[1].trim().replace(",", "#"));
			} else {
				map.put(k_v[0].trim(), k_v[1].trim());
			}
		}
		return map;
	}
	
	public static void main(String[] args){
		String line = "<134>Jul  5 19:22:51 web1 nginx: 106.91.185.21 |# - |# 2017-07-05 19:22:51 |# GET /bus/line!lineDetail.action?idfa=99501C17-3547-494E-BF7C-5E58E1DCB2E2&geo_type=wgs&language=1&geo_lat=29.633844&geo_lng=106.572242&sv=9.1&s=IOS&deviceType=iPhone6s&stats_referer=searchHistory&lchsrc=icon&lineName=153&screenHeight=1334&stats_order=1-9&lng=106.572242&pushkey=&v=5.32.1&udid=d41d8cd98f00b204e9800998ecf8427ec991ae25&stats_act=enter&sign=mXfNZaM0IoKXzJFiCK18tQ==&userAgent=Mozilla/5.0%20(iPhone;%20CPU%20iPhone%20OS%209_1%20like%20Mac%20OS%20X)%20AppleWebKit/601.1.46%20(KHTML,%20like%20Gecko)%20Mobile/13B143&cityState=0&nw=4G&mac=&lineNo=153&wifi_open=1&geo_lac=30.000000&lat=29.633844&gpstype=wgs&cityId=003&push_open=0&vc=10371&userId= HTTP/1.1 |# 200 |# 0.108 |# 1760 |# - |# lite/5.32.1 (iPhone; iOS 9.1; Scale/2.00) |# - |# api.chelaile.net.cn |# 10.168.197.211:6080 |# 200 |# 1499253771438f8f0082366e4ace5223 |# 0.108 |# https";
		
//		analysisLineDetail(line);
		

		String s = "Apr 18 05:58:09 web4 nginx: 60.222.40.21 |# - |# 2018-04-18 05:58:09 |# 302 |# 0.000 |# 264 |# - |# Mozilla/5.0 (Linux; Android 7.1.1; OD103 Build/NMF26F; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.97 Mobile Safari/537.36Chelaile/3.49.0 Duiba/1.0.7 |#- |# ad.chelaile.net.cn |# - |# - |# /?advId=14015&adtype=05&udid=823da646-7d0e-40e3-98af-a61942ac8924&utm_medium=banner&adv_id=14015&last_src=app_xiaomi_store&s=android&stats_referer=lineAd&push_open=1&stats_act=switch_stn&userId=unknown&provider_id=1&geo_lt=4&timestamp=1524002279067&geo_lat=38.064121&line_id=85%E8%B7%AF-0&vc=101&sv=7.1.1&v=3.49.0&secret=edee929efc154b0cbca840ee8c5d2561&imei=990009263988027&udid=823da646-7d0e-40e3-98af-a61942ac8924&platform_v=25&utm_source=app_linedetail&stn_name=%E5%86%9B%E6%A2%B0%E5%AD%A6%E9%99%A2%E8%A5%BF&cityId=053&adv_type=5&ad_switch=7&geo_type=gcj&mac=02%3A00%3A00%3A00%3A00%3A00&deviceType=OD103&wifi_open=1&lchsrc=icon&nw=WIFI&AndroidID=7276e4d4a463ab4e&api_type=0&stn_order=15&geo_lac=25.0&accountId=53378370&language=1&first_src=app_qq_sj&geo_lng=114.484855 |# 1 |# 14015 |# https";
		//		(s.split("\\|#").length > 3 && s.split("\\|#")[3].trim().split(" ").length > 1)
		analysisClick(s);
		
	}

}