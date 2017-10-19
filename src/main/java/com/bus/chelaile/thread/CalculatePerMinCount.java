package com.bus.chelaile.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.service.DynamicRegulation;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.util.config.PropertiesUtils;

public class CalculatePerMinCount implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(CalculatePerMinCount.class);
//	private static double rateStandard = Double.parseDouble(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
//			"rate.Standard", "0.03"));
	private static double rateStandardAdd = Double.parseDouble(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"rate.Standard.add", "0.01"));

	public static ConcurrentHashMap<String, Double> advPVRate = new ConcurrentHashMap<String, Double>();
	public static ConcurrentHashMap<String, Double> advCTRRate = new ConcurrentHashMap<String, Double>();

	@Override
	public void run() {
		try {
			logger.info("计算投放比例因子");
			for (String advIdRuleId : DynamicRegulation.advSendPV.keySet()) {
				if (!StaticAds.minuteNumber.containsKey(advIdRuleId)) {
					logger.error("按分钟投放没有找到该计数广告： advIdRuleId={}", advIdRuleId);
					continue;
				}
				
				if (!advPVRate.containsKey(advIdRuleId)) {
					advPVRate.put(advIdRuleId, 1.0);
				}
				if (!advCTRRate.containsKey(advIdRuleId)) {
					// advIdRuleId = advId + "#" + ruleId
					double rateStandard = Double.parseDouble(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
							"rate.Standard." + advIdRuleId, "0.03"));
					advCTRRate.put(advIdRuleId, rateStandard);
				}
				
				String key = AdvCache.getTotalSedPV(advIdRuleId);
				Object currentNumber = CacheUtil.getFromRedis(key); // redis获取当前投放PV量
				if (currentNumber != null) {
					int cuNumber = Integer.parseInt((String) currentNumber);

					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
					String minuteStr = sdf.format(new Date());

					int planNumber = 0;	 //接下来的时间里计划投放的次数
					int planTotal = 0; //计划总投放次数
					for (Entry<String, Integer> entry : StaticAds.minuteNumber.get(advIdRuleId).entrySet()) {
						if (entry.getKey().compareTo(minuteStr) >= 0) {
							planNumber += entry.getValue();
						}
						planTotal += entry.getValue();
					}

					if (planNumber != 0 && cuNumber != 0) {
						// TEST log TODO
						logger.info("遍历广告，进行pv量投放比例因子计算   ,advIdRuleId={}, (截止当前投放次数)currentNumber={}, (接下里时间里计划次数)planNumber={}",
								advIdRuleId, cuNumber, planNumber);
						
						// 修改按分钟投放调整因子
						if(planTotal > cuNumber && planNumber > 0) {
							advPVRate.put(advIdRuleId, (double) (planTotal - cuNumber) / planNumber);
						} 
//						else {
//							advPVRate.put(advIdRuleId, 0.00001);
//						}
						
						// 修改按点击率投放的阈值
						if (cuNumber > (planTotal - planNumber) * 1.1) {	// 实际投放过多，需要增加阈值
							advCTRRate.put(advIdRuleId, advCTRRate.get(advIdRuleId) + rateStandardAdd);
							logger.info("实际投放过多，advIdRuleId={}, 修改后阈值是：{}", advIdRuleId, advCTRRate.get(advIdRuleId));
						} else if (cuNumber < (planTotal - planNumber) / 1.1) {
							advCTRRate.put(advIdRuleId, advCTRRate.get(advIdRuleId) * 0.5);
							logger.info("实际投放过少, advIdRuleId={}, 修改后阈值是：{}", advIdRuleId, advCTRRate.get(advIdRuleId));
						}
					}
				}
			}
			
			for (String s : advPVRate.keySet()) {
				logger.info("修正后的投放pv因子是： key={}, rate={}", s, advPVRate.get(s));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("计算投放pv比例因子出错！ " + e.getMessage(), e);
		}

	}

	/*
	 * 获取pv投放调控因子
	 */
	public static double getPVRate(String advIdRuleId) {
		if (advPVRate.containsKey(advIdRuleId)) {
			return advPVRate.get(advIdRuleId);
		} else {
			return 1.0;
		}
	}
	
	/*
	 * 获取CTR点击率的阈值
	 */
	public static double getCTRRate(String advIdRuleId) {
		if (advCTRRate.containsKey(advIdRuleId)) {
			return advCTRRate.get(advIdRuleId);
		} else {
			return Double.parseDouble(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
					"rate.Standard." + advIdRuleId, "0.03"));
		}
	}
	
	public static void main(String[] args) {
		String a = "12:12";
		String b = "23:59";
		System.out.println(a.compareTo(b));

		int m = 111;
		int n = 2232423;
		System.out.println(m / n);
		System.out.println((double) m / n);
	}

}
