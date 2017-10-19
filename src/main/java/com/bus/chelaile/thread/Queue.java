package com.bus.chelaile.thread;

import java.util.concurrent.LinkedBlockingQueue;
/**
 * 队列
 * @author zzz
 *
 */
public class Queue<T>{
	//	阻塞队列
	private static LinkedBlockingQueue queue = new LinkedBlockingQueue();
	//	没有数据时候会阻塞
	public static <T> T get() throws InterruptedException{
		return (T) queue.take();
	}
	
	public static <T> void set(T e){
		queue.add(e);
	}
	
	public static int size(){
		return queue.size();
	}
}
