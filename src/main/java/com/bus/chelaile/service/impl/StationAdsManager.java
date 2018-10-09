package com.bus.chelaile.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
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
import com.bus.chelaile.model.ads.AdButtonInfo;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdStationlInnerContent;
import com.bus.chelaile.model.ads.BannerInfo;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.StationAdEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AbstractManager;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.service.model.Ads;
import com.bus.chelaile.service.model.FeedAdGoto;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.third.IfengAx.IfenAxService;
import com.bus.chelaile.third.IfengAx.model.response.Ad;
import com.bus.chelaile.third.meituan.MeiTuanService;
import com.bus.chelaile.third.meituan.MeituanData;
import com.bus.chelaile.thread.StaticTimeLog;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;

public class StationAdsManager extends AbstractManager {

    @Autowired
    IfenAxService ifenAxService;
    
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
            // 第三方特殊处理
            if (stationInner.getProvider_id() > 0) {
                // 加上版本控制，and， 目前只支持广点通
                if (stationInner.getProvider_id() == ProductType.GUANGDIANTONG.getProvider_id()
                        && ((advParam.getS().equalsIgnoreCase("android")
                                && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0505)
                                || (advParam.getS().equalsIgnoreCase("ios")
                                        && advParam.getVc() >= Constants.PLATFOMR_LOG_IOS_0514))) {
                    res = createSDKOpenAds(stationInner, ad);
                    return res;
                } else if ((advParam.getS().equalsIgnoreCase("android")
                        && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0528)
                        || (advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= Constants.PLATFOMR_LOG_IOS_0528)) {
                    res = createSDKOpenAds(stationInner, ad);
                    return res;
                } else {
                    logger.error("不合适的版本，或者不合适的第三方广告类型，udid={}", advParam.getUdid());
                    return null;
                }
            }
            
            // 跳转feed流的targetType处理。 从永春接口获取内容填充
            if (ad.getTargetType() == 12) {
                if ((advParam.getS().equalsIgnoreCase("android") && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0528)
                        || (advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= Constants.PLATFOMR_LOG_IOS_0528)) {
                    res = createFeedEntity(advParam, ad, stationInner);
                    if(res == null) {
                        
                    }
                } else {
                    logger.error("低版本投放了跳转信息流的广告， adId={}, s={}, v={}, vc={}", ad.getId(), advParam.getS(), advParam.getV(),
                            advParam.getVc());
                    return null;
                }
                return res;
            }
            

            res.setTitle(ad.getTitle());
            res.setAdWeight(stationInner.getAdWeight());
            res.setBuyOut(stationInner.getBuyOut());
            res.setWxMiniProId(stationInner.getWx_miniPro_id());
            res.setWxMiniProPath(stationInner.getWx_miniPro_path());
            res.setBrandPic(stationInner.getBrandPic());
            
            if (stationInner.getTasksGroup() != null) {
                res.setTasksGroup(stationInner.getTasksGroup());
            }

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
        
        
        AdStationlInnerContent stationInner = (AdStationlInnerContent) inner;
//        logger.info("adProducer={}",stationInner.getAdProducer());
        if( stationInner.getAdProducer() != null && stationInner.getAdProducer().equals("meituan") ) {
        	MeituanData data = MeiTuanService.getContext(advParam);
        	if( data != null ) {
        		res.setAdProducer(null);
        		res.setOpenType(0);
        		res.setLink(data.getDeepLink());
        		res.setTitle(data.getName());
        		res.setTargetType(4);
        		res.getBannerInfo().setSlogan("");
        		res.getBannerInfo().setName("");
        		res.getBannerInfo().setSlogan(data.getName());
        		res.setH5Url( data.getH5Url() );
        		
        	}else {
        		logger.info("获取美团数据为空");
        		return null;
        	}
        }
        
        // 凤凰网走服务端api
        if (stationInner.getAdProducer() != null && stationInner.getAdProducer().equals("IfengAx")) {
            createIfengAxEntity(res, advParam);
        }
        
        return res;
    }


    // app低版本判断
    private boolean isLowPlatfomr(AdvParam advParam) {
        Platform platform = Platform.from(advParam.getS());
        return ((platform.isAndriod(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_ANDROID_0118)
                || (platform.isIOS(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_IOS_0117));
    }

    public void writeSendLog(AdvParam advParam, StationAdEntity entity) {
        AnalysisLog.info(
                "[STATION_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},h5User={},h5Src={},provider_id={}",
                entity.buildIdentity(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(),
                advParam.getS(), advParam.getV(), advParam.getLineId(), advParam.getStnName(), advParam.getNw(), advParam.getIp(),
                advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getH5User(), advParam.getH5Src(),
                entity.getProvider_id());
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
    private StationAdEntity createSDKOpenAds(AdStationlInnerContent inner, AdContent ad) {
        StationAdEntity entity = new StationAdEntity();
        entity.setId(ad.getId());
        entity.setProvider_id(inner.getProvider_id() + "");
        entity.setOpenType(0); // 页面打开方式，0-内部
        entity.setType(3); // 第三方广告
        entity.setTitle(ad.getTitle());
        entity.setBuyOut(inner.getBuyOut());
        entity.setAdWeight(inner.getAdWeight());
        entity.setClickDown(inner.getClickDown());
        
        entity.setAutoInterval(inner.getAutoInterval());
        entity.setMixInterval(inner.getMixInterval());

        BannerInfo bannerInfo = new BannerInfo();
        bannerInfo.setBannerType(4); // 广点通专用样式，文字+标签（文案由客户端自定义）
        AdButtonInfo buttonInfo = new AdButtonInfo();
        buttonInfo.setButtonPic("https://image3.chelaile.net.cn/babb63e1f76244749298ffe47d176b45");
        bannerInfo.setButton(buttonInfo);

        entity.setPic("https://image3.chelaile.net.cn/13c5f05173c7413ba73a492fcd6c3dcb");
        entity.setBannerInfo(bannerInfo);
        
        // 任务列表
        // 2018-06-06
        if(inner.getTasksGroup() != null) {
            entity.setTasksGroup(inner.getTasksGroup());
        }
      
        
        return entity;
    }

    
    private StationAdEntity createFeedEntity(AdvParam p, AdContent ad, AdStationlInnerContent inner) {
        String response = null;
        String url = String.format(AD_GOTO_INFO_URL, p.getUdid(), p.getStatsAct(), p.getS(), p.getVc(), ShowType.STATION_ADV.getType());
        url += "&advId=" + ad.getId();
        logger.info("请求信息流**********： url={}", url);
        StationAdEntity entity = null;
        try {
            response = HttpUtils.get(url, "UTF-8");
            response = response.substring(6, response.length() - 6);
            FeedAdGoto feedAdGoto = JSON.parseObject(response, FeedAdGoto.class);
            if(feedAdGoto.getJsonr().getStatus().equals("00")) {
                List<Ads> ads = feedAdGoto.getJsonr().getData().getAds();
                if(ads != null && ads.size() > 0) {
                    String slogan = ads.get(0).getTitle();
                    String action = ads.get(0).getAction();
                    entity = new StationAdEntity();
                    
                    entity.setAction(action);
                    entity.setId(ad.getId());
                    entity.setTitle(ad.getTitle());
                    entity.setBuyOut(inner.getBuyOut());
                    entity.setAdWeight(inner.getAdWeight());
                    entity.setClickDown(inner.getClickDown());
                    entity.setAutoInterval(inner.getAutoInterval());
                    entity.setMixInterval(inner.getMixInterval());

                    BannerInfo bannerInfo = new BannerInfo();
                    bannerInfo.setSlogan(slogan);
                    bannerInfo.setBannerType(4); // 专用样式，文字+标签（文案由客户端自定义）
                    AdButtonInfo buttonInfo = new AdButtonInfo();
                    buttonInfo.setButtonPic("https://image3.chelaile.net.cn/babb63e1f76244749298ffe47d176b45");
                    bannerInfo.setButton(buttonInfo);

                    entity.setPic("https://image3.chelaile.net.cn/13c5f05173c7413ba73a492fcd6c3dcb");
                    entity.setBannerInfo(bannerInfo);
                    entity.setTargetType(ad.getTargetType());
                    
                    if(inner.getTasksGroup() != null) {
                        entity.setTasksGroup(inner.getTasksGroup());
                    }
                } else {
                    logger.error("信息流接口返回为空, url={}, response={} ", url, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取跳转feed流广告内容失败， url={}, response={}", url, response);
            return null;
        }
        return entity;
    }
    
    
    public static void main(String[] args) {
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

    @Override
    protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
    	StaticTimeLog.record(Constants.RECORD_LOG, "for_one" );
        List<BaseAdEntity> entities = New.arrayList();
        List<Integer> ids = New.arrayList();
        boolean hasOwnAd = false;
        for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
            AdContentCacheEle ad = entry.getValue();

            // 有非兜底的自采买广告。 直接返回第一个优先级最高的即可
            // 站点广告因为之前涉及到轮播和埋点的方案，所以这里有待商量 
            // TODO 
            AdStationlInnerContent inner = (AdStationlInnerContent) ad.getAds().getAdInnerContent();
            if (inner.getProvider_id() <= 1 && inner.getBackup() == 0) { // 非自采买的provider_id都大于1
                StationAdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType);
                if (entity != null) {
                    entities.add(entity);
                    int adId = ad.getAds().getId();
                    ids.add(adId);

                    hasOwnAd = true;
                    break;
                }
            }
        }
        logger.info("hasOwnA={}", hasOwnAd);
        StaticTimeLog.record(Constants.RECORD_LOG,"for_two" );
        // 如果没有自采买，那么返回一个列表
        if (!hasOwnAd) {
            AdContentCacheEle backupad = null;
            for (Map.Entry<Integer, AdContentCacheEle> entry : adMap.entrySet()) {
                AdContentCacheEle ad = entry.getValue();
                // 兜底的拿出来
                if(((AdStationlInnerContent)ad.getAds().getInnerContent()).getBackup() == 1) {
                    backupad = ad;
                    continue;
                }
                // 自采买的去掉
                if(((AdStationlInnerContent)ad.getAds().getInnerContent()).getProvider_id() <= 1 
                        && ((AdStationlInnerContent)ad.getAds().getInnerContent()).getBackup() == 0) {
                    continue;
                }
                StationAdEntity entity = from(advParam, cacheRecord, ad.getAds(), showType);
                if (entity != null) {
                    entities.add(entity);
                    // 如果是js版本的记录，那么不需要多条记录
                    // 原因： js每条广告记录可以包含多家sdk，并且可调顺序，估无需再获取多条进行整合
                    if(entity.getProvider_id().equals("100")) {
                        break;
                    }
                }
            }
            // 重新排序
            // 如果半小时内有上次的投放记录，那么根据上次返回到的位置，轮训下一个
            // 如果超过半小时，那么按照权重排序
//            if (!checkSendLog(advParam, entities, showType.getType()))    
                rankAds(advParam, entities);
            setClickAtLast(cacheRecord, entities);
            if(backupad != null) {
                StationAdEntity entity = from(advParam, cacheRecord, backupad.getAds(), showType);
                entities.add(entity);
            }
        }
        
        StaticTimeLog.record(Constants.RECORD_LOG,"setNoAdHistoryMap" );
        // 记录投放的第一条广告， 记录发送日志
        if (entities != null && entities.size() > 0) {
            // js请求返回自采买广告，不予计数（包括投放间隔和投放次数等）
//            if (!(queryParam.isJS() && entities.get(0).getProvider_id().equals("1"))) {
                cacheRecord.setNoAdHistoryMap(ids, showType.getType());
                recordSend(advParam, cacheRecord, adMap, showType, entities);
//            }
        }

        return entities;

    }
    
    private void createIfengAxEntity(StationAdEntity res, AdvParam p) {
        Ad ad = ifenAxService.getContext(p);
        if(ad == null || ad.getCreative() == null || ad.getCreative().getStatics() == null) {
            // 返回为空
            res = null;
            return;
        }
        res.buildIfendAxEntity(ad);

        res.setPic(res.getPicsList().get(0));
        res.setTitle(ad.getCreative().getStatics().getText());
        res.getBannerInfo().setSlogan("");
        res.getBannerInfo().setName("");
        res.getBannerInfo().setSlogan(ad.getCreative().getStatics().getDesc());
        res.setH5Url(res.getLink());

    }
}
