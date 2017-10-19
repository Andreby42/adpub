package com.bus.chelaile.strategy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AnalysisLog;

public class ImmuneStrategy extends AdShowBaseStrategy {
	protected static final Logger logger = LoggerFactory
			.getLogger(ImmuneStrategy.class);
	private static final int START_SCREEN_AD_FLAG = Integer.parseInt(PropertiesUtils.getValue(
			PropertiesName.PUBLIC.getValue(), "adv.strategy.third.party.adv.map.start", "4"));

	@Override
	public AdCategory getOwnAd(String udid, String platform,
			Map<AdCategory, Integer> adHistoryMap, List<AdInfo> availableAds) {
		return null;
	}


	@Override
	public AdCategory getAd(String udid, String platform,
			Map<AdCategory, Integer> adHistoryMap, List<AdInfo> availableAds,
			List<String> uninterestThirdPartyAdTypes, int reqAdType) {
		String date = new SimpleDateFormat("yyyyMMdd").format(Calendar
				.getInstance().getTime());
		if (reqAdType >= START_SCREEN_AD_FLAG) {
			AnalysisLog.info("{},{},{}", date, udid, getStrategyName() + ".start");
		} else {
			AnalysisLog.info("{},{},{}", date, udid, getStrategyName());
		}
		return null;
	}

	@Override
	public String getStrategyName() {
		return "immune";
	}
}
