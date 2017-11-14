package com.bus.chelaile.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdLineDetailInnerContent;
import com.bus.chelaile.model.ads.AdStationlInnerContent;
import com.bus.chelaile.model.ads.entity.ActiveAdEntity;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.StationAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;

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
					.info("[STATION_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={}",
							ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
							advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getLineId(),
							advParam.getStnName(), advParam.getNw(), advParam.getIp(), advParam.getDeviceType(),
							advParam.getLng(), advParam.getLat());

		return entity;
	}

	private StationAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContent ad, ShowType showType) {
		StationAdEntity res = new StationAdEntity();

		res.fillBaseInfo(ad, advParam, new HashMap<String, String>());

		res.dealLink(advParam);
		
		AdInnerContent inner = ad.getAdInnerContent();
		if (inner instanceof AdStationlInnerContent) {
			AdStationlInnerContent stationInner = (AdStationlInnerContent) inner;
			res.setBannerInfo(stationInner.getBannerInfo());
			res.setAdCard(stationInner.getAdCard());
			res.setPic(stationInner.getPic());
		} else {
			throw new IllegalArgumentException("=====> 错误的innerContent类型： "
					+ ((inner == null) ? null : inner.getClass()) + "; "
					+ inner + ",udid=" + advParam.getUdid());
		}

		return res;
	}
	
	

}
