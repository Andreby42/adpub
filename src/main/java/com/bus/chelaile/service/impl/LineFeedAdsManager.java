package com.bus.chelaile.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import scala.util.Random;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdLineFeedInnerContent;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.LineFeedAdEntity;
import com.bus.chelaile.model.ads.entity.StationAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.New;

public class LineFeedAdsManager extends AbstractManager {

    static Set<String> TBK_TITLE_KEY = New.hashSet();

    @Override
    protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) throws Exception {
        return null;
    }
    
    @Override
    protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
        List<BaseAdEntity> entities = New.arrayList();
        for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
            AdContentCacheEle ad = entry.getValue();
            LineFeedAdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType);
            if (entity != null) {
                entities.add(entity);
            }
        }
        
        // 重新排序
        // 如果超过半小时，那么按照权重排序
        // 如果半小时内，那么根据上次返回到的位置，轮训下一个
        calAdWeightAndOrder(advParam, entities);
        
        if (entities != null && entities.size() > 0)
            writeSendLog(advParam, entities);

        return entities;
    }

    private LineFeedAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContent ad, ShowType showType) {
        LineFeedAdEntity res = new LineFeedAdEntity();
        AdInnerContent inner = ad.getInnerContent();
        if (inner instanceof AdLineFeedInnerContent) {
            AdLineFeedInnerContent lineFeedInner = (AdLineFeedInnerContent) inner;
            // 第三方特殊处理
            if (lineFeedInner.getProvider_id() > 0) {
                res = createSDKOpenAds(lineFeedInner.getProvider_id(), ad, lineFeedInner);
            }
        }
        return res;
    }


    public void writeSendLog(AdvParam advParam, List<BaseAdEntity> entities) {
        for (BaseAdEntity entity : entities) {
            AnalysisLog.info(
                    "[ADV_SEND]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},h5User={},h5Src={},provider_id={}",
                    ((LineFeedAdEntity)entity).buildIdentity(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
                    advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getLineId(), advParam.getStnName(),
                    advParam.getNw(), advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(),
                    advParam.getH5User(), advParam.getH5Src(), entity.getProvider_id());
        }
    }

    // 按照权重计算，选择一个广告投放
    private BaseAdEntity calAdWeightAndOrder(AdvParam advParam, List<BaseAdEntity> stanAdsList) {
        // 获取所有符合规则的站点广告
        if (stanAdsList != null && stanAdsList.size() > 0) {
            int totalWeight = 0;
            for (BaseAdEntity entity : stanAdsList) {
                //                logger.info("多个站点广告，选择一个： id={}, title={}, priority={}", entity.getId(), ((StationAdEntity) entity).getTitle(),
                //                        entity.getPriority());
                if (((StationAdEntity) entity).getBuyOut() == 1) {
                    // 买断的广告按照优先级来， stanAdsList 之前已经按照优先级排序过
                    logger.info("买断的广告, udid={}, advId={}", advParam.getUdid(), entity.getId());
                    return entity;
                }
                totalWeight += ((StationAdEntity) entity).getAdWeight();
            }
            if (totalWeight > 0) {
                int randomOut = new Random().nextInt(totalWeight); // 取随机值
                int indexWeight = 0;
                for (BaseAdEntity entity : stanAdsList) {
                    if ((indexWeight += ((StationAdEntity) entity).getAdWeight()) > randomOut) {
                        return entity;
                    }
                }
            } else {
                return stanAdsList.get(0); // 所有站点广告都没有权重，那么直接返回第一个（优先级最高那个）
            }
        } else {
            return null;
        }
        logger.error("权重计算出现错误，没有广告站点返回了 , udid={}, stanAdsList.size={}", advParam.getUdid(), stanAdsList.size());
        return null;
    }


    // 2018-05-05 ，详情页下方feed位广告
    private LineFeedAdEntity createSDKOpenAds(int adType, AdContent ad, AdLineFeedInnerContent inner) {
        LineFeedAdEntity entity = new LineFeedAdEntity();
        entity.setId(ad.getId());
        entity.setProvider_id(adType + "");
        entity.setOpenType(0); // 页面打开方式，0-内部
        entity.setType(3); // 第三方广告
        entity.setTitle(ad.getTitle());
        entity.setAdWeight(inner.getAdWeight());
        return entity;
    }

    public static void main(String[] args) {
    }
}
