package com.bus.chelaile.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.AdvancedBinaryJedisCommands;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.TypeNumber;
import com.bus.chelaile.model.ads.AdButtonInfo;
import com.bus.chelaile.model.ads.AdCard;
import com.bus.chelaile.model.ads.AdTagInfo;
import com.bus.chelaile.model.ads.BannerInfo;
import com.bus.chelaile.model.ads.Station;
import com.bus.chelaile.model.ads.entity.ActiveAdEntity;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.StationAdEntity;
import com.bus.chelaile.model.client.ClientDto;
import com.bus.chelaile.model.record.DisplayUserCache;
import com.bus.chelaile.model.rule.version.VersionEntity;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.impl.ActiveManager;
import com.bus.chelaile.service.impl.DoubleAndSingleManager;
import com.bus.chelaile.service.impl.LineDetailsManager;
import com.bus.chelaile.service.impl.OpenManager;
import com.bus.chelaile.service.impl.RideManager;
import com.bus.chelaile.service.impl.SelfManager;
import com.bus.chelaile.service.impl.StationAdsManager;
import com.bus.chelaile.strategy.UserStrategyJudger;
import com.bus.chelaile.thread.ReloadInvalidAccountIdTimer;
import com.bus.chelaile.util.FlowUtil;
import com.bus.chelaile.util.New;

public class ServiceManager {

	protected static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
	@Autowired
	private LineDetailsManager lineDetailsManager;
	@Autowired
	private StationAdsManager stationAdsManager;

	@Autowired
	private OpenManager openManager;

	@Autowired
	private DoubleAndSingleManager doubleAndSingleManager;

	@Autowired
	private ActiveManager activeManager;

	@Autowired
	private RideManager rideManager;

	@Autowired
	private SelfManager selfManager;

	@Autowired
	private StartService startService;

	@Autowired
	AdvInvalidService advInvalidService;

	@Autowired
	ReloadInvalidAccountIdTimer reloadInvalidAccountIdTimer;

//	@Autowired
//	CommonService commonService;

	@Autowired
	UserStrategyJudger userStrategyJudger;

	// 线路详情最低的广告版本号
	public static final VersionEntity ANDROID_ADS_LINEDETAILS_VERSION = new VersionEntity(3, 13, 0);
	public static final VersionEntity IOS_ADS_LINEDETAILS_VERSION = new VersionEntity(5, 12, 0);

	/**
	 * - 单，双栏，首页浮层： - iOS>=5.2.0 - Android>=3.3.0
	 */
	public static final VersionEntity ANDROID_ADS_DOUBLE_VERSION = new VersionEntity(3, 3, 0);
	public static final VersionEntity IOS_ADS_DOUBLE_VERSION = new VersionEntity(5, 2, 0);

	public String getClienSucMap(Object obj, String status) {
		ClientDto clientDto = new ClientDto();
		clientDto.setSuccessObject(obj, status);
		try {
			String json = JSON.toJSONString(clientDto, SerializerFeature.BrowserCompatible);
			// JsonBinder.toJson(clientDto, JsonBinder.always);
			return "**YGKJ" + json + "YGKJ##";
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			return "";
		}
	}

	public String getClientErrMap(String errmsg, String status) {
		ClientDto clientDto = new ClientDto();
		clientDto.setErrorObject(errmsg, status);
		try {
			String json = JSON.toJSONString(clientDto);
			return "**YGKJ" + json + "YGKJ##";
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			return "";
		}
	}

	/**
	 * mvc BusAdvAction直接调用,返回给客户端json字符串
	 * 
	 * @param advParam
	 * @param methodName
	 * @return
	 */
	public String getAdsResponseStr(AdvParam advParam, String methodName) {
		String responseStr = "";

		try {
			Object object = getQueryValue(advParam, methodName);
			if (object == null) {
				responseStr = getClientErrMap("", Constants.STATUS_NO_DATA);
			} else {
				responseStr = getClienSucMap(object, Constants.STATUS_REQUEST_SUCCESS);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			responseStr = getClientErrMap(e.getMessage(), Constants.STATUS_NO_DATA);
		}
		return responseStr;
	}

	/**
	 * 根据methodName获取不同类型的广告
	 * 
	 * @param advParam
	 * @param methodName
	 * @return BaseAdEntity或者List<BaseAdEntity>
	 * @throws Exception
	 */
	public Object getQueryValue(AdvParam advParam, String methodName) throws Exception {
		// // 香港不投广告
		VersionEntity tgv = VersionEntity.parseVersionStr(advParam.getV());
		boolean isValidVersion = false;
		if (methodName.equals("getLineDetails") || methodName.equals("getNewLineDetails")) {
			isValidVersion = checkLineDetailsVersion(tgv, Platform.from(advParam.getS()));
		} else {
			isValidVersion = checkDoubleVersion(tgv, Platform.from(advParam.getS()));
		}
		// 版本检测失败
		if (!isValidVersion) {
			logger.info("isValidVersion return false,udid={},s={},v={}", advParam.getUdid(), advParam.getV(),
					advParam.getS());
			return null;
		}
		Object entity = null;
		JSONObject object = new JSONObject();
		if (methodName.equals("getLineDetails")) { // 线路详情
			BaseAdEntity entity1 = getLineDetails(advParam);
			if (entity1 == null) {
				return null;
			} else if (entity1 != null && entity1.getShowType() == ShowType.LINE_DETAIL.getValue()) {
				object.put("lineAds", entity1);
			} else if (entity1 != null && entity1.getShowType() == ShowType.STATION_ADV.getValue()) {
				object.put("stationAds", entity1);
			}
			return object;
		} else if (methodName.equals("getNewOpen")) { // 新版本开屏、浮层、乘车页浮层
			entity = getNewOpen(advParam);
			object.put("ads", entity);
		} else if (methodName.equals("getOldOpen")) { // 旧版本开屏、浮层
			entity = getOldOpen(advParam);
			object.put("ads", entity);
		} else if (methodName.equals("preLoadAds")) { // 旧版本开屏浮层预加载
			entity = preLoadAds(advParam);
			object = (JSONObject) entity;
			// object.put("ads", entity);
		} else if (methodName.equals("precacheResource")) { // 预缓存广告资源图片
			entity = precacheResource(advParam);
			object = (JSONObject) entity;
		} else if (methodName.equals("getDoubleAndSingleAds")) { // 单双栏
			entity = getDoubleAndSingleAds(advParam);
			object.put("ads", entity);
		} else if (methodName.equals("getRide")) {
			entity = getRide(advParam);
			object = (JSONObject) entity; // 乘车页广告，新增音频广告内容
		} else if (methodName.equals("getActive")) { // 活动页|聊天室 广告
			entity = getActive(advParam);
			switch (advParam.getType()) {
			case 0:
				object.put("activeAds", entity);
				break;
			case 2:
				object.put("chatAds", entity);
				break;
			default:
				return null;
			}
		} else if(methodName.equals("getAboardText")) { // 上车提醒文案
			entity = getAboardText(advParam);
			object = (JSONObject) entity;
		}
		if (entity == null)
			return null;
		return object;
	}


	/*
	 * 单双栏
	 */
	private List<BaseAdEntity> getDoubleAndSingleAds(AdvParam advParam) throws Exception {
		QueryParam queryParam = new QueryParam();
		
		if(advParam.getType() == TypeNumber.ONE.getType()) {	//type=1 : route_plan_adv
			BaseAdEntity routeEntity = doubleAndSingleManager.doService(advParam, ShowType.ROUTE_PLAN_ADV, false,
					queryParam, true);
			if(routeEntity == null) {
				return null;
			}
			List<BaseAdEntity> list = New.arrayList();
			list.add(routeEntity);
			return list;
		}
		
		List<Station> stList = advParam.getStationList();
		if (null == stList || stList.size() == 0) {
			logger.info("单双栏广告获取失败，stList为空，udid={}", advParam.getUdid());
			return null;
		}
		Station lastUnfoldStation = new Station("noUnfold", 0, false);
		Station firstUnfoldStation = new Station("noUnfold", -1, false);
		boolean isFirstUnfoldStation = false;
		for (Station st : stList) {
			if (st.isUnfold()) {
				if (!isFirstUnfoldStation) {
					firstUnfoldStation = st;
					isFirstUnfoldStation = true;
				}
				lastUnfoldStation = st;
			}
		}
		queryParam.setStation(lastUnfoldStation); // 将最后一个展开的站点赋值给 queryParam
		BaseAdEntity doubleEntity = doubleAndSingleManager.doService(advParam, ShowType.DOUBLE_COLUMN, false,
				queryParam, true);
		queryParam.setStation(firstUnfoldStation);
		BaseAdEntity singleEntity = doubleAndSingleManager.doService(advParam, ShowType.SINGLE_COLUMN, false,
				queryParam, true);

		if (doubleEntity == null && singleEntity == null) {
			return null;
		}

		// if(advParam.getGridLines() == 2) {
		// logger.info("doubleAndSingle ads return null, udid={}, cityId={}, gridLines={}",
		// advParam.getUdid(), advParam.getCityId(), advParam.getGridLines());
		// return null;
		// }

		List<BaseAdEntity> list = New.arrayList();

		// 首页导流入口广告如果有两层，那么不予返回广告 && 金数据调查问卷 除外
		if (doubleEntity != null) {
			if (advParam.getGridLines() != 2 || doubleEntity.getLink().contains("jinshuju.net")) {
				list.add(doubleEntity);
			} else {
				logger.info("double ads return null, udid={}, cityId={}, gridLines={}", advParam.getUdid(),
						advParam.getCityId(), advParam.getGridLines());
			}
		}
		if (singleEntity != null) {
			if (advParam.getGridLines() != 2 || singleEntity.getLink().contains("jinshuju.net")) {
				list.add(singleEntity);
			} else {
				logger.info("single ads return null, udid={}, cityId={}, gridLines={}", advParam.getUdid(),
						advParam.getCityId(), advParam.getGridLines());
			}
		}

		return list;

	}

	/**
	 * 详情页
	 * 
	 * @param advParam
	 * @return
	 * @throws Exception
	 */
	private BaseAdEntity getLineDetails(AdvParam advParam) throws Exception {

		boolean isNeedApid = false;
		QueryParam queryParam = new QueryParam();
		if (advParam.getS().equalsIgnoreCase("ios") && advParam.getVc() >= 10220 && advParam.getScreenHeight() > 0
				&& advParam.getScreenHeight() >= 960) {
			isNeedApid = true;
		} else if (advParam.getS().equalsIgnoreCase("android") && advParam.getVc() >= 60
				&& advParam.getScreenHeight() > 0 && advParam.getScreenHeight() >= 960) {
			isNeedApid = true;
		}
		
		if(returnStnAds(advParam)) {
			BaseAdEntity stnAds = stationAdsManager.doService(advParam, ShowType.STATION_ADV, false, queryParam, true);
			
			// TODO 手动构造一批数据
			stnAds = createStnAds(advParam);
			
			if(stnAds != null) {
				return stnAds;
			}
		}
		
		
		return lineDetailsManager.doService(advParam, ShowType.LINE_DETAIL, isNeedApid, queryParam, true);
	}

	//  TODO　虚构站点广告数据
	private BaseAdEntity createStnAds(AdvParam advParam) {
		String stationId = advParam.getStationId();
		StationAdEntity entity = new StationAdEntity();
		entity.setId(123123);
		entity.setShowType(ShowType.STATION_ADV.getValue());
		entity.setPic("http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png");
		
		AdTagInfo tagText = new AdTagInfo("搞起", "255,255,255,1", null);
		AdTagInfo tagPic = new AdTagInfo(null, null, "http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png");
		List<AdTagInfo> tags = New.arrayList();
		tags.add(tagText);
		tags.add(tagPic);
		
		AdButtonInfo button0 = new AdButtonInfo("查看", "255,255,255,1", "139,43,43,1", "118,89,89,1", "http://pic1.chelaile.net.cn/adv/brandIcon1187320170922.png");
		AdButtonInfo button1 = new AdButtonInfo(null, null, null, null, "http://pic1.chelaile.net.cn/adv/brandIcon1187320170922.png");
		
		//(int cardType, String topPic, String logo, List<AdTagInfo> tags, String name, String address,
		//Double lng, Double lat, String phoneNum, String link)
		AdCard adCard0 = new AdCard(0, "http://pic1.chelaile.net.cn/adv/ios67326f0f-ebeb-47e0-bce3-99cb78cc02aa.jpg","http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png",
				tags, "路边野店", "银河系，惠中路5号，B座，22层", 116.403931, 39.994642, "15072435749", null);	
		AdCard adCard1 = new AdCard(1, "http://pic1.chelaile.net.cn/adv/ios67326f0f-ebeb-47e0-bce3-99cb78cc02aa.jpg","http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png",
				tags, "路边野店", "银河系，惠中路5号，B座，22层", 116.403931, 39.994642, null, "http://www.baidu.com");	
		AdCard adCard2 = new AdCard(2, "http://pic1.chelaile.net.cn/adv/ios67326f0f-ebeb-47e0-bce3-99cb78cc02aa.jpg","http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png",
				tags, "路边野店", "银河系，惠中路5号，B座，22层", 116.403931, 39.994642, "15072435749", "http://www.baidu.com");	
		
		
//		BannerInfo(int bannerType, String name, String color, String slogan, String sloganColor, AdTagInfo tag,
//				AdButtonInfo button)
		BannerInfo bannerInfo0 = new BannerInfo(0, "路边野店", "255,255,255,1", "来路边野店，找童年的味道", "255,255,255,1", null, null);
		BannerInfo bannerInfo1 = new BannerInfo(1, "路边野店", "255,255,255,1", "来路边野店，找童年的味道", "255,255,255,1", tagText, null);
		BannerInfo bannerInfo2 = new BannerInfo(2, "路边野店", "255,255,255,1", "来路边野店，找童年的味道", "255,255,255,1", tagPic, null);
		BannerInfo bannerInfo3 = new BannerInfo(3, "路边野店", "255,255,255,1", "来路边野店，找童年的味道", "255,255,255,1", null, button0);
		BannerInfo bannerInfo4 = new BannerInfo(4, "路边野店", "255,255,255,1", "来路边野店，找童年的味道", "255,255,255,1", null, button1);
		
		
		if (stationId.equals("010-8795")) {
			entity.setBannerInfo(bannerInfo0);
		} else if (stationId.equals("010-3343")) {
			entity.setBannerInfo(bannerInfo1);
		} else if (stationId.equals("010-9053")) {
			entity.setBannerInfo(bannerInfo2);
		} else if (stationId.equals("010-7339")) {
			entity.setBannerInfo(bannerInfo3);
			entity.setAdCard(adCard0);
		} else if (stationId.equals("010-5459")) {
			entity.setBannerInfo(bannerInfo4);
			entity.setAdCard(adCard1);
		} else if (stationId.equals("010-7309")) {
			entity.setBannerInfo(bannerInfo3);
			entity.setAdCard(adCard2);
		} else {
			logger.info("站点广告为空 , stationId={}, udid={}", stationId, advParam.getUdid());
			return null;
		}
		
		return entity;
		
	}

	// 1107后的新版支持站点广告
	// 版本控制
	private boolean returnStnAds(AdvParam advParam) {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * 新版本开屏
	 */
	private BaseAdEntity getNewOpen(AdvParam advParam) throws Exception {

		QueryParam queryParam = new QueryParam();
		// 乘车页浮层广告
		if (advParam.getType() == 2) {
			return openManager.doService(advParam, ShowType.FULL_SCREEN_RIDE, false, queryParam, true);
		}
		// 共享单车浮层广告
		if (advParam.getType() == 3) {
			return openManager.doService(advParam, ShowType.FULL_SCREEN_MOBIKE, false, queryParam, true);
		}

		// 对于首页浮层和开屏广告。先获取浮层，如果存在浮层广告，那么开屏不调用第三方
		boolean isRecord = true;
		if(advParam.getType() == 0) {	// 如果type为0，那么不记录浮层广告的发送记录。
			isRecord = false;
		}
		boolean isNeedApi = true;
		BaseAdEntity baseAdEntity = openManager.doService(advParam, ShowType.FULL_SCREEN, false, queryParam, isRecord);
		if (baseAdEntity != null) {
			isNeedApi = false;
		}

		// 0 开屏, 1 首页浮层 , 2 乘车页浮层
		if (advParam.getType() == 0) {
			return openManager.doService(advParam, ShowType.OPEN_SCREEN, isNeedApi, queryParam, true);
		} else if (advParam.getType() == 1) {
			return baseAdEntity;
		} else {
			throw new IllegalArgumentException("type类型错误:type=" + advParam.getType());
		}

	}

	/*
	 * 活动页和聊天室
	 */
	private BaseAdEntity getActive(AdvParam advParam) throws Exception {

		QueryParam queryParam = new QueryParam();
		if (advParam.getType() == 0) {
			return activeManager.doService(advParam, ShowType.ACTIVE_DETAIL, false, queryParam, true);
		} else if (advParam.getType() == 2) {
			// 聊天室广告，从数据库获取广告，类型都是8，返回数据中需要区分对待
			return activeManager.doService(advParam, ShowType.RIDE_DETAIL, false, queryParam, true);
		} else {
			throw new IllegalArgumentException("type类型错误:type=" + advParam.getType());
		}
	}

	/*
	 * 乘车页广告单独处理
	 */
	private JSONObject getRide(AdvParam advParam) {

		JSONObject resultMap = new JSONObject();
		QueryParam queryParam = new QueryParam();
		BaseAdEntity rideAds = rideManager.doService(advParam, ShowType.RIDE_DETAIL, false, queryParam, true);
		BaseAdEntity audioAds = rideManager.doService(advParam, ShowType.RIDE_AUDIO, false, queryParam, true);

		if (rideAds == null && audioAds == null) {
			return null;
		}
		resultMap.put("rideAds", rideAds);
		resultMap.put("audioAds", audioAds);

		return resultMap;
	}

	/*
	 * 旧版本预加载
	 */
	private JSONObject preLoadAds(AdvParam advParam) throws Exception {

		QueryParam queryParam = new QueryParam();
		queryParam.setOldMany(true);
		BaseAdEntity openEntity = openManager.doService(advParam, ShowType.OPEN_SCREEN, false, queryParam, true);
		BaseAdEntity fullEntity = openManager.doService(advParam, ShowType.FULL_SCREEN, false, queryParam, true);
		if (openEntity == null && fullEntity == null) {
			return null;
		}
		List<BaseAdEntity> list = New.arrayList();
		if (openEntity != null) {
			list.add(openEntity);
		}
		if (fullEntity != null) {
			list.add(fullEntity);
		}
		// 构建返回结构
		JSONObject resultMap = openManager.buildResultMap(list);

		AnalysisLog.info("[PRELOAD_ADS]: tag=9, accountId={}, udid={}, cityId={}, s={}, v={},resultMap={}",
				advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(), advParam.getV(),
				resultMap);

		return resultMap;
	}

	/**
	 * 预缓存图片资源 获取今明两天，所有可投广告的图片地址。不需要做规则校验
	 * 
	 * @param advParam
	 * @return
	 */
	private Object precacheResource(AdvParam advParam) {

		logger.info(
				"[ENTERprecacheResource]:s={}, accountId={}, udid={}, cityId={}, v={}, lineId={}, stnName={}, vc={}",
				advParam.getS(), advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getV(),
				advParam.getLineId(), advParam.getStnName(), advParam.getVc());

		Set<String> pics = openManager.getAllAdsAdsAudiosPics(advParam, ShowType.OPEN_SCREEN);
		Set<String> picsFull = openManager.getAllAdsAdsAudiosPics(advParam, ShowType.FULL_SCREEN);
		Set<String> audios = openManager.getAllAdsAdsAudiosPics(advParam, ShowType.RIDE_AUDIO);

		JSONObject resultMap = new JSONObject();

		if (pics == null && picsFull == null && audios == null)
			return null;

		if (pics != null) {
			if (picsFull != null) {
				pics.addAll(picsFull);
			}
		} else {
			pics = picsFull;
		}

		resultMap.put("pics", pics);
		resultMap.put("audios", audios);

//		logger.info("precacheResourceResult: udid={}, pics={}", advParam.getUdid(), pics.toString());
		
		return resultMap;

	}

	/*
	 * 老版开屏，浮层
	 */
	private BaseAdEntity getOldOpen(AdvParam advParam) throws Exception {

		QueryParam queryParam = new QueryParam();
		// 0 开屏 1 浮层
		if (advParam.getType() == 0) {
			return openManager.doService(advParam, ShowType.OPEN_SCREEN, false, queryParam, true);
		} else if (advParam.getType() == 1) {
			return openManager.doService(advParam, ShowType.FULL_SCREEN, false, queryParam, true);
		} else {
			throw new IllegalArgumentException("type类型错误:type=" + advParam.getType());
		}

	}

	/*
	 * reload
	 */
	public String reloadDatas() {
		long start = System.currentTimeMillis();
		if (!SynchronizationControl.isReload()) {
			try {
				SynchronizationControl.setReloadSynLockState(true);
				List<String> advIds = startService.init();
				userStrategyJudger.init();
				JSONObject jsn = new JSONObject();
				jsn.put("advIds", advIds);
				logger.info("reload costs {} ms", (System.currentTimeMillis() - start));
				return getClienSucMap(jsn, Constants.STATUS_REQUEST_SUCCESS);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.error("reloadDatas exception");
			} finally {
				SynchronizationControl.setReloadSynLockState(false);
			}
		} else {
			logger.info("reloadDatas repeat failed");
			return getClientErrMap("请稍后再试", "01");
		}
		return null;
	}

	/*
	 * uninterest
	 */
	public String uninterest(AdvParam param, int showType, int advId, int apiType, String provider_id, String secret) {

		logger.info(
				"[ENTERuninterestads]: showType={}, advId={}, udid={}, accountId={}, cityId={}, lineId={}, apiType={},"
						+ " provider_id={}, secret={}", showType, advId, param.getUdid(), param.getAccountId(),
				param.getCityId(), param.getLineId(), apiType, provider_id, secret);

		if (!checkSecret()) {
			logger.error("点击关闭操作非法: advId={}, udid={}, accountId={}, secret={}", advId, param.getUdid(),
					param.getAccountId(), secret);
			return getClientErrMap("非法操作", Constants.STATUS_INTERNAL_ERROR);
		}

		AdvCache.saveNewUninterestedAds(param.getUdid(), advId, param.getLineId(), showType, provider_id, apiType,
				param.getS(), param.getVc());
		if (showType == 5) {
			// 清除右小角广告投放次数
			selfManager.clearRithPushNum(param.getUdid(), advId);
		}

		return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

	public String interest(AdvParam param, int showType, int advId) {
		AdvCache.removeUninterestAds(param.getUdid(), showType, advId);
		return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

	/*
	 * invalidUser
	 */
	public String invalidUser(AdvParam param, String startInvalidDate, String endInvalidDate) {
		try {
			logger.info("[ENTERinvalidUsers]: udid={}, accountId={}", param.getUdid(), param.getAccountId());
			advInvalidService.invalidUser(param.getUdid(), param.getAccountId(), startInvalidDate, endInvalidDate);
			logger.info("invalidUser Success: udid={}, accountId={}", param.getUdid(), param.getAccountId());
			return getClientErrMap("", Constants.STATUS_NO_DATA);
		} catch (Exception e) {
			logger.error("invalidUsers Failed: udid={}, accountId={}", param.getUdid(), param.getAccountId());
			return getClientErrMap("", Constants.STATUS_FUNCTION_NOT_ENABLED);
		}
	}

	/*
	 * clearInvalidAds
	 */
	public String clearInvalidAds(AdvParam param) {
		if (param.getAccountId() == null) {
			return getClientErrMap("accountId为空", Constants.STATUS_PARAM_ERROR);
		}
		try {
			logger.info("[ENTERclearInvalidAds]: udid={}, accountId={}", param.getUdid(), param.getAccountId());
			advInvalidService.clearAccountId(param.getAccountId());
		} catch (Exception e) {
			logger.error(String.format("Uncaught exception: errMsg=%s,udid=%s", e.getMessage(), param.getUdid()), e);
			return getClientErrMap(e.getMessage(), Constants.STATUS_PARAM_ERROR);
		}
		logger.info("清除掉失效的广告用户信息: accountId={}, udid={}", param.getAccountId(), param.getUdid());

		return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}
	
	/*
	 * 获取上车提醒文案
	 */
	private Object getAboardText(AdvParam advParam) {
		
		JSONObject resultMap = new JSONObject();
		// 取得所有刻意投放广告
		QueryParam queryParam = new QueryParam();
		BaseAdEntity audioAds = rideManager.doService(advParam, ShowType.RIDE_AUDIO, false, queryParam, true);
		System.out.println(JSONObject.toJSONString(audioAds));
		
		if(audioAds != null && audioAds instanceof ActiveAdEntity) {
			ActiveAdEntity ads = (ActiveAdEntity) audioAds;
			String text = ads.getAboardText();
			if(StringUtils.isNoneEmpty(text)) {
				resultMap.put("aboardText", text);
			}
		}
		
		return resultMap;
	}

	/**
	 * load invalidUsers
	 */
	public void reloadInvalidUsers() {
		reloadInvalidAccountIdTimer.run();
	}

	/*
	 * getDisplayAdv(获取所有可投放的广告)
	 */
	public String getDisplayAdv(AdvParam advParam) {
		logger.info("[ENTERgetDisplayAdv] udid={}, accountId={}, cityId={}", advParam.getUdid(),
				advParam.getAccountId(), advParam.getCityId());
		DisplayUserCache displayUserCache = CommonService.getDisplayAdvByUdid(advParam);
		JSONObject js = new JSONObject();
		js.put("lineAds", displayUserCache);

		return getClienSucMap(js, Constants.STATUS_REQUEST_SUCCESS);
	}

	/**
	 * 线路详情最低版本检测
	 *
	 * @param tgtV
	 * @param s
	 * @return
	 */
	private boolean checkLineDetailsVersion(VersionEntity tgtV, Platform platform) {
		VersionEntity baseVersion = null;
		if (platform == Platform.ANDROID) {
			baseVersion = ANDROID_ADS_LINEDETAILS_VERSION;
		} else if (platform == Platform.IOS) {
			baseVersion = IOS_ADS_LINEDETAILS_VERSION;
		} else if (platform == Platform.H5) {
			// H5无版本限制。
			return true;
		} else {
			logger.error("[WRONG_PLATFORM] 无法识别的目标平台: platform={}", platform);
			return false;
		}

		if (tgtV == null) {
			logger.error("目标版本为null");
			return false;
		}

		return tgtV.compareTo(baseVersion) >= 0;
	}

	private boolean checkDoubleVersion(VersionEntity tgtV, Platform platform) {
		VersionEntity baseVersion = null;

		if (platform == Platform.ANDROID) {
			baseVersion = ANDROID_ADS_DOUBLE_VERSION;
		} else if (platform == Platform.IOS) {
			baseVersion = IOS_ADS_DOUBLE_VERSION;
		} else if (platform == Platform.H5) {
			// H5无版本限制。
			return true;
		} else {
			logger.error("[WRONG_PLATFORM] 无法识别的目标平台: platform={}", platform);
			return false;
		}

		if (tgtV == null) {
			logger.error("目标版本为null");
			return false;
		}

		return tgtV.compareTo(baseVersion) >= 0;
	}

	private boolean checkSecret() {
		return true;
	}
	
	/*
	 * 修改从fileName中读取到的所有用户的收藏频道。
	 * 增加指定频道channelId
	 */
	public String handlFavChannels(String fileName, String channelId) {
		BufferedReader reader = null;
		int size = 0;
		try {
			File checkFile = new File(fileName);
			if (!checkFile.exists()) {
				System.out.println("fileName={} is not exist" + fileName);
				return "file not exit! , file=" + fileName;
			}

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String line = reader.readLine();
			while (line != null) {
				line = StringUtils.trimToEmpty(line);
				String buf[] = line.split(",");
				if(buf.length >= 2) {
					String udid = buf[0];
					String accountId = buf[1];
					// 根据用户id，添加channleId到他们的收藏中
					addFavChannleToUser(udid, accountId, channelId);
				}
				line = reader.readLine();
				size ++;
			}
		} catch (IOException ioe) {
			String errMsg = "Read file '" + fileName + "' exception, errMsg=" + ioe.getMessage();
			System.out.println(errMsg);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception ex) {
				// Ignore this exception
			}
		}
		logger.info("handle FavChannel end ,size={}", size);
		return "change FavChannel success! ";
	}

	private void addFavChannleToUser(String udid, String accountId, String channelId) {
		ArrayList<String> favChannelIds = null;
		boolean isFromUDID = true;
		// accountId不为空，那么取之
		if(StringUtils.isNoneEmpty(accountId) && ! accountId.equalsIgnoreCase("null")) {
			String key = "article_fav_" + accountId;
			String favStr = (String) CacheUtil.getNew(key);
			// accountId存在收藏，直接返回， 如果不存在，接下来继续看udid
			if (favStr != null) {
				isFromUDID = false;
				favChannelIds = new ArrayList<>(Arrays.asList(favStr.split(",")));
				logger.info("get favArticles from ocs by ACCOUNTID : udid={}, accountId={}, favStr={}",
						udid, accountId, favStr);
			}
		} 
		
		// 如果accountId取收藏失败，那么从udid入手
		if(favChannelIds == null){
			String key = "article_fav_" + udid;
			String favStr = (String) CacheUtil.getNew(key);
			// accountId存在收藏，直接返回， 如果不存在，接下来继续看udid
			if (favStr != null) {
				favChannelIds = new ArrayList<>(Arrays.asList(favStr.split(",")));
				logger.info("get favArticles from ocs by UDID : udid={}, accountId={}, favStr={}",
						udid, accountId, favStr);
			}
		}
		
		// 处理之！！！（增加channelId到第二位，既‘推荐频道’后面）
		if(favChannelIds != null) {
			if(! favChannelIds.contains(channelId)) {
				favChannelIds.add(1, channelId);
			}
			logger.info("favChannels after change, favStr={}", FlowUtil.changeArrayTOString(favChannelIds));
			String key = "article_fav_" + accountId;
			if(isFromUDID) {
				key = "article_fav_" + udid;
			}
			CacheUtil.setNew(key, -1, FlowUtil.changeArrayTOString(favChannelIds));
			return ;
		}
		
		logger.info("根据udid={},accountId={}, 无法查询到有效的收藏", udid, accountId);
		return;
	}
	
	
	public static void main(String[] args) {
		String stationId = "010-7309";
		StationAdEntity entity = new StationAdEntity();
		entity.setId(123123);
		entity.setShowType(ShowType.STATION_ADV.getValue());
		entity.setPic("http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png");
		
		AdTagInfo tagText = new AdTagInfo("搞起", "255,255,255,1", null);
		AdTagInfo tagPic = new AdTagInfo(null, null, "http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png");
		List<AdTagInfo> tags = New.arrayList();
		tags.add(tagText);
		tags.add(tagPic);
		
		AdButtonInfo button0 = new AdButtonInfo("查看", "255,255,255,1", "139,43,43,1", "118,89,89,1", "http://pic1.chelaile.net.cn/adv/brandIcon1187320170922.png");
		AdButtonInfo button1 = new AdButtonInfo(null, null, null, null, "http://pic1.chelaile.net.cn/adv/brandIcon1187320170922.png");
		
		//(int cardType, String topPic, String logo, List<AdTagInfo> tags, String name, String address,
		//Double lng, Double lat, String phoneNum, String link)
		AdCard adCard0 = new AdCard(0, "http://pic1.chelaile.net.cn/adv/ios67326f0f-ebeb-47e0-bce3-99cb78cc02aa.jpg","http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png",
				tags, "路边野店", "银河系，惠中路5号，B座，22层", 116.403931, 39.994642, "15072435749", null);	
		AdCard adCard1 = new AdCard(1, "http://pic1.chelaile.net.cn/adv/ios67326f0f-ebeb-47e0-bce3-99cb78cc02aa.jpg","http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png",
				tags, "路边野店", "银河系，惠中路5号，B座，22层", 116.403931, 39.994642, null, "http://www.baidu.com");	
		AdCard adCard2 = new AdCard(2, "http://pic1.chelaile.net.cn/adv/ios67326f0f-ebeb-47e0-bce3-99cb78cc02aa.jpg","http://pic1.chelaile.net.cn/adv/brandIcon1170620170823.png",
				tags, "路边野店", "银河系，惠中路5号，B座，22层", 116.403931, 39.994642, "15072435749", "http://www.baidu.com");	
		
		
//		BannerInfo(int bannerType, String name, String color, String slogan, String sloganColor, AdTagInfo tag,
//				AdButtonInfo button)
		BannerInfo bannerInfo0 = new BannerInfo(0, "路边野店", "255,255,255,1", "来路边野店，找童年的味道", "255,255,255,1", null, null);
		BannerInfo bannerInfo1 = new BannerInfo(1, "路边野店", "255,255,255,1", "来路边野店，找童年的味道", "255,255,255,1", tagText, null);
		BannerInfo bannerInfo2 = new BannerInfo(2, "路边野店", "255,255,255,1", "来路边野店，找童年的味道", "255,255,255,1", tagPic, null);
		BannerInfo bannerInfo3 = new BannerInfo(3, "路边野店", "255,255,255,1", "来路边野店，找童年的味道", "255,255,255,1", null, button0);
		BannerInfo bannerInfo4 = new BannerInfo(4, "路边野店", "255,255,255,1", "来路边野店，找童年的味道", "255,255,255,1", null, button1);
		
		
		if (stationId.equals("010-8795")) {
			entity.setBannerInfo(bannerInfo0);
		} else if (stationId.equals("010-3343")) {
			entity.setBannerInfo(bannerInfo1);
		} else if (stationId.equals("010-9053")) {
			entity.setBannerInfo(bannerInfo2);
		} else if (stationId.equals("010-7339")) {
			entity.setBannerInfo(bannerInfo3);
			entity.setAdCard(adCard0);
		} else if (stationId.equals("010-5459")) {
			entity.setBannerInfo(bannerInfo4);
			entity.setAdCard(adCard1);
		} else if (stationId.equals("010-7309")) {
			entity.setBannerInfo(bannerInfo3);
			entity.setAdCard(adCard2);
		}
		System.out.println(JSONObject.toJSONString(entity));
	}
}
