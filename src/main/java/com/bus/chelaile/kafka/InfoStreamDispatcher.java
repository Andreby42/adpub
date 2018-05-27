package com.bus.chelaile.kafka;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.ToutiaoHelp;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.service.RecordManager;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;
import com.chelaile.logcenter2.sdk.api.Consumer;
import com.chelaile.logcenter2.sdk.api.LcFactory;
import com.chelaile.logcenter2.sdk.kafka.consumer.ConsumerCallbackWorker;

/**
 * Created by tingx on 2016/12/20.
 */
/**
 *  2018-05-15 不再用了
 * @author Administrator
 *
 */
public class InfoStreamDispatcher {

	@Autowired
	private ToutiaoHelp toutiaoHelp;

	private static final String TOPIC_ID = "nginx_log";
	private static final String GROUP_ID = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "group_id",
			"info_flow_log");

	private static final Object KAFKA_LOCK = new Object();
	private volatile static boolean kafkaStarted = false;
	private static final Logger logger = LoggerFactory.getLogger(InfoStreamDispatcher.class);

	private static ExecutorService logAnalysisExec = Executors.newFixedThreadPool(5); // 固定5个线程执行解析的任务。

	public InfoStreamDispatcher() {
	}

	// return 0: 不需要处理的日志
	// return 1: 用户请求了信息流，会有展示
	// return 2: 用户点击日志
	private int filterContent(String str) {
		
		String maidian_log = Constants.MAIDIAN_LOG;
		if (Constants.ISTEST) {
			maidian_log = Constants.TEST_MAIDIAN_LOG;
		}
//		if(str.contains(maidian_log) && str.contains(Constants.ADV_EXHIBIT) && str.contains(Constants.OPEN_ADV_KEYWORD)) {
//			return Constants.ROW_OPEN_ADV_EXHIBIT;
//		}
//		else 
		    if(str.contains(maidian_log) && str.contains(Constants.ADV_CLICK) && str.contains(Constants.WXAPP_SRC)) {
		    return Constants.ROW_WXAPP_ADV_CLICK_MAIDIAN;
		}
			
//		else if (str.contains(Constants.LINEDETAIL)) {
//			return Constants.ROW_LINEDETAIL;
//		}
//		else if (str.contains(maidian_log) && str.contains(Constants.APP_INFO_LOG)) { 	// 用户安装了哪些app的埋点
//			return Constants.ROW_APP_INFO;
//		} 
		else {
			return Constants.ROW_SKIP;
		}
	}


	// 处理kafka日志（已被过滤过的）
	private void processLog(String line, int filterCode) {
		// if(filterCode == Constants.ROW_LINEDETAIL) {
		// InfoStreamHelp.analysisLineDetail(line);
		// }
		// else
		if (filterCode == Constants.ROW_TOUTIAO_CLICK) {
//			logger.info("<Info-Stream top-k> 收到头条点击点击日志：{}", line);
			try {
				toutiaoHelp.handleToutiaoClick(line);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
		} else if (filterCode == Constants.ROW_OPEN_ADV_EXHIBIT) {
			logger.info("<Info-Stream> 收到开屏广告展示日志：{}", line);
			analysisOpenAdvExhibit(line);
		} else if (filterCode == Constants.ROW_WXAPP_ADV_CLICK_MAIDIAN) {
		    logger.info("<Info-Stream> 收到小程序的广告点击埋点日志：{}", line);
		    InfoStreamHelp.analysisWXAppClick(line);
		}
		// else if (filterCode == Constants.ROW_APP_INFO) {
		// linkActiveHelp.analysisMaidian(line);
		// }
	}


	public void readKafka() {
		logger.info("TOPIC_ID="+TOPIC_ID+" GROUP_ID="+GROUP_ID);
		synchronized (KAFKA_LOCK) {
			if (kafkaStarted) {
				logger.warn("<Info-Stream top-k>: InfoStreamDispatcher信息流分发者客户端已经启动");
				return;
			}
			try {
				LcFactory lf = LcFactory.getInstance(null);
				Properties props = new Properties();
				// zookeeper地址
				props.put("zookeeper.connect", "nkfk1:2181,nkfk2:3181,nkfk2:2181/kafka");
				// 消费组
				props.put("group.id", GROUP_ID);
				//
				props.put("zookeeper.session.timeout.ms", "8000");
				props.put("zookeeper.sync.time.ms", "1000");
				props.put("auto.commit.interval.ms", "1000");
				// 新的group 从0开始消费
				props.put("auto.offset.reset", "largest");
				props.put("rebalance.backoff.ms", "4000");

				Consumer cm = lf.newConsumer(TOPIC_ID, GROUP_ID, props);
				
				cm.receive(new ConsumerCallbackWorker() {
				@Override
				public void callback(byte[] bt) {
					try {
						final String log = new String(bt, "UTF-8");
						final int filterCode = filterContent(log.trim());
						if (filterCode != Constants.ROW_SKIP) {
							logAnalysisExec.submit(new Runnable() {
								@Override
								public void run() {
									processLog(log, filterCode);
								}
							});
						}
					} catch (Throwable e) {
						logger.error("<Info-Stream top-k>: KafkaUtil Consumer异常： " + e.getMessage(), e);
					}
				}
			});
			kafkaStarted = true;
			logger.info("<Info-Stream top-k>: InfoStreamDispatcher信息流分发者客户端成功启动");
			
			} catch (Exception e) {
				logger.error("<Info-Stream top-k>: 启动Kafka客户端错误：" + e.getMessage(), e);
				e.printStackTrace();
			} finally {
				logger.info("<Info-Stream top-k>: InfoStreamDispatcher信息流分发者客户端退出");
			}
		}
	}

	/**
	 * 解析开屏广告展示埋点日志
	 * 并且记录进缓存
	 * @param line
	 */
	private void analysisOpenAdvExhibit(String line) {
		String[] segs = line.split(" \\|# ");
		String content = segs[3].trim();
		int endIdx = content.lastIndexOf(" ");
		content = content.substring(0, endIdx);
		String encodedURL = null;
		try {
			encodedURL = URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		int index = encodedURL.indexOf("ADV_EXHIBIT");
//		System.out.println(encodedURL.substring(index + 12));

		Map<String, String> params = paramsAnalysis(encodedURL.substring(index + 12));
		if (params != null) {
			String udid = params.get("udid");
			String advId = params.get("adv_id");
			if(udid == null || advId == null) {
				logger.info("广告为空 line={}", line);
				return;
			}
			
			if(StaticAds.allAds.get(advId) == null) {
//				if(! Constants.ISTEST) {	// 线上需要打印这种情况，测试无需
					logger.error("缓存中未发现广告,advId={}, line={}", advId, line);
//				}
				return;
			}
			
			// 记录缓存， 开屏广告‘展示’|‘发送’ + 1
			logger.info("更新开屏 udid={}, advId={}", udid, advId);
			AdPubCacheRecord cacheRecord = null;
			try {
				cacheRecord = AdvCache.getAdPubRecordFromCache(udid, ShowType.DOUBLE_COLUMN.getType());
			} catch (Exception e) {
				e.printStackTrace();
				cacheRecord = new AdPubCacheRecord();
			}
			logger.info("更新开屏广告前***， cacheRecord={}", JSONObject.toJSONString(cacheRecord));
			cacheRecord.buildAdPubCacheRecord(Integer.parseInt(advId));
			cacheRecord.setOpenAdHistory(new AdCategory(Integer.parseInt(advId), 1, -1));
			cacheRecord.setAndUpdateOpenAdPubTime(Integer.parseInt(advId));
			RecordManager.recordAdd(udid, ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
			logger.info("更新开屏广告后###， cacheRecord={}", JSONObject.toJSONString(cacheRecord));
		}
	}
	
	
	private static Map<String, String> paramsAnalysis(String url) {
		Map<String, String> params = New.hashMap();
		String entrys[] = url.split(" \\|# ");
		for (String s : entrys) {
			String[] maps = s.split(":");
			try {
				if (maps != null && maps.length >= 2)
					params.put(maps[0], URLDecoder.decode(maps[1], "UTF-8"));
			} catch (Exception e) {
				logger.error("参数解析出错: map={}", maps.toString());
				e.printStackTrace();
			}
		}
		return params;
	}
//	public void getTopKArticles() {
//		logger.info("<Info-Stream top-k>: Get top k articles task started");
//		int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//		if (curHour < earliestRefreshHour || curHour > latestRefreshHour) {
//			logger.info("<Info-Stream top-k>: Out of the given time interval");
//			// 每天零点清空用户屏蔽列表和当日最受欢迎的文章列表
//			// 但是由于本方法所依附的线程每3600秒运行一次，因此清空的时间不是零点整
//			// 而是0:00到0:59中的某一时刻
//			if (curHour == 0) {
//				wholeDayVisits.clear();
//				blacklist.clear();
//			}
//			return;
//		}
//		// 清除旧的top文章，换成新的
//		curDayPopularArticles.clear();
//		curHourPopularArticles.clear();
//		Map<String, Integer> wholeDayVisitsSnapshot = new HashMap<>(wholeDayVisits);
//		fillInAdIdSetFromMap(wholeDayVisitsSnapshot, curDayPopularArticles);
//		CacheUtil.setNew("WHOLE_DAY", curDayCacheExpireTime, JSON.toJSONString(curDayPopularArticles));
//		for (UcContent uc : curDayPopularArticles) {
//			AnalysisLog.info("<Info-Stream top-k>: Top K article id (whole day): {} ({})", uc.getId(), uc.getTitle());
//		}
//
//		Map<String, Integer> currentHourVisitsSnapshot = new HashMap<>(currentHourVisits);
//		fillInAdIdSetFromMap(currentHourVisitsSnapshot, curHourPopularArticles);
//		CacheUtil.setNew("CUR_HOUR", curHourCacheExpireTime, JSON.toJSONString(curHourPopularArticles));
//		for (UcContent uc : curHourPopularArticles) {
//			AnalysisLog.info("<Info-Stream top-k>: Top K article id (current hour): {} ({}) ({})", uc.getId(),
//					uc.getTitle(), uc.getUrl());
//		}
//		currentHourVisits.clear();
//
//		// 避免产生同步问题，所以产生一个快照
//		HashSet<String> copiedShownUsers;
//		copiedShownUsers = new HashSet<>(shownUsers);
//		shownUsers.clear();
//
//		// 对每个展示过广告的用户，组成key去OCS看给ta展示过什么新闻
//		// 展示超过一定次数的加入黑名单
//		HashMap<String, HashSet<String>> blacklistSnapshot = new HashMap<>(blacklist);
//
//		int i = 0;
//		for (String userId : copiedShownUsers) {
//			// 组成key
//			HashSet<String> curUserBlacklist = blacklistSnapshot.get(userId);
//			if (curUserBlacklist == null) {
//				curUserBlacklist = new HashSet<>();
//			}
//
//			// 用户之前的黑名单
//			ListIdsCache blockIds = new ListIdsCache(); // 之前不予展示的id
//			String blockStr = (String) CacheUtil.getNew("BLOCK#" + userId);
//			if (blockStr != null) {
//				blockIds = JSON.parseObject(blockStr, ListIdsCache.class);
//			}
//			curUserBlacklist.addAll(blockIds.getIdList());
//			blockIds.setIdList(new ArrayList<String>(curUserBlacklist));
//
//			// 缓存用户看过的 文章黑名单 ids
//			QueueObject objIds = new QueueObject();
//			objIds.setKey(AdvCache.getUserBlockContentIds(userId)); // BLOCK#
//			objIds.setTime(curDayCacheExpireTime);
//			objIds.setArticleIds(blockIds);
//			objIds.setQueueType(QueueCacheType.DISPLAY_IDS);
//			Queue.set(objIds);
//			i++;
//		}
//		logger.info("<Info-Stream top-k>: Block info for {} users is put into cache", i);
//	}

//	public void runGetTopKArticles() {
//		synchronized (TOPK_LOCK) {
//			if (topKStarted) {
//				logger.warn("Top k article selector has been started.");
//				return;
//			} else {
//				logger.info("Starting top k article selector thread...");
//			}
//		}
//
//		new Thread() {
//			@Override
//			public void run() {
//				// The comment below is for Intellij IDEA
//				// noinspection InfiniteLoopStatement
//				while (true) {
//					try {
//						getTopKArticles();
//						topKStarted = true;
//						Thread.sleep(curHourCacheExpireTime * 1000);
//					} catch (InterruptedException e) {
//						topKStarted = false;
//						logger.error("<Info-Stream top-k>: 取前k条新闻错误（中断异常）：" + e.getMessage(), e);
//						e.printStackTrace();
//					} catch (Exception e) {
//						topKStarted = false;
//						logger.error("<Info-Stream top-k>: 取前k条新闻错误（其他异常）：" + e.getMessage(), e);
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();
//	}

//	private void fillInAdIdSetFromMap(Map<String, Integer> counter, HashSet<UcContent> adObjSet) {
//		PriorityQueue<Map.Entry<String, Integer>> maxHeap = new PriorityQueue<>(topK,
//				new Comparator<Map.Entry<String, Integer>>() {
//					public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
//						if (a.getValue() < b.getValue()) {
//							return 1;
//						} else if (a.getValue().equals(b.getValue())) {
//							return 0;
//						} else {
//							return -1;
//						}
//					}
//				});
//		for (Map.Entry<String, Integer> entry : counter.entrySet()) {
//			maxHeap.add(entry);
//		}
//		Map.Entry<String, Integer> item = null;
//		int nPolledItems = 0;
//		while (!maxHeap.isEmpty() && nPolledItems < topK) {
//			item = maxHeap.poll();
//			String adId = item.getKey();
//			String content = (String) CacheUtil.getNew("ARTICLE#" + adId);
//			// 只有在top文章被缓存的时候才算数
//			if (content != null) {
//				adObjSet.add(JSON.parseObject(content, UcContent.class));
//				nPolledItems++;
//			}
//		}
//		// 看是否有没被选中的文章和最后拿出的top文章有相同的访问次数
//		// 也就是我们要拿并列第K多访问的文章
//		if (item != null) {
//			for (Map.Entry<String, Integer> entry : counter.entrySet()) {
//				if (entry.getValue().equals(item.getValue())) {
//					String content = (String) CacheUtil.getNew("ARTICLE#" + entry.getKey());
//					if (content != null) {
//						adObjSet.add(JSON.parseObject(content, UcContent.class));
//					}
//				}
//			}
//		}
//	}

	public static void main(String[] args) {
		//
		String str = "<190>Dec 28 17:18:25 web1 nginx: 117.136.38.58 |# - |# 2017-12-28 17:18:25 |# GET /realtimelog?%3CADV_EXHIBIT%3Eadv_id:12092+%7C%23+s:android+%7C%23+last_src:dev_alpha+%7C%23+load_time1:400+%7C%23+push_open:1+%7C%23+userId:unknown+%7C%23+provider_id:1+%7C%23+geo_lt:4+%7C%23+geo_lat:39.996124+%7C%23+sv:7.1.1+%7C%23+vc:94+%7C%23+v:3.43.0_20171221+%7C%23+secret:9cb51a5177224a52abc9a279be83dee2+%7C%23+imei:866822030825525+%7C%23+udid:fb6d0547-b3ba-435b-ba29-001a1bbe261b+%7C%23+cityId:027+%7C%23+adv_type:1+%7C%23+load_time2:15+%7C%23+wifi_open:0+%7C%23+deviceType:MI+6+%7C%23+mac:02%3A00%3A00%3A00%3A00%3A00+%7C%23+geo_type:gcj+%7C%23+lchsrc:icon+%7C%23+nw:MOBILE_LTE+%7C%23+AndroidID:30c3439cc1a1621c+%7C%23+geo_lac:66.0+%7C%23+accountId:4904321+%7C%23+language:1+%7C%23+first_src:app_xiaomi_store+%7C%23+geo_lng:116.40994 HTTP/1.1 |# 200 |# 0.000 |# 67 |# - |# Dalvik/2.1.0 (Linux; U; Android 7.1.1; MI 6 MIUI/V9.0.6.0.NCACNEI) |# - |# dev.logs.chelaile.net.cn |# - |# - |# - |# https";

		String[] segs = str.split(" \\|# ");
		String content = segs[3].trim();
		int endIdx = content.lastIndexOf(" ");
		content = content.substring(0, endIdx);
		String encodedURL = null;
		try {
			encodedURL = URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		int index = encodedURL.indexOf("ADV_EXHIBIT");
//		System.out.println(encodedURL.substring(index + 12));

		Map<String, String> params = paramsAnalysis(encodedURL.substring(index + 12));
		if (params != null) {
			String udid = params.get("udid");
			System.out.println(udid);
			System.out.println(params.get("adv_id"));
			System.out.println(params.get("geo_lng"));
		}
	}
}
