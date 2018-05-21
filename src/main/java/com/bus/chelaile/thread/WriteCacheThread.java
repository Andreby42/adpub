package com.bus.chelaile.thread;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.util.DateUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.common.TimeLong;
import com.bus.chelaile.model.QueueCacheType;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.model.record.ApiRecord;
import com.bus.chelaile.model.record.CacheRecord;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.thread.model.QueueObject;

public class WriteCacheThread implements Runnable {

	protected static final Logger logger = LoggerFactory.getLogger(WriteCacheThread.class);

	private static int count = 0;

	/**
	 * 未处理历史的访问次数
	 */
	@Override
	public void run() {

		count++;

		logger.info("startThread=" + count);

		while (true) {
			try {
				QueueObject obj = Queue.get();
				if (Queue.size() > 100) {
					TimeLong.info("Queue.size={}", Queue.size());
				}
				if (obj.getRedisIncrKey() != null && !obj.getRedisIncrKey().equals("")) {
					long time = System.currentTimeMillis();
					CacheUtil.incrToCache(obj.getRedisIncrKey(), Constants.SEVEN_DAY_TIME);	// 保存七天
					time = System.currentTimeMillis() - time;
					if (time > 30) {
						TimeLong.info("redisTime=" + time);
					}
				}
//				//缓存 有效的用户udid
//				else if(obj.getQueueType() != null && 
//						obj.getQueueType() == QueueCacheType.REALUSERS) {
//					CacheUtil.setActiveOcs(obj.getKey(), obj.getTime(), "1");
//				}
//				//缓存 用户领取过的二维码图片地址
//				else if(obj.getQueueType() != null && 
//						obj.getQueueType() == QueueCacheType.QRCODE) {
//					CacheUtil.setActiveOcs(obj.getKey(), obj.getTime(), JSON.toJSONString(obj.getQrcode()));
//				}
				//缓存信息流文章
				else if(obj.getQueueType() != null && 
						obj.getQueueType() == QueueCacheType.ARTICLES) {
					try {
						CacheUtil.setNew(obj.getKey(), obj.getTime(), JSON.toJSONString(obj.getUcContent()));
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				} 
				//缓存 用户看过的信息流
				else if (obj.getQueueType() != null && 
						obj.getQueueType() == QueueCacheType.DISPLAY_IDS) {
					try{
						CacheUtil.setNew(obj.getKey(), obj.getTime(), JSON.toJSONString(obj.getArticleIds()));
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
				//缓存用户广告相关
				else {
					dealQueueObject(obj);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				;
			}
		}

	}

	private void dealQueueObject(QueueObject obj) {

		AdPubCacheRecord cache = obj.getAdPubCacheRecord();

		dealHistoryMap(cache);

		dealOpenHistoryMap(cache);
		
		try {
			dealcacheRecordMap(cache);

			dealApiMap(cache);

			dealFirstClickMap(cache);

			dealUvMap(cache);
			
			dealOpenAdPubTime(cache);
			
			dealNoFeedAdHistoryMap(cache);
		} catch (Exception e) {
			logger.error("deal cache ERROR,cache={}", cache.toJson());
			logger.error(e.getMessage(), e);
		}

		CacheUtil.setNew(obj.getKey(), obj.getTime(), obj.getAdPubCacheRecord().toJson());

	}

	/**
	 * 只保留两天,控制feed广告投放 次数间隔 的结构
	 * @param cache
	 */
	private void dealNoFeedAdHistoryMap(AdPubCacheRecord cache) {
		Map<String, Map<Integer, Integer>> noFeedHistory = cache.getTodayNoFeedAdHistoryMap();
		if(noFeedHistory == null) {
			return;
		}
		Iterator<Map.Entry<String, Map<Integer, Integer>>> it = noFeedHistory.entrySet().iterator();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -2);// 把日期往后增加七天.整数往后推,负数往前移动
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(calendar.getTime()); // 这个时间就是日期往前推2天的结果
		
		while(it.hasNext()) {
			Map.Entry<String, Map<Integer, Integer>> entry = it.next();
			String dayStr = entry.getKey();
			// 存储日期在2天之内
			if (dateStr.compareTo(dayStr) <= 0) {
				continue;
			} else {
				// 2天之外的清理掉
				it.remove();
			}
		}
	}

	/*
	 * 只保留2天内，控制开屏广告投放 时间间隔 的结构
	 */
	private void dealOpenAdPubTime(AdPubCacheRecord cache) {
		Map<String, Map<Integer, Long>> openAdPubTime = cache.getTodayOpenAdPubTime();
		if(openAdPubTime == null) {
			return;
		}
		Iterator<Map.Entry<String, Map<Integer, Long>>> it = openAdPubTime.entrySet().iterator();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -2);// 把日期往后增加七天.整数往后推,负数往前移动
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(calendar.getTime()); // 这个时间就是日期往前推2天的结果
		
		while(it.hasNext()) {
			Map.Entry<String, Map<Integer, Long>> entry = it.next();
			String dayStr = entry.getKey();
			// 存储日期在2天之内
			if (dateStr.compareTo(dayStr) <= 0) {
				continue;
			} else {
				// 2天之外的清理掉
				// record.getDayCountMap().remove(dayStr);
				it.remove();
			}
		}
	}

	/**
	 * 只保留1个小时
	 * 
	 * @param cache
	 */
	private void dealApiMap(AdPubCacheRecord cache) {
		Map<String, ApiRecord> map = cache.getApiRecordMap();
		if (map == null) {
			return;
		}
		Iterator<Map.Entry<String, ApiRecord>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ApiRecord> entry = it.next();
			long time = System.currentTimeMillis() - entry.getValue().getTime();
			// 已经失效
			if (time >= AdPubCacheRecord.innmobiSaveTime * 60 * 1000) {
				map.remove(entry.getKey());
			}
		}
	}

	/**
	 * 只保留一天的记录
	 * 
	 * @param cache
	 */
	private void dealHistoryMap(AdPubCacheRecord cache) {
		// 详情页
		Map<String, Map<AdCategory, Integer>> map = cache.getTodayHistoryMap();
		if (map == null || 1 >= map.size()) {
			return;
		}
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		Map<AdCategory, Integer> todayMap = map.get(todayStr);
		cache.getTodayHistoryMap().clear();
		cache.getTodayHistoryMap().put(todayStr, todayMap);
	}

	private void dealOpenHistoryMap(AdPubCacheRecord cache) {
		// 开屏
		Map<String, Map<AdCategory, Integer>> openMap = cache.getTodayOpenHistoryMap();
		if (openMap == null || 1 >= openMap.size()) {
			return;
		}
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		Map<AdCategory, Integer> todayOpenMap = openMap.get(todayStr);
		cache.getTodayOpenHistoryMap().clear();
		cache.getTodayOpenHistoryMap().put(todayStr, todayOpenMap);
	}

	/**
	 * 保留7天
	 * 
	 * @param cache
	 */
	private void dealcacheRecordMap(AdPubCacheRecord cache) {
		Map<Integer, CacheRecord> recordMap = cache.getCacheRecordMap();
		if (recordMap == null || 1 >= recordMap.size()) {
			return;
		}

		recordMap.remove(-1); // 非自采买的清理
		if (recordMap == null || 1 >= recordMap.size()) {
			return;
		}

		Iterator<Map.Entry<Integer, CacheRecord>> it = recordMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, CacheRecord> entry = it.next();
			CacheRecord record = entry.getValue();
			if (record == null) {
				continue;
			}

			Iterator<Map.Entry<String, Integer>> entryDayCountIt = record.getDayCountMap().entrySet().iterator();
			if (record.getDayCountMap().size() == 0) { // 现在已经不记录clickCount了，如果dayCountMap大小为0，直接清理
				it.remove();
				continue;
			}

			while (entryDayCountIt.hasNext()) { // 解析dayCountMap，7天前的清理
				Map.Entry<String, Integer> entryDayCount = entryDayCountIt.next();
				String dayStr = entryDayCount.getKey();
				// int count = entryDayCount.getValue();

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(new Date());
				calendar.add(Calendar.DATE, -7);// 把日期往后增加七天.整数往后推,负数往前移动
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String dateStr = sdf.format(calendar.getTime()); // 这个时间就是日期往前推七天的结果

				// 存储日期在7天之内
				if (dateStr.compareTo(dayStr) <= 0) {
					continue;
				} else {
					// 7天之外的清理掉
					// record.getDayCountMap().remove(dayStr);
					entryDayCountIt.remove();
				}
			}

//			if (record.getDayCountMap().size() == 0) { // 现在已经不记录clickCount了，如果dayCountMap大小为0，直接清理
//				it.remove();
//			}

		}
	}

	/**
	 * 保留1天
	 * 
	 * @param cache
	 */
	private void dealFirstClickMap(AdPubCacheRecord cache) {
		if (cache.getFirstClickMap() == null || cache.getFirstClickMap().size() <= 1) {
			return;
		}

		Iterator<String> keyIt = cache.getFirstClickMap().keySet().iterator();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			String dayStr = key.substring(0, 10); // String todayStr =
													// DateUtil.getTodayStr("yyyy-MM-dd");
													// String key = todayStr +
													// "-" + ruleId + "-" +
													// time;

			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, -1); // 把日期往后增加一天.整数往后推,负数往前移动
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = sdf.format(calendar.getTime()); // 这个时间就是日期往前推一天的结果

			if (dateStr.compareTo(dayStr) <= 0) {
				continue;
			} else {
				// cache.getFirstClickMap().remove(key);
				keyIt.remove();
			}
		}
	}

	/**
	 * 保留10天
	 * 
	 * @param cache
	 */
	private void dealUvMap(AdPubCacheRecord cache) {
		Map<Integer, String> uvMap = cache.getUvMap();
		if (uvMap == null || uvMap.size() <= 1) {
			return;
		}

		Iterator<Map.Entry<Integer, String>> it = uvMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, String> entry = it.next();
			String dayStr = entry.getValue();

			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, -10); // 把日期往后增加十天.整数往后推,负数往前移动
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = sdf.format(calendar.getTime()); // 这个时间就是日期往前推十天的结果

			if (dateStr.compareTo(dayStr) <= 0) {
				continue;
			} else {
				it.remove();
			}
		}

	}

//	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
////		WriteCacheThread w = new WriteCacheThread();
//
////		QueueObject obj = new QueueObject();
////		String cacheStr = "{\"apiRecordMap\":{},\"cacheRecordMap\":{2511:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-26\":4,\"2016-10-27\":6,\"2016-10-28\":11}},2673:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-15\":5}},2613:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-27\":3}},2509:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-28\":8}},2612:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-11\":3,\"2016-12-05\":3,\"2016-12-07\":3}},2611:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-23\":3}},2677:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-17\":9}},2609:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-12\":5,\"2016-11-23\":5}},2683:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-19\":3}},2680:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-18\":3,\"2016-12-17\":3}},2438:{\"clickCount\":0,\"dayCountMap\":{\"2016-09-30\":3}},2497:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-25\":3}},2616:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-28\":1}},2598:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-17\":3,\"2016-11-21\":3,\"2016-11-30\":3}},2458:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-12\":10}},2391:{\"clickCount\":0,\"dayCountMap\":{\"2016-09-23\":2}},2390:{\"clickCount\":0,\"dayCountMap\":{\"2016-09-26\":3}},2525:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-31\":4,\"2016-10-30\":3,\"2016-10-29\":3}},2457:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-14\":1,\"2016-10-12\":14}},2662:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-14\":1}},2594:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-16\":6}},2523:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-31\":9}},2660:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-13\":3}},2661:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-18\":3,\"2016-12-17\":3}},2604:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-22\":5}},2448:{\"clickCount\":0,\"dayCountMap\":{\"2016-09-30\":3}},2517:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-26\":4}},2603:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-22\":7}},2515:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-04\":3,\"2016-11-02\":3,\"2016-10-31\":4,\"2016-10-26\":3,\"2016-10-28\":3,\"2016-11-06\":3}},2669:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-15\":3}},2601:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-19\":6}},2580:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-24\":1}},2543:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-12\":3}},2583:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-24\":5,\"2016-11-15\":11}},2464:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-14\":3,\"2016-10-15\":3}},2471:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-18\":5}},2470:{\"clickCount\":0,\"dayCountMap\":{\"2016-10-17\":3}},2586:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-14\":3}},2626:{\"clickCount\":0,\"dayCountMap\":{\"2016-12-01\":3}}},\"cacheTime\":600,\"displayAdv\":true,\"firstClickMap\":{\"2016-12-11-3276-07:01-08:00\":{\"count\":3,\"time\":1481413918275},\"2016-11-04-3138-18:01-19:00\":{\"count\":1,\"time\":1478254339674},\"2016-10-31-3158-00:00-06:00\":{\"count\":1,\"time\":1477843548267},\"2016-12-14-3320-08:01-09:00\":{\"count\":1,\"time\":1481673681647},\"2016-11-16-3234-08:01-09:00\":{\"count\":1,\"time\":1479254597158},\"2016-11-17-3236-08:01-09:00\":{\"count\":3,\"time\":1479341099536},\"2016-10-26-3138-08:01-09:00\":{\"count\":1,\"time\":1477440074060},\"2016-11-04-3138-08:01-09:00\":{\"count\":2,\"time\":1478217859482},\"2016-11-23-3252-14:01-15:00\":{\"count\":3,\"time\":1479882213245},\"2016-12-07-3275-14:01-15:00\":{\"count\":3,\"time\":1481091969867},\"2016-11-12-3197-09:01-10:00\":{\"count\":3,\"time\":1478913887507},\"2016-10-25-3133-18:01-19:00\":{\"count\":1,\"time\":1477389899647},\"2016-12-18-3319-23:01-23:59\":{\"count\":1,\"time\":1482075271809},\"2016-11-28-3261-08:01-09:00\":{\"count\":1,\"time\":1480291298973},\"2016-10-15-3090-23:01-23:59\":{\"count\":3,\"time\":1476545394591},\"2016-10-25-3133-08:01-09:00\":{\"count\":1,\"time\":1477353892146},\"2016-09-30-3069-23:01-23:59\":{\"count\":1,\"time\":1475249647604},\"2016-11-27-3258-07:01-08:00\":{\"count\":3,\"time\":1480204296535},\"2016-12-19-3366-13:01-14:00\":{\"count\":3,\"time\":1482124208648},\"2016-10-18-3097-23:01-23:59\":{\"count\":3,\"time\":1476803939436},\"2016-12-18-3360-23:01-23:59\":{\"count\":3,\"time\":1482073883344},\"2016-09-26-3033-08:01-09:00\":{\"count\":3,\"time\":1474848327845},\"2016-11-21-3236-14:01-15:00\":{\"count\":3,\"time\":1479709366245},\"2016-10-31-3138-14:01-15:00\":{\"count\":2,\"time\":1477895491510},\"2016-11-19-3240-23:01-23:59\":{\"count\":1,\"time\":1479568343290},\"2016-12-17-3319-09:01-10:00\":{\"count\":1,\"time\":1481936480264},\"2016-09-23-2990-22:01-23:00\":{\"count\":1,\"time\":1474640453103},\"2016-12-15-3324-23:01-23:59\":{\"count\":3,\"time\":1481814984894},\"2016-12-05-3274-14:01-15:00\":{\"count\":3,\"time\":1480918495258},\"2016-09-30-3067-23:01-23:59\":{\"count\":1,\"time\":1475248822158},\"2016-11-19-3240-14:01-15:00\":{\"count\":1,\"time\":1479536720815},\"2016-10-31-3158-14:01-15:00\":{\"count\":2,\"time\":1477895461284},\"2016-11-30-3256-17:01-18:00\":{\"count\":1,\"time\":1480498415337},\"2016-11-22-3245-14:01-15:00\":{\"count\":1,\"time\":1479796338012},\"2016-12-01-3278-18:01-19:00\":{\"count\":3,\"time\":1480586526555},\"2016-09-30-3067-14:01-15:00\":{\"count\":1,\"time\":1475217078455},\"2016-12-13-3318-00:01-06:00\":{\"count\":1,\"time\":1481559668996},\"2016-11-30-3256-18:01-19:00\":{\"count\":2,\"time\":1480500406151},\"2016-12-13-3318-08:01-09:00\":{\"count\":2,\"time\":1481587317683},\"2016-11-14-3211-12:01-13:00\":{\"count\":3,\"time\":1479096111814},\"2016-10-26-3149-17:31-18:30\":{\"count\":1,\"time\":1477476080531},\"2016-11-06-3138-08:01-09:00\":{\"count\":3,\"time\":1478390533189},\"2016-10-31-3138-00:00-06:30\":{\"count\":1,\"time\":1477843555886},\"2016-11-02-3138-23:01-23:59\":{\"count\":3,\"time\":1478100285515},\"2016-10-29-3158-15:01-16:00\":{\"count\":3,\"time\":1477724685605},\"2016-10-30-3158-23:01-23:59\":{\"count\":3,\"time\":1477840887438},\"2016-10-28-3138-20:01-21:00\":{\"count\":1,\"time\":1477656083870},\"2016-10-17-3096-23:01-23:59\":{\"count\":1,\"time\":1476717714360},\"2016-10-28-3138-23:01-23:59\":{\"count\":2,\"time\":1477668251650},\"2016-10-26-3138-18:01-19:00\":{\"count\":2,\"time\":1477476065063},\"2016-10-17-3096-00:00-06:30\":{\"count\":2,\"time\":1476634049648},\"2016-12-17-3360-09:01-10:00\":{\"count\":3,\"time\":1481936466856},\"2016-12-15-3328-23:01-23:59\":{\"count\":3,\"time\":1481815726325},\"2016-10-14-3090-23:01-23:59\":{\"count\":3,\"time\":1476458678712},\"2016-12-17-3319-23:01-23:59\":{\"count\":1,\"time\":1481988178571}},\"todayHistoryMap\":{\"2016-12-19\":{{\"adId\":-1,\"adType\":-1,\"apiType\":-1}:67,{\"adId\":2683,\"adType\":1,\"apiType\":-1}:3}},\"todayOpenHistoryMap\":{\"2016-12-18\":{{\"adId\":-1,\"adType\":-1,\"apiType\":-1}:11}},\"uninterestedMap\":{\"2580\":{\"time\":1479945260220},\"020-05710-0\":{\"time\":1477895604056},\"1290\":{\"time\":1482235032932},\"2457\":{\"time\":1476426537051},\"2511\":{\"time\":1477668397427},\"2661\":{\"time\":1482073903522},\"2601\":{\"time\":1479568331316}},\"uvMap\":{2598:\"2016-11-30\",2613:\"2016-11-27\",2594:\"2016-11-16\",2661:\"2016-12-17\",2603:\"2016-11-22\",2601:\"2016-11-19\"}}";
////		AdPubCacheRecord cache = JSON.parseObject(cacheStr, AdPubCacheRecord.class);
////		obj.setAdPubCacheRecord(cache);
////
////		w.dealQueueObject(obj);
////
////		System.out.println(obj.getAdPubCacheRecord().toJson());
//		
//		
//		new ClassPathXmlApplicationContext("classpath:servicebiz/locator-baseservice.xml");
//
//		QueueObject objIds = new  QueueObject();
//		objIds.setKey(AdvCache.getUserContentIds("ou7Q_aaa"));	// SHOWN#
//		objIds.setTime(1*24*60*60);
//		List<String> articlesIds = new ArrayList<String>();
//		articlesIds.add("aaaa111111");
//		
//		objIds.setArticleIds(articlesIds);
//		
//		objIds.setQueueType(QueueCacheType.DISPLAY_IDS.getType());
//		
//		
//		Queue.set(objIds);
//		
//		QueueObject obj = Queue.get();
//		if (Queue.size() > 100) {
//			logger.info("Queue.size={}", Queue.size());
//		}
//		if (obj.getRedisIncrKey() != null && !obj.getRedisIncrKey().equals("")) {
//			long time = System.currentTimeMillis();
//			CacheUtil.incrToCache(obj.getRedisIncrKey());
//			time = System.currentTimeMillis() - time;
//			if (time > 30) {
//				TimeLong.info("redisTime=" + time);
//			}
//		} 
//		//缓存信息流文章
//		else if(obj.getQueueType() != null && 
//				obj.getQueueType().equals(QueueCacheType.ARTICLES.getType())) {
//			try {
//				CacheUtil.setNew(obj.getKey(), obj.getTime(), JSON.toJSONString(obj.getUcContent()));
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//			}
//		} 
//		//缓存 用户看过的信息流
//		else if (obj.getQueueType() != null && 
//				obj.getQueueType().equals(QueueCacheType.DISPLAY_IDS.getType())) {
//			try{
//				System.out.println(JSON.toJSONString(obj.getArticleIds()));
//				CacheUtil.setNew(obj.getKey(), obj.getTime(), JSON.toJSONString(obj.getArticleIds()));
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//			}
//		}
//		else {
////			dealQueueObject(obj);
//		}
		
		AdPubCacheRecord cache = new AdPubCacheRecord();
		String str = "{\"cacheRecordMap\":{13694:{\"clickCount\":0,\"dayCountMap\":{\"2018-03-08\":1}},13695:{\"clickCount\":0,\"dayCountMap\":{\"2018-03-08\":1}},13693:{\"clickCount\":0,\"dayCountMap\":{\"2018-03-08\":1}},13524:{\"clickCount\":11,\"dayCountMap\":{\"2018-03-13\":33,\"2018-03-06\":1}},13646:{\"clickCount\":0,\"dayCountMap\":{\"2018-03-06\":8}},13690:{\"clickCount\":1,\"dayCountMap\":{\"2018-03-08\":8}},13309:{\"clickCount\":9,\"dayCountMap\":{\"2018-03-06\":7}},13612:{\"clickCount\":2,\"dayCountMap\":{\"2018-03-06\":8}},13685:{\"clickCount\":0,\"dayCountMap\":{\"2018-03-08\":4}},13708:{\"clickCount\":0,\"dayCountMap\":{\"2018-03-08\":13}},13709:{\"clickCount\":0,\"dayCountMap\":{\"2018-03-08\":33}},13605:{\"clickCount\":4,\"dayCountMap\":{\"2018-03-10\":10,\"2018-03-07\":1,\"2018-03-06\":9}},13681:{\"clickCount\":0,\"dayCountMap\":{\"2018-03-08\":6}}},\"cacheTime\":0,\"displayAdv\":true,\"firstClickMap\":{},\"todayHistoryMap\":{},\"todayNoFeedAdHistoryMap\":{\"2018-03-10\":{13633:0,-1:1,13634:2}},\"todayOpenAdPubTime\":{\"2018-03-12\":{13351:1516867110264,12910:1516864931860},\"2018-03-11\":{13337:1516799445802}},\"todayOpenHistoryMap\":{\"2018-03-13\":{{\"adId\":-1,\"adType\":-1,\"apiType\":-1}:5}},\"uninterestedMap\":{\"13542\":{\"time\":1518321298152},\"13408\":{\"time\":1517455644927},\"13452\":{\"time\":1517620896205},\"13276\":{\"time\":1516360176513},\"11153\":{\"time\":1520926191568},\"13354\":{\"time\":1517026189082},\"13482\":{\"time\":1517643542296},\"13420\":{\"time\":1517553826517},\"13268\":{\"time\":1516377353189},\"13480\":{\"time\":1517649164753}},\"uvMap\":{}}";
		cache = JSONObject.parseObject(str, AdPubCacheRecord.class);
		Map<String, Map<Integer, Integer>> noFeedHistory = cache.getTodayNoFeedAdHistoryMap();
		if(noFeedHistory == null) {
			return;
		}
		Iterator<Map.Entry<String, Map<Integer, Integer>>> it = noFeedHistory.entrySet().iterator();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -2);// 把日期往后增加七天.整数往后推,负数往前移动
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(calendar.getTime()); // 这个时间就是日期往前推2天的结果
		
		while(it.hasNext()) {
			Map.Entry<String, Map<Integer, Integer>> entry = it.next();
			String dayStr = entry.getKey();
			// 存储日期在2天之内
			if (dateStr.compareTo(dayStr) <= 0) {
				continue;
			} else {
				// 2天之外的清理掉
				it.remove();
			}
		}
		
		System.out.println(JSONObject.toJSONString(cache));
		
	}


	
	
}
