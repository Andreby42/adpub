package com.bus.chelaile.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StaticTimeLog {
	
	private static ThreadLocal<Map<String, TimeLog>> holder= new ThreadLocal<Map<String, TimeLog>>() {

		@Override
		protected Map<String, TimeLog> initialValue() {
			return new HashMap<>(); 
		}
		
	};
	
	public static void start(String name) {
		holder.get().put(name, TimeLog.ins(name));
	}
	
	public static void record(String name, String point) {
		holder.get().getOrDefault(name, TimeLog.ins(name)).record(point);
	}
	
	public static String summary(String name) {
		return holder.get().getOrDefault(name, TimeLog.ins(name)).summay();
	}
	
	public static void end(String name) {
		holder.get().remove(name);
	}

	public static void main(String[] args) throws InterruptedException {
		String name = "test1";
		StaticTimeLog.start(name);
		Random rnd = new Random();
		for (int i = 0; i < 4; i ++) {
			Thread.sleep(rnd.nextInt(1000) + 200);
			StaticTimeLog.record(name, "stage" + i);
		}
		
		System.out.println(StaticTimeLog.summary(name));
		
		System.out.println(System.currentTimeMillis());
		
	}
}
