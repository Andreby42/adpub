package com.bus.chelaile.thread;

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.common.TimeLong;
import com.bus.chelaile.service.AdvInvalidService;
import com.bus.chelaile.util.JsonBinder;



public class ReloadInvalidAccountIdTimer{

	protected static final Logger logger = LoggerFactory.getLogger(ReloadInvalidAccountIdTimer.class);
	
    public void run(){
    	updateInvalidAccountIdMap();
    }
   
	
	
	private void updateInvalidAccountIdMap(){
	 	try {
    		String value = (String) CacheUtil.getNew(AdvInvalidService.invalidAcountIdKey);
    		if( value == null ){
    			return;
    		}
    		AdvInvalidService.invalidAccountIdMap = JsonBinder.fromJsonList(value,
    				new TypeReference<Map<String, String>>() {
    				}, JsonBinder.nonNull);
    		Date date = new Date();
    		
    		int hour = date.getHours();
    		int minute = date.getMinutes();
    		if( date.getHours() == 23 && minute >= 10 && minute < 15 ){
    			String json = JsonBinder.toJson(AdvInvalidService.invalidAccountIdMap, JsonBinder.nonNull);
    			CacheUtil.setNew(AdvInvalidService.invalidAcountIdKey, Constants.LONGEST_CACHE_TIME, json);
    			TimeLong.info("更新无效广告的accountId;"+json);
    		}
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
			return;
		}
	}
}
