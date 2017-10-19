package com.bus.chelaile.strategy;


import java.util.List;
import java.util.Map;

public abstract class AdShowBaseStrategy {
	public abstract AdCategory getAd(String udid, String platform,
			Map<AdCategory, Integer> adHistoryMap, List<AdInfo> availableAds,
			List<String> uninterestThirdPartyAdTypes, int reqAdType);

	public abstract AdCategory getOwnAd(String udid, String platform,
			Map<AdCategory, Integer> adHistoryMap, List<AdInfo> availableAds);

	public abstract String getStrategyName();
}
