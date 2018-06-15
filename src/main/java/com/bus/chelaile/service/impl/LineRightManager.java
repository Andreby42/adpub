package com.bus.chelaile.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdLineRightInnerContent;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.LineRightAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.New;

public class LineRightManager extends AbstractManager {

    @Override
    protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) throws Exception {
        return null;
    }

    private LineRightAdEntity from(AdContent ad, AdvParam advParam) {
        AdInnerContent innerI = ad.getInnerContent();
        AdLineRightInnerContent inner = (AdLineRightInnerContent) innerI;

        LineRightAdEntity res = new LineRightAdEntity();
        // 第三方特殊处理
        if (inner.getProvider_id() > 1) {
            res = createSDKOpenAds(ad, inner);
        } else {
            res.fillBaseInfo(ad, advParam, new HashMap<String, String>());
            res.setAdMode(inner.getAdMode());
            res.dealLink(advParam);
            res.setPic(inner.getPic());
            res.setAutoInterval(inner.getAutoInterval());
            res.setMixInterval(inner.getMixInterval());
            if (inner.getTasksGroup() != null) {
                res.setTasksGroup(inner.getTasksGroup());
            }
        }

        return res;
    }

    @Override
    protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
        List<BaseAdEntity> entities = New.arrayList();
        List<Integer> ids = New.arrayList();
        boolean hasOwnAd = false;

        // 遍历所有符合条件的广告体
        for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
            AdContentCacheEle ad = entry.getValue();
            AdLineRightInnerContent inner = (AdLineRightInnerContent) ad.getAds().getAdInnerContent();
            if (inner.getProvider_id() <= 1 && inner.getBackup() == 0) { // 非自采买的provider_id都大于1
                LineRightAdEntity entity = from(ad.getAds(), advParam);
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
                if (((AdLineRightInnerContent) ad.getAds().getInnerContent()).getBackup() == 1) { // 兜底
                    backupad = ad;
                    continue;
                }
                LineRightAdEntity entity = from(ad.getAds(), advParam);
                if (entity != null) {
                    entities.add(entity);
                }
            }
            // 重新排序
            // 如果半小时内有上次的投放记录，那么根据上次返回到的位置，轮训下一个
            // 如果超过半小时，那么按照权重排序
            //                if (!checkSendLog(advParam, entities, showType.getType()))
            rankAds(advParam, entities);
            setClickAtLast(cacheRecord, entities);
            if (backupad != null) {
                LineRightAdEntity entity = from(backupad.getAds(), advParam);
                entities.add(entity);
            }
        }

        if (entities != null && entities.size() > 0) {
            if (! (queryParam.isJS() && entities.get(0).getProvider_id().equals("1"))) {
            cacheRecord.setNoAdHistoryMap(ids, showType.getType());
            recordSend(advParam, cacheRecord, adMap, showType, entities);
            }
        }

        return entities;
    }

    
    // 2018-05-05 ，详情页下方feed位广告
    private LineRightAdEntity createSDKOpenAds(AdContent ad, AdLineRightInnerContent inner) {
        LineRightAdEntity entity = new LineRightAdEntity();
        entity.setId(ad.getId());
        entity.setProvider_id(inner.getProvider_id() + "");
        entity.setOpenType(0); // 页面打开方式，0-内部
        entity.setType(3); // 第三方广告
        entity.setAutoInterval(inner.getAutoInterval());
        entity.setMixInterval(inner.getMixInterval());
        entity.setApiType(1);

        // 任务列表
        // 2018-06-06
        if (inner.getTasksGroup() != null) {
            entity.setTasksGroup(inner.getTasksGroup());
        }

        return entity;
    }
    
}
