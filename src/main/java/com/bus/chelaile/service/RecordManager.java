package com.bus.chelaile.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AdvCache;

import com.bus.chelaile.model.record.AdPubCacheRecord;


public class RecordManager {
	
	protected static final Logger logger = LoggerFactory
			.getLogger(RecordManager.class);
	
	  /**
     * 访问量累加
     * @param rule
     * @param advParam
     * @param ruleParam
     * @param ad
     * @param showType
     * @return	true	增加成功,false 已经达到上限
     */
    public static boolean recordAdd(String udid,String showType,AdPubCacheRecord cacheRecord){
        AdvCache.setAdPubRecordToCache(cacheRecord, udid, showType);
        return true;
    }
}
