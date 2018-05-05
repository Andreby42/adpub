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
                            "[OPEN_SCREEN_ADS]: adKey=ADV[id={}#showType={}#title={}], userId={}, accountId={}, udid={}, cityId={}, s={}, v={},nw={},ip={},deviceType={},geo_lng={},geo_lat={}",
                            entity.getId(), showType.getType(), entry.getValue().getAds().getTitle(), advParam.getUserId(),
                            advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(), advParam.getV(),
                            advParam.getNw(), advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat());
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
                    entity = setApiOpenAds(fullInner, advParam, cacheRecord, cateGory, showType);
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
        // 四种客户端 sdk, 新版本才支持
        else if (cateGory.getAdType() == 2 || cateGory.getAdType() == 8 || cateGory.getAdType() == 9
                || cateGory.getAdType() == 10) {
            if ((advParam.getS().equalsIgnoreCase("android") && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0420)
                    || (advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= Constants.PLATFORM_LOG_IOS_0420)) {
                entity = createSDKOpenAds(cateGory.getAdType());
                AnalysisLog.info(
                        "[NEW_OPEN_SCREEN_ADS]: adKey=ADV[id={}#showType={}#title={}], userId={}, accountId={}, udid={}, cityId={}, s={}, v={},provider_id={},nw={},ip={},deviceType={}",
                        entity.getId(), showType.getType(), "", advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
                        advParam.getCityId(), advParam.getS(), advParam.getV(), entity.getProvider_id(), advParam.getNw(),
                        advParam.getIp(), advParam.getDeviceType());
            } else {
                // TODO 
            }
        } else {
            throw new IllegalArgumentException("开屏的类型错误showType:" + showType + ",cateGory.getAdType()=" + cateGory.getAdType());
        }

        // 2017.12.28， 开屏广告记录不再走发送，而是走来自埋点日志处理的‘展示’
        //		cacheRecord.setOpenAdHistory(cateGory);
        return entity;
    }

    /*
     * 统一处理第三方广告
     */
    private OpenAdEntity setApiOpenAds(AdFullInnerContent fullInner, AdvParam advParam, AdPubCacheRecord cacheRecord,
            AdCategory cateGory, ShowType showType) throws Exception {
        if (showType != ShowType.OPEN_SCREEN) {
            return null;
        }
        OpenAdEntity entity = null;

        if (fullInner.getProvider_id() == 3) {
            // 低版本亦不支持nimobi的第三方广告
            cateGory.setApiType(4);  // 手动设置cateGory，因为接下来要走以前的策略控制的模块
            cateGory.setAdType(3);
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
        } else {
            if ((advParam.getS().equalsIgnoreCase("android") && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0420)
                    || (advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= Constants.PLATFORM_LOG_IOS_0420)) {
                entity = createSDKOpenAds(fullInner.getProvider_id());
            } else {
                logger.info("低版本不予返回非自采买的双栏广告 ");
                return null;
            }
        }

        return entity;
    }

    private OpenAdEntity createSDKOpenAds(int adType) {
        OpenAdEntity entity = new OpenAdEntity(ShowType.OPEN_SCREEN.getValue());
        entity.setId(adType * -1);
        entity.setProvider_id(adType + "");
        entity.setDuration(4); // 广告持续时间，单位-S
        entity.setOpenType(0); // 页面打开方式，0-内部
        entity.setIsDisplay(0); // 是否展示秒数，0-展示
        entity.setIsSkip(0); // 是否展示跳过按钮，0-展示
        entity.setIsFullShow(0); // 是否全屏展示，0-否
        entity.setType(3); // 第三方广告
        return entity;
    }

    /*
     * 自采买 开屏广告，以及浮层广告
     */
    private OpenAdEntity getSelfAdEntity(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContentCacheEle adc,
            ShowType showType, QueryParam queryParam) {
        OpenAdEntity entity = null;
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

    /**
     * 填充图片集合
     * 
     * @param pics
     *            图片集合
     * @param innerContent
     *            广告内容
     */
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
        // TODO Auto-generated method stub
        return null;
    }

}
