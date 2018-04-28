package com.bus.chelaile.service.impl;

import java.util.List;
import java.util.Map;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.ads.AdDoubleInnerContent;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdStationlInnerContent;
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

    //	// 0 双栏 1 单栏
    //	private ShowType type;
    //	//	这个值要好好测试一下
    //	private Station station;
    //
    //	public DoubleAndSingleManager(int type,Station station) {
    //		if (0 == type) {
    //			this.type = ShowType.DOUBLE_COLUMN;
    //		} else if (1 == type) {
    //			this.type = ShowType.SINGLE_COLUMN;
    //		}
    //		this.station = station;
    //	}

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
                    if (adInner.getProvider_id() != 2) {
                        // 双栏目前只支持广点通
                        throw new IllegalArgumentException(
                                "错误的双栏provider_id, advId=" + ad.getAds().getId() + ", provider_id=" + adInner.getProvider_id());
                    }
                    if ((advParam.getS().equalsIgnoreCase("android") && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0420)
                            || (advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= Constants.PLATFOMR_LOG_IOS_0502)) {
                        entity.setProvider_id(adInner.getProvider_id() + ""); // 新增第三方广告
                    } else {
                        logger.info("低版本不予返回非自采买的双栏广告 ");
                        return null;
                    }
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
                    "[LINE_LEVEL_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, storder={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},distance={}",
                    ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
                    advParam.getCityId(), advParam.getS(), advParam.getV(), entity.getSindex(), advParam.getNw(),
                    advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getDistance());
        } else if (showType == ShowType.DOUBLE_COLUMN) {
            AnalysisLog.info(
                    "[STATION_LEVEL_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, storder={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},distance={}",
                    ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
                    advParam.getCityId(), advParam.getS(), advParam.getV(), entity.getSindex(), advParam.getNw(),
                    advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getDistance());
        } else {
            AnalysisLog.info(
                    "[ROUTE_LEVEL_ADS]: adKey={}, userId={}, accountId={}, udid={}, cityId={}, s={}, v={}, storder={},nw={},ip={},deviceType={},geo_lng={},geo_lat={},distance={}",
                    ad.getAds().getLogKey(), advParam.getUserId(), advParam.getAccountId(), advParam.getUdid(),
                    advParam.getCityId(), advParam.getS(), advParam.getV(), entity.getSindex(), advParam.getNw(),
                    advParam.getIp(), advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getDistance());
        }

        return entity;

    }

    @Override
    protected List<BaseAdEntity> dealEntities(AdvParam advParam, AdPubCacheRecord cacheRecord,
            Map<Integer, AdContentCacheEle> adMap, ShowType showType, QueryParam queryParam) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    //	@Override
    //	public List<AdContentCacheEle> getAllAdsList(String udid, String accountId) {
    //		return getCommonsAdsList(udid, accountId, type);
    //	}

    //	@Override
    //	public void handleAds(Map<Integer, AdContentCacheEle> adMap,
    //			List<AdContentCacheEle> adsList, ShowType showType,
    //			AdvParam advParam, AdPubCacheRecord cacheRecord, boolean isNeedApid) {
    //		setAds(adMap, adsList, showType, advParam, cacheRecord, -1, isNeedApid);
    //
    //	}

}
