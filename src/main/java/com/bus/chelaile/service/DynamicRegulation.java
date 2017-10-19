package com.bus.chelaile.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.thread.CalculatePerMinCount;
import com.bus.chelaile.thread.UpdateSendPV;

public class DynamicRegulation {

	private static final Logger logger = LoggerFactory.getLogger(DynamicRegulation.class);

	// 临时存放广告发送的pv量,按照ruleId存放
	public static ConcurrentHashMap<String, AtomicInteger> advSendPV = new ConcurrentHashMap<String, AtomicInteger>();
	private static boolean hasStartThread = false;

	/*
	 * 更新发送pv到redis线程
	 */
	public static void threadUpdateTotalPV() {
		if (!hasStartThread) {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

			// 启动发送pv到redis
			Runnable runnable = new UpdateSendPV();
			// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
			service.scheduleWithFixedDelay(runnable, 10, 3, TimeUnit.SECONDS);
			logger.info("启动发送pv到redis线程");
			System.out.println("启动发送pv到redis线程");
			
			// 启动计算pv投放比例因子
			Runnable calculateThread = new CalculatePerMinCount();
			service.scheduleWithFixedDelay(calculateThread, 3, 20, TimeUnit.MINUTES);
			logger.info("启动计算pv投放比例因子");
			System.out.println("启动计算pv投放比例因子");

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
