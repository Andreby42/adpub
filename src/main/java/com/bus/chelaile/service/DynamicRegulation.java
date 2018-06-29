package com.bus.chelaile.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bus.chelaile.common.Constants;
import com.bus.chelaile.koubei.CouponService;
import com.bus.chelaile.thread.KoubeiThread;
import com.bus.chelaile.thread.UpdateSendPV;
import com.bus.chelaile.koubei.KBUpdateCouponStatusThread;

public class DynamicRegulation {
	@Autowired
	private CouponService couponService;
	
	private static final Logger logger = LoggerFactory.getLogger(DynamicRegulation.class);

	// 临时存放广告发送的pv量,按照ruleId存放
	public static ConcurrentHashMap<String, AtomicInteger> advSendPV = new ConcurrentHashMap<String, AtomicInteger>();
	private static boolean hasStartThread = false;

	/*
	 * 更新发送pv到redis线程
	 */
	public void threadUpdateTotalPV() {
		if (!hasStartThread) {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

//			// 启动发送pv到redis
//			Runnable runnable = new UpdateSendPV();
//			// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
//			service.scheduleWithFixedDelay(runnable, 10, 3, TimeUnit.SECONDS);
//			logger.info("启动发送pv到redis线程");
//			
//			// 启动计算pv投放比例因子
//			Runnable calculateThread = new CalculatePerMinCount();
//			service.scheduleWithFixedDelay(calculateThread, 3, 20, TimeUnit.MINUTES);
//			logger.info("启动计算pv投放比例因子");
			
//			if (Constants.IS_FLOW) {
//				// 启动缓存文章内容
//				Runnable qMThread = new DownArticles(wuliToutiaoHelp);
//				int interval = 600;
//				if (Constants.ISTEST) {
//					interval = 6000;
//				}
//				service.scheduleWithFixedDelay(qMThread, 30, interval, TimeUnit.SECONDS);
//				
////				// 启动缓存话题list
////				Runnable feedCacheThread = new FeedCacheThread();
////				service.scheduleWithFixedDelay(feedCacheThread, 30, 10, TimeUnit.SECONDS);
//				
//			}
			// 缓存口碑券
//			if(Constants.IS_CACHE_KOUBEI) {
//				Runnable koubeiThread = new KoubeiThread(couponService);
//				service.scheduleWithFixedDelay(koubeiThread, 600, 7200, TimeUnit.SECONDS);
//			}
			// 更新口碑券状态
			if(Constants.IS_CACHE_KOUBEI) {
				Runnable kBUpdateCouponStatusThread = new KBUpdateCouponStatusThread(couponService);
				service.scheduleWithFixedDelay(kBUpdateCouponStatusThread, 3, 24, TimeUnit.HOURS);
			}
			
			hasStartThread = true;
		}
	}

	public static void threadCalculate() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		Runnable runnable = new UpdateSendPV();
		// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		service.scheduleAtFixedRate(runnable, 10, 3, TimeUnit.SECONDS);
	}

	/*
	 * 计数 +1
	 */
	public static void IncValueSedPV(int advId, String ruleId) {
		String key = advId + "#" + ruleId;
		AtomicInteger count = advSendPV.get(key);

		if (count == null) {
			synchronized (advSendPV) {
				if (!advSendPV.containsKey(key)) {
					count = new AtomicInteger(0);
					advSendPV.put(key, count);
				} else {
					count = advSendPV.get(key);
				}
			}
		}

		count.incrementAndGet();

	}

	/*
	 * 归零
	 */
	public static void zeroSendPV(String advIdRuleId) {
		AtomicInteger count = advSendPV.get(advIdRuleId);

		if (count == null) {
			synchronized (advSendPV) {
				logger.error("归零的时候出现空计数， advIdRuleId={}", advIdRuleId);
				count = new AtomicInteger(0);
				advSendPV.put(advIdRuleId, count);
				return;
			}
		}

		count.set(0);
	}

}
