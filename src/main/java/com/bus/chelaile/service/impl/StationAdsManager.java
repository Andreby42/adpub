package com.bus.chelaile.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.koubei.CouponInfo;
import com.bus.chelaile.koubei.KBUtil;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdStationlInnerContent;
import com.bus.chelaile.model.ads.BannerInfo;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.StationAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.HttpUtils;

public class StationAdsManager extends AbstractManager {

	@Override
	protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord)
			throws Exception {
		AdContentCacheEle ad = null;

		for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
			ad = entry.getValue();
		}
		StationAdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType);

			AnalysisLog
					.info("[STATION_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},h5User={},h5Src={}",
							ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
							advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getLineId(),
							advParam.getStnName(), advParam.getNw(), advParam.getIp(), advParam.getDeviceType(),
							advParam.getLng(), advParam.getLat(), advParam.getH5User(),
							advParam.getH5Src());

		return entity;
	}

	private StationAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContent ad, ShowType showType) {
		StationAdEntity res = new StationAdEntity();

		res.fillBaseInfo(ad, advParam, new HashMap<String, String>());

		res.dealLink(advParam);
		
		AdInnerContent inner = ad.getInnerContent();
		if (inner instanceof AdStationlInnerContent) {
			AdStationlInnerContent stationInner = (AdStationlInnerContent) inner;
			// 对空串情况做一下处理
			if(stationInner.getBannerInfo() != null
//					&& StringUtils.isNoneBlank(stationInner.getBannerInfo().getName())
					)
			{
				res.setBannerInfo(stationInner.getBannerInfo());
			}
			if(stationInner.getAdCard() != null
//					&& StringUtils.isNoneBlank(stationInner.getAdCard().getName())
					) {
				res.setAdCard(stationInner.getAdCard());
			}
			res.setPic(stationInner.getPic());
			res.setAdPriority(ad.getPriority());
			
			// 针对口碑券和淘宝客的修改
			// 口碑券，需要从ocs中获取当前站点的券
			if(stationInner.getBannerInfo().getBannerType() == 5) {
				BannerInfo bann = (BannerInfo) stationInner.getBannerInfo().clone();
				CouponInfo ocsCoupon = null;
				String key = KBUtil.getKbCouponOcsKey(advParam.getCityId(), advParam.getStnName());
				String ocsValue = (String) CacheUtil.get(key);
	            if (StringUtils.isNotBlank(ocsValue)) {
	                ocsCoupon = JSONObject.parseObject(ocsValue, CouponInfo.class);
	            }
	            if(null != ocsCoupon && StringUtils.isNoneBlank(ocsCoupon.getShopName())) {
	            	bann.setSlogan("送你一张优惠券：" + ocsCoupon.getShopName());
	            } else {
	            	logger.error("获取不到站点缓存的优惠券信息, stopName={}, key={}", advParam.getStnName(), key);
	            	return null;	// 返回空
	            }
				res.setBannerInfo(bann);
			}
			// 淘宝客，只有slogan，没有name
			else if(stationInner.getBannerInfo().getBannerType() == 6) {
				BannerInfo bann = (BannerInfo) stationInner.getBannerInfo().clone();
				
				bann.setBannerType(5);
				res.setBannerInfo(bann);
			}
		} else {
			throw new IllegalArgumentException("=====> 错误的innerContent类型： "
					+ ((inner == null) ? null : inner.getClass()) + "; "
					+ inner + ",udid=" + advParam.getUdid());
		}

		// 版本控制，老版本，targetOrder都设置为0。否则无法打开card
		Platform platform = Platform.from(advParam.getS());
		if (platform.isAndriod(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_ANDROID_0118) {
			res.setTargetType(0);
		} else if (platform.isIOS(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_IOS_0117) {
			res.setTargetType(0);
		}
		
		
		return res;
	}
	
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String url = "http%3A%2F%2F121.40.95.166%3A7000%2Foutman%2Fadv%2FqueryAdv%3Fid%3D12024";
		System.out.println(HttpUtils.get(url, "utf-8"));
	}

	@Override
	protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
