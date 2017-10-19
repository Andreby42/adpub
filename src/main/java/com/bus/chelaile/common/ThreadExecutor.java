package com.bus.chelaile.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ThreadExecutor {
	@Autowired
	private ThreadPoolTaskExecutor executor;

	
//	public void execute(FutureTask<GaoDeDto> task){
//		executor.submit(task);
//	}
}
