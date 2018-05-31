package com.bus.chelaile.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdWXBannerInnerContent;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.WXAppBannerAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.New;

public class WXBannerManager extends AbstractManager {

	@Override
	protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord)
			throws Exception {
		return null;
	}

	private WXAppBannerAdEntity from(AdContentCacheEle ad, AdvParam advParam) {
		WXAppBannerAdEntity res = new WXAppBannerAdEntity(ShowType.WECHATAPP_BANNER_ADV);
		res.fillBaseInfo(ad.getAds(), advParam, new HashMap<String, String>());
		res.dealLink(advParam);

		AdWXBannerInnerContent adWXBanner = (AdWXBannerInnerContent) ad.getAds().getAdInnerContent();
		res.setPic(adWXBanner.getPic());
		res.setWxMiniProId(adWXBanner.getWx_miniPro_id());
		res.setWxMiniProPath(adWXBanner.getWx_miniPro_path());
		res.setPriority(ad.getAds().getPriority());

		return res;
	}

	@Override
	protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
		List<BaseAdEntity> entities = New.arrayList();
		
		// 遍历所有符合条件的广告体
		for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
			AdContentCacheEle ad = entry.getValue();
			AdWXBannerInnerContent adWXBanner = (AdWXBannerInnerContent) ad.getAds().getAdInnerContent();

			// 广告结构体有对来源的要求
			if (adWXBanner.getServingPlaceList() != null && adWXBanner.getServingPlaceList().size() > 0) {
				if (StringUtils.isEmpty(advParam.getWxs())) {
					continue;
				}
				for (String s : adWXBanner.getServingPlaceList()) {
					if (s.equals(advParam.getWxs())) {
						WXAppBannerAdEntity adEntity = from(ad, advParam);
						if (adEntity != null) {
							AnalysisLog
									.info("[ADV_SEND]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},shareId={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},src={},wxs={}",
											ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(),
											advParam.getUdid(), advParam.getCityId(), advParam.getS(), advParam.getV(),
											advParam.getLineId(), advParam.getStnName(), advParam.getShareId(),
											advParam.getNw(), advParam.getIp(), advParam.getDeviceType(),
											advParam.getLng(), advParam.getLat(), advParam.getSrc(), advParam.getWxs());
							cacheRecord.buildAdPubCacheRecord(adEntity.getId());
							entities.add(adEntity);
						}
					}
				}
			} else {
				// 如果广告结构体没有对来源有要求，那么直接返回即可
				WXAppBannerAdEntity adEntity = from(ad, advParam);
				if (adEntity != null) {
					AnalysisLog
							.info("[ADV_SEND]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},shareId={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},src={},wxs={}",
									ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(),
									advParam.getUdid(), advParam.getCityId(), advParam.getS(), advParam.getV(),
									advParam.getLineId(), advParam.getStnName(), advParam.getShareId(),
									advParam.getNw(), advParam.getIp(), advParam.getDeviceType(), advParam.getLng(),
									advParam.getLat(), advParam.getSrc(), advParam.getWxs());
					cacheRecord.buildAdPubCacheRecord(adEntity.getId());
					entities.add(adEntity);
				}
			}
		}
		if(entities.size() == 0)
			return null;
		return entities;
	}

}
