package com.bus.chelaile.thread;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.DiscountInfo;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.koubei.CouponInfo;
import com.bus.chelaile.koubei.CouponService;
import com.bus.chelaile.koubei.KBUtil;
import com.bus.chelaile.util.BaseServiceUtils;
import com.bus.chelaile.util.GPSConvert;
import com.bus.chelaile.util.model.StopInfo;

public class KoubeiThread implements Runnable {
	
	private CouponService couponService;
	private static final Logger logger = LoggerFactory.getLogger(KoubeiThread.class);
	
	public KoubeiThread(CouponService couponService) {
		this.setCouponService(couponService);
	}
	
	@Override
	public void run() {
		try {
			logger.info("刷新口碑券缓存~ ");
			String cityId = "034";
			List<String> lines = BaseServiceUtils.getAllLines(cityId);
			for(String lineId : lines) {
				List<StopInfo> stopInfos = BaseServiceUtils.getStopInfo(lineId, cityId);
				for(StopInfo stopInfo : stopInfos) {
					logger.info("拉取站点推荐优惠券，lineId={}, stopName={}, lng={}, lat={}", 
							lineId, stopInfo.getStationName(), stopInfo.getLng(), stopInfo.getLat());
					// 经纬度转换， wgs TO  gcj
					double[] gcjs = GPSConvert.wgs2gcj(stopInfo.getLng(), stopInfo.getLat());
					//获取周边推荐口碑券
					List<DiscountInfo> disCounts = couponService.getDiscounts(cityId, String.valueOf(gcjs[0]), String.valueOf(gcjs[1]), 
							null, null, stopInfo.getStationName());
					// 取第一条，构建CouponInfo结构体，存入ocs中
					if(disCounts != null && disCounts.size() > 0) {
						CouponInfo couponInfo = new CouponInfo(disCounts.get(0).getItemId(), disCounts.get(0).getItemName(), disCounts.get(0).getDistance(),
								disCounts.get(0).getApplyCondition(), disCounts.get(0).getShopName(), disCounts.get(0).getImageUrl());
						String couponInfoStr = JSONObject.toJSONString(couponInfo);
						logger.info("获取到的口碑券信息: stopName={}, disCount={}", stopInfo.getStationName(), couponInfoStr);
						String key = KBUtil.getKbCouponOcsKey(cityId, stopInfo.getStationName());
//						CacheUtil.set(key, Constants.ONE_DAY_TIME, couponInfoStr);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("刷新口碑券缓存出错！ " + e.getMessage(), e);
		}
	}

	public CouponService getCouponService() {
		return couponService;
	}

	public void setCouponService(CouponService couponService) {
		this.couponService = couponService;
	}
}
