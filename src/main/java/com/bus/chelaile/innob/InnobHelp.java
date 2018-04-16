package com.bus.chelaile.innob;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.common.HttpInfoLog;
import com.bus.chelaile.common.TimeLong;
import com.bus.chelaile.innob.net.RequestResponseManager;
import com.bus.chelaile.innob.request.AndroidDevice;
import com.bus.chelaile.innob.request.App;
import com.bus.chelaile.innob.request.BannerImp;
import com.bus.chelaile.innob.request.IOSDevice;
import com.bus.chelaile.innob.request.NativeImp;
import com.bus.chelaile.innob.request.Request;
import com.bus.chelaile.innob.response.ad.BannerResponse;
import com.bus.chelaile.innob.response.ad.NativeResponse;
import com.bus.chelaile.model.ProductType;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ads.entity.ApiLineEntity;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.model.record.ApiRecord;
import com.bus.chelaile.util.OSSUtil;
import com.bus.chelaile.util.UrlUtil;
import com.bus.chelaile.util.config.PropertiesUtils;

/**
 * Created by Administrator on 2016/8/9.
 */

public class InnobHelp {

	private static App appIosNativeAds = new App(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"adv.third.party.inmobi.ios.native.placeid"), "com.chelaile.lite");
	private static App appIosBannerAds = new App(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"adv.third.party.inmobi.ios.banner.placeid"), "com.chelaile.lite");

	private static App appAndroidNativeAds = new App(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"adv.third.party.inmobi.android.native.placeid"), "com.chelaile.lite");
	private static App appAndroidBannerAds = new App(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"adv.third.party.inmobi.android.banner.placeid"), "com.chelaile.lite");

	private static int innmobiLinedtailsNum = Integer.parseInt(PropertiesUtils.getValue(
			PropertiesName.PUBLIC.getValue(), "innmobiLinedtailsNum", "5"));

	private static final String folder = "adv/open/";

	/**
	 * android开屏
	 */
	private static App appAndroidOpenNativeAds = new App(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"adv.third.party.inmobi.android.nativeOpen.placeid"), "com.chelaile.lite");

	/**
	 * ios开屏
	 */
	private static App appIosOpenNativeAds = new App(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"adv.third.party.inmobi.ios.nativeOpen.placeid"), "com.chelaile.lite");

	private static String innobeOpenTemplate = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			("innobeTemplate"));
	private static String innobeOpenTemplate1 = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			("innobeTemplate1"));

	private static String innobeOpenPicNewPath = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			("innobeOpenPicNewPath"));

	protected static final Logger logger = LoggerFactory.getLogger(InnobHelp.class);

	public static ApiLineEntity getLineDetailsAndroid(String gpid, String ip, String iem, String o1, String ua,
			String udid, int apiType, AdPubCacheRecord cacheRecord, String showType) throws Exception {

		// logger.info("udid={},gpid={},ip={},iem={},o1={},ua={}",udid,gpid,ip,iem,o1,ua);

		// ip= "114.112.124.83";
		AndroidDevice device = new AndroidDevice();
		device.setGpid(gpid);
		device.setIem(iem);
		device.setO1(o1);
		device.setUa(ua);
		device.setIp(ip);
		// device.getExt().setOrientation(Orientation.VERTICAL);

		// device.getGeo().setCity("beijing");
		// device.getGeo().setCountry("CHN");
		// device.setConnectionType(ConnType.WIFI);
		// device.setCarrier("ChinaMobile");
		Request request = null;

		if (apiType == ApiType.NATIVE.getValue()) {
			NativeImp nativeImp = new NativeImp(innmobiLinedtailsNum);
			request = new Request(appAndroidNativeAds, device, nativeImp);
		} else if (apiType == ApiType.BANNER.getValue()) {
			BannerImp banner = new BannerImp();
			request = new Request(appAndroidBannerAds, device, banner);
		} else if (apiType == ApiType.OPEN.getValue()) {
			NativeImp nativeImp = new NativeImp(innmobiLinedtailsNum);
			request = new Request(appAndroidOpenNativeAds, device, nativeImp);
		} else {
			throw new IllegalArgumentException("apiType值错误:" + apiType);
		}

		String response = null;
		long startTime = System.currentTimeMillis();
		try {
			response = send(request, udid);

			if (response == null) {
				return null;
			}

			if (apiType == ApiType.NATIVE.getValue()) {
				return prareNativeResponse(response, 0, showType, cacheRecord);
			} else if (apiType == ApiType.BANNER.getValue()) {
				return prareBannerResponse(response);
			} else if (apiType == ApiType.OPEN.getValue()) {
				return prareNativeResponse(response, 1, showType, cacheRecord);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			// logger.error("udid={},request={},response={},apiType={}",udid,JsonBinder.toJson(request,
			// JsonBinder.nonNull),response,apiType);
			logger.error("udid={},request={},response={},apiType={}", udid, JSON.toJSONString(request), response,
					apiType);
			return null;
			// throw e; //这类错误太多，不予记录详情
		} finally {
			startTime = System.currentTimeMillis() - startTime;
			if (startTime >= 350) {
				TimeLong.info("immobe udid={},time={}", udid, startTime);
			}
		}

		throw new IllegalArgumentException("apiType值错误:" + apiType);
	}

	public static ApiLineEntity getApiLineEntityFromCache(AdPubCacheRecord cacheRecord, String showType, int apiType,
			String udid) throws Exception {
		ApiRecord ar = cacheRecord.getapiRecord(showType);
		if (ar == null) {
			return null;
		}
		ApiLineEntity entity = null;
		int pos = cacheRecord.getApiRecordPos(ar);
		if (apiType == ApiType.NATIVE.getValue()) {
			entity = getApiLineEntityByResponse(ar.getResponse(), 0, pos);
		} else if (apiType == ApiType.OPEN.getValue()) {
			entity = getApiLineEntityByResponse(ar.getResponse(), 1, pos);
		} else {
			logger.error("参数错误:apiType={}", apiType);
		}

		cacheRecord.setApiRecord(showType, pos);

		logger.info("fromcache udid={},title={},pos={},apiType={}", udid, entity.getApiTitle(), pos, apiType);

		return entity;
	}

	public static ApiLineEntity getLineDetailsIos(String ip, String ua, String udid, String ifa, int apiType,
			AdPubCacheRecord cacheRecord, String showType) throws Exception {

		// logger.info("udid={},ip={},ua={},ifa={}",udid,ip,ua,ifa);

		IOSDevice device = new IOSDevice();
		device.setIfa(ifa);
		device.setUa(ua);
		device.setIp(ip);
		// device.getExt().setOrientation(Orientation.VERTICAL);

		Request request = null;

		if (apiType == ApiType.NATIVE.getValue()) {
			NativeImp nativeImp = new NativeImp(innmobiLinedtailsNum);
			request = new Request(appIosNativeAds, device, nativeImp);
		} else if (apiType == ApiType.BANNER.getValue()) {
			BannerImp banner = new BannerImp();
			request = new Request(appIosBannerAds, device, banner);
		} else if (apiType == ApiType.OPEN.getValue()) {
			NativeImp nativeImp = new NativeImp(innmobiLinedtailsNum);
			request = new Request(appIosOpenNativeAds, device, nativeImp);
		} else {
			throw new IllegalArgumentException("apiType值错误:" + apiType);
		}

		String response = null;

		long startTime = System.currentTimeMillis();
		try {
			response = send(request, udid);

			if (response == null) {
				logger.error("调用innob返回为空:" + udid);
				return null;
			}

			if (apiType == ApiType.NATIVE.getValue()) {
				return prareNativeResponse(response, 0, showType, cacheRecord);
			} else if (apiType == ApiType.OPEN.getValue()) {
				return prareNativeResponse(response, 1, showType, cacheRecord);
			} else {
				return prareBannerResponse(response);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			// logger.error("udid={},request={},response={},apiType={}",udid,JsonBinder.toJson(request,
			// JsonBinder.nonNull),response,apiType);
			logger.error("udid={},request={},response={},apiType={}", udid, JSON.toJSONString(request), response,
					apiType);
			return null;
			// throw e; //这类错误太多，不予记录详情
		} finally {
			startTime = System.currentTimeMillis() - startTime;
			if (startTime >= 350) {
				TimeLong.info("immobe udid={},time={}", udid, startTime);
			}
		}

	}

	private static ApiLineEntity prareBannerResponse(String jsonStr) {
		BannerResponse response = JSON.parseObject(jsonStr, BannerResponse.class);
		ApiLineEntity entity = new ApiLineEntity();
		entity.setType(AdvType.API.getValue());
		entity.setApiType(ApiType.BANNER.getValue());
		entity.setApiPubContent(response.getPubContent());
		entity.setProvider_id(ProductType.INMOBI.getValue());
		return entity;
	}

	private static ApiLineEntity prareNativeResponse(String jsonStr, int type, String showType,
			AdPubCacheRecord cacheRecord) throws Exception {
		// long t1 = System.currentTimeMillis();
		NativeResponse response = JSON.parseObject(jsonStr, NativeResponse.class);
		if (response.getAds() == null || response.getAds().size() == 0) {
			logger.error("inmobe返回ads为空");
			throw new IllegalArgumentException("返回的ads为空");
		}
		response.setDecodeAd();

		cacheRecord.setApiRecord(response, showType);

		return getApiLineEntityByResponse(response, type, 0);
	}

	private static ApiLineEntity getApiLineEntityByResponse(NativeResponse response, int type, int pos)
			throws Exception {
		ApiLineEntity entity = new ApiLineEntity();
		entity.setType(AdvType.API.getValue());
		entity.setProvider_id(ProductType.INMOBI.getValue());
		// 开屏的小图
		if (appIosOpenNativeAds.getId().equals("1474017533182")) {
			entity.setProvider_id("31");
		}

		if (appAndroidOpenNativeAds.getId().equals("1472743668741")) {
			entity.setProvider_id("31");
		}

		entity.setApiType(ApiType.NATIVE.getValue());
		// entity.setApiDes(response.getDecodedAd(pos).getDescription());
		entity.setApiTitle(response.getDecodedAd(pos).getTitle());
		List<String> unfoldList = response.getEventTracking18Urls(pos);
		List<String> clickList = response.getEventTracking8Urls(pos);
		if (type == 0) {
			entity.setCombpic(response.getDecodedAd(pos).getScreenshots().getUrl());
		} else {
			entity.setCombpic(response.getDecodedAd(pos).getScreenshots().getUrl()); // 开屏
			entity.setCombpic(saveOpenPic(entity.getApiTitle(), entity.getCombpic(), entity.getApiDes()));
			if (entity.getCombpic() == null) {
				TimeLong.info("entity.compic={}", entity.getCombpic());
				throw new IllegalArgumentException("图片链接为空");
			}

		}

		entity.setLink(response.getDecodedAd(pos).getLandingURL());
		if (response.getDecodedAd(pos).getCta().equalsIgnoreCase("install")
				|| response.getDecodedAd(pos).getCta().indexOf("下载") != -1) {
			entity.setOpenType(1);
		} else {
			entity.setOpenType(0);
			if (entity.getLink().indexOf("itunes.apple.com") != -1) {
				entity.setOpenType(1);
			}
		}

		if (unfoldList == null || unfoldList.size() == 0) {
			throw new IllegalArgumentException("展示的链接为空");
		}
		entity.setUnfoldMonitorLink(splitList(unfoldList));

		if (clickList == null || clickList.size() == 0) {
			return entity;
			// throw new IllegalArgumentException("");
		}

		entity.setClickMonitorLink(splitList(clickList));

		return entity;
	}

	private static String splitList(List<String> list) {

		String url = "";

		for (String str : list) {

			url += str;

			url += ";";

		}

		return url.substring(0, url.length() - 1);

	}

	private static String send(Request request, String udid) {
		RequestResponseManager rrm = new RequestResponseManager();

		String response = rrm.fetchAdResponseAsString(request);
		if (response == null) {
			logger.info("inmobe return null,udid={}", udid);
			return null;
		}
		logger.debug("udid={},response.length={}", udid, response.length());
		if (udid.equals("12e20279-d650-47c1-8ace-d8a8f4672deb")) {
			HttpInfoLog.info("udid={},response={}", udid, response);
		}
		logger.debug("udid={},response={}", udid, response);
		return response;

	}

	/**
	 * 生成开屏原始图片
	 * 
	 * @param title
	 * @param picUrl
	 * @return
	 * @throws Exception
	 */
	private static synchronized String saveOpenPic(String title, String picUrl, String desc) throws Exception {

		String queryTitle = title;

		String nowDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

		title = nowDate + "_" + appIosOpenNativeAds.getId() + "_" + title.hashCode();

		String url = OSSUtil.getImgUrl(title + ".jpg");
		// 已经存在直接返回
		if (url != null) {

			// logger.info("inmobe pic from cache,url={},title={}", url,title);
			return url;
		}
		// 原始图片
		String picName = UrlUtil.saveUrlPic(picUrl, title);

		logger.info("inmobe pic save, picName={},title={}", picName, title);

		// 大的开屏
		if (appIosOpenNativeAds.getId().equals("1479589943065")) {
			File file = new File(picName);

			return OSSUtil.putPhoto(title + ".jpg", file, "image/jpeg", folder);
		}

		String[] template = { innobeOpenTemplate, innobeOpenTemplate1 };
		Random random = new Random();
		InnobeOpenPic.mergeImage(template[random.nextInt(2)], picName, queryTitle, desc, innobeOpenPicNewPath, title
				+ ".jpg");

		File file = new File(innobeOpenPicNewPath + title + ".jpg");

		return OSSUtil.putPhoto(title + ".jpg", file, "image/jpeg", folder);

		// return innobeOpenUrl + title +".jpg";

	}

}
