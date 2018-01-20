package com.bus.chelaile.flow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.model.ChannelType;
import com.bus.chelaile.flow.model.FlowChannel;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.wulitoutiao.WuliImage;
import com.bus.chelaile.flow.wulitoutiao.WuliResponse;
import com.bus.chelaile.flow.wulitoutiao.WuliResponseData;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public class WuliToutiaoHelp implements InterfaceFlowHelp {
	@Autowired
	private ActivityService activityService;
	private static final Logger logger = LoggerFactory.getLogger(WuliToutiaoHelp.class);
	
	private static final String DEFAULT_CHANNEL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"wuli.default.channel", "693b2246e514163143827e02073c99ba");
	private static final int DEFAULT_CHANNEL_ID = 202;
	private static final int ARTICLE_NUM = 10;
	private static final String requestUrl = PropertiesUtils
			.getValue(
					PropertiesName.PUBLIC.getValue(),
					"wulitoutiao.request.url",
					"http://api.9wuli.com/v3/message/list?appId=7dd1fd0f4b7aceb31f46e9cb33d40266&appSecret=b28da70602af2a1eb550638db89a3f6e&channelId=%s");

	@Override
	public List<FlowContent> getInfoByApi(AdvParam advParam, long ftime, String recoid, int id, boolean isShowAd) throws Exception {

		// 读取channelId，调用每个channel
		FlowChannel wuliChannel = activityService.getChannels(id, ChannelType.WULITOUTIAO);
		List<String> apiChannelIds = parseChannel(wuliChannel);

		if (id == -1 || apiChannelIds == null || apiChannelIds.size() == 0) {
//			logger.info("没有读取到有效的channelIds，用默认的推荐频道");
			return parseResponse(advParam, ftime, recoid, null, DEFAULT_CHANNEL, true); // 默认 热点频道
		} else {
			int contentNumber = 21 / apiChannelIds.size();
			List<FlowContent> wuliContents = new ArrayList<>();
			for (String chennelId : apiChannelIds) {
				List<FlowContent> reponseWuliContent = parseResponse(advParam, ftime, recoid, null, chennelId, true);
				if (reponseWuliContent != null && reponseWuliContent.size() > 0) {
					wuliContents
							.addAll(reponseWuliContent.subList(0,
									contentNumber >= reponseWuliContent.size() ? reponseWuliContent.size() - 1
											: contentNumber));
				} else {
					logger.error("出现频道返回为空的情况！ channelId={}", chennelId);
				}
			}
			AnalysisLog
					.info("[GET_UC_ARTICLES]: accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, nw={},ip={},deviceType={},geo_lng={},geo_lat={},stats_act={},channelId={},refer={}",
							advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(),
							advParam.getV(), advParam.getLineId(), advParam.getNw(), advParam.getIp(),
							advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getStatsAct(), id,
							advParam.getRefer());

			return wuliContents;
		}
	}
	

	@Override
	public List<String> parseChannel(FlowChannel ucChannel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FlowContent> parseResponse(AdvParam advParam, long ftime, String recoid, String token, String channelId, boolean isShowAd) {
		List<FlowContent> contents = New.arrayList();
		String url = null;
		String response = null;
		url = String.format(requestUrl, channelId);
		
		try {
			logger.info("唔哩头条接口url={}", url);
			response = HttpUtils.get(url, "UTF-8");
			System.out.println("唔哩头条 response = " + response);
			logger.info("唔哩头条 response = {}", response);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if (response == null) {
			logger.error("唔哩头条返回为空： url={}, response= {}", url, response);
			return null;
		}

		WuliResponse wuliRes = JSON.parseObject(response, WuliResponse.class);
		if (!wuliRes.getCode().equals("10000") || wuliRes.getData() == null) {
			logger.error("唔哩头条返回内容异常： url={}, response= {}", url, response);
			return null;
		}
		for (WuliResponseData wuliData : wuliRes.getData()) {
			List<WuliImage> images = wuliData.getImageDTOList();
			if(images == null || images.size() == 0) {
				continue;
			} // TODO 考虑是否是动图

			try {
				FlowContent content = wuliData.dealDate(channelId, images.get(0));
				contents.add(content);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return contents;
	}
	
	
	/**
	 * 拉取文章，放入缓存中
	 */
	public void setSortArticlesToCache() {
		try {
			// 目前只缓存一个频道的数据 ，默认频道~ 
			String key = AdvCache.getWuliArticleCacheKey(DEFAULT_CHANNEL_ID);	// 数据库里面默认频道的id
			List<FlowContent> flows = getInfoByApi(null, 0L, null, -1, false);
			logger.info("wulitoutiao:flows={}", JSONObject.toJSON(flows));
			if (flows != null) {
				for(FlowContent f : flows) {
//					CacheUtil.setSortedSet(key, System.currentTimeMillis(), JSONObject.toJSONString(f), Constants.ONE_DAY_TIME);
//					Thread.sleep(2);
					CacheUtil.setSortedSet(key, Long.parseLong(f.getRecoid()), JSONObject.toJSONString(f), Constants.ONE_DAY_TIME);
				}
			}
		} catch (Exception e) {
			logger.error("getArticle error ={}", e, e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 从缓存获取文章内容
	 * @return
	 */
	public List<FlowContent> getArticlesFromCache(AdvParam advParam) {
		List<FlowContent> flows = new ArrayList<>();
		Set<String> flowStrs = null;
		
		String udidKey = AdvCache.getWuliUdidCacheKey(advParam.getUdid());
		String udidRecoid = (String) CacheUtil.get(udidKey);
		Long recoidid = 0L;
		if(StringUtils.isNotBlank(udidRecoid)) {
			recoidid = Long.parseLong(udidRecoid);
		}
		
		
		// 1.0版，第一次进入，从缓存的第一篇的文章，获取历史的内容
		// 不是第一次进入，根据ftime，获取ftime时间戳之前的内容
		String setKey = AdvCache.getWuliArticleCacheKey(DEFAULT_CHANNEL_ID);
//		if(StringUtils.isBlank(recoid)) {
//			flowStrs = CacheUtil.getRangeSet(setKey, recoidid, System.currentTimeMillis(), ARTICLE_NUM);
//		} else {
//			String act = advParam.getStatsAct();
//			if(StringUtils.isNoneBlank(act) && act.equals("article_refresh")) {  // 按刷新按钮，获取新内容
//				flowStrs = CacheUtil.getRangeSet(setKey, Long.parseLong(recoid) + 1, System.currentTimeMillis(), ARTICLE_NUM);
//			} 
//			else {
			flowStrs = CacheUtil.getRangeSet(setKey, recoidid + 1 , System.currentTimeMillis(), ARTICLE_NUM);
				// -1  是为了上一次最后一篇跟这次一次第一篇重复
//				flowStrs = CacheUtil.getRevRangeSet(setKey, 0d, Long.parseLong(recoid) -1 , ARTICLE_NUM);
//			}
//		}
		
		logger.info("wulitoutiao: recoidid={}, udid={}", recoidid, advParam.getUdid());
//		logger.info("缓存文章列表：setKey={},value={}", setKey, JSONObject.toJSONString(flowStrs));
		if(flowStrs != null) {
			for(String s : flowStrs) {
				FlowContent f = JSONObject.parseObject(s, FlowContent.class);
				flows.add(f);
				recoidid = Long.parseLong(f.getRecoid());
			}
		}
		CacheUtil.set(udidKey, Constants.LONGEST_CACHE_TIME, String.valueOf(recoidid));
		return flows;
	}
	
	
	
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String url = "http://api.9wuli.com/v3/message/list?appId=7dd1fd0f4b7aceb31f46e9cb33d40266&appSecret=b28da70602af2a1eb550638db89a3f6e&channelId=693b2246e514163143827e02073c99ba";
		
		String response = HttpUtils.get(url, "UTF-8");
		System.out.println("唔哩头条 response = " + response);
		
	}

}
