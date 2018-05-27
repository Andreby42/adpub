package com.bus.chelaile.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdLineFeedInnerContent;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.LineFeedAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.New;

public class LineFeedAdsManager extends AbstractManager {

    @Override
    protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) throws Exception {
        return null;
    }

    @Override
    protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
        List<BaseAdEntity> entities = New.arrayList();
        List<Integer> ids = New.arrayList();
        boolean hasOwnAd = false;
        for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
            AdContentCacheEle ad = entry.getValue();
            
            // 有非兜底的自采买广告。 直接返回第一个优先级最高的即可
            AdLineFeedInnerContent lineFeedInner = (AdLineFeedInnerContent) ad.getAds().getAdInnerContent();
            if (lineFeedInner.getProvider_id() <= 1 && lineFeedInner.getBackup() == 0) { // 非自采买的provider_id都大于1
                LineFeedAdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType);
                if (entity != null) {
                    entities.add(entity);
                    int adId = ad.getAds().getId();
                    ids.add(adId);
                    
                    hasOwnAd = true;
                    break;
                }
            }
        }
        // 如果没有自采买，那么返回一个列表
        if (!hasOwnAd) {
            AdContentCacheEle backupad = null;
            for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
                AdContentCacheEle ad = entry.getValue();
                if(((AdLineFeedInnerContent) ad.getAds().getInnerContent()).getBackup() == 1) { // 兜底
                    backupad = ad;
                    continue;
                }
                LineFeedAdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType);
                if (entity != null) {
                    entities.add(entity);
                }
            }
            // 重新排序
            // 如果半小时内有上次的投放记录，那么根据上次返回到的位置，轮训下一个
            // 如果超过半小时，那么按照权重排序
            if (!checkSendLog(advParam, entities, showType.getType()))
                rankAds(advParam, entities);
            if(backupad != null) {
                LineFeedAdEntity entity = from(advParam, cacheRecord, backupad.getAds(), showType);
                entities.add(entity);
            }
        }
        // 记录投放的第一条广告， 记录发送日志
        if (entities != null && entities.size() > 0) {
            cacheRecord.setNoFeedAdHistoryMap(ids);
            recordSend(advParam, cacheRecord, adMap, showType, entities);
        }

        return entities;
    }


    //    protected boolean checkSendLog(AdvParam advParam, List<BaseAdEntity> lineFeedAds) {
    //        String sendLineFeedLogKey = AdvCache.getSendLineFeedLogKey(advParam.getUdid());
    //        String lastSendIdStr = (String) CacheUtil.getFromRedis(sendLineFeedLogKey);
    //        if (lastSendIdStr != null) {
    //            try {
    //                logger.info("找到未过期的投放记录，udid={}, lastSendId={}", advParam.getUdid(), lastSendIdStr);
    //                int sendId = Integer.parseInt(lastSendIdStr);
    //                int size = lineFeedAds.size();
    //
    //                // 有之前投放的记录，确保当前列表第一个变化后，直接return即可
    //                if (lineFeedAds.get(0).getId() == sendId) {
    //                    Collections.swap(lineFeedAds, 0, size - 1);
    //                }
    //                return true;
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        }
    //        return false;
    //    }

    private LineFeedAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContent ad, ShowType showType) {
        LineFeedAdEntity res = new LineFeedAdEntity(ShowType.LINE_FEED_ADV.getValue());
        AdInnerContent inner = ad.getInnerContent();
        if (inner instanceof AdLineFeedInnerContent) {
            AdLineFeedInnerContent lineFeedInner = (AdLineFeedInnerContent) inner;
            // 第三方特殊处理
            if (lineFeedInner.getProvider_id() > 1) {
                res = createSDKOpenAds(ad, lineFeedInner);
            } else {
                res.fillBaseInfo(ad, advParam, new HashMap<String, String>());
                res.dealLink(advParam);
                res.setImgsType(lineFeedInner.getImgsType());
                res.setSubhead(lineFeedInner.getSlogan());
                res.setHead(lineFeedInner.getFeedAdTitle());
                res.setPic(lineFeedInner.getPic());
            }
        }
        return res;
    }

    // 2018-05-05 ，详情页下方feed位广告
    private LineFeedAdEntity createSDKOpenAds(AdContent ad, AdLineFeedInnerContent inner) {
        LineFeedAdEntity entity = new LineFeedAdEntity(ShowType.LINE_FEED_ADV.getValue());
        entity.setId(ad.getId());
        entity.setProvider_id(inner.getProvider_id() + "");
        entity.setOpenType(0); // 页面打开方式，0-内部
        entity.setType(3); // 第三方广告
        entity.setTitle(ad.getTitle());
        
        entity.setAdWeight(inner.getAdWeight());
        entity.setAutoInterval(inner.getAutoInterval());
        entity.setMixInterval(inner.getMixInterval());
        entity.setApiType(1);
        return entity;
    }

    public static void main(String[] args) {
        List<Integer> ints = New.arrayList();
        ints.add(1);
        ints.add(2);
        Collections.swap(ints, 0, 1);
        System.out.println(ints);
    }
}
