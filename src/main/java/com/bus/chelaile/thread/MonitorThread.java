//package com.bus.chelaile.thread;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.bus.chelaile.common.TimeLong;
//import com.bus.chelaile.util.HttpUtils;
//
//public class MonitorThread implements Runnable {
//
//	protected static final Logger logger = LoggerFactory.getLogger(MonitorThread.class);
//
//	private static int count = 0;
//
//	/**
//	 * 未处理历史的访问次数
//	 */
//	@Override
//	public void run() {
//
//		count++;
//
//		logger.info("start Monitor Thread=" + count);
//
//		while (true) {
//			try {
//				String url = QueueMonitor.getMonitorUrl();
//				if (QueueMonitor.size() > 100) {
//					TimeLong.info("QueueMonitor.size={}", QueueMonitor.size());
//				}
//				
//				monitorUrl(url);
//				
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//				;
//			}
//		}
//
//	}
//
//	
//	/*
//	 * 请求url，上报
//	 */
//	private void monitorUrl(String url) {
//		logger.info("广告监控上报 url={}", url);
//		try {
//			String response = HttpUtils.get(url, "utf-8");
//			logger.info("广告监控 resonse={}", response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//		
//	}
//
//}
