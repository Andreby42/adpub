package com.bus.chelaile.service.impl;

import java.util.HashMap;

import com.bus.chelaile.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.LineDetailAdMode;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdLineDetailInnerContent;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.LineAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;


import com.bus.chelaile.service.ManagerCommon;

/**
 * 自采买广告
 * 
 * @author zzz
 * 
 */
public class SelfManager {

	public static final int ANDROID_DETAIL_ADMODE_VC = 52;
	public static final int IOS_DETAIL_ADMODE_VC = 10160;

	protected static final Logger logger = LoggerFactory
			.getLogger(SelfManager.class);

	public BaseAdEntity getEntity(AdvParam advParam,
			AdPubCacheRecord cacheRecord, AdContent ad,String rightPushNum) {

		LineAdEntity entity = from(
				ad,
				advParam,
				cacheRecord.isUninterest(ad.getId()), rightPushNum, cacheRecord);
		

	//	cacheRecord.setAdHistory(cateGory);

//		recordAdd(adMap.get(cateGory.getAdId()).getRule(), advParam,
//				adMap.get(cateGory.getAdId()).getAds(),
//				ShowType.LINE_DETAIL.getType(), cacheRecord);

		return entity;

	}

	private LineAdEntity from(AdContent ad, AdvParam advParam,
			boolean isUninterest, String rightPushNum,
			AdPubCacheRecord cacheRecord) {
		if (ad == null) {
			return null;
		}
		LineAdEntity res = new LineAdEntity();
		res.fillBaseInfo(ad, advParam, new HashMap<String, String>());

		AdInnerContent inner = ad.getAdInnerContent();
		boolean hasChangeedMode = false;		// 点击右下角不感兴趣，会修改广告的adMode值。记录这个变化
		if (inner instanceof AdLineDetailInnerContent) {
			AdLineDetailInnerContent detailLineInner = (AdLineDetailInnerContent) inner;

			boolean pushNewAdMode = isSupportNewAdMode(advParam.getS(),
					advParam.getVc());
			String platform = advParam.getS();
			if (pushNewAdMode) {
				// 区分ios和android的图片
				res.setCombpic(res.getPicUrl(platform,
						detailLineInner.getIosURL(),
						detailLineInner.getAndroidURL(),
						detailLineInner.getPic()));
				res.setAdMode(detailLineInner.getAdMode());

				
				boolean isSilentTimePassed = ManagerCommon.isSilentTimePassed(advParam.getUdid(), ad.getId(), isUninterest, detailLineInner.getSilentTime(), cacheRecord); 
				if (isUninterest) {
					if (!isSilentTimePassed) {
						int adMode = res.getAdMode()
								^ LineDetailAdMode.LOWER_RIGHT.getMask(); // getMask()值是多少,应该是和admode一样
						if (adMode == 0) {
							// 如果没有需要显示图片的位置， 设置为0。
							logger.info("udid={},adMode==0,time={},advId={}",
									advParam.getUdid(),
									cacheRecord.getCloseAdsTime(ad.getId()),
									ad.getId());
							return null;
						} else {
							res.setAdMode(adMode);
							hasChangeedMode = true;
						}
					}
					// 取消过期的不敢兴趣
					else {
						cacheRecord.removeUninterestAds(ad.getId());
					}
				}

			} else {
				if (isUninterest) {
					// 旧版本的客户端，如果用户已经点击了关闭该广告之后，不再给用户推送同一个广告。
					return null;
				}
				res.setCombpic(Constants.EMPTY_STR);
				res.setPic(res.getPicUrl(platform, detailLineInner.getIosURL(),
						detailLineInner.getAndroidURL(),
						detailLineInner.getPic()));

			}
		} else {

			throw new IllegalArgumentException("=====> 错误的innerContent类型： "
					+ ((inner == null) ? null : inner.getClass()) + "; "
					+ inner + ",udid=" + advParam.getUdid());
		}
		// 限制右小角投放次数
		if (rightPushNum != null && !rightPushNum.equals("0")
				&& res.getAdMode() >= 8) {
			// 是否需要投放右下角图片
			if (!isNeedPushRightPic(advParam.getUdid(), ad.getId(),
					Integer.parseInt(rightPushNum))) {
				int adMode = res.getAdMode() - 8;
				if (0 >= adMode) {
					logger.info("adMode<0,udid={},advId={},",
							advParam.getUdid(), ad.getId());
					return null;
				}
				if(! hasChangeedMode) { //如果adMode值之前没有被修改，那么此时需要修改
					res.setAdMode(adMode);
				}
			}
		}
		// 处理通配符
		res.dealLink(advParam);
		res.setPriority(ad.getPriority());
		

		return res;
	}

	/**
	 * 是否需要投放右下角图片,并把值加1
	 * 
	 * @param udid
	 * @param advId
	 * @param ruleId
	 * @param rightPushNum
	 * @return true 需要投放,false 不需要投放
	 */
	private boolean isNeedPushRightPic(String udid, int advId, long rightPushNum) {
		String key = getRightPushNumKey(udid, advId);
//		Object value = CacheUtil.getFromRedis(key);
		Object value = CacheUtil.getFromOftenRedis(key);
		if (value instanceof String) {
			long havePushNum = Long.parseLong((String) value);
			if (havePushNum >= rightPushNum) {
				logger.info("is need push right pic return false");
				return false;
			}
			// 加一
			incrRightPushNum(udid, advId);
		} else {
			// 保存10天
			CacheUtil.setToRedis(key, 60 * 60 * 24 * 10, "1");
		}
		return true;
	}

	/**
	 * 增加右下角图片推送次数
	 * 保存一天
	 * @param udid
	 * @param advId
	 * @param ruleId
	 */
	private void incrRightPushNum(String udid, int advId) {
		String key = getRightPushNumKey(udid, advId);

//		CacheUtil.incrToCache(key, Constants.ONE_DAY_TIME);
		CacheUtil.incrToOftenRedis(key, Constants.ONE_DAY_TIME);

	}

	/**
	 * 右下角图片推送次数放到缓存中的key
	 * 
	 * @param udid
	 * @param advId
	 * @return
	 */
	private String getRightPushNumKey(String udid, int advId) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		return "Right_push_" + advId + "_" + todayStr + "_" + udid;
	}

	/**
	 * 清除右下角图片点击次数
	 * 
	 * @param udid
	 * @param advId
	 */
	public void clearRithPushNum(String udid, int advId) {
		String key = getRightPushNumKey(udid, advId);
//		Object value = CacheUtil.getFromRedis(key);
		Object value = CacheUtil.getFromOftenRedis(key);
		if (value != null) {
//			CacheUtil.redisDelete(key);
			CacheUtil.redisOftenDelete(key);
			logger.debug("clearRithPushNumKey:" + key);
		}

	}

	public boolean isSupportNewAdMode(String platform, int vc) {

		if (platform.equalsIgnoreCase("android")) {
			return vc >= ANDROID_DETAIL_ADMODE_VC;
		} else if (platform.equalsIgnoreCase("ios")) {
			return vc >= IOS_DETAIL_ADMODE_VC;
		} else if (platform.equalsIgnoreCase("h5")) {
			return true;
		}
		throw new IllegalArgumentException("错误的平台:" + platform);

	}

}
