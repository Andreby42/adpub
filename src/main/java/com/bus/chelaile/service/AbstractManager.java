package com.bus.chelaile.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.common.TimeLong;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdLineDetailInnerContent;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.model.rule.UserClickRate;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.strategy.AdDispatcher;
import com.bus.chelaile.third.IfengAx.IfenAxService;
import com.bus.chelaile.thread.StaticTimeLog;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public abstract class AbstractManager {
//    @Autowired
//    private AdvInvalidService invaildService;

    protected static final Logger logger = LoggerFactory.getLogger(AbstractManager.class);
    protected static String AD_GOTO_INFO_URL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "ad.gotoinfo.url",
            "http://api.chelaile.net.cn/infoflow/api/v1/ad/goto?udid=%s&stats_act=%s&s=%s&vc=%d&showType=%s");

    /**
     * 
     * @param advParam
     * @param showType
     * @param isNeedApid
     *            false 不调用第三方,true 调用第三方,旧版本的开屏和浮层接口该值为false,单双栏也为false
     * @throws Exception
     */
    public BaseAdEntity doService(AdvParam advParam, ShowType showType, boolean isNeedApid, QueryParam queryParam,
            boolean isRecord) {
        // 检测失败直接返回
        if (!beforeCheck(advParam, showType)) {
            return null;
        }
        // 所有投放的广告
        List<AdContentCacheEle> adsList = gainAllValidAds(advParam, showType, isNeedApid);
        if ((adsList == null || adsList.size() == 0) && !isNeedApid)
            return null;

        // 用户投放广告的记录
        AdPubCacheRecord cacheRecord = gainCacheRecord(advParam, showType);
        // 单双栏、浮层、乘车页、活动页、和旧版本的开屏接口不走策略
        // 站点广告不走策略
        // 小程序 banner广告不走策略
//        Map<Integer, AdContentCacheEle> adMap = null;
        LinkedHashMap<Integer, AdContentCacheEle> adMap = new LinkedHashMap<>();
        if (!isNeedApid) {
            // 按照优先级
            Collections.shuffle(adsList); // 打乱顺序，然后再进行优先级排序 2018-04-28
            Collections.sort(adsList, AD_CONTENT_COMPARATOR);

//            adMap = New.hashMap();
            // 把所有符合规则的广告放到map中
            boolean isneedAllAds = isNeedApid;
            if (showType == ShowType.STATION_ADV) // 站点广告返回所有的
                isneedAllAds = true;
            handleAds(adMap, adsList, showType, advParam, cacheRecord, isneedAllAds, queryParam);
            if (adMap.size() == 0) {
                return null;
            }
            // 没有第三方广告,处理自采买广告
            return getEntity(null, advParam, cacheRecord, adMap, showType, queryParam, isRecord);
        } else {
            if (adsList != null && adsList.size() > 0) {
                // 把所有符合规则的广告放到map中
                handleAds(adMap, adsList, showType, advParam, cacheRecord, isNeedApid, queryParam);
            }
        }

        // 到这一步的都是 isNeedApid=true，目前只有新版的详情页和新版的开屏
        return needCategoryHandle(advParam, showType, queryParam, isRecord, cacheRecord, adMap);
    }

    /*
     * 获取ad List的方法 不涉及到策略
     */
    public List<BaseAdEntity> doServiceList(AdvParam advParam, ShowType showType, QueryParam queryParam) {
        StaticTimeLog.start(Constants.RECORD_LOG);
        // long tBeginService = System.currentTimeMillis();
        if (!beforeCheck(advParam, showType)) {
            return null;
        }
        StaticTimeLog.record(Constants.RECORD_LOG, "beforeCheck");

        // 所有投放的广告
        List<AdContentCacheEle> adsList = gainAllValidAds(advParam, showType, false);
        StaticTimeLog.record(Constants.RECORD_LOG, "gainAllValidAds");

        if (adsList == null || adsList.size() == 0)
            return null;

        AdPubCacheRecord cacheRecord = gainCacheRecord(advParam, showType);
        StaticTimeLog.record(Constants.RECORD_LOG, "gainCacheRecord");

        // 需要排序 先打乱次序 ，再按照优先级排序
        Collections.shuffle(adsList);
        Collections.sort(adsList, AD_CONTENT_COMPARATOR);
        LinkedHashMap<Integer, AdContentCacheEle> adMap = new LinkedHashMap<>();
        // 把所有符合规则的广告放到map中
        StaticTimeLog.start(Constants.RECORD_HANDLEADS_LOG);
        String ids = handleAds(adMap, adsList, showType, advParam, cacheRecord, true, queryParam).toString();
        logger.info("after adCheck and ruleCheck **** udid={}, showType={}, ids={}", advParam.getUdid(), showType.getType(), ids);
        logger.info(StaticTimeLog.summary(Constants.RECORD_HANDLEADS_LOG));
        
        StaticTimeLog.record(Constants.RECORD_LOG, "handleAdsAll");

        if (adMap.size() == 0) {
            // 此处，经过规则判断不返回广告，需要记录'不投放'的次数
            List<Integer> adIds = New.arrayList();
            adIds.add(-1);
            cacheRecord.setNoAdHistoryMap(adIds, showType.getType());
            AdvCache.setAdPubRecordToCache(cacheRecord, advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType());
            return null;
        }
        

        // logger.info("过滤条件后，得到适合条件的Ad数目为：{}, udid={}, showType={}", adMap.size(),
        // advParam.getUdid(), showType);
        List<BaseAdEntity> entities = getEntities(advParam, cacheRecord, adMap, showType, queryParam);

        StaticTimeLog.record(Constants.RECORD_LOG, "getEntities");

        // 增加通用内容
        if (entities != null && entities.size() > 0) {
            for (BaseAdEntity ad : entities) {
                // 子类已经处理过了
//                if (ad.getPicsList() != null) {
//                    continue;
//                }
                AdContentCacheEle ace = adMap.get(ad.getId());
                if (ace != null) {
                    ad.setDisplayType(ace.getAds().getAdInnerContent().getDisplayType());
                    if((ad.getPicsList() == null || ad.getPicsList().isEmpty())) {
                        ad.setPicsList(ace.getAds().getAdInnerContent().getPicsList());
                    }
                    ad.setWxMiniProId(ace.getAds().getAdInnerContent().getWx_miniPro_id());
                    ad.setWxMiniProPath(ace.getAds().getAdInnerContent().getWx_miniPro_path());

                    String placementId = getPlaceMentId(showType, ad.getProvider_id(), advParam.getS(), ad.getDisplayType());
                    ad.setPlacementId(placementId);
                }
            }
        }

        StaticTimeLog.record(Constants.RECORD_LOG, "getPlaceMentId");

        logger.info(StaticTimeLog.summary(Constants.RECORD_LOG));

        // logger.info("serviceList cost time: udid={}, showType={}, cost={}", advParam.getUdid(), showType.getType(), 
        //             System.currentTimeMillis() - tBeginService);

        return entities;
    }

    /**
     * 获取上次投放的第一个广告，这次需要轮训到下一个 . 查看上一次的投放记录 . 用户实现‘每次必选改变广告’的需求
     * 
     * @param advParam
     * @param entities
     * @return boolean
     */
    protected boolean checkSendLog(AdvParam advParam, List<BaseAdEntity> entities, String showType) {
        if (entities == null || entities.size() == 0 || entities.size() == 1) {
            return true;
        }
        String sendLineFeedLogKey = AdvCache.getSendLineFeedLogKey(advParam.getUdid(), showType);
//        String lastSendIdStr = (String) CacheUtil.getFromRedis(sendLineFeedLogKey);
        String lastSendIdStr = (String) CacheUtil.getFromOftenRedis(sendLineFeedLogKey);
        if (lastSendIdStr != null) {
            try {
                // logger.info("找到未过期的投放记录，udid={}, lastSendId={}", advParam.getUdid(),
                // lastSendIdStr);
                int sendId = Integer.parseInt(lastSendIdStr);
                int size = entities.size();

                // 有之前投放的记录，确保当前列表第一个变化后，直接return即可
                if (entities.get(0).getId() == sendId) {
                    Collections.swap(entities, 0, size - 1);
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 按照权重计算，选择一个广告投放
    protected void rankAds(AdvParam advParam, List<BaseAdEntity> entities) {
        // 根据权重设置第一次展示
        // 获取所有符合规则的广告
        // logger.info("投放记录超时： udid={}, list={}", advParam.getUdid(),
        // JSONObject.toJSONString(lineFeedAds));
        if (entities != null && entities.size() > 1) {
            int totalWeight = 0;
            for (BaseAdEntity entity : entities) {
                totalWeight += entity.getAdWeight();
            }

            if (totalWeight > 0) {
                int randomOut = new Random().nextInt(totalWeight); // 取随机值
                // logger.info("randomOut={}, udid={}", randomOut, advParam.getUdid());
                int indexWeight = 0;
                for (int index = 0; index < entities.size(); index++) {
                    BaseAdEntity entity = entities.get(index);
                    if ((indexWeight += entity.getAdWeight()) > randomOut) {
                        Collections.swap(entities, 0, index);
                        // logger.info("投放记录超时，调整后**** ： udid={}, list={}", advParam.getUdid(),
                        // JSONObject.toJSONString(lineFeedAds));
                        return;
                    }
                }
            }
        }
    }

    protected void setClickAtLast(AdPubCacheRecord cacheRecord, List<BaseAdEntity> entities) {
        for (int index = 0; index < entities.size(); index++) {
            BaseAdEntity entity = entities.get(index);
            if (cacheRecord.hasClickedToday(entity.getId()) && (entity.getClickDown() == 1)) {
                // 点击过， 并且属于‘点击后排最后’的属性，那么直接挪到最后
                // 这里，可能会冲掉之前的‘轮播’或者权重设置。不过考虑到点击用户占极少数，所以可以接受
                Collections.swap(entities, index, entities.size() - 1);
            }
        }
    }

//    // 记录发送的第一条广告的log
//    protected void setSendLog(BaseAdEntity baseAdEntity, String udid, String showType) {
//        String sendLineFeedLogKey = AdvCache.getSendLineFeedLogKey(udid, showType);
//        CacheUtil.setToRedis(sendLineFeedLogKey, Constants.SEDN_LINEFEED_EXTIME, String.valueOf(baseAdEntity.getId()));
//    }

    // 记录广告发送记录， 默认只取第一条
    protected void writeSendLog(AdvParam advParam, AdContent ad, BaseAdEntity entity) {
        AnalysisLog.info(
                "[ADV_SEND]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},h5User={},h5Src={},provider_id={}",
                ad.getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(),
                advParam.getS(), advParam.getV(), advParam.getLineId(), advParam.getStnName(), advParam.getNw(), advParam.getIp(),
                advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getH5User(), advParam.getH5Src(),
                entity.getProvider_id());

    }

    // 记录发送日志，包括： 把第一条记录【第三方的】写入redis，把第一条写发送log
    // 只记录第三方广告的发送log到redis
    protected void recordSend(AdvParam advParam, AdPubCacheRecord cacheRecord, Map<Integer, AdContentCacheEle> adMap,
            ShowType showType, List<BaseAdEntity> entities) {

        StaticTimeLog.record(Constants.RECORD_LOG, "recordSendStart");
        if (entities != null) {
            BaseAdEntity entity = entities.get(0);
            writeSendLog(advParam, adMap.get(entity.getId()).getAds(), entity);
            // 某个版本用于设定‘每次下发必换第一条’的环境
            //			if (!entity.getProvider_id().equals("1")) {
            //				setSendLog(entity, advParam.getUdid(), showType.getType());
            //			}
            StaticTimeLog.record(Constants.RECORD_LOG, "writeLog");

            // 记录缓存
            int adId = entity.getId();
            // 2017.12.28，开屏广告记录不再走发送，而是走来自埋点日志处理的‘展示’
            // 2018-05-27 修改此处，去掉这个限制了 // TODO 【因为实际研发中，发送依旧是等于展示的】
            // if (showType != ShowType.OPEN_SCREEN)
            cacheRecord.buildAdPubCacheRecord(adId);
            if (adMap.get(adId).getRule().getUvLimit() > 0) {
                // 首次访问, 2017.12.28，这里对不再记录发送的开屏广告记录有误
                if (!cacheRecord.getUvMap().containsKey(adId)) {
                    adMap.get(adId).getRule().setUvCount();
                    cacheRecord.setAdToUvMap(adId);
                }
            }

            // 每分钟发送量
            adTimeCounts(cacheRecord, advParam.getUdid(), adMap.get(adId));
            StaticTimeLog.record(Constants.RECORD_LOG, "adTimeCounts");

            AdContent advContent = StaticAds.allAds.get(String.valueOf(adId));
            if (advContent != null && StringUtils.isNoneBlank(advContent.getProjectId()))
                CacheUtil.incrProjectSend(advContent.getProjectId(), 1);
            StaticTimeLog.record(Constants.RECORD_LOG, "incrProjectSend");
        }

        StaticTimeLog.record(Constants.RECORD_LOG, "recordSendEnd");
    }

    /**
     * 把符合规则的广告放到map中
     * 
     * @param num
     *            1 的时候只要一个广告,-1的时候全部
     */
    private StringBuilder setAds(Map<Integer, AdContentCacheEle> adMap, List<AdContentCacheEle> adsList, ShowType showType,
            AdvParam advParam, AdPubCacheRecord cacheRecord, int num, boolean isNeedApid, QueryParam queryParam) {
        // 取得符合规则的广告
        int i = 0;
        StringBuilder ids = new StringBuilder("");
        for (AdContentCacheEle cacheEle : adsList) {
            AdContent ad = cacheEle.getAds();
            UserClickRate clickRate = cacheEle.getUserClickRate();

            // 跟规则无关的一些判断
            if(!adCheck(ad, cacheRecord, advParam, showType)) {
                continue;
            }
            
            StaticTimeLog.record(Constants.RECORD_HANDLEADS_LOG, "setAdsBeforRuleCheck_" + i);
            // 遍历所有规则
            // 如果一条广告存在多条规则，那么只会返回第一条满足 ruleCheck条件的广告
            // 所以后续不可以再涉及到规则判断，否则这里就存在漏洞
            // ===> 所有的规则都需要在 ruleCheck 这一步搞定
            // 运营上避免这种情况的完美做法就是： 同一个advId最好不要对应多条ruleId。
            int j = 0;
            for (Rule rule : cacheEle.getRules()) {
                if (!ruleCheck(rule, advParam, ad, cacheRecord, showType, isNeedApid, queryParam, clickRate, i, j )) {
                    continue;
                }
                AdContentCacheEle adContentCacheEle = new AdContentCacheEle();
                adContentCacheEle.setAds(ad);
                adContentCacheEle.setRule(rule);
                adMap.put(ad.getId(), adContentCacheEle);
                ids.append(ad.getId() + ", ");
                j ++;
                if (num == 1) {
                    return ids;
                }
                break;
            }
            i ++;
        }
        return ids;
//        StaticTimeLog.summary(Constants.RECORD_HANDLEADS_LOG);
    }

    private boolean adCheck(AdContent ad, AdPubCacheRecord cacheRecord, AdvParam advParam, ShowType showType) {
        if (!ad.getShowType().equals(showType.getType())) {
            return false;
        }
        
        // 佛山详情页底部广告
        if(ad.getShowType().equals(ShowType.LINE_FEED_ADV.getType())) {
            if(StringUtils.isNoneBlank(advParam.getCityId()) && advParam.getCityId().equals("019")
                    && advParam.getScreenDensity() != -1.0 && advParam.getScreenHeight() != -1) {
                double density = advParam.getScreenHeight() / advParam.getScreenDensity();
                if(density <= 590.0) {
                    logger.info("foshan density return false, udid={}, cityId={}, screenHeight={}, screenDensity={}",
                            advParam.getUdid(), advParam.getCityId(), advParam.getScreenHeight(), advParam.getScreenDensity());
                    return false;
                }
            }
        }
        
//        // 点击了fakeORrate的，不予再投放fake广告了
//        if(ad.getAdInnerContent().getFakeRate())
        
        // 线路详情
        if (ad.getShowType().equals(ShowType.LINE_DETAIL.getType())) {
            // 当点击了右下角不感兴趣
            if (!lineDetailIsSilentTimePassed(ad, cacheRecord, advParam)) {
                return false;
            }
        } else if (ad.getShowType().equals(ShowType.DOUBLE_COLUMN.getType())
                || ad.getShowType().equals(ShowType.FEED_ADV.getType())) {
            // 当点击了双栏不感兴趣
            if (cacheRecord.isUninterest(ad.getId())) {
                logger.info("isSilentTimePassed return false,adtype={}, advId={},udid={}", showType, ad.getId(),
                        advParam.getUdid());
                return false;
            }

            // 如果距离太远，不投放单车。
            if (ad.getTargetType() == Constants.DOUBLE_BICYCLE_ADV) {
                if (!CommonService.isShowBikeByDistance(advParam)) {
                    return false;
                }

                if (advParam.getRideStatus() == 1) { // 0没骑车 ，1骑行中， 3骑行结束
                    logger.info("rideStatus is riding, udid={}", advParam.getUdid());
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 当只有右下角图片的时候判断
     * 
     * @param ad
     * @param cacheRecord
     * @param advParam
     * @return
     */
    private boolean lineDetailIsSilentTimePassed(AdContent ad, AdPubCacheRecord cacheRecord, AdvParam advParam) {

        boolean isUninterest = cacheRecord.isUninterest(ad.getId());

        if (isUninterest) {
            AdLineDetailInnerContent inner = (AdLineDetailInnerContent) ad.getInnerContent();
            // 只有右小角
            if (inner.getAdMode() == 8) {
                // 在设置超时时间之内
                if (!ManagerCommon.isSilentTimePassed(advParam.getUdid(), ad.getId(), isUninterest, inner.getSilentTime(),
                        cacheRecord)) {
                    logger.info("isSilentTimePassed return false,adtype={}, advId={}, udid={}", ShowType.LINE_DETAIL, ad.getId(),
                            advParam.getUdid());
                    return false;
                } else {
                    // 超过时间限制了
                    cacheRecord.removeUninterestAds(ad.getId());
                }
            }
            logger.info("udid={},advId={},isUninterest", advParam.getUdid(), ad.getId());
        }
        return true;
    }

    /***
     * 广告规则校验
     * 
     * @return true 可投放 ; fasle 不可投放
     */
    private boolean ruleCheck(Rule rule, AdvParam advParam, AdContent ad, AdPubCacheRecord cacheRecord, ShowType showType,
            boolean isNeedApid, QueryParam queryParam, UserClickRate clickRate, int i, int j) {
        // 存在黑名单中
        if (StaticAds.isBlack(ad.getId(), advParam.getUdid())) {
            // logger.info("black list,advId={},udid={}", ad.getId(), advParam.getUdid());
            return false;
        }
        if (rule.hasPlatforms() && !rule.isPlatformMatch(advParam.getS(), advParam.getH5Src())) {
            return false;
        }
        // 开屏和浮层的老接口preloadAds需要返回两天的数据
        // 仅仅 preloadAds接口用到这一块
        if (queryParam.isOldMany()) {
            // 当前日期大于结束日期
            if (rule.isEndDateOverdue()) {
                return false;
            }
        } else {
            // 是否过期
            if (rule.isOverdue()) {
                return false;
            }
        }

        if (rule.hasCities() && !rule.isCityMatch(advParam.getCityId())) {
            return false;
        }
        if ((rule.hasVersions() || rule.hasExcludeVersions()) && !rule.isVersionMatch(advParam.getV())) {
            return false;
        }
        StaticTimeLog.record(Constants.RECORD_HANDLEADS_LOG, "afterVersions_" + i + "," + j);
        if (!rule.isVersionNoLessThan(advParam.getV(), advParam.getS())) {
            logger.info("version no less return false, udid={},advId={},ruleId={}, v={}, s={}", advParam.getUdid(), ad.getId(),
                    rule.getRuleId(), advParam.getV(), advParam.getS());
            return false;
        }

        // 开屏是否投给MIUI，投且只投
        if (showType == ShowType.OPEN_SCREEN
                && !rule.devicePub(advParam.getUdid(), advParam.getDeviceType(), advParam.getStartMode())) {
            logger.info("MIUI OPEN return false, deviceType={}, canPubMIUI={}, startMode={}, udid={}", advParam.getDeviceType(),
                    rule.getCanPubMIUI(), advParam.getStartMode(), advParam.getUdid());
            return false;
        }
        // 冷热启动模式测试
        if (showType == ShowType.OPEN_SCREEN && rule.getStartMode() != 0 && !rule.checkStartMode(advParam.getStartMode())) {
            logger.info("startMode return false, advId={}, ruleId={}, startMode={}, udid={}", ad.getId(), rule.getRuleId(),
                    advParam.getStartMode(), advParam.getUdid());
            return false;
        }
        
        if(rule.getLessThanAndroidSV() != null && advParam.getS() != null 
                && advParam.getS().equals("android") && !rule.isAndroidSVMatch(advParam.getSv())) {
            logger.info("android sv return false，advId={}, ruleId={}, sv={}, udid={}", ad.getId(), rule.getRuleId(),
                    advParam.getSv(), advParam.getUdid());
            return false;
        }
        
        if (rule.hasNetStatus() && !rule.isNetStatusMatch(advParam.getNw())) {
            return false;
        }
        if (!rule.isUserTypeMatch(advParam)) {
            return false;
        }
        StaticTimeLog.record(Constants.RECORD_HANDLEADS_LOG, "afterUserType_" + i + "," + j);
        if (rule.getScreenHeight() > 0 && advParam.getScreenHeight() < rule.getScreenHeight()) {
            logger.info("screenHeithg return false.advId={}, rule={}, s={}, udid={}, height={}", ad.getId(), rule.getRuleId(),
                    advParam.getS(), advParam.getUdid(), advParam.getScreenHeight());
            return false;
        }

        if (!rule.isLineStationMap(advParam.getLineId(), advParam.getStnName(), advParam.getStnOrder(), advParam.getUdid())) {
            logger.info("isLineStationMap return false,ruleId={},lineId={},stnName={},order={},udid={}", rule.getRuleId(),
                    advParam.getLineId(), advParam.getStnName(), advParam.getStnOrder(), advParam.getUdid());
            return false;
        }
        
        // 首页插屏广告
        // stats_act不为空的时候，可能会不予返回
        if (showType == ShowType.INTERSHOME_ADV && StringUtils.isNoneBlank(advParam.getStatsAct())) {
            if (StringUtils.isBlank(rule.getInterstitialType()) && advParam.getStatsAct().equals("enter")) {
                return false;
            }
            if (StringUtils.isNoneBlank(rule.getInterstitialType())
                    && !rule.getInterstitialType().contains(advParam.getStatsAct())) {
                return false;
            }
        }

        // 站点名匹配
        if (!rule.isStationMatch(advParam.getStnName())) {
            return false;
        }

        if (!rule.isPostionMatch(advParam.getLat(), advParam.getLng())) {
            logger.info("isPostionMatch return false,ruleId={},lng={},lat={},udid={}", rule.getRuleId(), advParam.getLng(),
                    advParam.getLat(), advParam.getUdid());
            return false;
        }
        StaticTimeLog.record(Constants.RECORD_HANDLEADS_LOG, "afterPostionMatch_" + i + "," + j);
        // 总点击次数判断
        if (rule.getTotalClickPV() > 0 && rule.currentTotalClickPV(ad) >= rule.getTotalClickPV()) {
            // logger.info("totalClickPV return false, ruleId={}, udid={}",
            // rule.getRuleId(), advParam.getUdid());
            return false;
        }
        StaticTimeLog.record(Constants.RECORD_HANDLEADS_LOG, "afterTotalClickPv_" + i + "," + j);
        // 每个人点击次数判断
        if (rule.getIsClickEndPush() > 0 && cacheRecord.hasClicked(ad.getId())) { // 点击后不再投放
            logger.info("hasClicked ad return false, ruleId={},advId={}, udid={}", rule.getRuleId(), ad.getId(),
                    advParam.getUdid());
            return false;
        }
        
        if (rule.isOverUvCount(advParam.getUdid())) {
            // 如果uvlimit次数已经饱和,查看该用户是否投放过,没投放过就返回了
            if (!cacheRecord.isSendUv(ad.getId())) {
                logger.info("isOverUvCount return false,ruleId={},advId={}, udid={}", rule.getRuleId(), ad.getId(),
                        advParam.getUdid());
                return false;
            }
        }
        
        // 项目控制
        if (StringUtils.isNoneBlank(ad.getProjectId())) {
            if (rule.getProjectClick() > 0 && rule.projectClickOut(advParam.getUdid(), ad.getProjectId())) {
                logger.info("projectClick return false, udid={}, advId={}, ruleId={}", advParam.getUdid(), ad.getId(),
                        rule.getRuleId());
                return false;
            }

            if (rule.getProjectTotalClick() > 0 && rule.projectTotalClickOut(ad.getProjectId())) {
                logger.info("ProjectTotalClick return false, udid={}, advId={},  ruleId={}", advParam.getUdid(), ad.getId(),
                        rule.getRuleId());
                return false;
            }

            if (rule.getProjectDayClick() > 0 && rule.projectDayClickOut(ad.getProjectId())) {
                logger.info("ProjectDayClick return false, udid={}, advId={},  ruleId={}", advParam.getUdid(), ad.getId(),
                        rule.getRuleId());
                return false;
            }

            if (rule.getProjectTotalSend() > 0 && rule.projectTotalSendOut(ad.getProjectId())) {
                logger.info("ProjectTotalSend return false, udid={}, advId={},  ruleId={}", advParam.getUdid(), ad.getId(),
                        rule.getRuleId());
                return false;
            }

            if (rule.getProjectDaySend() > 0 && rule.projectDaySendOut(ad.getProjectId())) {
                logger.info("ProjectDaySend return false, udid={}, advId={},  ruleId={}", advParam.getUdid(), ad.getId(),
                        rule.getRuleId());
                return false;
            }
        }
        StaticTimeLog.record(Constants.RECORD_HANDLEADS_LOG, "afterProject_" + i + "," + j);
        // 判断自动黑名单
        if (rule.getUvLimit() > 0 && cacheRecord.isDisplayUv(ad.getId(), rule.getAutoBlackList())) {
            logger.info("autoBlack return false,ruleId={},udid={}", rule.getRuleId(), advParam.getUdid());
            return false;
        }

        // 次数判断
        // 包括，每人点击次数限制、每人投放次数上限、每人days天内总投放次数上限
        if (cacheRecord != null && !cacheRecord.todayCanPub(ad, rule)) {
//            logger.info("todayCanPub return false,advId={},ruleId={},udid={}", ad.getId(), rule.getRuleId(), advParam.getUdid());
            return false;
        }
        StaticTimeLog.record(Constants.RECORD_HANDLEADS_LOG, "afterTodayCanPub_" + i + "," + j);
        // 每个时间段的发送次数，目前是考察到分钟
        if (rule.getTotalCount() > 0
                && !rule.adTimeCounts(ad.getId(), rule.getRuleId(), cacheRecord, advParam.getUdid(), false, i, j)) {
            // logger.info("hasAdTimeCounts return false,ruleId={},udid={}",
            // rule.getRuleId(), advParam.getUdid());

            return false;
        }
        StaticTimeLog.record(Constants.RECORD_HANDLEADS_LOG, "afterAdTime_" + i + "," + j);
        // 最小时间间隔，热启动 开屏广告用
        if (advParam.getStartMode() == 1 && rule.getMinIntervalTime() > 0) {
            if (cacheRecord != null && !cacheRecord.hasPassIntervalTime(ad.getId(), rule.getMinIntervalTime())) {
                logger.info("cannot pub OpenAd, because of minInterval time not past, ruleId={}, udid={}", rule.getRuleId(),
                        advParam.getUdid());
                return false;
            }
        }

        // 最小次数间隔，feed流广告用。 两次广告展示之间最少间隔几次调用
        // 2018-05-27 适用： 开屏、首页栏、站点位置、详情页底部
        if (rule.getMinIntervalPages() > 0
        // && ad.getShowType().equals(ShowType.FEED_ADV.getType())
        ) {
            if (cacheRecord != null && !cacheRecord.canPubFeedAd(ad, rule, showType.getType())) {
                logger.info("cannot pub feedAdv because of pages minInterval, ruleId={}, udid={}", rule.getRuleId(),
                        advParam.getUdid());
                return false;
            }
        }

        // udid 模糊匹配
        if (StringUtils.isNoneBlank(rule.getUdidPattern()) && advParam.getUdid() != null) {
            if (!advParam.getUdid().matches(rule.getUdidPattern())) {
                // logger.info("udid pattern matche return
                // false,ruleId={},udidPattern={},udid={}", rule.getRuleId(),
                // rule.getUdidPattern(), advParam.getUdid());
                return false;
            }
        }
        StaticTimeLog.record(Constants.RECORD_HANDLEADS_LOG, "afterUdidPater_" + i + "," + j);
        // 判断点击概率是否达标
//        if (clickRate != null) {
//            double rate = getClickStandardRate(advParam.getUdid(), ad.getId(), rule.getRuleId(), showType);
//            // logger.info("clickRate info : ruleId={},udid={},rate={},rateStandard={}",
//            // rule.getRuleId(),
//            // advParam.getUdid(), clickRate.getRate(), rate);
//            if (rate >= 0.0 && clickRate.getRate() < rate) {
//                // logger.info("clickRate return
//                // false,ruleId={},udid={},rate={},rateStandard={}", rule.getRuleId(),
//                // advParam.getUdid(), clickRate.getRate(), rate);
//                return false;
//            }
//        }

        return true;
    }

//    /**
//     * 是否通过花钱取消广告
//     * 
//     * @return
//     */
//    private boolean isInvalidAccountId(String accountId) {
//        if (accountId == null) {
//            return false;
//        }
//        try {
//            return invaildService.isInvalid(accountId);
//        } catch (Exception e) {
//            logger.error(e.getMessage() + ",accountId:" + accountId, e);
//        }
//        return false;
//    }

    /**
     * 应该按照优先级倒叙排序
     */
    private static final Comparator<AdContentCacheEle> AD_CONTENT_COMPARATOR = new Comparator<AdContentCacheEle>() {
        @Override
        public int compare(AdContentCacheEle o1, AdContentCacheEle o2) {
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            return o2.getAds().getPriority() - o1.getAds().getPriority();
        }
    };

    // private static final Comparator<BaseAdEntity> ENTITY_COMPARATOR = new
    // Comparator<BaseAdEntity>() {
    // @Override
    // public int compare(BaseAdEntity o1, BaseAdEntity o2) {
    // if (o1 == null)
    // return -1;
    // if (o2 == null)
    // return 1;
    // return o2.getPriority() - o1.getPriority();
    // }
    //
    // };

    /**
     * 记录 AdPubCacheRecord
     * 
     * @param cateGory
     * @param advParam
     * @param cacheRecord
     * @param adMap
     * @param showType
     * @param queryParam
     * @return
     * @throws Exception
     */
    protected BaseAdEntity getEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) {
        BaseAdEntity entity = null;
        try {
            entity = dealEntity(cateGory, advParam, cacheRecord, adMap, showType, queryParam, isRecord);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return entity;
        }
        boolean isSelfAd = false;
        int adId = -1;
        boolean hasSendSelfAd = false; // 今天之前是否投放过该自采买广告
        if (cateGory == null && entity != null) // 老版，不需要第三方广告，仅处理自采买
        {
            isSelfAd = true;
            adId = entity.getId();
            hasSendSelfAd = cacheRecord.hasPulished(adId);
        }
        if (cateGory != null && cateGory.getAdType() == 1) // 新版，策略返回自采买广告
        {
            isSelfAd = true;
            adId = cateGory.getAdId();
            hasSendSelfAd = cacheRecord.hasPulished(adId);
        }

        boolean isAutoRefresh = false;
        if (advParam.getStatsAct() != null && advParam.getStatsAct().equals(Constants.STATSACT_AUTO_REFRESH)) {
            isAutoRefresh = true;
        }
        if (isSelfAd && !(isAutoRefresh && hasSendSelfAd)) { // 记录自采买广告的次数
            // 2017.12.28，开屏广告记录不再走发送，而是走来自埋点日志处理的‘展示’
            // 2018-05-27 修改此处，去掉这个限制了 // TODO 【因为实际研发中，发送依旧是等于展示的】
            // if (showType != ShowType.OPEN_SCREEN)
            cacheRecord.buildAdPubCacheRecord(adId);
            if (adMap.get(adId).getRule().getUvLimit() > 0) {
                // 首次访问, 2017.12.28，这里对不再记录发送的开屏广告记录有误
                if (!cacheRecord.getUvMap().containsKey(adId)) {
                    adMap.get(adId).getRule().setUvCount();
                    cacheRecord.setAdToUvMap(adId);
                }
            }
            AdContent advContent = StaticAds.allAds.get(String.valueOf(adId));
            if (advContent != null && StringUtils.isNoneBlank(advContent.getProjectId()))
                CacheUtil.incrProjectSend(advContent.getProjectId(), 1);
        }

        if (showType == ShowType.LINE_DETAIL) {
            RecordManager.recordAdd(advParam.getUdid(), showType.getType(), cacheRecord);
        } else {
            // if (showType != ShowType.OPEN_SCREEN) // 2017.12.28，
            // // 开屏广告记录不再走发送，而是走来自埋点日志处理的‘展示’
            RecordManager.recordAdd(advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
        }

        return entity;

    }

    private List<BaseAdEntity> getEntities(AdvParam advParam, AdPubCacheRecord cacheRecord, Map<Integer, AdContentCacheEle> adMap,
            ShowType showType, QueryParam queryParam) {
        List<BaseAdEntity> entities = null;
        try {
            entities = dealEntities(advParam, cacheRecord, adMap, showType, queryParam);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return entities;
        }

        if (showType == ShowType.LINE_DETAIL) {
            RecordManager.recordAdd(advParam.getUdid(), showType.getType(), cacheRecord);
        } else {
            // 2017.12.28开屏广告记录不再走发送，而是走来自埋点日志处理的‘展示’
            // 2018.06.01 开屏重新开始记录发送
            // if (showType != ShowType.OPEN_SCREEN)
            RecordManager.recordAdd(advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
        }

        StaticTimeLog.record(Constants.RECORD_LOG, "recordAdd");
        return entities;
    }

    protected void adTimeCounts(AdPubCacheRecord cacheRecord, String udid, AdContentCacheEle adc) {
        // 每个时间段的发送次数
        /*
         * if (adc.getRule().hasAdTimeCounts() &&
         * !adc.getRule().adTimeCounts(adc.getRule().getRuleId(), cacheRecord, udid,
         * true)) { logger.info("hasAdTimeCounts return false,ruleId={},udid={}",
         * adc.getRule().getRuleId(), udid); throw new
         * IllegalArgumentException("hasAdTimeCounts error"); }
         */

        if (adc.getRule().getTotalCount() > 0) {
            // 记录每分钟的发送量
            adc.getRule().adTimeCounts(adc.getAds().getId(), adc.getRule().getRuleId(), cacheRecord, udid, true, -1, -1);
            // 记录总投放pv到缓存
            // logger.info("记录投放pv次数 advId={}, ruleId={}", adc.getAds().getId(),
            // adc.getRule().getRuleId());
            // DynamicRegulation.IncValueSedPV(adc.getAds().getId(),
            // adc.getRule().getRuleId());
        }
    }

    protected abstract BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord)
			throws Exception;

    protected abstract List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
			Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception;

    private StringBuilder handleAds(Map<Integer, AdContentCacheEle> adMap, List<AdContentCacheEle> adsList, ShowType showType,
            AdvParam advParam, AdPubCacheRecord cacheRecord, boolean isNeedApid, QueryParam queryParam) {
        if (isNeedApid) {
            return setAds(adMap, adsList, showType, advParam, cacheRecord, -1, isNeedApid, queryParam);
        } else {
            return setAds(adMap, adsList, showType, advParam, cacheRecord, 1, isNeedApid, queryParam);
        }
    }

    /**
     * 获取用户该广告的点击率
     * 
     * @param udid
     * @param advId
     * @param showType
     * @return
     */
//    private double getClickStandardRate(String udid, int advId, String ruleId, ShowType showType) {
//
//        boolean delUdidRule = false;
//        if (delUdidRule) {
//            StaticAds.delUdidRule(udid, advId, showType);
//        }
//
//        return CalculatePerMinCount.getCTRRate(advId + "#" + ruleId);
//    }

    private List<AdContentCacheEle> mergeAllAds(AdvParam advParam, ShowType showType, List<AdContentCacheEle> adsList,
            boolean isNeedApid) {
        adsList = CommonService.mergeAllAds(adsList); // 需要按照adid和ruleid做合并
        //		String adIdStr = "";
        //		for (AdContentCacheEle ad : adsList) {
        //			adIdStr += ad.getAds().getId();
        //			for (Rule rule : ad.getRules()) {
        //				adIdStr += "->" + rule.getRuleId();
        //			}
        //			adIdStr += ";";
        //		}
        //		logger.info(
        //				"[getallavailableAds]:udid={}, adtype={}, isNeedApi={}, type={}, advIds={}, ac={},s={}, "
        //						+ "cityId={}, v={}, vc={}, li={}, sn={}, startMode={}, H5Src={}, wxs={}",
        //				advParam.getUdid(), showType, isNeedApid, advParam.getType(), adIdStr, advParam.getAccountId(),
        //				advParam.getS(), advParam.getCityId(), advParam.getV(), advParam.getVc(), advParam.getLineId(),
        //				advParam.getStnName(), advParam.getStartMode(), advParam.getH5Src(), advParam.getWxs());
        return adsList;
    }

    /*
     * 获取用户投放缓存记录
     */
    protected AdPubCacheRecord gainCacheRecord(AdvParam advParam, ShowType showType) {
        AdPubCacheRecord cacheRecord = null;
        // 放缓存的时候除了线路详情就是双栏
        long t1 = System.currentTimeMillis();
        if (showType.getType().equals(ShowType.LINE_DETAIL.getType())) {
            cacheRecord = AdvCache.getAdPubRecordFromCache(advParam.getUdid(), ShowType.LINE_DETAIL.getType());
        } else {
            cacheRecord = AdvCache.getAdPubRecordFromCache(advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType());
        }
        long t2 = System.currentTimeMillis();
        if (t2 - t1 > 50) {
            TimeLong.info("get from ocs cost time: {}", t2 - t1);
        }
        if (cacheRecord == null) {
            cacheRecord = new AdPubCacheRecord();
        }
        return cacheRecord;
    }

    /*
     * 详情页|新版开屏广告，策略处理方法
     */
    private BaseAdEntity needCategoryHandle(AdvParam advParam, ShowType showType, QueryParam queryParam, boolean isRecord,
            AdPubCacheRecord cacheRecord, Map<Integer, AdContentCacheEle> adMap) {
        AdCategory cateGory = null;
        try {
            if (showType != ShowType.OPEN_SCREEN && showType != ShowType.FULL_SCREEN) {
                cateGory = AdDispatcher.getAdCategory(advParam, cacheRecord, adMap);
                if (cateGory != null && cateGory.getApiType() > 3) {
                    throw new IllegalArgumentException("请求了线路详情广告,然而返回的广告类型为" + cateGory.getApiType());
                }
            } else {
                cateGory = AdDispatcher.getOpenAdCategory(advParam, cacheRecord, adMap);
                if (cateGory != null && cateGory.getApiType() <= 3 && cateGory.getApiType() != -1) {
                    throw new IllegalArgumentException("请求了开屏广告,然而返回的广告类型为" + cateGory.getApiType());
                }
            }
        } catch (IllegalArgumentException e) {
            logger.error("udid={},category errormessage={}", advParam.getUdid(), e.getMessage());
            return null;
        }

        logger.info("[cateGoryInfo]:udid={}, cateGory={}", advParam.getUdid(), cateGory);
        if (cateGory != null) {
            // 暂时失联的时候不显示第三方广告
            if (cateGory.getAdType() > 1 && advParam.getInState() != null
                    && (advParam.getInState().equals("-2") || advParam.getInState().equals("-5"))) {
                return null;
            }
            return getEntity(cateGory, advParam, cacheRecord, adMap, showType, queryParam, isRecord);
        } else { // cateGory为空的时候记录
            cacheRecord.buildAdPubCacheRecord(-1);
            if (showType == ShowType.LINE_DETAIL) {
                cacheRecord.setAdHistory(new AdCategory(-1, -1, -1));
                AdvCache.setAdPubRecordToCache(cacheRecord, advParam.getUdid(), ShowType.LINE_DETAIL.getType());
            }
            // 开屏和浮层
            else {
                cacheRecord.setOpenAdHistory(new AdCategory(-1, -1, -1));
                AdvCache.setAdPubRecordToCache(cacheRecord, advParam.getUdid(), ShowType.DOUBLE_COLUMN.getType());
            }
            return null;
        }
    }

    private List<AdContentCacheEle> gainAllValidAds(AdvParam advParam, ShowType showType, boolean isNeedApid) {
        // 取得所有手动投放广告
        List<AdContentCacheEle> adsList = CommonService.getAllAdsList(advParam.getUdid(), advParam.getAccountId(), showType);
        if (adsList == null || adsList.size() == 0) {
            // logger.info("[getallavailableAds ISNULL]:udid={}, adtype={}, isNeedApi={},
            // type={}, ac={}, s={}", advParam.getUdid(),
            // showType, isNeedApid, advParam.getType(), advParam.getAccountId(),
            // advParam.getS());
        } else {
            // 合并广告
            adsList = mergeAllAds(advParam, showType, adsList, isNeedApid);
        }
        return adsList;
    }

    /**
     * 最初的检测
     * 
     * @param p
     * @param showType
     * @return true 检测成功,false 检测失败
     */
    protected boolean beforeCheck(AdvParam p, ShowType showType) {
        if (SynchronizationControl.isReload()) {
            logger.info("reload is Running");
            return false;
        }

        // 是否展开底部广告
        if (showType == ShowType.LINE_FEED_ADV && StaticAds.SETTINGSMAP.containsKey(Constants.SETTING_SCREENHEIGHT_KEY)) {
            String sL = (StaticAds.SETTINGSMAP.get(Constants.SETTING_SCREENHEIGHT_KEY));
            try {
                if (sL != null && Integer.parseInt(sL) >= p.getScreenHeight()) {
                    logger.info("screenHeight is too low to show bottomAds, deviceHeight={}, redLine={}",
                            p.getScreenHeight(), sL);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 关闭广告位之后，一段时间内不再打开
        String closeTime = CacheUtil.getCloseAdTime(p.getUdid(), showType.getType());
        if (StringUtils.isNoneBlank(closeTime)) {
            long expireTime = Long.parseLong(StaticAds.SETTINGSMAP.get(Constants.SETTING_COSE_AD_KEY));
            if (((System.currentTimeMillis() - Long.parseLong(closeTime))) / 1000 < expireTime) {
                logger.info("close ad time not pass ,udid={}, pid={}, closetime={}", p.getUdid(), showType.getType(),
                        closeTime);
                return false;
            }
        }

        // 香港不投广告
        if (StringUtils.isNotBlank(p.getCityId()) && (p.getCityId().equals("085"))) {
            return false;
        }

        // 详情页cshow非空，不等于linedetail的不返回
        if (showType.getType().equals(ShowType.LINE_DETAIL)) {
            if (StringUtils.isNoneBlank(p.getCshow()) && !p.getCshow().equals(Constants.CSHOW_LINEDETAIL)) {
                return false;
            }
        }

        // android 内核4.4一下的，不返回广告 20180118
        // 这个方法不够严谨，当android更新到版本10的时候，会出错
        Platform platform = Platform.from(p.getS());
        if (platform.isAndriod(platform.getDisplay())) {
            if (StringUtils.isNoneBlank(p.getSv()) && (p.getSv().compareTo("4.4") < 0)) {
                return false;
            }
        }
        // 来自城市服务|泰安，不返回广告
        if (platform.isH5(platform.getDisplay())) {
            if (StringUtils.isNoneBlank(p.getFrom()) && (p.getFrom().equalsIgnoreCase("city_taian")
                    || p.getFrom().equalsIgnoreCase("wxcityservice"))) {
                return false;
            }
        }
        
        // for temp TEST
        boolean isforTempTest = false;
        String forTestStr = StaticAds.SETTINGSMAP.get(Constants.SETTING_TESTNEWUSER_AD_KEY);
        if(StringUtils.isNoneBlank(forTestStr))
            isforTempTest = Boolean.parseBoolean(forTestStr);
        if (isforTempTest) {
            if (p.getUdid().startsWith("0") || p.getUdid().startsWith("1") || p.getUdid().startsWith("okBHq0A")
                    || p.getUdid().startsWith("okBHq0B")) {
                boolean isNew = UserHelper.isNewUser(p.getS(), p.getH5Src(), p.getUdid());
                if (isNew) {
                    AnalysisLog.info("FORTEST, NOADRETURN, s={}, udid={}", p.getS(), p.getUdid());

                    return false;
                }
            }
        }

        return true;
    }

    /*
     * 双栏点击了不感兴趣，默认15分钟 true：继续投放广告 false:不投放广告
     */
    public boolean doubleAdsIsSilentTimePassed(AdContent ad, AdPubCacheRecord cacheRecord, AdvParam advParam) {
        boolean isUninterest = cacheRecord.isUninterest(ad.getId());
        if (isUninterest) {
            if (!ManagerCommon.isSilentTimePassed(advParam.getUdid(), ad.getId(), isUninterest, 15, cacheRecord)) {
                logger.info("isSilentTimePassed return false,advId={},udid={}", ad.getId(), advParam.getUdid());
                return false;
            } else {
                // 超过时间限制了
                cacheRecord.removeUninterestAds(ad.getId());
            }
        }
        return true;
    }

    // /**
    // * 获取优先级最高的那一批详情页广告
    // * @param availableAds
    // * @return
    // */
    // private static List<AdContentCacheEle>
    // filterAvailableAdsByPriority(List<AdContentCacheEle> availableAds) {
    // if (availableAds == null || availableAds.size() == 0) {
    // return null;
    // }
    // ArrayList<AdContentCacheEle> highestPriorityAdsList = new ArrayList<>();
    // int highestPriority = 0;
    // for (AdContentCacheEle adInfo : availableAds) {
    // int currentPriority = adInfo.getAds().getPriority();
    // if (currentPriority < highestPriority) {
    // continue;
    // }
    // if (currentPriority > highestPriority) {
    // highestPriority = currentPriority;
    // highestPriorityAdsList.clear();
    // }
    // highestPriorityAdsList.add(adInfo);
    // }
    // return highestPriorityAdsList;
    // }

    public static void main(String[] args) {
        List<AdContentCacheEle> adsList = new ArrayList<>();
        AdContentCacheEle a1 = new AdContentCacheEle();
        AdContent add1 = new AdContent();
        add1.setId(1);
        a1.setAds(add1);
        a1.getAds().setPriority(1);

        AdContentCacheEle a2 = new AdContentCacheEle();
        AdContent add2 = new AdContent();
        add2.setId(2);
        a2.setAds(add2);
        a2.getAds().setPriority(2);

        AdContentCacheEle a3 = new AdContentCacheEle();
        AdContent add3 = new AdContent();
        add3.setId(3);
        a3.setAds(add3);
        a3.getAds().setPriority(2);
        adsList.add(a1);
        adsList.add(a2);
        adsList.add(a3);

        logger.info("1***adsList={}", JSONObject.toJSONString(adsList));
        System.out.println(JSONObject.toJSONString(adsList));

        Collections.shuffle(adsList); // 打乱顺序
        System.out.println(JSONObject.toJSONString(adsList));
        logger.info("2***adsList={}", JSONObject.toJSONString(adsList));

        Collections.sort(adsList, AD_CONTENT_COMPARATOR);
        System.out.println(JSONObject.toJSONString(adsList));
        logger.info("3***adsList={}", JSONObject.toJSONString(adsList));
    }

    private String getPlaceMentId(ShowType showType, String provider_id, String phoneType, int displayType) {

        String placeMentId = null;

        // 开屏
        if (showType.getValue() == ShowType.OPEN_SCREEN.getValue()) {
            if (phoneType.equalsIgnoreCase("ios")) {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "5050032383403340";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "1522609003688";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    placeMentId = "801451568";
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    placeMentId = "46232AB17BDA70BED71794AD4915D12A";
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "faf49b07805cca0a38097a732f388462";
                }

            } else {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "7030038393106222";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    placeMentId = "800673832";
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    placeMentId = "D028C0ADDDBC38952DA01241B4939E64";
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "31b89bd78e296d43ca0b1128779613cc";
                }
            }
        }
        // 双栏
        else if (showType.getValue() == ShowType.DOUBLE_COLUMN.getValue()) {
            if (phoneType.equalsIgnoreCase("ios")) {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "1000135358440755";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    if (displayType == 3) {
                        placeMentId = "901451895";
                    } else {
                        placeMentId = "901451521";
                    }
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "3E4C06551E3CC1FB40E83E18A6523BAD";
                    } else {
                        placeMentId = "5F7EDBCCC6C116C07DBB40EB9A937F4E";
                    }
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "05de0f72d83e44de913534cecda03451";
                }
                // 百度
                else if (provider_id.equals("5")) {
                    if (displayType == 3) {
                        placeMentId = "5846498";
                    } else {
                        placeMentId = "5826167";
                    }
                }
            } else {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "2030539481050032";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    if (displayType == 3) {
                        placeMentId = "900673292";
                    } else {
                        placeMentId = "900673519";
                    }
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "ACB0BED305BBA908DEA75B0036E51ECE";
                    } else {
                        placeMentId = "C23BFCFFE1F3D8D5C06D7E1AEEA83812";
                    }
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "7a7b059ca39624f1c1ec24fa2ad375f6";
                }
                // 百度
                else if (provider_id.equals("5")) {
                    if (displayType == 3) {
                        placeMentId = "5847843";
                    } else {
                        placeMentId = "5826167";
                    }
                }
            }
        }
        // 站点
        else if (showType.getValue() == ShowType.STATION_ADV.getValue()) {

            if (phoneType.equalsIgnoreCase("ios")) {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "9060637344635371";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    placeMentId = "901451537";
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    placeMentId = "70EB266B2C8429B0B6C10D9B6F9BFA93";
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "0fe897890119007664cfd009556ce283";
                }
                // 百度
                else if (provider_id.equals("5")) {
                    placeMentId = "5826171";
                }

            } else {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "6000631364333392";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    placeMentId = "900673616";
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {

                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "59856e17db1d73fb3dbcb5af6c1ab10f";
                }
            }

        }
        // 详情页底部
        else if (showType.getValue() == ShowType.LINE_FEED_ADV.getValue()) {
            if (phoneType.equalsIgnoreCase("ios")) {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "3080736383701333";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "1526495932479";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    if (displayType == 3) {
                        placeMentId = "901451382";
                    } else {
                        placeMentId = "901451891";
                    }
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "2FC6D42CC5E5536AA8B3EA1ACC530A4C";
                    } else {
                        placeMentId = "2D8857EE0D286E80203F7334F8356B1C";
                    }
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "e3f49841bbd3ceb0c6a531ca32f4a754";
                }

                // 百度
                else if (provider_id.equals("5")) {
                    if (displayType == 3) {
                        placeMentId = "5846500";
                    } else {
                        placeMentId = "5826170";
                    }
                }

            } else {
                //	广点通
                if (provider_id.equals("2")) {
                    if (displayType == 3) {
                        placeMentId = "3E4C06551E3CC1FB40E83E18A6523BAD";
                    } else {
                        placeMentId = "957963E3D7047F783BE1CBFC450BF458";
                    }
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    if (displayType == 3) {
                        placeMentId = "901451382";
                    } else {
                        placeMentId = "901451382";
                    }
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "FD95828C09A32D712082DC08D36CC15D";
                    } else {
                        placeMentId = "5CBF4E804C06EBF6EEAF93DC5EA6BBCF";
                    }
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "aae3f1e9fd3c10be479138b6b1288530";
                }
                // 百度
                else if (provider_id.equals("5")) {
                    if (displayType == 3) {
                        placeMentId = "5847849";
                    } else {
                        placeMentId = "5847849";
                    }
                }
            }
        }

        // 右上角
        else if (showType.getValue() == ShowType.LINE_RIGHT_ADV.getValue()) {
            if (phoneType.equalsIgnoreCase("ios")) {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "6040730401958055";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    placeMentId = "900673291";
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "3E4C06551E3CC1FB40E83E18A6523BAD";
                    } else {
                        placeMentId = "957963E3D7047F783BE1CBFC450BF458";
                    }
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "";
                }

            } else {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "4060239431859044";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    placeMentId = "901451875";
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    placeMentId = "2EC979D4F845F81DD899B62F497E3F67";
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "";
                }
            }
        }

        // 换乘
        else if (showType.getValue() == ShowType.TRANSFER_ADV.getValue()) {
            if (phoneType.equalsIgnoreCase("ios")) {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "2050932544734626";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "1528940869809";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    if (displayType == 3) {
                        placeMentId = "901451787";
                    } else {
                        placeMentId = "901451021";
                    }
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "94734C0A46CCBAE8E51C5CAEEE4327D2";
                    } else {
                        //    					placeMentId = "18F4307EE781076E5FCB43DB0413C6FD";
                        placeMentId = "02C9DEB51D1C6E13FACCDF0E0DC7A6AB";
                    }
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "982c92cba64fc11a80d27e02d8755895";
                }

                // 百度
                else if (provider_id.equals("5")) {
                    if (displayType == 3) {
                        placeMentId = "5846484";
                    } else if (displayType == 4) {
                        placeMentId = "5846497";
                    } else {
                        placeMentId = "5846481";
                    }
                }

            } else {
                //	广点通
                if (provider_id.equals("2")) {
                    if (displayType == 3) {
                        placeMentId = "7090134575505819";
                    } else {
                        placeMentId = "3010534505808848";
                    }
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    if (displayType == 3) {
                        placeMentId = "900673492";
                    } else {
                        placeMentId = "900673424";
                    }
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "AE9A21B49A87F9B6C67EC7548FB5DE48";
                    } else {
                        placeMentId = "A7ABF4CFA257C79F064A8A162266D924";
                    }
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "";
                }

                // 百度
                else if (provider_id.equals("5")) {
                    if (displayType == 3) {
                        placeMentId = "5847855";
                    } else if (displayType == 4) {
                        placeMentId = "5847852";
                    } else {
                        placeMentId = "5847852";
                    }
                }
            }
        }

        // 同站线路
        else if (showType.getValue() == ShowType.CAR_ALL_LINE_ADV.getValue()) {
            if (phoneType.equalsIgnoreCase("ios")) {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "9000935584337663";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "1529526629093";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    if (displayType == 3) {
                        placeMentId = "901451799";
                    } else {
                        placeMentId = "901451947";
                    }
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "6B109FC78051FB15D006975B2200F36B";
                    } else {
                        placeMentId = "45E5548B890B23A05132960D1DD9E2A0";
                    }
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "cefe7e180a76c3d4c32b9fdb4f4e083b";
                }

                // 百度
                else if (provider_id.equals("5")) {
                    if (displayType == 3) {
                        placeMentId = "5846484";
                    } else if (displayType == 4) {
                        placeMentId = "5846497";
                    } else {
                        placeMentId = "5846492";
                    }
                }

            } else {
                //	广点通
                if (provider_id.equals("2")) {
                    if (displayType == 3) {
                        placeMentId = "2080634575306901";
                    } else {
                        placeMentId = "5030836525008930";
                    }
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    if (displayType == 3) {
                        placeMentId = "900673492";
                    } else {
                        placeMentId = "900673424";
                    }
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "67842F384D42AE16958A2ADDC8080BA0";
                    } else {
                        placeMentId = "3D7818F4980FB323DAEA8B544B567B7C";
                    }
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "";
                }

                // 百度
                else if (provider_id.equals("5")) {
                    if (displayType == 3) {
                        placeMentId = "5847859";
                    } else if (displayType == 4) {
                        placeMentId = "5847856";
                    } else {
                        placeMentId = "5847856";
                    }
                }
            }
        }
        // 更多车辆
        else if (showType.getValue() == ShowType.ALL_CAR_ADV.getValue()) {
            if (phoneType.equalsIgnoreCase("ios")) {
                //	广点通
                if (provider_id.equals("2")) {
                    placeMentId = "8070332514332685";
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "1529496478073";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    placeMentId = "";
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "9B3FF3E3654C83C5063DB8A55A857304";
                    } else {
                        placeMentId = "759CA12FB9C31CB04382C9ADDC208A2E";
                    }
                }
                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "64c668c5c769d2235209cbd9f6208b33";
                }
                // 百度
                else if (provider_id.equals("5")) {
                    if (displayType == 3) {
                        placeMentId = "5846494";
                    } else if (displayType == 4) {
                        placeMentId = "5846495";
                    } else {
                        placeMentId = "5846493";
                    }
                }
            } else {
                //	广点通
                if (provider_id.equals("2")) {
                    if (displayType == 3) {
                        placeMentId = "4070030575903993";
                    } else {
                        placeMentId = "7000631515604932";
                    }
                }
                // innobe
                else if (provider_id.equals("3")) {
                    placeMentId = "";
                }
                // 今日头条
                else if (provider_id.equals("7")) {
                    if (displayType == 3) {
                        placeMentId = "901451822";
                    } else {
                        placeMentId = "901451388";
                    }
                }
                // 科大讯飞
                else if (provider_id.equals("10")) {
                    if (displayType == 3) {
                        placeMentId = "9B3FF3E3654C83C5063DB8A55A857304";
                    } else if (displayType == 4) {
                        placeMentId = "160204CA31459D67D525260102F3017D";
                    } else {
                        placeMentId = "759CA12FB9C31CB04382C9ADDC208A2E";
                    }
                }

                // 网易
                else if (provider_id.equals("11")) {
                    placeMentId = "";
                }
                // 百度
                else if (provider_id.equals("5")) {
                    if (displayType == 3) {
                        placeMentId = "5847867";
                    } else if (displayType == 4) {
                        placeMentId = "5847862";
                    } else {
                        placeMentId = "5847862";
                    }
                }
            }
        }

        if (provider_id.equals("3") && (placeMentId == null || placeMentId.equals(""))) {
            placeMentId = "1526909972727";
        }

        return placeMentId;
    }

}
