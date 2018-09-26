package com.bus.chelaile.common;


import net.spy.memcached.internal.OperationFuture;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.cache.ICache;
import com.bus.chelaile.common.cache.OCSCacheUtil;
import com.bus.chelaile.common.cache.RedisCacheImplUtil;
import com.bus.chelaile.common.cache.RedisCacheOftenReadImplUtil;
import com.bus.chelaile.common.cache.RedisCacheOftenWriteImplUtil;
import com.bus.chelaile.common.cache.RedisTBKCacheImplUtil;
import com.bus.chelaile.common.cache.RedisTokenCacheImplUtil;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.util.config.PropertiesUtils;

public class CacheUtil {
//    //	访问得到token缓存
//	private static ICache client;
	//	redis缓存
	private static ICache redisClient;
	// 存储token的redis
	private static ICache redisToken;
	// 存储tbk title的redis
    private static ICache redisTBK;
    // 高频率写
    private static ICache redisOftenWrite;
    // 高频率读
    private static ICache redisOftenRead;
    // 存储 traceInfo的redis
//    private static ICache redisAtrace;
    // 存储bus后台设置的cshow值的redis
//    private static ICache redisBUS;
//	//  用来获取用户头像的redis
//	private static ICache redisWow;
    
    
    // TODO 以下链接都转到使用新的meme客户端的链接方式来做
//		保存用户访问量等信息
    /*
	private static ICache cacheNewClient;
	//	获取access_token的 
	private static ICache cacheApiTokenClient;
//	支付信息
	private static ICache cacheCommonClient;
	
    // 用户traceInfo信息
	private static ICache traceInfoClient;*/
    
	
	private static boolean isInitSuccess = false;
	
	protected static final Logger logger = LoggerFactory
			.getLogger(CacheUtil.class);
	
    private static final int DEFAULT_EXPIRE = 60 * 60;
    
    private static String cacheType = PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), "cacheType","ocs");
    
    
    
    private static final String defauleTBKTitle = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "default.tbk.title");
    

    public static void initClient() {
    	
       if( isInitSuccess ){
    	   logger.info(cacheType+"已经reload成功");
    	   return;
       }
       redisClient = new RedisCacheImplUtil();
       redisOftenWrite = new RedisCacheOftenWriteImplUtil();
       redisOftenRead = new RedisCacheOftenReadImplUtil();
       
       redisToken = new RedisTokenCacheImplUtil();
       redisTBK = new RedisTBKCacheImplUtil();
//       redisBUS = new RedisBUSCacheImplUtil();
//       redisAtrace = new RedisAtraceCacheImplUtil();
       isInitSuccess = true;
    }
    
    
    
    /********************************************************************************************************/
    /************************************高频率 redis 的使用 *******************************************************/
    
    
    // 记录项目总次数
    public static void incrProjectSend(String projectId, int inc) {
        String key = AdvCache.getProjectKey(projectId);
        String filedTotal = AdvCache.getProjectTotalSendKey();
        String fieldDay = AdvCache.getProjectDaySendKey();
        
        redisOftenWrite.addHashSetValue(key, filedTotal, inc);
        redisOftenWrite.addHashSetValue(key, fieldDay, inc);
    }
    
    public static void incrProjectClick(String projectId, int inc) {
        String key = AdvCache.getProjectKey(projectId);
        String filedTotal = AdvCache.getProjectTotalClickKey();
        String fieldDay = AdvCache.getProjectDayClickKey();
        
        redisOftenWrite.addHashSetValue(key, filedTotal, inc);
        redisOftenWrite.addHashSetValue(key, fieldDay, inc);
    }
    
    public static int getProjectTotalSend(String projectId) {
        String field = AdvCache.getProjectTotalSendKey();
        return getIntValueFromRedis(redisOftenRead, AdvCache.getProjectKey(projectId), field);
    }
    
    public static int getProjectDaySend(String projectId) {
        String field = AdvCache.getProjectDaySendKey();
        return getIntValueFromRedis(redisOftenRead,  AdvCache.getProjectKey(projectId), field);
    }

    public static int getProjectTotalClick(String projectId) {
        String field = AdvCache.getProjectTotalClickKey();
        return getIntValueFromRedis(redisOftenRead,  AdvCache.getProjectKey(projectId), field);
    }

    public static int getProjectDayClick(String projectId) {
        String field = AdvCache.getProjectDayClickKey();
        return getIntValueFromRedis(redisOftenRead,  AdvCache.getProjectKey(projectId), field);
    }
    
    private static int getIntValueFromRedis(ICache redisClientParam, String key, String field) {
        String value = (String)redisClientParam.getHashSetValue(key, field);
        if(value != null) {
            return Integer.parseInt(value);
        }
        return 0;
    }
    
    
    // 保存点击关闭按钮
    public static void setCloseTimeToRedis(String udid, String pid) {
        String key = AdvCache.getCloseAdKey(udid);
        String field = pid;
        redisOftenWrite.setHashSetValue(key, field, String.valueOf(System.currentTimeMillis()));
    }
    
    public static String getCloseAdTime(String udid, String pid) {
        String key = AdvCache.getCloseAdKey(udid);
        return redisOftenRead.getHashSetValue(key, pid);
    }
    
    
    public static void setToOftenRedis(String key,int exp,Object obj){
        redisOftenWrite.set(key, exp, obj);
    }
    
    public static Object getFromOftenRedis(String key){
        return redisOftenRead.get(key);
    }
    
    public static void incrToOftenRedis(String key, int exp){
        redisOftenWrite.IncValue(key, exp);
    }
    
    public static void redisOftenDelete(String key){
        redisOftenWrite.delete(key);
    }
    
    /********************************************************************************************************/
    /************************************低频率 redis 的使用 *******************************************************/
    public static void setToRedis(String key,int exp,Object obj){
    	redisClient.set(key, exp, obj);
    }
    
    public static Object getFromRedis(String key){
    	return redisClient.get(key);
    }
    
    public static void incrToCache(String key, int exp){
    	redisClient.IncValue(key, exp);
    }
    
    public static void redisDelete(String key){
    	redisClient.delete(key);
    }
    
    
    public static Map<String, Object> getValueFromRedisByList(List<String> list){
    	return redisClient.getByList(list);
    }
    
    public static boolean acquireLock() {
    	return redisClient.acquireLock();
    }
    
    public static boolean releaseLock() {
    	return redisClient.releaseLock();
    }
    
    public static void redisIncrBy(String key, int number, int exp) {
    	redisClient.incrBy(key, number, exp);
    }
    
    // redis 有序集合 3个方法
    public static void setSortedSet(String key, long score,String value, int expire) {
    	redisClient.setSortedSet(key, score, value, expire);
    }
    
    public static Set<String> getRangeSet(String key, long startScore, long endScore, int count) {
    	return redisClient.zrangeByScore(key, startScore, endScore, count);
    }
    
    public static Set<String> getRevRangeSet(String key, long startScore, long endScore, int count) {
    	return redisClient.zrevRangeByScore(key, endScore, startScore, count);
    }
    
 // token存储升级，在这一步做一些处理，保证输出跟老版本一致 2018-04-02
    // 输出类似：120c83f760217151408|android|3
    public static String getTokenFromRedis(String udid) {
        Map<String, String> tokenMap = redisToken.getHsetAll("UDID2TOKEN#" + udid);
        logger.info("tokenMap.content={}", tokenMap.toString());
        logger.info("tokenMap.size={}", tokenMap.size());
        if(tokenMap == null || tokenMap.size() < 3) {
            return null;
        } else {
            String token = tokenMap.get("token");
            String sys = tokenMap.get("sys");
            String tokenType = tokenMap.get("tokenType");
            return new StringBuilder().append(token).append("|").append(sys).append("|").append(tokenType).toString();
        }
    }
    
    // 从redis中获取淘宝客title，随机拿出一个
    public static String getTBKTitle(int advId) {
        if (StaticAds.advTBKTitleKey.containsKey(advId)) {
            String key = StaticAds.advTBKTitleKey.get(advId);
            String value = (String) redisTBK.get(key);
            if (StringUtils.isNotEmpty(value)) {
                String[] titles = value.split("#");
                int size = titles.length;
                return titles[new Random().nextInt(size)];
            }
        }
        return defauleTBKTitle;
    }
    
    
    
    
    /********************************************************************************************************/
    /************************************OCS 的使用 *******************************************************/
    
    
    /**
     * @param key the Cache Key
     * @param exp the expiration time of the records, should not exceeds 60 * 60 * 24 * 30(30 天), 单位: 秒
     * @param obj 缓存的对象
     */
    public static void set(String key, int exp, Object obj) {
        if( Constants.ISDEV ) {
            redisClient.set(key, exp, obj);
            return;
        }
        OCSCommonUtil.set(key, exp, obj);
    }
    
    public static void set(String key, Object obj) {
    	if( Constants.ISDEV ) {
    		redisClient.set(key, DEFAULT_EXPIRE, obj);
    		return;
    	}
        set(key, DEFAULT_EXPIRE, obj);
    }
    
    public static Object get(String key) {
    	if( Constants.ISDEV ) {
    		return redisClient.get(key);
    	}
       return OCSCommonUtil.get(key);
    }
    
    public static Object getNew(String key) {
    	if( Constants.ISDEV ) {
    		return redisClient.get(key);
    	}
//        return cacheNewClient.get(key);
        return OCSNewUtil.get(key);
     }
    
    public static void setNew(String key, int exp, Object obj) {
    	if( Constants.ISDEV ) {
    		redisClient.set(key, exp, obj);
    		return;
    	}
        OCSNewUtil.set(key, exp, obj);
//    	cacheNewClient.set(key, exp, obj);
    }
    
    public static OperationFuture<Boolean> deleteNew(String key) {
    	if( Constants.ISDEV ) {
    		return redisClient.delete(key);
    	}
//        return cacheNewClient.delete(key);
        return OCSNewUtil.delete(key);
    }

    public static OperationFuture<Boolean> delete(String key) {
    	if( Constants.ISDEV ) {
    		return redisClient.delete(key);
    	}
//    	return cacheCommonClient.delete(key);
    	return OCSCommonUtil.delete(key);
    }

    
    public static Object getApiInfo(String key){
    	if( Constants.ISDEV ) {
    		return redisClient.get(key);
    	}
//    	return cacheApiTokenClient.get(key);
    	return OCSApiUtil.get(key);
    }
    
//    public static Set<String> getWowDatas(String key) {
//    	return redisWow.getSet(key);
//    }
    
    // ocs查询
    public static String getFromCommonOcs(String key) {
    	
    	if( Constants.ISDEV ) {
    		Object j = redisClient.get(key);
    		if(j == null)
        	    return null;
        	else
        	    return String.valueOf(j);
    	}
    	
//    	Object j = cacheCommonClient.get(key);
    	Object j = OCSCommonUtil.get(key);
    	if(j == null)
    	    return null;
    	else
    	    return String.valueOf(j);
    }
    
    // ocs设置值
    public static void setToCommonOcs(String key, int exp, Object obj) {
    	if( Constants.ISDEV ) {
    		redisClient.set(key, exp, obj);
    		return;
    	}
//    	cacheCommonClient.set(key, exp, obj);
    	OCSCommonUtil.set(key, exp, obj);
    }
    
    // wechatocs查询
    public static String getFromWechatOcs(String key) {
        
        if( Constants.ISDEV ) {
            Object j = redisClient.get(key);
            if(j == null)
                return null;
            else
                return String.valueOf(j);
        }
        
//      Object j = cacheCommonClient.get(key);
        Object j = OCSWechatUtil.get(key);
        if(j == null)
            return null;
        else
            return String.valueOf(j);
    }
    
    
//    // 从BUS redis中获取数据
//    public static Object getFromBUSRedis(String key){
//        return redisBUS.get(key);
//    }
    
    // 从redis中获取所有的keys，慎用！ 
//    public static Set<String> allKeys(String pattern) {
//        return redisClient.allKeys(pattern);
//    }
 
    // 将traceInfo保存到redis中, 永久
    public static void setToAtrace(String key, String value) {
    	if( Constants.ISDEV ) {
    		redisClient.set(key, -1, value);
    		return;
    	}
        OCSTraceUtil.set(key, -1, value);;
    }
    // 同上
    public static void setToAtrace(String key, String value, int exp) {
    	if( Constants.ISDEV ) {
    		redisClient.set(key, exp, value);
    		return;
    	}
        OCSTraceUtil.set(key, exp, value);
    }
    
    
    
    public static void main(String[] args) throws InterruptedException {
//    	initClient();
//    	setToRedis("a", -1, "12");
//    	System.out.println(getFromRedis("a"));
//    	
    	
    	
    	ICache client1 = new OCSCacheUtil("121.40.210.76","7078","9c0e27d0f09544c9","Yuanguang2014");
    	client1.set("qkk_test1", Constants.ONE_DAY_TIME, "1111");
    	System.out.println(client1.get("qkk_test1"));
    	System.out.println(client1.get("qkk_test2"));
    	
    	System.out.println(client1.get("REMINDERTOKEN#fb6d0547-b3ba-435b-ba29-001a1bbe261b"));
    	
    	
    	// ************8  redis test
    	JedisPoolConfig config = new JedisPoolConfig();
        String host = "127.0.0.1";
        int port = 6379;
        config.setMaxTotal(400);
        //config.setMaxActive(400);
        config.setMaxIdle(200);
        config.setMinIdle(20);

        //config.setMaxWait(2000000);
        //config.setMaxWaitMillis();
        config.setTestWhileIdle(true);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        JedisPool pool = new JedisPool(config, host, port);
        Map<String, String> result = null;
        Jedis conn = null;
        //set
        conn = pool.getResource();
        long result1 = conn.hset("testMap", "1", "1");
        pool.returnResource(conn);
        
        conn = pool.getResource();
        result = conn.hgetAll("testMap");
        pool.returnResource(conn);
        
        for(Entry<String, String> s : result.entrySet()) {
            System.out.println(s.getKey() + "--->" + s.getValue());
        }
        System.out.println(result.get("1"));
    	System.exit(1);
    }
}
