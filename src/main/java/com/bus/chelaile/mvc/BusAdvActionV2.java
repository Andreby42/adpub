package com.bus.chelaile.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bus.chelaile.common.Constants;
import com.bus.chelaile.service.ServiceManager;

/**
 * 广告相关接口
 * 
 * @author linzi
 * 2018-05-26 新版广告
 * 
 */

@Controller
@RequestMapping("")
public class BusAdvActionV2 extends AbstractController {

    @Resource
    private ServiceManager serviceManager;

    //    private static final Logger log = LoggerFactory.getLogger(BusAdvActionV2.class);

    /*
     * 详情页下方广告（及之前的feed流顶部广告)
     */
    @ResponseBody
    @RequestMapping(value = "adv!getLineFeedAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getLineFeedAds(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        AdvParam param = getActionParam(request);
        //        param.setIsTop(getInt(request, "isTop"));

        return serviceManager.getAdsResponseStr(param, "getLineFeedAds");
    }

    /*
     * 新版首页广告（既之前的首页单双栏位置广告)
     */
    @ResponseBody
    @RequestMapping(value = "adv!geColumntAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String geColumntAds(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        AdvParam advParam = getActionParam(request);

        Object result = serviceManager.getColumntAds(advParam);
        return serviceManager.getClienSucMap(result, Constants.STATUS_REQUEST_SUCCESS);
    }

    /*
     * 新版开屏广告
     */
    @ResponseBody
    @RequestMapping(value = "adv!getCoopenAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getCoopenAds(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam advParam = getActionParam(request);
        advParam.setStartMode(getInt(request, "startMode"));

        Object result = serviceManager.getCoopenAds(advParam);
        return serviceManager.getClienSucMap(result, Constants.STATUS_REQUEST_SUCCESS);
    }

    /*
     * 新版站点位置广告（从原来的详情页广告中拆分出来的）
     */
    @ResponseBody
    @RequestMapping(value = "adv!getStationAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getStationAds(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam advParam = getActionParam(request);
        if(StringUtils.isBlank(advParam.getStnName()))
            advParam.setStnName(request.getParameter("stationName"));

        Object result = serviceManager.getStationAds(advParam);
        return serviceManager.getClienSucMap(result, Constants.STATUS_REQUEST_SUCCESS);
    }
}
