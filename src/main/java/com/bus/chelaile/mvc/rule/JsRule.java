package com.bus.chelaile.mvc.rule;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @RequestMapping("/splashAd.js")
    public String splashAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("splash_origin");
        //        TasksGroup tasksGroups = JSService.getTask("splash");
        TasksGroup tgs = new TasksGroup();
        List<String> task1 = New.arrayList();
        //        task1.add("api_voicead");
        task1.add("sdk_toutiao");
        task1.add("sdk_baidu");

        List<String> task2 = New.arrayList();
        task2.add("sdk_gdt");

        List<List<String>> ts = new ArrayList<List<String>>();
        ts.add(task1);
        ts.add(task2);

        List<Long> times = New.arrayList();
        times.add(200L);
        times.add(1500L);

        tgs.setTasks(ts);
        tgs.setTimeouts(times);

        String splashJS = splashOrigin.replace("${TASKS}", tgs.getTasks().toString());
        splashJS = splashJS.replace("${TIMEOUTS}", tgs.getTimeouts().toString());

        for (List<String> tasks : tgs.getTasks()) {
            for (String task : tasks) {
                if (task.contains("sdk"))
                    splashJS += StaticAds.JS_FILE_STR.get(task);
            }
        }
        logger.info("返回的开屏js代码：{}", splashJS);

        //        return "hello, splashAd";
        return splashJS;
    }

   
    /*
     * 开屏
     */
    @ResponseBody
    @RequestMapping("/splashAd1.js")
    public String splashAd1(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("splash_origin");
        TasksGroup tgs = jSService.getTask(p);

        String splashJS = "";
        if (tgs != null) {
            splashJS = splashOrigin.replace("${TASKS}", tgs.getTasks().toString());
            splashJS = splashJS.replace("${TIMEOUTS}", tgs.getTimeouts().toString());

            for (List<String> tasks : tgs.getTasks()) {
                for (String task : tasks) {
                    if (task.contains("sdk"))
                        splashJS += StaticAds.JS_FILE_STR.get(task);
                }
            }
        }

        return splashJS;
    }
    
    /*
     * 首页
     */
    @ResponseBody
    @RequestMapping("/homeAd1.js")
    public String homeAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("splash_origin");
        TasksGroup tgs = jSService.getTask(p);

        String splashJS = "";
        if (tgs != null) {
            splashJS = splashOrigin.replace("${TASKS}", tgs.getTasks().toString());
            splashJS = splashJS.replace("${TIMEOUTS}", tgs.getTimeouts().toString());

            for (List<String> tasks : tgs.getTasks()) {
                for (String task : tasks) {
                    if (task.contains("sdk"))
                        splashJS += StaticAds.JS_FILE_STR.get(task);
                }
            }
        }

        return splashJS;
    }

    
    /*
     * 详情页右上角
     */
    @ResponseBody
    @RequestMapping("/rightTopAd1.js")
    public String rightTopAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("splash_origin");
        TasksGroup tgs = jSService.getTask(p);

        String splashJS = "";
        if (tgs != null) {
            splashJS = splashOrigin.replace("${TASKS}", tgs.getTasks().toString());
            splashJS = splashJS.replace("${TIMEOUTS}", tgs.getTimeouts().toString());

            for (List<String> tasks : tgs.getTasks()) {
                for (String task : tasks) {
                    if (task.contains("sdk"))
                        splashJS += StaticAds.JS_FILE_STR.get(task);
                }
            }
        }

        return splashJS;
    }
    
    
    /*
     * 站点位置
     */
    @ResponseBody
    @RequestMapping("/stationAd1.js")
    public String stationAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("splash_origin");
        TasksGroup tgs = jSService.getTask(p);

        String splashJS = "";
        if (tgs != null) {
            splashJS = splashOrigin.replace("${TASKS}", tgs.getTasks().toString());
            splashJS = splashJS.replace("${TIMEOUTS}", tgs.getTimeouts().toString());

            for (List<String> tasks : tgs.getTasks()) {
                for (String task : tasks) {
                    if (task.contains("sdk"))
                        splashJS += StaticAds.JS_FILE_STR.get(task);
                }
            }
        }

        return splashJS;
    }
    
    
    /*
     * 详情页底部
     */
    @ResponseBody
    @RequestMapping("/bottomAd1.js")
    public String bottomAd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam p = getActionParam(request);
        logger.info("***请求splashAds.js, s={}, v={}, vc={}, udid={}, cityId={}", p.getS(), p.getV(), p.getVc(), p.getUdid(),
                p.getCityId());

        // 模板 
        String splashOrigin = StaticAds.JS_FILE_STR.get("splash_origin");
        TasksGroup tgs = jSService.getTask(p);

        String splashJS = "";
        if (tgs != null) {
            splashJS = splashOrigin.replace("${TASKS}", tgs.getTasks().toString());
            splashJS = splashJS.replace("${TIMEOUTS}", tgs.getTimeouts().toString());

            for (List<String> tasks : tgs.getTasks()) {
                for (String task : tasks) {
                    if (task.contains("sdk"))
                        splashJS += StaticAds.JS_FILE_STR.get(task);
                }
            }
        }

        return splashJS;
    }
}
