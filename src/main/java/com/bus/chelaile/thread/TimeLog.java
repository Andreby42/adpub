package com.bus.chelaile.thread;

import java.util.LinkedHashMap;
import java.util.Random;

public class TimeLog {
	
	private String name;
	private long start, last;
	private LinkedHashMap<String, Integer> rs = new LinkedHashMap<>();
	
	public static TimeLog ins(String name) {
		TimeLog ret = new TimeLog();
		ret.name = name;
		ret.start = ret.last = System.currentTimeMillis();
		return ret;
	}

	public TimeLog record(String point) {
		long now = System.currentTimeMillis();
		int used = (int) (now - last);
		rs.put(point, used);
		last = now;
		return this;
	}
	
	public String summay() {
		rs.put("_all_", (int) (System.currentTimeMillis() - start));
		return name + ":" + rs;
	}
	
	public static void main(String[] args) throws InterruptedException {
		TimeLog log = TimeLog.ins("test");
		Random rnd = new Random();
		for (int i = 0; i < 4; i ++) {
			Thread.sleep(rnd.nextInt(1000) + 200);
			log.record("stage" + i);
		}
		
		System.out.println(log.summay());
	}
}
