package com.bus.chelaile.thread;

import java.util.List;

import com.bus.chelaile.koubei.CouponType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.DiscountInfo;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.koubei.CouponInfo;
import com.bus.chelaile.koubei.CouponService;
import com.bus.chelaile.koubei.KBUtil;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.BaseServiceUtils;
import com.bus.chelaile.util.GPSConvert;
import com.bus.chelaile.util.config.PropertiesUtils;
import com.bus.chelaile.util.model.StopInfo;

public class KoubeiThread implements Runnable {

	private CouponService couponService;
	private static final Logger logger = LoggerFactory.getLogger(KoubeiThread.class);
	// need to cache koubei. citylist , split by '\\|'
	private static final String CITY_LIST = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"koubei.cities", "034");
	
	public KoubeiThread(CouponService couponService) {
		this.setCouponService(couponService);
	}
	
	@Override
	public void run() {
		try {
			logger.info("刷新口碑券缓存~ ");
//			String cityId = "034";
			if(CITY_LIST != null) {
				for(String cityId : CITY_LIST.split("\\|")) {
					List<String> lines = BaseServiceUtils.getAllLines(cityId);
					for(String lineId : lines) {
						List<StopInfo> stopInfos = BaseServiceUtils.getStopInfo(lineId, cityId);
						for(StopInfo stopInfo : stopInfos) {
//							logger.info("拉取站点推荐优惠券，lineId={}, stopName={}, lng={}, lat={}", 
//									lineId, stopInfo.getStationName(), stopInfo.getLng(), stopInfo.getLat());
							// 经纬度转换， wgs TO  gcj
							double[] gcjs = GPSConvert.wgs2gcj(stopInfo.getLng(), stopInfo.getLat());
							//获取周边推荐口碑券
							List<DiscountInfo> disCounts = couponService.getDiscounts(cityId, String.valueOf(gcjs[0]), String.valueOf(gcjs[1]), 
									null, null, stopInfo.getStationName(), false);
//							logger.info("获取到的disCounts信息：stnName={}, disCounts={}", stopInfo.getStationName(), JSONObject.toJSONString(disCounts));
							// 取第一条，构建CouponInfo结构体，存入ocs中
							if(null == disCounts || disCounts.isEmpty()) {
								continue;
							}
							for (int i = 0; i < disCounts.size(); i++) {
								DiscountInfo discountInfo = disCounts.get(i);
								CouponInfo couponInfo = new CouponInfo(discountInfo.getItemId(), discountInfo.getDistance(),
										discountInfo.getShopName(), discountInfo.getImageUrl());
								String type = discountInfo.getType();
								CouponType couponType = CouponType.getType(type);
								if (null == couponType) {
									continue;
								}
								couponService.parseCouponInfo(discountInfo, couponType, couponInfo);
								if (StringUtils.isBlank(couponInfo.getItemName()) || couponInfo.getItemName().contains("null") || couponInfo.getItemName().contains("NULL")) {
									continue;
								}
								String couponInfoStr = JSONObject.toJSONString(couponInfo);
//								logger.info("获取到的口碑券信息: stopName={}, couponInfo={}", stopInfo.getStationName(), couponInfoStr);
								String key = KBUtil.getKbCouponOcsKey(cityId, stopInfo.getStationName());
								CacheUtil.set(key, Constants.ONE_DAY_TIME, couponInfoStr);
								break;
							}
						}
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

	public static void main(String[] args) {
		for(String cityId : CITY_LIST.split("\\|")) {
			System.out.println(cityId);
		}
	}
}

