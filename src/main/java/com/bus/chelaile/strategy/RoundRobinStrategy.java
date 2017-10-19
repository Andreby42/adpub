package com.bus.chelaile.strategy;

import java.text.SimpleDateFormat;
import java.util.*;

import com.bus.chelaile.model.strategy.AdStrategyParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;
import com.bus.chelaile.common.AnalysisLog;

public class RoundRobinStrategy extends DefaultStrategy {
	protected static final Logger logger = LoggerFactory
			.getLogger(RoundRobinStrategy.class);

	public RoundRobinStrategy(AdStrategyParam adStrategyParam) {
		super(adStrategyParam);
	}

	private static final Comparator<AdInfo> AD_ID_COMPARATOR = new Comparator<AdInfo>() {
		@Override
		public int compare(AdInfo o1, AdInfo o2) {
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;

			return o1.getAdid() - o2.getAdid();
		}
	};

	@Override
	public AdCategory getOwnAd(String udid, String platform,
			Map<AdCategory, Integer> adHistoryMap, List<AdInfo> availableAds) {
		int ownType = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
				"adv.strategy.third.party.adv.map.own", "1"));
		if (udid == null) {
			logger.error("udid is null");
			throw new IllegalArgumentException("udid is null");
		}
		if (platform == null) {
			logger.error("Platform is null");
			throw new IllegalArgumentException("Platform is null");
		}
		String date = new SimpleDateFormat("yyyyMMdd").format(Calendar
				.getInstance().getTime());
		AnalysisLog.info("{},{},{}", date, udid, getFullStrategyName());
		if (availableAds == null || availableAds.size() == 0) {
			logger.debug("No available own ads for {}", udid);
			return null;
		}
		List<AdInfo> sortedAdsList = new ArrayList<>(availableAds);
		Collections.sort(sortedAdsList, AD_ID_COMPARATOR);
		AdInfo adInfo = null;
		if (adHistoryMap == null || adHistoryMap.size() == 0) {
			adInfo = sortedAdsList.get(0);
		} else {
			if (sortedAdsList.size() == 1) {
				adInfo = sortedAdsList.get(0);
			} else {
				int minShowns = Integer.MAX_VALUE;
				adInfo = sortedAdsList.get(0);
				for (AdInfo i : sortedAdsList) {
					AdCategory ac = new AdCategory(i.getAdid(), ownType, -1);
					if (adHistoryMap.containsKey(ac)) {
						int nShown = adHistoryMap.get(ac);
						if (nShown < minShowns) {
							minShowns = nShown;
							adInfo = i;
						}
					} else {
						adInfo = i;
						break;
					}
				}
			}
		}

		return new AdCategory(adInfo.getAdid(), ownType, -1);
	}
}
