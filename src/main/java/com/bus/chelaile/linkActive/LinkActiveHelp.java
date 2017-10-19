/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.bus.chelaile.linkActive;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.linkActive.response.LinkActiveResponseData;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.entity.ApiLineEntity;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public class LinkActiveHelp {

	private static final Logger logger = LoggerFactory.getLogger(LinkActiveHelp.class);

	private static final String getAdURL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"linkedme_url", "http://a.lkme.cc/ad/openapi/get_ad");
	private static final String linkedme_key = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"linkedme_key", "bf001474392dcd85422728980803a1e7");
	private static final String android_ad_position = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"linkedme_android_ad_position", "4000020_39");
	private static final String ios_ad_position = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"linkedme_ios_ad_position", "4000020_42");
	private static final String recordAdURL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"linkedme_report_url", "http://a.lkme.cc/ad/openapi/record_status");
	
	private int curDayCacheExpireTime = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"uc.top-k.curDayCacheExpireTime", "86400")); // 24 hours
	private static final String picsStr = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "linkedMePicsStr");
	private static final Map<String, String> linkedMePicsMap = New.hashMap();

	/*
	 * 调用第三方接口，获取linkActive广告实体
	 */
	public ApiLineEntity getLineDetails(AdvParam advParam, String showType) {
		ApiLineEntity entity = new ApiLineEntity();

		Platform platform = Platform.from(advParam.getS());
		StringBuilder paramStr = new StringBuilder();
		int retry_times = 0;
		paramStr.append("?linkedme_key=").append(linkedme_key);
		paramStr.append("&retry_times=").append(retry_times);
		if (platform.isIOS(advParam.getS())) { // ios
			String os = "iOS";
			String idfa = advParam.getIdfa();

			paramStr.append("&ad_position=").append(ios_ad_position);
			paramStr.append("&os=").append(os);
			paramStr.append("&idfa=").append(idfa);
		} else if (platform.isAndriod(advParam.getS())) { // android
			String os = "Android";
			String imei = advParam.getImei();
			String android_id = advParam.getAndroidID();

			paramStr.append("&ad_position=").append(android_ad_position);
			paramStr.append("&os=").append(os);
			paramStr.append("&imei=").append(imei);
			paramStr.append("&android_id=").append(android_id);
		}
		long timestamp = System.currentTimeMillis();
		paramStr.append("&timestamp=").append(timestamp);

		String url = getAdURL + paramStr.toString();
		logger.info("LinkActive: udid={}, url={}", advParam.getUdid(), url);
		String response = null;
		try {
			response = HttpUtils.get(url, "utf-8");
			logger.info("LinkActive: udid={}, response={}", advParam.getUdid(), response);
			if(response == null || response.equals("[]")) {
				logger.error("获取linkActive广告为空, udid={}, url={}, response={}", advParam.getUdid(), url, response);
				return null;
			}
			List<LinkActiveResponseData> responseDatas = JSON.parseArray(response, LinkActiveResponseData.class);

			entity = handleResponse(responseDatas, advParam, platform, paramStr);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("LinkActive: 请求LinkActive接口出错, udid={}", advParam.getUdid());
		}

		return null;
	}

	/*
	 * 感觉返回数据构建广告第
	 */
	private ApiLineEntity handleResponse(List<LinkActiveResponseData> responseDatas, AdvParam advParam,
			Platform platform, StringBuilder paramStr) {
		ApiLineEntity entity = new ApiLineEntity();
		Set<String> installedApps = getInstalledApps(advParam);	// 获取用户安装了的app列表

		if (installedApps != null) {
			for (LinkActiveResponseData responseData : responseDatas) {
				// 根据手机系统，以及返回的pkgName，决定返回那个图片。图片是我们设计自己做的
				String pic = null;
				if ((platform.isAndriod(advParam.getS()))) {
					if (installedApps.contains(responseData.getPkg_name())
							&& linkedMePicsMap.containsKey(responseData.getPkg_name())) {
						pic = linkedMePicsMap.get(responseData.getPkg_name());
					} else {
						continue;
					}
				} else if (platform.isIOS(advParam.getS())) {
					String uri_scheme = responseData.getUri_scheme().split("://")[0];
					if (uri_scheme == null) {
						logger.error("LinkActive: 接口返回的uri_scheme截取://后为空，udid={}, uri_scheme={}", advParam.getUdid(),
								responseData.getUri_scheme());
						continue;
					}
					if (installedApps.contains(uri_scheme) && linkedMePicsMap.containsKey(uri_scheme)) {
						pic = linkedMePicsMap.get(uri_scheme);
					} else {
						continue;
					}
				} else {
					continue;
				}

				entity.setTargetType(4); // 打开外部app
				entity.setId(responseData.getAd_content_id());
				entity.setCombpic(pic);
				// if(platform.isAndriod(advParam.getS())) {
				// entity.setCombpic("https://image3.chelaile.net.cn/f69b6b0ccf9f4024bddb03914c020c92");
				// } else {
				// entity.setCombpic("https://image3.chelaile.net.cn/e881398968d64b8ea79c15d993eab293");
				// }
				entity.setLink(responseData.getUri_scheme());
				entity.setPackageName(responseData.getPkg_name());
				entity.setShowType(ShowType.LINE_DETAIL.getValue());
				entity.setAdMode(7); // 目前仅右上角 和 大小车
				entity.setProvider_id("7"); // 手动设置7，对应linkActive
				entity.setApiType(1); // 手动设置1，对应native

				entity.setMonitorType(3); // 表示客户端不需要替换 监控链接 的参数
				String monitorLink = handleClickMonitorLink(responseData, paramStr, advParam.getUdid());
				entity.setClickMonitorLink(monitorLink + "&status=12" + ";" + monitorLink + "&status=13"); // 两条上报用逗号隔开
				entity.setUnfoldMonitorLink("http://www.baidu.com");
				// entity.setClickMonitorLink(monitorLink + "&status=13");
				// entity.setUnfoldMonitorLink(monitorLink + "&status=12");

				return entity;
			}
		} else {
			logger.error("LinkActive: 获取用户安装app信息失败，udid={}, s={}, v={}", advParam.getUdid(), advParam.getS(),
					advParam.getV());
		}

		return null;
	}

	/*
	 * 根据用户 udid 获取安装app信息， 对ios的版本可以加以控制，只有最新版才会有
	 */
	@SuppressWarnings("unchecked")
	private Set<String> getInstalledApps(AdvParam advParam) {
		Set<String> apps = New.hashSet();
		String appInfoStr = (String) CacheUtil.getNew(AdvCache.getAppInfoKey(advParam.getUdid()));
		logger.info("从ocs获取用户安装app的信息： udid={},appInfo={}", advParam.getUdid(),appInfoStr);
		try {
			if (appInfoStr != null) {
				apps = JSON.parseObject(appInfoStr, HashSet.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("LinkActive: 用户安装app从ocs转set出错，udid={}, appinfoJ={}", advParam.getUdid(), appInfoStr);
			return apps;
		}

		// apps.add("snssdk141"); // 头条，ios
		// apps.add("com.ss.android.article.news"); // 头条，android
//		logger.info("LinkActive: 获取到用户安装app是：udid={}, appinfo={}", advParam.getUdid(), JSON.toJSONString(apps));
		return apps;
	}

	/*
	 * 构建linkedMe的report链接 类型为点击
	 * type=12，点击上报
	 * type=13，打开app上报
	 */
	private String handleClickMonitorLink(LinkActiveResponseData responseData, StringBuilder paramStr, String udid) {
		String active_device_type = responseData.getActive_device_type();
		String ad_code = responseData.getAd_code();
		String request_id = responseData.getRequest_id();
		int ad_content_id = responseData.getAd_content_id();
		long timestamp = System.currentTimeMillis();

		paramStr.append("&active_device_type=" + active_device_type);
		paramStr.append("&ad_code=" + ad_code);
		paramStr.append("&ad_content_id=" + ad_content_id);
		paramStr.append("&request_id=" + request_id);
		paramStr.append("&timestamp=" + timestamp);
		boolean debug = false;
		if (Constants.ISTEST) {
			debug = true;
		}
		paramStr.append("&debug=" + debug);

		String url = recordAdURL + paramStr.toString();
		logger.info("LinkActive: 构建的监控链接是： udid={}, url={}", udid, url);
		return url;
	}

	/*
	 * 解析埋点日志，获取用户安装app的信息 android和ios处理逻辑不同
	 */
	public void analysisMaidian(String s) {
//		logger.info("获取到info埋点 s={}", s);
		String buf[] = AdvUtil.decodeUrl(s).split(" \\|# ");
		HashMap<String, String> mapInfo = new HashMap<String, String>();
		if (buf != null)
			for (int i = 0; i < buf.length; i++) {
				// System.out.println(buf[i]);
				String info[] = buf[i].split(":");
				if (info != null && info.length == 2) {
					if (info[0].contains("<APP_INFO>")) {
						String tmp_arr[] = info[0].split("<APP_INFO>");
						if (tmp_arr.length > 1) {
							info[0] = tmp_arr[1];
						}
					}
					mapInfo.put(info[0].trim(), info[1].trim());
				}
			}

		String udid = mapInfo.get("udid");
		String platform = mapInfo.get("s"); // android OR ios
		if (StringUtils.isBlank(udid) || platform == null) {
			logger.info("LinkActive: 无平台信息:{},行信息{}", platform, s);
			return;
		}

		// 根据投放策略 决定是否 存储用户的安装app信息
//		AdCategory cateGory  = AdDispatcher.getAd(udid, platform.toLowerCase(), null, null, null, 3);
//		logger.info("LinkActive: udid={}, cateGory={}", udid, cateGory);
//		if (cateGory != null && cateGory.getAdType() == 7) {
//			logger.info("LinkActive: 需要投放linkActive广告的用户, udid={}, s={}", udid, platform);
//		} else {
//			return;
//		}

		// 获取用户的info字段信息，解码得到各个竞品信息！
		String info = mapInfo.get("info");
		String appInfo = null; 
		HashSet<String> appsInfo = new HashSet<String>();
		if (info != null) {
			info = info.replace(" HTTP/1.1", "");	//ios埋点最后会多出来这个字符串
			StringBuffer buffer = new StringBuffer(info);
			for (int i = 0; i < info.length(); i++) {
				buffer.setCharAt(i, (char) (info.charAt(i) - 1));
			}
			appInfo = buffer.toString();
			// android 3.20.0版本，埋点出现错误，没有将分隔不同app的逗号编码，所以解码后变成 + ，故此处对 + 也当做逗号处理
			if (appInfo != null && appInfo.contains(",")) {
				String apps[] = appInfo.split(",");
				if (apps != null) {
					for (String app : apps)
						appsInfo.add(app);
				}
			} else if (appInfo != null && appInfo.contains("+")) {
				String apps[] = appInfo.split("\\+");
				if (apps != null) {
					for (String app : apps)
						appsInfo.add(app);
				}
			}
		} else {
			return;
		}

		// 保存至 ocs
		if (CacheUtil.getNew(AdvCache.getAppInfoKey(udid)) == null) {
			CacheUtil.setNew(AdvCache.getAppInfoKey(udid), curDayCacheExpireTime, JSONObject.toJSONString(appsInfo));
		}

	}
	
	/*
	 * 初始化图片对应关系
	 */
	public void initLinkedMePics() {
		try {
			if(linkedMePicsMap != null && linkedMePicsMap.size() > 0) {
				linkedMePicsMap.clear();
			}
			if (picsStr != null) {
				String[] str = picsStr.split(",");
				for (String s : str) {
					linkedMePicsMap.put(s, PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), s));
				}
				logger.info("linkedMe init success! ******************");
				System.out.println("linkedMe init success! ******************");
				for (String s : linkedMePicsMap.keySet()) {
					logger.info("linkedMe init info : s={}, pic={}", s, linkedMePicsMap.get(s));
				}
			} else {
				logger.error("linkedMe pics Str is null !  ****************");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		String str = "<190>May 16 10:38:53 web1 nginx: 182.18.10.10 |# - |# 2017-05-16 10:38:53 |# GET /realtimelog?%3CAPP_INFO%3EuserId:%20%7C%23%20geo_type:wgs%20%7C%23%20language:1%20%7C%23%20secret:4af32c2d565446e19e0429d91f379a4e%20%7C%23%20geo_lat:39.994587%20%7C%23%20geo_lng:116.403899%20%7C%23%20sv:10.3.1%20%7C%23%20deviceType:iPhone6%20%7C%23%20s:IOS%20%7C%23%20lchsrc:icon%20%7C%23%20v:5.31.0%20%7C%23%20udid:e30647e8cf40370a927c1d9101af467f89ac2cf0%20%7C%23%20sign:on/DpOXtrkqZqik69RfhqQ==%20%7C%23%20nw:WiFi%20%7C%23%20mac:%20%7C%23%20geo_lac:65.000000%20%7C%23%20cityId:027%20%7C%23%20wifi_open:1%20%7C%23%20push_open:1%20%7C%23%20vc:10360%20%7C%23%20accountId:4904321%20%7C%23%20idfa:06DA0F64-6E86-4D26-B66E-5C9CBC351D3D%20%7C%23%20info:ejejusjqdbse-cbjevnbq-tottel252-psqifvt-ejboqjoh-jnfjuvbo-pqfoBqq/keNpcjmf-ubpcbp HTTP/1.1 |# 302 |# 0.000 |# 264 |# - |# liteTest/5.31.0 (iPhone; iOS 10.3.1; Scale/2.00) |# - |# test.logs.chelaile.net.cn |# - |# -";
		LinkActiveHelp link = new LinkActiveHelp();
		link.analysisMaidian(str);

		AdvParam advParam = new AdvParam();
		advParam.setS("android");
		advParam.setImei("111111111");
		
		Set<String> set = New.hashSet();
		System.out.println(set);
		set.add("1");
		System.out.println(JSONObject.toJSON(set));
		String setJ = JSONObject.toJSONString(set);
		
		Set<String> set1 = JSON.parseObject(setJ, Set.class);
		for(String s : set1) {
			System.out.println(s);
		}
				

		// getLineDetails(advParam, "01");
	}
}