//package com.bus.chelaile.thread;
//
//import java.util.concurrent.LinkedBlockingQueue;
//
//public class QueueMonitor {
//
//
//	// 阻塞队列，存放广告监控的链接
//	private static LinkedBlockingQueue<String> queueMonitor = new LinkedBlockingQueue<String>();
//	
//	public static String getMonitorUrl() throws InterruptedException {
//		return  queueMonitor.take();
//	}
//	
//	public static void setMonitorUrl(String e) {
//		queueMonitor.add(e);
//	}
//	
//	public static int size(){
//		return queueMonitor.size();
//	}
//}
