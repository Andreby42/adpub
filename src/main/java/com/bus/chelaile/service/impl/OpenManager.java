package com.bus.chelaile.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.common.TimeLong;
import com.bus.chelaile.innob.AdvType;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdFullInnerContent;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdSchedule;
import com.bus.chelaile.model.ads.entity.ApiLineEntity;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.OpenAdEntity;
import com.bus.chelaile.model.ads.entity.OpenOldAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.service.CommonService;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.New;
 
/**
 * 新版本开屏和浮层
 * 
 * @author zzz
 * 
 */
public class OpenManager extends AbstractManager {

    @Autowired
    private SelfOpenManager selfOpen;

    @Autowired
    private ApiDetailsManager apiDetailsManager;

    @Override
    protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) throws Exception {

        OpenAdEntity entity = null;
        // 旧版本的返回自己的一条广告
        if (cateGory == null) {

            for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
                entity = getSelfAdEntity(advParam, cacheRecord, entry.getValue(), showType, queryParam);
                if (isRecord) {
                    AnalysisLog.info(
                            "[OPEN_SCREEN_ADS]: adKey=ADV[id={}#showType={}#title={}], userId={}, accountId={}, udid={}, cityId={}, s={}, v={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},provider_id={}",
                            entity.getId(), showType.getType(), entry.getValue().getAds().getTitle(), advParam.getUserId(),
                            advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(), advParam.getV(),
                            advParam.getNw(), advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(),
                            entity.getProvider_id());
                }

                return entity;
            }
        }

        // 自采买
        if (cateGory.getAdType() == 1) {
            // 2018-05-04， 加入第三方广告，同样走自采买投放的逻辑
            AdInnerContent inner = adMap.get(cateGory.getAdId()).getAds().getInnerContent();
            if ((inner != null) && (inner instanceof AdFullInnerContent)) {
                AdFullInnerContent fullInner = (AdFullInnerContent) inner;
                if (fullInner.getProvider_id() != 0) { // 非自采买广告，需要特殊处理
                    cateGory.setAdType(fullInner.getProvider_id());
                    entity = setApiOpenAds(fullInner, advParam, cacheRecord, cateGory.getAdId(), showType,
                            adMap.get(cateGory.getAdId()).getAds().getTitle());
                    return entity;
                }

            }

            entity = getSelfAdEntity(advParam, cacheRecord, adMap.get(cateGory.getAdId()), showType, queryParam);

            AnalysisLog.info(
                    "[NEW_OPEN_SCREEN_ADS]: adKey=ADV[id={}#showType={}#title={}], userId={}, accountId={}, udid={}, cityId={}, s={}, v={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},provider_id={}",
                    entity.getId(), showType.getType(), adMap.get(cateGory.getAdId()).getAds().getTitle(), advParam.getUserId(),
                    advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(), advParam.getV(),
                    advParam.getNw(), advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(),
                    entity.getProvider_id());

        }
        // 开屏的 inmobe
        else if (cateGory.getAdType() == 3 && showType == ShowType.OPEN_SCREEN) { // 开屏现在是有innobe

            ApiLineEntity apiEntity =
                    apiDetailsManager.from(Platform.from(advParam.getS()), advParam, cacheRecord, cateGory, showType.getType());
            if (apiEntity == null) {
                return null;
            }
            entity = setApiOpenAdEntity(apiEntity, showType.getValue());
            AnalysisLog.info(
                    "[NEW_OPEN_SCREEN_ADS]: adKey=ADV[id={}#showType={}#title={}],des={},link={} , userId={}, accountId={}, udid={}, cityId={}, s={}, v={},provider_id={},pic={},nw={},ip={},deviceType={},geo_lng={},geo_lat={}",
                    entity.getId(), showType.getType(), apiEntity.getApiTitle(), apiEntity.getApiDes(), apiEntity.getLink(),
                    advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(),
                    advParam.getV(), entity.getProvider_id(), entity.getPic(), advParam.getNw(), advParam.getIp(),
                    advParam.getDeviceType(), advParam.getLng(), advParam.getLat());

        }
        //        // 四种客户端 sdk, 新版本才支持
        //        else if (cateGory.getAdType() == 2 || cateGory.getAdType() == 8 || cateGory.getAdType() == 9
        //                || cateGory.getAdType() == 10) {
        //            if ((advParam.getS().equalsIgnoreCase("android") && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0420)
        //                    || (advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= Constants.PLATFORM_LOG_IOS_0420)) {
        //                entity = createSDKOpenAds(fullInner, cateGory.getAdType() * (-1));
        //                AnalysisLog.info(
        //                        "[NEW_OPEN_SCREEN_ADS]: adKey=ADV[id={}#showType={}#title={}], userId={}, accountId={}, udid={}, cityId={}, s={}, v={},provider_id={},nw={},ip={},deviceType={}",
        //                        entity.getId(), showType.getType(), "", advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
        //                        advParam.getCityId(), advParam.getS(), advParam.getV(), entity.getProvider_id(), advParam.getNw(),
        //                        advParam.getIp(), advParam.getDeviceType());
        //            } else {
        //            }
        //        } else {
        //            throw new IllegalArgumentException("开屏的类型错误showType:" + showType + ",cateGory.getAdType()=" + cateGory.getAdType());
        //        }

        // 2017.12.28， 开屏广告记录不再走发送，而是走来自埋点日志处理的‘展示’
        //		cacheRecord.setOpenAdHistory(cateGory);
        return entity;
    }

    /*
     * 统一处理第三方广告
     */
    private OpenAdEntity setApiOpenAds(AdFullInnerContent fullInner, AdvParam advParam, AdPubCacheRecord cacheRecord, int advId,
            ShowType showType, String title) throws Exception {
        if (showType != ShowType.OPEN_SCREEN) {
            return null;
        }
        OpenAdEntity entity = null;

        if (fullInner.getProvider_id() == 3) {
            if ((advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= Constants.PLATFOMR_LOG_IOS_0528)) {
                entity = createSDKOpenAds(fullInner, advId);
                AnalysisLog.info(
                        "[NEW_OPEN_SCREEN_ADS]: adKey=ADV[id={}#showType={}#title={}], userId={}, accountId={}, udid={}, cityId={}, s={}, v={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},provider_id={}",
                        entity.getId(), showType.getType(), title, advParam.getUserId(), advParam.getAccountId(),
                        advParam.getUdid(), advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getNw(),
                        advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(),
                        entity.getProvider_id());
                return entity;
            } else {
                logger.info("低版本返回了inmobi开屏广告 , udid={} s={}, v={}", advParam.getUdid(), advParam.getS(), advParam.getV());
                return entity;
            }
        } else {
            if ((fullInner.getProvider_id() == 2 || fullInner.getProvider_id() == 10) && ((advParam.getS().equalsIgnoreCase("android") && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0420)
                    || (advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= Constants.PLATFORM_LOG_IOS_0420))) {
                entity = createSDKOpenAds(fullInner, advId);
                AnalysisLog.info(
                        "[NEW_OPEN_SCREEN_ADS]: adKey=ADV[id={}#showType={}#title={}], userId={}, accountId={}, udid={}, cityId={}, s={}, v={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},provider_id={}",
                        entity.getId(), showType.getType(), title, advParam.getUserId(), advParam.getAccountId(),
                        advParam.getUdid(), advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getNw(),
                        advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(),
                        entity.getProvider_id());

            } else {
                logger.info("低版本不予返回非自采买的开屏广告 , udid={} s={}, v={}", advParam.getUdid(), advParam.getS(), advParam.getV());
                return null;
            }

            return entity;
        }
    }

    private OpenAdEntity createSDKOpenAds(AdFullInnerContent inner, int id) {
        OpenAdEntity entity = new OpenAdEntity(ShowType.OPEN_SCREEN.getValue());
        entity.setId(id);
        entity.setProvider_id(inner.getProvider_id() + "");
        entity.setDuration(4); // 广告持续时间，单位-S
        entity.setOpenType(0); // 页面打开方式，0-内部
        entity.setIsDisplay(0); // 是否展示秒数，0-展示
        entity.setIsSkip(0); // 是否展示跳过按钮，0-展示
        entity.setIsFullShow(0); // 是否全屏展示，0-否
        entity.setType(3); // 第三方广告

        entity.setAdWeight(inner.getAdWeight());
        entity.setTimeout(inner.getTimeout());

        return entity;
    }

    /*
     * 自采买 开屏广告，以及浮层广告
     */
    private OpenAdEntity getSelfAdEntity(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContentCacheEle adc,
            ShowType showType, QueryParam queryParam) {
        OpenAdEntity entity = null;
        AdFullInnerContent fullInner = (AdFullInnerContent) (adc.getAds().getAdInnerContent());
        if (fullInner.getProvider_id() != 0) {
            // TODO 2018-05-26  注释掉 
            //            logger.error("老版本返回了第三方的广告，udid={},s={},v={},vc={}", advParam.getUdid(), advParam.getS(), advParam.getV(),
            //                    advParam.getVc());
            //            return null;
            try {
                entity = setApiOpenAds(fullInner, advParam, cacheRecord, adc.getAds().getId(), showType, adc.getAds().getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 判断是否是老版本的广告接口
        if (!queryParam.isOldMany()) {
            entity = selfOpen.from(adc.getAds(), advParam.getS(), advParam, showType.getType());
            entity.setExpire(adc.getRule().getEndDate().getTime());

        } else {
            //仅仅 preloadAds接口用到这一块
            OpenOldAdEntity oldEntity = selfOpen.fromOld(adc.getAds(), advParam.getS(), advParam, showType.getType());
            oldEntity.setSt(adc.getRule().getStartDate().getTime());
            oldEntity.setEt(adc.getRule().getEndDate().getTime());
            oldEntity.setExpire(adc.getRule().getEndDate().getTime());
            entity = oldEntity;
        }

        // 每个时间段的发送次数
        adTimeCounts(cacheRecord, advParam.getUdid(), adc);
        return entity;
    }

    private OpenAdEntity setApiOpenAdEntity(ApiLineEntity apiEntity, int showType) {
        OpenAdEntity entity = new OpenAdEntity(showType);
        entity.setId(apiEntity.getId());
        entity.setLink(apiEntity.getLink());
        entity.setPic(apiEntity.getCombpic());
        if (entity.getPic() == null || entity.getPic().equals("")) {
            TimeLong.info("id={},pic=null", entity.getId());
            throw new IllegalArgumentException("图片地址为空");
        }
        entity.setApiIsSkip();
        entity.setApiDisplay();
        entity.setApiDuration();
        entity.setOpenType(apiEntity.getOpenType());
        entity.setTargetType(apiEntity.getTargetType());
        entity.setMonitorType(3);
        entity.setUnfoldMonitorLink(apiEntity.getUnfoldMonitorLink());
        entity.setClickMonitorLink(apiEntity.getClickMonitorLink());
        entity.setType(AdvType.API.getVal());
        entity.setProvider_id(apiEntity.getProvider_id());
        entity.setPlacementId("");

        return entity;
    }

    /**
     * 根据返回的开屏和浮层广告构建preloadAds的返回结构
     * 返回的结构之中，任意一个时间段需要展示的广告（开屏或者浮层）将只会有一个，schedule数组之中各个时间段不会重叠，
     * 并且按照开始时间从小到大排序。
     * 
     * @param list
     * @param resultMap
     * @param ads
     */
    public JSONObject buildResultMap(List<BaseAdEntity> list) {
        JSONObject resultMap = new JSONObject();

        List<AdSchedule> adSchedules = new ArrayList<>();
        List<OpenAdEntity> fullAdEntities = new ArrayList<>();

        // 按照开始生效时间排序
        Collections.sort(list, AD_START_TIME_COMPARATOR);
        int size = list.size();
        buildAd(list.get(0), adSchedules, fullAdEntities);
        if (size > 1) {
            long endTime = ((OpenOldAdEntity) list.get(0)).getEt();
            for (int i = 0; i < size; i++) {
                // 广告之间时间不能重叠，既前一个广告结束时间 要小于 后一个广告开始时间
                if (endTime <= ((OpenOldAdEntity) list.get(i)).getSt()) {
                    buildAd(list.get(i), adSchedules, fullAdEntities);
                    endTime = ((OpenOldAdEntity) list.get(i)).getEt();
                }
            }
        }

        resultMap.put("schedule", adSchedules);
        resultMap.put("ads", fullAdEntities);

        return resultMap;
    }

    private void buildAd(BaseAdEntity baseAdEntity, List<AdSchedule> adSchedules, List<OpenAdEntity> fullAdEntities) {
        AdSchedule adSchedule = new AdSchedule();
        //		OpenAdEntity fullAdEntity = new OpenAdEntity(baseAdEntity.getType());

        adSchedule.setAdId(baseAdEntity.getId());
        adSchedule.setSt(((OpenOldAdEntity) baseAdEntity).getSt());
        adSchedule.setEt(((OpenOldAdEntity) baseAdEntity).getEt());
        adSchedules.add(adSchedule);

        fullAdEntities.add((OpenAdEntity) baseAdEntity);
    }

    /***
     * 获取所有广告的pics和audios 事实上，只获取了 开屏、浮层、音频广告的pics和audios
     * 
     * @param advParam
     * @param showType
     */
    public Set<String> getAllAdsAdsAudiosPics(AdvParam advParam, ShowType showType) {

        Set<String> picsAndAudios = new HashSet<>();

        // 检测失败直接返回
        if (!beforeCheck(advParam, showType)) {
            return null;
        }

        // //保存有效用户的udid
        // if(advParam.getUdid() != null) {
        // AdvCache.saveRealUsers(advParam.getUdid());
        // }

        // 取得所有刻意投放广告
        List<AdContentCacheEle> adsList = CommonService.getAllAdsList(advParam.getUdid(), advParam.getAccountId(), showType);

        if (adsList == null || adsList.size() == 0) {
            return null;
        } else {
            adsList = CommonService.mergeAllAds(adsList);
        }

        for (AdContentCacheEle ad : adsList) {
            for (Rule rule : ad.getRules()) {
                if (CommonService.dateCompare(rule.getStartDate(), showType.getType())) {
                    // 增加城市匹配规则
                    if (rule.hasCities() && !rule.isCityMatch(advParam.getCityId())) {
                        continue;
                    }
                    AdInnerContent innerContent = ad.getAds().getInnerContent(); // audios
                                                                                 // 的innerContent是
                                                                                 // AdLineDetailInnerContent
                                                                                 // ad.getAds().getInnerPicContent();
                    fillPicsAndAudios(picsAndAudios, innerContent, advParam.getS(), showType, ad, advParam.getUdid());
                }
            }
        }

        return picsAndAudios;
    }

    private void fillPicsAndAudios(Set<String> picsAndAudios, AdInnerContent innerContent, String s, ShowType showType,
            AdContentCacheEle ad, String udid) {

        if (showType == ShowType.OPEN_SCREEN || showType == ShowType.FULL_SCREEN) {
            String url = innerContent.extractFullPicUrl(s);
            if (StringUtils.isNotEmpty(url)) {
                picsAndAudios.add(url);
            }
        } else {
            String url1 = innerContent.extractAudiosUrl(s, 0);
            String url2 = innerContent.extractAudiosUrl(s, 1);
            if (StringUtils.isNotEmpty(url1)) {
                picsAndAudios.add(url1);
                picsAndAudios.add(url2);
            }
        }
    }

    /**
     * 按照广告生效时间排序
     */
    private Comparator<BaseAdEntity> AD_START_TIME_COMPARATOR = new Comparator<BaseAdEntity>() {
        @Override
        public int compare(BaseAdEntity o1, BaseAdEntity o2) {
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;

            return (int) (((OpenOldAdEntity) o1).getSt() - ((OpenOldAdEntity) o2).getSt());
        }
    };

    @Override
    protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {

        List<BaseAdEntity> entities = New.arrayList();
        List<Integer> ids = New.arrayList();
        boolean hasOwnAd = false;
        for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
            AdContentCacheEle ad = entry.getValue();

            // 有非兜底的自采买广告。 直接返回第一个优先级最高的即可
            AdFullInnerContent inner = (AdFullInnerContent) ad.getAds().getAdInnerContent();
            logger.info("***** {}", JSONObject.toJSONString(inner));
            if (inner.getProvider_id() <= 1 && inner.getBackup() == 0) { // 非自采买的provider_id都大于1
                OpenAdEntity entity = getSelfAdEntity(advParam, cacheRecord, ad, showType, queryParam);
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
                AdFullInnerContent inner = (AdFullInnerContent) ad.getAds().getAdInnerContent();
                OpenAdEntity entity = null;
                // 第三方特殊处理
                if (inner.getProvider_id() > 1) {
                    entity = createSDKOpenAds(inner, ad.getAds().getId());
                } else {
                    if(inner.getBackup() == 1) {    // 兜底的广告单独摘出来
                        backupad = ad;
                        continue;
                    }
                    entity = getSelfAdEntity(advParam, cacheRecord, ad, showType, queryParam);
                }

                if (entity != null) {
                    entities.add(entity);
                }
            }
            // 重新排序
            // 如果半小时内有上次的投放记录，那么根据上次返回到的位置，轮训下一个【方法是将这个广告排到最后一位】
            // 如果超过半小时，那么按照权重排序
            if (!checkSendLog(advParam, entities, showType.getType())) {
                rankAds(advParam, entities);
            }
            if(backupad != null) {
                OpenAdEntity entity = getSelfAdEntity(advParam, cacheRecord, backupad, showType, queryParam);
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
}
