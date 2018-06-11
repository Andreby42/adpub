package com.bus.chelaile.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
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
import com.bus.chelaile.service.model.Ads;
import com.bus.chelaile.service.model.FeedAdGoto;
import com.bus.chelaile.service.model.Thumbnails;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.HttpUtils;
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
            AdLineFeedInnerContent inner = (AdLineFeedInnerContent) ad.getAds().getAdInnerContent();
            if (inner.getProvider_id() <= 1 && inner.getBackup() == 0) { // 非自采买的provider_id都大于1
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
                if (((AdLineFeedInnerContent) ad.getAds().getInnerContent()).getBackup() == 1) { // 兜底
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
//            if (!checkSendLog(advParam, entities, showType.getType()))
                rankAds(advParam, entities);
            setClickAtLast(cacheRecord, entities);
            if (backupad != null) {
                LineFeedAdEntity entity = from(advParam, cacheRecord, backupad.getAds(), showType);
                entities.add(entity);
            }
        }
        // 记录投放的第一条广告， 记录发送日志
        if (entities != null && entities.size() > 0) {
            cacheRecord.setNoAdHistoryMap(ids, showType.getType());
            recordSend(advParam, cacheRecord, adMap, showType, entities);
        }

        return entities;
    }


    private LineFeedAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContent ad, ShowType showType) {
        LineFeedAdEntity res = new LineFeedAdEntity(ShowType.LINE_FEED_ADV.getValue());
        AdInnerContent inner = ad.getInnerContent();
        if (inner instanceof AdLineFeedInnerContent) {
            AdLineFeedInnerContent lineFeedInner = (AdLineFeedInnerContent) inner;

            // 跳转feed流的targetType处理。 从永春接口获取内容填充
            if (ad.getTargetType() == 12) {
                if ((advParam.getS().equalsIgnoreCase("android") && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0605)
                        || (advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= Constants.PLATFOMR_LOG_IOS_0605)) {
                    res = createFeedEntity(advParam, ad, lineFeedInner);
                } else {
                    logger.error("低版本投放了跳转信息流的广告， adId={}, s={}, v={}, vc={}", ad.getId(), advParam.getS(), advParam.getV(),
                            advParam.getVc());
                    return null;
                }
                return res;
            }

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
                
                if(lineFeedInner.getTasksGroup() != null) {
                    res.setTasksGroup(lineFeedInner.getTasksGroup());
                }
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
        entity.setClickDown(inner.getClickDown());

        // 任务列表
        // 2018-06-06
        if (inner.getTasksGroup() != null) {
            entity.setTasksGroup(inner.getTasksGroup());
        }

        return entity;
    }

    // 跳转feed流的广告体
    private LineFeedAdEntity createFeedEntity(AdvParam p, AdContent ad, AdLineFeedInnerContent inner) {
        String response = null;
        String url = String.format(AD_GOTO_INFO_URL, p.getUdid(), p.getStatsAct(), p.getS(), p.getVc(),
                ShowType.LINE_FEED_ADV.getType());
        logger.info("请求信息流**********： url={}", url);
        LineFeedAdEntity entity = null;
        try {
            response = HttpUtils.get(url, "UTF-8");
            response = response.substring(6, response.length() - 6);
            FeedAdGoto feedAdGoto = JSON.parseObject(response, FeedAdGoto.class);
            if (feedAdGoto.getJsonr().getStatus().equals("00")) {
                List<Ads> ads = feedAdGoto.getJsonr().getData().getAds();
                if (ads != null && ads.size() > 0) {
                    String title = ads.get(0).getTitle();
                    String source = ads.get(0).getSource();
                    String action = ads.get(0).getAction();
                    //                    String timeShow = ads.get(0).getTimeShow();
                    int imgsType = ads.get(0).getThumbnailType();
                    List<Thumbnails> thumbnails = ads.get(0).getThumbnails();
                    if (thumbnails == null || thumbnails.size() == 0) {
                        logger.error("返回内容没有图片 , url={}, response={}", url, response);
                        return null;
                    }

                    entity = new LineFeedAdEntity(ShowType.LINE_FEED_ADV.getValue());
                    entity.setId(ad.getId());
                    entity.setTitle(ad.getTitle());
                    entity.setAdWeight(inner.getAdWeight());
                    entity.setClickDown(inner.getClickDown());
                    entity.setAutoInterval(inner.getAutoInterval());
                    entity.setMixInterval(inner.getMixInterval());
                    entity.setPic(thumbnails.get(0).getUrl());
                    entity.setAction(action);
                    
                    if(inner.getTasksGroup() != null) {
                        entity.setTasksGroup(inner.getTasksGroup());
                    }

                    if (imgsType == 1) {
                        entity.setImgsType(0);
                    } else if (imgsType == 2) {
                        entity.setImgsType(2);
                        entity.setSubhead(title);
                        entity.setHead(source);
                    } else {
                        logger.error("详情页底部，不支持的图片类型 , url={}, response={}", url, response);
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取跳转feed流广告内容失败， url={}, response={}", url, response);;
        }
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
