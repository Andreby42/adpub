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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.kafka.InfoStreamHelp;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.service.RecordManager;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.thread.Queue;
import com.bus.chelaile.thread.model.QueueObject;
import com.bus.chelaile.util.New;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class MaidianLogsHandle implements Runnable {
    private KafkaStream<byte[], byte[]> m_stream;
    private int m_threadNumber;

    public static final Logger logger = LoggerFactory.getLogger(MaidianLogsHandle.class);

    public MaidianLogsHandle(KafkaStream<byte[], byte[]> a_stream, int a_threadNumber) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
    }

    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext()) {
            String log = null;
            try {
                log = new String(it.next().message(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            filterContent(log.trim());
        }
    }

    // return 9: 开屏广告展示
    // return 10: 小程序点击
    private void filterContent(String str) {
        String maidian_log = Constants.MAIDIAN_LOG;
        if (Constants.ISTEST) {
            maidian_log = Constants.TEST_MAIDIAN_LOG;
        }
        if (str.contains(maidian_log) && str.contains(Constants.ADV_EXHIBIT) && str.contains(Constants.OPEN_ADV_KEYWORD)) {
//            logger.info("读到展示埋点日志： str={}", str);
            try {
                analysisOpenAdvExhibit(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (str.contains(maidian_log) && str.contains(Constants.ADV_CLICK) && str.contains(Constants.WXAPP_SRC)) {
            logger.info("读到点击埋点日志： str={}", str);
            try {
                analysisWXAppClick(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    /**
     * 解析小程序的点击日志
     * 并记录进缓存
     * @param str
     */
    private void analysisWXAppClick(String line) {
        Map<String, String> params = preHandleMaidianLog(line);
        if(params != null) {
            String udid = params.get("userId");
            String advId = params.get("adv_id");
            if (udid == null || advId == null) {
                logger.info("广告为空 line={}", line);
                return;
            }
            if (StaticAds.allAds.get(advId) == null) {
                logger.error("缓存中未发现广告,advId={}, line={}", advId, line);
                return;
            }
            logger.info("点击埋点得到点击日志： udid={}, advId={}", udid, advId);
            
            // 存储广告点击次数到redis
            QueueObject queueobj = new QueueObject();
            queueobj.setRedisIncrKey(AdvCache.getTotalClickPV(advId));
            Queue.set(queueobj);
            
            // 存储用户点击广告到ocs中
            InfoStreamHelp.setClickToRecord(advId, udid);
            
        }
    }

    /**
     * 解析开屏广告展示埋点日志
     * 并且记录进缓存
     * @param line
     */
    private void analysisOpenAdvExhibit(String line) {
        Map<String, String> params = preHandleMaidianLog(line);
        if (params != null) {
            String udid = params.get("udid");
            String advId = params.get("adv_id");
            if (udid == null || advId == null) {
                logger.info("广告为空 line={}", line);
                return;
            }

            if (StaticAds.allAds.get(advId) == null) {
                logger.error("缓存中未发现广告,advId={}, line={}", advId, line);
                return;
            }

            // 记录缓存， 开屏广告‘展示’|‘发送’ + 1
            logger.info("更新开屏 udid={}, advId={}", udid, advId);
            AdPubCacheRecord cacheRecord = null;
            try {
                cacheRecord = AdvCache.getAdPubRecordFromCache(udid, ShowType.DOUBLE_COLUMN.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(cacheRecord == null) {
                cacheRecord = new AdPubCacheRecord();
            }
//            logger.info("更新开屏广告前***， cacheRecord={}", JSONObject.toJSONString(cacheRecord));
            cacheRecord.buildAdPubCacheRecord(Integer.parseInt(advId));
            cacheRecord.setOpenAdHistory(new AdCategory(Integer.parseInt(advId), 1, -1));
            cacheRecord.setAndUpdateOpenAdPubTime(Integer.parseInt(advId));
            RecordManager.recordAdd(udid, ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
//            logger.info("更新开屏广告后###， cacheRecord={}", JSONObject.toJSONString(cacheRecord));
        }
    }

    private Map<String, String> preHandleMaidianLog(String line) {
        String encodedURL = null;
        try {
            encodedURL = URLDecoder.decode(line, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        int index = 0;
        Map<String, String> params = New.hashMap();
        if(encodedURL.contains("ADV_EXHIBIT")) {
            index = encodedURL.indexOf("ADV_EXHIBIT");
            params = paramsAnalysis(encodedURL.substring(index + 12));
        } else if(encodedURL.contains("ADV_CLICK")) {
            index = encodedURL.indexOf("ADV_CLICK");
            params = paramsAnalysis(encodedURL.substring(index + 10));
        }
//        System.out.println(encodedURL.substring(index + 12));

        return params;
    }

    private static Map<String, String> paramsAnalysis(String url) {
        Map<String, String> params = New.hashMap();
        String entrys[] = url.split(" \\|# ");
        for (String s : entrys) {
            String[] maps = s.split(":");
            try {
                if (maps != null && maps.length >= 2)
                    params.put(maps[0], URLDecoder.decode(maps[1], "UTF-8"));
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
//        preHandleMaidianLog(s);
    }
}
