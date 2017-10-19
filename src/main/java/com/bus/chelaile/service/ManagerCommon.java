package com.bus.chelaile.service;

import com.bus.chelaile.model.record.AdPubCacheRecord;

public class ManagerCommon {
	/**
	 * 判断这个广告的不敢兴趣是否失效
	 * @param udid
	 * @param advId
	 * @param isUninterest
	 * @param silentTime
	 * @param cacheRecord
	 * @return
	 */
	public static boolean isSilentTimePassed(String udid, int advId,
			boolean isUninterest, int silentTime, AdPubCacheRecord cacheRecord) {
		if (silentTime <= 0) { // silentTime 没有设置。
			return false;
		}
		long closeTime = cacheRecord.getCloseAdsTime(advId);
		// closeTime = 1472026666142L;
		if (closeTime < 0
				|| closeTime + silentTime * 60 * 1000 < System
						.currentTimeMillis()) {
			// 已经超过了设置的silentTime时间了。
			return true;
		}
		return false;
	}
}
