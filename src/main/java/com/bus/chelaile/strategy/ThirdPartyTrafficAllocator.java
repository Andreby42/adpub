package com.bus.chelaile.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;


public class ThirdPartyTrafficAllocator {
	protected static final Logger logger = LoggerFactory
			.getLogger(ThirdPartyTrafficAllocator.class);

    private static int startUpAdType;

	private static final double EPSILON = 1e-6;

	private static void normalize(Map<String, Double> ratioDict, double sum) {
		// Normalize the weight list
		// e.g. [1, 2, 3, 4] => [0.1, 0.2, 0.3, 0.4]
		if (ratioDict == null || ratioDict.size() == 0) {
			return;
		}
		for (Entry<String, Double> entry : ratioDict.entrySet()) {
			ratioDict.put(entry.getKey(), entry.getValue() / sum);
		}
	}

	public static String getKeyByWeights(Map<String, Double> ratioDict) {
        if (ratioDict == null || ratioDict.size() == 0) {
            return null;
        }
        double sum = 0.0;
        for (Entry<String, Double> entry : ratioDict.entrySet()) {
            sum += entry.getValue();
        }
        normalize(ratioDict, sum);
        Random random = new Random();
        double randDouble = random.nextDouble();
        double aggr = 0.0;
        String key = null;
        for (Entry<String, Double> entry : ratioDict.entrySet()) {
            aggr += entry.getValue();
            if (aggr > randDouble) {
                key = entry.getKey();
                break;
            }
        }
        return key;
    }

	public static AdCategory getRandomThirdPartyAdsProvider(Map<String, Double> weights,
                                                            List<String> uninterestThirdPartyAdTypes,
                                                            boolean isLongTail, int reqAdType,
															String fullStrategyName, String udid) {
		if (weights == null) {
			return null;
		}
		HashMap<String, Double> dict = new HashMap<>(weights);

		// Remove all the third party ads providers that the user has no interest of
		if (uninterestThirdPartyAdTypes != null) {
			for (String uninterestThirdPartyAdType : uninterestThirdPartyAdTypes) {
				dict.remove(uninterestThirdPartyAdType.toLowerCase());
			}
		}
		// Get the provider according to a random number
		// The principle is shown below using an example
		// Suppose the weight allocation is 0.3 for GDT, 0.4 for InMobi and the rest for Baidu
		// If the generated number is below 0.3, GDT is returned
		// Else if it is between 0.3 and 0.7 (0.3 + 0.4), InMobi is returned
		// Else Baidu is returned.
		String thirdPartyAdType = getKeyByWeights(dict);

		if (thirdPartyAdType == null)
			return null;

		String[] segs = thirdPartyAdType.split("\\.");
		String provider = segs[0];
		String type = segs[1];
		String providerKey = String.format(
				"adv.strategy.third.party.adv.map.%s", provider);
		String typeKey = String.format("adv.strategy.third.party.adv.map.%s",
				type);
		int adType = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
				providerKey, "-1"));
		int apiType = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
				typeKey, "-1"));
		if (adType == -1) {
			logger.error(String.format("Key %s not found", providerKey));
			throw new IllegalArgumentException(String.format(
					"Key %s not found", providerKey));
		}
		if (apiType == -1) {
			logger.error(String.format("Key %s not found", typeKey));
			throw new IllegalArgumentException(String.format(
					"Key %s not found", typeKey));
		}
		String date = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		String logContent = String.format("%s@%s", fullStrategyName, weights.toString());
		if (isLongTail) {
			logContent += "(long tail triggered)";
		}
		if (!fullStrategyName.startsWith("default") || fullStrategyName.contains("long tail")) {
			AnalysisLog.info("{},{},{}", date, udid, logContent);
		}
		return new AdCategory(-1, adType, apiType);
	}
}
