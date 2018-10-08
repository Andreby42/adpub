package com.bus.chelaile.model.record;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;






import com.bus.chelaile.util.DateUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.innob.response.ad.NativeResponse;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.strategy.AdCategory;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;


/**
 * 这个类是用户的投放记录，用于一些涉及到流量分配时的数据记录
 * 目前包含：
 * 用户每天投放的广告、每天点击的广告
 * 
 * 开屏广告最近一次投放时间
 * 【老-线路详情页投放记录】
 * 开屏广告投放记录--->用于之前历史上的一些‘策略’投放
 * 记录广告‘没有投放’的次数，用于控制每条广告的投放间隔
 * 其他……………… 
 *
 * 放到ocs里面的key只有线路详情和双栏两个类型
 */
public class AdPubCacheRecord {

	protected static final Logger logger = LoggerFactory
			.getLogger(AdPubCacheRecord.class);

	//记录自采买广告的点击次数，和展示次数
	//取advId=1，格式类似：\"cacheRecordMap\":{1:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-22\":7}}}
	Map<Integer, CacheRecord> cacheRecordMap = New.hashMap();
	
	// 日期， ClickCacheRecord 
	private Map<String, ClickCacheRecord> aidTitlesMap = New.hashMap();
	
	// 一个时间段之内的广告发送次数，和用户第一次请求该广告时间，
	//取ruleId=9，格式类似：{\"2016-11-22-9-10:00-17:00\":{\"count\":4,\"time\":1479802720999}}
	Map<String, TimeAndCount> firstClickMap = New.hashMap();
	// 保存用户不感兴趣的广告,value是时间
	Map<String, uninterested> uninterestedMap = New.hashMap();
	// 存放开屏广告最近一次投放时间,用于‘最小投放时间间隔’
	//格式类似：{"2018-01-18":{13289:1516280051535,13286:1516290063402}}
	private Map<String, Map<Integer, Long>> todayOpenAdPubTime = New.hashMap();

	// Rule中的cacheTime
	Long cacheTime = 0L;
	
	// 线路详情的历史记录。
	// 第一个key是日期
	// 第二个key是广告的AdCategory结构体，如果没有投放广告。详情页会记录历史，adId为-1。 非详情页不会记录
	private Map<String, Map<AdCategory, Integer>> todayHistoryMap = New
			.hashMap();
	// 开屏的历史记录，规则同上。只不过没记录不投放广告的情况
	private Map<String, Map<AdCategory, Integer>> todayOpenHistoryMap = New
			.hashMap();
	// 新增 feed流广告的历史记录也放入其中。不同的记录的是不投放广告的情况，投放之后置零
	// 格式类似：{"2018-03-01":{13633:0,-1:1,13634:2}}
//	private Map<String, Map<String, Map<Integer, Integer>>> todayNoFeedAdHistoryMap = New.hashMap();
	
	private Map<String, Map<String, Map<Integer, Integer>>> todayNoAdHistoryMap = New.hashMap();
	
    
	/***********分割线******
    ***********以下部分应该是没有用了的***********/
	// 取消广告
	private invalidAdv invalidInfo;
	// 存储uv的广告 key  广告id,value 日期
	private Map<Integer,String> uvMap= New.hashMap();

	// 第三方广告信息，该缓存只保留1个小时
	private Map<String, ApiRecord> apiRecordMap;
	
	public static int innmobiSaveTime = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"innmobiSaveTime","30"));


	public void setInvalidAdv(String startDate,String endDate,String accountId) throws ParseException{
		invalidInfo = new invalidAdv();
		String pattern = "yyyy-MM-dd";
		invalidInfo.setStartDate(new SimpleDateFormat(pattern).parse(startDate)) ;
		invalidInfo.setEndDate(new SimpleDateFormat(pattern).parse(endDate));
		invalidInfo.setAccountId(accountId);
	}
	
	public void setAdHistory(AdCategory ad) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		if (todayHistoryMap.containsKey(todayStr)) {
			Map<AdCategory, Integer> temp = todayHistoryMap.get(todayStr);
			Integer value = temp.get(ad);
			if (value == null) {
				value = 1;
			} else {
				value++;
			}

			temp.put(ad, value);

		} else {
			Map<AdCategory, Integer> temp = New.hashMap();
			temp.put(ad, 1);
			todayHistoryMap.put(todayStr, temp);
		}
	}
	

	public void setOpenAdHistory(AdCategory ad) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		if (todayOpenHistoryMap.containsKey(todayStr)) {
			Map<AdCategory, Integer> temp = todayOpenHistoryMap.get(todayStr);
			Integer value = temp.get(ad);
			if (value == null) {
				value = 1;
			} else {
				value++;
			}

			temp.put(ad, value);

		} else {
			Map<AdCategory, Integer> temp = New.hashMap();
			temp.put(ad, 1);
			todayOpenHistoryMap.put(todayStr, temp);
		}
	}

	// feed流广告投放记录。 投放后计数置零。不投放，计数+1
	public void setNoAdHistoryMap(List<Integer> adIds, String showType) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		if (todayNoAdHistoryMap.containsKey(todayStr)) {
			Map<String, Map<Integer, Integer>> tempShowType = todayNoAdHistoryMap.get(todayStr);
            if (tempShowType.containsKey(showType)) {
                Map<Integer, Integer> temp = tempShowType.get(showType);
                // 对应的广告置零
                for (Integer i : adIds) {
                    temp.put(i, 0);
                }
                // 如果有其他广告，+1	
                for(Entry<Integer, Integer> entry : temp.entrySet()) {
                    if(! adIds.contains(entry.getKey())) {
                        entry.setValue(entry.getValue() + 1);
                    }
                }
            } else {
                Map<Integer, Integer> temp = New.hashMap();
                for (Integer i : adIds) {
                    temp.put(i, 0);
                }
                tempShowType.put(showType, temp);
            }
			
		} else {
		    Map<String, Map<Integer, Integer>> tempShowType = New.hashMap();
			Map<Integer, Integer> temp = New.hashMap();
			for(Integer i : adIds) {
				temp.put(i, 0);
			}
			tempShowType.put(showType, temp);
			todayNoAdHistoryMap.put(todayStr, tempShowType);
		}
	}
	
	// 设置开屏广告最近一次打开时间
	public void setAndUpdateOpenAdPubTime(int adId) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		if(todayOpenAdPubTime.containsKey(todayStr)) {
			Map<Integer, Long> temp = todayOpenAdPubTime.get(todayStr);
			temp.put(adId, System.currentTimeMillis());
		} else {
			Map<Integer, Long> temp = New.hashMap();
			temp.put(adId, System.currentTimeMillis());
			todayOpenAdPubTime.put(todayStr, temp);
		}
	}
	
	/**
	 * 只返回今天的历史记录
	 * 
	 * @return
	 */
	public Map<AdCategory, Integer> todayAdHistoryList() {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		return todayHistoryMap.get(todayStr);
	}
	
	public Map<AdCategory, Integer> todayOpenAdHistoryList() {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		return todayOpenHistoryMap.get(todayStr);
	}
	
	
	public Map<String, Map<Integer, Integer>> todayNoAdHistoryList() {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		return todayNoAdHistoryMap.get(todayStr);
	}
	
	public Map<Integer, Long> todayOpenAdPubTimeList() {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		return todayOpenAdPubTime.get(todayStr);
	}
	
	
	// feedAd的最小投放间隔
	// todayNoFeedAdHistoryMap中计数了feedAd投放次数。每次投放置零，不投放+1。达到阈值可以再次投放
	// 找不到广告记录，可以投放
	public boolean canPubFeedAd(AdContent ad, Rule rule, String showType) {
		int adId = ad.getId();
		// 说明今天投放过该广告了
		if (todayNoAdHistoryList() != null && todayNoAdHistoryList().containsKey(showType) 
		        && todayNoAdHistoryList().get(showType).containsKey(adId)) {
			if (todayNoAdHistoryList().get(showType).get(adId) < rule.getMinIntervalPages()) {
				return false;
			}
		}
		return true;
	}
	
	// 开屏广告最小展示时间间隔
	public boolean hasPassIntervalTime(int id, long minIntervalTime) {
		if (todayOpenAdPubTimeList() != null && todayOpenAdPubTimeList().containsKey(id)) {
			if (System.currentTimeMillis() - todayOpenAdPubTimeList().get(id) < minIntervalTime) {
				return false;
			}
		}
		return true;
	}
	

	// 第一访问时间放到内存中
	// 当天保留一个第一次访问时间即可。以前的逻辑疑似存在问题。以前会根据adTimeCounts来设置用户投放记录，每个时段最少有一条，其实不够严谨, 2017-06-15
	public void setQueryTimeAndCount(String ruleId) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		String key = todayStr + "-" + ruleId;
		if (firstClickMap.containsKey(key)) {
			TimeAndCount ta = firstClickMap.get(key);
			long dfTime = (System.currentTimeMillis() - ta.getTime());
			dfTime /= 1000;
			if (cacheTime >= dfTime) {
				ta.setCount(ta.getCount() + 1);
			} else {				// 如果第一次时间到现在，已经超过 cacheTime，那么重置计数。
				ta.setTime(System.currentTimeMillis());
				ta.setCount(1);
			}
			return;
		}
		TimeAndCount ta = new TimeAndCount();
		ta.setCount(1);
		ta.setTime(System.currentTimeMillis());
		firstClickMap.put(key, ta);
	}

	public Map<Integer, CacheRecord> getCacheRecordMap() {
		return cacheRecordMap;
	}

	public void setCacheRecordMap(Map<Integer, CacheRecord> cacheRecordMap) {
		this.cacheRecordMap = cacheRecordMap;
	}

	public static AdPubCacheRecord fromJson(String jsonStr) {
		AdPubCacheRecord adPubCacheRecord = null;
		try {
			adPubCacheRecord = JSON
					.parseObject(jsonStr, AdPubCacheRecord.class);
			// adPubCacheRecord = mapper.readValue(jsonStr,
			// AdPubCacheRecord.class);
		} catch (Exception e) {
			logger.info("取缓存出错, jsonStr={}", jsonStr);
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return adPubCacheRecord;
	}

	public String toJson() {
		// ObjectWriter writer = mapper.writer();
		try {
			return JSON.toJSONString(this);
			// return writer.writeValueAsString(this);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	// 线路详情的特殊设置
	public int getAdTimeCountsQueryCount(String ruleId) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		String key = todayStr + "-" + ruleId;
		if (firstClickMap.containsKey(key)) {
			return firstClickMap.get(key).getCount();
		} else {
			return 0;
		}
	}
	

	// 发送 +1
    public void buildAdPubCacheRecord(int adId) {
        String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
        CacheRecord cacheRecord = cacheRecordMap.get(adId);
        if (cacheRecord == null) {
            cacheRecord = new CacheRecord(0);
            cacheRecordMap.put(adId, cacheRecord);
        }

        cacheRecord.putDayCountMap(todayStr, 1);
    }
    
    // 点击 +1
    public void buildAdPubCacheRecord(int adId, String isFakeClick, String isRateClick) {
        String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
        CacheRecord cacheRecord = cacheRecordMap.get(adId);
        if (cacheRecord == null) {
            cacheRecord = new CacheRecord(0);
            cacheRecordMap.put(adId, cacheRecord);
        }

        cacheRecord.incrClickCount();
        cacheRecord.putDayClickMap(todayStr, 1);
        if ((isFakeClick != null && isFakeClick.equals("1")) || (isRateClick != null && isRateClick.equals("1"))) {
            cacheRecord.incrFakeCount();
            cacheRecord.putDayRateMap(todayStr, 1);
        }
    }
    
    // 点击 +1
    // 与之前不同在于： 这条记录偏重于记录pid和aid，而不是adid
    public void buildAdClickRecord(String pid, String aid, String title) {
        String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
        if(! aidTitlesMap.containsKey(todayStr)) {
            ClickCacheRecord record = new ClickCacheRecord(pid, aid, title);
            aidTitlesMap.put(todayStr, record);
        } else {
            ClickCacheRecord record = aidTitlesMap.get(todayStr);
            record.addClickRecord(pid, aid, title);
        }
    }
    
    // 获取今日的点击标题列表
    public List<String> getTodayClickedTitles(String pid, String aid) {
        String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
        if(! aidTitlesMap.containsKey(todayStr)) {
            return null;
        } else {
            ClickCacheRecord record = aidTitlesMap.get(todayStr);
            return record.getListByPidAndAid(pid, aid);
        }
    }
    
    
	
	
	public boolean hasClicked(int adId) {
		if (cacheRecordMap.containsKey(adId)) {
			int clickCount = cacheRecordMap.get(adId).getClickCount();
			if(clickCount > 0) {
				return true;
			}
		}
		return false;
	}
	
    public boolean hasClickedToday(int adId) {
        if (cacheRecordMap.containsKey(adId)) {
            String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
            CacheRecord cacheRecord = cacheRecordMap.get(adId);
            if (cacheRecord.getDayClickMap().size() > 0 && cacheRecord.getDayClickMap().containsKey(todayStr)) {
                return true;
            }
        }
        return false;
    }
	

	public boolean todayCanPub(AdContent ad, Rule rule) {
		int adId = ad.getId();
		if (cacheRecordMap.containsKey(adId)) {
			CacheRecord cacheRecord = cacheRecordMap.get(adId);
			Map<String, Integer> dayCountMap = cacheRecord.getDayCountMap();
			Map<String ,Integer> dayClickMap = cacheRecord.getDayClickMap();
			Map<String, Integer> dayFakeMap = cacheRecord.getDayRateMap();
			int clickCount = rule.getClickCount();
			int pclickCount = rule.getPclickCount();
			
			int fakeCount = rule.getFakeCount();
            int pfakeCount = rule.getPfakeCount();
			
			int days = rule.getDays();
			int perDayCount = rule.getPerDayCount();
//			int totalCount = rule.getTotalCount(); // 针对一个人的
			String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
			if (clickCount > 0) { // 说明配置了点击次数属性，只判断该属性即可，这个是单人点击次数限制
				if (cacheRecord.getClickCount() >= clickCount) {
					return false;
				}
			}
			if (pclickCount > 0) { // 每人每天点击次数上限
			    if(dayClickMap.containsKey(todayStr) && dayClickMap.get(todayStr) >= pclickCount) {
			        return false;
			    }
			}
			
			if (fakeCount > 0) { // 说明配置了 误点击 次数属性，只判断该属性即可，这个是单人点击次数限制
                if (cacheRecord.getFakeCount() >= fakeCount) {
                    return false;
                }
            }
            if (pfakeCount > 0) { // 每人每天 误点击 次数上限
                if(dayFakeMap.containsKey(todayStr) && dayFakeMap.get(todayStr) >= pfakeCount) {
                    return false;
                }
            }
			
            if (days > 0) {
                if (dayCountMap.size() > days) { // 大于是不可能的，正确的情况下。
                    return false;
                } else if (dayCountMap.size() == days) {
                    // 没有的情况就说名已经到了次数
                    if (!dayCountMap.containsKey(todayStr)) {
                        return false;
                    }
                }
            }
           
            if (perDayCount > 0) {
                // 线路详情特殊处理
                //  每天投放次数：针对某个用户，每天投放几次。如果前面设置了cacheTime，则这里设置的投放次数必须在cacheTime时间内达到，
                //              否则超时以后对同一用户会重新计数。例如假设设置广告每天投放次数为5，cacheTime为600（10分钟），
                //              如果用户在早高峰时10分钟内只看到了2次广告，那么晚高峰时仍然认为需要给该用户投递5次广告
                if (rule.getCacheTime() > 0) {
                    int adCount = getAdTimeCountsQueryCount(rule.getRuleId());
                    logger.debug("getAdTimeCountsQueryCount:" + adCount + ",ruleId:" + rule.getRuleId());
                    if (adCount >= perDayCount) {
                        return false;
                    }
                    return true; // 设置了cacheTime,有可能直接返回ture，而不进行接下来的总次数判断
                }

                if (dayCountMap.containsKey(todayStr)) {
                    if (dayCountMap.get(todayStr) >= perDayCount) {
                        return false;
                    }
                }
            }
            
			
			/*if (days > 0 && perDayCount > 0) { // 说明配置了每天多少次规则
				if (dayCountMap.size() > days) { // 大于是不可能的，正确的情况下。
					return false;
				} else {
					// 当总数相等的时候做处理
					if (dayCountMap.size() == days) {
						// 没有的情况就说名已经到了次数
						if (!dayCountMap.containsKey(todayStr)) {
							return false;
						}
					}

					// 线路详情特殊处理
					//  每天投放次数：针对某个用户，每天投放几次。如果前面设置了cacheTime，则这里设置的投放次数必须在cacheTime时间内达到，
//					否则超时以后对同一用户会重新计数。例如假设设置广告每天投放次数为5，cacheTime为600（10分钟），
//					如果用户在早高峰时10分钟内只看到了2次广告，那么晚高峰时仍然认为需要给该用户投递5次广告
					if (rule.getCacheTime() > 0) {
						int adCount = getAdTimeCountsQueryCount(rule.getRuleId());
						logger.debug("getAdTimeCountsQueryCount:" + adCount
								+ ",ruleId:" + rule.getRuleId());
						if (adCount >= perDayCount) {
							return false;
						}
						return true;	// 设置了cacheTime,有可能直接返回ture，而不进行接下来的总次数判断
					}

					if (dayCountMap.containsKey(todayStr)) {
						if (dayCountMap.get(todayStr) >= perDayCount) {
							return false;
						}
					}
				}
			} */
			
		}
		return true;
	}
	
	/**
	 * 设置uv属性
	 * @param advId
	 */
	public void setAdToUvMap(int advId){
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		uvMap.put(advId, todayStr);
	}
	
	/**
	 * 判断该uv广告是否已经投放过
	 * @param advId
	 * @return	true	已经投放过(指设置了自动黑名单，且以前投放过)
	 */
	public boolean isDisplayUv(int advId,int autoBlackList){
		if( uvMap == null ){
			return false;
		}
		//	不需要是黑名单的
		if( autoBlackList == 0 ){
			return false;
		}
		String time = uvMap.get(advId);
		if( time == null ){
			return false;
		}
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		logger.debug("advId=,todayStr=",advId,todayStr);
		//	今天才开始投放的,继续投放
		if( time.equals(todayStr) ){
			return false;
		}
		
		return true;
	}
	
	/**
	 * false 没有展示过广告,true 展示过广告
	 * @param advId
	 * @return
	 */
	public boolean isSendUv(int advId){
		if( uvMap == null ){
			return false;
		}
		
		String time = uvMap.get(advId);
		if( time == null ){
			return false;
		}
		return true;
	}

	public void saveUninterestedAds(int advId, String lineId, int showType, String provider_id, int apiType) {
		uninterested entity = null;
		String key = null;
		if (provider_id == null || provider_id.equals("1")
				|| provider_id.equals("0") || provider_id.equals("")) {
			// 自己的广告
			key = advId + "";
		} else {
			// 暂时只有线路详情
			key = lineId;
//			typesKey = getUninterestedApiKey(showType, provider_id, apiType);
			if (key == null) {
				return;
			}
		}

		entity = uninterestedMap.get(key);
		if (entity == null) {
			entity = new uninterested();
		}
		entity.setTime(System.currentTimeMillis());
		if (provider_id == null || provider_id.equals("1")
				|| provider_id.equals("0")) {
			uninterestedMap.put(key, entity);
			return;
		}

		// if( entity.getTypes() == null ){
		// Map<String,Long> map = New.hashMap();
		// Long time = System.currentTimeMillis();
		// map.put(typesKey, time);
		// entity.setTypes(map);
		// }else{
		// Long time = System.currentTimeMillis();
		// entity.getTypes().put(typesKey, time);
		// }

		uninterestedMap.put(key, entity);

	}

	// 今天是否投放过该广告
	public boolean hasPulished(int advId) {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		if (cacheRecordMap.containsKey(advId) && cacheRecordMap.get(advId).getDayCountMap().containsKey(todayStr)) {
			return true;
		}
		return false;
	}

	// false 不包含,true 包含
	public boolean isUninterest(int advId) {
		uninterested entity = null;
		entity = uninterestedMap.get(advId + "");
		if (entity == null) {
			return false;
		} else if(advId == 11919) {	// 天津全运会，点击不感兴趣不生效。延迟一天生效
//			if(entity.getTime() + Constants.ONE_DAY_NEW_USER_PERIOD <= System.currentTimeMillis())
			return false;
		}
		return true;
	}

	public void removeUninterestAds(int advId) {
		uninterestedMap.remove(advId + "");
	}

	public Long getCloseAdsTime(int advId) {
		uninterested entity = null;
		entity = uninterestedMap.get(advId + "");
		if (entity == null) {
			return -1L;
		}
		return entity.getTime();
	}


	public boolean isUninterestApi(String lineId, int showType,
			String provider_id, int apiType) {
		uninterested entity = null;
		if (lineId == null) {
			return false;
		}
		entity = uninterestedMap.get(lineId);

		if (entity == null) {
			return false;
		}
		if (lineId != null && !lineId.equals("")) {
			// if( entity.getTypes().get(getUninterestedApiKey(showType,
			// provider_id, apiType)) != null ){
			return true;
			// }else{
			// return false;
			// }
		}

		return true;
	}


	/**
	 * 第三方不感兴趣的列表
	 * 
	 * @return
	 */
	public List<String> getUniterestApiList(String lineId) {

		if (uninterestedMap == null || uninterestedMap.size() == 0) {
			return null;
		}
		// 不包含
		if (!uninterestedMap.containsKey(lineId)) {
			return null;
		}

		List<String> list = New.arrayList();

		uninterested entity = uninterestedMap.get(lineId);

		list.add("1," + entity.getTime());

		// Map<String,Long> map = entity.getTypes();
		//
		// Iterator<Map.Entry<String, Long>> it = map.entrySet().iterator();
		//
		// while (it.hasNext()) {
		// Map.Entry<String, Long> entry = it.next();
		// list.add( entry.getKey()+"," +entry.getValue() );
		// }

		return list;
	}
	
	/*
	 * 该缓存只保留1个小时
	 */
	public ApiRecord getapiRecord(String showType) {
		if (apiRecordMap == null) {
			return null;
		}
		ApiRecord ar = apiRecordMap.get(showType);
		if (ar == null) {
			return null;
		}
		long time = System.currentTimeMillis() - ar.getTime();
		// 已经失效
		if (time >= innmobiSaveTime * 60 * 1000) {
			apiRecordMap.remove(showType);
			return null;
		}
		return ar;
	}

	public int getApiRecordPos(ApiRecord ar) {
		Iterator<Map.Entry<Integer, Integer>> it = ar.getMap().entrySet()
				.iterator();
		int num = Integer.MAX_VALUE;
		int pos = 0;
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> entry = it.next();
			if (entry.getValue() == 0) {
				return entry.getKey();
			}
			if (entry.getValue() < num) {
				num = entry.getValue();
				pos = entry.getKey();
			}
		}
		return pos;
	}
	/**
	 * 第一次保存map
	 * @param response
	 * @param showType
	 */
	public void setApiRecord(NativeResponse response, String showType) {
		apiRecordMap = New.hashMap();
		ApiRecord ar = new ApiRecord();
		Map<Integer,Integer> map = New.hashMap();
		for( int i = 0;i < response.getAds().size();i++ ){
			if( i == 0 ){
				map.put(0, 1);
			}else{
				map.put(i, 0);
			}
			
		}
		
		ar.setMap(map);
		ar.setTime(System.currentTimeMillis());
		ar.setResponse(response);
		apiRecordMap.put(showType, ar);
	}

	/**
	 * apiRecordMap不为null
	 * 
	 * @param showType
	 * @param pos
	 */
	public void setApiRecord(String showType, int pos) {
		ApiRecord ar = apiRecordMap.get(showType);
		int value = ar.getMap().get(pos);
		value++;
		ar.getMap().put(pos, value);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (cacheRecordMap != null) {
			sb.append("cacheRecordMap={");
			Set<Integer> keySet = cacheRecordMap.keySet();
			boolean isFirst = true;
			for (Integer key : keySet) {
				CacheRecord record = cacheRecordMap.get(key);
				if (isFirst)
					isFirst = false;
				else
					sb.append(", ");

				sb.append(key).append(":").append(record);
			}
			sb.append("}");
		}
		return sb.toString();
	}


	// public void setCacheTime(Long cacheTime){
	// this.cacheTime = cacheTime;
	// }
	// // 一个时间内总点击次数
	// private Map<String,Long> adTimeCountsMap = new ConcurrentHashMap<>();

	public Map<String, Map<AdCategory, Integer>> getTodayHistoryMap() {
		return todayHistoryMap;
	}

	public Map<String, Map<AdCategory, Integer>> getTodayOpenHistoryMap() {
		return todayOpenHistoryMap;
	}

	// public void setTodayHistoryMap(
	// Map<String, Map<AdCategory, Integer>> todayHistoryMap) {
	// this.todayHistoryMap = todayHistoryMap;
	// }

	public Map<String, uninterested> getUninterestedMap() {
		return uninterestedMap;
	}

	public void setUninterestedMap(Map<String, uninterested> uninterestedMap) {
		this.uninterestedMap = uninterestedMap;
	}

	// public Map<String, List<AdCategory>> getAdHistoryMap() {
	// return adHistoryMap;
	// }
	//
	// public void setAdHistoryMap(Map<String, List<AdCategory>> adHistoryMap) {
	// this.adHistoryMap = adHistoryMap;
	// }

	public Map<String, TimeAndCount> getFirstClickMap() {
		return firstClickMap;
	}

	public void setFirstClickMap(Map<String, TimeAndCount> firstClickMap) {
		this.firstClickMap = firstClickMap;
	}

	public Long getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(Long cacheTime) {
		this.cacheTime = cacheTime;
	}
	
	public Map<Integer,String> getUvMap() {
		return uvMap;
	}
	
	public void setUvMap(Map<Integer,String> uvMap) {
		this.uvMap = uvMap;
	}

	public Map<String, Map<String, Map<Integer, Integer>>> getTodayNoAdHistoryMap() {
		return todayNoAdHistoryMap;
	}
	
	public Map<String, ApiRecord> getApiRecordMap() {
		return apiRecordMap;
	}

	public void setApiRecordMap(Map<String, ApiRecord> apiRecordMap) {
		this.apiRecordMap = apiRecordMap;
	}

	public invalidAdv getInvalidInfo() {
		return invalidInfo;
	}
	
	
	/**
	 * 是否显示广告
	 * @return	true 显示
	 */
	public boolean isDisplayAdv(){
//		if( invalidInfo == null ){
//			return true;
//		}
//		Date nowDate = new Date();
//		if( 0 >= nowDate.compareTo(invalidInfo.getEndDate()) ){
//			return false;
//		}else{
//			invalidInfo = null;
//		}
		return true;
	}

	public void setInvalidInfo(invalidAdv invalidInfo) {
		this.invalidInfo = invalidInfo;
	}
	
	
	public static void main(String[] args) {
		Map<String, Integer> msp = new ConcurrentHashMap<>();
		for (int i = 0; i < 10; i++) {
			msp.put("" + i, i);
		}

		for (String key : msp.keySet()) {
			System.out.println(key + " : " + msp.get(key));
		}

		System.out.println("+++++++++++++++++++++++++++++++++++");

		for (String key : msp.keySet()) {
			if (key.equalsIgnoreCase("9")) {
				msp.remove(key);
			}
		}

		for (String key : msp.keySet()) {
			System.out.println(key + " : " + msp.get(key));
		}
		
		
		String s = "{\"cacheRecordMap\":{-1:{\"clickCount\":0,\"dayCountMap\":{\"2017-12-20\":8,\"2017-12-11\":19,\"2017-12-21\":8,\"2017-12-13\":100,\"2017-12-12\":39,\"2017-12-23\":20,\"2017-12-15\":37,\"2017-12-26\":15,\"2017-12-14\":1,\"2017-12-06\":6,\"2017-12-28\":3,\"2017-12-27\":30,\"2017-12-08\":72,\"2017-12-19\":95,\"2017-12-07\":41,\"2017-12-18\":44}}},\"cacheTime\":0,\"displayAdv\":true,\"firstClickMap\":{},\"todayHistoryMap\":{\"2017-12-28\":{{\"adId\":-1,\"adType\":-1,\"apiType\":-1}:3}},\"todayOpenHistoryMap\":{},\"111todayOpenHistoryMap\":{},\"uninterestedMap\":{\"-1\":{\"time\":1514428999980}},\"todayOpenAdPubTime\":{},\"uvMap\":{}}";
//		String s = "{}";
//		s = "{\"cacheRecordMap\":{12192:{\"clickCount\":0,\"dayCountMap\":{\"2017-12-28\":1}}},\"cacheTime\":0,\"displayAdv\":true,\"firstClickMap\":{},\"openAdPubTime\":{\"111\":{333:222}},\"todayHistoryMap\":{},\"todayNoFeedAdHistoryMap\":{},\"todayOpenHistoryMap\":{},\"uninterestedMap\":{},\"uvMap\":{}}ue,\"firstClickMap\":{},\"todayHistoryMap\":{\"2017-12-28\":{{\"adId\":-1,\"adType\":-1,\"apiType\":-1}:3}},\"todayNoFeedAdHistoryMap\":{},\"todayOpenHistoryMap\":{},\"uninterestedMap\":{\"-1\":{\"time\":1514428999980}},\"uvMap\":{}}";
		AdPubCacheRecord ad1 = fromJson(s);
		System.out.println("1---->" + JSONObject.toJSONString(ad1));
		
		String s1 = "{\"cacheRecordMap\":{12201:{\"clickCount\":0,\"dayCountMap\":{\"2017-12-28\":1}}},\"cacheTime\":0,\"displayAdv\":true,\"firstClickMap\":{},\"todayOpenAdPubTime\":{},\"todayHistoryMap\":{},\"todayNoFeedAdHistoryMap\":{\"2017-12-28\":{12201:0}},\"todayOpenHistoryMap\":{},\"uninterestedMap\":{},\"uvMap\":{}}";
		AdPubCacheRecord ad2 = fromJson(s1);
		System.out.println("2---->" + JSONObject.toJSONString(ad2));
	}

	public Map<String, Map<Integer, Long>> getTodayOpenAdPubTime() {
		return todayOpenAdPubTime;
	}

    public Map<String, ClickCacheRecord> getAidTitlesMap() {
        return aidTitlesMap;
    }

    public void setAidTitlesMap(Map<String, ClickCacheRecord> aidTitlesMap) {
        this.aidTitlesMap = aidTitlesMap;
    }
}

class TimeAndCount {
	private int count;
	private Long time;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

}

class uninterested {

	private Long time;

	// // key 类型组合的key,value 不感兴趣时间
	// private Map<String,Long> types;
	//
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
}

class invalidAdv{
	private Date endDate;
	private Date startDate;
	private String accountId;
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
}
