package com.bus.chelaile.flow;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.dao.ActivityContentMapper;
import com.bus.chelaile.dao.AppAdvContentMapper;
import com.bus.chelaile.dao.InsideUdidsMapper;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdDoubleInnerContent;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.model.rule.InsideUdids;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.flow.model.ActivityContent;
import com.bus.chelaile.flow.model.Channel;
import com.bus.chelaile.flow.model.ChannelType;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.model.FlowChannel;
import com.bus.chelaile.flow.model.TabEntity;
import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.util.CitiesList;
import com.bus.chelaile.util.DateUtil;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public class ActivityService {

	private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

	@Autowired
	private AppAdvContentMapper advContent;
	@Autowired
	private ActivityContentMapper activityContentMapper;
	@Autowired
	private InsideUdidsMapper insideUdidsMapper;

	public static final List<ActivityContent> ALL_ACTIVITIES = New.arrayList();
	public static final List<ActivityContent> ALL_TAB_ACTIVITIES = New.arrayList();
	public static final List<AdContent> allFlowAds = New.arrayList();

	// 指定投放信息流的udid列表
	public static final Set<String> FLOWUDIDS = New.hashSet();
	public static final Map<Integer, Map<Integer, FlowChannel>> CHANNELS = New.hashMap(); // 第一个key是type，第二个key是id
	private static final Set<FlowChannel> CUSTOM_CHANNELS = New.hashSet();	// 存放自定义模块的有效频道。比如id是200,201的那些频道。
	public static final Map<String, String> CITIES = New.hashMap();
	private static final String HEADSTRS = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"detail.flow.udidHEADS", "4|5|6|7");
	private static final List<Channel> FAVCHANNELS = New.arrayList();
	private static final List<String> HEADLIST = New.arrayList();

	public void initActivitity() {
		ALL_ACTIVITIES.clear();
		ALL_TAB_ACTIVITIES.clear();
		FLOWUDIDS.clear();
		CHANNELS.clear();
		CITIES.clear();
		allFlowAds.clear();
		HEADLIST.clear();
		CUSTOM_CHANNELS.clear();
		
		List<ActivityContent> activityContens = activityContentMapper.listValidActivity(); //获取所有的活动，包括上线的和下线的
		if(activityContens != null)
			ALL_ACTIVITIES.addAll(activityContens);
		List<AdContent> allAds = advContent.listValidAds();
		for(AdContent ad : allAds) {
			if(ad.getShowType().equals(ShowType.FLOW_ADV.getType())) {
				logger.info("发现信息广告：advId={},advTitle={}", ad.getId(), ad.getTitle());
				allFlowAds.add(ad);
			}
		}
		
		List<ActivityContent> tabActivities = activityContentMapper.listTabActivity(); //获取所有tab弹窗
//		if(tabActivities != null)
			ALL_TAB_ACTIVITIES.addAll(tabActivities);	// TODO  测试如果没有任何tab活动的时候，会否出错

		// 获取内部udid, type=1 或者0 表示 信息流 内推用户
		// 从文件获取指定投放的udid列表
		initFlowudids(FLOWUDIDS);

		// 初始化uc channels,包括所有channel和自定义模块的channel
		initChannels();

		// 初始化城市列表
		CITIES.putAll(CitiesList.getAllCities());
		
		// 初始化默认收藏频道
		initDefaultFavChannels();

		for(String s : HEADSTRS.split("\\|")) {
			HEADLIST.add(s);
		}
		
		logger.info("活动初始化成功，活动数目size={}", ALL_ACTIVITIES.size());
		logger.info("投放信息流的内测用户成功，数目是size={}", FLOWUDIDS.size());
		logger.info("初始化channel成功，总数={}, 自定义模块有效频道数={}，分别是：{}", CHANNELS.size(), CUSTOM_CHANNELS.size(), CUSTOM_CHANNELS.toString());
		logger.info("初始化城市列表成功，总数={}", CITIES.size());
		logger.info("初始化信息流广告结束，总数={}", allFlowAds.size());
		logger.info("初始化投放详情页信息流的用户第一个字母结束，字母分别是={}", HEADLIST.toString());
		logger.info("初始化所有Tab弹窗，数目是={}", ALL_TAB_ACTIVITIES.size());
	}

	/*
	 * 指定投放的用户
	 */
	private void initFlowudids(Set<String> flowudids2) {
		// 数据库
		List<InsideUdids> insideUdidsList = insideUdidsMapper.listAllInsideUdids();
		if (insideUdidsList != null && insideUdidsList.size() != 0) {
			for (InsideUdids inside : insideUdidsList) {
				if (inside.getType() == 0 || inside.getType() == 1) {
					FLOWUDIDS.add(inside.getUdid());
				}
			}
		}
		
		// 文件
//		try{
//			String dateStr = DateUtil.getFormatTime(new Date(), "yyyy-MM-dd");
//			String todayStr = DateUtil.getTodayStr("yyyyMM");
//			String flowudidFile = "/data/outman/rule/" + todayStr + "/" + dateStr + ".flowudid.csv";
//			FLOWUDIDS.addAll(FileUtil.getFileContent(flowudidFile));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 获取信息流活动内容
	 */
	public HashMap<Integer, FlowContent> getActivity(AdvParam advParam, int id, ChannelType channelType) {
		if( ! isReturnActives(advParam, id, channelType)) {
			return null;
		}

		// 03-15,应对android 3.29.0 bug, 刷新按钮不予返回活动
		Platform platform = Platform.from(advParam.getS());
		if (platform.isAndriod(platform.getDisplay()) && advParam.getVc() == 76
				&& StringUtils.isNoneEmpty(advParam.getStatsAct()) && advParam.getStatsAct().equals("article_refresh")) {
			return null;
		}
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
//		paramMap.put("wse", "1");			//放在最外层的参数，未编码进link参数里面，如果跳转是ad，最终页面会丢失这个参数
//		paramMap.put("gse", "1");
		if(advParam.getAccountId() != null) {
			paramMap.put("accountId", advParam.getAccountId());
		}
		HashMap<Integer, FlowContent> ucMap = new HashMap<Integer, FlowContent>();

		// 获取符合规则的活动
		List<ActivityContent> allActivites = getActivites(ALL_ACTIVITIES, advParam, id, channelType);
		if (allActivites == null) {
			return null;
		}
		
		if(channelType == ChannelType.CUSTOM) {
			Collections.sort(allActivites, CUSTOM_ID_COMPARATOR);
		}
		
		// 根据从数据库获取到的活动构建返回给信息流的活动
		int i = 0;
		for (ActivityContent activity : allActivites) {
			FlowContent content = new FlowContent();
			if (content.fillActivityInfo(activity, advParam, paramMap)) {
				if(channelType != ChannelType.CUSTOM) {
					ucMap.put(activity.getSort_index(), content);
				} else {
					ucMap.put(i, content);
					i ++;
				}
			}
		}
		
		return ucMap;
	}
	
	
	/**
	 * 获取信息流广告
	 */
	public HashMap<Integer, FlowContent> getAdv(AdvParam advParam, int id, ChannelType channelType) {
		// 下拉‘获取更多’的时候
		HashMap<Integer, FlowContent> ucMap = new HashMap<Integer, FlowContent>();
		try {
			if (!isReturnActives(advParam, id, channelType)) {
				return null;
			}

			for (AdContent ad : allFlowAds) {
				ad.completePicUrl();
				FlowContent content = new FlowContent();

				AdEntity adEntity = new AdEntity(ShowType.FLOW_ADV.getValue());
				AdDoubleInnerContent adInner = (AdDoubleInnerContent) ad.getInnerContent();
				if (adInner == null) {
					logger.info("adinner为空, ");
					return null;
				}
				adInner.fillAdEntity(adEntity, advParam, 0);
				Map<String, String> paramMap = New.hashMap();
				if(advParam.getAccountId() != null) {
					paramMap.put(Constants.PARAM_ACCOUNTID, advParam.getAccountId());
				}
				adEntity.fillBaseInfo(ad, advParam, paramMap);
				adEntity.dealLink(advParam);

				content.setType(2);
				content.setImgsType(3); // 单图大图，且无遮罩模式
				content.setAdEntity(adEntity);
				content.setTitle(ad.getTitle()); // 广告的title会显示在图片上面，所以去掉
				content.setId(adEntity.getId() + "");
				content.setUrl(adEntity.getLink());

				ArrayList<Thumbnail> imgs = new ArrayList<Thumbnail>();
				imgs.add(new Thumbnail(adEntity.getBrandIcon())); // 暂时，
																	// 利用双栏广告的结构。这个字段是图片链接
				content.setImgs(imgs);
				content.setTag(adEntity.getButtonTitle()); // tag文字
				content.setTagColor(adEntity.getButtonColor()); // tag颜色

				ucMap.put(adInner.getPosition(), content);
			}
			return ucMap;
		} catch (Exception e) {
			e.printStackTrace();
			return ucMap;
		}
	}
	
	/**
	 * 获取Tab弹窗内容
	 * @param advParam
	 * @return
	 */
	public List<TabEntity> getTabActivities(AdvParam advParam, int entryId) {
		// 获取符合规则tab弹窗
		if(ALL_TAB_ACTIVITIES == null || ALL_TAB_ACTIVITIES.size() == 0) {
			return null;
		}
		HashMap<String, String> paramMap = new HashMap<String, String>();
		List<TabEntity> tabs = New.arrayList();
		if(advParam.getAccountId() != null) {
			paramMap.put("accountId", advParam.getAccountId());
		}
		for (ActivityContent activity : ALL_TAB_ACTIVITIES) {
			if(ruleCitiesCheck(activity, advParam)) {
				TabEntity tabEntity = new TabEntity();
				if (activity.getEntry_id() == entryId && tabEntity.fillActivityInfo(activity, advParam, paramMap)) {
					tabs.add(tabEntity);
				}
			}
		}
		return tabs;
	}
	
	/*
	 * 获取信息流，有一定的规则判断。目前只有城市
	 */
	private List<ActivityContent> getActivites(List<ActivityContent> activities, AdvParam advParam, int id, ChannelType channelType) {
		if (activities == null || activities.size() == 0)
			return null;

		List<ActivityContent> activites = New.arrayList();
		for (ActivityContent activity : activities) {

			if (ruleCheck(activity, advParam, id, channelType)) {
				activites.add(activity);
			}
		}
		
		return activites;
	}
	

	/*
	 * 控制规则
	 */
	private boolean ruleCheck(ActivityContent activity, AdvParam advParam, int id, ChannelType channelType) {

		if (StringUtils.isBlank(advParam.getUdid())) {
			return false;
		}
		
		
		// 频道控制。对于自定义模块，需要信息流的类型为自定义的类型2，并且频道id需要对上。
		// 自定义模块都在这里处理，没有涉及到城市控制
		if(channelType == ChannelType.CUSTOM) {
			if(activity.getChannelType() == ChannelType.CUSTOM.getType() && activity.getCustom_channel_id() == id) {
				return true;
			} else {
				return false;
			}
		}
		
		//如果是自定义模块的，判断是否需要加入推荐频道中
		if(activity.getChannelType() == ChannelType.CUSTOM.getType() && activity.getIsShowInRecommend() != 1) {
			return false;
		}
		

		// 控制投放，目前只有城市， 时间 2017-05-11
		if(ruleCitiesCheck(activity, advParam)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断城市规则
	 * @param activity
	 * @param advParam
	 * @return
	 */
	private boolean ruleCitiesCheck(ActivityContent activity, AdvParam advParam) {
		if (activity.getRule() != null && !activity.getRule().equals("") && !activity.getRule().equals("{}")) {
			JSONObject jsonRule = JSONObject.parseObject(activity.getRule());
			JSONArray jsonCityIds = jsonRule.getJSONArray("cities");
			String startDate = jsonRule.getString("startDate");
			String endDate = jsonRule.getString("endDate");

			// 判断有效时间
			try {
				if (StringUtils.isNoneBlank(startDate) && StringUtils.isNoneBlank(endDate)) {
					String nowDate = DateUtil.getTodayStr("yyyy-MM-dd HH:mm:ss");
					if (nowDate.compareTo(startDate) > 0 && nowDate.compareTo(endDate) < 0) {
						// 有效期内，接下来继续判断城市
					} else {
						logger.info("time Over due, activityId={}, startDate={}, endDate={}",
								activity.getActivity_id(), startDate, endDate);
						return false;
					}
				}

				// 判断城市
				if (jsonCityIds != null && jsonCityIds.size() > 0) {
					if (advParam.getCityId() == null) {
						return false;
					}

					int size = jsonCityIds.size();
					for (int i = 0; i < size; i++) {
						if (advParam.getCityId().equals(jsonCityIds.get(i))) {
							return true;
						}
					}
					logger.info("is city matche return false, activityId={}, udid={}, cityId={}, rule={}",
							activity.getActivity_id(), advParam.getUdid(), advParam.getCityId(), activity.getRule());
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}
		return true;
	}

	/*
	 * 是否投放信息流广告
	 */
	public boolean isReturnUC(String udid, String refer, int supportNovel) {
		if (udid == null) {
			return false;
		}

		if (supportNovel == 1 || (refer != null  && ! refer.equals("lineDetail"))) {
			return true; // 发现和乘车跳转 的信息流 全开 || 支持小说的城市，全开
		}
		
		if ((refer == null || refer.equals("lineDetail")) && (isFirstWord(udid, HEADLIST))) {
			return true; // 详情页的信息流
		}
		
		if (FLOWUDIDS.contains(udid)) {
			return true;
		}
		if (Constants.ISTEST) {			// TODO 
			return true; // FOR TEST
		}

		return false;
	}

	/*
	 * 根据udid决定是否打开详情页信息流入口
	 */
	public int getHasLineDetailArticles(String udid, int supportNovel) {
		if(udid == null) {
			return 0;
		}
		if(isFirstWord(udid, HEADLIST) || supportNovel == 1) {
			return 1;					// udid开头的一些用户打开详情页入口||支持小说的，全部打开详情页入口
		}
		
		if (FLOWUDIDS.contains(udid)) {
			return 1;
		}
		if (Constants.ISTEST) {
			return 1; 			// FOR TEST	 // TODO 
		}
		return 0;
	}
	
	private boolean isFirstWord(String udid, List<String> headlist) {
		if(headlist != null && udid != null) {
			for(String s : headlist) {
				if(udid.startsWith(s)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * 根据udid决定投放何种渠道的文章
	 */
	public ChannelType getChannelType(String udid, int id) {
		// if (UCudids.contains(udid) || ISTEST) { // 全开！ FOR TEST
		// return ChannelType.TOUTIAO; // 手动控制 内部用户 和 测试用户 头条或者uc
		// }
		// if (udid.startsWith(HEAD1)) {
		// return ChannelType.UC;
		// }
		// if (udid.startsWith(HEAD2)) {
		// return ChannelType.TOUTIAO;
		// }
		// logger.error("不在预期内的udid投放了详情页信息流:udid={}", udid);
		
		//判断id是否是自定义模块里面的
		if(CHANNELS.containsKey(ChannelType.CUSTOM.getType())){
			logger.info("custom Channels = {}", CHANNELS.get(ChannelType.CUSTOM.getType()));
			
			for(Integer i : CHANNELS.get(ChannelType.CUSTOM.getType()).keySet()) {
				if(id == i) {
					logger.info("用户的频道类型是：{}", "CUSTOM");
					return ChannelType.CUSTOM;
				}
			}
		}
		
		return ChannelType.TOUTIAO;	 //TODO
	}
	

	/*
	 * 根据udid决定何种展示方式
	 */
	public int getArticlesShowType(String udid, String accountId, int supportNovel) {
		if(supportNovel == 1) {	//支持小说的城市，返回‘固定单栏’形式
			return 0;
		}
		return 1;

//		if(UserHelper.isTodayNewUser(udid, accountId)) {	// 当天新增用户
////		if(UserHelper.isNewUser(udid, null, accountId)) {	// 近7天新增用户
//			int code = Math.abs(udid.hashCode()) % TITLE_TYPE_SIZE;
//			logger.info("ariticleType:udid={},type={}", udid, code);
//			if(code == 0) {
//				return 0;
//			} else if (code == 1) {
//				return 1;
//			} else if (code == 2) {
//				return 2;
//			} else if(code == 3) {		// 对比样本，不投详情页信息流
//				return 3;
//			}
//		}
		
//		if(FLOWUDIDS.contains(udid)) {
//			int code = Math.abs(udid.hashCode()) % TITLE_TYPE_SIZE;
//			logger.info("ariticleType:udid={},type={}", udid, code);
//			if(code == 0) {
//				return 0;
//			} else if (code == 1) {
//				return 1;
//			} else if (code == 2) {
//				return 2;
//			}
//		}
//		
//		
//		return 2;
	}


	/*
	 * 获取所有的channel,初始化渠道缓存
	 */
	private void initChannels() {
		List<FlowChannel> channels = New.arrayList();

		channels = activityContentMapper.listValidChannel();
		if (channels != null) {
			for (FlowChannel channel : channels) {
				if (!CHANNELS.containsKey(channel.getChannelType())) {
					Map<Integer, FlowChannel> tmp = New.hashMap();
					tmp.put(channel.getId(), channel);
					CHANNELS.put(channel.getChannelType(), tmp);
				} else {
					Map<Integer, FlowChannel> tmp = CHANNELS.get(channel.getChannelType());
					tmp.put(channel.getId(), channel);
					CHANNELS.put(channel.getChannelType(), tmp);
				}
			}
		} else {
			logger.error("初始化channel失败！！ ");
		}
		// logger.info("初始化后所有channel是：{}", JSONObject.toJSONString(CHANNELS));
	
		if(CHANNELS.containsKey(ChannelType.CUSTOM.getType())) {
			CUSTOM_CHANNELS.addAll(CHANNELS.get(ChannelType.CUSTOM.getType()).values());
		}
	}

	/*
	 * 获取所有的channel给客户端
	 */
	public List<Channel> getChannels(AdvParam advParam) {
		List<Channel> channels = new ArrayList<>();

//		ChannelType channelType = getChannelType(advParam.getUdid(), 0);
		List<FlowChannel> ucChannels = new ArrayList<>(CHANNELS.get(ChannelType.TOUTIAO.getType()).values());

		// 是否投放‘本地’频道
		boolean delBenDi = false; // 默认删除本地频道
		String cityName = getCityName(advParam.getCityId());
		if (cityName == null) {
			delBenDi = true;
		}

		for (FlowChannel ucch : ucChannels) {
			if (!(delBenDi && (ucch.getChannelId().equals("200") || ucch.getChannelId().equals("news_local")))) {
				channels.add(new Channel(ucch.getId(), ucch.getName()));
			}
		}

		Collections.sort(channels, new Comparator<Channel>() {
			@Override
			public int compare(Channel o1, Channel o2) {
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;

				return o1.getId() - o2.getId();
			}
		});

		//增加自定义模块的频道
		if(CHANNELS.containsKey(ChannelType.CUSTOM.getType())) {
			List<FlowChannel> custom_channels = new ArrayList<>(CHANNELS.get(ChannelType.CUSTOM.getType()).values());
			for (FlowChannel ucch : custom_channels) {
				channels.add(new Channel(ucch.getId(), ucch.getName()));
			}
		}
		
		return channels;
	}

	/*
	 * 获取指定id的UCChannle
	 */
	public FlowChannel getChannels(int channelId, ChannelType channelType) {
		if (CHANNELS == null) {
			logger.error("频道信息为空");
			return null;
		}
		return CHANNELS.get(channelType.getType()).get(channelId);
	}

	/*
	 * 获取城市name
	 */
	public String getCityName(String cityId) {
		if (cityId == null) {
			return null;
		}
		if (CITIES == null) {
			logger.error("城市列表为空");
			return null;
		}
		return CITIES.get(cityId);
	}


	/**
	 * 版本控制，是否返回收藏渠道
	 * 
	 * @param advParam
	 * @return
	 */
	public boolean isNewVersion (AdvParam advParam) {
		// 06-02,应对android 3.33.0 以上的版本, ios 5.31.1 以上的版本， 返回收藏渠道
		Platform platform = Platform.from(advParam.getS());
		if (platform.isAndriod(platform.getDisplay()) && advParam.getVc() > 81) {
			return true;
		}
		if (platform.isIOS(platform.getDisplay()) && advParam.getVc() > 10361) {
			return true;
		}
		return false;
	}
	
	/**
	 * 返回默认的频道，目前直接写死。后续增加新的渠道类型的时候再更新
	 * @return
	 */
	public List<Channel> getDefaultFavChannels() {
		if(FAVCHANNELS != null && FAVCHANNELS.size() > 0) {
			return FAVCHANNELS;
		} else {
			logger.error("默认收藏为空");
			List<Channel> channels = New.arrayList();
			channels.add(new Channel(1, "推荐"));
			channels.add(new Channel(31, "热点"));
			return channels;
		}
	}
	
	/*
	 * 是否返回活动或者广告
	 */
	private boolean isReturnActives (AdvParam advParam, int id, ChannelType channelType) {
		if(channelType == ChannelType.CUSTOM) {	//自定义模块，除了下拉，任何时候都需要返回信息流
			if(StringUtils.isEmpty(advParam.getStatsAct()) || advParam.getStatsAct().equals("get_more")) {
				return false;
			}
			return true;
		}
		
		
		Object beginIndexStr = CacheUtil.getNew("flowIndex" + advParam.getUdid());
//		logger.info("获取信息流之前，获取用户的beginIndexStr值为：udid={}, beginIndexStr={}", advParam.getUdid(), beginIndexStr);
		int beginIndex = 0;
		if(beginIndexStr != null) {
			beginIndex = (Integer)(beginIndexStr);
		}
		
		// 下拉‘获取更多’的时候，不拉取活动和广告
		// 上一次拉取信息流，没有多余的，则江beginIndex赋值为0
		if (beginIndex == 0 && (StringUtils.isEmpty(advParam.getStatsAct()) || advParam.getStatsAct().equals("get_more"))) {
			return false;
		}
		
		if(beginIndex != 0 && StringUtils.isNotEmpty(advParam.getStatsAct()) && !advParam.getStatsAct().equals("get_more")) {
			beginIndex = 0;	//beginIndex不为0，且非下拉的请求，将beginIndex赋值为0
			CacheUtil.setNew("flowIndex" + advParam.getUdid(), Constants.ONE_DAY_TIME, beginIndex);
//			logger.info("给beginIndex赋初始化值,udid={}", advParam.getUdid());
		}
		
		if(id != -1 && id != 1) {
			return false;	//默认频道才返回信息流或者广告
		}
		
		return true;
	}
	
	private void initDefaultFavChannels() {
		if(FAVCHANNELS != null && FAVCHANNELS.size() > 0) {
			return;
		}
		FAVCHANNELS.clear();
		
		FAVCHANNELS.add(new Channel(1, "推荐"));
		for(FlowChannel flow :CUSTOM_CHANNELS) {
			FAVCHANNELS.add(new Channel(flow.getId(), flow.getName()));
		}
		FAVCHANNELS.add(new Channel(31, "热点"));
//		FAVCHANNELS.add(new Channel(32, "本地"));
		FAVCHANNELS.add(new Channel(36, "汽车"));
		FAVCHANNELS.add(new Channel(33, "社会"));
		FAVCHANNELS.add(new Channel(34, "娱乐"));
		FAVCHANNELS.add(new Channel(35, "科技"));
		FAVCHANNELS.add(new Channel(39, "体育"));
		FAVCHANNELS.add(new Channel(38, "军事"));
		FAVCHANNELS.add(new Channel(42, "国际"));
		FAVCHANNELS.add(new Channel(59, "星座"));
		logger.info("默认收藏初始化成功 ");
	}
	
	/**
	 * 应该按照优先级倒叙排序
	 */
	private static final Comparator<ActivityContent> CUSTOM_ID_COMPARATOR = new Comparator<ActivityContent>() {
		@Override
		public int compare(ActivityContent o1, ActivityContent o2) {
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return (o2.getActivity_id() - o1.getActivity_id());
		}
	};
	
	
	public static void main(String[] args) throws ParseException{

		// ApplicationContext context = new
		// ClassPathXmlApplicationContext("classpath:servicebiz/locator-baseservice.xml");
		// StartService st = context.getBean(StartService.class);
		// ActivityContentMapper activityContentMapper =
		// context.getBean(ActivityContentMapper.class);
		//
		// List<UCChannel> channels = New.arrayList();
		//
		// channels = activityContentMapper.listValidChannel();
		//
		// System.out.println("结束");
		List<Channel> channels = New.arrayList();
		List<Channel> favChannels = New.arrayList();
		
		channels.add(new Channel(1, "推荐"));
//		channels.add(new Channel(2, "热点"));
		
		favChannels.add(new Channel(1, "推荐"));
		favChannels.add(new Channel(2, "热点"));
		
		channels.removeAll(favChannels);
		
		
		
		String head = "1|2|3|4";
		for(String s : head.split("\\|")) {
			System.out.println(s);
		}
		
		Map<Integer, String> map = New.hashMap();
		map.put(2, "a");
		map.put(8, "b");
		System.out.println(map.toString());
		
		String nowDate = DateUtil.getTodayStr("yyyy-MM-dd HH:mm:ss");
		System.out.println(nowDate);
		String startDate = ("2017-11-29 11:21:00");
		String endDate = ("2017-11-24 11:59:00");
		System.out.println(nowDate.compareTo(startDate));
		System.out.println(nowDate.compareTo(startDate) > 0 && nowDate.compareTo(endDate) < 0);
		
	}
}
