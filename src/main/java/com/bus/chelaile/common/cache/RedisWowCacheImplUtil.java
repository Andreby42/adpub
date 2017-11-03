package com.bus.chelaile.common.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import net.spy.memcached.internal.OperationFuture;

public class RedisWowCacheImplUtil implements ICache{

	
	private static Logger log = LoggerFactory.getLogger(RedisCacheImplUtil.class) ;
    
	//private static String REDIS_HOST = PropertiesReaderWrapper.read("redisCount.host", DEFAULT_REDIS_HOST);
	//private static int REDIS_PORT = PropertiesReaderWrapper.readInt("redisCount.port", DEFAULT_REDIS_PORT);
	
	private static String REDIS_HOST = PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), "redisCount.wow.host", "10.117.16.42");
	private static int REDIS_PORT = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), "redisCount.wow.port", "7379"));
	
	private static JedisPool pool = null;
	
    static {
    	initPool();
    }
    
	private static void initPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		String host = REDIS_HOST;
		int port = REDIS_PORT;
		config.setMaxTotal(400);
		//config.setMaxActive(400);
		config.setMaxIdle(200);
		config.setMinIdle(20);
		
		//config.setMaxWait(2000000);
		//config.setMaxWaitMillis();
		config.setTestWhileIdle(true);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		
		pool = new JedisPool(config, host, port);
		
		log.info("Redis Wow CacheImplUtil init success,ip={},host={}",REDIS_HOST,REDIS_PORT);
	}
	
	private static JedisPool getPool() {
		if (pool == null) initPool();
		return pool;
	}
	
	
	@Override
	public void set(String key, int exp, Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationFuture<Boolean> delete(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getByList(List<String> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long IncValue(String key, int exp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean acquireLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean releaseLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void incrBy(String key, int incNumber, int exp) {
		// TODO Auto-generated method stub
		
	}
		
	/**
	 * 从wow redis中过去用户信息用
	 * @param key
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public  Set<String> getSet(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		Set<String> ret = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			ret = conn.smembers(key);
			log.debug("Redis-Get: Key=" + key + ",Value=" + ret);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.set, key=%s, error message: " + e.getMessage(), key));
			if (pool!=null && conn!=null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool!=null && conn!=null) 
				pool.returnResource(conn);
		}
		
		return ret;
	}
	
	
}
