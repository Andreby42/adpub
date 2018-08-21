package com.bus.chelaile.service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.dao.AppAdvContentMapper;
import com.bus.chelaile.dao.AppAdvRuleMapper;
import com.bus.chelaile.flow.ActivityService;
import com.bus.chelaile.kafka.newPackage.InfoStreamForAdvClick;
import com.bus.chelaile.model.AdvProject;
import com.bus.chelaile.model.PlacementInfo;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdContentCacheEle;
import com.bus.chelaile.model.rule.AdRule;
import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.model.rule.RuleEngine;
import com.bus.chelaile.model.rule.UserClickRate;
import com.bus.chelaile.thread.WriteCacheThread;
import com.bus.chelaile.util.FileUtil;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public class StartService {

	protected static final Logger logger = LoggerFactory.getLogger(StartService.class);
	@Autowired
	private AppAdvRuleMapper advRule;
	@Autowired
	private AppAdvContentMapper advContent;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private InfoStreamForAdvClick infoStreamForAdvClick;
//	@Autowired
//	private LinkActiveHelp linkActiveHelp;
	@Autowired
	private DynamicRegulation dynamicRegulation;

	private static final String minuteTimesFile = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"minuteTimesFile", "/data/advConfig/commonMinuteTimesFile.csv");

    public List<String> init() {
    	

        // 初始化缓存
        CacheUtil.initClient();
        // 启动线程
        startThread();
        StaticAds.init();
        activityService.initActivitity(); // 信息流活动初始化
        if( !Constants.ISDEV ) {
//    		linkActiveHelp.initLinkedMePics();	// linkedMe 图片信息初始化
            initMinuteTimes(StaticAds.minuteTimes);
        }
        
        // 获取当前所有的可以使用的ADS
        List<AdContent> allAds = advContent.listValidAds();
        logger.info("****AllAdsinfosize*****   {}", allAds.size());
        // 获取所有的placementId
        List<PlacementInfo> allPlacements = advContent.listAllPlacementId();
        readyPlacementCache(allPlacements);
        logger.info("****android Placements***  {}", JSONObject.toJSONString(StaticAds.androidPlacementMap));
        logger.info("****ios Placements***  {}", JSONObject.toJSONString(StaticAds.iosPlacementMap));
        
        List<AdvProject> allProjects = advContent.listAllProjects();
        
        // 保存已生效的广告id
        // 1的话就取全部的,0就只读取线路详情,2除线路详情\站点广告外的其它内容
        // 2018-07不再使用了
//        String isLineDetails = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "isLineDetails", "1");
        List<String> advIds = New.arrayList();

        for (AdContent ad : allAds) {
            // 详情页浮层
            if (ad.getShowType() != null && ad.getShowType().equals(ShowType.LINE_DETAIL_PIC.getType())) {
                continue;
            }

            AdvProject projectRelation = null;
            for(AdvProject project : allProjects) {
                if(project.getProjectId().equals(ad.getProjectId())) {
                    projectRelation = project;
                    ad.setProjectIdClickExpireTime(projectRelation.getProjectIdClickExpireTime());
                    break;
                }
            }
            // ad.completePicUrl();
            List<Rule> ruleList = new ArrayList<Rule>();
            List<AdRule> adRuleList = advRule.list4AdvIdByTime(ad.getId(), new Date()); // 包含 endDate >= #{today,jdbcType=TIMESTAMP}

            for (AdRule adRule : adRuleList) {
                // 开始时间在合理范围之内
                if (!dateCompare(adRule.getStartDate(), adRule.getEndDate(), ad.getShowType())) {
                    continue;
                }

                Rule rule = RuleEngine.parseRule(adRule, projectRelation);
                if (rule != null) {
                    if (rule.getUserIds() != null && rule.getUserIds().size() == 0) {
                        continue;
                    }
                    ruleList.add(rule);
                }
            }

            if (ruleList.size() == 0) {
                continue;
            }
            // 把所有当前可能投放的广告放入这个集合中
            StaticAds.addAds(ad);

            // 黑名单
            initBlackListMap(ad, ruleList);
            // 把广告分按照用户投放和不按照用户投放两种，分开初始化入map中
            prepareAdv(ad, ruleList, false);
            // initPic(ad);
            advIds.add(ad.getId() + "");
        }

        logger.info("所有按照用户投放的广告加载完毕，用户数={}", StaticAds.adsMap.size());
        logger.info("所有按照用户投放的广告数目={}", StaticAds.allAdContentCache.size());
        for (Entry<String, List<AdContentCacheEle>> entry : StaticAds.allAdsMap.entrySet()) {
            logger.info("所有不按照用户投放的广告加载完毕，广告类型={}, 广告数={}", entry.getKey(), entry.getValue().size());
        }
        logger.info("所有存放到缓存的广告数={}", StaticAds.allAds.size());
        logger.info("所有配置项******************：{}", StaticAds.SETTINGSMAP);
        logger.info("js文件*********************：{}", StaticAds.JS_FILE_STR.keySet());

        try {
        	if( !Constants.ISDEV ) {
                //	          infoStreamDispatcher.readKafka();
        	    // 2018-08-20
                infoStreamForAdvClick.readKafka(); // 广告点击日志
                //	          infoSteamForMaidianLogs.readKafka();  // 埋点日志

        	}
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("启动kafka出错！ e={}", e.getMessage());
        }

        return advIds;
    }

    // 将placementInfo放入缓存中
	private void readyPlacementCache(List<PlacementInfo> allPlacements) {
	    for(PlacementInfo p : allPlacements) {
	        p.redayPlacementCache(StaticAds.androidPlacementMap, StaticAds.iosPlacementMap);
	    }
    }

    private void startThread() {

		int userCacheThreadNum = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
				"userCacheThreadNum", "5"));

		for (int i = 0; i < userCacheThreadNum; i++) {
			WriteCacheThread wc = new WriteCacheThread();
			Thread td = new Thread(wc);
			td.start();
		}
		
		// 启动固定频率更新投放pv到redis，和启动固定频率计算控制投放的比例因子
		dynamicRegulation.threadUpdateTotalPV();
		
	}

	private void initBlackListMap(AdContent adv, List<Rule> ruleList) {
		for (Rule rule : ruleList) {
			if (rule.getBlackList() != null && !rule.getBlackList().equals("")) {
				List<String> list = FileUtil.getFileContent(rule.getBlackList());
				if (list != null && list.size() > 0) {
					for (String udid : list) {
						if (StaticAds.blackListMap.containsKey(adv.getId())) {
							StaticAds.blackListMap.get(adv.getId()).put(udid, null);
						} else {
							Map<String, String> map = New.hashMap();
							map.put(udid, null);
							StaticAds.blackListMap.put(adv.getId(), map);
						}
					}

				}

				rule.setBlackList(null);
			}
		}
		if (StaticAds.blackListMap.containsKey(adv.getId())) {
			logger.info("advId={},blackList.size={}", adv.getId(), StaticAds.blackListMap.get(adv.getId()).size());
		}

	}

	private void prepareAdv(AdContent adv, List<Rule> ruleList, boolean isUpdate) {
		for (Rule rule : ruleList) {
			logger.debug("prepareAdv ruleId: {}, userIds:{}", rule.getRuleId(), rule.getUserIds());
		}
		ShowType showType = null;
		ShowType[] types = ShowType.values();
		for (int i = 0; i < types.length; i++) {
			if (types[i].getType().equals(adv.getShowType())) {
				showType = types[i];
				break;
			}
		}
		if (showType != null) {
			prepareAdvByType(adv, ruleList, showType, isUpdate);
		}
	}

	private void prepareAdvByType(AdContent adv, List<Rule> ruleList, ShowType advType, boolean isUpdate) {
		if (adv == null || ruleList == null) {
			return;
		}

		Map<String, Rule> tempMap = New.hashMap();

		for (Rule rule : ruleList) {
			tempMap.put(rule.getRuleId(), rule);
		}

		List<Rule> tempList = New.arrayList();
		for (Map.Entry<String, Rule> entry : tempMap.entrySet()) {
			tempList.add(entry.getValue());
		}
		// key udid or accountId
		Map<String, List<Rule>> ruleMap = New.hashMap();
		// 没有标识的广告
		List<Rule> noIdentificationList = New.arrayList();
		// AdContentCacheEle ac = new AdContentCacheEle();
		// ac.setAds(adv);

		for (Rule rule : tempList) {
			logger.info("prepareAdvByType adId: {}, ruleId: {}", adv.getId(), rule.getRuleId());
			try {
				if (rule.hasUserIds()) {
					for (String userId : rule.getUserIds()) {
						if (ruleMap.containsKey(userId)) {
							ruleMap.get(userId).add(rule);
						} else {
							List<Rule> rl = New.arrayList();
							rl.add(rule);
							ruleMap.put(userId, rl);
						}
					}
					rule.setUserIds(null);
				} else {
					noIdentificationList.add(rule);
				}

				// 处理按分钟投放
				if (rule.getTotalCount() > 0 && StaticAds.minuteTimes.size() > 0) {
					int i = 0;
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
					Calendar initDate = getInitDate(); // 00:00
					while (i < 24) {
						for (int j = 0; j <= 59; j++) {
							String minuteStr = sdf.format(initDate.getTime());
							int number = 0;
							if(StaticAds.minuteTimes.containsKey(minuteStr)) {
								number = (int) (StaticAds.minuteTimes.get(minuteStr) * rule.getTotalCount());
							}
							String key = adv.getId() + "#" + rule.getRuleId();
//							logger.info("存放按分钟投放: key={}, nimuteStr={}, number={}", key, minuteStr, number);
							StaticAds.setMinuteNumbers(key, minuteStr, number);
							
//							System.out.println(sdf.format(initDate.getTime()));
							initDate.add(Calendar.MINUTE, 1);
						}
						i++;
					}
				}
			} catch (Exception e) {
				logger.error("prepareAdvByType exception: {}", e);
				continue;
			}
		}
		// 保存有标识用户的广告
		if (ruleMap.size() > 0) {
			for (Map.Entry<String, List<Rule>> entry : ruleMap.entrySet()) {
				try {
					// TODO 针对离线算法，新加的内容 05/22
					String[] buf = entry.getKey().split(",");
					String udid = null;
					String rate = null;
					if (buf.length > 2) {	// 文件是3列，其实只用到 udid和rate
						udid = buf[0];
						rate = buf[2];
					} else {
						udid = entry.getKey();
					}

					String key = adv.getId() + getRuleIds(entry.getValue());
					AdContentCacheEle ac = new AdContentCacheEle();
					ac.setAds(adv);
					ac.setRules(entry.getValue());
					if (rate != null) {
						key = rate + "," + key;
						ac.setUserClickRate(new UserClickRate(udid, adv.getId(), Double.parseDouble(rate)));
					}
					if (!StaticAds.allAdContentCache.containsKey(key)) {
						StaticAds.AddAdContentCache(key, ac);
					}

					StaticAds.setIdentificationToMap(udid, key, adv.getShowType());
				} catch (Exception e) {
					logger.error("加载按用户投放广告出错, {}", e.getMessage());
					e.printStackTrace();
				}
			}
		}
		// 保存没有标识用户的广告
		if (noIdentificationList.size() > 0) {
			AdContentCacheEle ac = new AdContentCacheEle();
			ac.setAds(adv);
			ac.setRules(noIdentificationList);
			StaticAds.setNoIdentificationToMap(adv.getShowType(), ac);
		}

		// 组建广告的图片链接字段
		if (ruleMap.size() > 0 || noIdentificationList.size() > 0) {
			adv.completePicUrl();
		}

	}

	private String getRuleIds(List<Rule> rules) {
		if (rules == null || rules.size() == 0) {
			return null;
		}
		String buf = null;
		for (Rule rule : rules) {
			buf += "#" + rule.getRuleId();
		}
		return buf;
	}

	/**
	 * 时间比较
	 * 
	 * @param startDate
	 * @param showType
	 * @return
	 */
	private boolean dateCompare(Date startDate, Date endDate, String showType) {

		if (showType.equals(ShowType.FULL_SCREEN.getType()) || showType.equals(ShowType.OPEN_SCREEN.getType())
				 || showType.equals(ShowType.RIDE_AUDIO.getType())) {	// 浮层，开屏，乘车音频需要预加载
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, 7);// 把日期往后增加七天.整数往后推,负数往前移动
			Date date = calendar.getTime(); // 这个时间就是日期往后推七天的结果
			// 开始时间在7天之内
			if (date.compareTo(startDate) >= 0) {
				return true;
			} else {
				return false;
			}
		} else {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, 1);// 把日期往后增加1天.整数往后推,负数往前移动
			Date date = calendar.getTime(); // 这个时间就是日期往后推1天的结果
			// 开始时间在1天之内
			if (date.compareTo(startDate) >= 0) {
				return true;
			} else {
				return false;
			}

		}
	}

	/**
	 * 读取文件，初始化 按分钟投放缓存
	 * 
	 * @param minuteTimes
	 */
	private void initMinuteTimes(Map<String, Double> minuteTimes) {
		BufferedReader fileIn = null;
		try {
			logger.info("读取分钟配置文件:{}", minuteTimesFile);
			logger.info("读取配置文件11111：{}", PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"minuteTimesFile", "/data/advConfig/commonMinuteTimesFile.csv"));
			fileIn = new BufferedReader(new InputStreamReader(new FileInputStream(new File(minuteTimesFile))));
			String str = null;
			while ((str = fileIn.readLine()) != null) {
				try {
					String buf[] = str.split(",");
					if (buf.length >= 2) {
						minuteTimes.put(buf[0], Double.parseDouble(buf[1]));
					} else {
						minuteTimes.put(buf[0], Double.parseDouble(buf[1]));
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("处理分钟投放文件出错，出错行是:{}", str);
				}
			}

			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("处理分钟投放文件出错");
		}
	}

	private Calendar getInitDate() {
		Calendar calendarInstance = Calendar.getInstance();
		calendarInstance.set(Calendar.HOUR_OF_DAY, 0);
		calendarInstance.set(Calendar.MINUTE, 0);
		
		return calendarInstance;
	}

	public static void main(String[] args) {

		StartService start = new StartService();
		// 处理按分钟投放
		int i = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Calendar initDate = start.getInitDate(); // 00:00
		while (i < 24) {
			for (int j = 0; j <= 59; j++) {
				System.out.println(sdf.format(initDate.getTime()) + "," + 0.01);
				initDate.add(Calendar.MINUTE, 1);
			}
			i++;
		}
		
		Map<String, Map<Integer, String>> a = New.hashMap();
		
		Map<Integer, String> temp = New.hashMap();
		temp.put(1, "2");
		a.put("dd", temp);
		
		System.out.println(JSONObject.toJSONString(a));
		
		
		Map<String, String> b = New.hashMap();
		b.put("da", null);
		System.out.println(JSONObject.toJSONString(b));
		
	}
}
