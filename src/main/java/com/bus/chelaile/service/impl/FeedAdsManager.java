package com.bus.chelaile.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

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
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.New;

public class FeedAdsManager extends AbstractManager {

    private FeedAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContent ad, ShowType showType, Date date) {
        FeedAdEntity res = new FeedAdEntity();

        AdFeedInnerContent feedInner1 = (AdFeedInnerContent) ad.getInnerContent();
        if (feedInner1.getIsSetTop() != advParam.getIsTop()) { // 是否置顶，不匹配
            return null;
        }
        if (feedInner1.getIsSetTop() != 1 && feedInner1.getProvider_id() > 0) { // 非置顶位，不允许投放第三方广告
            return null;
        }

        // 第三方广告处理
        // 只有置顶位才返回这个
        // 此处需要加上版本控制
        if (feedInner1.getProvider_id() > 0 && advParam.getIsTop() == 1) {
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
            if (feedInner.getTag() != null && feedInner.getTagId() != null && StringUtils.isNoneBlank(feedInner.getTag())
                    && StringUtils.isNoneBlank(feedInner.getTagId())) {
                res.setTag(new Tag(feedInner.getTag(), feedInner.getTagId()));
            }

            if (feedInner.getFeedAdType() == 0) { // 话题样式
                res.setFeedInfo(new FeedAdInfo(feedInner.getFeedAdTitle(), date.getTime(), feedInner.getSlogan(),
                        feedInner.getIcon(), feedInner.getLikeNum(), feedInner.getFeedTag(), feedInner.getIsSetTop()));
            } else if (feedInner.getFeedAdType() == 1) { // 透视样式
                res.setFeedInfo(new FeedAdInfo(null, 0L, null, null, 0, feedInner.getFeedTag(), feedInner.getIsSetTop()));
            } else if (feedInner.getFeedAdType() == 2 || feedInner.getFeedAdType() == 3) { // 文章样式 | 图片样式
                res.setArticleInfo(new FeedAdArticleInfo(feedInner.getSlogan(), date.getTime(), feedInner.getFeedTag(),
                        new ArrayList<String>(), feedInner.getFeedAdTitle())); // TODO
                if (StringUtils.isNoneBlank(feedInner.getPic())) {
                    for (String s : feedInner.getPic().split(";")) {
                        res.getArticleInfo().getImgs().add(s);
                    }
                } else {
                    logger.error("feed流广告 文章样式，没有图片，advId={}, imgs={}", res.getId(), feedInner.getPic());
                }
            }
        } else {
            throw new IllegalArgumentException("=====> 错误的innerContent类型： " + ((inner == null) ? null : inner.getClass()) + "; "
                    + inner + ",udid=" + advParam.getUdid());
        }

        return res;
    }

    public static void main(String[] args) {
        String url = "http%3A%2F%2F121.40.95.166%3A7000%2Foutman%2Fadv%2FqueryAdv%3Fid%3D12024";
    }

    @Override
    protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
        List<BaseAdEntity> entities = New.arrayList();
        List<Integer> ids = New.arrayList();
        for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
            AdContentCacheEle ad = entry.getValue();
            FeedAdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType, ad.getRule().getStartDate());

            if (entity != null) {
                // 低版本，不予返回 ‘文章样式的feed流广告’
                if (entity.getFeedAdType() == 2) {
                    Platform platform = Platform.from(advParam.getS());
                    if (platform.isAndriod(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_ANDROID_0208) {
                        continue;
                    }
                    if (platform.isIOS(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_IOS_0208) {
                        continue;
                    }

                }
                entities.add(entity);
                int adId = entity.getId();
                ids.add(adId);

            } else {
                logger.info("feedAd 广告未创建~ , adId={}, isTop={}, udid={}", ad.getAds().getId(), advParam.getIsTop(),
                        advParam.getUdid());
                continue;
            }

            AnalysisLog.info(
                    "[FEED_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},provider_id={}",
                    ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
                    advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getLineId(), advParam.getStnName(),
                    advParam.getNw(), advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(),
                    entity.getProvider_id());

            // 记录每个适合规则的广告未投放的记录，用于‘出现间隔’的控制
            cacheRecord.setNoFeedAdHistoryMap(ids);

            // 置顶位，只需要一个广告
            if (advParam.getIsTop() == 1) {
                return entities;
            }
        }

        return entities;
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
        if (inner.getApi_type() == 1) { // gdt 原生广告设置样式
            entity.setFeedAdType(2);
            entity.setImgsType(1); // 单独小图
        } else if (inner.getApi_type() == 2) { // gdt banner样式
            entity.setFeedAdType(3);
            entity.setImgsType(4); // 宽图窄图
        }
        return entity;
    }

}
