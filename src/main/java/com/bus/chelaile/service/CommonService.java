package com.bus.chelaile.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.SingleInfoLog;
import com.bus.chelaile.common.TimeLong;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.model.record.DisplayUserCache;
import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.DateUtil;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;


public class CommonService{

	// 单车投放距离区间
	private static final int min_distance = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "bicycle.min.distance","100"));
	private static final int max_distance = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "bicycle.max.distance","1000"));
	private static final String getOdUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "getOd.url", "http://120.26.103.141:7070/getOd");
	
	protected static final Logger logger = LoggerFactory.getLogger(CommonService.class);
	
	/**
	 * 得到一个用户所有可看到的广告
	 * @param advParam
	 * @return
	 */
	public static DisplayUserCache getDisplayAdvByUdid(AdvParam advParam) {
		// 线路详情
		List<AdContentCacheEle> lineCacheList = getCommonsAdsList(advParam.getUdid(), advParam.getAccountId(), ShowType.LINE_DETAIL);
		// 开屏
		List<AdContentCacheEle> openCacheList = getCommonsAdsList(advParam.getUdid(), advParam.getAccountId(), ShowType.OPEN_SCREEN);
		// 浮层
		List<AdContentCacheEle> fullCacheList = getCommonsAdsList(advParam.getUdid(), advParam.getAccountId(), ShowType.FULL_SCREEN);
		
		// 双栏的
		List<AdContentCacheEle> doubleCacheList = getCommonsAdsList(advParam.getUdid(), advParam.getAccountId(), ShowType.DOUBLE_COLUMN);
		// 单栏的
		List<AdContentCacheEle> singleCacheList = getCommonsAdsList(advParam.getUdid(), advParam.getAccountId(), ShowType.SINGLE_COLUMN);
		//	分享页
		List<AdContentCacheEle> rideCacheList = getCommonsAdsList(advParam.getUdid(), advParam.getAccountId(), ShowType.RIDE_DETAIL);
		//	活动页
		List<AdContentCacheEle> activeCacheList = getCommonsAdsList(advParam.getUdid(), advParam.getAccountId(), ShowType.ACTIVE_DETAIL);
		// 详情
		AdPubCacheRecord cacheRecord = AdvCache.getAdPubRecordFromCache(
				advParam.getUdid(), ShowType.LINE_DETAIL.getType());

		if (cacheRecord != null) {
			SingleInfoLog.info("udid={},value={}", advParam.getUdid(),
					cacheRecord.toJson());
		}

		AdPubCacheRecord stationCacheRecord = AdvCache.getAdPubRecordFromCache(
				advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType());

		if (stationCacheRecord != null) {
			SingleInfoLog.info("udid={},value={}", advParam.getUdid(),
					stationCacheRecord.toJson());
		}

		DisplayUserCache dc = new DisplayUserCache();
		
		lineCacheList = mergeAllAds(lineCacheList);
		
		openCacheList = mergeAllAds(openCacheList);
		
		fullCacheList = mergeAllAds(fullCacheList);
		
		doubleCacheList = mergeAllAds(doubleCacheList);
		
		singleCacheList = mergeAllAds(singleCacheList);
		
		rideCacheList = mergeAllAds(rideCacheList);
		
		activeCacheList = mergeAllAds(activeCacheList);

		setDisplayAdvList(lineCacheList, dc);

		setDisplayAdvList(openCacheList, dc);

		setDisplayAdvList(fullCacheList, dc);
		
		setDisplayAdvList(doubleCacheList, dc);
		
		setDisplayAdvList(singleCacheList, dc);
		
		setDisplayAdvList(rideCacheList, dc);
		
		setDisplayAdvList(activeCacheList, dc);

		if (cacheRecord != null) {
			dc.createUninterested(cacheRecord.getUninterestedMap());
		}

		if (stationCacheRecord != null) {
			dc.createUninterested(stationCacheRecord.getUninterestedMap());
		}

		return dc;
	}

	private static void setDisplayAdvList(List<AdContentCacheEle> cacheList,
			DisplayUserCache dc) {
		if (cacheList != null) {
			for (AdContentCacheEle ad : cacheList) {
				dc.createAdvInfo(ad.getAds().getTitle(), ad.getAds()
						.getShowType() + "", ad.getAds().getId());
			}

		}
	}

	
	/**
	 * 得到该类型的所有广告
	 * 
	 * @param showType
	 * @return
	 */
	private static List<AdContentCacheEle> getAdsList(String showType) {
		return StaticAds.getAllAdsMap(showType);
	}

	private static List<String> getUserAdCache(String id, String showType) {
		return StaticAds.getIdentificationAdsList(id, showType);
	}
	
	/**
	 * 根据id获得广告
	 * 
	 * @param udid
	 * @param accountId
	 * @return
	 */
	private static List<AdContentCacheEle> getIdList(String udid, String accountId, String showType) {
		List<AdContentCacheEle> list = null;

		List<String> udidListKeys = getUserAdCache(udid, showType);
		List<AdContentCacheEle> udidList = New.arrayList();
		if (udidListKeys != null) {
			for (String s : udidListKeys) {
				udidList.add(StaticAds.allAdContentCache.get(s));
			}
		}

		List<String> accountIdListKeys = getUserAdCache(accountId, showType);
		List<AdContentCacheEle> accountIdList = New.arrayList();
		if (accountIdListKeys != null) {
			for (String s : accountIdListKeys) {
				accountIdList.add(StaticAds.allAdContentCache.get(s));
			}
		}

		if (udidList != null && accountIdList != null) {
			list = New.arrayList();
			if (udidList != null) {
				list.addAll(udidList);
			}
			if (accountIdList != null) {
				list.addAll(accountIdList);
			}
		} else {

			if (udidList != null) {
				return udidList;
			} else {
				return accountIdList;
			}

		}

		return list;
	}


	/**
	 * 合并所有广告
	 * 
	 * @param adsList
	 * @return
	 */
	public static List<AdContentCacheEle> mergeAllAds(List<AdContentCacheEle> adsList) {
		if (adsList == null || adsList.size() == 0)
			return null;
		if (adsList.size() == 1) {
			return adsList;
		}

		Map<String, AdContentCacheEle> map = New.hashMap();

		for (AdContentCacheEle ac : adsList) {
			String ruleIds = "";
			for (Rule rule : ac.getRules()) {
				ruleIds += rule.getRuleId();
			}
			map.put(ac.getAds().getId() + ruleIds, ac);
		}

		return new ArrayList<AdContentCacheEle>(map.values());

	}
	
	/**
	 * 取得所有广告
	 * 
	 * @param udid
	 * @param accountId
	 * @param showType
	 * @return
	 */
	public static List<AdContentCacheEle> getAllAdsList(String udid, String accountId, ShowType showType) {
		return getCommonsAdsList(udid, accountId, showType);
	}
	
	/**
	 * 通用的得到所有广告代码
	 * 
	 * @param udid
	 * @param accountId
	 * @param showType
	 * @return
	 */
	private static List<AdContentCacheEle> getCommonsAdsList(String udid, String accountId, ShowType showType) {
		List<AdContentCacheEle> idList = getIdList(udid, accountId, showType.getType());
		List<AdContentCacheEle> list = getAdsList(showType.getType());
		if (idList == null && list == null) {
			return null;
		}
		if (idList != null) {
			if (list != null) {
				idList.addAll(list);
			}
			return idList;
		} else {
			return list;
		}
	}
	
	/**
	 * 时间比较
	 * 
	 * @param startDate
	 * @param showType
	 * @return
	 */
	public static boolean dateCompare(Date startDate, String showType) {

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
		Date date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		// 开始时间在2天之内
		if (date.compareTo(startDate) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 根据距离判断是否投放单车
	 * 1、用户与首页第一个站点的距离
	 * 2、用户与接下来换乘的站点的距离
	 */
	public static boolean isShowBikeByDistance(AdvParam advParam) {
		
		if(advParam.getDistance() < min_distance || advParam.getDistance() > max_distance) {
			logger.info("first station distance too far. udid={}, distance={}", advParam.getUdid(), advParam.getDistance());
			return false;
		}
		
		int distance = getTransferDistance(advParam);
		if(distance < min_distance || distance > max_distance) {
			logger.info("transfer station distance too far. udid={}, distance={}", advParam.getUdid(), advParam.getDistance());
			return false;
		}
		
		return true;
	}

	/*
	 * 从鹏飞的接口获取换乘站点距离
	 */
	private static int getTransferDistance(AdvParam advParam) {
		if(advParam.getLng() == -1.0 || advParam.getLat() == -1.0) {
			return -1;
		}
		
		try {
			List<NameValuePair> params = New.arrayList();
			params.add(new BasicNameValuePair("udid", advParam.getUdid()));
			params.add(new BasicNameValuePair("lng", String.valueOf(advParam.getLng())));
			params.add(new BasicNameValuePair("lat", String.valueOf(advParam.getLat())));
			params.add(new BasicNameValuePair("cityId", advParam.getCityId()));
//			params.add(new BasicNameValuePair("lineId", advParam.getLineId()));
			params.add(new BasicNameValuePair("time", DateUtil.getTodayStr("yyyy-MM-dd HH:mm:ss")));

			long t1 = System.currentTimeMillis();
			String response = HttpUtils.getUri(getOdUrl, params, "utf-8");
			if(System.currentTimeMillis() - t1 > 100L) {	// 记录接口请求大于100ms的情况
				TimeLong.info("getOd url cost too much! costTime={}", System.currentTimeMillis() - t1);
				logger.error("getOd url cost too much! costTime={}", System.currentTimeMillis() - t1);
			}
			
			JSONObject reponsJ = JSONObject.parseObject(response);
			String state = reponsJ.getString("state");
			if(state != null && state.equals("Success")) {
				JSONArray data = reponsJ.getJSONArray("data");
				if(data.size() >= 1) {
					return data.getJSONObject(0).getIntValue("mileage");
				}
			}
		} catch (Exception  e ) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public static void main(String[] args) {
		AdvParam param = new AdvParam();
		
		param.setUdid("3ecdfaac8649fedd64ae2fb8c6233553e756fbae");
		param.setCityId("003");
		param.setLng(106.5);
		param.setLat(29.5);
		
		int distance = getTransferDistance(param);
		System.out.println(distance);
		
	}
	
}
