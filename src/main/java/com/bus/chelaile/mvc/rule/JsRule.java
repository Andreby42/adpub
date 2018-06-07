package com.bus.chelaile.mvc.rule;

import java.util.ArrayList;
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

import com.bus.chelaile.model.ads.entity.TasksGroup;
import com.bus.chelaile.mvc.AbstractController;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.JSService;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.util.New;

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


    /*
     * 开屏
     */
    @ResponseBody
    @RequestMapping(value = "/splashAd.do", produces = "text/plain;charset=UTF-8")
    public String splashAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("splash_origin");
        TasksGroup tgs = jSService.getTask(p, "splash");

        String splashJS = produceJS(p, splashOrigin, tgs, "splash_");

        return splashJS;
    }
    
    /*
     * 首页
     */
    @ResponseBody
    @RequestMapping(value = "/homeAd.do", produces = "text/plain;charset=UTF-8")
    public String homeAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("home_origin");
        TasksGroup tgs = jSService.getTask(p, "home");

        String splashJS = produceJS(p, splashOrigin, tgs, "home_", );

        return splashJS;
    }

    private String produceJS(AdvParam p, String originJs, TasksGroup tgs, String tag, String maidianParam) {
        if(StringUtils.isBlank(originJs)) {
            return "┭┮﹏┭┮ 原始js文件为空 ";
        }
        
        String splashJS = "";
        if (tgs != null) {
            splashJS = originJs.replace("${TASKS}", tgs.getTasks().toString());
            splashJS = splashJS.replace("${TIMEOUTS}", tgs.getTimeouts().toString());
            splashJS = splashJS.replaceAll("${MAIDIAN_PARAM}", );

            for (List<String> tasks : tgs.getTasks()) {
                for (String task : tasks) {
                    if (task.contains("sdk")) {
                        if(StaticAds.JS_FILE_STR.containsKey(tag + task))
                            splashJS += StaticAds.JS_FILE_STR.get(tag + task);
                        else 
                            logger.error("没有配置文件的 sdk|api，task={},  udid={}, JS_FILE_STR.keys={}", tag + task, p.getUdid(), StaticAds.JS_FILE_STR.keySet());
                    }
                }
            }
        } else {
            return "┭┮﹏┭┮ tasks为空 ";
        }
        return splashJS;
    }

    
    /*
     * 详情页右上角
     */
    @ResponseBody
    @RequestMapping(value = "/rightTopAd.do", produces = "text/plain;charset=UTF-8")
    public String rightTopAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("right_origin");
        TasksGroup tgs = jSService.getTask(p, "rightTop");

        String splashJS = produceJS(p, splashOrigin, tgs, "right_");

        return splashJS;
    }
    
    
    /*
     * 站点位置
     */
    @ResponseBody
    @RequestMapping(value = "/stationAd.do", produces = "text/plain;charset=UTF-8")
    public String stationAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("station_origin");
        TasksGroup tgs = jSService.getTask(p, "station");

        String splashJS = produceJS(p, splashOrigin, tgs, "station_");

        return splashJS;
    }
    
    
    /*
     * 详情页底部
     */
    @ResponseBody
    @RequestMapping(value = "/bottomAd.do", produces = "text/plain;charset=UTF-8")
    public String bottomAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("bottom_origin");
        TasksGroup tgs = jSService.getTask(p, "bottom");

        String splashJS = produceJS(p, splashOrigin, tgs, "bottom_");

        return splashJS;
    }
}
