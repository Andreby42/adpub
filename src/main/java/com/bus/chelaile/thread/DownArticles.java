package com.bus.chelaile.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.flow.WuliToutiaoHelp;
import com.bus.chelaile.flowNew.FlowStartService;
import com.bus.chelaile.flowNew.FlowStaticContents;
import com.bus.chelaile.flowNew.customContent.TagUtils;

public class DownArticles implements Runnable {
	
	private WuliToutiaoHelp wuliToutiaoHelp;

	private static final Logger logger = LoggerFactory.getLogger(DownArticles.class);
	
	public DownArticles(WuliToutiaoHelp wuliToutiaoHelp) {
		this.wuliToutiaoHelp = wuliToutiaoHelp;
	}
	
	
	@Override
	public void run() {
		try {
			logger.info("刷新缓存~ ");
			
			// 接口获取文章
//			FlowStaticContents.initArticleContents();
//			getWuliToutiaoHelp().setSortArticlesToCache();
			// 合并文章和其他内容，更新缓存
			FlowStartService.initLineDetailFlows(FlowStaticContents.activityContens);  // 这样不可行，需要用static方法
			
			// 缓存话题列表
			TagUtils.getFeedListToCache();
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("刷新文章和话题缓存出错！ " + e.getMessage(), e);
		}
	}


	public WuliToutiaoHelp getWuliToutiaoHelp() {
		return wuliToutiaoHelp;
	}


	public void setWuliToutiaoHelp(WuliToutiaoHelp wuliToutiaoHelp) {
		this.wuliToutiaoHelp = wuliToutiaoHelp;
	}
}
