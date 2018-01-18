/**
 * @author quekunkun
 *
 */
package com.bus.chelaile.flowNew.customContent;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flowNew.FlowStartService;
import com.bus.chelaile.flowNew.model.FlowNewContent;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.config.PropertiesUtils;

public class TagUtils {
	
	protected static final Logger logger = LoggerFactory.getLogger(TagUtils.class);
	private static final String TAGS＿LINK = "http://api.chelaile.net.cn:7000/feed/native!tags.action?cityId=027";   // TODO　城市有用否？
	private static final String FEED_LINK_TEST = "https://dev.chelaile.net.cn/feed/native!feeds.action?feedCityId=-1";
	private static final String FEED_LINK_ONLINE = "https://api.chelaile.net.cn/feed/native!feeds.action?feedCityId=-1";
	private static final String FEED_LIST_URL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"feed.list.url", "https://dev.chelaile.net.cn/feed/native!feeds.action?refer=lineDetail&fid=%s&psize=%d");
	private static final String FEED_DETAIL_URL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"feed.detail.url", "https://dev.chelaile.net.cn/feed/native!getFeed.action?fid=%s&accountId=%s");
	/**
	 * 获取话题标签
	 * @param init
	 */
	public static void getInitTags(List<FlowNewContent> initFlows) {
		String url = TAGS＿LINK;
		String response = null;
		try{
			response = HttpUtils.get(url, "utf-8");
		} catch(Exception e) {
			logger.error("拉取tag列表出错， url={}, response={}", url, response);
			return;
		}
		
		if(response != null && response.length() > 12) {
			String resJ = response.substring(6, response.length() - 6);
//			System.out.println(resJ);
			JSONObject res = JSONObject.parseObject(resJ);
			JSONArray tags = res.getJSONObject("jsonr").getJSONObject("data").getJSONArray("tags");
			for(int i = 0; i < tags.size() && i < FlowStartService.LINEDETAIL_NUM; i++ ) {		 // TODO　随机取3条，获取的随机方法有待完善
				createTagFlow(initFlows, (JSONObject) tags.get(i));
			}
			logger.info("详情页下方滚动栏，话题标签数为：{}", tags.size());
		}
	}
	
	private static void createTagFlow(List<FlowNewContent> initFlows, JSONObject object) {
		FlowNewContent f = new FlowNewContent();
		f.setDestType(0);
		f.setFlowTitle(object.getString("tag"));
		f.setFlowTag("热门话题");
		f.setFlowTagColor("255,175,0");
		f.setFlowIcon("https://image3.chelaile.net.cn/4c860c68c14d468b90f29974f036bf96");
		f.setTag(object.getString("tag"));
		f.setTagId(String.valueOf(object.getIntValue("tagId")));
		f.setFeedId("633123838082781184");
		int random = (int) (1000 + Math.random() * 1000);
		f.setFlowDesc(random + "人参与");
		f.setPic("https://image3.chelaile.net.cn/4c860c68c14d468b90f29974f036bf96");
		
		initFlows.add(f);
	}
	
	
	
	/**
	 * 获取话题详情页
	 * @param init
	 */
	public static void getInitTagDetails(List<FlowNewContent> initFlows) {
		String urlOrigin = FEED_LINK_ONLINE;
		if(Constants.ISTEST) {
			urlOrigin = FEED_LINK_TEST;
		}
		String url = urlOrigin;
		
		String response = null;
		for (int count = 0; count < FlowStartService.LINEDETAIL_NUM - 1;) {
			try {
				response = HttpUtils.get(url, "utf-8");
			} catch (Exception e) {
				logger.error("拉取tag详情页列表出错， url={}, response={}", url, response);
				return;
			}

			if (response != null && response.length() > 12) {
				String resJ = response.substring(6, response.length() - 6);
				JSONObject res = JSONObject.parseObject(resJ);
				JSONArray feedsJ = res.getJSONObject("jsonr").getJSONObject("data").getJSONArray("feeds");
				String fid = null;
				for (int index = 0; index < feedsJ.size() && count < FlowStartService.LINEDETAIL_NUM; index++) { // TODO　随机取3条，获取的随机方法有待完善
					JSONObject feedJ = (JSONObject) feedsJ.get(index);
					JSONArray imagesJ = feedJ.getJSONArray("images");
					fid = feedJ.getString("fid");
					if (imagesJ == null || imagesJ.size() == 0) {
						continue;
					}
					createTagDetetailFlow(initFlows, feedJ);
					count++;
				}
				url = urlOrigin + "&fid=" + fid;
			}
		}
	}
	
	public static String getFeedList() {
		String urlOrigin = FEED_LINK_ONLINE;
		if (Constants.ISTEST) {
			urlOrigin = FEED_LINK_TEST;
		}
		String url = urlOrigin;
		String response = null;
		try {
			response = HttpUtils.get(url, "utf-8");
		} catch (Exception e) {
			logger.error("拉取tag详情页列表出错， url={}, response={}", url, response);
			return null;
		}
		String resJ = response.substring(6, response.length() - 6);
		JSONObject res = JSONObject.parseObject(resJ);
		JSONArray feedsJ = res.getJSONObject("jsonr").getJSONObject("data").getJSONArray("feeds");
		if (feedsJ != null && feedsJ.size() > 0) {
			String feedJ = feedsJ.getJSONObject(0).toJSONString();
//			System.out.println(feedJ);
			return feedJ;
		} else {
			logger.error("获取到话题列表长度为0 , response={}", response);
			return null;
		}
	}

	public static FeedInfo getFeedInfo(String fid, String accountId) {
		String url = String.format(FEED_DETAIL_URL, fid, accountId);
		try {
			String response = HttpUtils.get(url, "utf-8");
			logger.info("获取话题详情, url={}", url);
//			System.out.println(response);
			String resJ = response.substring(6, response.length() - 6);
			JSONObject res = JSONObject.parseObject(resJ);
			JSONObject feedJ = res.getJSONObject("jsonr").getJSONObject("data");
			FeedInfo feed = JSON.parseObject(feedJ.toJSONString(), FeedInfo.class);
			return feed;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 获取话题列表，入缓存
	 */
	public static void getFeedListToCache() {
		String fid = "-1";
		int psize = 0;
		int index = 0;
		try {
			for (int i = 0; i < 3; i++) {
				logger.info("开始缓存话题, url={}", FEED_LIST_URL);
				String url = String.format(FEED_LIST_URL, fid, psize);
				logger.info("url={}", url);
				String response = HttpUtils.get(url, "utf-8");
//				System.out.println(response);
				String resJ = response.substring(6, response.length() - 6);

				JSONObject res = JSONObject.parseObject(resJ);
				JSONObject feedsJ = res.getJSONObject("jsonr").getJSONObject("data");
				FeedListInfo feeds = JSON.parseObject(feedsJ.toJSONString(), FeedListInfo.class);

				if (feeds != null) {
					psize += feeds.getFeeds().size();

					// 存入ocs中
					for (int j = 0; j < feeds.getFeeds().size(); j++) {
						fid = feeds.getFeeds().get(j).getFid();
						String key = "FEED_SORT_CACHE" + "#" + index;
						index++;
						CacheUtil.set(key, Constants.ONE_DAY_TIME, fid);
					}
				} else {
					i--;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void createTagDetetailFlow(List<FlowNewContent> initFlows, JSONObject feedJ) {
		FlowNewContent f = new FlowNewContent();
		f.setDestType(7);
		f.setFlowTag(feedJ.getString("tag"));
		f.setFlowTitle(feedJ.getString("content"));
		f.setFlowTagColor("255,130,165");
		f.setFeedId(feedJ.getString("fid"));
		f.setPic(((JSONObject)feedJ.getJSONArray("images").get(0)).getString("picUrl"));
		int random = (int) (1000 + Math.random() * 1000);
		f.setFlowDesc(random + "人参与");
		
		initFlows.add(f);
	}
	
	public static void main(String[] args) {
//		getFeedInfo("1", "");
		System.out.println(String.format("https://dev.chelaile.net.cn/feed/native!feeds.action?refer=lineDetail&fid=%s&psize=%d", "1", 1));
	
		getFeedListToCache();
	}
}