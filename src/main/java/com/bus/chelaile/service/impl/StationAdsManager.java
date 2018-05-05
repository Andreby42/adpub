package com.bus.chelaile.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;

import scala.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.koubei.CouponInfo;
import com.bus.chelaile.koubei.KBUtil;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.ProductType;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdStationlInnerContent;
import com.bus.chelaile.model.ads.BannerInfo;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.OpenAdEntity;
import com.bus.chelaile.model.ads.entity.StationAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.New;

public class StationAdsManager extends AbstractManager {

    static Set<String> TBK_TITLE_KEY = New.hashSet();

    @Override
    protected BaseAdEntity dealEntity(AdCategory cateGory, AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam, boolean isRecord) throws Exception {
        List<BaseAdEntity> entities = New.arrayList();
        for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
            AdContentCacheEle ad = entry.getValue();
            //            logger.info("********************id={}, priority={}", ad.getAds().getId(), ad.getAds().getPriority());
            StationAdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType);
            if (entity != null) {
                entities.add(entity);
            }
        }
        //        logger.info("适配站点广告数:{}", entities.size());
        StationAdEntity entity = null;
        if (entities.size() == 0) {
            return null;
        } else if (entities.size() == 1) {
            entity = (StationAdEntity) entities.get(0);
        } else {
            Collections.sort(entities, ENTITY_COMPARATOR);
            entity = (StationAdEntity) calAdWeightAndByOut(advParam, entities);
        }

        if (entity != null)
            writeSendLog(advParam, entity);

        return entity;
    }

    private StationAdEntity from(AdvParam advParam, AdPubCacheRecord cacheRecord, AdContent ad, ShowType showType) {
        StationAdEntity res = new StationAdEntity();
        AdInnerContent inner = ad.getInnerContent();
        if (inner instanceof AdStationlInnerContent) {
            AdStationlInnerContent stationInner = (AdStationlInnerContent) inner;
            logger.info("*************** stationInner={}", JSONObject.toJSONString(stationInner));
            // 第三方特殊处理
            if (stationInner.getProvider_id() > 0) {
                // 加上版本控制，and， 目前只支持广点通
                if (stationInner.getProvider_id() == ProductType.INMOBI.getProvider_id()
                        && advParam.getS().equalsIgnoreCase("android")
                        && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0505) {
                    res = createSDKOpenAds(stationInner.getProvider_id(), ad);
//                    AnalysisLog.info(
//                            "[STATION_ADS]: adKey=ADV[id={}#showType={}#title={}], userId={}, accountId={}, udid={}, cityId={}, s={}, v={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},provider_id={}",
//                            res.getId(), showType.getType(), ad.getTitle(), advParam.getUserId(),
//                            advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(), advParam.getV(),
//                            advParam.getNw(), advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(),
//                            res.getProvider_id());
                    return res;
                } else
                    return null;
            }

            res.setTitle(ad.getTitle());
            res.setAdWeight(stationInner.getAdWeight());
            res.setBuyOut(stationInner.getBuyOut());
            res.setWxMiniProId(stationInner.getWx_miniPro_id());
            res.setWxMiniProPath(stationInner.getWx_miniPro_path());

            // 对空串情况做一下处理
            if (stationInner.getBannerInfo() != null
            // &&
            // StringUtils.isNoneBlank(stationInner.getBannerInfo().getName())
            ) {
                res.setBannerInfo(stationInner.getBannerInfo());
            }
            // banner可以没有名字（对应双图片结构）,但是card是不可能没有商户名的！！！
            if (stationInner.getAdCard() != null && StringUtils.isNoneBlank(stationInner.getAdCard().getName())) {
                res.setAdCard(stationInner.getAdCard());
            }
            res.setPic(stationInner.getPic());
            res.setPriority(ad.getPriority());

            if (isLowPlatfomr(advParam)) {
                // 低版本，不支持跳转h5。 不支持新增加的两种样式
                if (res.getTargetType() == 0 || stationInner.getBannerInfo().getBannerType() == 5
                        || stationInner.getBannerInfo().getBannerType() == 6) {
                    return null;
                }
                // 版本控制，老版本，targetOrder都设置为0。否则无法打开card
                res.setTargetType(0);
            }

            // 针对口碑券和淘宝客的修改
            // 口碑券，需要从ocs中获取当前站点的券
            if (stationInner.getBannerInfo().getBannerType() == 5) {
                // 用clone，保持 静态缓存 不受修改的影响
                BannerInfo bann = (BannerInfo) stationInner.getBannerInfo().clone();
                CouponInfo ocsCoupon = null;
                String key = KBUtil.getKbCouponOcsKey(advParam.getCityId(), advParam.getStnName());
                String ocsValue = (String) CacheUtil.get(key);
                if (StringUtils.isNotBlank(ocsValue)) {
                    ocsCoupon = JSONObject.parseObject(ocsValue, CouponInfo.class);
                }
                if (null != ocsCoupon && StringUtils.isNoneBlank(ocsCoupon.getShopName())) {
                    bann.setSlogan("送你一张优惠券：" + ocsCoupon.getShopName());
                } else {
                    logger.error("获取不到站点缓存的优惠券信息, stopName={}, key={}", advParam.getStnName(), key);
                    return null; // 返回空
                }
                res.setBannerInfo(bann);
            }
            // 淘宝客，只有slogan，没有name
            // 客户端只支持bannerType到5，所以这个地方需要修改成5
            else if (stationInner.getBannerInfo().getBannerType() == 6) {
                // 用clone，保持 静态缓存 不受修改的影响
                BannerInfo bann = (BannerInfo) stationInner.getBannerInfo().clone();

                bann.setBannerType(5);
                res.setBannerInfo(bann);
            }
            // 针对指定的几个淘宝客广告，替换slogan, 不用忧虑修改了innerContent的内容，无妨
            if (StaticAds.advTBKTitleKey.containsKey(res.getId())) {
                res.getBannerInfo().setSlogan(CacheUtil.getTBKTitle(res.getId()));
            }
        } else {
            throw new IllegalArgumentException("=====> 错误的innerContent类型： " + ((inner == null) ? null : inner.getClass()) + "; "
                    + inner + ",udid=" + advParam.getUdid());
        }

        res.fillBaseInfo(ad, advParam, new HashMap<String, String>());
        res.dealLink(advParam);

        return res;
    }

    // app低版本判断
    private boolean isLowPlatfomr(AdvParam advParam) {
        Platform platform = Platform.from(advParam.getS());
        return ((platform.isAndriod(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_ANDROID_0118)
                || (platform.isIOS(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_IOS_0117));
    }

    @Override
    protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
        return null;
    }

    public void writeSendLog(AdvParam advParam, StationAdEntity entity) {
        AnalysisLog.info(
                "[STATION_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},h5User={},h5Src={}",
                entity.buildIdentity(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(),
                advParam.getS(), advParam.getV(), advParam.getLineId(), advParam.getStnName(), advParam.getNw(), advParam.getIp(),
                advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getH5User(), advParam.getH5Src());
    }

    // 按照权重计算，选择一个广告投放
    private BaseAdEntity calAdWeightAndByOut(AdvParam advParam, List<BaseAdEntity> stanAdsList) {
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

    private static final Comparator<BaseAdEntity> ENTITY_COMPARATOR = new Comparator<BaseAdEntity>() {
        @Override
        public int compare(BaseAdEntity o1, BaseAdEntity o2) {
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            return o2.getPriority() - o1.getPriority();
        }

    };

    // 2018-05-05 ，站点广告，支持原生gdt
    private StationAdEntity createSDKOpenAds(int adType, AdContent ad) {
        StationAdEntity entity = new StationAdEntity();
        entity.setId(ad.getId());
        entity.setProvider_id(adType + "");
        entity.setOpenType(0); // 页面打开方式，0-内部
        entity.setType(3); // 第三方广告
        return entity;
    }

    public static void main(String[] args) {
        // String url =
        // "http%3A%2F%2F121.40.95.166%3A7000%2Foutman%2Fadv%2FqueryAdv%3Fid%3D12024";
        // System.out.println(HttpUtils.get(url, "utf-8"));
        // int totalWeight = 0;
        // int randomOut = new Random().nextInt(totalWeight);
        // System.out.println(randomOut);

        StationAdEntity a = new StationAdEntity();
        StationAdEntity b = new StationAdEntity();

        a.setPriority(1);
        b.setPriority(2);

        List<StationAdEntity> enties = New.arrayList();
        enties.add(b);
        enties.add(a);
        Collections.sort(enties, ENTITY_COMPARATOR);

        for (StationAdEntity stn : enties) {
            System.out.println(stn.getPriority());
        }
    }
}
