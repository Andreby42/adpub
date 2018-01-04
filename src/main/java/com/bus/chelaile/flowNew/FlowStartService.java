package com.bus.chelaile.flowNew;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.model.ActivityContent;
import com.bus.chelaile.flowNew.customContent.TagUtils;
import com.bus.chelaile.flowNew.model.ArticleContent;
import com.bus.chelaile.flowNew.model.FlowNewContent;
import com.bus.chelaile.util.New;

public class FlowStartService {

	protected static final Logger logger = LoggerFactory.getLogger(FlowStartService.class);
	public static final int LINEDETAIL_NUM = 5;
	
	/***
	 * type列表： 0 话题详情页、 1 普通活动、 10 游戏活动、 11 福利活动、 2 文章
	 * @param activityContens
	 */
	public static void initLineDetailFlows(List<ActivityContent> activityContens) {
		try {
			logger.info("开始初始化详情页下方的 flows");
			List<FlowNewContent> initFlows = New.arrayList();
			TagUtils.getInitTags(initFlows);
			TagUtils.getInitTagDetails(initFlows);
			getInitActivities(initFlows, activityContens);
//			getInitArticles(initFlows);

			for (FlowNewContent flow : initFlows) {
				int typeKey = flow.getDestType();
				if(flow.getDestType() == 1) {	 // 活动下的游戏|福利需要单独提出来
					if(flow.getActivityEntity().getType() == 10 || flow.getActivityEntity().getType() == 11) {
						typeKey = flow.getActivityEntity().getType();
					}
				}
				FlowStaticContents.addFlowsToMap(typeKey, flow);
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
	 * 获取活动
	 * @param initFlows
	 */
	private static void getInitActivities(List<FlowNewContent> initFlows, List<ActivityContent> activityContens) {
		int commonAc = 0;
		int gameAc = 0;
		int wealAc = 0;
		for (ActivityContent activity : activityContens) {
			FlowNewContent f = new FlowNewContent();
			f.fillActivityInfo(activity);	// 填充活动
			if(activity.getType() == 10) { // 游戏
				f.setFlowTag(activity.getTag_title());
				f.setFlowTagColor("79,184,13");
				gameAc ++;
			} else if (activity.getType() == 11) {
				f.setFlowTag(activity.getTag_title());
				f.setFlowTagColor("255,87,34");
				wealAc ++;
			} else {
				f.setFlowTag("这活动厉害了");
				f.setFlowTagColor("52,152,219");
				commonAc ++;
			}
			
			int random = (int) (6000 + Math.random() * 1000);
			f.setFlowDesc(random + "人在玩");
			initFlows.add(f);
			if (commonAc + gameAc + wealAc >= LINEDETAIL_NUM) {
				break;
			}
		}
		if(commonAc == 0) {
			// 如果更新获取不到活动，那么清除掉ocs缓存的所有活动
			String key = "QM_LINEDETAIL_FLOW_" + 1;
			CacheUtil.deleteNew(key);
		}
		if(gameAc == 0) {
			String key = "QM_LINEDETAIL_FLOW_" + 10;
			CacheUtil.deleteNew(key);
		}
		if(wealAc == 0) {
			String key = "QM_LINEDETAIL_FLOW_" + 11;
			CacheUtil.deleteNew(key);
		}
		logger.info("详情页下方滚动栏，正在进行的普通活动数为：{}, 游戏数为：{}, 福利数为：{}", commonAc, gameAc, wealAc);
	}
	

	/**
	 * 获取文章
	 * @param init
	 * 改成从ocs提取文章
	 */
//	private static void getInitArticles(List<FlowNewContent> initFlows) {
//		int i = 0;
//		for(ArticleContent article : FlowStaticContents.ARTICLE_CONTENTS.values()) {
//			FlowNewContent f = new FlowNewContent();
//			f.setDestType(2);
//			f.setFlowTitle(article.getTitle());
//			f.setFlowTag("热门文章");
//			f.setFlowTagColor("52,152,219");
//			f.setFlowIcon("https://image3.chelaile.net.cn/3cd56eeb33c8434daf2e17bdc9fde48d");
//			
//			f.setArticleUrl(article.getLink());
//			
//			initFlows.add(f);
//			i ++;
//			if(i >= LINEDETAIL_NUM) {
//				break;
//			}
//		}
//		logger.info("获取详情页文章数据：{}", i);
//	}
	
	
	public static void main(String[] args) {
		int i = 0;
		FlowStaticContents.ARTICLE_CONTENTS.clear();
		for(ArticleContent article : FlowStaticContents.ARTICLE_CONTENTS.values()) {
			FlowNewContent f = new FlowNewContent();
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
