package com.bus.chelaile.mvc.rule;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.PositionJs;
import com.bus.chelaile.common.ReplaceJs;
import com.bus.chelaile.common.Text;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.entity.TaskEntity;
import com.bus.chelaile.mvc.AbstractController;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.mvc.utils.JSFileHandle;
import com.bus.chelaile.service.JSService;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.util.New;

@Controller
@RequestMapping("/js/android/js/rule")
public class JsRule extends AbstractController {

	protected static final Logger logger = LoggerFactory.getLogger(JsRule.class);
	@Autowired
	private JSService jSService;

	// private static final Logger logger = LoggerFactory.getLogger(JsRule.class);

	// for test
	@ResponseBody
	@RequestMapping(value = "/splash2.js", produces = "text/html;charset=UTF-8")
	public String splash2(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		return "hello_1111111111你好 啊";
	}

	/*
	 * 开屏
	 */

	@RequestMapping(value = "/splashAd.do", produces = "text/plain;charset=UTF-8")
	public void splashAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam p = getActionParam(request);

		String traceInfo = JSONObject.toJSONString(p);

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("splash_origin");
		TaskEntity tgs = jSService.getTask(p, "splash");

		try {
			produceJS(p, splashOrigin, tgs, "splash_origin", request, ShowType.OPEN_SCREEN,response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		response.setHeader("traceId", p.getTraceid());
		response.setHeader("traceIdInfo", traceInfo);
		//return "";
	}

	/*
	 * 首页
	 */

	@RequestMapping(value = "/homeAd.do", produces = "text/plain;charset=UTF-8")
	public void homeAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
	    long t1 = System.currentTimeMillis();
		AdvParam p = getActionParam(request);
		String traceInfo = JSONObject.toJSONString(p);

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("home_origin");
		TaskEntity tgs = jSService.getTask(p, "home");
		// setMaidianParams(p, );

//		try {
//			produceJS(p, splashOrigin, tgs, "home_origin", request, ShowType.DOUBLE_COLUMN,response.getWriter());
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//		}

		response.setHeader("traceId", p.getTraceid());
		response.setHeader("traceIdInfo", traceInfo);
		logger.info("total cost time: {}", System.currentTimeMillis() - t1);
//		return "";
	}

	/*
	 * 详情页右上角
	 */

	@RequestMapping(value = "/rightTopAd.do", produces = "text/plain;charset=UTF-8")
	public void rightTopAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("right_origin");
		TaskEntity tgs = jSService.getTask(p, "rightTop");

		try {
			produceJS(p, splashOrigin, tgs, "right_origin", request, ShowType.LINE_RIGHT_ADV,response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		// response.getWriter().write(s);
		String traceInfo = JSONObject.toJSONString(p);
		response.setHeader("traceId", p.getTraceid());
		response.setHeader("traceIdInfo", traceInfo);
		//return "";
	}

	/*
	 * 站点位置
	 */

	@RequestMapping(value = "/stationAd.do", produces = "text/plain;charset=UTF-8")
	public void stationAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("station_origin");
		TaskEntity tgs = jSService.getTask(p, "station");

		try {
			produceJS(p, splashOrigin, tgs, "station_origin", request, ShowType.STATION_ADV,response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		String traceInfo = JSONObject.toJSONString(p);
		response.setHeader("traceId", p.getTraceid());
		response.setHeader("traceIdInfo", traceInfo);
	//	return "";
	}

	/*
	 * 详情页底部
	 */

	@RequestMapping(value = "/bottomAd.do", produces = "text/plain;charset=UTF-8")
	public void bottomAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("bottom_origin");
		TaskEntity tgs = jSService.getTask(p, "bottom");

		try {
			produceJS(p, splashOrigin, tgs, "bottom_origin", request, ShowType.LINE_FEED_ADV,response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		String traceInfo = JSONObject.toJSONString(p);
		response.setHeader("traceId", p.getTraceid());
		response.setHeader("traceIdInfo", traceInfo);
	//	return "";
	}

	/*
	 * 换乘
	 */

	@RequestMapping(value = "/transfer.do", produces = "text/plain;charset=UTF-8")
	public void transfer(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("transfer_origin");
		TaskEntity tgs = jSService.getTask(p, "transfer");

		try {
			produceJS(p, splashOrigin, tgs, "transfer_origin", request, ShowType.TRANSFER_ADV, response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		String traceInfo = JSONObject.toJSONString(p);
		response.setHeader("traceId", p.getTraceid());
		response.setHeader("traceIdInfo", traceInfo);
		//return "";
	}

	//
	/*
	 * 同站线路
	 */

	@RequestMapping(value = "/stationDetail.do", produces = "text/plain;charset=UTF-8")
	public void getStationLine(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("stationDetail_origin");

		TaskEntity tgs = jSService.getTask(p, "stationDetail");

		try {
			produceJS(p, splashOrigin, tgs, "stationDetail_origin", request, ShowType.CAR_ALL_LINE_ADV,
					response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		String traceInfo = JSONObject.toJSONString(p);
		response.setHeader("traceId", p.getTraceid());
		response.setHeader("traceIdInfo", traceInfo);

		//return "";
	}

	//
	// /*
	// * 更多车辆
	// */
	//@ResponseBody
	@RequestMapping(value = "/allCars.do", produces = "text/plain;charset=UTF-8")
	public void allCars(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("allCars_origin");

		// logger.info("splashOrigin={}",splashOrigin);

		TaskEntity tgs = jSService.getTask(p, "allCars");

		try {
			produceJS(p, splashOrigin, tgs, "allCars_origin", request, ShowType.ALL_CAR_ADV, response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		String traceInfo = JSONObject.toJSONString(p);
		response.setHeader("traceId", p.getTraceid());
		response.setHeader("traceIdInfo", traceInfo);
	//	return "";
	}

	private void produceJS(AdvParam p, String originJs, TaskEntity tgs, String tag, HttpServletRequest request,
			ShowType showType, Writer writer) {
		if (StringUtils.isBlank(originJs)) {
			return;
			// return "originjs file is null ";
		}

		if (tgs == null || tgs.getTaskGroups() == null || tgs.getTaskGroups().getTasks().size() == 0) {
			logger.info("tgs is null");
			return;
		}
		
		
		Map<String, String> map = New.hashMap();

		long tBeginService = System.currentTimeMillis();
		if (tgs != null) {
			logger.info(tgs.getTaskGroups().getTasks().toString());

			map.put("TASKS", tgs.getTaskGroups().getTasks().toString());
			map.put("TIMEOUTS", tgs.getTaskGroups().getTimeouts().toString());
			if (StringUtils.isNoneBlank(tgs.getTraceid())) {
				map.put("TRACEID", tgs.getTraceid());
			}

			for (List<String> tasks : tgs.getTaskGroups().getTasks()) {
				for (String task : tasks) {
					if (task.contains("api_chelaile")) {
						map.put("QUERY_STRING", request.getQueryString());
						map.put("API_CHELAILE_DATA", tgs.getAdDataString());
					}
				}
			}

			List<Text> list = StaticAds.NEW_JS_FILE_STR.get(tag);

			// logger.info("map.size={}",map.size());

			JSFileHandle.replaceNewJs(p.getS(), showType, tgs, tag, map);

			try {
				ReplaceJs.getNewReplaceStr(list, map, writer);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("replaceJs cost time: udid={}, showType={}, cost={}", p.getUdid(), showType.getType(), 
	                System.currentTimeMillis() - tBeginService);
			// return ReplaceJs.getReplaceStr(list, map);
		}
		// return tag;
	}
	// String splashJS = "";
	// if (tgs != null) {
	// splashJS = originJs.replace("${TASKS}",
	// tgs.getTaskGroups().getTasks().toString());
	// splashJS = splashJS.replace("${TIMEOUTS}",
	// tgs.getTaskGroups().getTimeouts().toString());
	// if (StringUtils.isNoneBlank(tgs.getTraceid())) {
	// splashJS = splashJS.replace("${TRACEID}", tgs.getTraceid());
	// }
	//
	// for (List<String> tasks : tgs.getTaskGroups().getTasks()) {
	// for (String task : tasks) {
	// if (task.contains("api_chelaile")) {
	// splashJS = splashJS.replace("${QUERY_STRING}", request.getQueryString());
	// splashJS = splashJS.replace("${API_CHELAILE_DATA}", tgs.getAdDataString());
	// }
	// }
	// }
	// }

	// return replaceJs(splashJS, showType, tgs, tag);
	// return JSFileHandle.replaceJs(p.getS(), splashJS, showType, tgs, tag);

	// }

	/*
	 * private String replaceJs(String splashJS, ShowType showType, TaskEntity tgs,
	 * String tag) { // 这里要替换placementid,displayType 也要替换成默认值 String
	 * sdk_gdt_placementId = null; String sdk_toutiao_placementId = null; String
	 * sdk_voicead_placementId = null; String sdk_baidu_placementId = null;
	 * 
	 * String sdk_gdt_displayType = null; String sdk_toutiao_displayType = null;
	 * String sdk_voicead_displayType = null; String sdk_baidu_displayType = null;
	 * 
	 * Map<String, String> map = null; if (tgs != null && tgs.getTaskGroups() !=
	 * null && tgs.getTaskGroups().getMap() != null) { map =
	 * tgs.getTaskGroups().getMap(); } else { map = New.hashMap(); }
	 * 
	 * try { logger.info("map={}", JsonBinder.toJson(map, JsonBinder.always)); }
	 * catch (Exception e) { logger.error(e.getMessage()); } // 广点通
	 * sdk_gdt_displayType = map.get("sdk_gdt_displayType"); if (sdk_gdt_displayType
	 * != null) { map.put("sdk_gdt_displayType", sdk_gdt_displayType); int type =
	 * Integer.parseInt(sdk_gdt_displayType); sdk_gdt_placementId =
	 * getPlaceMentId(showType, "2", type); map.put("sdk_gdt_placementId",
	 * sdk_gdt_placementId); map.put("sdk_gdt_aid", type + ""); } else {
	 * map.put("sdk_gdt_displayType", "2"); sdk_gdt_placementId =
	 * getPlaceMentId(showType, "2", 2); map.put("sdk_gdt_placementId",
	 * sdk_gdt_placementId); map.put("sdk_gdt_aid", "2"); }
	 * 
	 * // 头条 sdk_toutiao_displayType = map.get("sdk_toutiao_displayType"); if
	 * (sdk_toutiao_displayType != null) { map.put("sdk_toutiao_displayType",
	 * sdk_toutiao_displayType); int type =
	 * Integer.parseInt(sdk_toutiao_displayType); sdk_toutiao_placementId =
	 * getPlaceMentId(showType, "7", type); map.put("sdk_toutiao_placementId",
	 * sdk_toutiao_placementId); map.put("sdk_toutiao_aid", type + ""); } else {
	 * map.put("sdk_toutiao_displayType", "2"); sdk_toutiao_placementId =
	 * getPlaceMentId(showType, "7", 2); map.put("sdk_toutiao_placementId",
	 * sdk_toutiao_placementId); map.put("sdk_toutiao_aid", "2"); }
	 * 
	 * // 科大讯飞 sdk_voicead_displayType = map.get("sdk_voicead_displayType"); if
	 * (sdk_voicead_displayType != null) { map.put("sdk_voicead_displayType",
	 * sdk_voicead_displayType); int type =
	 * Integer.parseInt(sdk_voicead_displayType); sdk_voicead_placementId =
	 * getPlaceMentId(showType, "10", type); map.put("sdk_voicead_placementId",
	 * sdk_voicead_placementId); map.put("sdk_voicead_aid", type + "");
	 * map.put("sdk_ifly_aid", type + ""); map.put("sdk_ifly_placementId",
	 * sdk_voicead_placementId); } else { map.put("sdk_voicead_displayType", "2");
	 * sdk_voicead_placementId = getPlaceMentId(showType, "10", 2);
	 * map.put("sdk_voicead_placementId", sdk_voicead_placementId);
	 * map.put("sdk_voicead_aid", "2"); map.put("sdk_ifly_aid", "2");
	 * map.put("sdk_ifly_placementId", sdk_voicead_placementId); }
	 * 
	 * String sdk_ifly_displayType = map.get("sdk_ifly_displayType"); if
	 * (sdk_ifly_displayType != null) { map.put("sdk_voicead_displayType",
	 * sdk_ifly_displayType); int type = Integer.parseInt(sdk_ifly_displayType);
	 * sdk_voicead_placementId = getPlaceMentId(showType, "10", type);
	 * map.put("sdk_voicead_placementId", sdk_voicead_placementId);
	 * map.put("sdk_voicead_aid", type + ""); map.put("sdk_ifly_aid", type + "");
	 * map.put("sdk_ifly_placementId", sdk_voicead_placementId); } else {
	 * map.put("sdk_voicead_displayType", "2"); sdk_voicead_placementId =
	 * getPlaceMentId(showType, "10", 2); map.put("sdk_voicead_placementId",
	 * sdk_voicead_placementId); map.put("sdk_voicead_aid", "2");
	 * map.put("sdk_ifly_aid", "2"); map.put("sdk_ifly_placementId",
	 * sdk_voicead_placementId); }
	 * 
	 * // baidu sdk_baidu_displayType = map.get("sdk_baidu_displayType"); if
	 * (sdk_baidu_displayType != null) { map.put("sdk_baidu_displayType",
	 * sdk_baidu_displayType); int type = Integer.parseInt(sdk_baidu_displayType);
	 * sdk_baidu_placementId = getPlaceMentId(showType, "5", type);
	 * map.put("sdk_baidu_placementId", sdk_baidu_placementId);
	 * 
	 * map.put("sdk_baidu_aid", type + ""); } else {
	 * map.put("sdk_baidu_displayType", "2"); sdk_baidu_placementId =
	 * getPlaceMentId(showType, "5", 2); map.put("sdk_baidu_placementId",
	 * sdk_baidu_placementId);
	 * 
	 * map.put("sdk_baidu_aid", "2"); }
	 * 
	 * // 科大讯飞api String api_voicead_displayType =
	 * map.get("api_voicead_displayType"); if (api_voicead_displayType != null) {
	 * map.put("api_voicead_displayType", api_voicead_displayType); int type =
	 * Integer.parseInt(api_voicead_displayType); String api_voicead_placementId =
	 * getApiPlaceMentId(showType, type, "api_voicead");
	 * map.put("api_voicead_placementId", api_voicead_placementId);
	 * 
	 * map.put("api_voicead_aid", type + ""); } else {
	 * map.put("api_voicead_displayType", "2"); String api_voicead_placementId =
	 * getApiPlaceMentId(showType, 2, "api_voicead");
	 * map.put("api_voicead_placementId", api_voicead_placementId);
	 * 
	 * map.put("api_voicead_aid", "2"); }
	 * 
	 * // 有道api String api_yd_displayType = map.get("api_yd_displayType"); if
	 * (api_yd_displayType != null) { map.put("api_yd_displayType",
	 * api_yd_displayType); int type = Integer.parseInt(api_yd_displayType);
	 * map.put("api_yd_aid", type + ""); } else { map.put("api_yd_displayType",
	 * "2"); map.put("api_yd_aid", "2"); }
	 * 
	 * // 舜飞api String api_shunfei_displayType = map.get("api_shunfei_displayType");
	 * if (api_shunfei_displayType != null) { map.put("api_shunfei_displayType",
	 * api_shunfei_displayType); int type =
	 * Integer.parseInt(api_shunfei_displayType); map.put("api_shunfei_aid", type +
	 * ""); } else { map.put("api_shunfei_displayType", "2");
	 * map.put("api_shunfei_aid", "2"); }
	 * 
	 * // 众盟api String api_zm_displayType = map.get("api_zm_displayType"); if
	 * (api_zm_displayType != null) { map.put("api_zm_displayType",
	 * api_zm_displayType); int type = Integer.parseInt(api_zm_displayType);
	 * map.put("api_zm_aid", type + ""); } else { map.put("api_zm_displayType",
	 * "2"); map.put("api_zm_aid", "2"); } // try { // logger.info("json1={}",
	 * JsonBinder.toJson(map, JsonBinder.always)); // } catch (Exception e) { //
	 * logger.error(e.getMessage()); // }
	 * 
	 * if (tgs.getTaskGroups().getMap() != null) { for (Map.Entry<String, String>
	 * entry : tgs.getTaskGroups().getMap().entrySet()) { splashJS =
	 * splashJS.replace("${" + entry.getKey() + "}", entry.getValue()); } }
	 * 
	 * for (Map.Entry<String, String> entry : map.entrySet()) { splashJS =
	 * splashJS.replace("${" + entry.getKey() + "}", entry.getValue()); }
	 * 
	 * // logger.info(splashJS);
	 * 
	 * return splashJS; }
	 * 
	 * private String getApiPlaceMentId(ShowType showType, int displayType, String
	 * apiName) { String placeMentId = "111";
	 * 
	 * // 双栏 if (showType.getValue() == ShowType.DOUBLE_COLUMN.getValue()) {
	 * 
	 * // 科大讯飞 if (apiName.equals("api_voicead")) { if (displayType == 3) {
	 * placeMentId = "ACB0BED305BBA908DEA75B0036E51ECE"; } else { placeMentId =
	 * "C23BFCFFE1F3D8D5C06D7E1AEEA83812"; } }
	 * 
	 * }
	 * 
	 * // 详情页底部 else if (showType.getValue() == ShowType.LINE_FEED_ADV.getValue()) {
	 * 
	 * // 科大讯飞 if (apiName.equals("api_voicead")) { if (displayType == 3) {
	 * placeMentId = "FD95828C09A32D712082DC08D36CC15D"; } else { placeMentId =
	 * "5CBF4E804C06EBF6EEAF93DC5EA6BBCF"; } } else if (apiName.equals("api_yd")) {
	 * placeMentId = ""; }
	 * 
	 * }
	 * 
	 * // 换乘 else if (showType.getValue() == ShowType.TRANSFER_ADV.getValue()) {
	 * 
	 * // 科大讯飞 if (apiName.equals("api_voicead")) { if (displayType == 3) {
	 * placeMentId = "AE9A21B49A87F9B6C67EC7548FB5DE48"; } else { placeMentId =
	 * "A7ABF4CFA257C79F064A8A162266D924"; } } // 网易 else if
	 * (apiName.equals("api_yd")) { placeMentId = ""; }
	 * 
	 * }
	 * 
	 * // 车辆所有线路 else if (showType.getValue() ==
	 * ShowType.CAR_ALL_LINE_ADV.getValue()) {
	 * 
	 * // 科大讯飞 if (apiName.equals("api_voicead")) { if (displayType == 3) {
	 * placeMentId = "67842F384D42AE16958A2ADDC8080BA0"; } else { placeMentId =
	 * "3D7818F4980FB323DAEA8B544B567B7C"; } } // 网易 else if
	 * (apiName.equals("api_yd")) { placeMentId = ""; }
	 * 
	 * } // 更多车辆 else if (showType.getValue() == ShowType.ALL_CAR_ADV.getValue()) {
	 * 
	 * // 科大讯飞 if (apiName.equals("api_voicead")) { if (displayType == 3) {
	 * placeMentId = "B7880FAC93B6C405F219E54DABBAD2A3"; } else { placeMentId =
	 * "BEAD9E183E07E296B1855E2AB3F2E6ED"; } }
	 * 
	 * // 网易 else if (apiName.equals("api_yd")) { placeMentId = ""; }
	 * 
	 * }
	 * 
	 * return placeMentId; }
	 */

	/*
	 * private String getPlaceMentId(ShowType showType, String provider_id, int
	 * displayType) {
	 * 
	 * String placeMentId = "111";
	 * 
	 * // 开屏 if (showType.getValue() == ShowType.OPEN_SCREEN.getValue()) { // 广点通 if
	 * (provider_id.equals("2")) { placeMentId = "7030038393106222"; } // innobe
	 * else if (provider_id.equals("3")) { placeMentId = ""; } // 今日头条 else if
	 * (provider_id.equals("7")) { placeMentId = "800673832"; } // 科大讯飞 else if
	 * (provider_id.equals("10")) { placeMentId =
	 * "D028C0ADDDBC38952DA01241B4939E64"; } // 网易 else if
	 * (provider_id.equals("11")) { placeMentId = ""; }
	 * 
	 * } // 双栏 else if (showType.getValue() == ShowType.DOUBLE_COLUMN.getValue()) {
	 * // 广点通 if (provider_id.equals("2")) { if (displayType == 3) { placeMentId =
	 * "6020731595504836"; } else { placeMentId = "2030539481050032"; } } // innobe
	 * else if (provider_id.equals("3")) { placeMentId = ""; } // 今日头条 else if
	 * (provider_id.equals("7")) { if (displayType == 3) { placeMentId =
	 * "900673292"; } else { placeMentId = "900673519"; } } // 科大讯飞 else if
	 * (provider_id.equals("10")) { if (displayType == 3) { placeMentId =
	 * "ACB0BED305BBA908DEA75B0036E51ECE"; } else { placeMentId =
	 * "C23BFCFFE1F3D8D5C06D7E1AEEA83812"; } } // 网易 else if
	 * (provider_id.equals("11")) { placeMentId = ""; } // 百度 else if
	 * (provider_id.equals("5")) { if (displayType == 3) { placeMentId = "5847843";
	 * } else { placeMentId = "5826173"; } }
	 * 
	 * } // 站点 else if (showType.getValue() == ShowType.STATION_ADV.getValue()) { //
	 * 广点通 if (provider_id.equals("2")) { placeMentId = "6000631364333392"; } //
	 * innobe else if (provider_id.equals("3")) { placeMentId = ""; } // 今日头条 else
	 * if (provider_id.equals("7")) { placeMentId = "900673616"; } // 科大讯飞 else if
	 * (provider_id.equals("10")) {
	 * 
	 * } // 网易 else if (provider_id.equals("11")) { placeMentId = ""; }
	 * 
	 * } // 详情页底部 else if (showType.getValue() == ShowType.LINE_FEED_ADV.getValue())
	 * { // 广点通 if (provider_id.equals("2")) { if (displayType == 3) { placeMentId =
	 * "9080635585600817"; } else { placeMentId = "3040333351258521"; } } // innobe
	 * else if (provider_id.equals("3")) { placeMentId = ""; } // 今日头条 else if
	 * (provider_id.equals("7")) { if (displayType == 3) { placeMentId =
	 * "900673814"; } else { placeMentId = "900673326"; } } // 科大讯飞 else if
	 * (provider_id.equals("10")) { if (displayType == 3) { placeMentId =
	 * "FD95828C09A32D712082DC08D36CC15D"; } else { placeMentId =
	 * "5CBF4E804C06EBF6EEAF93DC5EA6BBCF"; } } // 网易 else if
	 * (provider_id.equals("11")) { placeMentId = ""; } // 百度 else if
	 * (provider_id.equals("5")) { if (displayType == 3) { placeMentId = "5847849";
	 * } else { placeMentId = "5826174"; } }
	 * 
	 * }
	 * 
	 * // 右上角 else if (showType.getValue() == ShowType.LINE_RIGHT_ADV.getValue()) {
	 * // 广点通 if (provider_id.equals("2")) { placeMentId = "4060239431859044"; } //
	 * innobe else if (provider_id.equals("3")) { placeMentId = ""; } // 今日头条 else
	 * if (provider_id.equals("7")) { placeMentId = "900673291"; } // 科大讯飞 else if
	 * (provider_id.equals("10")) { placeMentId =
	 * "2EC979D4F845F81DD899B62F497E3F67"; } // 网易 else if
	 * (provider_id.equals("11")) { placeMentId = ""; }
	 * 
	 * }
	 * 
	 * // 换乘 else if (showType.getValue() == ShowType.TRANSFER_ADV.getValue()) { //
	 * 广点通 if (provider_id.equals("2")) { if (displayType == 3) { placeMentId =
	 * "7090134575505819"; } else { placeMentId = "3010534505808848"; } } // innobe
	 * else if (provider_id.equals("3")) { placeMentId = ""; } // 今日头条 else if
	 * (provider_id.equals("7")) { if (displayType == 3) { placeMentId =
	 * "900673297"; } else { placeMentId = "900673966"; } } // 科大讯飞 else if
	 * (provider_id.equals("10")) { if (displayType == 3) { placeMentId =
	 * "AE9A21B49A87F9B6C67EC7548FB5DE48"; } else { placeMentId =
	 * "A7ABF4CFA257C79F064A8A162266D924"; } } // 网易 else if
	 * (provider_id.equals("11")) { placeMentId = ""; }
	 * 
	 * // 百度 else if (provider_id.equals("5")) { if (displayType == 3) { placeMentId
	 * = "5847855"; } else if (displayType == 4) { placeMentId = "5847852"; } else {
	 * placeMentId = "5847852"; } }
	 * 
	 * }
	 * 
	 * // 车辆所有线路 else if (showType.getValue() ==
	 * ShowType.CAR_ALL_LINE_ADV.getValue()) { // 广点通 if (provider_id.equals("2")) {
	 * if (displayType == 3) { placeMentId = "2080634575306901"; } else {
	 * placeMentId = "5030836525008930"; } } // innobe else if
	 * (provider_id.equals("3")) { placeMentId = ""; } // 今日头条 else if
	 * (provider_id.equals("7")) { if (displayType == 3) { placeMentId =
	 * "900673492"; } else { placeMentId = "900673424"; } } // 科大讯飞 else if
	 * (provider_id.equals("10")) { if (displayType == 3) { placeMentId =
	 * "67842F384D42AE16958A2ADDC8080BA0"; } else { placeMentId =
	 * "3D7818F4980FB323DAEA8B544B567B7C"; } } // 网易 else if
	 * (provider_id.equals("11")) { placeMentId = ""; }
	 * 
	 * // 百度 else if (provider_id.equals("5")) { if (displayType == 3) { placeMentId
	 * = "5847859"; } else if (displayType == 4) { placeMentId = "5847856"; } else {
	 * placeMentId = "5847856"; } }
	 * 
	 * } // 更多车辆 else if (showType.getValue() == ShowType.ALL_CAR_ADV.getValue()) {
	 * // 广点通 if (provider_id.equals("2")) { if (displayType == 3) { placeMentId =
	 * "4070030575903993"; } else { placeMentId = "7000631515604932"; } } // innobe
	 * else if (provider_id.equals("3")) { placeMentId = ""; } // 今日头条 else if
	 * (provider_id.equals("7")) { if (displayType == 3) { placeMentId =
	 * "900673101"; } else { placeMentId = "900673512"; } } // 科大讯飞 else if
	 * (provider_id.equals("10")) { if (displayType == 3) { placeMentId =
	 * "BEAD9E183E07E296B1855E2AB3F2E6ED"; } else { placeMentId =
	 * "B7880FAC93B6C405F219E54DABBAD2A3"; } }
	 * 
	 * // 网易 else if (provider_id.equals("11")) { placeMentId = ""; } // 百度 else if
	 * (provider_id.equals("5")) { if (displayType == 3) { placeMentId = "5847867";
	 * } else if (displayType == 4) { placeMentId = "5847862"; } else { placeMentId
	 * = "5847862"; } }
	 * 
	 * }
	 * 
	 * return placeMentId; }
	 */

}
