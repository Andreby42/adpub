package com.bus.chelaile.flow;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.QueueCacheType;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.SynchronizationControl;
import com.bus.chelaile.thread.Queue;
import com.bus.chelaile.thread.model.QueueObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.model.*;
import com.bus.chelaile.flowNew.TbkUtils;
import com.bus.chelaile.util.FlowUtil;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

/**
 * 第三方接口
 * 
 * @author zzz
 *
 */
public class FlowService {

	private static final Logger logger = LoggerFactory.getLogger(FlowService.class);

	@Autowired
	private XishuashuaHelp xishuashuaHelp;
	@Autowired
	private ToutiaoHelp toutiaoHelp;
	@Autowired
	private WuliToutiaoHelp wuliToutiaoHelp;
	@Autowired
	private WangYiYunHelp wangYiYunHelp;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private FlowOcs flowOcs;

	private static final String ucIcon1 = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "ucIcon1",
			"https://image3.chelaile.net.cn/98aeb0cc10f44693aaa6348940cc8582");
	private static final String ucIcon2 = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "ucIcon2",
			"https://image3.chelaile.net.cn/589a5885aa404fe98f4b696753e25177");
	private static final String flowIconNovel = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "flowIconNovel",
			"https://image3.chelaile.net.cn/a332fff49472400c8f8496d0d3f6306a");
	private static final String flowTitle = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "flowTitle",
			"小说免费看，伴你公交行！");

	// private static final int ARTICLE_TOTALSIZE = 15;

	/**
	 * 最初的检测
	 * 
	 * @return true 检测成功,false 检测失败
	 */
	private boolean beforeCheck(AdvParam advParam) {
		if (SynchronizationControl.isReloadUC()) {
			logger.info("reload is Running");
			return false;
		}
		return true;
	}

	/*
	 * 获取信息流服务
	 */
	public FlowResponse getResponse(AdvParam advParam, long ftime, String recoid, int id, int supportNovel) {

		if (!beforeCheck(advParam)) {
			return null;
		}

		ChannelType channelType = activityService.getChannelType(advParam.getUdid(), id); // 频道类型，UC还是toutiao还是自定义模块
		int hasLineArticles = activityService.getHasLineDetailArticles(advParam.getUdid(), supportNovel); // 是否在详情页展示信息流入口
		boolean isNewVersion = false;	//是否是新版
		isNewVersion = activityService.isNewVersion(advParam); // 版本控制

		// 接口拉取文章,获取活动
		List<FlowContent> contents = getArticlesInfoList(advParam, ftime, recoid, id, channelType, supportNovel);
		if (contents == null || contents.size() == 0) { // 获取不到信息流
			if (!isNewVersion) {
				return null;
			} 
			 // 详情页以及自定义频道，如果拉取不到内容，不予直接返回错误，需要告知是否打开入口
			else if ((advParam.getRefer() != null && advParam.getRefer().equals("lineDetail")) || channelType == ChannelType.CUSTOM) {
				FlowResponse flowResponse = new FlowResponse();
				flowResponse.setHasLineArticles(hasLineArticles);
				return flowResponse;
			} else {		// 其他情况，返回空。做错误处理
				return null;
			}
		}

		FlowResponse flowResponse = new FlowResponse();
		flowResponse.setUcArticles(contents);

		// 获取title
		FlowChannel ucChannel = activityService.getChannels(id, channelType);
		if (ucChannel == null) {
			ucChannel = activityService.getChannels(1, channelType); // 默认是1
		}
		flowResponse.setUcTitle(ucChannel.getTitle());

		// 文章单双栏样式
		int articleType = activityService.getArticlesShowType(advParam.getUdid(), advParam.getAccountId(), supportNovel);
		if (articleType == 2) {
			flowResponse.setUcIcon(ucIcon2); // 双栏的图标
		} else {
			flowResponse.setUcIcon(ucIcon1); // 单栏的图标
		}
		if(supportNovel == 1) {	//支持小说的城市，返回特定的icon和文案
			flowResponse.setUcIcon(flowIconNovel);
			flowResponse.setUcTitle(flowTitle);
		}
		flowResponse.setArticleShowType(articleType);
		flowResponse.setHasLineArticles(hasLineArticles);

		// 获取渠道列表信息
		List<Channel> channels = activityService.getChannels(advParam);
		if (isNewVersion) {
			List<Channel> favChannels = getFavChannels(advParam, channelType);
			if (favChannels == null || favChannels.size() == 0) {
				favChannels = New.arrayList();
				favChannels.addAll(activityService.getDefaultFavChannels());
			}
			List<Channel> otherChannels = getOtherChannels(channels, favChannels);
			flowResponse.setFavChannels(favChannels);
			flowResponse.setOtherChannels(otherChannels);
		} else {
			flowResponse.setChannels(channels);
		}

		return flowResponse;
	}

	/**
	 * 获得信息流列表
	 * 
	 * @param channelId
	 * @param ftime
	 */
	private List<FlowContent> getArticlesInfoList(AdvParam advParam, long ftime, String recoid, int id,
			ChannelType channelType, int supportNovel) {
		if (!activityService.isReturnUC(advParam.getUdid(), advParam.getRefer(), supportNovel)) { // 判断是否投放信息流
			return null;
		}

		List<FlowContent> contents;
		try {
			// 获取信息流 活动
			HashMap<Integer, FlowContent> contentActivity = activityService.getActivity(advParam, id, channelType);

//			// 获取信息流 广告
//			HashMap<Integer, FlowContent> contentAdv = activityService.getAdv(advParam, id, channelType);
			
//			HashMap<Integer, FlowContent> contentThirdAdv = TanxHelper.getAdv(advParam, id, channelType);

			// 先取得top列表
			// List<UcContent> contentsFromOCSTop = getTopInfo(advParam);
			// List<FlowContent> contentsFromOCSTop = null;

			// 得到最新内容
			List<FlowContent> contentsFromApi = null;
			if (channelType == ChannelType.TOUTIAO) {
//				return null;	// FOR TEST
				contentsFromApi = toutiaoHelp.getInfoByApi(advParam, ftime, recoid, id, false);
			} else if (channelType == ChannelType.UC) {
				contentsFromApi = xishuashuaHelp.getInfoByApi(advParam, ftime, recoid, id, false);
			} else if(channelType == ChannelType.WULITOUTIAO) {
				contentsFromApi = wuliToutiaoHelp.getArticlesFromCache(advParam, recoid, id);
			} else if(channelType == ChannelType.WANGYI) {
				contentsFromApi = wangYiYunHelp.getInfoByApi(advParam, ftime, recoid, -1, false);
			}
			contents = flowOcs.merageList(null, contentsFromApi, contentActivity, null, advParam.getUdid(), channelType);
//			FlowUtil.setImagsType(contents);
			
			// 粗暴的取一条
			// tbk TODO 
//			FlowContent flow = TbkUtils.getTbkContent();
//			if(flow != null) {
//				contents.add(0, flow);;
//			}
			
			return contents;

			// // 得到用户不需要展示的id
			// List<String> blockIds = getNotDisplayIds(advParam);
			// // List<String> blockIds = null;
			// // 过滤掉不要展示的文章列表
			// filterTopList(contentsFromOCSTop, contentsFromApi, blockIds);
			// // 合并
			// contents = merageList(contentsFromOCSTop,
			// contentsFromApi,contentActivity, contentAdv);
			// // 把给用户展示的列表放入队列
			// if (contents != null) {
			// setToQueue(contents, advParam);
			// } else {
			// logger.error("信息流返回为空! udid={}", advParam.getUdid());
			// return null;
			// }
		} catch (Exception e) {
			logger.error("信息流出错, udid={}, s={}", advParam.getUdid(), advParam.getS());
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			return null;
		}

	}



	/*
	 * 保存不感兴趣
	 */
	public String uninterest(AdvParam param, String articleId, int articleType) {
		logger.info("[ENTERuninterestads]: articleId={}, articleType={}, udid={}, accountId={}, cityId={}, lineId={}",
				articleId, articleType, param.getUdid(), param.getAccountId(), param.getCityId(), param.getLineId());

		// 该文章加入黑名单中
		HashSet<String> curUserBlacklist = new HashSet<>();
		curUserBlacklist.add(articleId);

		ListIdsCache blockIds = new ListIdsCache(); // 之前不予展示的id
		String blockStr = (String) CacheUtil.getNew(AdvCache.getUserBlockContentIds(param.getUdid()));
		if (blockStr != null) {
			blockIds = JSON.parseObject(blockStr, ListIdsCache.class);
		}
		curUserBlacklist.addAll(blockIds.getIdList());
		blockIds.setIdList(new ArrayList<String>(curUserBlacklist));

		logger.info("不感兴趣的文章加入黑名单：blockIds={}", JSONObject.toJSON(blockIds));

		// 缓存用户看过的 文章黑名单 ids
		QueueObject objIds = new QueueObject();
		objIds.setKey(AdvCache.getUserBlockContentIds(param.getUdid())); // BLOCK#
		objIds.setTime(Constants.ONE_DAY_TIME);
		objIds.setArticleIds(blockIds);
		objIds.setQueueType(QueueCacheType.DISPLAY_IDS);
		Queue.set(objIds);

		return null;
	}

	/*
	 * 获取收藏的频道
	 */
	private List<Channel> getFavChannels(AdvParam advParam, ChannelType channelType) {
		if (StringUtils.isBlank(advParam.getUdid())) {
			return null;
		}

		// accountId 不为空的情况，如果为空，接下来看udid
		if (StringUtils.isNoneBlank(advParam.getAccountId())) {
			String key = "article_fav_" + advParam.getAccountId();
			String favStr = (String) CacheUtil.getNew(key);
			// accountId存在收藏，直接返回， 如果不存在，接下来继续看udid
			if (favStr != null) {
				ArrayList<String> favChannelIds = new ArrayList<>(Arrays.asList(favStr.split(",")));
				logger.info("get favArticles from ocs by accountId : udid={}, accountId={}, favStr={}",
						advParam.getUdid(), advParam.getAccountId(), favStr);
				return idsToChannels(favChannelIds, channelType, advParam);
			}
		}

		// 1.udid存在收藏，name返回udid，并且更新accountid
		// 2.udid不存在收藏，返回空
		String key = "article_fav_" + advParam.getUdid();
		String favStr = (String) CacheUtil.getNew(key);
		if (favStr != null) {
			ArrayList<String> favChannelIds = new ArrayList<>(Arrays.asList(favStr.split(",")));
			logger.info("get favArticles from ocs by udid: udid={}, accountId={}, favStr={}", advParam.getUdid(),
					advParam.getAccountId(), favStr);
			if (StringUtils.isNoneBlank(advParam.getAccountId())) {
				updateArticleChannels(favStr, advParam);
			}
			return idsToChannels(favChannelIds, channelType, advParam);
		}

		return null;
	}

	/*
	 * 用所有频道，减去收藏的频道，得到other频道
	 */
	private List<Channel> getOtherChannels(List<Channel> channels, List<Channel> favChannels) {
		if (channels != null) {
			if (favChannels != null) {
				channels.removeAll(favChannels);
			}
			return channels;
		}
		return null;
	}

	/**
	 * 更新 收藏，accountid优先。更新功能不负责同步udid与accoutnId之间的关系
	 * 
	 * @param favArticleIds
	 * @param advParam
	 * @return
	 */
	public boolean updateArticleChannels(String favArticleIds, AdvParam advParam) {
		if (StringUtils.isBlank(advParam.getUdid())) {
			return false;
		}

		String key = "article_fav_" + advParam.getUdid();
		if (StringUtils.isNoneBlank(advParam.getAccountId())) {
			key = "article_fav_" + advParam.getAccountId();
		}

		try {
			if (StringUtils.isBlank(favArticleIds)) {
				CacheUtil.deleteNew(key);
				logger.info("udid={},accountId={}, delete articleChannels", advParam.getUdid(), advParam.getAccountId());
			} else {
				CacheUtil.setNew(key, -1, favArticleIds);
				logger.info("set favArticles to ocs, udid={},accountId={},favStr={}, update articleChannels success",
						advParam.getUdid(), advParam.getAccountId(), favArticleIds);
			}
			return true;
		} catch (Exception e) {
			logger.error("udid={}, accountId={}, update articleChannels exception", advParam.getUdid(),
					advParam.getAccountId());
			return false;
		}
	}

	/**
	 * 根据 渠道 id，获取频道id和频道name
	 * 
	 * @param favChannelIds
	 * @return
	 */
	private List<Channel> idsToChannels(ArrayList<String> favChannelIds, ChannelType channelType, AdvParam advParam) {
		List<Channel> channels = New.arrayList();
		if (favChannelIds == null)
			return null;

		// android某个版本出现bug，导致部分用户将本该补丁的‘推荐’频道删掉了，所以这里做一下处理。
		if( !favChannelIds.get(0).equals("1")) {
			favChannelIds.remove("1");
			favChannelIds.remove("");
			favChannelIds.add(0, "1");
			
			String favStr = "";
			for(String s : favChannelIds) {
				favStr += s + ",";
			}
			favStr = favStr.substring(0, favStr.length() -1);
			updateArticleChannels(favStr, advParam);
		}
		
//		for (String s : favChannelIds) {
//			try {
//				int id = Integer.parseInt(s);
//				Channel channel = null;
//				if(ActivityService.CHANNELS.get(ChannelType.TOUTIAO.getType()).containsKey(id)){
//					channel = new Channel(id, ActivityService.CHANNELS.get(ChannelType.TOUTIAO.getType()).get(id).getName());
//				} else {
//					channel = new Channel(id, ActivityService.CHANNELS.get(ChannelType.CUSTOM.getType()).get(id).getName());
//				}
//				channels.add(channel);
//			} catch (Exception e) {
//				logger.error("用户uc收藏出现错误, favStr={}", s);
//				favChannelIds.remove(s);
//				updateArticleChannels(FlowUtil.changeArrayTOString(favChannelIds), advParam);
//				e.printStackTrace();
//				continue;
//			}
//		}
		
		Iterator<String> favIter = favChannelIds.iterator();
		while(favIter.hasNext()) {
			String s = favIter.next();
			try {
				int id = Integer.parseInt(s);
				Channel channel = null;
				if(ActivityService.CHANNELS.get(ChannelType.TOUTIAO.getType()).containsKey(id)){
					channel = new Channel(id, ActivityService.CHANNELS.get(ChannelType.TOUTIAO.getType()).get(id).getName());
				} else {
					channel = new Channel(id, ActivityService.CHANNELS.get(ChannelType.CUSTOM.getType()).get(id).getName());
				}
				channels.add(channel);
			} catch (Exception e) {
				logger.error("用户uc收藏出现错误, favStr={}", s);
				favIter.remove();	// 删除出错的频道，很多时候是这个频道已经下线了。或者存储的时候出错
				e.printStackTrace();
				continue;
			}
		}
		updateArticleChannels(FlowUtil.changeArrayTOString(favChannelIds), advParam);
		
		
		return channels;
	}
	
	/**
	 * 获取tab页弹窗位内容
	 * @param param
	 * @return
	 */
	public TabEntity getTabActivities(AdvParam param, int entryId) {
		List<TabEntity> tabs = activityService.getTabActivities(param, entryId + 3); // 接口entryid 0:tab3， 数据库 3:tab3
		
		if(tabs == null || tabs.size() == 0) {
			return null;
		}
		for (TabEntity tabEntity : tabs) {
			if (flowOcs.checkTabActivities(tabEntity, param)) {
				return tabEntity;
			} 
		}
		return null;
		
//		return testGetTabActivities(param);
	}
	
	
	/**
	 * 获取tab页弹窗位内容
	 * @param param
	 * @return
	 */
	public TabEntity testGetTabActivities(AdvParam param) {
		//  TODO  Tab点击弹窗
			TabEntity tabAdEntity1 = new TabEntity();
			tabAdEntity1.setId(1);
			tabAdEntity1.setPic("https://image3.chelaile.net.cn/5fd5ee4d009344649fc7756addf92e96");
			tabAdEntity1.setLink("http://www.chelaile.net.cn");
			tabAdEntity1.setActivityType(1);
			logger.info("返回tab弹窗信息={}", JSONObject.toJSONString(tabAdEntity1));
			
			TabEntity tabAdEntity2 = new TabEntity();
			tabAdEntity2.setId(3);
			tabAdEntity2.setTagId(81);
			tabAdEntity2.setTag("公交小事");
			tabAdEntity2.setPic("https://image3.chelaile.net.cn/5fd5ee4d009344649fc7756addf92e96");
//			tabAdEntity2.setLink("https://image3.chelaile.net.cn/5fd5ee4d009344649fc7756addf92e96");
			tabAdEntity2.setActivityType(3);
			logger.info("返回tab弹窗信息={}", JSONObject.toJSONString(tabAdEntity2));
			
//			TabEntity tabAdEntity3 = new TabEntity();
//			tabAdEntity3.setId(4);
//			tabAdEntity3.setPic("");
//			tabAdEntity3.setLink("https://image3.chelaile.net.cn/5fd5ee4d009344649fc7756addf92e96");
//			tabAdEntity3.setActivityType(4);
//			logger.info("返回tab弹窗信息={}", JSONObject.toJSONString(tabAdEntity3));
			
			TabEntity tabAdEntity4 = new TabEntity();
			tabAdEntity4.setId(5);
			tabAdEntity4.setPic("https://image3.chelaile.net.cn/5fd5ee4d009344649fc7756addf92e96");
//			tabAdEntity4.setLink("https://image3.chelaile.net.cn/5fd5ee4d009344649fc7756addf92e96");
			tabAdEntity4.setActivityType(5);
			logger.info("返回tab弹窗信息={}", JSONObject.toJSONString(tabAdEntity4));
			
			TabEntity tabAdEntity5 = new TabEntity();
			tabAdEntity5.setId(6);
			tabAdEntity5.setPic("https://image3.chelaile.net.cn/5fd5ee4d009344649fc7756addf92e96");
			tabAdEntity5.setLink("https://activity.m.duiba.com.cn/newtools/index?id=2521608");
			tabAdEntity5.setActivityType(6);
			logger.info("返回tab弹窗信息={}", JSONObject.toJSONString(tabAdEntity5));
			
			TabEntity tabAdEntity6 = new TabEntity();
			tabAdEntity6.setId(7);
			tabAdEntity6.setPic("https://image3.chelaile.net.cn/5fd5ee4d009344649fc7756addf92e96");
			tabAdEntity6.setActivityType(7);
			tabAdEntity6.setFeedId("627093497618468864");
			logger.info("返回tab弹窗信息={}", JSONObject.toJSONString(tabAdEntity6));
			
			if(param.getCityId().equals("027"))
				return tabAdEntity1;
			else if(param.getCityId().equals("006"))
				return tabAdEntity2;
//			else if(param.getCityId().equals("019"))
//				return tabAdEntity3;
			else if(param.getCityId().equals("004"))
				return tabAdEntity4;
			else if(param.getCityId().equals("007"))
				return tabAdEntity5;
			else
				return tabAdEntity6;
	}
	

	public static void main(String[] args) {
		
		String favStr = ",2,3";
		ArrayList<String> favChannelIds = new ArrayList<>(Arrays.asList(favStr.split(",")));
		System.out.println(favChannelIds);
		
		if(!favChannelIds.get(0).equals("1")) {
			favChannelIds.remove("1");
			favChannelIds.remove("");
			favChannelIds.add(0, "1");
		}
		System.out.println(favChannelIds);
		System.out.println(JSONObject.toJSONString(favChannelIds));
		String favStr1 = "";
		for(String s : favChannelIds) {
			favStr1 += s + ",";
		}
		favStr1 = favStr1.substring(0, favStr1.length() -1);
		System.out.println(favStr1);
		
		
		System.out.println("转换后字符串是：" + FlowUtil.changeArrayTOString(favChannelIds));
//		Random random = new Random();
//		for (int i = 0; i < 100; i++) {
//			System.out.println(random.nextInt(2));
//		}
	}
}
