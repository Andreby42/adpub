package com.bus.chelaile.strategy;

import java.util.*;


public class AdGroupFilter {
//	private static int ownAdType = PropertiesReaderWrapper.readInt(
//			"adv.strategy.third.party.adv.map.own", 1);
	
//	private static int ownAdType = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
//			"adv.strategy.third.party.adv.map.own","1"));

	private static List<Set<Integer>> parseAdGroupExclusionInfo(String info) {
		List<Set<Integer>> infoSetList = new ArrayList<>();
		if (info != null && info.trim().length() != 0) {
			String[] adGroups = info.split("\\|");
			for (String adGroup : adGroups) {
				String[] adStrs = adGroup.split("&");
				Integer[] ads = new Integer[adStrs.length];
				for (int i = 0; i < adStrs.length; i++) {
					ads[i] = Integer.parseInt(adStrs[i]);
				}
				Set<Integer> adSet = new HashSet<>();
				adSet.addAll(Arrays.asList(ads));
				infoSetList.add(adSet);
			}
		}
		return infoSetList;
	}

	public static List<AdInfo> filterAvailableList(String adGroupExclusionInfo,
			Map<AdCategory, Integer> adHistoryMap, List<AdInfo> availableAds) {
		if (availableAds == null) {
			return null;
		}
		List<Set<Integer>> udidAdGroup = parseAdGroupExclusionInfo(adGroupExclusionInfo);
		List<AdInfo> filteredList = new ArrayList<>(availableAds);
		// No ad group info, or the user never requests ad before, just shuffle the
		// available list.
		if (udidAdGroup == null || adHistoryMap == null || udidAdGroup.size() == 0) {
			Collections.shuffle(filteredList, new Random(System.nanoTime()));
			return filteredList;
		} else {
			HashSet<Integer> removedAdSet = new HashSet<>();
			for (Map.Entry<AdCategory, Integer> entry : adHistoryMap.entrySet()) {
				int adId = entry.getKey().getAdId();
				for (Set<Integer> exclusionSet : udidAdGroup) {
					if (exclusionSet.contains(adId)) {
						for (Integer i : exclusionSet) {
							if (i != adId) {
								removedAdSet.add(i);
							}
						}
					}
				}
			}
			Iterator<AdInfo> itr = filteredList.iterator();
			while (itr.hasNext()) {
				AdInfo adInfo = itr.next();
				if (removedAdSet.contains(adInfo.getAdid())) {
					itr.remove();
				}
			}
			Collections.shuffle(filteredList, new Random(System.nanoTime()));
			return filteredList;
		}
	}

//	private static void test1() {
//		String exclusionInfo = "2792&2772|2792&2793|2793&2826";
//		System.out.println(parseAdGroupExclusionInfo(exclusionInfo));
//		Map<AdCategory, Integer> historyMap = new HashMap<>();
//		historyMap.put(new AdCategory(2792, 1, 1), 3);
//		List<AdInfo> available = new ArrayList<>();
//		available.add(new AdInfo(2793, 61));
//		System.out.println(filterAvailableList(exclusionInfo, historyMap, available).toString());
//	}
//
//	private static void test2() {
//		String exclusionInfo = "1&2&3|4&5&6|7&8&9";
//		System.out.println(parseAdGroupExclusionInfo(exclusionInfo));
//		Map<AdCategory, Integer> historyMap = new HashMap<>();
//		historyMap.put(new AdCategory(1, 1, 1), 3);
//		historyMap.put(new AdCategory(4, 1, 1), 3);
//		List<AdInfo> available = new ArrayList<>();
//		available.add(new AdInfo(2, 61));
//		available.add(new AdInfo(3, 61));
//		available.add(new AdInfo(5, 61));
//		available.add(new AdInfo(6, 61));
//		available.add(new AdInfo(7, 61));
//		available.add(new AdInfo(8, 61));
//		available.add(new AdInfo(9, 61));
//		System.out.println(filterAvailableList(exclusionInfo, historyMap, available).toString());
//	}
}
