package com.bus.chelaile.mvc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.common.ShortUrlUtil;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ads.Station;
import com.bus.chelaile.service.ServiceManager;
import com.bus.chelaile.util.DateUtil;
import com.bus.chelaile.util.config.PropertiesManager;
import com.bus.chelaile.util.config.PropertiesUtils;

/**
 * 广告相关接口
 * 
 * @author zzz
 * 
 */

@Controller
@RequestMapping("")
public class BusAdvAction extends AbstractController {

    @Resource
    private ServiceManager serviceManager;

    private static final Logger log = LoggerFactory.getLogger(BusAdvAction.class);
//    private static final  int TINGYUN_SWITCH = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "tingyunSwitch", "1"));
    
    @ResponseBody
    @RequestMapping(value = "adv!getLineDetailAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getLineDetailAds(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {
        AdvParam param = getActionParam(request);
        param.setStnList(request.getParameter("stnList"));
        param.setStationList(Station.parseStationList(request.getParameter("stnList")));
        param.setLnState(getInt(request, "lnState"));
        param.setStnOrder(getInt(request, "stnOrder"));
        param.setCshow(request.getParameter("cshow"));
        param.setStationId(request.getParameter("stationId"));
        param.setStnLng(request.getParameter("stnLng"));
        param.setStnLat(request.getParameter("stnLat"));
        return serviceManager.getAdsResponseStr(param, "getLineDetails");
    }

    @ResponseBody
    @RequestMapping(value = "adv!getNewOpenAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getNewOpenAds(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);
        param.setType(getInt(request, "type"));
        param.setStartMode(getInt(request, "startMode"));

        return serviceManager.getAdsResponseStr(param, "getNewOpen");
    }

    @ResponseBody
    @RequestMapping(value = "adv!getAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getAds(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);
        param.setStnList(request.getParameter("stnList")); //首页站线组合
        param.setStationList(Station.parseStationList(request.getParameter("stnList")));
        param.setGridLines(getInt(request, "gridLines")); //标注首页有几行导流入口广告
        param.setDistance(getInt(request, "distance")); //用户距离站点距离
        param.setRideStatus(getInt(request, "rideStatus"));//用户骑行状态
        param.setType(getInt(request, "type")); //type=1的时候，表示线路规划页的单栏广告
        param.setlSize(getInt(request, "lSize")); //推荐线路的条目数  2018-03-14更新参数

        return serviceManager.getAdsResponseStr(param, "getDoubleAndSingleAds");
    }

    @ResponseBody
    @RequestMapping(value = "adv!getOpenScreenAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getOpenScreenAds(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {
        AdvParam param = getActionParam(request);
        param.setType(0);

        return serviceManager.getAdsResponseStr(param, "getOldOpen");
    }

    @ResponseBody
    @RequestMapping(value = "adv!getFullAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getFullAds(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);
        param.setType(1);

        return serviceManager.getAdsResponseStr(param, "getOldOpen");
    }

    @ResponseBody
    @RequestMapping(value = "adv!preloadAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String preloadAds(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);

        return serviceManager.getAdsResponseStr(param, "preLoadAds");
    }

    @ResponseBody
    @RequestMapping(value = "adv!precacheResource.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String precacheResource(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {
        AdvParam param = getActionParam(request);

        return serviceManager.getAdsResponseStr(param, "precacheResource");
    }

    @ResponseBody
    @RequestMapping(value = "adv!getActiveAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getActiveAds(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);
        param.setType(0);

        return serviceManager.getAdsResponseStr(param, "getActive");
    }

    @ResponseBody
    @RequestMapping(value = "adv!getRide.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getRide(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);
        param.setType(1);

        return serviceManager.getAdsResponseStr(param, "getRide");
    }

    @ResponseBody
    @RequestMapping(value = "adv!getH5Banner.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getH5Banner(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);

        Object object = serviceManager.getQueryValue(param, "h5BannerAds");
        if (object == null) {
            return serviceManager.getClientErrMapWithNoHead("", Constants.STATUS_NO_DATA);
        } else {
            return serviceManager.getClienSucMapWithNoHead(object, Constants.STATUS_REQUEST_SUCCESS);
        }
    }

    /*
     * 小程序 banner 位广告
     */
    @ResponseBody
    @RequestMapping(value = "adv!getWechatAppHomeBanner.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getWechatAppHomeBanner(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {
        AdvParam param = getActionParam(request);
        param.setWxs(request.getParameter("wxs")); // 小程序来源
        param.setSrc(request.getParameter("src"));
        if (StringUtils.isEmpty(param.getH5Src()))
            param.setH5Src(request.getParameter("src"));
        Object object = serviceManager.getQueryValue(param, "getWXBannerAds");
        if (object == null) {
            return serviceManager.getClientErrMapWithNoHead("", Constants.STATUS_NO_DATA);
        } else {
            return serviceManager.getClienSucMapWithNoHead(object, Constants.STATUS_REQUEST_SUCCESS);
        }
    }

    @ResponseBody
    @RequestMapping(value = "adv!getChat.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getChat(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);
        param.setType(2);

        return serviceManager.getAdsResponseStr(param, "getActive");
    }

    /*
     * reload
     */
    @ResponseBody
    @RequestMapping(value = "adv!reloadDatas.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String reloadDatas(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        log.info("reload ******************************");

        return serviceManager.reloadDatas();
    }

    /*
     * swSwitch
     * 
     */
    @ResponseBody
    @RequestMapping(value = "adv!getSwSwitch.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getSwSwitch(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        log.info("[entergetSwSwitch]");
        String swSwitch = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "swSwitch", "1");
        if (StringUtils.isBlank(swSwitch))
            return serviceManager.getClientErrMap("", Constants.STATUS_INTERNAL_ERROR);
        JSONObject sw = new JSONObject();
        // 饭饭交代，不能够返回0，也不能够返回空data。 否则会crash!!!
        sw.put("switch", swSwitch);

        return serviceManager.getClienSucMap(sw, Constants.STATUS_REQUEST_SUCCESS);
    }

//    /*
//     * tingyunSwitch
//     * 
//     */
//    @ResponseBody
//    @RequestMapping(value = "adv!getTYSwitch.action", produces = "Content-Type=text/plain;charset=UTF-8")
//    public String getTYSwitch(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
//        log.info("[entergetTYSwitch]");
//        JSONObject ty = new JSONObject();
//        ty.put("switch", TINGYUN_SWITCH);
//
//        return serviceManager.getClienSucMap(ty, Constants.STATUS_REQUEST_SUCCESS);
//    }
    
    /*
     * uninterest
     */
    @ResponseBody
    @RequestMapping(value = "adv!uninterest.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String uninterest(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);
        String secret = request.getParameter("secret");
        int showType = getInt(request, "showType");
        int advId = getInt(request, "advId");
        int apiType = getInt(request, "apiType");
        String provider_id = request.getParameter("provider_id");

        //		if (StringUtils.isBlank(request.getParameter("secret"))) {
        //			log.error(
        //					"点击关闭广告secret错误: advId={}, udid={}, accountId={}, secret={}",
        //					advId, param.getUdid(), param.getAccountId(), secret);
        //			return serviceManager.getClientErrMap("参数错误",
        //					Constants.STATUS_PARAM_ERROR);
        //		}

        return serviceManager.uninterest(param, showType, advId, apiType, provider_id, secret);
    }

    /*
     * interest,内部测试使用
     */
    @ResponseBody
    @RequestMapping(value = "adv!interest.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String interest(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);
        int showType = getInt(request, "showType");
        int advId = getInt(request, "advId");
        if (advId == -1) {
            advId = 0;
        }

        //		if (StringUtils.isBlank(request.getParameter("secret"))) {
        //			log.error(
        //					"点击关闭广告secret错误: advId={}, udid={}, accountId={}, secret={}",
        //					advId, param.getUdid(), param.getAccountId(), secret);
        //			return serviceManager.getClientErrMap("参数错误",
        //					Constants.STATUS_PARAM_ERROR);
        //		}

        return serviceManager.interest(param, showType, advId);
    }

    /*
     * invalidUser(会员定制不投放广告功能)
     */
    @ResponseBody
    @RequestMapping(value = "notice!invalidUser.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String invalidUser(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);
        String startInvalidDate = request.getParameter("startInvalidDate"); // 失效开始时间
        String endInvalidDate = request.getParameter("endInvalidDate"); // 失效结束时间

        return serviceManager.invalidUser(param, startInvalidDate, endInvalidDate);

    }

    /*
     * clearInvalidAds(取消 用户不投放广告)
     */
    @ResponseBody
    @RequestMapping(value = "adv!clearInvalidAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String clearInvalidAds(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {
        AdvParam param = getActionParam(request);

        return serviceManager.clearInvalidAds(param);

    }

    /*
     * getDisplayAdv(获取所有可投放的广告，客服系统使用)
     */
    @ResponseBody
    @RequestMapping(value = "adv!getDisplayAdv.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getDisplayAdv(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AdvParam param = getActionParam(request);

        return serviceManager.getDisplayAdv(param);
    }

    @ResponseBody
    @RequestMapping(value = "adv!getShortUrl.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getShortUrl(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        String srcUrl = request.getParameter("srcUrl");
        String udid = request.getParameter("udid");

        if (StringUtils.isBlank(srcUrl)) {

            return "srcUrl不能为空";
        }
        //		srcUrl = null;
        //		srcUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxbddfa9a4542032b5&redirect_uri=https%3A%2F%2Fweb.chelaile.net.cn%2Fredirectdev%2Fpassenger-manager%2Factivity%2FgoNocarActivity&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

        //		String queryString = request.getQueryString();
        //		log.info("queryString = {}", queryString);
        //		srcUrl = queryString.split("srcUrl=")[1];
        String srcUrlDecode = null;
        try {
            srcUrlDecode = URLDecoder.decode(srcUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("srcUrl = {}", srcUrlDecode);

        String goalUrl = ShortUrlUtil.getShortUrl(srcUrlDecode);
        if (null != goalUrl) {
            log.info("busAdvAction, " + udid + " shortUrl: " + goalUrl + " redirectUrl: " + srcUrlDecode);
            return goalUrl;
        } else {
            log.info("busAdvAction, shortUrl:{}, redirectUrl:{}, failed", goalUrl, srcUrlDecode);
            return "生成短链接失败";
        }

    }

    /*
     * 给三妹返回上车提醒的文字配置
     */
    @ResponseBody
    @RequestMapping(value = "adv!getAboardText.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getAboardText(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        AdvParam param = getActionParam(request);

        return serviceManager.getAdsResponseStr(param, "getAboardText");

    }

    @ResponseBody
    @RequestMapping(value = "adv!reloadConfigs.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String reloadConfigs(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {

        String fortest = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "fortest", "1");
        log.info("reload前， for test值为：{}", fortest);
        PropertiesManager.init();
        fortest = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "fortest", "1");
        log.info("reload后， for test值为：{}", fortest);
        return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
    }
    
    @ResponseBody
    @RequestMapping(value = "adv!getFeedAds.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String getFeedAds(HttpServletRequest request) {
        
        AdvParam param = getActionParam(request);
        
        return serviceManager.getAdsResponseStr(param, "getFeedAds");
    }

    /*
     * setNewUserstoOCS(往ocs中塞入用户创建时间，测试新用户不投放广告的时候使用)
     */
    @ResponseBody
    @RequestMapping(value = "adv!setNewUserstoOCS.action", produces = "Content-Type=text/plain;charset=UTF-8")
    public String setNewUserstoOCS(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {
        String udid = request.getParameter("udid");
        Long time = Long.parseLong(request.getParameter("time"));

        String isDelete = request.getParameter("isDelete");
        String shortUrl = request.getParameter("shortUrl");
        String type = request.getParameter("type");
        String fileName = request.getParameter("fileName");
        String channelId = request.getParameter("channelId");
        boolean delete = false;

        if (StringUtils.isNoneBlank(isDelete)) {
            delete = Boolean.parseBoolean(isDelete);
        }

        // 删除短连接
        if (delete) {
            CacheUtil.delete("ADVSHORTURL#" + shortUrl);
            return "delete short url success, shortUrl=" + shortUrl;
        }

        // 处理收藏频道问题
        if (StringUtils.isNoneBlank(type) && type.equalsIgnoreCase("handleFavChannel")) {
            log.info("handleChannels begin : fileName={}, channelId={}", fileName, channelId);
            return serviceManager.handlFavChannels(fileName, channelId);
        }

        // 修改用户存储在OCS中的创建时间
        String key = "CREATEUSERTIME#" + udid;
        CacheUtil.set(key, 3600 * 10, time);
        log.info("udid={}, time={}, date={}", udid, time, DateUtil.getFormatTime(new Date(time), "yyyy-MM-dd HH:mm:SS"));

        Long timeStr = Long.parseLong(CacheUtil.getFromCommonOcs(key));
        String dateOut = DateUtil.getFormatTime(new Date(timeStr), "yyyy-MM-dd HH:mm:SS");

        return "设置用户创建时间成功,udid=" + udid + ", time=" + dateOut;
    }
}
