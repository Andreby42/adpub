package com.bus.chelaile.model.rule;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.TimeLong;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.UserType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.Station;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.model.rule.version.VersionEntity;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.service.UserHelper;
import com.bus.chelaile.thread.model.QueueObject;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.DateUtil;
import com.bus.chelaile.util.LocationKDTree;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.bus.chelaile.thread.Queue;

public class Rule {
	private Date startDate;
	private String ruleId;
	private Date endDate;
	// private List<String> cities;
	private Map<String, String> cities;
	private List<String> stations;
	private List<Position> gpsList;
	private List<String> channels;
	private List<String> platforms;
	private VersionEntity noLessThanVersionsAndroid;
	private VersionEntity noLessThanVersionsIos;
	private Map<VersionEntity, String> versions;
	private List<String> lines; // 线路详情的投放规则
	private LocationKDTree kdTree;

	private Map<String, Set<String>> lineStationsMap;

	// 如何根据站点进行投放？

	private int userType; // 用户类型
	private List<String> userIds; // userId是旧的遗留，不再使用userId。然而实际上还是用的这个属性，只是存储的是用户的udid罢了
	private int sendType; // 1按照accoutId投放
	private int clickCount; // 每人点击次数
	private int pclickCount; // 每人每天点击次数
	private int totalClickPV; // 每天点击次数上限
	private int days; // 投放天数
	private int perDayCount; // 每天投放次数
	private int totalCount; // 投放总次数
	private List<String> netStatus; // 网络状态
	private String rightPushNum; // 右下角显示次数
	private String blackList; // 黑名单
	private String udidPattern; // udid模式，用模糊匹配
	public static final Rule EMPTY_RULE;
	private int uvLimit; // 每日UV上限
	private int autoBlackList; // 0不需要,1需要
	private int chatOrRide; // 0 显示乘车页 1 显示聊天室 2 均显示 3 均不显示
	@JsonIgnore
	private ConcurrentHashMap<String, Boolean> cityHash;

	private List<AdTimeCounts> adTimeCounts; // 线路详情按照pv投放规则

	private Long cacheTime; // 一个用户线路详情放到缓存中有效时间
	
	private int isClickEndPush; // 0点击后无影响，1点击后不投放
	
	private long minIntervalTime; // 开屏广告最小投放时间间隔， 单位：min转成的毫秒
	private int minIntervalPages;	// feed流广告最小投放间隔，单位：次。
	
	private int screenHeight; // 屏幕高度
	
	private int canPubMIUI; // 开屏是否给MIUI投放， 0 不投MIUI， 1 只投MIUI， 2 没有限制
	private int startMode; // 冷热启动模式控制. 1 只投冷启动， 2 只投热启动。  0 没有限制
	private int projectClick; // 项目点击次数
	private int projectTotalClick;
	private int projectDayClick;
	private int projectTotalSend;
	private int projectDaySend;

	protected static final Logger logger = LoggerFactory.getLogger(Rule.class);

	static {
		EMPTY_RULE = new Rule("EMPTY_RULE");
	}

	public Rule() {
	}

	public Rule(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleId() {
		return ruleId;
	}

	/**
	 * 在发送时间之内并且次数是小于要求的数量,return true
	 * 
	 */
	public boolean adTimeCounts(int advId, String ruleId, AdPubCacheRecord cacheRecord, String udid, boolean isRecord) {

		String minuteStr = DateUtil.getMinuteStr();
		if (!isRecord) {
			// 该时间段的次数
			Long count = getAdTimeCount(ruleId, minuteStr, udid);

			// 该分钟发送次数已经到达上限
			int countMinute = StaticAds.getMinuteNumbers(advId + "#" + ruleId, minuteStr, udid);
			logger.info("adv send times per minute info : advId={}, udid={}, ruleId:{}, countMinute:{}, countReal:{}", advId, udid, ruleId, countMinute, count);
//			double pvRate = CalculatePerMinCount.getPVRate(advId + "#" + ruleId);
//			logger.info("advId={}, totalPVRate={}, udid={}, countMinute after rate ={}", advId, pvRate, udid, countMinute * pvRate);
			
//			if (countMinute == 0 || countMinute * pvRate <= count) {
//				// 新增规则，如果该用户之前投过了，那么不受分钟投放限制，接着投放
//				if(cacheRecord.hasPulished(advId)) {
//					logger.info("have send adv today, udid={}, advId={},ruleId={}", udid, advId, ruleId);
//					return true;
//				}
//				return false;
//			} 
			// 2018-06-01， 严格按照每分钟投放，不再考虑‘连续投放’和‘比例因子’的东西
            if (countMinute == 0 || countMinute <= count) {
                return false;
            } else {
                return true;
            }
		} else if (isRecord) { // true 的时候才需要记录
			// 广告每分钟发送总次数
			setAdTimeCount(ruleId, minuteStr);

			if (cacheRecord == null) {
				cacheRecord = new AdPubCacheRecord();
			}
			cacheRecord.setCacheTime(cacheTime);
			// 把第一次访问时间放回到缓存中
//			cacheRecord.setQueryTimeAndCount(ruleId, minuteStr);
//			if (cacheTime > 0) {
//				cacheRecord.setQueryTimeAndCount(ruleId);
//			}

			return true;
		}
		return false;
	}

	/**
	 * 以前的adTimeCounts
	 * 
	 * @param ruleId
	 * @param key
	 * @param udid
	 * @return
	 */
	/*
	 * public boolean adTimeCounts(String ruleId, AdPubCacheRecord cacheRecord,
	 * String udid, boolean isRecord) { for (AdTimeCounts adu : adTimeCounts) {
	 * if (adu.getTime() != null) { String[] args = adu.getTime().split("-");
	 * try { int start = DateUtil.compareNowDate(args[0]); int end =
	 * DateUtil.compareNowDate(args[1]); // 当前日期要大于等于开始时间,小于等于结束时间 if (start >=
	 * 0 && 0 >= end) { if (!isRecord) {
	 * 
	 * // 该时间段的次数 Long count = getAdTimeCount(ruleId, adu.getTime(), udid);
	 * 
	 * logger.debug("ruleId:" + ruleId + ",count:" + count); // 发送次数已经到达上限 if
	 * (adu.getCount() > 0 && adu.getCount() < count) { continue; } else if
	 * (adu.getCount() > 0 && adu.getCount() > count) { return true; }
	 * 
	 * } else if (isRecord) { // true 的时候才需要记录 // 总次数放回到缓存中
	 * setAdTimeCount(ruleId, adu.getTime());
	 * 
	 * if (cacheRecord == null) { cacheRecord = new AdPubCacheRecord(); }
	 * cacheRecord.setCacheTime(cacheTime); // 把第一次访问时间放回到缓存中
	 * cacheRecord.setQueryTimeAndCount(ruleId, adu.getTime());
	 * 
	 * return true; }
	 * 
	 * } } catch (ParseException e) { throw new
	 * IllegalArgumentException("时间比较失败:" + adu.getTime()); } } } return false;
	 * }
	 */

	/**
	 * 点击总次数获取
	 * 
	 * @return
	 */
	public int currentTotalClickPV(AdContent ad) {
		Object value = CacheUtil.getFromRedis(AdvCache.getTotalClickPV(String.valueOf(ad.getId())));
//		logger.info("get click numbers : advId={}, number={}", ad.getId(), value);

		if (value instanceof String) {
			int number = 0;
			try {
				number = Integer.parseInt((String) value);
			} catch (Exception e) {
				return number;
			}
			if (!StaticAds.hasSendEmailhalf && number == totalClickPV / 2) {
				TimeLong.info("【广告点击预警】 ID:{}_标题:{}_已完成50%##  ID:{}_标题:{}_已完成50%##  PV:{}", ad.getId(), ad.getTitle(),
						ad.getId(), ad.getTitle(), number);
				StaticAds.hasSendEmailhalf = true;
				// TimeLong.info("当前曝光量： PV: {}", );
			} else if (!StaticAds.hasSendEmail && number == totalClickPV - 1) { // -1 是避免重复邮件
				TimeLong.info("【广告点击预警】 ID:{}_标题{}_已完成50%##  ID:{}_标题:{}_已完成50%##  PV:{}", ad.getId(), ad.getTitle(),
						ad.getId(), ad.getTitle(), number);
				StaticAds.hasSendEmail = true;
			}
			return number;
		}
		return 0;
	}

	/**
	 * 放回到缓存中,保存一天
	 * 
	 * @param ruleId
	 * @param key
	 */
	private void setAdTimeCount(String ruleId, String minuteStr) {
		// long time =System.currentTimeMillis();
		QueueObject queueobj = new QueueObject();
		// 用线程去处理了
		queueobj.setRedisIncrKey(AdvCache.getMinuteTimesKey(ruleId, minuteStr));
		Queue.set(queueobj);
	}

	/*
	 * 见上一个方法，这个方法是取上面存的数
	 */
	private Long getAdTimeCount(String ruleId, String minuteStr, String udid) {
		long time = System.currentTimeMillis();
		Object value = CacheUtil.getFromRedis(AdvCache.getMinuteTimesKey(ruleId, minuteStr));
		time = System.currentTimeMillis() - time;
		if (time > 40) {
			TimeLong.info("udid={},getAdTimeCount={}", udid, time);
		}

		if (value instanceof String) {
			return Long.parseLong((String) value);
		}
		return 0L;
	}

	/**
	 * 检查是否在投放时间内
	 *
	 * @return
	 */
	public boolean isValidTime() {
		return true;
	}

	public boolean isCityMatch(String cityId) {
		return isStrMatch(cities, cityId);
	}

	public boolean isStationMatch(String station) {
		return isStrMatch(stations, station);
	}

	public boolean isStationMatch(List<Station> stList) {
		if (stations == null || stations.isEmpty()) {
			return true;
		}
		if (stList == null || stList.isEmpty()) {
			return false;
		}

		for (Station st : stList) {
			if (stations.contains(st.getStnName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isLineMatch(String lineId) {
		if (!hasLines()) {
			return true;
		}
		if (lineId == null) {
			return false;
		}

		return lines.contains(lineId);
	}

	/**
	 * 如何输入规则？ 文件？ lineId=lineId,station= lineId
	 * <p>
	 * 由于Map之中的值无法是null， 因此当Set<String>是空的时候，表示匹配线上的所有站点。
	 *
	 * @param lineId
	 * @param stnName
	 * @param stnOrder
	 * @return
	 */
	public boolean isLineStationMap(String lineId, String stnName, int stnOrder, String udid) {
		if (lineStationsMap == null || lineStationsMap.size() == 0) {
			return true;
		}
		if (lineId == null) {
			return false;
		}

		Set<String> stationsSet = lineStationsMap.get(lineId);

		if (stationsSet == null) {
			return false;
		}
		if (stationsSet.size() == 0) {
			// stationsSet为空的时候，表示匹配所有的站点。
			return true;
		}

		// 站点名为空
		if (stnName == null) {
			return false;
		}
		// 允许输入order或者不输入？
		return stationsSet.contains(stnName) || stationsSet.contains(stnName + "," + stnOrder);
	}

	public boolean isChannelMatch(RuleParam param) {
		return isStrMatch(channels, param.getLastSrc());
	}

	public boolean isPostionMatch(double lat, double lng) {
		if (kdTree == null) {
			return true;
		}
		long time = System.currentTimeMillis();
		Position ps = kdTree.findNearest(lat, lng);
		time = System.currentTimeMillis() - time;
		if (time > 11) {
			TimeLong.info("lng={},lat={},time={}", lat, lng, time);
		}
		if (ps == null) {
			// logger.debug("lng={},lat={},return false",param.getLng(),param.getLat());
			return false;
		}
		// for (Position pos : gpsList) {
		// if (pos.isMatch(param.getLng(), param.getLat())) {
		// return true;
		// }
		// }

		return true;
	}

	public boolean isUserTypeMatch(int tgtType) {

		if (userType == 0) {
			return true;
		} else if (userType == 1) {
			return UserType.isNew(tgtType);
		} else if (userType == 2) {
			return UserType.isAnonymous(tgtType);
		}

		return false;
	}

	public boolean isUserTypeMatch(AdvParam param) {
		if (userType == 0) {
			return true;
		} else if (userType == UserType.NOT_NEW.getType()) {	//默认的情况，不给新用户投放。除非是非当天新增且访问过详情页
			return isReturnAds(param);
		} else if (userType == UserType.NEW.getType()) { // 新用户
			return isNewUser(param);
		} else if (userType == UserType.ANONYMOUS.getType()) { // 匿名用户
			return isAnonymousUser(param);
		} else if (userType == UserType.TODAY_NEW.getType()) { // 今日新增
			return isTodayNewUser(param);
		}

		return false;
	}

	public boolean isNetStatusMatch(String nw) {

		// String nw = ruleParam.getNw();
		if (StringUtils.isEmpty(nw)) { // 没有状态则不匹配
			return false;
		}

		boolean isMatch = false;
		for (String netState : netStatus) {
			if (netState.equalsIgnoreCase(nw)) {
				isMatch = true;
				break;
			}
		}
		return isMatch;
	}

	private boolean isTodayNewUser(AdvParam param) {
		return UserHelper.isTodayNewUser(param.getUdid(), param.getAccountId());
	}

	/**
	 * 平台是否符合
	 * 来自客户端的请求，s分为三种：android|ios|h5
	 * 对于服务器的判断，s为h5的时候，再分为h5和wechatApp，wechatApp来自参数‘src’
	 *                  在数据库中，针对小程序，platform记录为 ‘wechatApp’
	 * @param s
	 * @param h5Src
	 * @return
	 */
	public boolean isPlatformMatch(String s, String h5Src) {
		if (s == null) {
			return false;
		}

		// h5 用户必须是从微信来的才行
        Platform platform = Platform.from(s);
        if (platform.isH5(s)) {
            if (h5Src == null || (!h5Src.equalsIgnoreCase("webapp_weixin_mp") && !h5Src.equals("weixinapp_cx"))) {
                return false;
            }
            if (h5Src.equals("weixinapp_cx")) {//小程序
                return isStrMatch(platforms, "wechatApp");
            }
        }

		return isStrMatch(platforms, s);
	}

	public boolean isVersionMatch(String tgtVersion) {
		if (versions == null || versions.isEmpty()) {
			return true;
		}

		if (tgtVersion == null) {
			return false;
		}

		VersionEntity queryVersion = VersionEntity.parseVersionStr(tgtVersion);

		if (versions.containsKey(queryVersion))
			return true;

		return false;
	}

    public boolean isVersionNoLessThan(String tgtVersion, String s) {
        if(noLessThanVersionsAndroid == null && noLessThanVersionsIos == null) {
            return true;
        }
        if(StringUtils.isBlank(s)) {
            return false;
        }
        
        if(noLessThanVersionsAndroid != null && s.equalsIgnoreCase("android")) {
            VersionEntity queryVersion = VersionEntity.parseVersionStr(tgtVersion);
            if(queryVersion.compareTo(noLessThanVersionsAndroid) < 0) {
                return false;
            }
        }
        
        if(noLessThanVersionsIos != null && s.equalsIgnoreCase("ios")) {
            VersionEntity queryVersion = VersionEntity.parseVersionStr(tgtVersion);
            if(queryVersion.compareTo(noLessThanVersionsIos) < 0) {
                return false;
            }
        }
        
        return true;
    }

	// public boolean isVersionMatch(RuleParam param) {
	// if (versions == null || versions.isEmpty()) {
	// return true;
	// }
	//
	// String verStr = param.getV();
	// VersionEntity version = VersionEntity.parseVersionStr(verStr);
	//
	// if (version == null) {
	// logger.error("Parsed VersionEntity is null: " + verStr);
	// return false;
	// }
	//
	// for (IVersionCmp v : versions) {
	// if (v.isMatch(version)) {
	// return true;
	// }
	// }
	//
	// return false;
	// }

	private boolean isStrMatch(List<String> list, String value) {
		if (list == null || list.isEmpty()) {
			return true;
		}

		if (value == null) {
			return false;
		}
		for (String s : list) {
			if (value.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	private boolean isStrMatch(Map<String, String> map, String value) {
		if (map == null || map.isEmpty()) {
			return true;
		}

		if (value == null) {
			return false;
		}
		if (map.containsKey(value))
			return true;
		return false;
	}

	private boolean isNewUser(AdvParam param) {
		return UserHelper.isNewUser(param.getUdid(), param.getUserId(), param.getAccountId());
	}

	private boolean isAnonymousUser(AdvParam param) {
		return UserHelper.isAnonymousUser(param.getUserId(), param.getAccountId());
	}

//	private boolean isOldNewUser(AdvParam param) {
//		return !UserHelper.isNewUser(param.getUdid(), param.getUserId(), param.getAccountId());
//	}
	
	private boolean isReturnAds(AdvParam param) {
		return UserHelper.isReturnAds(param.getUdid());
	}

	public boolean hasCities() {
		return isNotEmpty(cities);
	}

	public boolean hasChannels() {
		return isNotEmpty(channels);
	}

	public boolean hasStations() {
		return isNotEmpty(stations);
	}

	public boolean hasLines() {
		return isNotEmpty(lines);
	}

	public boolean hasVersions() {
		return isNotEmpty(versions);
	}

	public boolean hasPlatforms() {
		return isNotEmpty(platforms) && !platforms.contains("all");
	}

	public boolean hasGpsList() {
		return isNotEmpty(gpsList);
	}

	public boolean hasUserIds() {
		return isNotEmpty(userIds);
	}

	public boolean hasNetStatus() {
		return isNotEmpty(netStatus);
	}

	public boolean hasAdTimeCounts() {
		if (adTimeCounts == null || adTimeCounts.size() == 0) {
			cacheTime = 0L;
		}
		return isNotEmpty(adTimeCounts);
	}

	public static boolean isNotEmpty(List<?> list) {
		return list != null && !list.isEmpty();
	}

	public static boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

	public static boolean isNotEmpty(Map<?, ?> list) {
		return list != null && !list.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> list) {
		return list == null || list.isEmpty();
	}

	public void addCity(String city) {
		if (cities == null) {
			cities = new HashMap<String, String>();
		}
		cities.put(city, null);
	}

	public void addStations(String... stArray) {
		if (stations == null) {
			stations = new ArrayList<String>();
		}
		addToList(stations, stArray);
	}

	public void addLines(String... stArray) {
		if (lines == null) {
			lines = new ArrayList<String>();
		}
		addToList(stations, stArray);
	}

	public void addChannels(String... stArray) {
		if (channels == null) {
			channels = new ArrayList<String>();
		}
		addToList(channels, stArray);
	}

	public void addPlatforms(String... stArray) {
		if (platforms == null) {
			platforms = new ArrayList<String>();
		}
		addToList(platforms, stArray);
	}

	public void addUdids(List<String> udidList) {
		if (userIds == null) {
			userIds = new ArrayList<String>();
		}

		if (udidList != null) {
			this.userIds.addAll(udidList);
		}
	}

	private void addToList(final List<String> list, String... params) {
		for (String value : params) {
			list.add(value);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Rule(");
		try {
			sb.append("ruleId=").append(ruleId).append(", startDate=")
					.append(startDate == null ? null : AdvUtil.DATE_FORMAT.format(startDate)).append(", endDate=")
					.append(endDate == null ? null : AdvUtil.DATE_FORMAT.format(endDate)).append(", cities=")
					.append(cities).append(", stations=").append(stations).append(", gpsList=").append(gpsList)
					.append(", channels=").append(channels).append(", platforms=").append(platforms)
					.append(", versions=").append(versions).append(", userType=").append(userType).append(", userIds=")
					.append(AdvUtil.listToStr(userIds, 500))

					.append(", lines=").append(lines).append(", lineStnMap=").append(lineStationsMap)
					.append(", clickCount=").append(clickCount).append(", days=").append(days).append(", perDayCount=")
					.append(perDayCount).append(", totalCount=").append(totalCount).append(", cityHash=")
					.append(cityHash);
		} catch (Exception ex) {
			logger.error("Rule toString exception: " + ex.getMessage());
		}

		sb.append(")");

		return sb.toString();
	}

	/**
	 * 判断最后日期是否小于当前日期
	 * 
	 * @return true 已经过期
	 */
	public boolean isOverdue() {
		Date nowDate = new Date();
		if (nowDate.compareTo(endDate) > 0 || nowDate.compareTo(startDate) < 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断结束日期是否过期
	 * 
	 * @return
	 */
	public boolean isEndDateOverdue() {
		Date nowDate = new Date();
		if (nowDate.compareTo(endDate) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断uv投放次数是否超过要求的投放次数
	 * 
	 * @param key
	 * @param udid
	 * @return true 超过投放次数
	 */
	public boolean isOverUvCount(String udid) {
		if (getUvLimit() == 0) {
			return false;
		}
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		long time = System.currentTimeMillis();
		Object value = CacheUtil.getFromRedis(todayStr + "_uvRuleId_" + ruleId);
		time = System.currentTimeMillis() - time;
		if (time > 40) {
			TimeLong.info("udid={},isOverUv={}", udid, time);
		}
		if (udid.equals("12e20279-d650-47c1-8ace-d8a8f4672deb")) {
			logger.info(todayStr + "_" + ruleId + ",value:" + value + ",udid:" + udid);
		}

		if (value instanceof String) {
			long count = Long.parseLong((String) value);
			if (count >= getUvLimit()) {
				return true;
			}
		}
		return false;
	}

	public void setUvCount() {
		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
		QueueObject queueobj = new QueueObject();
		// 用线程去处理了
		queueobj.setRedisIncrKey(todayStr + "_uvRuleId_" + ruleId);
		Queue.set(queueobj);
	}
	
	/*
	 * 关于设备号的投放控制
	 * startMode==0表示冷启动
	 * isMIUIcold表示米UI的冷启动
	 */
    public boolean devicePub(String udid, String deviceType, int startMode) {
        boolean isMIUIcold = false;
        if (StringUtils.isNoneBlank(udid) && udid.contains("a143270d-0453-4e34-9e34-bbf5c8eb38c2")) {
            return true;
        }
        if (StringUtils.isNoneBlank(deviceType) && (deviceType.toLowerCase().contains("mi") || deviceType.contains("HM "))
                && startMode == 0) {
            isMIUIcold = true;
        }
        if (this.getCanPubMIUI() == 0 && isMIUIcold) { // 不投miUI冷启动
            return false;
        } else if (this.getCanPubMIUI() == 1 && !isMIUIcold) { // 只投MIUI冷启动
            return false;
        }
        return true;
    }

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Map<String, String> getCities() {
		return cities;
	}

	public void setCities(Map<String, String> cities) {
		this.cities = cities;
	}

	public List<String> getStations() {
		return stations;
	}

	public void setStations(List<String> stations) {
		this.stations = stations;
	}

	public List<Position> getGpsList() {
		return gpsList;
	}

	public void setGpsList(List<Position> gpsList) {
		this.gpsList = gpsList;
	}

	public List<String> getChannels() {
		return channels;
	}

	public void setChannels(List<String> channels) {
		this.channels = channels;
	}

	public List<String> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(List<String> platforms) {
		this.platforms = platforms;
	}

	public Map<VersionEntity, String> getVersions() {
		return versions;
	}

	public void setVersions(Map<VersionEntity, String> versions) {
		this.versions = versions;
	}

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	public LocationKDTree getKdTree() {
		return kdTree;
	}

	public void setKdTree(LocationKDTree kdTree) {
		this.kdTree = kdTree;
	}

	public Map<String, Set<String>> getLineStationsMap() {
		return lineStationsMap;
	}

	public void setLineStationsMap(Map<String, Set<String>> lineStationsMap) {
		this.lineStationsMap = lineStationsMap;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public int getSendType() {
		return sendType;
	}

	public void setSendType(int sendType) {
		this.sendType = sendType;
	}

	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getPerDayCount() {
		return perDayCount;
	}

	public void setPerDayCount(int perDayCount) {
		this.perDayCount = perDayCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<String> getNetStatus() {
		return netStatus;
	}

	public void setNetStatus(List<String> netStatus) {
		this.netStatus = netStatus;
	}

	public String getRightPushNum() {
		return rightPushNum;
	}

	public void setRightPushNum(String rightPushNum) {
		this.rightPushNum = rightPushNum;
	}

	public String getBlackList() {
		return blackList;
	}

	public void setBlackList(String blackList) {
		this.blackList = blackList;
	}

	public ConcurrentHashMap<String, Boolean> getCityHash() {
		return cityHash;
	}

	public void setCityHash(ConcurrentHashMap<String, Boolean> cityHash) {
		this.cityHash = cityHash;
	}

	public List<AdTimeCounts> getAdTimeCounts() {
		return adTimeCounts;
	}

	public void setAdTimeCounts(List<AdTimeCounts> adTimeCounts) {
		this.adTimeCounts = adTimeCounts;
	}

	public Long getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(Long cacheTime) {
		this.cacheTime = cacheTime;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public int getAutoBlackList() {
		return autoBlackList;
	}

	public void setAutoBlackList(int autoBlackList) {
		this.autoBlackList = autoBlackList;
	}

	public int getUvLimit() {
		return uvLimit;
	}

	public void setUvLimit(int uvLimit) {
		this.uvLimit = uvLimit;
	}

	public int getChatOrRide() {
		return chatOrRide;
	}

	public void setChatOrRide(int chatOrRide) {
		this.chatOrRide = chatOrRide;
	}

	public String getUdidPattern() {
		return udidPattern;
	}

	public void setUdidPattern(String udidPattern) {
		this.udidPattern = udidPattern;
	}

	public static void main(String[] args) {
		String pattern = "4.*||5.*";
		String udid = "1234123";
		String udid1 = "4lsadfadf";
		String udid2 = "52444dfa";
		System.out.println(udid.matches(pattern));
		System.out.println(udid1.matches(pattern));
		System.out.println(udid2.matches(pattern));

//		System.out.println(DateUtil.getMinuteStr());
//		
//		Map<Integer, String> test = New.hashMap();
//		test.put(new Integer(1), "aaa");
//		
//		int a = 1;
//		System.out.println(test.containsKey(a));
		
		
	}

	public int getTotalClickPV() {
		return totalClickPV;
	}

	public void setTotalClickPV(int totalClickPV) {
		this.totalClickPV = totalClickPV;
	}

	public int getIsClickEndPush() {
		return isClickEndPush;
	}

	public void setIsClickEndPush(int isClickEndPush) {
		this.isClickEndPush = isClickEndPush;
	}

	public long getMinIntervalTime() {
		return minIntervalTime;
	}

	public void setMinIntervalTime(long minIntervalTime) {
		this.minIntervalTime = minIntervalTime;
	}

	public int getMinIntervalPages() {
		return minIntervalPages;
	}

	public void setMinIntervalPages(int minIntervalPages) {
		this.minIntervalPages = minIntervalPages;
	}

    public int getPclickCount() {
        return pclickCount;
    }

    public void setPclickCount(int pclickCount) {
        this.pclickCount = pclickCount;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getCanPubMIUI() {
        return canPubMIUI;
    }

    public void setCanPubMIUI(int canPubMIUI) {
        this.canPubMIUI = canPubMIUI;
    }

    public boolean checkStartMode(int startMode) {
        if (this.startMode == 1 && startMode == 1) { // 条件是只投冷启动，参数表示是热启动，故返回false
            return false;
        } else if (this.startMode == 2 && startMode == 0) { // 条件是指投热启动，参数表示是冷启动，故返回false
            return false;
        }
        return true;
    }

    public int getStartMode() {
        return startMode;
    }

    public void setStartMode(int startMode) {
        this.startMode = startMode;
    }

    public int getProjectClick() {
        return projectClick;
    }

    public void setProjectClick(int projectClick) {
        this.projectClick = projectClick;
    }
    
    public int getProjectTotalClick() {
        return projectTotalClick;
    }

    public void setProjectTotalClick(int projectTotalClick) {
        this.projectTotalClick = projectTotalClick;
    }

    public int getProjectDayClick() {
        return projectDayClick;
    }

    public void setProjectDayClick(int projectDayClick) {
        this.projectDayClick = projectDayClick;
    }

    public int getProjectTotalSend() {
        return projectTotalSend;
    }

    public void setProjectTotalSend(int projectTotalSend) {
        this.projectTotalSend = projectTotalSend;
    }

    public int getProjectDaySend() {
        return projectDaySend;
    }

    public void setProjectDaySend(int projectDaySend) {
        this.projectDaySend = projectDaySend;
    }

    // 判断 单人的项目点击次数是否达到了
    public boolean projectClickOut(String udid, String projectId) {
        if(StringUtils.isEmpty(projectId)) {
            return false;
        }
        
        String projectClickKey = AdvCache.getProjectClickKey(udid, projectId);
        String value = (String) CacheUtil.getFromRedis(projectClickKey);
        if (value != null) {
            logger.info("projectNumControl, projectId={}, projectClick={}, realProjectClick={}", projectId, projectClick, value);
            if (Integer.parseInt(value) >= projectClick) {
                return true;
            }
        }
        return false;
    }
    
    public boolean projectTotalClickOut(String projectId) {
        int value = CacheUtil.getProjectTotalClick(projectId);
        logger.info("projectNumControl, projectId={}, projectTotalClick={}, realprojectTotalClick={}", projectId, projectTotalClick, value);
        if(value >= projectTotalClick)
            return true;
        return false;
    }
    
    public boolean projectTotalSendOut(String projectId) {
        int value = CacheUtil.getProjectTotalSend(projectId);
        logger.info("projectNumControl, projectId={}, projectTotalSend={}, realprojectTotalSend={}", projectId, projectTotalSend, value);
        if(value >= projectTotalSend)
            return true;
        return false;
    }
    
    public boolean projectDayClickOut(String projectId) {
        int value = CacheUtil.getProjectDayClick(projectId);
        logger.info("projectNumControl, projectId={}, projectDayClick={}, realprojectDayClick={}", projectId, projectDayClick, value);
        if(value >= projectDayClick)
            return true;
        return false;
    }
    
    public boolean projectDaySendOut(String projectId) {
        int value = CacheUtil.getProjectDaySend(projectId);
        logger.info("projectNumControl, projectId={}, projectDaySend={}, realprojectDaySend={}", projectId, projectDaySend, value);
        if(value >= projectDaySend)
            return true;
        return false;
    }

    public VersionEntity getNoLessThanVersionsAndroid() {
        return noLessThanVersionsAndroid;
    }

    public void setNoLessThanVersionsAndroid(VersionEntity noLessThanVersionsAndroid) {
        this.noLessThanVersionsAndroid = noLessThanVersionsAndroid;
    }

    public VersionEntity getNoLessThanVersionsIos() {
        return noLessThanVersionsIos;
    }

    public void setNoLessThanVersionsIos(VersionEntity noLessThanVersionsIos) {
        this.noLessThanVersionsIos = noLessThanVersionsIos;
    }

}
