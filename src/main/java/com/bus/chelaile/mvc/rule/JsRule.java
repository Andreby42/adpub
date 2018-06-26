package com.bus.chelaile.mvc.rule;

import java.util.List;

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
import com.bus.chelaile.model.ads.entity.TaskEntity;
import com.bus.chelaile.mvc.AbstractController;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.JSService;
import com.bus.chelaile.service.StaticAds;

@Controller
@RequestMapping("/js/android/js/rule")
public class JsRule extends AbstractController {

    @Autowired
    private JSService jSService;

    private static final Logger logger = LoggerFactory.getLogger(JsRule.class);

    // for test
    @ResponseBody
    @RequestMapping(value="/splash.do", produces = "text/plain;charset=UTF-8")
    public String splash(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        return "hello_1111111111你好 啊";
    }
    
    
 // for test
    @ResponseBody
    @RequestMapping(value="/splash1.js", produces = "text/plain;charset=UTF-8")
    public String splash1(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        return "hello_1111111111你好 啊";
    }
    
    // for test
    @ResponseBody
    @RequestMapping(value="/splash2.js", produces = "text/html;charset=UTF-8")
    public String splash2(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        return "hello_1111111111你好 啊";
    }
    

    /*
     * 开屏
     */
    @ResponseBody
    @RequestMapping(value = "/splashAd.do", produces = "text/plain;charset=UTF-8")
    public String splashAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        
        String traceInfo = JSONObject.toJSONString(p);
        
        
        
        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("splash_origin");
        TaskEntity tgs = jSService.getTask(p, "splash");

        String splashJS = produceJS(p, splashOrigin, tgs, "splash_", request);

        
        response.setHeader("traceId", p.getTraceid());
        response.setHeader("traceIdInfo", traceInfo);
        return splashJS;
    }
    
    /*
     * 首页
     */
    @ResponseBody
    @RequestMapping(value = "/homeAd.do", produces = "text/plain;charset=UTF-8")
    public String homeAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        String traceInfo = JSONObject.toJSONString(p);
       

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("home_origin");
        TaskEntity tgs = jSService.getTask(p, "home");
//        setMaidianParams(p, );

        String splashJS = produceJS(p, splashOrigin, tgs, "home_", request);
        
        response.setHeader("traceId", p.getTraceid());
        response.setHeader("traceIdInfo", traceInfo);

        return splashJS;
    }

    
    /*
     * 详情页右上角
     */
    @ResponseBody
    @RequestMapping(value = "/rightTopAd.do", produces = "text/plain;charset=UTF-8")
    public String rightTopAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        if(StringUtils.isBlank(p.getStnName()))
            p.setStnName(request.getParameter("stationName"));

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("right_origin");
        TaskEntity tgs = jSService.getTask(p, "rightTop");

        String splashJS = produceJS(p, splashOrigin, tgs, "right_", request);

        return splashJS;
    }
    
    
    /*
     * 站点位置
     */
    @ResponseBody
    @RequestMapping(value = "/stationAd.do", produces = "text/plain;charset=UTF-8")
    public String stationAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        if(StringUtils.isBlank(p.getStnName()))
            p.setStnName(request.getParameter("stationName"));

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("station_origin");
        TaskEntity tgs = jSService.getTask(p, "station");

        String splashJS = produceJS(p, splashOrigin, tgs, "station_", request);

        return splashJS;
    }
    
    
    /*
     * 详情页底部
     */
    @ResponseBody
    @RequestMapping(value = "/bottomAd.do", produces = "text/plain;charset=UTF-8")
    public String bottomAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        if(StringUtils.isBlank(p.getStnName()))
            p.setStnName(request.getParameter("stationName"));

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("bottom_origin");
        TaskEntity tgs = jSService.getTask(p, "bottom");
        
        String splashJS = produceJS(p, splashOrigin, tgs, "bottom_", request);

        return splashJS;
    }
    
    
    /*
     * 换乘
     */
    @ResponseBody
    @RequestMapping(value = "/transfer.do", produces = "text/plain;charset=UTF-8")
    public String transfer(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        if(StringUtils.isBlank(p.getStnName()))
            p.setStnName(request.getParameter("stationName"));

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("transfer_origin");
        TaskEntity tgs = jSService.getTask(p, "transfer");
        
        String splashJS = produceJS(p, splashOrigin, tgs, "route_", request);

        return splashJS;
    }
//    
    /*
     * 同站线路
     */
    @ResponseBody
    @RequestMapping(value = "/stationDetail.do", produces = "text/plain;charset=UTF-8")
    public String getStationLine(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        if(StringUtils.isBlank(p.getStnName()))
            p.setStnName(request.getParameter("stationName"));

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("stationDetail_origin");
        TaskEntity tgs = jSService.getTask(p, "stationDetail");
        
        String splashJS = produceJS(p, splashOrigin, tgs, "bottom_", request);

        return splashJS;
    }
//    
//    /*
//     * 更多车辆
//     */
    @ResponseBody
    @RequestMapping(value = "/allCars.do", produces = "text/plain;charset=UTF-8")
    public String allCars(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        if(StringUtils.isBlank(p.getStnName()))
            p.setStnName(request.getParameter("stationName"));

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("allCars_origin");
        TaskEntity tgs = jSService.getTask(p, "allCars");
        
        String splashJS = produceJS(p, splashOrigin, tgs, "bottom_", request);

        return splashJS;
    }
    
    private String produceJS(AdvParam p, String originJs, TaskEntity tgs, String tag, HttpServletRequest request) {
        if(StringUtils.isBlank(originJs)) {
            return "┭┮﹏┭┮ 原始js文件为空 ";
        }
        
        if(StringUtils.isBlank(tgs.getTraceid())) {
            logger.info("traceId is null -- , udid={},s={}, v={}", p.getUdid(), p.getS(), p.getV());
            originJs = originJs.replace("${TASKS}", tgs.getTaskGroups().getTasks().toString());
            originJs = originJs.replace("${TIMEOUTS}", tgs.getTaskGroups().getTasks().toString());
            return originJs;
        }
        
        String splashJS = "";
        if (tgs != null) {
            splashJS = originJs.replace("${TASKS}", tgs.getTaskGroups().getTasks().toString());
            splashJS = splashJS.replace("${TIMEOUTS}", tgs.getTaskGroups().getTimeouts().toString());
            splashJS = splashJS.replace("${TRACEID}", tgs.getTraceid());
//            splashJS = splashJS.replaceAll("${MAIDIAN_PARAM}", );

            for (List<String> tasks : tgs.getTaskGroups().getTasks()) {
                for (String task : tasks) {
                    if(task.contains("api_chelaile")) {
                        splashJS = splashJS.replace("${QUERY_STRING}", request.getQueryString());
                    } 
//                    else if (task.contains("sdk")) {
//                        if(StaticAds.JS_FILE_STR.containsKey(tag + task)) {
//                            splashJS += StaticAds.JS_FILE_STR.get(tag + task);
//                        }
//                        else {
//                            logger.error("没有配置文件的 sdk|api，task={},  udid={}, JS_FILE_STR.keys={}", tag + task, p.getUdid(), StaticAds.JS_FILE_STR.keySet());
//                        }
//                    }
                }
            }
        }
        return splashJS;
    }

}
