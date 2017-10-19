package com.bus.chelaile.strategy;

import java.util.*;

import com.bus.chelaile.dao.AppAdvStrategyMapper;
import com.bus.chelaile.model.strategy.AdStrategyParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;


public class UserStrategyJudger {
	protected static final Logger logger = LoggerFactory
			.getLogger(UserStrategyJudger.class);
	private static Trie udidPrefixTrie = new Trie();
	private static Map<String, AdStrategyParam> idStrategyParamDict = new HashMap<>();
	private static Map<String, AdShowBaseStrategy> idStrategyDict = new HashMap<>();
	private static HashSet<String> allowedOSList = new HashSet<>();

	@Autowired
	private AppAdvStrategyMapper advStrategyMapper;

	public void init() {
		logger.info("Initializing User Strategy Judger...");
		List<AdStrategyParam> defaultStrategyParams = advStrategyMapper.listDefaultAdvStrategies();
		// For different mobile OSs, the configuration may be different
		// Thus there should be corresponding default configuration for every platform
		assert defaultStrategyParams.size() == 2; // For android and iOS
		for (AdStrategyParam defaultStrategyParam: defaultStrategyParams) {
			// set up parameters for default configuration
			assert defaultStrategyParam.getStrategyName().equalsIgnoreCase("default");
			allowedOSList.add(defaultStrategyParam.getOs());
			verifyStrategyParameters(defaultStrategyParam);
			indexUdidPrefixes(defaultStrategyParam);
		}
		List<AdStrategyParam> specifiedStrategyParams = advStrategyMapper.listSpecifiedAdvStrategies();
		for (AdStrategyParam specifiedStrategyParam: specifiedStrategyParams) {
			completeStategyParams(specifiedStrategyParam);
			verifyStrategyParameters(specifiedStrategyParam);
			indexUdidPrefixes(specifiedStrategyParam);
		}
		logger.info("User Strategy Judger initialized successfully.");
	}

	private void indexUdidPrefixes(AdStrategyParam adStrategyParam) {
		String udidPrefixes = adStrategyParam.getUdidPrefixes();
		String os = adStrategyParam.getOs();
		String[] udidPrefixList = udidPrefixes.split("&");
		for (String udidPrefix: udidPrefixList) {
			String key = String.format("%s_%s", udidPrefix, os);
			udidPrefixTrie.insert(udidPrefix);
			idStrategyParamDict.put(key, adStrategyParam);
			idStrategyDict.put(key, getStrategyByParameters(adStrategyParam));
		}
	}

	private void verifyStrategyParameters(AdStrategyParam adStrategyParam) throws AssertionError {
		String thisOs = adStrategyParam.getOs();
		String udidPrefixes = adStrategyParam.getUdidPrefixes();
		try {
			assert allowedOSList.contains(thisOs);
			assert adStrategyParam.getStartScreenNPV() != 0 && adStrategyParam.getLineDetailNPV() != 0;
			assert adStrategyParam.getLineDetail3rdGroups() != null && adStrategyParam.getStartScreen3rdGroups() != null;
		} catch (AssertionError exception) {
			if (!allowedOSList.contains(adStrategyParam.getOs())) {
				logger.error("Invalid os ({}) for udid prefixes {}", thisOs, udidPrefixes);
			}
			if (adStrategyParam.getStartScreenNPV() == 0) {
				logger.error("Start screen ads NPV == 0 for udid prefixes {}", udidPrefixes);
			}
			if (adStrategyParam.getLineDetailNPV() == 0) {
				logger.error("Line detail ads NPV == 0 for udid prefixes {}", udidPrefixes);
			}
			if (adStrategyParam.getLineDetail3rdGroups() == null) {
				logger.error("Line Detail ads groups for 3rd ads is null for udid prefixes {}", udidPrefixes);
			}
			if (adStrategyParam.getStartScreen3rdGroups() == null) {
				logger.error("Start screen ads groups for 3rd ads is null for udid prefixes {}", udidPrefixes);
			}
			throw new IllegalArgumentException(String.format("Bad setting for %s (os = %s)", udidPrefixes, thisOs));
		}
	}

	public static AdShowBaseStrategy getStrategy(String udid, String platform) {
		String prefix = udidPrefixTrie.getLongestMatchedPrefix(udid);
		if (prefix == null || prefix.trim().length() == 0) {
			prefix = "default";
		}
		String key = String.format("%s_%s", prefix, platform);
		AdShowBaseStrategy strategy = idStrategyDict.get(key);
		if (strategy == null) {
			key = String.format("default_%s", platform);
			strategy = idStrategyDict.get(key);
		}
		assert strategy != null;
		return strategy;
	}

	public static String getGroupExclusionConfig(String udid, String platform) {
		String prefix = udidPrefixTrie.getLongestMatchedPrefix(udid);
		if (prefix.trim().length() == 0) {
			prefix = "default";
		}
		String key = String.format("%s_%s", prefix, platform);
		AdStrategyParam param = idStrategyParamDict.get(key);
		return param.getAdExclusion();
	}

	private void completeStategyParams(AdStrategyParam originalStrategy) {
		// For each empty attribute, get its default value
		String os = originalStrategy.getOs();
		if (!allowedOSList.contains(os)) {
			logger.error("Illegal os (assigned {}) for {}", os, originalStrategy.getUdidPrefixes());
			return;
		}
		AdStrategyParam correspondingDefaultParams = idStrategyParamDict.get(String.format("default_%s", os));
		if (isEmptyString(originalStrategy.getStrategyName())) {
			originalStrategy.setStrategyName("default");
		}
		if (isEmptyString(originalStrategy.getAdExclusion())) {
			originalStrategy.setAdExclusion(correspondingDefaultParams.getAdExclusion());
		}
		if (isEmptyString(originalStrategy.getThirdPartyLineDetailWeights())) {
			originalStrategy.setThirdPartyLineDetailWeights(
					correspondingDefaultParams.getThirdPartyLineDetailWeights());
		}
		if (isEmptyString(originalStrategy.getThirdPartyLineDetailLongtailWeights())) {
			originalStrategy.setThirdPartyLineDetailLongtailWeights(
					correspondingDefaultParams.getThirdPartyLineDetailLongtailWeights());
		}
		if (isEmptyString(originalStrategy.getThirdPartyStartScreenWeights())) {
			originalStrategy.setThirdPartyStartScreenWeights(
					correspondingDefaultParams.getThirdPartyStartScreenWeights());
		}
		if (isEmptyString(originalStrategy.getThirdPartyStartScreenLongtailWeights())) {
			originalStrategy.setThirdPartyStartScreenLongtailWeights(
					correspondingDefaultParams.getThirdPartyStartScreenLongtailWeights());
		}
		if (originalStrategy.getLineDetailNPV() == 0) {
			originalStrategy.setLineDetailNPV(correspondingDefaultParams.getLineDetailNPV());
		}
		if (originalStrategy.getStartScreenNPV() == 0) {
			originalStrategy.setStartScreenNPV(correspondingDefaultParams.getStartScreenNPV());
		}
		if (isEmptyString(originalStrategy.getLineDetail3rdGroups())) {
			originalStrategy.setLineDetail3rdGroups(correspondingDefaultParams.getLineDetail3rdGroups());
		}
		if (isEmptyString(originalStrategy.getStartScreen3rdGroups())) {
			originalStrategy.setStartScreen3rdGroups(correspondingDefaultParams.getStartScreen3rdGroups());
		}
		if (originalStrategy.getLineDetailLongtailThreshold() == null) {
			originalStrategy.setLineDetailLongtailThreshold(
					correspondingDefaultParams.getLineDetailLongtailThreshold());
		}
		if (originalStrategy.getStartScreenLongtailThreshold() == null) {
			originalStrategy.setStartScreenLongtailThreshold(
					correspondingDefaultParams.getStartScreenLongtailThreshold());
		}
	}

	private boolean isEmptyString(String str) {
		return str == null || str.trim().length() == 0;
	}

	private AdShowBaseStrategy getStrategyByParameters(AdStrategyParam adStrategyParams) {
		String strategyName = adStrategyParams.getStrategyName();
		switch (strategyName.trim()) {
		case "immune":
			return new ImmuneStrategy();
		case "own_ad_block":
			return new OwnAdBlockStrategy(adStrategyParams);
		case "roundrobin":
			return new RoundRobinStrategy(adStrategyParams);
		case "group":
			return new GroupStrategy(adStrategyParams);
		case "default":
			return new DefaultStrategy(adStrategyParams);
		default:
			logger.error("Return null because of invalid strategy name={}", strategyName.trim());
			return null;
		}
	}
}
