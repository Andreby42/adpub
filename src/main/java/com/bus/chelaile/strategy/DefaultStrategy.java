package com.bus.chelaile.strategy;

import java.text.SimpleDateFormat;
import java.util.*;

import com.bus.chelaile.model.strategy.AdStrategyParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

public class DefaultStrategy extends AdShowBaseStrategy {
	protected static final Logger logger = LoggerFactory
			.getLogger(DefaultStrategy.class);

    // NBP is short for Native/Banner/Picture
	protected int nNBPAdPvEveryGroup;
	protected ArrayList<Integer> thirdPartyNBPAdGroupNoList = new ArrayList<>();
    protected int nStartScreenAdPvEveryGroup;
    protected ArrayList<Integer> thirdPartyStartScreenAdGroupNoList = new ArrayList<>();
    protected Integer lineDetailAdsLongtailThreshold;
    protected Integer startScreenAdsLongtailThreshold;
	// If ad type is 1, 2 or 3, it means the ad is not for opening / floating.
	// If ad type is above 4, we need a separate logic to handle the 3rd party ad
	private Map<String, Double> thirdPartyLineDetailWeights = new HashMap<>();
	private Map<String, Double> thirdPartyLineDetailLongtailWeights = new HashMap<>();
	private Map<String, Double> thirdPartyStartScreenWeights = new HashMap<>();
	private Map<String, Double> thirdPartyStartScreenLongtailWeights = new HashMap<>();
	private String adGroupExclusionInfo;
//	private String primaryStrategyName;
//	private String paramInfo;
	private String fullStrategyName;
	protected static int startScreenAdType = 4;

	public DefaultStrategy(AdStrategyParam adStrategyParam) {
		StringBuilder fullStrategyNameSB = new StringBuilder();
		fullStrategyNameSB.append(adStrategyParam.getStrategyName());
		thirdPartyLineDetailWeights = parseString2Dict(adStrategyParam.getThirdPartyLineDetailWeights());
		thirdPartyLineDetailLongtailWeights = parseString2Dict(adStrategyParam.getThirdPartyLineDetailLongtailWeights());
		thirdPartyStartScreenWeights = parseString2Dict(adStrategyParam.getThirdPartyStartScreenWeights());
		thirdPartyStartScreenLongtailWeights = parseString2Dict(adStrategyParam.getThirdPartyStartScreenLongtailWeights());
		nNBPAdPvEveryGroup = adStrategyParam.getLineDetailNPV();
		String lineDetail3rdGroupsStr = adStrategyParam.getLineDetail3rdGroups();
		if(lineDetail3rdGroupsStr != null) {
			String[] thirdPartyNBPAdGroupNoStrList = lineDetail3rdGroupsStr.split("&");
			for (String groupNoStr: thirdPartyNBPAdGroupNoStrList) {
				thirdPartyNBPAdGroupNoList.add(Integer.parseInt(groupNoStr));
			}
		}
		lineDetailAdsLongtailThreshold = adStrategyParam.getLineDetailLongtailThreshold();
		nStartScreenAdPvEveryGroup = adStrategyParam.getStartScreenNPV();
		String startScreen3rdGroupsStr = adStrategyParam.getStartScreen3rdGroups();
		if(startScreen3rdGroupsStr != null) {
		String[] thirdPartyStartScreenAdGroupNoStrList = startScreen3rdGroupsStr.split("&");
			for (String groupNoStr: thirdPartyStartScreenAdGroupNoStrList) {
				thirdPartyStartScreenAdGroupNoList.add(Integer.parseInt(groupNoStr));
			}
		}
		startScreenAdsLongtailThreshold = adStrategyParam.getStartScreenLongtailThreshold();
		adGroupExclusionInfo = adStrategyParam.getAdExclusion();
		if (lineDetailAdsLongtailThreshold != null && startScreenAdsLongtailThreshold != null) {
			fullStrategyNameSB.append(String.format("(longtail)|%d|%d",
					lineDetailAdsLongtailThreshold, startScreenAdsLongtailThreshold));
		}
		fullStrategyNameSB.append(String.format("|%d|%s|%d|%s",
				nNBPAdPvEveryGroup, adStrategyParam.getLineDetail3rdGroups(),
				nStartScreenAdPvEveryGroup, adStrategyParam.getStartScreen3rdGroups()));
		fullStrategyName = fullStrategyNameSB.toString();
	}

	public String getAdGroupExclusionInfo() {
		return adGroupExclusionInfo;
	}

	public String getFullStrategyName() {
		return fullStrategyName;
	}

	private static Map<String, Double> parseString2Dict(String dictStr) {
		if (dictStr == null || dictStr.trim().length() == 0) {
			return null;
		}
		Map<String, Double> dict = new HashMap<>();
		String[] groups = dictStr.split("\\|");
		for (String group: groups) {
			String[] kv = group.split(":");
			double weight = Double.parseDouble(kv[1]);
			String[] keys = kv[0].split("&");
			for (String key: keys) {
				dict.put(key, weight);
			}
		}
		return dict;
	}

	@Override
	public String getStrategyName() {
		return getFullStrategyName();
	}

	@Override
	public AdCategory getAd(String udid, String platform,
			Map<AdCategory, Integer> adHistoryMap, List<AdInfo> availableAds,
			List<String> uninterestThirdPartyAdTypes, int reqAdType) {
		int nPvEveryGroup;
        List<Integer> thirdPartyAdGroupNoList;
        Integer longtailThreshold;
        Map<String, Double> thirdPartyAdWeights;
        Map<String, Double> longtailThirdPartyAdWeights;

        // Assign corresponding parameters (third party weights, long tail ad threshold...
		// according to the request ad type
		if (reqAdType == startScreenAdType) {
            thirdPartyAdGroupNoList = thirdPartyStartScreenAdGroupNoList;
            nPvEveryGroup = nStartScreenAdPvEveryGroup;
            longtailThreshold = startScreenAdsLongtailThreshold;
            if (thirdPartyStartScreenWeights != null) {
				thirdPartyAdWeights = new HashMap<>(thirdPartyStartScreenWeights);
			} else {
				thirdPartyAdWeights = null;
			}
			if (thirdPartyStartScreenLongtailWeights != null) {
				longtailThirdPartyAdWeights = new HashMap<>(thirdPartyStartScreenLongtailWeights);
			} else {
				longtailThirdPartyAdWeights = null;
			}
        } else {
            thirdPartyAdGroupNoList = thirdPartyNBPAdGroupNoList;
            nPvEveryGroup = nNBPAdPvEveryGroup;
            longtailThreshold = lineDetailAdsLongtailThreshold;
            if (thirdPartyLineDetailWeights != null) {
				thirdPartyAdWeights = new HashMap<>(thirdPartyLineDetailWeights);
			} else {
				thirdPartyAdWeights = null;
			}
			if (thirdPartyLineDetailLongtailWeights != null) {
				longtailThirdPartyAdWeights = new HashMap<>(thirdPartyLineDetailLongtailWeights);
			} else {
				longtailThirdPartyAdWeights = null;
			}
        }

        // See how many times has the user requested ads
		int totalPushedTime = 1;
		if (adHistoryMap != null) {
			for (Map.Entry<AdCategory, Integer> entry : adHistoryMap.entrySet()) {
				totalPushedTime += entry.getValue();
			}
		}
//		String date = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		// See if the current visit falls into the third party ads groups, or is beyond the long tail threshold
		for (int thirdPartyAdGroupNo : thirdPartyAdGroupNoList) {
			int minThirdPartyAdPushTime = nPvEveryGroup
					* (thirdPartyAdGroupNo - 1) + 1; 
			int maxThirdPartyAdPushTime = nPvEveryGroup * thirdPartyAdGroupNo;
			if (availableAds == null || availableAds.size() == 0) {
				if (totalPushedTime <= maxThirdPartyAdPushTime
					&& totalPushedTime >= minThirdPartyAdPushTime) {
					return ThirdPartyTrafficAllocator.getRandomThirdPartyAdsProvider(thirdPartyAdWeights,
							uninterestThirdPartyAdTypes, false, reqAdType, fullStrategyName, udid);
				}
				if (longtailThreshold != null && totalPushedTime > longtailThreshold) {
					return ThirdPartyTrafficAllocator.getRandomThirdPartyAdsProvider(longtailThirdPartyAdWeights,
							uninterestThirdPartyAdTypes, true, reqAdType, fullStrategyName, udid);
				}
			}
		}
		return getOwnAd(udid, platform, adHistoryMap, availableAds);
	}

	@Override
	public AdCategory getOwnAd(String udid, String platform,
			Map<AdCategory, Integer> adHistoryMap, List<AdInfo> availableAds) {
		// Default strategy: random get an ad from the available list
		if (udid == null) {
			logger.error("udid is null");
			throw new IllegalArgumentException("udid is null");
		}
		if (platform == null) {
			logger.error("Platform is null");
			throw new IllegalArgumentException("Platform is null");
		}
		if (availableAds == null || availableAds.size() == 0) {
			logger.debug(String.format("No available own ads for %s", udid));
			return null;
		}
		Random random = new Random();
		int adIndex = random.nextInt(availableAds.size());
		AdInfo adInfo = availableAds.get(adIndex);
		int ownType = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
				"adv.strategy.third.party.adv.map.own", "1"));
		return new AdCategory(adInfo.getAdid(), ownType, -1);
	}
}
