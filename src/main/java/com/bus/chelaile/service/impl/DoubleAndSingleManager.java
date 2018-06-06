package com.bus.chelaile.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdDoubleInnerContent;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.New;

/**
 * 单双栏广告
 * 
 * @author zzz
 * 
 */
public class DoubleAndSingleManager extends AbstractManager {

    @Override
    public BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) throws Exception {
        AdEntity entity = new AdEntity(showType.getValue());
        AdContentCacheEle ad = null;

        if (cateGory == null) { //双栏策略控制 ---> not valid
            for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
                ad = entry.getValue();
                break;
            }
        } else if (cateGory.getAdType() == 1) {
            ad = adMap.get(cateGory.getAdId());
            cacheRecord.setOpenAdHistory(cateGory); //将双栏投放记录，放到openAdHistory中，理论上会影响开屏广告的长尾投放和分组轮播。  // TODO 
        } else {
            logger.info("双栏错误的返回非自采买的广告，udid={}, cateGory={}", advParam.getUdid(), cateGory);
            return null;
        }

        //		entity.setSindex(queryParam.getStation().getIndex()); // 为单栏广告设置SINDEX,双栏广告将复写该值
        entity.setSindex(0); // 为单栏广告设置SINDEX，目前单栏默认是0，双栏广告将复写该值

        AdInnerContent inner = ad.getAds().getInnerContent();

        if (inner != null) {
            // 此处给广告赋予位置，双栏给sindex，单栏给lindex
            if (showType == ShowType.ROUTE_PLAN_ADV) {
                inner.fillAdEntity(entity, advParam, 0);
            } else {
                AdDoubleInnerContent adInner = (AdDoubleInnerContent) inner;
                if (adInner.getProvider_id() != 0) { // 如果是第三方广告，加一些版本控制
                    return null;
//                    if (adInner.getProvider_id() != ProductType.GUANGDIANTONG.getProvider_id()) {
//                        // 双栏目前只支持广点通
//                        throw new IllegalArgumentException(
//                                "错误的双栏provider_id, advId=" + ad.getAds().getId() + ", provider_id=" + adInner.getProvider_id());
//                    }
//                    if ((advParam.getS().equalsIgnoreCase("android") && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0420)
//                            || (advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= Constants.PLATFOMR_LOG_IOS_0502)) {
//                        entity.setProvider_id(adInner.getProvider_id() + ""); // 新增第三方广告
//                    } else {
//                        logger.info("低版本不予返回非自采买的双栏广告 ");
//                        return null;
//                    }
                }

                inner.fillAdEntity(entity, advParam, queryParam.getStation().getIndex());
            }
        }

        Map<String, String> paramMap = New.hashMap();
        if (ShowType.SINGLE_COLUMN.getType().equals(ad.getAds().getShowType())) {
            // iOS客户端会将从server拿到的数据进行处理，对其中的所有的null串替换成""
            // 因此，此处不能写入值为null的stname
            if (queryParam.getStation() != null && queryParam.getStation().getStnName() != null) {
                paramMap.put(Constants.PARAM_STATION_NAME, AdvUtil.encodeUrl(queryParam.getStation().getStnName()));
            }
        }
        paramMap.put(Constants.PARAM_STATION_ORDER, String.valueOf(entity.getSindex()));
        paramMap.put(Constants.PARAM_DISTANCE, String.valueOf(advParam.getDistance()));

        entity.fillBaseInfo(ad.getAds(), advParam, paramMap);
        entity.dealLink(advParam);

        // 每个时间段的发送次数
        adTimeCounts(cacheRecord, advParam.getUdid(), ad);

        if (showType == ShowType.SINGLE_COLUMN) {
            AnalysisLog.info(
                    "[LINE_LEVEL_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, storder={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},distance={},provider_id={}",
                    ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
                    advParam.getCityId(), advParam.getS(), advParam.getV(), entity.getSindex(), advParam.getNw(),
                    advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getDistance(),
                    entity.getProvider_id());
        } else if (showType == ShowType.DOUBLE_COLUMN) {
            AnalysisLog.info(
                    "[STATION_LEVEL_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, storder={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},distance={},provider_id={}",
                    ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
                    advParam.getCityId(), advParam.getS(), advParam.getV(), entity.getSindex(), advParam.getNw(),
                    advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getDistance(),
                    entity.getProvider_id());
        } else {
            AnalysisLog.info(
                    "[ROUTE_LEVEL_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, storder={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},distance={},provider_id={}",
                    ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
                    advParam.getCityId(), advParam.getS(), advParam.getV(), entity.getSindex(), advParam.getNw(),
                    advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getDistance(),
                    entity.getProvider_id());
        }

        return entity;

    }

    @Override
    protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {

        List<BaseAdEntity> entities = New.arrayList();
        if (showType == ShowType.DOUBLE_COLUMN) {
            List<Integer> ids = New.arrayList();
            boolean hasOwnAd = false;
            for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
                AdContentCacheEle ad = entry.getValue();

                // 有非兜底的自采买广告。 直接返回第一个优先级最高的即可
                AdDoubleInnerContent inner = (AdDoubleInnerContent) ad.getAds().getAdInnerContent();
                if (inner.getProvider_id() <= 1 && inner.getBackup() == 0) { // 非自采买的provider_id都大于1
                    AdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType);
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
                    AdDoubleInnerContent inner = (AdDoubleInnerContent) ad.getAds().getInnerContent();
                    if(inner.getBackup() == 1) { //兜底
                        backupad = ad;
                        continue;
                    }
                    AdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType);
                    if (entity != null) {
                        entities.add(entity);
                    }
                }
                // 重新排序
                // 如果半小时内有上次的投放记录，那么根据上次返回到的位置，轮训下一个
                // 如果超过半小时，那么按照权重排序
                if (!checkSendLog(advParam, entities, showType.getType()))
                    rankAds(advParam, entities);
                // 如果有点击记录，那么将该条广告放在最后一位  TODO 
                setClickAtLast(cacheRecord, entities);
                
                if(backupad != null) {
                    AdEntity entity = from(advParam, cacheRecord, backupad.getAds(), showType);
                    entities.add(entity);
                }
            }
            // 记录投放的第一条广告， 记录发送日志
            if (entities != null && entities.size() > 0) {
                cacheRecord.setNoAdHistoryMap(ids, showType.getType());
                recordSend(advParam, cacheRecord, adMap, showType, entities);
            }
        }

        if (showType == ShowType.ROUTE_PLAN_ADV) {
            for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
                AdContent ad = entry.getValue().getAds();
                AdEntity entity = from(advParam, cacheRecord, ad, showType);
                entities.add(entity);
                AnalysisLog.info(
                        "[ADV_SEND]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, storder={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},distance={}",
                        ad.getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(),
                        advParam.getS(), advParam.getV(), entity.getSindex(), advParam.getNw(), advParam.getIp(),
                        advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getDistance());

            }
        }

        return entities;
    }


    private AdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContent ad, ShowType showType) {
        AdEntity entity = new AdEntity(showType.getValue());
        AdInnerContent inner = ad.getInnerContent();
        
         AdDoubleInnerContent doubleInner = (AdDoubleInnerContent)inner;
         if(doubleInner.getProvider_id() > 1) {
             entity = createSDKAds(ad, doubleInner);
             return entity;
         }

        inner.fillAdEntity(entity, advParam, 0);

        Map<String, String> paramMap = New.hashMap();
        paramMap.put(Constants.PARAM_STATION_ORDER, String.valueOf(entity.getSindex()));
        paramMap.put(Constants.PARAM_DISTANCE, String.valueOf(advParam.getDistance()));

        entity.fillBaseInfo(ad, advParam, paramMap);
        entity.dealLink(advParam);

        return entity;
    }
    
    // 2018-05-05 ，详情页下方feed位广告
    private AdEntity createSDKAds(AdContent ad, AdDoubleInnerContent inner) {
        AdEntity entity = new AdEntity(ShowType.DOUBLE_COLUMN.getValue());
        entity.setId(ad.getId());
        entity.setProvider_id(inner.getProvider_id() + "");
        entity.setOpenType(0); // 页面打开方式，0-内部
        entity.setType(3); // 第三方广告
//        entity.setTitle(ad.getTitle());
        
        entity.setAdWeight(inner.getAdWeight());
        entity.setAutoInterval(inner.getAutoInterval());
        entity.setMixInterval(inner.getMixInterval());
        entity.setSindex(inner.getPosition());
        entity.setClickDown(inner.getClickDown());
//        entity.setApiType(1);
        
     // 任务列表
        // 2018-06-06
        if(inner.getTasksGroup() != null) {
            entity.setTasksGroup(inner.getTasksGroup());
        }
        
        return entity;
    }
}
