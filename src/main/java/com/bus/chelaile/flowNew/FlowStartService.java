package com.bus.chelaile.flowNew;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.model.ActivityContent;
import com.bus.chelaile.flowNew.model.ArticleContent;
import com.bus.chelaile.flowNew.model.FlowContent;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;

public class FlowStartService {

	protected static final Logger logger = LoggerFactory.getLogger(FlowStartService.class);
	private static final String TAGS＿LINK = "http://api.chelaile.net.cn:7000/feed/native!tags.action?cityId=027";   // TODO　城市有用否？
	public static final int LINEDETAIL_NUM = 3;

	
	public static void initLineDetailFlows(List<ActivityContent> activityContens) {
		try {

			
			logger.info("开始初始化详情页下方的 flows");
			List<FlowContent> initFlows = New.arrayList();
			getInitTags(initFlows);
			getInitActivities(initFlows, activityContens);
			getInitArticles(initFlows);
			getInitGoodsAndEnergy(initFlows);

			for (FlowContent flow : initFlows) {
				FlowStaticContents.addFlowsToMap(flow.getDestType(), flow);
			}

			// 推入ocs缓存起来
			for (Integer type : FlowStaticContents.LINE_DETAIL_FLOWS.keySet()) {
				String key = "QM_LINEDETAIL_FLOW_" + type;
				CacheUtil.setNew(key, Constants.ONE_DAY_TIME,
						JSONObject.toJSONString(FlowStaticContents.LINE_DETAIL_FLOWS.get(type)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取话题标签
	 * @param init
	 */
	private static void getInitTags(List<FlowContent> initFlows) {
		String url = TAGS＿LINK;
		String response = null;
		try{
			response = HttpUtils.get(url, "utf-8");
		} catch(Exception e) {
			logger.error("拉取tag列表出错， url={}, response={}", url, response);
		}
		
		if(response != null && response.length() > 12) {
			String resJ = response.substring(6, response.length() - 6);
//			System.out.println(resJ);
			JSONObject res = JSONObject.parseObject(resJ);
			JSONArray tags = res.getJSONObject("jsonr").getJSONObject("data").getJSONArray("tags");
			for(int i = 0; i < tags.size() && i < LINEDETAIL_NUM; i++ ) {		 // TODO　随机取3条，获取的随机方法有待完善
				createFlow(initFlows, (JSONObject) tags.get(i));
			}
			logger.info("详情页下方滚动栏，话题标签数为：{}", tags.size());
		}
	}
	
	private static void createFlow(List<FlowContent> initFlows, JSONObject object) {
		FlowContent f = new FlowContent();
		f.setDestType(0);
		f.setFlowTitle(object.getString("tag"));
		f.setFlowTag("热门话题");
		f.setFlowTagColor("255,175,0");
		f.setFlowIcon("https://image3.chelaile.net.cn/4c860c68c14d468b90f29974f036bf96");
		f.setTag(object.getString("tag"));
		f.setTagId(String.valueOf(object.getIntValue("tagId")));
		
		initFlows.add(f);
	}
	
	/**
	 * 获取活动
	 * @param initFlows
	 */
	private static void getInitActivities(List<FlowContent> initFlows, List<ActivityContent> activityContens) {
		int i = 0;
		for (ActivityContent activity : activityContens) {
			if (activity.getType() == 1) {		// 目前只支持h5类型的活动
				FlowContent f = new FlowContent();
				f.setDestType(1);
				f.setFlowTitle(activity.getTitle());
				f.setFlowTag("正在进行");
				f.setFlowTagColor("255,90,0");
				f.setFlowIcon("https://image3.chelaile.net.cn/bc27f4c64cd04fad9704b032b9b912d8");
				f.setActivityLink(activity.getLink());
				if(StringUtils.isBlank(activity.getLink())) {
					continue;
				}
				initFlows.add(f);
				i++;
				if (i >= LINEDETAIL_NUM) {
					break;
				}
			}
		}
		if(i == 0) {
			String key = "QM_LINEDETAIL_FLOW_" + 1;
			CacheUtil.deleteNew(key);
		}
		logger.info("详情页下方滚动栏，正在进行的活动数为：{}", i);
	}
	

	/**
	 * 获取文章
	 * @param init
	 * 改成从ocs提取文章
	 */
	private static void getInitArticles(List<FlowContent> initFlows) {
		int i = 0;
		for(ArticleContent article : FlowStaticContents.ARTICLE_CONTENTS.values()) {
			FlowContent f = new FlowContent();
			f.setDestType(2);
			f.setFlowTitle(article.getTitle());
			f.setFlowTag("热门文章");
			f.setFlowTagColor("52,152,219");
			f.setFlowIcon("https://image3.chelaile.net.cn/3cd56eeb33c8434daf2e17bdc9fde48d");
			
			f.setArticleUrl(article.getLink());
			
			initFlows.add(f);
			i ++;
			if(i >= LINEDETAIL_NUM) {
				break;
			}
		}
		logger.info("获取详情页文章数据：{}", i);
	}
	
	/**
	 * 积分商城 && 能量馆首页
	 * @param init
	 */
	private static void getInitGoodsAndEnergy(List<FlowContent> initFlows) {
		// 弄成可配置  // TODO
		FlowContent flowGoodsIndex = new FlowContent(3, "商城逛逛", "积分商城",
				"https://image3.chelaile.net.cn/a6f96bcf5ee742d7aa732259c32d1b8c", "255,175,0", null, null, null, null,
				null);
		FlowContent flowGood0 = new FlowContent(3, "iPhone X 抽抽抽", "积分商城",
				"https://image3.chelaile.net.cn/a6f96bcf5ee742d7aa732259c32d1b8c", "255,175,0", null, null, null, null,
				"https://activity.m.duiba.com.cn/newtools/index?id=2521608");
		FlowContent flowGood1 = new FlowContent(3, "好物翻出来", "积分商城",
				"https://image3.chelaile.net.cn/a6f96bcf5ee742d7aa732259c32d1b8c", "255,175,0", null, null, null, null,
				"https://activity.m.duiba.com.cn/newtools/index?id=2526447");
//		FlowContent flowGood1 = new FlowContent(3, "1元公交卡", "积分商城",
//				"https://image3.chelaile.net.cn/a6f96bcf5ee742d7aa732259c32d1b8c", "255,175,0", null, null, null, null,
//				"https://goods.m.duiba.com.cn/mobile/detail?itemId=1885");
		
		
		// 能量馆首页
		FlowContent flowEnergy = new FlowContent(4, "能量馆", "去能量馆充能",
				"https://image3.chelaile.net.cn/e8817ef0255649e8af326365d994329d", "52,152,219", null, null, null, null,
				null);
		
		
		initFlows.add(flowGoodsIndex);
		initFlows.add(flowGood0);
		initFlows.add(flowGood1);
		initFlows.add(flowEnergy);
		logger.info("积分商城和能量馆总共数量:{}", 4);
	}
	
	public static void main(String[] args) {
		int i = 0;
		FlowStaticContents.ARTICLE_CONTENTS.clear();
		for(ArticleContent article : FlowStaticContents.ARTICLE_CONTENTS.values()) {
			FlowContent f = new FlowContent();
			f.setDestType(2);
			f.setFlowTitle(article.getTitle());
			f.setFlowTag("热门文章");
			f.setFlowTagColor("52,152,219");
			f.setFlowIcon("https://image3.chelaile.net.cn/3cd56eeb33c8434daf2e17bdc9fde48d");
			
			f.setArticleUrl(article.getLink());
			
//			initFlows.add(f);
			i ++;
			if(i >= 3) {
				return;
			}
		}
		System.out.println("文章数： " + i);
	}
}
