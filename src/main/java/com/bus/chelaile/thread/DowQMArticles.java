package com.bus.chelaile.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.flowNew.FlowStartService;
import com.bus.chelaile.flowNew.FlowStaticContents;

public class DowQMArticles implements Runnable {
//	@Autowired
//	private FlowServiceManager flowServiceManager;

	private static final Logger logger = LoggerFactory.getLogger(DowQMArticles.class);
	
	
	@Override
	public void run() {
		try {
			logger.info("开始缓存QM文章~ ");
			
			FlowStaticContents.initArticleContents();
			FlowStartService.initLineDetailFlows(FlowStaticContents.activityContens);  // 这样不可行，需要用static方法
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("计算投放pv比例因子出错！ " + e.getMessage(), e);
		}

	}
}
