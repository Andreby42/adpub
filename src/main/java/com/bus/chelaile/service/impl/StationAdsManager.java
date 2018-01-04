package com.bus.chelaile.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdStationlInnerContent;
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
		
		AdInnerContent inner = ad.getInnerContent();
		if (inner instanceof AdStationlInnerContent) {
			AdStationlInnerContent stationInner = (AdStationlInnerContent) inner;
			// 对空串情况做一下处理
			if(stationInner.getBannerInfo() != null && StringUtils.isNoneBlank(stationInner.getBannerInfo().getName()))
			{
				res.setBannerInfo(stationInner.getBannerInfo());
			}
			if(stationInner.getAdCard() != null && StringUtils.isNoneBlank(stationInner.getAdCard().getName())) {
				res.setAdCard(stationInner.getAdCard());
			}
			res.setPic(stationInner.getPic());
		} else {
			throw new IllegalArgumentException("=====> 错误的innerContent类型： "
					+ ((inner == null) ? null : inner.getClass()) + "; "
					+ inner + ",udid=" + advParam.getUdid());
		}

		return res;
	}
	
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String url = "http%3A%2F%2F121.40.95.166%3A7000%2Foutman%2Fadv%2FqueryAdv%3Fid%3D12024";
		System.out.println(HttpUtils.get(url, "utf-8"));
	}
}
