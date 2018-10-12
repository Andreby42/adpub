package com.bus.chelaile.third.IfengAx;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.third.IfengAx.model.IfengRequestBody;
import com.bus.chelaile.third.IfengAx.model.response.Ad;
import com.bus.chelaile.third.IfengAx.model.response.IfengResponse;
import com.bus.chelaile.util.HttpUtils;

public class IfenAxService {

    private static final String URL_TEST = "http://axbox.deliver.ifeng.com/adx/bid?deal_id=3";
    private static final String URL_ONLINE = "https://iis1.deliver.ifeng.com/adx/bid?deal_id=3";

    private static final Logger logger = LoggerFactory.getLogger(IfenAxService.class);

    public Ad getContext(AdvParam p, int isTest, int bannerType, int w, int h, String tagid) {

        IfengResponse responseEntity = null;

        String url = URL_ONLINE;
        // TODO
//        if (Constants.ISTEST) {
            url = URL_TEST;
            if (p.getImei() != null)
                p.setImei(p.getImei() + System.currentTimeMillis());
            if (p.getIdfa() != null) {
                p.setImei(p.getIdfa() + System.currentTimeMillis());
                p.setIdfa(p.getIdfa() + System.currentTimeMillis());
            }
//        }

        IfengRequestBody requestBody = new IfengRequestBody(p, isTest, bannerType, w, h, tagid);
        logger.info("请求凤凰网body={}", JSONObject.toJSONString(requestBody));
        System.out.println(JSONObject.toJSONString(requestBody));

        String result = HttpUtils.post(url, JSONObject.toJSONString(requestBody));

        //        logger.info("凤凰网返回result={}", result);
        System.out.println(result);
        if (StringUtils.isNoneBlank(result))
            responseEntity = JSON.parseObject(result, IfengResponse.class);

        if (responseEntity != null && responseEntity.getAd() != null && responseEntity.getAd().size() > 0) {
            return responseEntity.getAd().get(0);
        }

        logger.error("凤凰网返回为空 ");
        return null;
    }

    public static void main(String[] args) {
        AdvParam p = new AdvParam();
        p.setUdid("db9bef8b-93a1-4698-9c3b-d7ee59808f15");
        p.setIp("210.51.19.3");
        p.setS("android");
        p.setImei("861063046917681139");
//        p.setS("ios");
//        p.setIdfa("861063046917681139");
        p.setV("3.62.0");
        p.setScreenHeight(1920);
        p.setScreenWidth(680);
        p.setLng(119.123);
        p.setLat(39.0093);

        IfenAxService i = new IfenAxService();
//        Ad ad = i.getContext(p, 1, 6, 300, 200, "1-1-1"); // banner位尺寸
//                Ad ad = i.getContext(p, 1, 2, 320, 180, "1-1-1"); // 右上角尺寸
                Ad ad = i.getContext(p, 1, 6, 640, 960, "1-2-1");  // 开屏尺寸
        if (ad != null) {
            System.out.println(JSONObject.toJSONString(ad));
            System.out.println("text=" + ad.getCreative().getStatics().getText());
            System.out.println("desc=" + ad.getCreative().getStatics().getDesc());
            System.out.println("pic=" + ad.getCreative().getStatics().getAurl().get(0));
            System.out.println("link=" + ad.getCreative().getStatics().getCurl());
            System.out.println("clickMonitor=" + ad.getCreative().getStatics().getAcurl());
            System.out.println("showMonitor=" + ad.getCreative().getStatics().getMurl());
        }

        System.exit(1);
    }
}
