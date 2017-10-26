package com.bus.chelaile.flowNew;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flowNew.model.ArticleContent;
import com.bus.chelaile.flowNew.model.FlowContent;
import com.bus.chelaile.flowNew.qingmang.QMHelper;
import com.bus.chelaile.util.DateUtil;
import com.bus.chelaile.util.New;

public class FlowStaticContents {
	
	protected static final Logger logger = LoggerFactory.getLogger(FlowStaticContents.class);
	
	// 详情页下方滚动栏内容
	public static final Map<Integer, List<FlowContent>> LINE_DETAIL_FLOWS = New.hashMap();
	public static final int ARTICLE_NUMBER_LIMIT = 1000;
	
	// 定时拉取的文章列表,key=自定义文章序列(有递增项)，value=文章详情
	// key="QM_ARTICLE_KEY#" + ${date} + "#" + articleNo
	// articleNo从0开始递增,每天从0开始
	public static final Map<String, ArticleContent> ARTICLE_CONTENTS = New.hashMap();
	private static final Set<String> CHANNELS = New.hashSet();
	static {
		CHANNELS.add("c284209068");CHANNELS.add("c284209082");CHANNELS.add("c284209029");
	}
	
	/**
	 * 初始化拉取文章
	 */
	public static void initArticleContents() {
		String date = DateUtil.getTodayStr("yyyy-MM-dd");
		int articleNo = getArticleNo(date); //获取本次拉取之前的 ‘文章no’ 起始值
		logger.info("本次拉取文章，起始id数为：{}", articleNo);
		
		while(ARTICLE_CONTENTS.size() < ARTICLE_NUMBER_LIMIT) {
			for(String channelId : CHANNELS) {
				articleNo = QMHelper.getArticlesFromAPI(channelId, ARTICLE_CONTENTS, articleNo, date);
				logger.info("拉取频道 ：{} 之后，最新文章id数为：{}", channelId, articleNo);
			}
		}
	}


	
	// 获取最新文章i序列编号
	private static int getArticleNo(String date) {
		int articleNo = 0;
		String articleNoStr = (String)CacheUtil.getFromRedis(AdvCache.getQMArticleNo(date));
		if(articleNoStr != null) {
			articleNo = Integer.parseInt(articleNoStr);
		}
		return articleNo;
	}
	// 更新最新文章序列编号
	public static void setArticleNo(String date, int no) {
		String key = AdvCache.getQMArticleNo(date);
		CacheUtil.setToRedis(key, Constants.ONE_DAY_NEW_USER_PERIOD, no);
	}



	public static void initLineDetailFlows() {
		
		// 获取活动列表
		
		
		
		//获取文章列表
		
		
		//获取话题标签列表
		
		
		
		//获取商城物品列表
		
		
		
		//构建 lineDetailFlows
		
		
		
		
		logger.info("开始初始化详情页下方的 flows");
		// 构造一个假数据
		// FlowContent(int destType, String flowtitle, String flowTag, String flowIcon, String flowTagColor,
		// String tag, String tagId, String channelLink, String activityLink, String duibaLink)
		FlowContent flow0 = new FlowContent(0, "小车君送礼啦：晒天空，赢好礼", "热门话题", "http://pic1.chelaile.net.cn/adv/brandIcon286320170221.png", "230,43,22,1", 
				"晒天空", "78", null, null, null);
		FlowContent flow1 = new FlowContent(1, "世界无车日活动", "正在进行", "http://pic1.chelaile.net.cn/adv/brandIcon286320170221.png", "230,43,22,1", 
				null, null, null, "https://www.chelaile.net.cn/nt4web/201704/477ff2a660d549a5894e2fbc479eb44e.html", null);
		FlowContent flow2 = new FlowContent(2, "娱乐八卦尽在此处", "热门文章", "http://pic1.chelaile.net.cn/adv/brandIcon286320170221.png", "230,43,22,1", 
				null, null, "c284209068", null, null);
		FlowContent flow3 = new FlowContent(3, "1元公交卡", "能量商城", "http://pic1.chelaile.net.cn/adv/brandIcon286320170221.png", "230,43,22,1", 
				null, null, null, null, "https://goods.m.duiba.com.cn/mobile/detail?itemId=1885");
		FlowContent flow4 = new FlowContent(3, "商城逛逛", "能量商城", "http://pic1.chelaile.net.cn/adv/brandIcon286320170221.png", "230,43,22,1", 
				null, null, null, null, null);
		FlowContent flow5 = new FlowContent(4, "能量倌", "去能量倌充能", "http://pic1.chelaile.net.cn/adv/brandIcon286320170221.png", "230,43,22,1", 
				null, null, null, null, null);
		
		addFlowsToMap(0, flow0);
		addFlowsToMap(1, flow1);
		addFlowsToMap(2, flow2);
		addFlowsToMap(3, flow3);
		addFlowsToMap(3, flow4);
		addFlowsToMap(4, flow5);
	}


	private static void addFlowsToMap(int type, FlowContent flow) {
		if(LINE_DETAIL_FLOWS.containsKey(type)) {
			LINE_DETAIL_FLOWS.get(type).add(flow);
		} else {
			List<FlowContent> flowList = New.arrayList();
			flowList.add(flow);
			LINE_DETAIL_FLOWS.put(type, flowList);
		}
	}
}
