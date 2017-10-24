package com.bus.chelaile.flowNew;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.flowNew.model.FlowContent;
import com.bus.chelaile.util.New;

public class FlowStaticContents {
	
	protected static final Logger logger = LoggerFactory.getLogger(FlowStaticContents.class);
	public static final Map<Integer, FlowContent> lineDetailFlows = New.hashMap();
	
	

	
	
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
				null, null, "https://www.chelaile.net.cn/nt4web/201704/477ff2a660d549a5894e2fbc479eb44e.html", null, null);
		FlowContent flow3 = new FlowContent(3, "你真的可以兑换雨伞啦", "能量商城", "http://pic1.chelaile.net.cn/adv/brandIcon286320170221.png", "230,43,22,1", 
				null, null, null, null, null);
		FlowContent flow4 = new FlowContent(3, "商城逛逛", "能量商城", "http://pic1.chelaile.net.cn/adv/brandIcon286320170221.png", "230,43,22,1", 
				null, null, null, null, null);
		lineDetailFlows.put(0, flow0);
		lineDetailFlows.put(1, flow1);
		lineDetailFlows.put(2, flow2);
		lineDetailFlows.put(3, flow3);
		lineDetailFlows.put(3, flow4);
	}


}
