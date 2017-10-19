package com.bus.chelaile.service;




import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;


import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;

import com.bus.chelaile.util.JsonBinder;
import com.bus.chelaile.util.New;



public class AdvInvalidService {
	// 保存所有的没有广告的accountId
	public static final String invalidAcountIdKey = "invalidAcountIdKey";
	
	 //	保存取消了广告的用户accountId
    public static Map<String,String> invalidAccountIdMap = New.hashMap();

	public void invalidUser(String udid,String accountId,String startTime,String endTime) throws Exception{
		if( udid == null || udid.equals("") ){
			throw new IllegalArgumentException("udid为空");
		}
		
		if( accountId == null ){
			return;
		}
	
		Map<String,String> map = getInvalidCount();
		
		map.put(accountId, endTime);
		
		String json = JsonBinder.toJson(map, JsonBinder.nonNull);
		
		CacheUtil.setNew(invalidAcountIdKey, Constants.LONGEST_CACHE_TIME, json);
		
	}
	/**
	 * 判断是否失效
	 * @param accountId
	 * @return
	 * @throws Exception
	 */
	public boolean isInvalid(String accountId) throws Exception{
	
		String endTime = invalidAccountIdMap.get(accountId);
		if( endTime == null ){
			return false;
		}
		Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endTime);
		
		Date nowDate = new Date();

		if( endDate.compareTo(nowDate) > 0){
			return true;
		}
		//	小于当前日期
		clearAccountId(accountId);
		return false;
	}
	
	
	/**
	 * 得到失效广告的accountId
	 * @return
	 * @throws Exception 
	 */
	public Map<String,String> getInvalidCount() throws Exception{
		String value = (String) CacheUtil.getNew(invalidAcountIdKey);
		if( value == null ){
			return New.hashMap();
		}
		return JsonBinder.fromJsonList(value,
				new TypeReference<Map<String, String>>() {
				}, JsonBinder.nonNull);
	}
	
	/**
	 * 清除
	 * @param accountId
	 * @throws Exception 
	 */
	public void clearAccountId(String accountId) throws Exception{
		Map<String,String> map = getInvalidCount();
		if( map.size() == 0 ){
			return;
		}
		map.remove(accountId);
		String json = JsonBinder.toJson(map, JsonBinder.nonNull);
		
		CacheUtil.setNew(invalidAcountIdKey, Constants.LONGEST_CACHE_TIME, json);
	}
	
}
