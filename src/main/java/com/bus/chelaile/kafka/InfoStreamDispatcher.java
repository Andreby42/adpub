package com.bus.chelaile.kafka;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.ToutiaoHelp;
import com.bus.chelaile.linkActive.LinkActiveHelp;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;
import com.chelaile.logcenter2.sdk.api.Consumer;
import com.chelaile.logcenter2.sdk.api.LcFactory;
import com.chelaile.logcenter2.sdk.kafka.consumer.ConsumerCallbackWorker;

/**
 * Created by tingx on 2016/12/20.
 */
public class InfoStreamDispatcher {

	@Autowired
	private ToutiaoHelp toutiaoHelp;
	@Autowired
	private LinkActiveHelp linkActiveHelp;

	private static final String TOPIC_ID = "nginx_log";
	private static final String GROUP_ID = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "group_id",
			"info_flow_log");

	public static final Object KAFKA_LOCK = new Object();
	public volatile static boolean kafkaStarted = false;
	public static final Logger logger = LoggerFactory.getLogger(InfoStreamDispatcher.class);

	public static ExecutorService adClickLogExec = Executors.newFixedThreadPool(5); // 固定5个线程执行解析的任务。

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
		if (str.contains(Constants.AD_DOMAIN_NAME) && str.contains(Constants.TOUTIAO_CLICK_KEYWORD)) { // 头条点击
			return Constants.ROW_TOUTIAO_CLICK;
		} 
//		else if ((str.contains(Constants.AD_DOMAIN_NAME) || str.contains(Constants.REDIRECT_DOMAIN_NAME))
//				&& (str.contains(Constants.PARAM_AD_ID) && !str.contains(Constants.FOR_DEVELOP_EXHIBIT))) { 									// 广告点击
//			return Constants.ROW_ADV_CLICK;
//		} 
//		else if (str.contains(Constants.LINEDETAIL)) {
//			return Constants.ROW_LINEDETAIL;
//		}
		else if (str.contains(maidian_log) && str.contains(Constants.APP_INFO_LOG)) { 	// 用户安装了哪些app的埋点
			return Constants.ROW_APP_INFO;
		} 
		else {
			return Constants.ROW_SKIP;
		}
	}


	// 处理kafka日志（已被过滤过的）
	private void processLog(String line, int filterCode) {
//		if(filterCode == Constants.ROW_LINEDETAIL) {
//			InfoStreamHelp.analysisLineDetail(line);
//		}
//		else 
			if (filterCode == Constants.ROW_TOUTIAO_CLICK) {
//			logger.info("<Info-Stream top-k> 收到头条点击点击日志：{}", line);
			try {
				toutiaoHelp.handleToutiaoClick(line);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
		} 
		else if (filterCode == Constants.ROW_ADV_CLICK) {
			InfoStreamHelp.analysisClick(line);
		}
//		else if (filterCode == Constants.ROW_APP_INFO) {
//			linkActiveHelp.analysisMaidian(line);
//		} 
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
							adClickLogExec.submit(new Runnable() {
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
//		 InfoStreamDispatcher st = new InfoStreamDispatcher();
////		 st.runGetTopKArticles();
//		// st.readKafka();
//
//		 String
//		 str = "line=<190>Apr 14 17:41:39 web6 nginx: 123.147.244.13 |# - |# 2017-04-14 17:41:39 |# GET /?link=http://m.lemall.com/cn/sale/hongse414/index.html?cps_id=QT_sspmj_chelaile_youshangjiaotp_zh&deviceType=m1 metal&advId=3028&adtype=05&lng=106.53626518425347&udid=44785918-549e-4c2a-b99e-1c2d0b78e2a3&nw=MOBILE_LTE&lat=29.584922334963398&ip=123.147.244.13&utm_medium=floating&adv_id=3028&last_src=app_qq_sj&s=android&stats_referer=lineDetail&push_open=1&stats_act=auto_refresh&userId=unknown&provider_id=1&geo_lt=5&timestamp=1492158320493&geo_lat=29.578926&line_id=023-319-1&vc=78&sv=5.1&v=3.30.0&imei=868024027752105&udid=44785918-549e-4c2a-b99e-1c2d0b78e2a3&platform_v=22&utm_source=app_linedetail&stn_name=大庙&cityId=003&adv_type=5&ad_switch=63&geo_type=gcj&wifi_open=0&mac=68:3e:34:66:b2:08&deviceType=m1 metal&lchsrc=icon&stats_order=1-1&nw=MOBILE_LTE&AndroidID=3dcece3350d1d4f4&api_type=0&stn_order=2&geo_lac=25.0&language=1&first_src=app_meizhu_store&geo_lng=106.529763 HTTP/1.1 |# 302 |# 0.000 |# 264 |# - |# Chelaile/3.30.0 Duiba/1.0.7 Mozilla/5.0 (Linux; Android 5.1; m1 metal Build/LMY47I) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.127 Mobile Safari/537.36 |# - |# ad.chelaile.net.cn |# - |# -";
//		 int filterCode = st.filterContent(str.trim());
//		 System.out.println(filterCode);
//		 st.processLog(str, filterCode);
//		 
//		
//		 CacheUtil.initClient();
//		 String msg =
//		 "<190>Apr 13 15:53:34 web4 nginx: 117.136.40.230 |# - |# 2017-04-13 15:53:34 |# GET /realtimelog?<ADV_EXHIBIT>adv_id:3025+%7C%23+s:android+%7C%23+last_src:app_huawei_store+%7C%23+push_open:1+%7C%23+adv_type:1+%7C%23+userId:unknown+%7C%23+provider_id:1+%7C%23+deviceType:HUAWEI+RIO-TL00+%7C%23+mac:74%3Aa5%3A28%3A3d%3Afb%3Aaa+%7C%23+wifi_open:1+%7C%23+lchsrc:icon+%7C%23+nw:MOBILE_LTE+%7C%23+AndroidID:34933e8aec55710+%7C%23+sv:6.0.1+%7C%23+vc:78+%7C%23+v:3.30.0+%7C%23+imei:867119024362584+%7C%23+udid:ac853978-a760-4ddb-8e83-bf23cdef2734+%7C%23+language:1+%7C%23+first_src:app_huawei_store+%7C%23+cityId:014 HTTP/1.1 |# 200 |# 0.000 |# 67 |# - |# Dalvik/2.1.0 (Linux; U; Android 6.0.1; HUAWEI RIO-TL00 Build/HuaweiRIO-TL00) |# - |# logs.chelaile.net.cn |# - |# -";
//		 st.processLog(msg, 5);

//		String amc = "20:5d:47:6a:ea:ec";
//		System.out.println(amc.replace(":", "").toUpperCase());
//		System.out.println(DigestUtils.md5Hex(amc.replace(":", "")));
//		System.out.println(DigestUtils.md5Hex(amc.replace(":", "").toUpperCase()));
		System.out.println(System.currentTimeMillis());
	}
}
