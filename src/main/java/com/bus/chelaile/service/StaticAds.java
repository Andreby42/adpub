package com.bus.chelaile.service;

import java.io.*;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.common.PositionJs;
import com.bus.chelaile.common.ReplaceJs;
import com.bus.chelaile.common.Text;
import com.bus.chelaile.model.PlacementCache;
import com.bus.chelaile.model.PlacementInfo;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

import scala.reflect.generic.Constants.Constant;

/**
 * 保存静态的广告数据
 * 
 * @author linzi
 *
 */
public class StaticAds {

	protected static final Logger logger = LoggerFactory.getLogger(StaticAds.class);

	// 指定udid或者accountId的广告,key udid or accountId,mapKey showType
	public static final Map<String, Map<String, List<String>>> adsMap = New.hashMap();
	// 除去指定udid或accountId的广告,key showType
	public static final Map<String, List<AdContentCacheEle>> allAdsMap = New.hashMap();
	// 所有广告
	public static final Map<String, AdContent> allAds = New.hashMap();

	// 将按照用户id投放的广告组合 单独存储程map，alsMap中value改成这个map的key
	public static final Map<String, AdContentCacheEle> allAdContentCache = New.hashMap();

	// 黑名单,key 广告id,value key udid
	public static Map<Integer, Map<String, String>> blackListMap = New.hashMap();

	// 按分钟计算广告的投放比例
	public static Map<String, Double> minuteTimes = New.hashMap();
	// key=advId#ruleId, value=《key=minute(eg. 17:06, value=该分钟需要投放的次数)》
	public static Map<String, Map<String, Integer>> minuteNumber = New.hashMap();
	// key=advId, value=tbkTitle存储到redis中的key
	public static Map<Integer, String> advTBKTitleKey = New.hashMap();
	
	// 配置项的缓存
	public static Map<String, String> SETTINGSMAP = New.hashMap();
	private static String settingKeys = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "setting_keys", "AD_SETTING_linefeed_screenHeight;");
	// js原始文件的缓存
	public static Map<String, String> JS_FILE_STR = New.hashMap();
	// 新的替换
	public static Map<String,List<Text>> NEW_JS_FILE_STR = New.hashMap();
	
	// 存储placementId的缓存
	public static Map<String, String> androidPlacementMap = New.hashMap();
	public static Map<String, String> iosPlacementMap = New.hashMap();
	
	public static boolean hasSendEmailhalf = false;
	public static boolean hasSendEmail = false;

	/**
	 * 通过唯一标识返回广告
	 * 
	 * @param identification
	 * @param showType
	 * @return
	 */
	public static List<String> getIdentificationAdsList(String identification, String showType) {
		if (adsMap.containsKey(identification)) {
			return adsMap.get(identification).get(showType);
		}
		return null;
	}

	/**
	 * 设置有唯一标识的广告
	 * 
	 * @param identification
	 * @param list
	 * @param showType
	 */
	public static void setIdentificationToMap(String identification, String acKey, String showType) {
		if (adsMap.containsKey(identification)) {
			Map<String, List<String>> map = adsMap.get(identification);
			if (map.containsKey(showType)) {
				map.get(showType).add(acKey);
			} else {
				Map<String, List<String>> tempMap = New.hashMap();
				List<String> list = New.arrayList();
				list.add(acKey);
				tempMap.put(showType, list);
				tempMap.putAll(map); // 02.24修改
				adsMap.put(identification, tempMap);
			}
		} else {
			Map<String, List<String>> tempMap = New.hashMap();
			List<String> list = New.arrayList();
			list.add(acKey);
			tempMap.put(showType, list);
			adsMap.put(identification, tempMap);
		}
	}

	/**
	 * 设置没有标识的广告
	 * 
	 * @param showType
	 * @param list
	 */
	public static void setNoIdentificationToMap(String showType, AdContentCacheEle ac) {
		if (allAdsMap.containsKey(showType)) {
			allAdsMap.get(showType).add(ac);
		} else {
			List<AdContentCacheEle> list = New.arrayList();
			list.add(ac);
			allAdsMap.put(showType, list);
		}
	}

	/*
	 * 保存所有的广告到缓存
	 */
	public static void addAds(AdContent ad) {
		allAds.put(String.valueOf(ad.getId()), ad);
	}

	/*
	 * 将按照用户投放的广告和规则组合体 放入Map中
	 */
	public static void AddAdContentCache(String key, AdContentCacheEle adContentEle) {
		allAdContentCache.put(key, adContentEle);
	}

	/**
	 * 初始化
	 */
	public static void init() {
		adsMap.clear();
		allAdsMap.clear();
		allAds.clear();
		allAdContentCache.clear();
		blackListMap.clear();
		minuteTimes.clear();
		minuteNumber.clear();
		advTBKTitleKey.clear();
		JS_FILE_STR.clear();
		SETTINGSMAP.clear();
		androidPlacementMap.clear();
		iosPlacementMap.clear();
		NEW_JS_FILE_STR.clear();
		
		readSettings();
		readJSFILESTR();
	}

    private static void readJSFILESTR() {
        Reader reader = null;
        try {
            String fileStr = "/data/advConfig/js/";
            if(Constants.ISTEST)
                fileStr = "/data/advConfig/test/js/";
            if( Constants.ISDEV ) {
            	fileStr = "D:\\js\\android\\";
            }
            File file = new File(fileStr);
            File[] tempList = file.listFiles();
            for (int i = 0; i < tempList.length; i++) {
                reader = new InputStreamReader(new FileInputStream(tempList[i]), "UTF-8");
                int tempchar;
                StringBuilder jsStr = new StringBuilder();
                while ((tempchar = reader.read()) != -1) {
                    if(((char)tempchar) != '\r') {
                        jsStr.append((char)tempchar);
                    }
                }
                JS_FILE_STR.put(tempList[i].getName().split("\\.")[0], jsStr.toString());
                NEW_JS_FILE_STR.put(tempList[i].getName().split("\\.")[0], ReplaceJs.parse(jsStr.toString()));
                logger.info(tempList[i].getName().split("\\.")[0]+"="+jsStr.length());
                logger.info(tempList[i].getName().split("\\.")[0] + "----->" + ReplaceJs.parse(jsStr.toString()));
//                reader.close();
            }
        } catch (Exception e) {
            logger.error("读取js文件到缓存出错！ ", e);
            e.printStackTrace();
        }
            
        try {
            String iosFileStr = "/data/advConfig/iosjs/";
            if (Constants.ISTEST)
                iosFileStr = "/data/advConfig/test/iosjs/";
            if (Constants.ISDEV) {
                iosFileStr = "D:\\js\\ios\\";
            }
            File iosFile = new File(iosFileStr);
            File[] ios_tempList = iosFile.listFiles();
            for (int i = 0; i < ios_tempList.length; i++) {
                reader = new InputStreamReader(new FileInputStream(ios_tempList[i]), "UTF-8");
                int tempchar;
                StringBuilder jsStr = new StringBuilder();
                while ((tempchar = reader.read()) != -1) {
                    if (((char) tempchar) != '\r') {
                        jsStr.append((char) tempchar);
                    }
                }
                JS_FILE_STR.put(ios_tempList[i].getName().split("\\.")[0], jsStr.toString());
                NEW_JS_FILE_STR.put(ios_tempList[i].getName().split("\\.")[0], ReplaceJs.parse(jsStr.toString()));
                reader.close();
            }
        } catch (Exception e) {
            logger.error("读取js文件到缓存出错！ ", e);
            e.printStackTrace();
        }
    }

    private static void readSettings() {
        if (settingKeys != null) {
            for (String key : settingKeys.split(";")) {
                if (CacheUtil.getFromRedis(key) != null) {
                    SETTINGSMAP.put(key, (String) CacheUtil.getFromRedis(key));
                }
            }
        }
        if(! SETTINGSMAP.containsKey(Constants.SETTING_COSE_AD_KEY)) {
            SETTINGSMAP.put(Constants.SETTING_COSE_AD_KEY, "86400"); // 默认一天
        }
    }

    public static boolean isBlack(int advId, String udid) {
		if (udid == null) {
			return false;
		}
		Map<String, String> map = blackListMap.get(advId);
		// logger.info("advId={},map={}",advId,map);
		if (map != null && map.containsKey(udid)) {
			return true;
		}
		return false;
	}

	/**
	 * 通过类型找到所有广告
	 * 
	 * @param showType
	 * @return
	 */
	public static List<AdContentCacheEle> getAllAdsMap(String showType) {
		return allAdsMap.get(showType);
	}

	/*
	 * 从按udid投放map中 删除该广告 在 不按udid投放的map中 加入该广告
	 */
	public static void delUdidRule(String udid, int advId, ShowType showType) {

		// 上锁
		SynchronizationControl.setReloadSynLockState(true);

		// 解锁
		SynchronizationControl.setReloadSynLockState(false);
	}

	/*
	 * 初始化存储广告对应的分钟投放数 key = advId#ruleId
	 */
	public static void setMinuteNumbers(String key, String minuteStr, int number) {
		if (minuteNumber.containsKey(key)) {
			minuteNumber.get(key).put(minuteStr, number);
		} else {
			Map<String, Integer> numberMap = New.hashMap();
			numberMap.put(minuteStr, number);
			minuteNumber.put(key, numberMap);
		}
	}

	/*
	 * 初始化存储广告对应的分钟投放数 key = advId#ruleId
	 * 初始化保证了每分钟都会有，保险起见还是加上判断
	 */
	public static int getMinuteNumbers(String key, String minuteStr, String udid) {
		if (minuteNumber.containsKey(key) && minuteNumber.get(key).containsKey(minuteStr)) {
			return minuteNumber.get(key).get(minuteStr);
		} else {
			return 0;
		}
	}
	
	
	   /*
     * 将点击记录，存储到缓存中
     */
    public static void setClickToRecord(String advId, String udid) {
        AdPubCacheRecord cacheRecord = null;
        // 放缓存的时候除了线路详情就是双栏
        if(! allAds.containsKey(advId)) {
            logger.error("出现缓存中不存在advId点击上报事件， udid={}, advId={}", udid, advId);
            return;
        }
        String showType = allAds.get(advId).getShowType();
        if (showType.equals(ShowType.LINE_DETAIL.getType())) {
            cacheRecord = AdvCache.getAdPubRecordFromCache(udid, ShowType.LINE_DETAIL.getType());
        } else {
            cacheRecord = AdvCache.getAdPubRecordFromCache(udid, ShowType.DOUBLE_COLUMN.getType());
        }
        if (cacheRecord == null) {
            cacheRecord = new AdPubCacheRecord();
        }
        logger.info("点击记录， udid={}, showType={}, advId={}, record={}", udid, showType, advId, JSONObject.toJSONString(cacheRecord));
        cacheRecord.buildAdPubCacheRecord(Integer.parseInt(advId), true);
        logger.info("点击记录， udid={}, showType={}, advId={}, record={}", udid, showType, advId, JSONObject.toJSONString(cacheRecord));
        
        if (showType.equals(ShowType.LINE_DETAIL.getType())) {
            RecordManager.recordAdd(udid, showType, cacheRecord);
        } else {
            RecordManager.recordAdd(udid, ShowType.DOUBLE_COLUMN.getType(), cacheRecord);
        }
    }

	public static void main(String[] args) {

//		System.out.println(minuteTimes.get("adf "));
//		Map<String, Integer> a = New.hashMap();
//		System.out.println(a.get("adf"));
		
		String a1 = "banner.js";
		System.out.println(a1.split("\\.")[0]);

	}
}
