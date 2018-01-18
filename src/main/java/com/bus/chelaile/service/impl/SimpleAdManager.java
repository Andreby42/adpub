package com.bus.chelaile.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdLineDetailInnerContent;
import com.bus.chelaile.model.ads.AdLineRefreshInnerContent;
import com.bus.chelaile.model.ads.Tag;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.LineRefreshAdEntity;
import com.bus.chelaile.model.ads.entity.SimpleAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;

public class SimpleAdManager extends AbstractManager {

	protected static final Logger logger = LoggerFactory.getLogger(SimpleAdManager.class);


	@Override
	protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord)
			throws Exception {

		AdContentCacheEle ad = null;

		for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
			ad = entry.getValue();
		}
		if (showType == ShowType.LINEDETAIL_REFRESH_ADV) {
			LineRefreshAdEntity entity = (LineRefreshAdEntity) from(advParam, cacheRecord, ad, showType);
			AnalysisLog
					.info("[LINEDETAIL_REFRESH_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},shareId={},nw={},ip={},deviceType={},geo_lng={},geo_lat={}",
							ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
							advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getLineId(),
							advParam.getStnName(), advParam.getShareId(), advParam.getNw(), advParam.getIp(),
							advParam.getDeviceType(), advParam.getLng(), advParam.getLat());
			return entity;
		} else if (showType == ShowType.H5_LINEBANNER_ADV) {
			SimpleAdEntity entity = from(advParam, cacheRecord, ad, showType);
			AnalysisLog
					.info("[H5_LINEBANNER_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},shareId={},nw={},ip={},deviceType={},geo_lng={},geo_lat={}",
							ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
							advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getLineId(),
							advParam.getStnName(), advParam.getShareId(), advParam.getNw(), advParam.getIp(),
							advParam.getDeviceType(), advParam.getLng(), advParam.getLat());
			return entity;
		}

		return null;

	}

	private SimpleAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContentCacheEle ad, ShowType showType) {
		SimpleAdEntity res = null;
		
		AdLineRefreshInnerContent inner = (AdLineRefreshInnerContent) ad.getAds().getInnerContent();
		if (showType == ShowType.LINEDETAIL_REFRESH_ADV) {
			res = new LineRefreshAdEntity(2);
			if(StringUtils.isNoneBlank(inner.getTag())
					&& StringUtils.isNoneBlank(inner.getTagId())) {
				res.setTag(new Tag(inner.getTag(), inner.getTagId()));
			}
			res.setFeedId(inner.getFeedId());
//			res.setPic(res.getPicUrl(advParam.getS(), inner.getIosURL(), inner.getAndroidURL(), inner.getPic()));
//			res.setDuration(2);
		} else if (showType == ShowType.H5_LINEBANNER_ADV) {
			res = new SimpleAdEntity();
		}

		res.setPic(inner.getPic());
		res.fillBaseInfo(ad.getAds(), advParam, new HashMap<String, String>());
		res.dealLink(advParam);

		// 区分ios和android的图片

		return res;

	}

}
