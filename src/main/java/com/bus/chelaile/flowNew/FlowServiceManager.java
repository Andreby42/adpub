package com.bus.chelaile.flowNew;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.dao.ActivityContentMapper;
import com.bus.chelaile.flow.ActivityService;
import com.bus.chelaile.flow.FlowService;
import com.bus.chelaile.flow.ToutiaoHelp;
import com.bus.chelaile.flow.WangYiYunHelp;
import com.bus.chelaile.flow.WuliToutiaoHelp;
import com.bus.chelaile.flow.model.ChannelType;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flowNew.model.FlowNewContent;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.client.ClientDto;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public class FlowServiceManager {

	@Autowired
	private ActivityContentMapper activityContentMapper;
//	@Autowired
//	private PayService payService;
//	@Autowired
//	private ToutiaoHelp toutiaoHelp;
//	@Autowired
//	private WangYiYunHelp wangYiYunHelp;
	@Autowired
	private ActivityService activityService;
//	@Autowired
//	private WuliToutiaoHelp wuliToutiaoHelp;
	@Resource
	private FlowService flowService;
	
	private static final String HEADSTRS = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"detail.new.flow.udidHEADS", "4|5|6|7");
	
	public static final List<String> HEADLIST = New.arrayList();
	protected static final Logger logger = LoggerFactory.getLogger(FlowServiceManager.class);
	/**
	 * 初始化一些需要提前录入缓存的内容 除了‘热门文章’，其他都需要放入缓存中
	 */
	public void initFlows() {
		// 详情页投放用户的处理
		HEADLIST.clear();
		for(String s : HEADSTRS.split("\\|")) {
			HEADLIST.add(s);
		}
		
		// 文章相关初始化
//		FlowStaticContents.initArticleContents();
//		FlowStaticContents.FAKE_PHOTOS = new ArrayList<>(CacheUtil.getWowDatas("BUSUGC_FAKE_PHOTO"));	  // 用户头像set，直接构造list
		
		// 缓存从数据库拉取的‘进行中’的活动
		FlowStaticContents.activityContens.clear();
		FlowStaticContents.activityContens.addAll(activityContentMapper.listOnlineActivity());
		logger.info("feeds，初始化，得到活动数目为：{}", FlowStaticContents.activityContens.size());
		FlowStartService.initLineDetailFlows(FlowStaticContents.activityContens);
	}
	
	public String getResponseLineDetailFlows(AdvParam advParam) {
//		 乘车码banner入口
//		PayInfo payInfo = payService.getPayInfo(param);
//		if (payInfo != null) {
//			JSONObject responseJ = new JSONObject();
//			responseJ.put("payInfo", payInfo);
//			return getClienSucMap(responseJ, Constants.STATUS_REQUEST_SUCCESS);
//		}
		
		// 单栏信息流
		List<FlowContent> contentsFromApi = null;
		ChannelType channelType = activityService.getChannelType(advParam.getUdid(), -1);
		if (FlowStaticContents.isReturnLineDetailFlows(advParam)) {
			try {
				long ftime = 0L; String recoid = null;
				contentsFromApi = flowService.getApiContent(advParam, ftime, recoid, -1, channelType, contentsFromApi);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (contentsFromApi != null && contentsFromApi.size() > 0) {
			List<FlowNewContent> flows = New.arrayList();
			for (FlowContent f : contentsFromApi) {
				flows.add(f.createNewContents());
			}
			// 糅和进去话题到指定位置
			addTagsIntoFlows(flows, advParam); // 在第1、5、10放入话题
			
			JSONObject responseJ = new JSONObject();
			responseJ.put("flows", flows);
			return getClienSucMap(responseJ, Constants.STATUS_REQUEST_SUCCESS);
		} else {
			return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
		}
	}

	private void addTagsIntoFlows(List<FlowNewContent> flows, AdvParam advParam) {
		List<FlowNewContent> flowTag = New.arrayList();
		String key;
		String lineFlowsStr ;
		key = "QM_LINEDETAIL_FLOW_" + 0;	// 话题标签
		lineFlowsStr = (String) CacheUtil.getNew(key);
		if (StringUtils.isNoneBlank(lineFlowsStr)) {
			flowTag = JSON.parseArray(lineFlowsStr, FlowNewContent.class);
		}
		if(flowTag.size() > 0) {
			for(int index = 0; index < flowTag.size(); index ++) {
				if(flows.size() > (index * 4 + 5))
					flows.add(index * 4 + 4, flowTag.get(index));
			}
		}
		
		// 能量馆首页
		FlowNewContent flowEnergy = new FlowNewContent(4, "能量馆", "去能量馆充能",
				"https://image3.chelaile.net.cn/e8817ef0255649e8af326365d994329d", "52,152,219", null, null, null,
				null, null);
		FlowNewContent flowEnergyNew = new FlowNewContent(4, "福利社", "今天的福利你领了吗？",
				"https://image3.chelaile.net.cn/02fb259f5a5f421581edef7d38a6d7ab", "255,87,34", null, null, null,
				null, null);
		
		Platform platform = Platform.from(advParam.getS());
		if ((platform.isAndriod(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_ANDROID_0208)
				|| (platform.isIOS(platform.getDisplay()) && advParam.getVc() < Constants.PLATFORM_LOG_IOS_0208)) {
			flows.add(0, flowEnergy);
		} else {
			// 新版用这个
			flows.add(0, flowEnergyNew);
		}
	}


//	public String getResponseArticleList(AdvParam param, String channelId, String articleId) {
//		List<ArticleContent> articles = null;
//		try{
//			articles = getArticleList(param, channelId, articleId);
//		} catch(Exception e) {
//			e.printStackTrace();
//			return getClientErrMap("服务器错误", Constants.STATUS_INTERNAL_ERROR);
//		}
//		
//		if (articles != null && articles.size() > 0) {
//			JSONObject responseJ = new JSONObject();
//			int readNumber = getPersonArticleReadNum(param.getAccountId(), channelId); // 该用户阅读过几篇文章
//			int hasShared = getPersonArticleShare(param.getAccountId(), channelId); //  该用户是否分享过
//
//			responseJ.put("articles", articles);
//			responseJ.put("readNumber", readNumber);
//			responseJ.put("hasShared", hasShared);
//			return getClienSucMap(responseJ, Constants.STATUS_REQUEST_SUCCESS);
//		} else {
//			return getClienSucMap(new JSONObject(), Constants.STATUS_NO_ARTICLES);
//		}
//	}

//	/*
//	 * 获取文章列表 从缓存获取
//	 */
//	private List<ArticleContent> getArticleList(AdvParam param, String channelId, String articleId) {
//		// 获取给用户的第一篇文章的no
//		// 需要与缓存中的第一篇文章做对比，不能小于第一篇文章no，也不能大于最后一篇文章no 	
//		// 如果大于最后一篇，那么返回‘没有更多文章了’，如果小于第一篇，那么返回缓存中的第一篇
//		
//		// 2.0版，缓存全部存入ocs中，所以无需从‘缓存最小文章’开始，直接从1开始即可~
//		int articlePersonNo = getNoFromArticleId(articleId, param.getAccountId(), channelId);
//		
//		if(StringUtils.isBlank(channelId)) {
//			logger.error("没有频道id , acId={}", param.getAccountId());
//			return null;
//		}
//		List<ArticleContent> articleList = FlowStaticContents.getArticleList(channelId, articlePersonNo, param);
//		
////		for(ArticleContent arContent : articleList) {
////			// 针对用户，做一些去重之类的处理
////		}
//		return articleList;
//	}

//	/*
//	 * articleId=
//	 * 
//	 */
//	private int getNoFromArticleId(String articleId, String acId, String channelId) {
//		logger.info("articleId={}", articleId);
//		String date = DateUtil.getTodayStr("yyyy-MM-dd");
//		int articleLastNo = FlowStaticContents.getArticleNo(date + "#" + channelId);
//		
//		// articleId为空，从杂志外面进入列表
//		if (StringUtils.isBlank(articleId)) {
//			String articlePersonNoStr = (String) CacheUtil.getNew("QM_last_person_articleId_" + date + "_" + acId + "_" + channelId);
//			// 用户当天有缓存记录
//			if (StringUtils.isNoneBlank(articlePersonNoStr)) {
//				logger.info("轻芒文章， 获取到用户的上次记录的 channelId={}, aritlceNo={}, 缓存最后一篇文章no={}", channelId, articlePersonNoStr, articleLastNo);
//				int arPersonNo = Integer.parseInt(articlePersonNoStr);
//				if (arPersonNo + 2 >= articleLastNo) { // 从杂志外进入，即使记录no大于最大的no，也需要保证返回给用户内容。
//					return 0;
//				}
//				return arPersonNo;
//			} else {
//				logger.info("轻芒文章， 获取不到用户的上次记录的aritlceNo, 直接从第一篇开始");
//				return 0;
//			}
//		} else {
//			try {
//				// 文章id格式是：文章原始id_自定义数字id
//				String noStr = articleId.split("_")[1];
//				logger.info("用户起始文章channelId={}, no为：{}, 缓存最后一篇文章no={} ",channelId, noStr, articleLastNo);
//				int arPersonNo = Integer.parseInt(noStr);
////				if (arPersonNo + 2 >= articleLastNo) {
////					//  此处让用户从第一篇开始，好处在于：
////					// 1.用户可能没有开最开始的，
////					// 2.后续会做排重，用户如果真的看完了，最后依旧不会返回文章，状态set为11
////					logger.info("文章刷完了~，让用户从第一篇重新开始");
////					return 0;
////				}
//				return arPersonNo;
//			} catch (Exception e) {
//				return 0;
//			}
//		}
//	}

	
	/**
	 * 获取阅读过杂志的人数
	 * @param param
	 * @param channelId
	 * @param articleId
	 * @return
	 */
	public String getChannelReadNumResponse(AdvParam param, String channelId) {
		String key = "QM_CHANNEL_CLICK_" + channelId;
		int value = getRecordValue(key);
		JSONObject j = new JSONObject();
		j.put("readNum", value);
		return getClienSucMap(j, Constants.STATUS_REQUEST_SUCCESS);
	}

	/*
	 * 记录杂志点击
	 */
	public String recordChannleClick(AdvParam param, String channelId) {
		String key = "QM_CHANNEL_CLICK_" + channelId;
		recordAddOne(key, 1);
		return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}
	
	/*
	 * 记录杂志分享
	 */
	public String recordPersonChannleShared(AdvParam param, String channelId) {
		String key = "QM_SHARED_" + param.getAccountId() + "_" + channelId;
		recordAddOne(key, 1);
		return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

	/*
	 * 记录文章点击
	 */
	public String recordArticleClick(AdvParam param, String channelId, String articleId) {
		// 文章阅读数 +5
		String key = "QM_ARTICLE_CLICK_" + channelId + "_" + articleId;
		recordAddOne(key, 5);

		// 用户阅读文章数 +1
		String keyPerson = "QM_PERSON_ARTICLE_READ_" + param.getAccountId() + "_" + channelId;
		recordAddOne(keyPerson, 1);
		
		return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}
	
//	// 用户是否分享过文章
//	private int getPersonArticleShare(String acId, String channelId) {
//		String key = "QM_SHARED_" + acId + "_" + channelId;
//		int value = getRecordValue(key);
//		if(value == 0) {
//			return 0;
//		}
//		return 1;
//	}
//
//	// 用户阅读过的文章数目
//	private int getPersonArticleReadNum(String acId, String channelId) {
//		String keyPerson = "QM_PERSON_ARTICLE_READ_" + acId + "_" + channelId;
//		return getRecordValue(keyPerson);
//	}

	private void recordAddOne(String key, int num) {
		int valueOld = getRecordValue(key);
		CacheUtil.setNew(key, Constants.LONGEST_CACHE_TIME, String.valueOf(valueOld + num));
	}
	
	private int getRecordValue(String key) {
		String valueStr = (String)CacheUtil.getNew(key);
		int value = 0;
		if(valueStr != null) {
			value = Integer.parseInt(valueStr);
		}
		return value;
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
		// System.out.println(getClienSucMap(new JSONObject(),
		// Constants.STATUS_REQUEST_SUCCESS));
		String key = "dafda" + 1;
		System.out.println(key);
	}
}
