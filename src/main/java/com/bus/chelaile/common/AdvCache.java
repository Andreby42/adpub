package com.bus.chelaile.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.thread.Queue;
import com.bus.chelaile.thread.model.QueueObject;
import com.bus.chelaile.util.DateUtil;


public class AdvCache {

	//private static final int AD_RECORD_EXPIRE_TIME = 10 * 24 * 60 * 60;
	private static final int LONGEST_CACHE_TIME = 11 * 24 * 60 * 60 - 1;
//	private static final int ACITVE_CACHE_TIME = 30 * 24 * 60 * 60;
	private static final String CSHOW_KEY = "bus#cshow#";
	private static final String SEND_LINE_FEED = "SEND_LINEFEED_LOG_";
	private static final String PROJECT_CLICK_KEY = "PROJECT_CLICK_KEY_";
	
	//项目总次数key。以及各自的field
    // 类似：PROJECT_2006S1 
    //             TOTAL_SEND : 1000
    //             TOTAL_CLICK: 100
    //             2018-06-28_DAY_SEND: 900
	//             2018-06-28_DAY_CLICK: 900
    private static final String PROJECT_KEY = "PROJECT";
    private static final String TOTAL_SEND = "TOTAL_SEND";
    private static final String TOTAL_CLICK = "TOTAL_CLICK";
    private static final String DAY_SEND = "_DAY_SEND";
    private static final String DAY_CLICK = "_DAY_CLICK";
    
	protected static final Logger logger = LoggerFactory
			.getLogger(AdvCache.class);


	/**
	 * 移除不感兴趣
	 * @param udid
	 * @param showType
	 * @param advId
	 * @return
	 */
	public static String removeUninterestAds(String udid, int showType,
			int advId) {
		String key = null;
		if (showType == 5) {
			key = getAdPubRecordCacheKey(udid, ShowType.LINE_DETAIL.getType());
		} else {
			key = getAdPubRecordCacheKey(udid, ShowType.DOUBLE_COLUMN.getType());
		}
		String orgValue = (String) CacheUtil.getNew(key);
		if (orgValue == null) {
			return "";
		}
		AdPubCacheRecord record = AdPubCacheRecord.fromJson((String) orgValue);
		if (advId == 0) {
			record.getUninterestedMap().clear();
		} else {
			record.getUninterestedMap().remove(advId + "");
		}

		QueueObject obj = new QueueObject();
		obj.setAdPubCacheRecord(record);
		obj.setTime(LONGEST_CACHE_TIME);
		obj.setKey(key);

		Queue.set(obj);
		// CacheUtil.setNew(key, LONGEST_CACHE_TIME, record.toJson());
		return orgValue;
	}

	
	/**
	 * 广告缓存相关
	 * @param udid
	 * @param adType
	 * @return
	 */
	public static String getAdPubRecordCacheKey(String udid, String adType) {
		return new StringBuilder("NEWADSRECORD#").append(udid).append("#")
				.append(adType).toString();
	}

	public static void setAdPubRecordToCache(AdPubCacheRecord adPubCacheRecord,
			String udid, String adType) {
		if (adPubCacheRecord != null) {
			String adPubRecordCacheKey = getAdPubRecordCacheKey(udid, adType);
			QueueObject obj = new QueueObject();
			obj.setAdPubCacheRecord(adPubCacheRecord);
			obj.setTime(LONGEST_CACHE_TIME);
			obj.setKey(adPubRecordCacheKey);
			Queue.set(obj);
			// CacheUtil.setNew(adPubRecordCacheKey, LONGEST_CACHE_TIME,
			// adPubCacheRecord.toJson());
		}
	}

	public static AdPubCacheRecord getAdPubRecordFromCache(String udid,
			String adType) {
		String adPubRecordCacheKey = getAdPubRecordCacheKey(udid, adType);
		String value = (String) CacheUtil.getNew(adPubRecordCacheKey);
		if (null != value) {
			return AdPubCacheRecord.fromJson(value);
		}
		return null;
	}
	
	
	
	/**
	 * 信息流cache入队列相关
	 */
	/*
	 * 获取top5文章的key
	 * 当前小时
	 */
	public static String getTop5ContentsKey() {
		return new StringBuilder("CUR_HOUR").toString();
	}
	/*
	 * 获取用户不展示的ids的key
	 */
	public static String getUserBlockContentIds(String udid) {
		return new StringBuilder("BLOCK#").append(udid).toString();
	}
	/*
	 * 获取文章key
	 */
	public static String getContenKey(String id) {
		return new StringBuilder("ARTICLE#").append(id).toString();
	}
	/*
	 * 获取用户看过的ids的key
	 */
	public static String getUserContentIds(String udid) {
		return new StringBuilder("SHOWN#").append(udid).toString();
	}
	
	/*
	 * 用户点击过的信息流详情 的key
	 */
	public static String getStreamClickInfoKey(String udid) {
		return new StringBuilder("STREAM_C_INFO#").append(udid).toString();
	}
	
	/*
	 * 用户点击信息流的数据 的key
	 */
	public static String getStreamClickNumberKey(String udid) {
		return new StringBuilder("STREAM_C_NUMBER#").append(udid).toString();
	}
	
	/*
	 * 缓存的轻芒文章最后一篇
	 * 参数key=date + "#" + channelId
	 */
	public static String getQMArticleNo(String dateChannelId) {
		return new StringBuilder("QM_ARTICLE_NO#").append(dateChannelId).toString();
	}
	// 缓存的轻芒文章第一篇
	public static String getQMArticleFirstNo(String dateChannelId) {
		return new StringBuilder("QM_ARTICLE_FIRST_NO#").append(dateChannelId).toString();
	}
	
	/*
	 * 给缓存的轻芒文章构造id
	 *参数key=${date} + "#" + channelId + "#" + articleNo
	 */
	public static String getQMArticleKey(String key) {
		return new StringBuilder("QM_ARTICLE_KEY#").append(key).toString();
	}
	
	
//	public static String getQMPersonArticleNo
	
	
	
//	/*
//	 * key用户记录用户有已经点击过的广告， 对于已经点击过的广告可能不能再想用户推送。
//	 */
//	public static String getClickAdsCacheKey(String udid) {
//		return "CLICKED_ADS#" + udid;
//	}

	private static String getShortUrlCacheKey(String shortUrl) {
		return "ADVSHORTURL#" + shortUrl;
	}
	
	public static String getAppInfoKey(String udid) {
		return "APP_INFO" + udid;
	}
	
	// 存放新用户第一次访问线路详情页的时间
	public static String getBusesDetailKey(String udid) {
		return "ADV_BUSES_DETAIL_" + udid;
	}

	public static String getMinuteTimesKey(String ruleId, String minuteStr) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		return todayStr + "_" + ruleId + "_" + minuteStr;
	}
	
	public static String getUrlFromCacheByShortUrl(String shortUrl) {
		return (String) CacheUtil.get(getShortUrlCacheKey(shortUrl));
	}


	public static String getTabActivitesKey(String udid, int id) {
		return "tab_activities_" + udid  + "_" + id;
	}
	
	public static void setUrlToCache(String shortUrl, String url) {
		if (StringUtils.isNotBlank(shortUrl) && StringUtils.isNotBlank(url)) {
			CacheUtil.set(getShortUrlCacheKey(shortUrl), LONGEST_CACHE_TIME,
					url); // 时间需要再商量
		}
	}

	private static String getPushRequestKey(int advId, int ruleId) {
		return "PUSHREQUST#ADVID#" + advId + "#RULEID#" + ruleId;
	}

	public static void setPushRequestToCache(int advId, int ruleId) {
		if (advId > 0 || ruleId > 0) {
			String key = getPushRequestKey(advId, ruleId);
			CacheUtil.set(key, LONGEST_CACHE_TIME, "PUSH");
		}
	}

	public static boolean isExist(int advId, int ruleId) {
		if (advId > 0 || ruleId > 0) {
			String key = getPushRequestKey(advId, ruleId);
			Object oj = CacheUtil.get(key);
			if (null == oj) {
				return false;
			}
		}
		return true;
	}
	
	// 点击总次数，存放在redis中的key值
	public static String getTotalClickPV(String advId) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		return todayStr + "_" + "totalClickPV" + advId;
	}
	
	// 发送总次数，存放redis的key值
	// advIdRuleId = advId + "#" + ruleId
	public static String getTotalSedPV(String advIdRuleId) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		return todayStr + "_" + "totalSendPV" + advIdRuleId;
	}
	
//	/*
//	 * 保存为有效用户
//	 */
//	public static void saveRealUsers(String udid) {
//		QueueObject queueobj = new QueueObject();
//		queueobj.setKey("REALUSERS#" + udid);
//		queueobj.setTime(ACITVE_CACHE_TIME);
//		queueobj.setQueueType(QueueCacheType.REALUSERS);
//		Queue.set(queueobj);
//	}
	
//	/*
//	 * 是否有效用户
//	 */
//	public static boolean isRealUsers(String udid) {
//		Object oj = CacheUtil.getActiveOcs("REALUSERS#" + udid);
//		if(null == oj) {
//			return false;
//		}
//		return true;
//	}
	
//	/*
//	 * 保存 领取过 二维码图片的用户
//	 */
//	public static void saveQrcode(String udid, QrCode qrcode) {
//		QueueObject queueobj = new QueueObject();
//		queueobj.setKey("QRCODE#" + udid);
//		queueobj.setTime(ACITVE_CACHE_TIME);
//		queueobj.setQrcode(qrcode);
//		queueobj.setQueueType(QueueCacheType.QRCODE);
//		Queue.set(queueobj);
//	}
	
	/*
	 * 从ocs中获取保存过的qrcode
	 * 如果返回null，说明没有领取过二维码
	 */
//	public static QrCode getQrcodeFromOCS(String udid) {
//		Object oj = CacheUtil.getActiveOcs("QRCODE#" + udid);
//		if(null == oj) {
//			return null;
//		}
//		QrCode qrcode = null;
//		try {
//			qrcode = JSON.parseObject((String)oj, QrCode.class);
//		} catch(Exception e) {
//			logger.error("qrcode json 转换失败：oj = {}", oj);
//		}
//		return qrcode;
//	}

	public static void saveNewUninterestedAds(String udid, int advId,
			String lineId, int showType, String provider_id, int apiType,
			String platform, int vc) {
		String key = null;

		if (showType == 5) {
			key = getAdPubRecordCacheKey(udid, ShowType.LINE_DETAIL.getType());
		} else {
			key = getAdPubRecordCacheKey(udid, ShowType.DOUBLE_COLUMN.getType());
		}

		Object obj = CacheUtil.getNew(key);
		AdPubCacheRecord record = null;
		if (obj == null) {
			record = new AdPubCacheRecord();
		} else {
			record = AdPubCacheRecord.fromJson((String) obj);
		}

		record.saveUninterestedAds(advId, lineId, showType, provider_id,
				apiType);

		logger.info("key={},record={}", key, record.toJson());
		QueueObject queueobj = new QueueObject();
		queueobj.setAdPubCacheRecord(record);
		queueobj.setTime(LONGEST_CACHE_TIME);
		queueobj.setKey(key);
		Queue.set(queueobj);

		if (platform.equalsIgnoreCase("ios") && vc < 10240) {
			key = getAdPubRecordCacheKey(udid, ShowType.LINE_DETAIL.getType());

			obj = CacheUtil.getNew(key);
			record = null;
			if (obj == null) {
				record = new AdPubCacheRecord();
			} else {
				record = AdPubCacheRecord.fromJson((String) obj);
			}

			record.saveUninterestedAds(advId, lineId, showType, provider_id,
					apiType);

			QueueObject newqueueobj = new QueueObject();
			newqueueobj.setAdPubCacheRecord(record);
			newqueueobj.setTime(LONGEST_CACHE_TIME);
			newqueueobj.setKey(key);
			Queue.set(newqueueobj);
		}
		// CacheUtil.setNew(key, LONGEST_CACHE_TIME, record.toJson());

		logger.debug(record.toJson());
	}


	/**
	 * wuliToutiao 缓存文章的集合 key
	 * @param channelId
	 * @return
	 */
	public static String getWuliArticleCacheKey(int channelId) {
		return new StringBuilder("WULITOUTIAO#").append(channelId).toString();
	}

	// wuli udid key
	public static String getWuliUdidCacheKey(String udid) {
		return new StringBuilder("WULITOUTIAO#UDID#").append(udid).toString();
	}
	
	//网易文章缓存 key
	public static String getWangyiArticleCacheKey(String articleId) {
		return new StringBuilder("WANGYI#").append(articleId).toString();
	}
	
	// cshow。 从app的redis中获取到
	public static String getCshowKey(String udid) {
	    return CSHOW_KEY + udid;
	}

	//  详情页下方feed位投放记录
    public static String getSendLineFeedLogKey(String udid, String showType) {
        return SEND_LINE_FEED + showType + "_" + udid;
    }

    // 记录用户点击某个项目的 key
    public static String getProjectClickKey(String udid, String projectId) {
        return PROJECT_CLICK_KEY + projectId + "_" + udid;
    }
    
    // 总项目key
    public static String getProjectKey(String projectId) {
        return PROJECT_KEY + projectId;
    }
    public static String getProjectTotalSendKey() {
        return TOTAL_SEND;
    }
    public static String getProjectDaySendKey() {
        return DateUtil.getTodayStr("yyyy-MM-dd") + DAY_SEND;
    }
    public static String getProjectTotalClickKey() {
        return TOTAL_CLICK;
    }
    public static String getProjectDayClickKey() {
        return DateUtil.getTodayStr("yyyy-MM-dd") + DAY_CLICK;
    }
}
