package com.bus.chelaile.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.entity.ApiLineEntity;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;
/**
 * 换乘，更多车辆，站点对应线路接口走这个manager
 * @author 41945
 *
 */
public class OtherManager extends AbstractManager {

	@Autowired
	private SelfManager selfManager;
	@Autowired
	private ApiDetailsManager apiDetailsManager;

	@Override
	/*
	 * 自采买广告，除非当天没有投放过，否则自动刷新不计数
	 */
	public BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam,
			AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType,
			QueryParam queryParam, boolean isRecord) throws Exception {
		
		boolean isAutoRefresh = false;  // 是否是自动刷新。默认不是
		if(advParam.getStatsAct() != null
				&& advParam.getStatsAct().equals(Constants.STATSACT_AUTO_REFRESH)) {
			isAutoRefresh = true;
		}

		//	旧版本的广告接口
		if (cateGory == null) {
			for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
				// 自采买
				// 自动刷新且今天投放过该广告的用户，不再记录投放次数
				if (! (isAutoRefresh && cacheRecord.hasPulished(entry.getValue().getAds().getId()))) {
					isAutoRefresh = false;
				} else {
					isAutoRefresh = true;
				}
				return getSelfAdEntity(advParam, cacheRecord, entry.getValue(), showType, isAutoRefresh);
			}
		}

		if (cateGory.getAdType() == 1) {
			//自采买
			// 自动刷新且今天投放过该广告的用户，不再记录投放次数
			if (! (isAutoRefresh && cacheRecord.hasPulished(cateGory.getAdId()))) {
				cacheRecord.setAdHistory(cateGory);
				isAutoRefresh = false;
			} else {
				isAutoRefresh = true;
			}
			return getSelfAdEntity(advParam, cacheRecord, adMap.get(cateGory.getAdId()), showType, isAutoRefresh);
		} else {
			ApiLineEntity entity = apiDetailsManager.from(
					Platform.from(advParam.getS()), advParam, cacheRecord,
					cateGory, showType.getType());
			
			if( entity == null ){
				//logger.info("udid={}第三方广告返回为空");
				if (! isAutoRefresh) {
					cacheRecord.setAdHistory(new AdCategory(-1, -1, -1));
				}
				return null;
			}
			
			
				if (cateGory.getAdType() == 7 || cateGory.getAdType() == 3 || cateGory.getAdType() == 6) {
					AnalysisLog
						.info("[LINE_DETAIL_ADS]: adKey=ADV[id={}#showType=05#title={}],des={},link={} ,userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},provider_id={},clickMonitorLink={},unfoldMonitorLink={},nw={},ip={},deviceType={},geo_lng={},geo_lat={}",
								entity.getId(), entity.getApiTitle(),
								entity.getApiDes(), entity.getCombpic(),
								advParam.getUserId(), advParam.getAccountId(),
								advParam.getUdid(), advParam.getCityId(),
								advParam.getS(), advParam.getV(),
								advParam.getLineId(), advParam.getStnName(),
								entity.getProvider_id(),
								entity.getClickMonitorLink(),
								entity.getUnfoldMonitorLink(),
								advParam.getNw(), advParam.getIp(),
								advParam.getDeviceType(),advParam.getLng(),advParam.getLat());
				} else {
					AnalysisLog
						.info("[LINE_DETAIL_ADS]: adKey=ADV[id={}#showType=05] ,userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={}, provider_id={},nw={},ip={},deviceType={},geo_lng={},geo_lat={}",
								entity.getId(), advParam.getUserId(),
								advParam.getAccountId(), advParam.getUdid(),
								advParam.getCityId(), advParam.getS(),
								advParam.getV(), advParam.getLineId(),
								advParam.getStnName(), entity.getProvider_id(),
								advParam.getNw(), advParam.getIp(),
								advParam.getDeviceType(),advParam.getLng(),advParam.getLat());
				}
			if (! isAutoRefresh) {
				cacheRecord.setAdHistory(cateGory);
			}
			return entity;
		}

	}

	/**
	 * 自采买
	 * 
	 * @param cateGory
	 * @param advParam
	 * @param cacheRecord
	 * @param ad
	 * @param showType
	 * @return
	 */
	private BaseAdEntity getSelfAdEntity(AdvParam advParam, AdPubCacheRecord cacheRecord,
			AdContentCacheEle ad, ShowType showType, boolean isAutoRefresh) {
		BaseAdEntity entity = selfManager.getEntity(advParam, cacheRecord,
				ad.getAds(),ad.getRule().getRightPushNum());

		// 每个时间段的发送次数
		if (!isAutoRefresh) {
			adTimeCounts(cacheRecord, advParam.getUdid(), ad);
		}
		wirteLinedetailsLog(advParam, ad.getAds().getLogKey(), advParam.getS(), !isAutoRefresh);
		return entity;
	}

	// @Override
	// public List<AdContentCacheEle> getAllAdsList(String udid, String
	// accountId) {
	// return getCommonsAdsList(udid, accountId, ShowType.LINE_DETAIL);
	// }

	// @Override
	// public void handleAds(Map<Integer, AdContentCacheEle> adMap,
	// List<AdContentCacheEle> adsList, ShowType showType,
	// AdvParam advParam, AdPubCacheRecord cacheRecord, boolean isNeedApid) {
	// setAds(adMap, adsList, showType, advParam, cacheRecord, -1, isNeedApid);
	// }

	private void wirteLinedetailsLog(AdvParam advParam, String adLogKey,
			String platform, boolean isRecord) {

		boolean isH5 = platform.equalsIgnoreCase(Platform.H5.getValue()); // 当前的客户端是否是H5
		
		if (! isH5) {
			AnalysisLog
			.info("[LINE_DETAIL_ADS]:  adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},stats_act={},isRecord={}",
					adLogKey, advParam.getUserId(),
					advParam.getAccountId(), advParam.getUdid(),
					advParam.getCityId(), platform, advParam.getV(),
					advParam.getLineId(), advParam.getStnName(),
					advParam.getNw(), advParam.getIp(),
					advParam.getDeviceType(),advParam.getLng(),advParam.getLat(),advParam.getStatsAct(),isRecord);
			
		} else {
			AnalysisLog
			.info("[H5_LINE_DETAIL_ADS]:  adKey={}, h5User={}, h5Src={}, cityId={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},stats_act={},isRecord={}",
					adLogKey, advParam.getH5User(),
					advParam.getH5Src(), advParam.getCityId(),
					advParam.getLineId(), advParam.getStnName(),
					advParam.getNw(), advParam.getIp(),
					advParam.getDeviceType(),advParam.getLng(),advParam.getLat(),advParam.getStatsAct(),isRecord);
		}
	}

	@Override
	protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
