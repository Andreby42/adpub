package com.bus.chelaile.mvc.rule;

import java.io.IOException;

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

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.entity.TaskEntity;
import com.bus.chelaile.mvc.AbstractController;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.JSService;
import com.bus.chelaile.service.StaticAds;

@Controller
@RequestMapping("/js/ios/js/rule")
public class IosJsRule extends AbstractController {

	@Autowired
	private JSService jSService;

	private static final Logger logger = LoggerFactory.getLogger(IosJsRule.class);

	/*
	 * 开屏
	 */
	@ResponseBody
	@RequestMapping(value = "/splashAd.do", produces = "text/plain;charset=UTF-8")
	public void splashAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam p = getActionParam(request);
		response.setContentType("application/javascript;charset=UTF-8");

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("ios_splash_origin");
		
		TaskEntity tgs = jSService.getTask(p, "splash");

		response.setHeader("traceId", p.getTraceid());
		try {
            produceJS(p, splashOrigin, tgs, "ios_splash_origin", request, ShowType.OPEN_SCREEN,response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
	}

	/*
	 * 首页
	 */
	@ResponseBody
	@RequestMapping(value = "/homeAd.do", produces = "text/plain;charset=UTF-8")
	public void homeAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

	    response.setContentType("application/javascript;charset=UTF-8");
		AdvParam p = getActionParam(request);

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("ios_home_origin");
		TaskEntity tgs = jSService.getTask(p, "home");
		// setMaidianParams(p, );

		response.setHeader("traceId", p.getTraceid());
		try {
            produceJS(p, splashOrigin, tgs, "ios_home_origin", request, ShowType.DOUBLE_COLUMN,response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
	}

	/*
	 * 详情页右上角
	 */
	@ResponseBody
	@RequestMapping(value = "/rightTopAd.do", produces = "text/plain;charset=UTF-8")
	public void rightTopAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

	    response.setContentType("application/javascript;charset=UTF-8");
		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("ios_right_origin");
		TaskEntity tgs = jSService.getTask(p, "rightTop");

		response.setHeader("traceId", p.getTraceid());
        try {
            produceJS(p, splashOrigin, tgs, "ios_right_origin", request, ShowType.LINE_RIGHT_ADV,response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
	}

	/*
	 * 站点位置
	 */
	@ResponseBody
	@RequestMapping(value = "/stationAd.do", produces = "text/plain;charset=UTF-8")
	public void stationAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

	    response.setContentType("application/javascript;charset=UTF-8");
		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("ios_station_origin");
		TaskEntity tgs = jSService.getTask(p, "station");

        response.setHeader("traceId", p.getTraceid());
        try {
            produceJS(p, splashOrigin, tgs, "ios_station_origin", request, ShowType.STATION_ADV,response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
	}

	/*
	 * 详情页底部
	 */
	@ResponseBody
	@RequestMapping(value = "/bottomAd.do", produces = "text/plain;charset=UTF-8")
	public void bottomAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

	    response.setContentType("application/javascript;charset=UTF-8");
		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("ios_bottom_origin");
		TaskEntity tgs = jSService.getTask(p, "bottom");

        response.setHeader("traceId", p.getTraceid());
        try {
            produceJS(p, splashOrigin, tgs, "ios_bottom_origin", request, ShowType.LINE_FEED_ADV,response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
	}

	/*
	 * 换乘
	 */
	@ResponseBody
	@RequestMapping(value = "/transfer.do", produces = "text/plain;charset=UTF-8")
	public void transfer(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

	    response.setContentType("application/javascript;charset=UTF-8");
		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("ios_transfer_origin");
		TaskEntity tgs = jSService.getTask(p, "transfer");

        response.setHeader("traceId", p.getTraceid());
        try {
            produceJS(p, splashOrigin, tgs, "ios_transfer_origin", request, ShowType.TRANSFER_ADV, response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
	}

	//
	/*
	 * 同站线路
	 */
	@ResponseBody
	@RequestMapping(value = "/stationDetail.do", produces = "text/plain;charset=UTF-8")
	public void getStationLine(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

	    response.setContentType("application/javascript;charset=UTF-8");
		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("ios_stationDetail_origin");
		
		TaskEntity tgs = jSService.getTask(p, "stationDetail");

        response.setHeader("traceId", p.getTraceid());
        try {
            produceJS(p, splashOrigin, tgs, "ios_stationDetail_origin", request, ShowType.CAR_ALL_LINE_ADV,
                    response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
	}

	//
	// /*
	// * 更多车辆
	// */
	@ResponseBody
	@RequestMapping(value = "/allCars.do", produces = "text/plain;charset=UTF-8")
	public void allCars(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

	    response.setContentType("application/javascript;charset=UTF-8");
		AdvParam p = getActionParam(request);
		if (StringUtils.isBlank(p.getStnName()))
			p.setStnName(request.getParameter("stationName"));

		// 模板
		String splashOrigin = StaticAds.JS_FILE_STR.get("ios_allCars_origin");

		// logger.info("splashOrigin={}",splashOrigin);

		TaskEntity tgs = jSService.getTask(p, "allCars");

        response.setHeader("traceId", p.getTraceid());
        try {
            produceJS(p, splashOrigin, tgs, "ios_allCars_origin", request, ShowType.ALL_CAR_ADV, response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
	}
	
	
	// 插屏的四个
    @RequestMapping(value = "/interstitialHomeAd.do", produces = "application/javascript;charset=UTF-8")
    public void getInterstitialHomeAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        AdvParam p = getActionParam(request);
        if (StringUtils.isBlank(p.getStnName()))
            p.setStnName(request.getParameter("stationName"));

        // 模板
        String splashOrigin = StaticAds.JS_FILE_STR.get("ios_interstitialHome_origin");

        // logger.info("splashOrigin={}",splashOrigin);
        TaskEntity tgs = jSService.getTask(p, "interstitialHome");

        response.setContentType("application/javascript;charset=UTF-8");
        response.setHeader("traceId", p.getTraceid());
        try {
            produceJS(p, splashOrigin, tgs, "ios_interstitialHome_origin", request, ShowType.INTERSHOME_ADV, response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        //  return "";
    }
    
    @RequestMapping(value = "/interstitialTransitAd.do", produces = "application/javascript;charset=UTF-8")
    public void getInterstitialTransitAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        AdvParam p = getActionParam(request);
        if (StringUtils.isBlank(p.getStnName()))
            p.setStnName(request.getParameter("stationName"));

        // 模板
        String splashOrigin = StaticAds.JS_FILE_STR.get("ios_interstitialTransit_origin");

        // logger.info("splashOrigin={}",splashOrigin);
        TaskEntity tgs = jSService.getTask(p, "interstitialTransit");

        response.setContentType("application/javascript;charset=UTF-8");
        response.setHeader("traceId", p.getTraceid());
        try {
            produceJS(p, splashOrigin, tgs, "ios_interstitialTransit_origin", request, ShowType.INTERSTRANSIT_ADV, response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        //  return "";
    }
    
    @RequestMapping(value = "/interstitialEnergyAd.do", produces = "application/javascript;charset=UTF-8")
    public void getInterstitialEnergyAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        AdvParam p = getActionParam(request);
        if (StringUtils.isBlank(p.getStnName()))
            p.setStnName(request.getParameter("stationName"));

        // 模板
        String splashOrigin = StaticAds.JS_FILE_STR.get("ios_interstitialEnergy_origin");

        // logger.info("splashOrigin={}",splashOrigin);
        TaskEntity tgs = jSService.getTask(p, "interstitialEnergy");

        response.setContentType("application/javascript;charset=UTF-8");
        response.setHeader("traceId", p.getTraceid());
        try {
            produceJS(p, splashOrigin, tgs, "ios_interstitialEnergy_origin", request, ShowType.INTERSENERGY_ADV, response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        //  return "";
    }
    
    @RequestMapping(value = "/interstitialMineAd.do", produces = "application/javascript;charset=UTF-8")
    public void getInterstitialMineAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        AdvParam p = getActionParam(request);
        if (StringUtils.isBlank(p.getStnName()))
            p.setStnName(request.getParameter("stationName"));

        // 模板
        String splashOrigin = StaticAds.JS_FILE_STR.get("ios_interstitialMine_origin");

        // logger.info("splashOrigin={}",splashOrigin);
        TaskEntity tgs = jSService.getTask(p, "interstitialMine");

        response.setContentType("application/javascript;charset=UTF-8");
        response.setHeader("traceId", p.getTraceid());
        try {
            produceJS(p, splashOrigin, tgs, "ios_interstitialMine_origin", request, ShowType.INTERSMINE_ADV, response.getWriter());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        //  return "";
    }

//	private void produceJS(AdvParam p, String originJs, TaskEntity tgs, String tag, HttpServletRequest request,
//            ShowType showType) {
//        if (StringUtils.isBlank(originJs)) {
//            return "┭┮﹏┭┮ 原始js文件为空 ";
//        }
//        
//        if(tgs == null || tgs.getTaskGroups() == null || tgs.getTaskGroups().getTasks().size() == 0) {
//            // TODO 
//        }
//
//
//        String splashJS = "";
//        if (tgs != null) {
//            splashJS = originJs.replace("${TASKS}", tgs.getTaskGroups().getTasks().toString());
//            splashJS = splashJS.replace("${TIMEOUTS}", tgs.getTaskGroups().getTimeouts().toString());
//            if (StringUtils.isNoneBlank(tgs.getTraceid())) {
//                splashJS = splashJS.replace("${TRACEID}", tgs.getTraceid());
//            }
//
//            for (List<String> tasks : tgs.getTaskGroups().getTasks()) {
//                for (String task : tasks) {
//                    if (task.contains("api_chelaile")) {
//                        splashJS = splashJS.replace("${QUERY_STRING}", request.getQueryString());
//                        splashJS = splashJS.replace("${API_CHELAILE_DATA}", tgs.getAdDataString());
//                    }
//                }
//            }
//        }
//
//        //		return replaceJs(splashJS, showType, tgs, tag);
//        return JSFileHandle.replaceJs(p.getS(), splashJS, showType, tgs, tag);
//
//    }

}
