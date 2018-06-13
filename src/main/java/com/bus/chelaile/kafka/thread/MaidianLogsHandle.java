/**
 * 
 */
/**
 * @author Administrator

 *
 */
package com.bus.chelaile.kafka.thread;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.kafka.InfoStreamHelp;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.thread.Queue;
import com.bus.chelaile.thread.model.QueueObject;
import com.bus.chelaile.util.New;

public class MaidianLogsHandle {
    //    private KafkaStream<byte[], byte[]> m_stream;

    public static final Logger logger = LoggerFactory.getLogger(MaidianLogsHandle.class);

    public MaidianLogsHandle() {
        super();
    }

    //    public void run() {
    //        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
    //        while (it.hasNext()) {
    //            String log = null;
    //            try {
    //                log = new String(it.next().message(), "UTF-8");
    //            } catch (UnsupportedEncodingException e) {
    //                e.printStackTrace();
    //            }
    //            filterContent(log.trim());
    //        }
    //    }
    //
    //    // return 9: 开屏广告展示
    //    // return 10: 点击埋点
    //    private void filterContent(String str) {
    //        String maidian_log = Constants.MAIDIAN_LOG;
    //        if (Constants.ISTEST) {
    //            maidian_log = Constants.TEST_MAIDIAN_LOG;
    //        }
    //        /*if (str.contains(maidian_log) && str.contains(Constants.ADV_EXHIBIT) && str.contains(Constants.OPEN_ADV_KEYWORD)) {
    //        //            logger.info("读到展示埋点日志： str={}", str);
    //            try {
    //                analysisOpenAdvExhibit(str);
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        } else*/
    //        if (str.contains(maidian_log) && str.contains(Constants.ADV_CLICK)
    //        //                && str.contains(Constants.WXAPP_SRC)
    //        ) {
    //            TimeLong.info("读到点击埋点日志： str={}", str);
    //            try {
    //                analysisMaidianClick(str);
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        } else {
    //            return;
    //        }
    //    }

    /**
     * 解析小程序的点击日志
     * 并记录进缓存
     * @param str
     */
    public static void analysisMaidianClick(String line) {
        Map<String, String> params = preHandleMaidianLog(line);
        if (params != null) {
            String udid = params.get("userId");
            if (params.containsKey("udid")) {
                udid = params.get("udid");
            }
            String advId = params.get("adv_id");
            if (udid == null || advId == null) {
                logger.error("点击埋点解析， 广告为空 line={}", line);
                return;
            }
            logger.info("点击埋点得到点击日志： udid={}, advId={}", udid, advId);
            if (StaticAds.allAds.get(advId) == null) {
                //                logger.error("缓存中未发现广告,advId={}, line={}", advId, line);
                return;
            }
            //            logger.info("点击埋点得到点击日志： udid={}, advId={}", udid, advId);

            // 存储广告点击次数到redis
            QueueObject queueobj = new QueueObject();
            queueobj.setRedisIncrKey(AdvCache.getTotalClickPV(advId));
            Queue.set(queueobj);

            // 存储用户点击广告到ocs中
            InfoStreamHelp.setClickToRecord(advId, udid);
            
            // 存储项目点击
            String projectId = StaticAds.allAds.get(advId).getProjectId();
            if(StringUtils.isNotBlank(projectId)) {
                String projectClickKey = AdvCache.getProjectClickKey(udid, projectId);
                CacheUtil.incrToCache(projectClickKey, Constants.LONGEST_CACHE_TIME);    // 存储30天
            }

        }
    }

    //    /**
    //     * 解析开屏广告展示埋点日志
    //     * 并且记录进缓存
    //     * @param line
    //     */
    //    private void analysisOpenAdvExhibit(String line) {
    //        Map<String, String> params = preHandleMaidianLog(line);
    //        if (params != null) {
    //            String udid = params.get("udid");
    //            String advId = params.get("adv_id");
    //            if (udid == null || advId == null) {
    //                logger.info("广告展示埋点解析， 广告为空 line={}", line);
    //                return;
    //            }
    //
    //            if (StaticAds.allAds.get(advId) == null) {
    ////                logger.error("缓存中未发现广告,advId={}, line={}", advId, line);
    //                return;
    //            }
    //
    //            // 记录缓存， 开屏广告‘展示’|‘发送’ + 1
    //            logger.info("更新开屏 udid={}, advId={}", udid, advId);
    //            AdPubCacheRecord cacheRecord = null;
    //            try {
    //                cacheRecord = AdvCache.getAdPubRecordFromCache(udid, ShowType.DOUBLE_COLUMN.getType());
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //            if(cacheRecord == null) {
    //                cacheRecord = new AdPubCacheRecord();
    //            }
    ////            logger.info("更新开屏广告前***， cacheRecord={}", JSONObject.toJSONString(cacheRecord));
    //            cacheRecord.buildAdPubCacheRecord(Integer.parseInt(advId));
    //            cacheRecord.setOpenAdHistory(new AdCategory(Integer.parseInt(advId), 1, -1));
    //            cacheRecord.setAndUpdateOpenAdPubTime(Integer.parseInt(advId));
    //            RecordManager.recordAdd(udid, ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
    ////            logger.info("更新开屏广告后###， cacheRecord={}", JSONObject.toJSONString(cacheRecord));
    //        }
    //    }

    private static Map<String, String> preHandleMaidianLog(String line) {
        String encodedURL = null;
        try {
            encodedURL = URLDecoder.decode(line, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        int index = 0;
        Map<String, String> params = New.hashMap();
        if (encodedURL.contains("ADV_EXHIBIT")) {
            index = encodedURL.indexOf("ADV_EXHIBIT");
            params = paramsAnalysis(encodedURL.substring(index + 12));
        } else if (encodedURL.contains("ADV_CLICK")) {
            index = encodedURL.indexOf("ADV_CLICK");
            params = paramsAnalysis(encodedURL.substring(index + 10));
        }
        //        System.out.println(encodedURL.substring(index + 12));

        return params;
    }

    private static Map<String, String> paramsAnalysis(String url) {
        Map<String, String> params = New.hashMap();
        String entrys[] = url.split("\\|#");
        for (String s : entrys) {
            String[] maps = s.split(":");
            try {
                if (maps != null && maps.length >= 2)
                    params.put(maps[0].trim(), URLDecoder.decode(maps[1].trim(), "UTF-8"));
            } catch (Exception e) {
                logger.error("参数解析出错: map={}", maps.toString());
                e.printStackTrace();
                return null;
            }
        }
        return params;
    }

    public static void main(String[] args) {
        //        String s = "May 16 18:00:00 web1 nginx: 112.97.52.202 |# - |# 2018-05-16 18:00:00 |# 200 |# 0.000 |# 67 |# - |# Dalvik/2.1.0 (Linux; U; Android 7.1.2; Redmi 5 Plus MIUI/V9.5.4.0.NEGCNFA) |#- |# logs.chelaile.net.cn |# - |# - |# /realtimelog?<ADV_EXHIBIT>adv_id:14317 |# s:android |# last_src:app_xiaomi_store |# stats_referer:recommend |# push_open:1 |# stats_act:pull_refresh |# userId:unknown |# provider_id:1 |# geo_lt:4 |# geo_lat:22.783977 |# line_id:075288966572 |# sv:7.1.2 |# vc:103 |# v:3.50.2 |# secret:6cf5b4d8dfe44b9cbef6b85b71efa3c6 |# imei:868027038762880 |# udid:dda482b3-380f-4fec-bd5f-e25ae177e334 |# stn_name:"
        //                + " dafsadf |# cityId:016 |# ad_switch:7 |# adv_type:5 |# wifi_open:1 |# deviceType:Redmi 5 Plus |# mac:02:00:00:00:00:00 |# geo_type:gcj |# lchsrc:icon |# nw:MOBILE_LTE |# AndroidID:6b55ddbb67ad3962 |# api_type:0 |# stn_order:16 |# geo_lac:25.0 |# accountId:54842956 |# language:1 |# first_src:app_xiaomi_store |# geo_lng:114.46562 |# https";
        //        
        //        analysisOpenAdvExhibit(s);
        //        
        //        String s1 = "223.104.3.200 |# - |# 2018-05-16 19:09:53 |# 200 |# 0.000 |# 67 |# https://servicewechat.com/wx71d589ea01ce3321/24/page-frame.html |# Mozilla/5.0 (Linux; Android 8.0; MI 6 Build/OPR1.170623.027; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/044030 Mobile Safari/537.36 MicroMessenger/6.6.6.1300(0x26060637) NetType/4G Language/zh_CN MicroMessenger/6.6.6.1300(0x26060637) NetType/4G Language/zh_CN |#- |# logs.chelaile.net.cn |# - |# - |# /realtimelog?<STN_ADV_CLICK>adv_id:14268|#s:h5|#wxs:wx_app|#src:weixinapp_cx|#sign:1|#v:3.2.1|#cityId:006|#userId:okBHq0Ed6jVNJpJ7uxIWijM3G4YU|#unionId:oSpTTju5G8AlotnUQ-52c5v2HWns |# https";
        //        analysisWXAppClick(s1);

        String s1 =
                "May 30 18:00:20 web6 nginx: 113.116.90.150 |# - |# 2018-05-30 18:00:20 |# 200 |# 0.000 |# 67 |# https://servicewechat.com/wx71d589ea01ce3321/29/page-frame.html |# Mozilla/5.0 (Linux; Android 5.1; HUAWEI TAG-AL00 Build/HUAWEITAG-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.143 Crosswalk/24.53.595.0 XWEB/151 MMWEBSDK/19 Mobile Safari/537.36 MicroMessenger/6.6.6.1300(0x26060637) NetType/WIFI Language/zh_CN MicroMessenger/6.6.6.1300(0x26060637) NetType/WIFI Language/zh_CN |#- |# logs.chelaile.net.cn |# - |# - |# /realtimelog?<STN_ADV_CLICK>adv_id:14471|#s:h5|#wxs:wx_app|#src:weixinapp_cx|#sign:1|#v:3.2.5|#cityId:014|#userId:okBHq0O8nRDian42cckCDhZljQOE|#unionId:oSpTTjs6fImNq8QE9u7kNickYzTI |# https";
        String s3 =
                "May 30 18:00:05 web3 nginx: 112.96.109.148 |# - |# 2018-05-30 18:00:05 |# 200 |# 0.000 |# 67 |# https://servicewechat.com/wx71d589ea01ce3321/29/page-frame.html |# Mozilla/5.0 (Linux;May 30 18:00:14 web3 nginx: 183.22.29.186 |# - |# 2018-05-30 18:00:14 |# 200 |# 0.000 |# 67 |# - |# lite/5.49.0 (iPhone; iOS 11.3.1; Scale/2.00) |#- |# logs.chelaile.net.cn |# - |# - |# /realtimelog?<ADV_CLICK>userId: |# geo_type:wgs |# language:1 |# geo_lat:22.977655 |# geo_lng:113.894144 |# sv:11.3.1 |# deviceType:iPhone9,1 |# s:IOS |# lchsrc:icon |# v:5.49.0 |# udid:9ee5bcfbd59ba2361a18c30ea1a3503550188bfd |# sign:lSzvpo8Wfr83GPB40Bwj w== |# nw:WiFi |# mac: |# wifi_open:1 |# geo_lac:65.000000 |# cityId:008 |# push_open:0 |# vc:10540 |# idfa:05F50086-DE77-46E7-A3E3-6635F4EB147C |# adv_type:16 |# adv_id:14350 |# adv_image: |# provider_id:2 |# adv_title: |# adv_desc: |# gd_type:0 |# https";
        String s4 =
                "May 30 18:00:04 web10 nginx: 120.197.196.105 |# - |# 2018-05-30 18:00:04 |# 200 |# 0.000 |# 67 |# - |# Dalvik/2.1.0 (Linux; U; Android 7.0; FRD-AL10 Build/HUAWEIFRD-AL10) |#- |# logs.chelaile.net.cn |# - |# - |# /realtimelog?<ADV_CLICK>adv_title: |# s:android |# last_src:app_huawei_store |# adv_id:14458 |# push_open:0 |# userId:unknown |# provider_id:2 |# adv_image: |# sv:7.0 |# vc:105 |# v:3.52.0 |# secret:2f94a12124d34d79a71725e1a5d94605 |# imei:864131039035819 |# udid:0218c707-91b0-472c-8835-1dd2e953dd01 |# cityId:019 |# adv_type:1 |# wifi_open:1 |# deviceType:FRD-AL10 |# mac:02:00:00:00:00:00 |# lchsrc:icon |# nw:MOBILE_LTE |# adv_desc: |# AndroidID:587409f48abd45fe |# api_type:1 |# accountId:29565498 |# language:1 |# first_src:app_huawei_store |# https";
        
        String s6 = "Jun 13 18:06:04 web3 nginx: 223.104.97.46 |# - |# 2018-06-13 18:06:04 |# 200 |# 0.011 |# 79 |# - |# lite/5.50.3 (iPhone; iOS 11.2.6; Scale/3.00) |#- |# logs.chelaile.net.cn |# - |# - |# /realtimelog?<ADV_CLICK>userId: |# geo_type:wgs |# language:1 |# geo_lat:26.661279 |# geo_lng:106.580928 |# dpi:3 |# sv:11.2.6 |# deviceType:iPhone6sPlus |# s:IOS |# lchsrc:icon |# vendor:apple |# screenHeight:2208 |# v:5.50.3 |# udid:d41d8cd98f00b204e9800998ecf8427e58978720 |# gpsAccuracy:65.000000 |# sign:hTr3s7g6Yo/bL2jWUjVIsA== |# nw:4G |# mac: |# wifi_open:1 |# geo_lac:65.000000 |# cityId:083 |# idfv:373409D0-B5AF-46AB-8F75-B0B1248D3ADB |# userAgent:Mozilla/5.0 (iPhone; CPU iPhone OS 11_2_6 like Mac OS X) AppleWebKit/604.5.6 (KHTML, like Gecko) Mobile/15D100 |# screenWidth:1242 |# push_open:0 |# vc:10553 |# idfa:666FF421-AA36-4C0E-A85E-A9C5D1C37008 |# adv_id:14517 |# adv_type:4 |# adv_image:http@3a//img1.360buyimg.com/pop/jfs/t20581/141/1087136404/44390/38d313c/5b1f6613N6dc7adee.jpg |# line_id: |# provider_id:10 |# stn_order:(null) |# api_type:1 |# stats_act:enter |# adv_tit:dafd |# http";
//        MaidianLogsHandle.analysisMaidianClick(s1);
//        MaidianLogsHandle.analysisMaidianClick(s3);
        MaidianLogsHandle.analysisMaidianClick(s6);

    }
}
