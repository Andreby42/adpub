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
            analysisOpenAdvExhibit(str);
        } else if (str.contains(maidian_log) && str.contains(Constants.ADV_CLICK) && str.contains(Constants.WXAPP_SRC)) {
            analysisWXAppClick(str);
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
                cacheRecord = new AdPubCacheRecord();
            }
            logger.info("更新开屏广告前***， cacheRecord={}", JSONObject.toJSONString(cacheRecord));
            cacheRecord.buildAdPubCacheRecord(Integer.parseInt(advId));
            cacheRecord.setOpenAdHistory(new AdCategory(Integer.parseInt(advId), 1, -1));
            cacheRecord.setAndUpdateOpenAdPubTime(Integer.parseInt(advId));
            RecordManager.recordAdd(udid, ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
            logger.info("更新开屏广告后###， cacheRecord={}", JSONObject.toJSONString(cacheRecord));
        }
    }

    private Map<String, String> preHandleMaidianLog(String line) {
        String[] segs = line.split(" \\|# ");
        String content = segs[3].trim();
        int endIdx = content.lastIndexOf(" ");
        content = content.substring(0, endIdx);
        String encodedURL = null;
        try {
            encodedURL = URLDecoder.decode(content, "UTF-8");
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
        System.out.println(encodedURL.substring(index + 12));

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
}
