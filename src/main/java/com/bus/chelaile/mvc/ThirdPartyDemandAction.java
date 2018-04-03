package com.bus.chelaile.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.service.ServiceManager;

/*
 * 第三方临时的一些需求
 */
@Controller
@RequestMapping("")
public class ThirdPartyDemandAction extends AbstractController {
    @Resource
    private ServiceManager serviceManager;
    Logger logger = LoggerFactory.getLogger(ThirdPartyDemandAction.class);

    @ResponseBody
    @RequestMapping(value = "adv!leyuansuActivationBack.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String activationBack(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam param = getActionParam(request);
        String appid = request.getParameter("appid");
        logger.info("来自乐元素的回调请求： imei={}, idfa={}, appid={}", param.getImei(), param.getIdfa(), appid);
        AnalysisLog.info("leyuansu callback, imei={}, idfa={}, appid={}", param.getImei(), param.getIdfa(), appid);

        logger.info("return 空白");
        return "";
//        return serviceManager.getClienSucMapWithNoHead(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
    }
}
