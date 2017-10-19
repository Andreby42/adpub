package com.bus.chelaile.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.service.DynamicRegulation;

/*
 * 定时计算当前pv投放量，
 */
public class UpdateSendPV implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(UpdateSendPV.class);

	@Override
	public void run() {
		try {
			if (CacheUtil.acquireLock()) { // 获取锁成功，才能进行操作
//				logger.info("成功获取锁");
				long time1 = System.currentTimeMillis();
				for (String advIdRuleId : DynamicRegulation.advSendPV.keySet()) {
					String key = AdvCache.getTotalSedPV(advIdRuleId);
					// Object countOld = CacheUtil.getFromRedis(key);
					int inc = DynamicRegulation.advSendPV.get(advIdRuleId).get(); // 增量
					if (inc == 0) { // inc == 0 无需更新
						continue;
					}
					DynamicRegulation.zeroSendPV(advIdRuleId); // 归零

					CacheUtil.redisIncrBy(key, inc, Constants.ONE_DAY_TIME); // redis存储增加inc
					
					logger.info("遍历广告，进行pv量更新到redis操作  ,advIdRuleId={}, inc={}, newCount={}", advIdRuleId, inc,
							CacheUtil.getFromRedis(key));
					logger.info("耗时： {}", System.currentTimeMillis() - time1);
				}

				CacheUtil.releaseLock(); // 释放锁
			} else {
//				logger.info("获取锁失败！");
				Thread.sleep(150);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新PV到redis出错！" + e.getMessage(), e);
		}
	}

}
