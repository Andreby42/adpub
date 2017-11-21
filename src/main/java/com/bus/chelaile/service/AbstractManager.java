package com.bus.chelaile.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdLineDetailInnerContent;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.model.rule.UserClickRate;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.strategy.AdDispatcher;
import com.bus.chelaile.thread.CalculatePerMinCount;
import com.bus.chelaile.util.New;

public abstract class AbstractManager {
	@Autowired
	private AdvInvalidService invaildService;

	protected static final Logger logger = LoggerFactory.getLogger(AbstractManager.class);
	
	/**
	 * 最初的检测
	 * 
	 * @param advParam
	 * @param showType
	 * @return true 检测成功,false 检测失败
	 */
	protected boolean beforeCheck(AdvParam advParam, ShowType showType) {
		if (SynchronizationControl.isReload()) {
			logger.info("reload is Running");
			return false;
		}

		// 乘车页和活动页不去除广告
		if (!showType.getType().equals(ShowType.ACTIVE_DETAIL.getType())
				&& !showType.getType().equals(ShowType.RIDE_DETAIL.getType())) {

			if (isInvalidAccountId(advParam.getAccountId())) {
				logger.info("取消了广告,accountId={},udid={}", advParam.getAccountId(), advParam.getUdid());
				return false;
			}
		}
		
		// 详情页cshow非空，不等于linedetail的不返回
		if(showType.getType().equals(ShowType.LINE_DETAIL)) {
			if(StringUtils.isNoneBlank(advParam.getCshow()) && !advParam.getCshow().equals(Constants.CSHOW_LINEDETAIL)) {
				return false;
			}
		}

		// // udid不为空
		// if (StringUtils.isBlank(advParam.getUdid()) ||
		// advParam.getUdid().equals("null")) {
		// // logger.error("udid is NULL ,return null! ");
		// return false;
		// }
		return true;
	}

	/**
	 * 
	 * @param advParam
	 * @param showType
	 * @param isNeedApid
	 *            false 不调用第三方,true 调用第三方,旧版本的开屏和浮层接口该值为false,单双栏也为false
	 * @throws Exception
	 */
	public BaseAdEntity doService(AdvParam advParam, ShowType showType, boolean isNeedApid, QueryParam queryParam, boolean isRecord) {
		// 检测失败直接返回
		if (!beforeCheck(advParam, showType)) {
			return null;
		}

		// //保存有效用户的udid
		// if((showType == ShowType.OPEN_SCREEN || showType ==
		// ShowType.FULL_SCREEN)
		// && advParam.getUdid() != null) {
		// AdvCache.saveRealUsers(advParam.getUdid());
		// }

		// 取得所有刻意投放广告
		List<AdContentCacheEle> adsList = CommonService.getAllAdsList(advParam.getUdid(), advParam.getAccountId(), showType);

		if (adsList == null || adsList.size() == 0) {
			logger.info("[getallavailableAds ISNULL]:udid={}, adtype={}, isNeedApi={}, type={}, ac={}, s={}",
					advParam.getUdid(), showType, isNeedApid, advParam.getType(), advParam.getAccountId(),
					advParam.getS());
			// 不需要第三方的直接返回
			if (!isNeedApid || showType == ShowType.DOUBLE_COLUMN || showType == ShowType.SINGLE_COLUMN
					|| showType == ShowType.FULL_SCREEN) {
				return null;
			}
		} else {
			// 合并广告
			adsList = CommonService.mergeAllAds(adsList); // 需要按照adid和ruleid做合并
			String adIdStr = "";
			for (AdContentCacheEle ad : adsList) {
				adIdStr += ad.getAds().getId();
				for (Rule rule : ad.getRules()) {
					adIdStr += "->" + rule.getRuleId();
				}
				adIdStr += ";";
			}
			logger.info("[getallavailableAds]:udid={}, adtype={}, isNeedApi={}, type={}, advIds={}, ac={},s={}, "
					+ "cityId={}, v={}, vc={}, li={}, sn={}", advParam.getUdid(), showType, isNeedApid,
					advParam.getType(), adIdStr, advParam.getAccountId(), advParam.getS(), advParam.getCityId(),
					advParam.getV(), advParam.getVc(), advParam.getLineId(), advParam.getStnName());
		}

		AdPubCacheRecord cacheRecord = null;
		// 放缓存的时候除了线路详情就是双栏
		if (showType.getType().equals(ShowType.LINE_DETAIL.getType())) {
			cacheRecord = AdvCache.getAdPubRecordFromCache(advParam.getUdid(), ShowType.LINE_DETAIL.getType());
		} else {
			cacheRecord = AdvCache.getAdPubRecordFromCache(advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType());
		}

		if (cacheRecord == null) {
			cacheRecord = new AdPubCacheRecord();
		}

		Map<Integer, AdContentCacheEle> adMap = null;
		// 单双栏、浮层、乘车页、活动页、和旧版本的开屏接口不走策略
		// 站点广告不走策略
		if (!isNeedApid || showType == ShowType.DOUBLE_COLUMN || showType == ShowType.SINGLE_COLUMN
				|| showType == ShowType.FULL_SCREEN) {
			// 需要排序
			Collections.sort(adsList, AD_CONTENT_COMPARATOR);

			adMap = New.hashMap();
			// 把所有符合规则的广告放到map中
			handleAds(adMap, adsList, showType, advParam, cacheRecord, isNeedApid, queryParam);

			if (adMap.size() == 0) {
				// throw new IllegalArgumentException("handleAds出错");
				return null;
			}

			// 没有第三方广告,处理自才买广告
			return getEntity(null, advParam, cacheRecord, adMap, showType, queryParam, isRecord);

		} else {

			if (adsList != null && adsList.size() > 0) {
				adMap = New.hashMap();
				// 把所有符合规则的广告放到map中
				handleAds(adMap, adsList, showType, advParam, cacheRecord, isNeedApid, queryParam);
			}

		}

		// 从训哥儿那里获取策略
		// 到这一步的都是 isNeedApid=true，目前只有新版的详情页和新版的开屏
		AdCategory cateGory = null;
		try {
			if (showType != ShowType.OPEN_SCREEN && showType != ShowType.FULL_SCREEN) {
				cateGory = AdDispatcher.getAdCategory(advParam, cacheRecord, adMap);
				if (cateGory != null && cateGory.getApiType() > 3) {
					throw new IllegalArgumentException("请求了线路详情广告,然而返回的广告类型为" + cateGory.getApiType());
				}
			} else {
				cateGory = AdDispatcher.getOpenAdCategory(advParam, cacheRecord, adMap);
				if (cateGory != null && cateGory.getApiType() <= 3 && cateGory.getApiType() != -1) {
					throw new IllegalArgumentException("请求了开屏广告,然而返回的广告类型为" + cateGory.getApiType());
				}
			}

		} catch (IllegalArgumentException e) {
			logger.error("udid={},category errormessage={}", advParam.getUdid(), e.getMessage());
			return null;
		}

//		logger.info("[cateGoryInfo]:udid={}, cateGory={}", advParam.getUdid(), cateGory);
		if (cateGory != null) {
			// 暂时失联的时候不显示第三方广告
			if (cateGory.getAdType() > 1 && advParam.getInState() != null
					&& (advParam.getInState().equals("-2") || advParam.getInState().equals("-5"))) {
				return null;
			}
			return getEntity(cateGory, advParam, cacheRecord, adMap, showType, queryParam, isRecord);
		} else { // cateGory为空的时候记录
			cacheRecord.buildAdPubCacheRecord(-1);
			if (showType == ShowType.LINE_DETAIL) {
				cacheRecord.setAdHistory(new AdCategory(-1, -1, -1));
				AdvCache.setAdPubRecordToCache(cacheRecord, advParam.getUdid(), ShowType.LINE_DETAIL.getType());
			}
			// 开屏和浮层
			else {
				cacheRecord.setOpenAdHistory(new AdCategory(-1, -1, -1));
				AdvCache.setAdPubRecordToCache(cacheRecord, advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType());
			}

			return null;
		}
	}


	/**
	 * 把符合规则的广告放到map中
	 * 
	 * @param adMap
	 * @param adsList
	 * @param showType
	 * @param advParam
	 * @param cacheRecord
	 * @param num
	 *            1 的时候只要一个广告,-1的时候全部
	 */
	private void setAds(Map<Integer, AdContentCacheEle> adMap, List<AdContentCacheEle> adsList, ShowType showType,
			AdvParam advParam, AdPubCacheRecord cacheRecord, int num, boolean isNeedApid, QueryParam queryParam) {
		// 取得符合规则的广告
		for (AdContentCacheEle cacheEle : adsList) {
			AdContent ad = cacheEle.getAds();
			UserClickRate clickRate = cacheEle.getUserClickRate();

			if (!ad.getShowType().equals(showType.getType())) {
				continue;
			}
			// 线路详情单独处理
			if (ad.getShowType().equals(ShowType.LINE_DETAIL.getType())) {
				// 当点击了右下角不感兴趣
				if (!lineDetailIsSilentTimePassed(ad, cacheRecord, advParam)) {
					continue;
				}
			} else if (ad.getShowType().equals(ShowType.DOUBLE_COLUMN.getType())) {
				// 当点击了双栏不感兴趣
				if (cacheRecord.isUninterest(ad.getId())) {
					logger.info("isSilentTimePassed return false,adtype={}, advId={},udid={}", showType, ad.getId(),
							advParam.getUdid());
					continue;
				}
				
				// 如果距离太远，不投放单车。
				if(ad.getTargetType() == Constants.DOUBLE_BICYCLE_ADV) {
					if( !CommonService.isShowBikeByDistance(advParam)) {
						continue;
					}
					
					if(advParam.getRideStatus() == 1) {	// 0没骑车 ，1骑行中， 3骑行结束
						logger.info("rideStatus is riding, udid={}", advParam.getUdid());
						continue;
					}
				}
			}
			// 遍历所有规则
			for (Rule rule : cacheEle.getRules()) {
				if (!ruleCheck(rule, advParam, ad, cacheRecord, showType, isNeedApid, queryParam, clickRate)) {
					continue;
				}
				AdContentCacheEle adContentCacheEle = new AdContentCacheEle();
				adContentCacheEle.setAds(ad);
				adContentCacheEle.setRule(rule);
				adMap.put(ad.getId(), adContentCacheEle);
				if (num == 1) {
					return;
				}
				break;
			}
		}
	}

	/**
	 * 当只有右下角图片的时候判断
	 * 
	 * @param ad
	 * @param cacheRecord
	 * @param advParam
	 * @return
	 */
	private boolean lineDetailIsSilentTimePassed(AdContent ad, AdPubCacheRecord cacheRecord, AdvParam advParam) {

		boolean isUninterest = cacheRecord.isUninterest(ad.getId());

		if (isUninterest) {
			AdLineDetailInnerContent inner = (AdLineDetailInnerContent) ad.getInnerContent();
			// 只有右小角
			if (inner.getAdMode() == 8) {
				// 在设置超时时间之内
				if (!ManagerCommon.isSilentTimePassed(advParam.getUdid(), ad.getId(), isUninterest,
						inner.getSilentTime(), cacheRecord)) {
					logger.info("isSilentTimePassed return false,adtype={}, advId={}, udid={}", ShowType.LINE_DETAIL,
							ad.getId(), advParam.getUdid());
					return false;
				} else {
					// 超过时间限制了
					cacheRecord.removeUninterestAds(ad.getId());
				}
			}
			logger.info("udid={},advId={},isUninterest", advParam.getUdid(), ad.getId());
		}
		return true;
	}

	/*
	 * 双栏点击了不感兴趣，默认15分钟 true：继续投放广告 false:不投放广告
	 */
	public boolean doubleAdsIsSilentTimePassed(AdContent ad, AdPubCacheRecord cacheRecord, AdvParam advParam) {
		boolean isUninterest = cacheRecord.isUninterest(ad.getId());
		if (isUninterest) {
			if (!ManagerCommon.isSilentTimePassed(advParam.getUdid(), ad.getId(), isUninterest, 15, cacheRecord)) {
				logger.info("isSilentTimePassed return false,advId={},udid={}", ad.getId(), advParam.getUdid());
				return false;
			} else {
				// 超过时间限制了
				cacheRecord.removeUninterestAds(ad.getId());
			}
		}
		return true;
	}

	/***
	 * 广告规则校验
	 * 
	 * @param rule
	 * @param advParam
	 * @param ad
	 * @param cacheRecord
	 * @param showType
	 * @param isNeedApid
	 * @return true 可投放 fasle 不可投放
	 */
	private boolean ruleCheck(Rule rule, AdvParam advParam, AdContent ad, AdPubCacheRecord cacheRecord,
			ShowType showType, boolean isNeedApid, QueryParam queryParam, UserClickRate clickRate) {

		if (advParam.getUdid() != null && advParam.getUdid().equals("yuanxiang")) {
			return false;
		}
		// 存在黑名单中
		if (StaticAds.isBlack(ad.getId(), advParam.getUdid())) {
//			logger.info("black list,advId={},udid={}", ad.getId(), advParam.getUdid());
			return false;
		}
		// 开屏和浮层的老接口preloadAds需要返回两天的数据
		// 仅仅 preloadAds接口用到这一块
		if (queryParam.isOldMany()) {
			// 当前日期大于结束日期
			if (rule.isEndDateOverdue()) {
//				logger.info("isEndDateOverdue return false,ruleId={},udid={}", rule.getRuleId(), advParam.getUdid());
				return false;
			}
		} else {
			// 是否过期
			if (rule.isOverdue()) {
//				logger.info("isOverdue return false,ruleId={},udid={}", rule.getRuleId(), advParam.getUdid());
				return false;
			}
		}

		if (rule.hasCities() && !rule.isCityMatch(advParam.getCityId())) {
//			logger.info("isCityMatch return false,ruleId={},cityId={},udid={}", rule.getRuleId(), advParam.getCityId(),
//					advParam.getUdid());
			return false;
		}
		if (rule.hasPlatforms() && !rule.isPlatformMatch(advParam.getS(), advParam.getH5Src())) {
//			logger.info("isPlatformMatch return false,ruleId={},s={},src={},udid={}", rule.getRuleId(), advParam.getS(),
//					advParam.getH5Src(), advParam.getUdid());
			return false;
		}
		if (rule.hasVersions() && !rule.isVersionMatch(advParam.getV())) {
//			logger.info("isVersionMatch return false,ruleId={},version={},udid={}", rule.getRuleId(), advParam.getV(),
//					advParam.getUdid());
			return false;
		}
		if (rule.hasNetStatus() && !rule.isNetStatusMatch(advParam.getNw())) {
//			logger.info("hasNetStatus return false,ruleId={},nw={},udid={}", rule.getRuleId(), advParam.getNw(),
//					advParam.getUdid());
			return false;
		}
		if (!rule.isUserTypeMatch(advParam)) {
//			logger.info("isUserTypeMatch return false,ruleId={},udid={}", rule.getRuleId(), advParam.getUdid());
			return false;
		} 

		if (!rule.isLineStationMap(advParam.getLineId(), advParam.getStnName(), advParam.getStnOrder(),
				advParam.getUdid())) {
//			logger.info("isLineStationMap return false,ruleId={},lineId={},stnName={},order={},udid={}",
//					rule.getRuleId(), advParam.getLineId(), advParam.getStnName(), advParam.getStnOrder(),
//					advParam.getUdid());
			return false;
		}
		if (!rule.isStationMatch(advParam.getStnName())) {
//			logger.info("isStationMatch return false,ruleId={},stnName={},udid={}", rule.getRuleId(),
//					advParam.getStnName(), advParam.getUdid());
			return false;
		}

		if (!rule.isPostionMatch(advParam.getLat(), advParam.getLng())) {
			logger.info("isPostionMatch return false,ruleId={},lng={},lat={},udid={}", rule.getRuleId(),
					advParam.getLng(), advParam.getLat(), advParam.getUdid());
			return false;
		}

		// 总点击次数判断
		if (rule.getTotalClickPV() > 0 && rule.currentTotalClickPV(ad) >= rule.getTotalClickPV()) {
//			logger.info("totalClickPV return false, ruleId={}, udid={}", rule.getRuleId(), advParam.getUdid());
			return false;
		}
		
		// 每个人点击次数判断
		if (rule.getIsClickEndPush() > 0 && cacheRecord.hasClicked(ad.getId())) {	//点击后不再投放
			logger.info("hasClicked ad return false, ruleId={}, udid={}", rule.getRuleId(), advParam.getUdid());
			return false;
		}

		if (rule.isOverUvCount(advParam.getUdid())) {
			// 如果uvlimit次数已经饱和,查看该用户是否投放过,没投放过就返回了
			if (!cacheRecord.isSendUv(ad.getId())) {
				logger.info("isOverUvCount return false,ruleId={},udid={}", rule.getRuleId(), advParam.getUdid());
				return false;
			}
		}

		// 判断自动黑名单
		if (rule.getUvLimit() > 0 && cacheRecord.isDisplayUv(ad.getId(), rule.getAutoBlackList())) {
			logger.info("autoBlack return false,ruleId={},udid={}", rule.getRuleId(), advParam.getUdid());
			return false;
		}

		// 次数判断
		// 包括，每人点击次数限制、每人投放次数上限、每人days天内总投放次数上限
		if (cacheRecord != null && !cacheRecord.todayCanPub(ad, rule)) {
			logger.info("todayCanPub return false,ruleId={},udid={}", rule.getRuleId(), advParam.getUdid());
			return false;
		}

		// 每个时间段的发送次数，目前是考察到分钟
		if (rule.getTotalCount() > 0
				&& !rule.adTimeCounts(ad.getId(), rule.getRuleId(), cacheRecord, advParam.getUdid(), false)) {
//			logger.info("hasAdTimeCounts return false,ruleId={},udid={}", rule.getRuleId(), advParam.getUdid());

			return false;
		}
		
		// 乘车页广告和聊天室广告
		if (showType == ShowType.RIDE_DETAIL) {
			// 0 显示乘车页 1 显示聊天室 2 均显示 3 均不显示
			if (rule.getChatOrRide() == 3) {
//				logger.info("getChatOrRide return false,ruleId={},type={},udid={}", rule.getRuleId(),
//						advParam.getType(), advParam.getUdid());
				return false;
			}
			// 聊天室的请求
			else if (advParam.getType() == 2 && rule.getChatOrRide() != 1 && rule.getChatOrRide() != 2) {
//				logger.info("getChatOrRide return false,ruleId={},type={},udid={}", rule.getRuleId(),
//						advParam.getType(), advParam.getUdid());
				return false;
			}
			// 乘车页的请求
			else if (advParam.getType() == 1 && rule.getChatOrRide() != 0 && rule.getChatOrRide() != 2) {
//				logger.info("getChatOrRide return false,ruleId={},type={},udid={}", rule.getRuleId(),
//						advParam.getType(), advParam.getUdid());
				return false;
			}
		}
		// udid 模糊匹配
		if (StringUtils.isNoneBlank(rule.getUdidPattern()) && advParam.getUdid() != null) {
			if (!advParam.getUdid().matches(rule.getUdidPattern())) {
//				logger.info("udid pattern matche return false,ruleId={},udidPattern={},udid={}", rule.getRuleId(),
//						rule.getUdidPattern(), advParam.getUdid());
				return false;
			}
		}

		// 判断点击概率是否达标
		if (clickRate != null) {
			double rate = getClickStandardRate(advParam.getUdid(), ad.getId(), rule.getRuleId(), showType);
//			logger.info("clickRate info : ruleId={},udid={},rate={},rateStandard={}", rule.getRuleId(),
//					advParam.getUdid(), clickRate.getRate(), rate);
			if (rate >= 0.0 && clickRate.getRate() < rate) {
//				logger.info("clickRate return false,ruleId={},udid={},rate={},rateStandard={}", rule.getRuleId(),
//						advParam.getUdid(), clickRate.getRate(), rate);
				return false;
			}
		}

		return true;
	}





	/**
	 * 是否通过花钱取消广告
	 * 
	 * @param accountId
	 * @return
	 */
	private boolean isInvalidAccountId(String accountId) {
		if (accountId == null) {
			return false;
		}
		try {
			return invaildService.isInvalid(accountId);
		} catch (Exception e) {
			logger.error(e.getMessage() + ",accountId:" + accountId, e);
		}
		return false;
	}

	

	/**
	 * 应该按照优先级倒叙排序
	 */
	private static final Comparator<AdContentCacheEle> AD_CONTENT_COMPARATOR = new Comparator<AdContentCacheEle>() {
		@Override
		public int compare(AdContentCacheEle o1, AdContentCacheEle o2) {
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return o2.getAds().getPriority() - o1.getAds().getPriority();
		}
	};

	/**
	 * 记录 AdPubCacheRecord
	 * 
	 * @param cateGory
	 * @param advParam
	 * @param cacheRecord
	 * @param adMap
	 * @param showType
	 * @param queryParam
	 * @return
	 * @throws Exception
	 */
	private BaseAdEntity getEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) {
		BaseAdEntity entity = null;
		try {
			entity = dealEntity(cateGory, advParam, cacheRecord, adMap, showType, queryParam, isRecord);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return entity;
		}
		boolean isSelfAd = false;
		int adId = -1;
		boolean hasSendSelfAd = false;	// 今天之前是否投放过该自采买广告
		if (cateGory == null && entity != null) // 老版，不需要第三方广告，仅处理自采买
		{
			isSelfAd = true;
			adId = entity.getId();
			hasSendSelfAd = cacheRecord.hasPulished(adId);
		}
		if (cateGory != null && cateGory.getAdType() == 1) // 新版，策略返回自采买广告
		{
			isSelfAd = true;
			adId = cateGory.getAdId();
			hasSendSelfAd = cacheRecord.hasPulished(adId);
		}

		boolean isAutoRefresh = false;
		if (advParam.getStatsAct() != null && advParam.getStatsAct().equals(Constants.STATSACT_AUTO_REFRESH)) {
			isAutoRefresh = true;
		}
		if (isSelfAd && !(isAutoRefresh && hasSendSelfAd)) { // 记录自采买广告的次数
			cacheRecord.buildAdPubCacheRecord(adId);
			if (adMap.get(adId).getRule().getUvLimit() > 0) {
				// 首次访问
				if (!cacheRecord.getUvMap().containsKey(adId)) {
					adMap.get(adId).getRule().setUvCount();
					cacheRecord.setAdToUvMap(adId);
				}
			}
		}

		if (showType == ShowType.LINE_DETAIL) {
			RecordManager.recordAdd(advParam.getUdid(), showType.getType(), cacheRecord);
		} else {
			RecordManager.recordAdd(advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
		}

		return entity;

	}

	protected void adTimeCounts(AdPubCacheRecord cacheRecord, String udid, AdContentCacheEle adc) {
		// 每个时间段的发送次数
		// if (adc.getRule().hasAdTimeCounts()
		// && !adc.getRule().adTimeCounts(adc.getRule().getRuleId(),
		// cacheRecord, udid, true)) {
		// logger.info("hasAdTimeCounts return false,ruleId={},udid={}",
		// adc.getRule().getRuleId(), udid);
		// throw new IllegalArgumentException("hasAdTimeCounts error");
		// }

		if (adc.getRule().getTotalCount() > 0) {
			// 记录firstClickMap到缓存，和每分钟点击数到redis
			adc.getRule().adTimeCounts(adc.getAds().getId(), adc.getRule().getRuleId(), cacheRecord, udid, true);
			// 记录总投放pv到缓存
//			logger.info("记录投放pv次数 advId={}, ruleId={}", adc.getAds().getId(), adc.getRule().getRuleId());
			DynamicRegulation.IncValueSedPV(adc.getAds().getId(), adc.getRule().getRuleId());
		}
	}

	protected abstract BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) throws Exception;



	private void handleAds(Map<Integer, AdContentCacheEle> adMap, List<AdContentCacheEle> adsList, ShowType showType,
			AdvParam advParam, AdPubCacheRecord cacheRecord, boolean isNeedApid, QueryParam queryParam) {
		if (isNeedApid) {
			setAds(adMap, adsList, showType, advParam, cacheRecord, -1, isNeedApid, queryParam);
		} else {
			// 不需要走策略的只返回一条广告
			setAds(adMap, adsList, showType, advParam, cacheRecord, 1, isNeedApid, queryParam);
		}
	}


	/**
	 * 获取用户该广告的点击率
	 * 
	 * @param udid
	 * @param advId
	 * @param showType
	 * @return
	 */
	private double getClickStandardRate(String udid, int advId, String ruleId, ShowType showType) {
		
		boolean delUdidRule = false;
		if (delUdidRule) {
			StaticAds.delUdidRule(udid, advId, showType);
		}
		
		return CalculatePerMinCount.getCTRRate(advId + "#" + ruleId);
	}

}
