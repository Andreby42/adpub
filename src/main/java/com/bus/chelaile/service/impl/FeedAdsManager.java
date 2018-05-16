package com.bus.chelaile.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdFeedInnerContent;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.Tag;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.FeedAdArticleInfo;
import com.bus.chelaile.model.ads.entity.FeedAdEntity;
import com.bus.chelaile.model.ads.entity.FeedAdInfo;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.service.CommonService;
import com.bus.chelaile.service.RecordManager;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.New;

public class FeedAdsManager extends AbstractManager {

	/*
	 * 获取多条广告
	 */
	private List<FeedAdEntity> getEntities(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) {
		List<FeedAdEntity> entities = New.arrayList();
		List<Integer> ids = New.arrayList();
		for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
			AdContentCacheEle ad = entry.getValue();
			FeedAdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType, ad.getRule().getStartDate());

			if (entity != null) {
		         // 低版本，不予返回 ‘文章样式的feed流广告’
	            if(entity.getFeedAdType() == 2) {
	                Platform platform = Platform.from(advParam.getS());
	                if (platform.isAndriod(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_ANDROID_0208) {
	                    continue;
	                }
	                if (platform.isIOS(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_IOS_0208) {
	                    continue;
	                }
	                
	            }
			    
	            // 记录投放
                entities.add(entity);
                int adId = entity.getId();
                ids.add(adId);
                cacheRecord.buildAdPubCacheRecord(adId);
                if (adMap.get(adId).getRule().getUvLimit() > 0) {
                    // 首次访问
                    if (!cacheRecord.getUvMap().containsKey(adId)) {
                        adMap.get(adId).getRule().setUvCount();
                        cacheRecord.setAdToUvMap(adId);
                    }
                }
            } else {
                logger.info("feedAd 广告未创建~ , adId={}, isTop={}", ad.getAds().getId(), advParam.getIsTop());
                continue;
            }
			
			AnalysisLog
					.info("[FEED_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},provider_id={}",
							ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
							advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getLineId(),
							advParam.getStnName(), advParam.getNw(), advParam.getIp(), advParam.getDeviceType(),
							advParam.getLng(), advParam.getLat(),entity.getProvider_id());

			cacheRecord.setNoFeedAdHistoryMap(ids);
		}
		
		RecordManager.recordAdd(advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
		
		// 置顶位，只需要一个广告
		if(advParam.getIsTop() == 1) {
		    return entities;
		}
		return entities;
	}

	private FeedAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContent ad, ShowType showType, Date date) {
		FeedAdEntity res = new FeedAdEntity();
		
		// 第三方广告处理
		// 只有置顶位才返回这个
		AdFeedInnerContent feedInner1 = (AdFeedInnerContent) ad.getInnerContent();
		if(feedInner1.getIsSetTop() != advParam.getIsTop()) {  // 是否置顶，不匹配
		    return null;
		}
		if(feedInner1.getIsSetTop() != 1 && feedInner1.getProvider_id() > 0) {  // 非置顶位，不允许投放第三方广告
		    return null;
		}
		
		if(feedInner1.getProvider_id() > 0 && advParam.getIsTop() == 1) {
		    res = createSDKAds(feedInner1, ad);
            return res;
		}

		res.fillBaseInfo(ad, advParam, new HashMap<String, String>());

		res.dealLink(advParam);

		AdInnerContent inner = ad.getInnerContent();
		if (inner instanceof AdFeedInnerContent) {
			AdFeedInnerContent feedInner = (AdFeedInnerContent) inner;
			// 对空串情况做一下处理
			res.setPic(feedInner.getPic());
			res.setWidth(feedInner.getWidth());
			res.setHeight(feedInner.getHeight());
			res.setFeedId(feedInner.getFeedId());
			res.setFeedAdType(feedInner.getFeedAdType());
			res.setIsSetTop(feedInner.getIsSetTop());
			res.setImgsType(feedInner.getImgsType());
			if (feedInner.getTag() != null && feedInner.getTagId() != null 
					&& StringUtils.isNoneBlank(feedInner.getTag()) && StringUtils.isNoneBlank(feedInner.getTagId())) {
				res.setTag(new Tag(feedInner.getTag(), feedInner.getTagId()));
			}

			if (feedInner.getFeedAdType() == 0) {	 // 话题样式
				res.setFeedInfo(new FeedAdInfo(feedInner.getFeedAdTitle(), date.getTime(), feedInner.getSlogan(),
						feedInner.getIcon(), feedInner.getLikeNum(), feedInner.getFeedTag(), feedInner.getIsSetTop()));
			} else if(feedInner.getFeedAdType() == 1) { // 透视样式
				res.setFeedInfo(new FeedAdInfo(null, 0L, null, null, 0, feedInner.getFeedTag(), feedInner.getIsSetTop()));
			} else if(feedInner.getFeedAdType() == 2 || feedInner.getFeedAdType() == 3) { // 文章样式 | 图片样式
				res.setArticleInfo(new FeedAdArticleInfo(feedInner.getSlogan(), date.getTime(), feedInner.getFeedTag(),
					new ArrayList<String>(), feedInner.getFeedAdTitle()));  // TODO
				if(StringUtils.isNoneBlank(feedInner.getPic())) {
					for(String s : feedInner.getPic().split(";")) {
						res.getArticleInfo().getImgs().add(s);
					}
				} else {
					logger.error("feed流广告 文章样式，没有图片，advId={}, imgs={}", res.getId(), feedInner.getPic());
				}
			}
		} else {
			throw new IllegalArgumentException("=====> 错误的innerContent类型： "
					+ ((inner == null) ? null : inner.getClass()) + "; " + inner + ",udid=" + advParam.getUdid());
		}

		return res;
	}

	// TODO 临时这样处理
	public List<FeedAdEntity> doFeedAdService(AdvParam advParam, ShowType showType, boolean b, QueryParam queryParam,
			boolean c) {
		if (!beforeCheck(advParam, showType)) {
			return null;
		}

		// 取得所有刻意投放广告
		List<AdContentCacheEle> adsList = CommonService.getAllAdsList(advParam.getUdid(), advParam.getAccountId(),
				showType);

		if (adsList == null || adsList.size() == 0) {
			logger.info("[getallavailableAds ISNULL]:udid={}, adtype={}, isNeedApi={}, type={}, ac={}, s={}",
					advParam.getUdid(), showType, false, advParam.getType(), advParam.getAccountId(), advParam.getS());
			return null;
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
					+ "cityId={}, v={}, vc={}, li={}, sn={}", advParam.getUdid(), showType, false, advParam.getType(),
					adIdStr, advParam.getAccountId(), advParam.getS(), advParam.getCityId(), advParam.getV(),
					advParam.getVc(), advParam.getLineId(), advParam.getStnName());
		}

		AdPubCacheRecord cacheRecord = AdvCache.getAdPubRecordFromCache(advParam.getUdid(),
				ShowType.DOUBLE_COLUMN.getType());
		if (cacheRecord == null) {
			cacheRecord = new AdPubCacheRecord();
		}

		// 需要排序
		Collections.sort(adsList, FEEDAD_CONTENT_COMPARATOR);
		Map<Integer, AdContentCacheEle> adMap = New.hashMap();
		// 把所有符合规则的广告放到map中
		handleAds(adMap, adsList, showType, advParam, cacheRecord, true, queryParam);

		if (adMap.size() == 0) {
			// throw new IllegalArgumentException("handleAds出错");
			// 此处，经过规则判断不返回广告，如果是feedAd，需要记录次数
			List<Integer> ids = New.arrayList();
			ids.add(-1);
			cacheRecord.setNoFeedAdHistoryMap(ids);
			AdvCache.setAdPubRecordToCache(cacheRecord, advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType());
			return null;
		}

		// 没有第三方广告,处理自采买广告
		logger.info("过滤条件后，得到feedAd数目为：{}", adMap.size());
		
//		BaseAdEntity entity = dealEntity(null, advParam, cacheRecord, adMap, showType, queryParam, true);
		
		return getEntities(null, advParam, cacheRecord, adMap, showType, queryParam, true);

	}

	public static void main(String[] args){
		String url = "http%3A%2F%2F121.40.95.166%3A7000%2Foutman%2Fadv%2FqueryAdv%3Fid%3D12024";
	}

	@Override
	protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final Comparator<AdContentCacheEle> FEEDAD_CONTENT_COMPARATOR = new Comparator<AdContentCacheEle>() {
		@Override
		public int compare(AdContentCacheEle o1, AdContentCacheEle o2) {
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return o2.getAds().getPriority() - o1.getAds().getPriority();
		}
	};

	@Override
	protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
		return null;
	}

	
	// 2018-05-05 ，feed流广告，支持gdt：原生、banner
    private FeedAdEntity createSDKAds(AdFeedInnerContent inner, AdContent ad) {
        FeedAdEntity entity = new FeedAdEntity();
        entity.setId(ad.getId());
        entity.setProvider_id(inner.getProvider_id() + "");
        entity.setType(3); // 第三方广告
        entity.setTitle(ad.getTitle());
        entity.setApi_type(inner.getApi_type());
        entity.setIsSetTop(1);
        
        return entity;
    }
    
}
