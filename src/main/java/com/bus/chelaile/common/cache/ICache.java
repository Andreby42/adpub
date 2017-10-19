package com.bus.chelaile.common.cache;

import java.util.List;
import java.util.Map;

import net.spy.memcached.internal.OperationFuture;

public interface ICache {
	 public void set(String key, int exp, Object obj);
	 
	 public Object get(String key);
	
	 public OperationFuture<Boolean> delete(String key);
	 
	 public Map<String, Object> getByList(List<String> list);
	 
	 
	 public long IncValue(String key, int exp);
	 
	 // redis 分布式锁获取
	 public boolean acquireLock();
	 
	 // redis 分布式锁释放
	 public boolean releaseLock();
	 
	 //
	 public void incrBy(String key, int incNumber, int exp);
	 
}
