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
			try {
				parameterMap = arrayToMap(line.split("\\|#")[3].trim().split(" ")[1].split("&"), "=");
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
			if(StaticAds.allAds.get(advId) == null) {
				if(! Constants.ISTEST) {	// 线上需要打印这种情况，测试无需
					logger.error("缓存中未发现广告,advId={}, line={}", advId, line);
				}
				return;
			}
			
			// 存储点击次数到redis,同样只保存一天
			
			QueueObject queueobj = new QueueObject();
			queueobj.setRedisIncrKey(AdvCache.getTotalClickPV(advId));
			Queue.set(queueobj);
			
			// 存储用户点击广告到ocs中
			setClickToRecord(advId, udid);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * 将点击记录，存储到缓存中
	 */
	private static void setClickToRecord(String advId, String udid) {
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
	public static void analysisLineDetail(String line) {
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
	 * 解析 文章点击埋点日志
	 */
//	public static analysisMaidian(String msg) {
//
//		try {
//			String line = URLDecoder.decode(msg, "utf-8");
//			Pattern pattern = Pattern.compile("<" + Constants.ARTICLE_CLICK + ">(.*)");
//			Matcher matcher = pattern.matcher(line);
//			Map<String, String> parameterMap = new HashMap<String, String>();
//			if (matcher.find()) {
//				String parameterS = matcher.group(1);
//				parameterMap = arrayToMap(parameterS.split("\\|#"), ":");
//			} else {
//				logger.error("匹配日志出错：{}", line);
//				return;
//			}
//			String udid = parameterMap.get("udid");
//			String newsId = parameterMap.get("article_id");
//			String newsType = parameterMap.get("article_type");
//			// String subType = parameterMap.get("sub_type");
//
//			if (udid == null || newsId == null || newsType == null) {
//				logger.error("udid,news_id,news_type 三者有字段为空: msg={}", msg);
//				return;
//			}
//
//			// 根据信息流点击，计算top点击
//			if (udid != null && newsId != null) {
//				if (!blacklist.containsKey(udid)) {
//					blacklist.put(udid, new HashSet<String>());
//				}
//				blacklist.get(udid).add(newsId);
//				// logger.info("<Info-Stream top-k>: Click log - {} clicked {}",
//				// udid, newsId);
//
//				String contentStr = (String) CacheUtil.getNew("ARTICLE#" + newsId);
//				// 只有在top文章被缓存的时候才算数
//				if (contentStr != null) {
//					UcContent uc = JSON.parseObject(contentStr, UcContent.class);
//					AnalysisLog.info("<Info-Stream top-k>: click article info:udid={},({}) ({}) ({})", udid,
//							uc.getId(), uc.getTitle(), uc.getUrl());
//					// 记录用户点击
//				}
//
//				// 统计文章的访问量，为之后找出top xx的文章做准备
//				if (wholeDayVisits.containsKey(newsId)) {
//					wholeDayVisits.put(newsId, wholeDayVisits.get(newsId) + 1);
//				} else {
//					wholeDayVisits.put(newsId, 1);
//				}
//
//				if (currentHourVisits.containsKey(newsId)) {
//					currentHourVisits.put(newsId, currentHourVisits.get(newsId) + 1);
//				} else {
//					currentHourVisits.put(newsId, 1);
//				}
//
//			}
//
//			try {
//				// 记录点击信息流文章的情况
//				StreamFormData streamData = new StreamFormData(newsId, Integer.parseInt(newsType));
//				handleClickToOcs(streamData, udid);
//			} catch (Exception e) {
//				logger.error("点击数据入ocs错误 ", e, e.getMessage());
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("解析埋点日志出错", e, e.getMessage());
//		}
//
//	}

	/*
	 * 点击结果入缓存
	 */
//	public static handleClickToOcs(StreamFormData streamData, String udid) {
//		// 详情
//		String keyInfo = AdvCache.getStreamClickInfoKey(udid);
//		String valueInfo = (String) CacheUtil.getNew(keyInfo);
//		StreamForm streamForm = new StreamForm(); // 存入ocs的结构
//		try {
//			if (null != valueInfo) {
//				streamForm = JSON.parseObject(valueInfo, StreamForm.class);
//			}
//		} catch (Exception e) {
//			CacheUtil.deleteNew(keyInfo);
//			logger.error("用户 stream click 转换出错, udid={}, streamFormStr={}", udid, valueInfo);
//			logger.error(e.getMessage(), e);
//			return;
//		}
//
//		// 如果记录不是当天的，清除掉
//		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
//		if (streamForm != null && !streamForm.getDay().equals(todayStr)) {
//			streamForm.clearData();
//			streamForm.setNumber(0);
//		}
//
//		streamForm.setDay(todayStr); // 日期是当天
//		streamForm.addData(streamData);
//		streamForm.setNumber(streamForm.getNumber() + 1); // 数目加一,初始值是0
//
//		CacheUtil.setNew(keyInfo, curDayCacheExpireTime, JSON.toJSONString(streamForm));
//
//	}
	
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
		
		analysisLineDetail(line);
		
	}
}