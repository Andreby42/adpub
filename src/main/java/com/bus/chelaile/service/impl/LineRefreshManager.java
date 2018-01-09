package com.bus.chelaile.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdLineDetailInnerContent;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.LineRefreshAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;

public class LineRefreshManager extends AbstractManager {

	protected static final Logger logger = LoggerFactory.getLogger(LineRefreshManager.class);

	/***
	 * 处理了包括：活动页、乘车页、音频、聊天室，共四类广告
	 */
	@Override
	protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) throws Exception {

		AdContentCacheEle ad = null;

		for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
			ad = entry.getValue();
		}
		LineRefreshAdEntity entity = from(advParam, cacheRecord, ad, showType);

		AnalysisLog
				.info("[LINEDETAIL_REFRESH_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},shareId={},nw={},ip={},deviceType={},geo_lng={},geo_lat={}",
						ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
						advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getLineId(),
						advParam.getStnName(), advParam.getShareId(), advParam.getNw(), advParam.getIp(),
						advParam.getDeviceType(), advParam.getLng(), advParam.getLat());

		return entity;
	}

	private LineRefreshAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContentCacheEle ad, ShowType showType) {
		LineRefreshAdEntity res = new LineRefreshAdEntity();

		res.fillBaseInfo(ad.getAds(), advParam, new HashMap<String, String>());

		res.dealLink(advParam);

		AdLineDetailInnerContent inner = (AdLineDetailInnerContent) ad.getAds().getInnerContent();
		// 区分ios和android的图片
		res.setPic(res.getPicUrl(advParam.getS(), inner.getIosURL(), inner.getAndroidURL(), inner.getPic()));

		return res;

	}

}
