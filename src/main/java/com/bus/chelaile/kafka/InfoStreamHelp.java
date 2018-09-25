/**
 * 
 */
/**
 * @author linzi
 *
 */
package com.bus.chelaile.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.common.TimeLong;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.service.RecordManager;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.thread.Queue;
import com.bus.chelaile.thread.model.QueueObject;
import com.bus.chelaile.util.New;

public class InfoStreamHelp {
    public static final Logger logger = LoggerFactory.getLogger(InfoStreamHelp.class);

    /*
     * 解析广告点击日志, 记录点击数目，控制点击量
     */
//    public static void analysisClick(String line) {
//
//        try {
//            //			String line = URLDecoder.decode(msg, "utf-8");
//            Map<String, String> parameterMap = New.hashMap();
//            //			System.out.println(line.split("\\|#")[12]);
//            try {
//                if (line.contains(Constants.REDIRECT_DOMAIN_NAME) || line.contains("dev.ad.chelaile.net.cn"))
//                    parameterMap = arrayToMap(line.split("\\|#")[3].trim().replace("?", "").replace("/", "").split("&"), "=");
//                //				else if(line.split("\\|#").length > 3 && line.split("\\|#")[12].trim().split(" ").length > 1)
//                //					parameterMap = arrayToMap(line.split("\\|#")[12].trim().split(" ")[1].replace("?", "").replace("/", "").split("&"), "=");
//                else
//                    parameterMap = arrayToMap(line.split("\\|#")[3].trim().replace("?", "").replace("/", "").split("&"), "=");
//            } catch (Exception e) {
//                logger.error("广告 解析点击日志出错,line={}", line);
//                e.printStackTrace();
//                logger.error(e.getMessage(), e);
//                return;
//            }
//            String advId = parameterMap.get("advId");
//            String udid = parameterMap.get("udid");
//            if (udid == null || advId == null) {
//                logger.error("广告为空 line={}", line);
//                return;
//            }
//            if (!Constants.ISTEST)
//                logger.info("点击日志解析结果： advId={}, udid={}", advId, udid);
//            if (!StaticAds.allAds.containsKey(advId)) {
//                if (!Constants.ISTEST) { // 线上需要打印这种情况，测试无需
//                    logger.error("缓存中未发现广告,advId={}, line={}", advId, line);
//                }
//                return;
//            }
//
//            recordClick(udid, advId);
//
//            //			// 广告总点击次数
//            //			QueueObject queueobj = new QueueObject();
//            //			queueobj.setRedisIncrKey(AdvCache.getTotalClickPV(advId));
//            //			Queue.set(queueobj);
//            //			
//            //			// 存储用户点击广告到ocs中
//            //			setClickToRecord(advId, udid);
//            //			
//            //			// 存储项目点击
//            //            String projectId = StaticAds.allAds.get(advId).getProjectId();
//            //            if(StringUtils.isNotBlank(projectId)) {
//            //                String projectClickKey = AdvCache.getProjectClickKey(udid, projectId);
//            //                CacheUtil.incrToCache(projectClickKey, Constants.HALF_YEAR_CACHE_TIME);    // 存储半年
//            //                CacheUtil.incrProjectClick(projectId, 1);
//            //            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


    /*
     * 将日志参数临时转为map
     */
    private static Map<String, String> arrayToMap(String[] array, String splitS) {
        Map<String, String> map = new HashMap<String, String>();
        for (String k_vS : array) {
            String k_v[] = k_vS.trim().split(splitS);
            if (k_vS.trim().split(" ").length > 1) { // 出现类似 |# adv_type:1 |#
                                                         // adv_id:2401 HTTP/1.1
                                                     // |# 200 的情况
                k_v = k_vS.trim().split(" ")[0].split(splitS);
            }
            if (k_v.length < 2) {
                continue;
            }

            if (k_v[1].trim().contains(",")) {
                map.put(k_v[0].trim(), k_v[1].trim().replace(",", "#"));
            } else {
                map.put(k_v[0].trim(), k_v[1].trim());
            }
        }
        return map;
    }

    /*
     * 解析广告点击日志, 记录点击数目，控制点击量
     */
    public static void analysisAtraceClick(String line) {

        try {
            Map<String, String> parameterMap = New.hashMap();
            try {
                String requestUrl = line.split("\\|#")[12].trim();
                int index = requestUrl.indexOf("?");
                parameterMap = arrayToMap(requestUrl.substring(index, requestUrl.length()).split("&"), "=");

            } catch (Exception e) {
                logger.error("atrace 广告 解析点击日志出错,line={}", line);
                e.printStackTrace();
                logger.error(e.getMessage(), e);
                return;
            }
            String s = parameterMap.get("s");
            String traceid = parameterMap.get("traceid");
            String aid = parameterMap.get("aid");
            String pid = parameterMap.get("pid");
            String adid = parameterMap.get("adid");
            String isFakeClick = parameterMap.get("isFakeClick");
            String isRateClick = parameterMap.get("isRateClick");
            String jsid = parameterMap.get("jsid");
            if (jsid == null || pid == null || traceid == null || aid == null) {
                logger.error("atrace 广告为空 line={}", line);
                return;
            }
            String udid = traceid.split("_")[0];
            TimeLong.info("ATRACE 点击日志解析结果：s={}, pid={}, jsid={}, aid={}, udid={}, adid={}, traceid={}, isFake={}, isRate={}", s, pid,
                    jsid, aid, udid, adid, traceid, isFakeClick, isRateClick);
            AnalysisLog.info("ATRACE 点击日志解析结果：s={}, pid={}, jsid={}, aid={}, udid={}, adid={}, traceid={}, isFake={}, isRate={}", s, pid,
                    jsid, aid, udid, adid, traceid, isFakeClick, isRateClick);
            logger.info("ATRACE 点击日志解析结果：s={}, pid={}, jsid={}, aid={}, udid={}, adid={}, traceid={}, isFake={}, isRate={}", s, pid, jsid,
                    aid, udid, adid, traceid, isFakeClick, isRateClick);

            
            // jsid!=adid 这种情况，需要记录的点击广告id，不再是jsid，而是adid
            if(StringUtils.isNoneBlank(jsid) && StringUtils.isNoneBlank(adid) && !jsid.equals(adid)) {
                TimeLong.info("ATRACE 点击日志解析结果, jsid != adid,说明下发了第三方广告和兜底广告，最终兜底生效 ：s={}, pid={}, jsid={}, aid={}, udid={}, adid={}, traceid={}, isFake={}, isRate={}", s, pid,
                        jsid, aid, udid, adid, traceid, isFakeClick, isRateClick);
                AnalysisLog.info("ATRACE 点击日志解析结果：jsid != adid,说明下发了第三方广告和兜底广告，最终兜底生效：s={}, pid={}, jsid={}, aid={}, udid={}, adid={}, traceid={}, isFake={}, isRate={}", s, pid,
                        jsid, aid, udid, adid, traceid, isFakeClick, isRateClick);
                jsid = adid;
            }
            
            if (!StaticAds.allAds.containsKey(jsid)) {
                if (!Constants.ISTEST) { // 线上需要打印这种情况，测试无需
                    logger.error("缓存中未发现广告,advId={}, line={}", jsid, line);
                }
                return;
            }
            // 记录点击
            recordClick(udid, jsid, isFakeClick, isRateClick);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    public static void recordClick(String udid, String advId, String isFakeClick, String isRateClick) {
        // 存储广告点击次数到redis
        QueueObject queueobj = new QueueObject();
        queueobj.setRedisIncrKey(AdvCache.getTotalClickPV(advId));
        Queue.set(queueobj);

        // 存储用户点击广告到ocs中
        InfoStreamHelp.setClickToRecord(udid, advId, isFakeClick, isRateClick);
        
        // 存储项目点击
        String projectId = StaticAds.allAds.get(advId).getProjectId();
        if(StringUtils.isNotBlank(projectId)) {
            String projectClickKey = AdvCache.getProjectClickKey(udid, projectId);
            int expireTime = StaticAds.allAds.get(advId).getProjectIdClickExpireTime();
            if( expireTime == 0 ) {
                expireTime = Constants.HALF_YEAR_CACHE_TIME;
            }
            logger.info("projectClickKey={},expireTime={}",projectClickKey, expireTime);
//            CacheUtil.incrToCache(projectClickKey,expireTime);
            CacheUtil.incrToOftenRedis(projectClickKey,expireTime);
            // CacheUtil.incrToCache(projectClickKey, Constants.HALF_YEAR_CACHE_TIME);    // 存储半年
            
            CacheUtil.incrProjectClick(projectId, 1);
        }
    }
    
    
    /*
     * 将点击记录，存储到缓存中
     * isRecordFakeOnly 是否只记录fake点击
     */
    public static void setClickToRecord(String udid, String advId, String isFakeClick, String isRateClick) {
        AdPubCacheRecord cacheRecord = null;
        String showType = StaticAds.allAds.get(advId).getShowType();
        if (showType.equals(ShowType.LINE_DETAIL.getType())) {
            cacheRecord = AdvCache.getAdPubRecordFromCache(udid, ShowType.LINE_DETAIL.getType());
        } else {
            cacheRecord = AdvCache.getAdPubRecordFromCache(udid, ShowType.DOUBLE_COLUMN.getType());
        }
        if (cacheRecord == null) {
            cacheRecord = new AdPubCacheRecord();
        }

        cacheRecord.buildAdPubCacheRecord(Integer.parseInt(advId), isFakeClick, isRateClick);

        if (showType.equals(ShowType.LINE_DETAIL.getType())) {
            RecordManager.recordAdd(udid, showType, cacheRecord);
        } else {
            RecordManager.recordAdd(udid, ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
        }
    }
    

    public static void main(String[] args) {
        //		String line = "<134>Jul  5 19:22:51 web1 nginx: 106.91.185.21 |# - |# 2017-07-05 19:22:51 |# GET /bus/line!lineDetail.action?idfa=99501C17-3547-494E-BF7C-5E58E1DCB2E2&geo_type=wgs&language=1&geo_lat=29.633844&geo_lng=106.572242&sv=9.1&s=IOS&deviceType=iPhone6s&stats_referer=searchHistory&lchsrc=icon&lineName=153&screenHeight=1334&stats_order=1-9&lng=106.572242&pushkey=&v=5.32.1&udid=d41d8cd98f00b204e9800998ecf8427ec991ae25&stats_act=enter&sign=mXfNZaM0IoKXzJFiCK18tQ==&userAgent=Mozilla/5.0%20(iPhone;%20CPU%20iPhone%20OS%209_1%20like%20Mac%20OS%20X)%20AppleWebKit/601.1.46%20(KHTML,%20like%20Gecko)%20Mobile/13B143&cityState=0&nw=4G&mac=&lineNo=153&wifi_open=1&geo_lac=30.000000&lat=29.633844&gpstype=wgs&cityId=003&push_open=0&vc=10371&userId= HTTP/1.1 |# 200 |# 0.108 |# 1760 |# - |# lite/5.32.1 (iPhone; iOS 9.1; Scale/2.00) |# - |# api.chelaile.net.cn |# 10.168.197.211:6080 |# 200 |# 1499253771438f8f0082366e4ace5223 |# 0.108 |# https";

        //		analysisLineDetail(line);

        //		String s = "Apr 18 05:58:09 web4 nginx: 60.222.40.21 |# - |# 2018-04-18 05:58:09 |# 302 |# 0.000 |# 264 |# - |# Mozilla/5.0 (Linux; Android 7.1.1; OD103 Build/NMF26F; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.97 Mobile Safari/537.36Chelaile/3.49.0 Duiba/1.0.7 |#- |# ad.chelaile.net.cn |# - |# - |# /?advId=14015&adtype=05&udid=823da646-7d0e-40e3-98af-a61942ac8924&utm_medium=banner&adv_id=14015&last_src=app_xiaomi_store&s=android&stats_referer=lineAd&push_open=1&stats_act=switch_stn&userId=unknown&provider_id=1&geo_lt=4&timestamp=1524002279067&geo_lat=38.064121&line_id=85%E8%B7%AF-0&vc=101&sv=7.1.1&v=3.49.0&secret=edee929efc154b0cbca840ee8c5d2561&imei=990009263988027&udid=823da646-7d0e-40e3-98af-a61942ac8924&platform_v=25&utm_source=app_linedetail&stn_name=%E5%86%9B%E6%A2%B0%E5%AD%A6%E9%99%A2%E8%A5%BF&cityId=053&adv_type=5&ad_switch=7&geo_type=gcj&mac=02%3A00%3A00%3A00%3A00%3A00&deviceType=OD103&wifi_open=1&lchsrc=icon&nw=WIFI&AndroidID=7276e4d4a463ab4e&api_type=0&stn_order=15&geo_lac=25.0&accountId=53378370&language=1&first_src=app_qq_sj&geo_lng=114.484855 |# 1 |# 14015 |# https";
        //		//		(s.split("\\|#").length > 3 && s.split("\\|#")[3].trim().split(" ").length > 1)
        //		analysisClick(s);

        //		String clickS = "Jun 13 17:59:48 web7 nginx: 182.18.10.10 |# - |# 2018-06-13 17:59:48 |# 302 |# 0.000 |# 264 |# - |# Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_5 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13G36 Chelaile/5.50.3 |#- |# ad.chelaile.net.cn |# - |# - |# //?advId=14597&adtype=05&udid=aa6f7599a3775bc08cf03fcd44954335d50bf237&mac=&userId=&line_id=0372-13-0&utm_source=app_linedetail&adv_type=5&accountId=7415714&userAgent=Mozilla/5.0%20(iPhone;%20CPU%20iPhone%20OS%209_3_5%20like%20Mac%20OS%20X)%20AppleWebKit/601.1.46%20(KHTML,%20like%20Gecko)%20Mobile/13G36&vc=10553&adv_id=14597&sv=9.3.5&geo_lat=39.994618&screenWidth=640&pushkey=&provider_id=1&geo_lac=65.000000&secret=94c2f2088edc40d29f9b5b7fb15c5116&gpsAccuracy=65.000000&stats_act=auto_refresh&stn_order=6&deviceType=iPhone5s&idfa=D16BA6D5-7B9F-4BA6-A6AF-C1BA8A892652&idfv=DE2BE7E8-2882-427E-84F3-4C5FD86DA067&screenHeight=1136&utm_medium=banner&cityId=030&sign=ifdRRw8r6WtvKzaEM5IpsA==&s=IOS&wifi_open=1&dpi=2&push_open=1&stats_referer=lineAd&ad_switch=7&api_type=1&v=5.50.3&geo_type=wgs&stn_name=%E5%85%AC%E4%BA%A4%E6%80%BB%E5%85%AC%E5%8F%B8&nw=WiFi&language=1&vendor=apple&lchsrc=icon&geo_lng=116.403749 |# 1 |# 14597 |# https";
        //		analysisClick(clickS);

        String line =
                "GET /click?&traceid=29432001-9e50-4242-9422-5093a5e85dcd_1536254374.294&pid=22&ad_order=0&is_backup=0&v=3.61.0&s=android&adv_title=%E8%85%BE%E8%AE%AF%E6%96%B0%E9%97%BB&isFakeClick=0&cost_time=130&adv_image=http%403a%2F%2Fpgdt.ugdtimg.com%2Fgdt%2F0%2FDAAHVPWAUAALQABiBbRdapAjzF3_KQ.jpg%2F0%3Fck%3Dc552b7fe621c039dd4c8b01292aa2ade&isRateClick=0&adv_desc=%E8%85%BE%E8%AE%AF%E6%96%B0%E9%97%BB%E4%BA%8B%E5%AE%9E%E6%B4%BE%EF%BC%8C%E6%90%9C%E9%9B%86%E4%B8%96%E7%95%8C%E6%B6%88%E6%81%AF%E5%8F%AA%E4%B8%BA%E5%91%88%E7%8E%B0%E4%BD%A0&aid=sdk_gdt_2&show_status=0&sdk_result=false HTTP/1.1";
        String requestUrl = line.split(" ")[1];
        int index = requestUrl.indexOf("?");
        System.out.println(index);
        System.out.println(line);
        System.out.println(requestUrl);

        Map<String, String> parameterMap = arrayToMap(requestUrl.substring(index, requestUrl.length()).split("&"), "=");
        System.out.println(JSONObject.toJSONString(parameterMap));

        String lineAtraceClick =
                "120.193.158.112 |# - |# 2018-09-10 19:16:16 |# 200 |# 0 |# 91 |# - |# Dalvik/2.1.0 (Linux; U; Android 7.1.1; OPPO R11s Build/NMF26X) |# - |# atrace.chelaile.net.cn |# - |# - |# /click?&traceid=734df656-345c-4144-8a2b-0fe811d71e84_1536578171.116&pid=22&ad_order=0&is_backup=0&v=3.61.0&s=android&imei=867464034198135&adv_title=58%E5%90%8C%E5%9F%8E&isFakeClick=0&cost_time=39&adv_image=http%403a%2F%2Fpgdt.ugdtimg.com%2Fgdt%2F0%2FDAAE7lKAUAALQABTBbFQWwDAbBxDvh.jpg%2F0%3Fck%3D391cd145a07a8dc202a21b8375681d3a&isRateClick=0&adv_desc=%E6%80%A5%E6%8B%9B%E9%80%81%E9%A4%90%E5%91%98%EF%BC%8C%E5%8C%85%E5%90%83%E5%8C%85%E4%BD%8F%EF%BC%8C%E5%BA%95%E8%96%AA8000%E5%85%83%EF%BC%8C%E5%A4%9A%E5%8A%B3%E5%A4%9A%E5%BE%97%E8%BF%98%E7%BB%99%E6%8F%90%E4%BE%9B%E8%BD%A6%EF%BC%81&aid=sdk_gdt_2&show_status=0&sdk_result=false |# http |# - |#  ";
        InfoStreamHelp.analysisAtraceClick(lineAtraceClick);
    }

}
