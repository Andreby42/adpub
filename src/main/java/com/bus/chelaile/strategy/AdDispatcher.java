package com.bus.chelaile.strategy;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.New;


public class AdDispatcher {
	protected static final Logger logger = LoggerFactory
			.getLogger(AdDispatcher.class);

	/*
	 * 如果请求线路详情页广告（包括横幅banner、原生native、图片picture，reqAdType传任意
	 * 小于3的值就可以。目前策略不区分这三种类型的广告 如果请求开屏广告，reqAdType传4
	 */
	public static AdCategory getAd(String udid, String platform,
			Map<AdCategory, Integer> adHistoryMap, List<AdInfo> availableAds,
			List<String> uninterestThirdPartyAdTypes, int reqAdType) {
		// Get the corresponding strategy given a user. Mappings are available in the database.
		AdShowBaseStrategy adShowStrategy = UserStrategyJudger.getStrategy(udid, platform);
		// If the given strategy is "own ad block", then clear the available ads list
		// Thus if current visit doesn't need 3rd party ad, the method returns null
		logger.info("udid={}, strategyName={}", udid, adShowStrategy.getStrategyName());
		if (adShowStrategy.getStrategyName().startsWith("own_ad_block")) {
			if (availableAds != null) {
				availableAds.clear();
			}
		}
		// Ad group exclusion
		// For example, for a given user, if the available ads are 2140, 2141 and 2142, and in the
		// configuration they are divided into the same group, then the user will see the different
		// ads randomly with equal probabilities when he first "requests" the ad (suppose we display
		// 2040). After that he'll never see the ads other than what he saw at the first visit
		// i.e. he cannot see 2141 and 2142.
		String adGroupExclusionInfo = null;
		if (adShowStrategy instanceof DefaultStrategy) {
			adGroupExclusionInfo = ((DefaultStrategy)adShowStrategy).getAdGroupExclusionInfo();
		}
		List<AdInfo> filteredAvailableAdsByGroup = AdGroupFilter
				.filterAvailableList(adGroupExclusionInfo, adHistoryMap, availableAds);
//		logger.info(
//				"{} after group filter: {}",udid, filteredAvailableAdsByGroup);

		// Filter by priority
		// Ads that have lower priority are filtered out
		List<AdInfo> filteredAvailableAdsByPriority = filterAvailableAdsByPriority(filteredAvailableAdsByGroup);
//		logger.info(
//				"{} after priority filter: {}",udid, filteredAvailableAdsByPriority);
		return adShowStrategy.getAd(udid, platform, adHistoryMap,
				filteredAvailableAdsByPriority, uninterestThirdPartyAdTypes,
				reqAdType);
	}

	private static List<AdInfo> filterAvailableAdsByPriority(
			List<AdInfo> availableAds) {
		if (availableAds == null || availableAds.size() == 0) {
			return null;
		}
		ArrayList<AdInfo> highestPriorityAdsList = new ArrayList<>();
		int highestPriority = 0;
		for (AdInfo adInfo : availableAds) {
			int currentPriority = adInfo.getPriority();
			if (currentPriority < highestPriority) {
				continue;
			}
			if (currentPriority > highestPriority) {
				highestPriority = currentPriority;
				highestPriorityAdsList.clear();
			}
			highestPriorityAdsList.add(adInfo);
		}
		return highestPriorityAdsList;
	}

	/**
	 * adv.strategy.third.party.adv.provider=gdt,inmobi
	 * adv.strategy.third.party.adv.type=native,banner,pic 线路详情
	 * 
	 * @param advParam
	 * @param cacheRecord
	 * @param advMap
	 * @return
	 */
	public static AdCategory getAdCategory(AdvParam advParam,
			AdPubCacheRecord cacheRecord, Map<Integer, AdContentCacheEle> advMap) {
		List<AdInfo> availableAds = null;
		if (advMap != null) {
			Iterator<Map.Entry<Integer, AdContentCacheEle>> it = advMap
					.entrySet().iterator();
			availableAds = New.arrayList();
			while (it.hasNext()) {
				Map.Entry<Integer, AdContentCacheEle> entry = it.next();
				//下面这部分，挪到 ruleCheck中
//				//判断是否投放过
//				if (isUvSend((AdContentCacheEle) entry.getValue(), cacheRecord,
//						advParam.getUdid())) {
//					continue;
//				}
				
				AdInfo info = new AdInfo(entry.getValue().getAds().getId(),
						entry.getValue().getAds().getPriority());

				availableAds.add(info);
			}
		}

		
		//	当自采买广告为空的时候,并且该用户点了 ‘对第三方广告’ 不感兴趣
		if( advMap == null || advMap.size() == 0 ){
			//	用户点了不感兴趣
			if(	isUninterest(advParam.getLineId(), cacheRecord) ){
			   return null;
		   }
		}

		logger.info(
				"udid={},s={},availableAds={}",
				advParam.getUdid(), advParam.getS(), availableAds);

		return getAd(advParam.getUdid(), advParam.getS(),
				cacheRecord.todayAdHistoryList(), availableAds,
				null, 3);
	}

	/**
	 * 开屏
	 * 
	 * @param advParam
	 * @param cacheRecord
	 * @param advMap
	 * @return
	 */
	public static AdCategory getOpenAdCategory(AdvParam advParam,
			AdPubCacheRecord cacheRecord, Map<Integer, AdContentCacheEle> advMap) {
		List<AdInfo> availableAds = null;
		if (advMap != null) {
			Iterator<Map.Entry<Integer, AdContentCacheEle>> it = advMap
					.entrySet().iterator();
			availableAds = New.arrayList();
			while (it.hasNext()) {
				Map.Entry<Integer, AdContentCacheEle> entry = it.next();
				AdInfo info = new AdInfo(entry.getValue().getAds().getId(),
						entry.getValue().getAds().getPriority());

				availableAds.add(info);
			}
		}

		logger.info("udid={},s={},availableAds={}", advParam.getUdid(),
				advParam.getS(), availableAds);

		AdCategory ac = getAd(advParam.getUdid(), advParam.getS(),
				cacheRecord.todayOpenAdHistoryList(), availableAds, null, 4);

		return ac;
	}

//	/**
//	 * 该线路是否点击了不敢兴趣
//	 * @param platform
//	 * @param advParam
//	 * @param cacheRecord
//	 * @return	true 不敢兴趣
//	 */
	private static boolean isUninterest(String lineId, AdPubCacheRecord cacheRecord) {
		
		if (cacheRecord.getUninterestedMap() == null || cacheRecord.getUninterestedMap().size() == 0) {
			return false;
		}
		List<String> list = cacheRecord.getUniterestApiList(lineId);

		if (list == null || list.size() == 0) {
			return false;
		}
			
		for(String str : list) {
			String [] args = str.split(",");
			if(args.length != 2) {
				continue;
			}
			long time = Long.parseLong(args[1]);
			if (time > 0) {
				time = System.currentTimeMillis() - time;
				if (time < 15 * 60 * 1000) {
					return true;
				}
			}
		}
		return false;
	}
}
