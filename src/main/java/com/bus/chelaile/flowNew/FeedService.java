package com.bus.chelaile.flowNew;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.ActivityService;
import com.bus.chelaile.flow.FlowService;
import com.bus.chelaile.flow.ToutiaoHelp;
import com.bus.chelaile.flow.WangYiYunHelp;
import com.bus.chelaile.flow.WuliToutiaoHelp;
import com.bus.chelaile.flow.model.ChannelType;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.flowNew.customContent.FeedInfo;
import com.bus.chelaile.flowNew.customContent.TagUtils;
import com.bus.chelaile.flowNew.model.FeedContent;
import com.bus.chelaile.flowNew.model.FlowNewContent;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.ads.entity.FeedAdEntity;
import com.bus.chelaile.model.client.ClientDto;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.ServiceManager;
import com.bus.chelaile.util.New;

public class FeedService {
	@Autowired
	private ToutiaoHelp toutiaoHelp;
	@Autowired
	private WangYiYunHelp wangYiYunHelp;
	@Autowired
	private WuliToutiaoHelp wuliToutiaoHelp;
	@Autowired
	private ServiceManager serviceManager;
	@Autowired
	private ActivityService activityService;
	@Resource
	private FlowService flowService;

	protected static final Logger logger = LoggerFactory.getLogger(FeedService.class);
	private static final int FEEDSIZE_LIMIT = 3;
	private static final int FEED_NEW_LIMIT = 12;	 // 线上放12个

	/**
	 * 详情页下方 feed 流 。3.0 版
	 * 
	 * @param param
	 * @return
	 */
	public String getResponseLineDetailFeeds(AdvParam param) {
		// 单栏信息流
		List<FlowNewContent> flows = getLineDetailFeeds(param);
		if (flows != null && flows.size() > 0) {
			JSONObject responseJ = new JSONObject();
			responseJ.put("flows", flows);
			return getClienSucMap(responseJ, Constants.STATUS_REQUEST_SUCCESS);
		} else {
			return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
		}
	}
	
	/**
	 * 详情页下方 feed 流 。4.0 版
	 * 
	 * @param param
	 * @return
	 */
	public String getResponseLineDetailNewFeeds(AdvParam param) {
		// 单栏信息流
		List<FeedContent> flows = getLineDetailNewFeeds(param);
		if (flows != null && flows.size() > 0) {
			JSONObject responseJ = new JSONObject();
			responseJ.put("flows", flows);
			return getClienSucMap(responseJ, Constants.STATUS_REQUEST_SUCCESS);
		} else {
			return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
		}
	}
	

	public List<FlowNewContent> getLineDetailFeeds(AdvParam param) {
		List<FlowNewContent> flows = New.arrayList();
		// 条件判断
		// if (FlowStaticContents.isReturnLineDetailFlows(param)) {
		createList(param, flows);
		// }
		return flows;
	}


	private List<FeedContent> getLineDetailNewFeeds(AdvParam param) {
		List<FeedContent> feeds = New.arrayList();
		List<FeedContent> feedAds = New.arrayList();
		List<FeedContent> feedArticles = New.arrayList();
		List<FeedContent> flowsFeed = New.arrayList();

		// 广告
		List<FeedAdEntity> adList = null;
		try {
			adList = serviceManager.getFeedAds(param);
			if(adList != null)
				logger.info("获取到feedads广告数目：{}", adList.size());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取feeds广告出错, {}", e, e.getMessage());
		}
		if (adList != null && adList.size() > 0) {
			adAdToFeeds(param, feedAds, adList);
		}

		// 文章
		List<FlowContent> flowsApi = null;
		ChannelType channelType = activityService.getChannelType(param.getUdid(), -1);
		try {
			if (channelType == ChannelType.TOUTIAO) {
				flowsApi = toutiaoHelp.getInfoByApi(param, 0L, null, -1, false);
			} else if (channelType == ChannelType.WANGYI) {
				flowsApi = wangYiYunHelp.getInfoByApi(param, 0L, null, 3, false); // 3 是中山本地的频道id
			}
			else if (channelType == ChannelType.WULITOUTIAO) {
				flowsApi = wuliToutiaoHelp.getArticlesFromCache(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (flowsApi != null) {
			for (int i = 0; i < flowsApi.size(); i++) {
				feedArticles.add(flowsApi.get(i).createNewFeeds());
				if ((i + 1) % 3 != 0) {
					feedArticles.get(i).setImgsType(0);
				}
			}
		}

		// 获取更多的时候，拉取历史，否则获取最新的额
//		int index = 0;
//		if (StringUtils.isNoneBlank(param.getStatsAct()) && param.getStatsAct().equals("get_more")) {
//			index = Integer.parseInt((String) CacheUtil.get("FEED_CACHE_USER_INDEX#" + param.getUdid()));
//		}
//		logger.info("用户话题index, udid={}, index={}", param.getUdid(), index);

		//给测试用的特殊例子
//		if (returnFeedNew(param)) {
//			FeedInfo feedTemp = TagUtils.getFeedInfo("646277590927118336", param.getAccountId());
//			index = createFeedFromFeedInfo(flowsFeed, index, feedTemp);
//			FeedInfo feedTemp1 = TagUtils.getFeedInfo("646277359619641344", param.getAccountId());
//			index = createFeedFromFeedInfo(flowsFeed, index, feedTemp1);
//			FeedInfo feedTemp2 = TagUtils.getFeedInfo("646277180946485248", param.getAccountId());
//			index = createFeedFromFeedInfo(flowsFeed, index, feedTemp2);
//			FeedInfo feedTemp3 = TagUtils.getFeedInfo("646276268542758912", param.getAccountId());
//			index = createFeedFromFeedInfo(flowsFeed, index, feedTemp3);

			// 每次返回3条feed
//			for (int size = 0; size < FEEDSIZE_LIMIT; size++) {
//				String fid = (String) CacheUtil.get("FEED_SORT_CACHE" + "#" + index);
//				if (fid == null) {
//					index = 0;
//					fid = (String) CacheUtil.get("FEED_SORT_CACHE" + "#" + index);
//					if(fid == null) {
//						continue;
//					}
//				}
//
//				FeedInfo feedf = TagUtils.getFeedInfo(fid, param.getAccountId());
//				index = createFeedFromFeedInfo(flowsFeed, index, feedf);
//			}
//			// 保存获取话题的位置
//			CacheUtil.set("FEED_CACHE_USER_INDEX#" + param.getUdid(), Constants.ONE_DAY_TIME, String.valueOf(index));
//		}

		// 排序
		String[] ids = sortNewFeeds(feeds, feedAds, feedArticles, flowsFeed);
		
		// 记录feed流下发的日志
		AnalysisLog
		.info("[GET_FEEDS]: accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={}, "
				+ "id={}, feedIds={}, stats_act={}, refer={}",
				param.getAccountId(), param.getUdid(),
				param.getCityId(), param.getS(), param.getV(), param.getLineId(),
				param.getStnName(), param.getNw(), param.getIp(), param.getDeviceType(),
				param.getLng(), param.getLat(), ids[0], ids[1],param.getStatsAct(), param.getRefer());

		return feeds;
	}

	// 信息流是否返回话题
	private boolean returnFeedNew(AdvParam advParam) {
		// 01-13,应对android 3.45.0及 以上的版本, ios 5.43.0及 以上的版本， 返回话题内容
		Platform platform = Platform.from(advParam.getS());
		if (platform.isAndriod(platform.getDisplay()) && advParam.getVc() >= Constants.PLATFORM_LOG_ANDROID_0118) {
			return true;
		}
		if (platform.isIOS(platform.getDisplay()) && advParam.getVc() >= Constants.PLATFORM_LOG_IOS_0117) {
			return true;
		}
		return false;
	}

	private int createFeedFromFeedInfo(List<FeedContent> flowsFeed, int index, FeedInfo feedTemp) {
		if(feedTemp != null) {
			index ++;
			FeedContent ff = new FeedContent();
			ff.setFeedInfo(feedTemp);
			ff.setId(feedTemp.getFeed().getFid());
			ff.setDestType(0);
			flowsFeed.add(ff);
		}
		return index;
	}

	// 排序
	private String[] sortNewFeeds(List<FeedContent> feeds, List<FeedContent> feedAds, List<FeedContent> feedArticles, List<FeedContent> flowsFeed) {
		int index = 0; // 列表组合，计数间隔使用
		int limitsize = 0; // 列表记录，总数
		Iterator<FeedContent> it = feedAds.iterator();
		boolean hasTopAd = false; // 是否有顶置广告
		while (it.hasNext()) {
			FeedContent x = it.next();
			// 置顶广告
			if (x.getAds() != null && x.getAds().getIsSetTop() == 1) {
				feeds.add(x);
				it.remove();
				index++;limitsize++;
				hasTopAd = true;
				break;
			}
		}
		String feedIds = "";
		int fIndex = 0; // 话题计数
		if(!hasTopAd && flowsFeed != null && flowsFeed.size() > 0) {
			feeds.add(flowsFeed.get(0));	// 没有顶置广告，那么放一个话题
			index ++;limitsize++;
			feedIds += flowsFeed.get(0).getId() + ";";
			fIndex ++;
		}

		String articleIds = "";
		int aIndex = 0; // 广告计数
		for (FeedContent f : feedArticles) {
			feeds.add(f);
			index++;limitsize++;
			articleIds += f.getId() + ";";
			if(limitsize >= FEED_NEW_LIMIT)		// 默认给12条即可
				break;
			if (index >= 2 && aIndex < feedAds.size() && aIndex < 1) {	 // 除去顶置外，最多一条广告
				feeds.add(feedAds.get(aIndex));
				index = 1;limitsize++;
				aIndex ++;
			}
			if(index >= 4 && fIndex < flowsFeed.size()) {
				feeds.add(flowsFeed.get(fIndex));
				index = 1;limitsize++;
				feedIds += flowsFeed.get(fIndex).getId() + ";";
				fIndex ++;
			}
		}
		return new String[]{articleIds, feedIds};
	}

	
	// 根据 feed流广告，构建 详情页feed流 FEED
	private void adAdToFeeds(AdvParam param, List<FeedContent> feedAds, List<FeedAdEntity> adList) {
		for (FeedAdEntity ads : adList) {
			// List<String> imgs = New.arrayList();
			// imgs.add(ads.getPic());
			List<Thumbnail> imgs = New.arrayList();
			Thumbnail img = new Thumbnail(ads.getPic(), ads.getWidth(), ads.getHeight());
			imgs.add(img);
			FeedContent feed = null;
			if (ads.getFeedAdType() == 0) { // 话题样式
				feed = new FeedContent(String.valueOf(ads.getId()), 5, imgs, 3, ads.getFeedInfo().getTitle(), null, ads
						.getFeedInfo().getTime(), ads);
				feed.getAds().getFeedInfo().setIsLike(getIsLikeAds(ads.getId(), param.getUdid()));
				feed.getAds().getFeedInfo().setLikeNum(getLikeNum(ads.getId()));
			} else if (ads.getFeedAdType() == 1) { // 透视样式
				feed = new FeedContent(String.valueOf(ads.getId()), 5, imgs, 2, null, null, 0L, ads);
			} else if (ads.getFeedAdType() == 2) { // 文章样式
				logger.info("文章样式的feed流广告， advId={}", ads.getId());
				List<Thumbnail> imgsAr = New.arrayList();
				int imgsType = 0;	// 单图
				if(ads.getArticleInfo().getImgs().size() > 1) {
					imgsType = 1;   // 三图
				}
				for(String s : ads.getArticleInfo().getImgs()) {
					imgsAr.add(new Thumbnail(s));
				}
				feed = new FeedContent(String.valueOf(ads.getId()), 5, imgsAr, imgsType, ads.getArticleInfo().getTitle(),
						null, ads.getArticleInfo().getTime(), ads);
			}

			feedAds.add(feed);
		}
	}


	/*
	 * 可能涉及到一些链接增加用户id之类的修正
	 */
	private void createList(AdvParam advParam, List<FlowNewContent> flows) {

		List<FlowNewContent> flowTagDetail = New.arrayList();
		List<FlowNewContent> flowActivity = New.arrayList();
		List<FlowNewContent> flowGame = New.arrayList();
		List<FlowNewContent> flowArticle = New.arrayList();
		List<FlowNewContent> flowWeal = New.arrayList();

		String key;
		String lineFlowsStr;

		key = "QM_LINEDETAIL_FLOW_" + 7; // 话题详情页
		lineFlowsStr = (String) CacheUtil.getNew(key);
		if (StringUtils.isNoneBlank(lineFlowsStr)) {
			flowTagDetail = JSON.parseArray(lineFlowsStr, FlowNewContent.class);
		}

		key = "QM_LINEDETAIL_FLOW_" + 1; // 普通活动
		lineFlowsStr = (String) CacheUtil.getNew(key);
		if (StringUtils.isNoneBlank(lineFlowsStr)) {
			flowActivity = JSON.parseArray(lineFlowsStr, FlowNewContent.class);
		}

		key = "QM_LINEDETAIL_FLOW_" + 10; // 游戏
		lineFlowsStr = (String) CacheUtil.getNew(key);
		if (StringUtils.isNoneBlank(lineFlowsStr)) {
			flowGame = JSON.parseArray(lineFlowsStr, FlowNewContent.class);
		}

		List<FlowContent> contentsFromApi = null;
		ChannelType channelType = activityService.getChannelType(advParam.getUdid(), -1);
		try {
			long ftime = 0L; String recoid = null;
			contentsFromApi = flowService.getApiContent(advParam, ftime, recoid, -1, channelType, contentsFromApi);
			
			for (FlowContent f : contentsFromApi) {
				flowArticle.add(f.createFeeds());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		key = "QM_LINEDETAIL_FLOW_" + 11; // 福利
		lineFlowsStr = (String) CacheUtil.getNew(key);
		if (StringUtils.isNoneBlank(lineFlowsStr)) {
			flowWeal = JSON.parseArray(lineFlowsStr, FlowNewContent.class);
		}

		for (int index = 0; index < FlowStartService.LINEDETAIL_NUM; index++) {
			addIntoFlows(flowTagDetail, flows, index, advParam);
			addIntoFlows(flowActivity, flows, index, advParam);
			addIntoFlows(flowGame, flows, index, advParam);
			addIntoFlows(flowArticle, flows, index, advParam);
			addIntoFlows(flowWeal, flows, index, advParam);
		}

		// 手动添加活动内容
		// FlowNewContent f = new FlowNewContent();
		// f.setDestType(1);
		// f.setFlowTitle("活动标题---");
		// f.setFlowTag("这活动厉害了");
		// f.setFlowTagColor("52,152,219");
		// f.setFlowDesc("12312人参与");
		// f.setPic("https://image3.chelaile.net.cn/a6f96bcf5ee742d7aa732259c32d1b8c");
		// f.setLink("http://www.baidu.com");
		//
		// ActivityEntity ac = new ActivityEntity();
		// ac.setLinkUrl("http://www.baidu.com");
		// ac.setImageUrl("https://image3.chelaile.net.cn/a6f96bcf5ee742d7aa732259c32d1b8c");
		// ac.setType(ActivityType.H5.getType());
		// ac.setImageUrl("");
		// f.setActivityEntity(ac);

		// flows.add(f);
	}

	private void addIntoFlows(List<FlowNewContent> flowElement, List<FlowNewContent> flows, int index, AdvParam param) {
		if (index < flowElement.size()) {
			FlowNewContent flow = new FlowNewContent();
			flow.deal(flowElement.get(index), param);
			if (flow.getDestType() == 7) { // 话题详情页的标志type，客户端识别为0
				flow.setDestType(0);
			}
			flows.add(flow);
		}
	}

	/**
	 * 点赞
	 */
	public String addLike(AdvParam param, String id) {
		String key = "feedAdvLike#" + id;
		
		CacheUtil.incrToCache(key, Constants.LONGEST_CACHE_TIME);
		String keyU = "FeedAdvLikePeople#" + param.getUdid() + "#" + id;
		CacheUtil.setToRedis(keyU, Constants.LONGEST_CACHE_TIME, String.valueOf(1));

		return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

	/**
	 * 取消点赞
	 */
	public String delLike(AdvParam param, String id) {
		String key = "feedAdvLike#" + id;
		CacheUtil.redisIncrBy(key, -1, Constants.LONGEST_CACHE_TIME);

		String keyU = "FeedAdvLikePeople#" + param.getUdid() + "#" + id;
		CacheUtil.redisDelete(keyU);

		return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

	/*
	 * 不感兴趣
	 */
	public String uninterestNewFeeds(AdvParam advParam, String id, int destType) {
		if (destType == 5) { // 广告
			logger.info("feed流广告不感兴趣, id={}", id);
			logger.info(
					"[ENTERuninterestads]: showType={}, advId={}, udid={}, accountId={}, cityId={}, lineId={}, apiType={},"
							+ " provider_id={}, secret={}", "16", id, advParam.getUdid(), advParam.getAccountId(),
					advParam.getCityId(), advParam.getLineId(), 1, -1, null);
			AdvCache.saveNewUninterestedAds(advParam.getUdid(), Integer.parseInt(id), advParam.getLineId(), 16, "1", -1,
					advParam.getS(), advParam.getVc());
			
		} else if (destType == 2) { // 文章
			logger.info("feed流文章不感兴趣, id={}", id);
		}
		AnalysisLog
		.info("[UNINTEREST_FEEDS]: accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, stnName={},nw={},ip={},deviceType={},geo_lng={},geo_lat={}, "
				+ "id={}, destType={}",
				advParam.getAccountId(), advParam.getUdid(),
				advParam.getCityId(), advParam.getS(), advParam.getV(), advParam.getLineId(),
				advParam.getStnName(), advParam.getNw(), advParam.getIp(), advParam.getDeviceType(),
				advParam.getLng(), advParam.getLat(), id, destType);
		
		return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

	private int getIsLikeAds(int id, String udid) {
		String keyU = "FeedAdvLikePeople#" + udid + "#" + id;
		String isLike = (String) CacheUtil.getFromRedis(keyU);
		if (isLike == null)
			return 0;
		else
			return 1;
	}

	private int getLikeNum(int id) {
		String key = "feedAdvLike#" + id;
		String likeNum = (String) CacheUtil.getFromRedis(key);
		if (likeNum == null)
			return 0;
		else
			return Integer.parseInt(likeNum);
	}

	public String getClienSucMap(Object obj, String status) {
		ClientDto clientDto = new ClientDto();
		clientDto.setSuccessObject(obj, status);
		try {
			String json = JSON.toJSONString(clientDto, SerializerFeature.BrowserCompatible);
			return "**YGKJ" + json + "YGKJ##";
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			return "";
		}
	}

	public String getClientErrMap(String errmsg, String status) {
		ClientDto clientDto = new ClientDto();
		clientDto.setErrorObject(errmsg, status);
		try {
			String json = JSON.toJSONString(clientDto);
			return "**YGKJ" + json + "YGKJ##";
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			return "";
		}
	}

	public static void main(String[] args) {
		// System.out.println(StringUtils .isNoneBlank(null));
	}

}
