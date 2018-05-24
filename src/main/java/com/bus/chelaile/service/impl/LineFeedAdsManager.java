package com.bus.chelaile.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Entities;

import scala.util.Random;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
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
        
        if (entities != null && entities.size() > 0) {
            writeSendLog(advParam, entities);
            setSendLog(entities.get(0), advParam.getUdid());
        }

        return entities;
    }

    // 记录发送的第一条广告的log
    private void setSendLog(BaseAdEntity baseAdEntity, String udid) {
        String sendLineFeedLogKey = AdvCache.getSendLineFeedLogKey(udid);
        CacheUtil.setToRedis(sendLineFeedLogKey, Constants.SEDN_LINEFEED_EXTIME, String.valueOf(baseAdEntity.getId()));
    }

    private LineFeedAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContent ad, ShowType showType) {
        LineFeedAdEntity res = new LineFeedAdEntity();
        AdInnerContent inner = ad.getInnerContent();
        if (inner instanceof AdLineFeedInnerContent) {
            AdLineFeedInnerContent lineFeedInner = (AdLineFeedInnerContent) inner;
            // 第三方特殊处理
            if (lineFeedInner.getProvider_id() > 0) {
                res = createSDKOpenAds(ad, lineFeedInner);
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
    private void calAdWeightAndOrder(AdvParam advParam, List<BaseAdEntity> lineFeedAds) {
        // 获取上次投放的第一个广告，这次需要轮训到下一个
        String sendLineFeedLogKey = AdvCache.getSendLineFeedLogKey(advParam.getUdid());
        String lastSendIdStr = (String) CacheUtil.getFromRedis(sendLineFeedLogKey);
        if (lastSendIdStr != null) {
            try {
//                logger.info("找到未过期的投放记录，udid={}, lastSendId={}", advParam.getUdid(), lastSendIdStr);
                int sendId = Integer.parseInt(lastSendIdStr);
                int size = lineFeedAds.size();
                
                // 有之前投放的记录，确保当前列表第一个变化后，直接return即可
                if (lineFeedAds.get(0).getId() == sendId) {
                    Collections.swap(lineFeedAds, 0, size - 1);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // 如果获取不到，那么根据权重设置第一次展示
        // 获取所有符合规则的广告
//        logger.info("投放记录超时： udid={}, list={}", advParam.getUdid(), JSONObject.toJSONString(lineFeedAds));
        if (lineFeedAds != null && lineFeedAds.size() > 0) {
            int totalWeight = 0;
            for (BaseAdEntity entity : lineFeedAds) {
                totalWeight += ((LineFeedAdEntity) entity).getAdWeight();
            }
            
            if (totalWeight > 0) {
                int randomOut = new Random().nextInt(totalWeight); // 取随机值
                logger.info("randomOut={}, udid={}", randomOut, advParam.getUdid());
                int indexWeight = 0;
                for (int index = 0; index < lineFeedAds.size(); index++) {
                    BaseAdEntity entity = lineFeedAds.get(index);
                    if ((indexWeight += ((LineFeedAdEntity) entity).getAdWeight()) > randomOut) {
                        Collections.swap(lineFeedAds, 0, index);
//                        logger.info("投放记录超时，调整后**** ： udid={}, list={}", advParam.getUdid(), JSONObject.toJSONString(lineFeedAds));
                        return;
                    }
                }
            }
        }
    }

    // 2018-05-05 ，详情页下方feed位广告
    private LineFeedAdEntity createSDKOpenAds(AdContent ad, AdLineFeedInnerContent inner) {
        LineFeedAdEntity entity = new LineFeedAdEntity();
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
