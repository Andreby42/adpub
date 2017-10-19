package com.bus.chelaile.service.impl;



import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bus.chelaile.alimama.AlimamaHelper;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.innob.AdvType;
import com.bus.chelaile.innob.ApiType;
import com.bus.chelaile.innob.InnobHelp;
import com.bus.chelaile.innob.ProductType;
import com.bus.chelaile.linkActive.LinkActiveHelp;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.entity.ApiLineEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.HashTextUtils;
import com.bus.chelaile.util.IdGenerateUtil;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public class ApiDetailsManager {
	protected static final Logger logger = LoggerFactory
			.getLogger(ApiDetailsManager.class);

	private static AlimamaHelper alimamaHelper = new AlimamaHelper();

	private final static String iosPlacementId = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "iosPlacementId");

	private final static String androidPlacementId = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "androidPlacementId"); 

	private final static String androidNativePlacementId = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "androidNativePlacementId"); 
	
	private final static String iosNativePlacementId =  PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "iosNativePlacementId");

	private final static String baiduIos = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "baiduIos");

	private final static String baiduAndroid = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "baiduAndroid"); 
			
	// 不需要带通用参数的url
	public final static String redirectUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "api.redirect.url");
	// 最大访问数
	private final static int api_MaxSize = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "apiMaxSize"));
			
	// 临时计数
	private static int api_TempNum = 0;
	
	@Autowired
	private LinkActiveHelp linkActiveHelp;

	/**
	 * 获取第三方广告
	 * @param platform
	 * @param advParam
	 * @param cacheRecord
	 * @param cateGory
	 * @param showType
	 * @return
	 * @throws Exception
	 */
	public ApiLineEntity from(Platform platform, AdvParam advParam,
			AdPubCacheRecord cacheRecord, AdCategory cateGory, String showType)
			throws Exception {
		// 不需要调用第三方广告直接返回
		if (cateGory.getAdType() != 3 && cateGory.getAdType() != 6) {
			return fromValue(platform, advParam, cacheRecord, cateGory,
					showType);
		}
		ApiLineEntity entity = null;
		try {
			api_TempNum++;
			if (api_TempNum >= api_MaxSize) {
				logger.info("udid={},Api请求数量超过最大值", advParam.getUdid());
				throw new IllegalArgumentException("api数量超过最大访问数量");
			}
			entity = fromValue(platform, advParam, cacheRecord, cateGory,
					showType);
			return entity;
		} finally {
			api_TempNum--;
			if (entity != null) {
				entity.setLink(dealLink(entity.getLink(),entity.getId(), advParam));
			}
		}

	}

	private static String dealLink(String needEncodeOrgLink, int advId, AdvParam advParam) {
		if (needEncodeOrgLink == null || needEncodeOrgLink.equals("")) {
			return "";
		}
		needEncodeOrgLink = needEncodeOrgLink.contains("?") ? needEncodeOrgLink
				: needEncodeOrgLink.replaceFirst("#!", "?#!");
		String encodedOrgUrl = AdvUtil.encodeUrl(needEncodeOrgLink);
		String url = redirectUrl;
		url = url.replace(AdvUtil.ORGIN_URL_TAG, encodedOrgUrl);
		url += "&advId="+advId+"&provider_id=3";
		
		HashMap<String, String> param = New.hashMap();
		param.put(Constants.PARAM_DEVICE, advParam.getDeviceType());
		param.put(Constants.PARAM_IP, advParam.getIp());
		param.put(Constants.PARAM_LNG, advParam.getLng() + "");
		param.put(Constants.PARAM_LAT, advParam.getLat() + "");
		param.put(Constants.PARAM_NW, advParam.getNw());
		
		if (param != null && param.size() > 0) {
            StringBuilder sb = new StringBuilder(url);
            Set<Entry<String, String>> entrySet = param.entrySet();
            for (Entry<String, String> entry : entrySet) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue();
                sb.append('&').append(paramName).append('=').append(paramValue);
            }
            url = sb.toString();
        }
		
		return url;
	}

	private ApiLineEntity fromValue(Platform platform, AdvParam advParam,
			AdPubCacheRecord cacheRecord, AdCategory cateGory, String showType)
			throws Exception {

		// 先看缓存
		if (cateGory.getAdType() == 3) {
			
			if((advParam.getIdfa() == null && advParam.getImei() == null)
					|| advParam.getIp() == null || advParam.getUa() == null) {
				logger.error("有参数为空, idfa={}, imei={}, ip={}, ua={}", 
						advParam.getIdfa(), advParam.getImei(), advParam.getIp(), advParam.getUa());
				return null;
			}

			if( advParam.getIdfa().equals("00000000-0000-0000-0000-000000000000") 
					|| advParam.getIp().equals("") 
					|| advParam.getUa().equals("")
					|| (advParam.getIdfa() == null && advParam.getImei() == null)){
				logger.error("参数值有误, udid={}, s={}, cityId={}, showType={}, idfa={}, imei={}, ip={}, ua={}",
						advParam.getUdid(), advParam.getS(), advParam.getCityId(), showType,
						advParam.getIdfa(), advParam.getImei(), advParam.getIp(), advParam.getUa());
				return null;
//				throw new IllegalArgumentException("idfa为空");
			}
			
			ApiLineEntity entity = InnobHelp.getApiLineEntityFromCache(
					cacheRecord, showType, cateGory.getApiType(),
					advParam.getUdid());
			if (entity != null) {
				entity.setMonitorType(3);
				entity.setId(getApiId());
		    	//entity.setShowType(Integer.parseInt(showType));
				return entity;
			}

		} else if (showType.equals(ShowType.LINE_DETAIL.getType())
				&& cateGory.getAdType() == 6) {
			ApiLineEntity entity = null;
			if (platform.isIOS(platform.getDisplay())) {
				entity = alimamaHelper.getIosInfo(advParam.getNw(),
						advParam.getVc(), advParam.getIp(),
						advParam.getDeviceType(), advParam.getS(),
						advParam.getSv(), advParam.getIdfa(),
						advParam.getUdid());
			} else if (platform.isAndriod(platform.getDisplay())) {
				entity = alimamaHelper.getAndroidInfo(advParam.getNw(),
						advParam.getVc(), advParam.getIp(),
						advParam.getDeviceType(), advParam.getS(),
						advParam.getSv(), advParam.getImei(),
						advParam.getUdid());
			}
			entity.setId(getApiId());
			entity.setShowType(ShowType.LINE_DETAIL.getValue());
			entity.setMonitorType(3);
			return entity;
		}

		logger.info("udid={},ip={},AdType={},ifa={},imei={}", advParam.getUdid(),
				advParam.getIp(), cateGory.getAdType(), advParam.getIdfa(), advParam.getImei());

		ApiLineEntity entity = null;

		String ip = advParam.getIp();

		// 缓存如果没有返回，再调api
		if (cateGory.getAdType() == 3) {

			if (platform.isIOS(platform.getDisplay())) {
				entity = InnobHelp.getLineDetailsIos(ip, advParam.getUa(),
						advParam.getUdid(), advParam.getIdfa(),
						cateGory.getApiType(), cacheRecord, showType);
			} else if (platform.isAndriod(platform.getDisplay())) {
				String o1 = "";
				if (advParam.getO1() != null) {
					o1 = advParam.getO1();
				} else {
					//取随机数，生成o1参数
					int number = (int)((Math.random()*9+1)*100000);
					o1 = HashTextUtils.sha1(String.valueOf(number));
				}
				
				entity = InnobHelp.getLineDetailsAndroid("", ip,
						advParam.getImei(), o1, advParam.getUa(),
						advParam.getUdid(), cateGory.getApiType(), cacheRecord,
						showType);
			}

			//entity.setProvider_id(ProductType.INMOBI.getValue());
			if(entity == null) {
				return entity;
			}
			entity.setMonitorType(3);
		}
		// LinkActive 
		else if(cateGory.getAdType() == 7) {
			entity = linkActiveHelp.getLineDetails(advParam, showType);
			return entity;
		}
		// 广点通
		else if (cateGory.getAdType() == 2) {
			entity = new ApiLineEntity();
			entity.setApiType(cateGory.getApiType());
			entity.setType(AdvType.GUANGDIANTONG.getValue());
			entity.setProvider_id(ProductType.GUANGDIANTONG.getValue());
			if (platform.isIOS(platform.getDisplay())) {
				if (cateGory.getApiType() == ApiType.NATIVE.getValue()) {
					entity.setPlacementId(iosNativePlacementId);
				} else {
					entity.setPlacementId(iosPlacementId);
				}

			} else if (platform.isAndriod(platform.getDisplay())) {
				if (cateGory.getApiType() == ApiType.NATIVE.getValue()) {
					entity.setPlacementId(androidNativePlacementId);
				} else {
					entity.setPlacementId(androidPlacementId);
				}

			}
			entity.setMonitorType(0);
		}
		// 百度
		else if (cateGory.getAdType() == 5) {
			entity = new ApiLineEntity();
			entity.setApiType(cateGory.getApiType());
			entity.setType(AdvType.BAIDU.getValue());
			entity.setProvider_id(ProductType.BAIDULIANMENG.getValue());
			if (platform.isIOS(platform.getDisplay())) {
				entity.setPlacementId(baiduIos);
			} else if (platform.isAndriod(platform.getDisplay())) {
				entity.setPlacementId(baiduAndroid);
			}
			entity.setMonitorType(0);
		}

		entity.setId(getApiId());
		entity.setShowType(ShowType.LINE_DETAIL.getValue());

		return entity;
	}

//	public static ApiLineEntity fromTest(Platform platform,
//			AdvParam advParam, AdPubCacheRecord cacheRecord) {
//
//		int apiType = 0;
//		int type = 3;
//		String lineId = advParam.getLineId();
//		if (advParam.getLineId().indexOf("0532-103-") != -1) {
//			apiType = 1;
//			type = 3;
//		} else if (advParam.getLineId().indexOf("0532-105-") != -1) {
//			apiType = 3;
//			type = 3;
//
//		} else if (advParam.getLineId().indexOf("0532-311-") != -1) {
//			apiType = 1;
//			type = 2;
//
//		} else if (advParam.getLineId().indexOf("0532-115-") != -1) {
//			apiType = 2;
//			type = 2;
//
//		}
//		// if (cacheRecord != null) {
//		// // 小于15分钟返回null
//		// if (cacheRecord.isUninterestApi(lineId, 5, type, apiType)) {
//		// long time = cacheRecord.getCloseAdsTimeApi(lineId, 5, type,
//		// apiType);
//		// if (time > 0) {
//		// time = System.currentTimeMillis() - time;
//		// if (time < 15 * 60 * 1000) {
//		// return null;
//		// }
//		// }
//		// }
//		// }
//
//		return fromTest(platform, advParam, cacheRecord);
//		// ApiLineEntity entity = new ApiLineEntity();
//		// entity.setType(3);
//		// entity.setApiType(1);
//		// entity.setApiDes("说明");
//		// entity.setProvider_id("innob");
//		//
//		// entity.setApiTitle("标题");
//		// //entity.fillBaseInfo(ad, platform, advParam);
//		// entity.setCombpic("http://pic1.chelaile.net.cn/adv/ios5ca921d7-f25d-4cb1-9ff0-0a7310428b2b.png");
//		// entity.setId(1);
//		// entity.setLink("http://redirect.chelaile.net.cn/?link=http%3A%2F%2Fm.ppdai.com%2Flandingcpsnew.html%3Fregsourceid%3Dchelailefeng&advId=2254&adtype=05");
//		//
//		//
//		// return entity;
//	}

	// private static ApiLineEntity fromTest1(Platform platform,
	// BusAdvAction.AdvParam advParam, AdPubCacheRecord cacheRecord) throws
	// Exception {
	//
	// if (platform.isH5(platform.getDisplay())) {
	// return null;
	// }
	//
	// logger.info("udid={},ip={},ua={},ifa={}", advParam.getUdid(),
	// advParam.getIp(), advParam.getUa(), advParam.getIdfa());
	//
	// ApiLineEntity entity = null;
	//
	// String ip = advParam.getIp();
	//
	// // if( advParam.getRemote_addr() != null &&
	// // !advParam.getRemote_addr().equals("") &&
	// // advParam.getX_forwarded_for() != null &&
	// // !advParam.getX_forwarded_for().equals("") ){
	// // ip = advParam.getX_forwarded_for();
	// // // 多个代理的直接返回
	// // if( ip.length() > 1 && ip.split(",").length > 1 ){
	// //
	// logger.info("udid={},ip={}",advParam.getUdid(),advParam.getX_forwarded_for());
	// // return null;
	// // }
	// // }else if( advParam.getRemote_addr() != null ){
	// // ip = advParam.getRemote_addr();
	// // }
	//
	// logger.info("udid={},display={}", advParam.getUdid(),
	// platform.getDisplay());
	// if (advParam.getLineId().indexOf("0532-103-") != -1) {
	//
	// if (platform.isIOS(platform.getDisplay())) {
	// entity = InnobHelp.getLineDetailsIos(ip, advParam.getUa(),
	// advParam.getUa(), advParam.getIdfa(), 1);
	// } else if (platform.isAndriod(platform.getDisplay())) {
	// String gpid = "";
	// String o1 = "";
	// if (advParam.getO1() != null) {
	// o1 = advParam.getO1();
	// }
	// entity = InnobHelp.getLineDetailsAndroid(gpid, ip,
	// advParam.getImei(), o1, advParam.getUa(),
	// advParam.getUdid(), 1);
	// }
	//
	// } else if (advParam.getLineId().equals("0532-101-0")) {
	// if (platform.isIOS(platform.getDisplay())) {
	// entity = InnobHelp.getLineDetailsIos(ip, advParam.getUa(),
	// advParam.getUa(), advParam.getIdfa(), 2);
	// } else if (platform.isAndriod(platform.getDisplay())) {
	// String gpid = "";
	// String o1 = "";
	// if (advParam.getO1() != null) {
	// o1 = advParam.getO1();
	// }
	// entity = InnobHelp.getLineDetailsAndroid(gpid, ip,
	// advParam.getImei(), o1, advParam.getUa(),
	// advParam.getUdid(), 2);
	// }
	//
	// } else if (advParam.getLineId().indexOf("0532-311-") != -1) {
	// entity = new ApiLineEntity();
	// entity.setApiType(1);
	// entity.setType(2);
	// entity.setProvider_id(ProductType.GUANGDIANTONG.getValue());
	//
	// } else if (advParam.getLineId().indexOf("0532-115-") != -1) {
	// entity = new ApiLineEntity();
	// entity.setApiType(2);
	// entity.setType(2);
	// if (platform.isIOS(platform.getDisplay())) {
	// entity.setPlacementId("7070813368593247");
	// } else if (platform.isAndriod(platform.getDisplay())) {
	// entity.setPlacementId("4070514430006239");
	// }
	// entity.setProvider_id(ProductType.GUANGDIANTONG.getValue());
	//
	// } else if (advParam.getLineId().indexOf("0532-105-") != -1) {
	// entity = new ApiLineEntity();
	// entity.setType(3);
	// entity.setProvider_id(ProductType.ANWO.getValue());
	// entity.setApiType(3);
	// entity.setClickMonitorLink("http://test.chelaile.net.cn:7000/adpub/adv!getActiveAds.action?userId=4904316&geo_type=wgs&secret=c378faec6af34bb08312b2e98282021b&geo_lat=39.997515&geo_lng=116.403729&sv=9.1&s=ios&lng=116.403729&v=5.17.0&udid=fdc92388ec4d89555ab8d0b7924d61c560915734&advId=-1&nw=WiFi&mac=&cityState=0&cityId=006&sign=0vMBB4FOt2p26M5naEw08A==&lat=39.997515&gpstype=wgs&vc=10800&accountId=4904101&idfa=06BC2C48-E0BA-4E40-981A-093EBE83143B&lineId=022-10-0&stnName=%E5%8D%8E%E6%B6%A6%E4%B8%87%E5%AE%B6%E5%8C%97%E8%BE%B0%E5%BA%97");
	// entity.setUnfoldMonitorLink("http://test.chelaile.net.cn:7000/adpub/adv!getActiveAds.action?userId=4904316&geo_type=wgs&secret=c378faec6af34bb08312b2e98282021b&geo_lat=39.997515&geo_lng=116.403729&sv=9.1&s=ios&lng=116.403729&v=5.17.0&udid=fdc92388ec4d89555ab8d0b7924d61c560915734&advId=-1&nw=WiFi&mac=&cityState=0&cityId=006&sign=0vMBB4FOt2p26M5naEw08A==&lat=39.997515&gpstype=wgs&vc=10800&accountId=4904101&idfa=06BC2C48-E0BA-4E40-981A-093EBE83143B&lineId=022-10-0&stnName=%E5%8D%8E%E6%B6%A6%E4%B8%87%E5%AE%B6%E5%8C%97%E8%BE%B0%E5%BA%97");
	// //if( advParam.getS().equalsIgnoreCase("ios") ){
	// entity.setCombpic("http://pic1.chelaile.net.cn/adv/ios5dd74d8e-cc9d-418b-8983-de2a999b7770.jpg");
	// //}
	//
	// entity.setLink("http://e.cn.miaozhen.com/r/k=2026594&p=71gFv&dx=0&rt=2&ns=__IP__&ni=__IESID__&v=__LOC__&xa=__ADPLATFORM__&mo=__OS__&m0=__OPENUDID__&m0a=__DUID__&m1=__ANDROIDID1__&m1a=__ANDROIDID__&m2=__IMEI__&m4=__AAID__&m5=__IDFA__&m6=__MAC1__&m6a=__MAC__&vo=3f0c44fa9&vr=2&o=http%3A%2F%2Fylzt.qq.com%2Fm%2Fmedia_index.shtml%3Fmedia%3D10022581&wtb=1");
	// entity.setMonitorType(3);
	// }
	// //entity.setId(getApiId());
	// entity.setShowType(5);
	// entity.setMonitorType(3);
	// return entity;
	// }

	

	private synchronized int getApiId() {
		// apiId++;
		return IdGenerateUtil.generateId();
	}

}
