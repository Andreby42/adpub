package com.bus.chelaile.common;


import java.io.IOException;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.OperationFuture;
import com.alibaba.fastjson.JSON;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

public class OCSTraceUtil {
    /**
     * 回调接口，参考spring hibernate中的相关代码
     *
     * @author 19781971@qq.com
     */
    public static interface MemcachedCallback<T> {

        /**
         * 利用MemcachedClient执行一段代码，返回一个值
         *
         * @param client
         * @return
         */
        T execute(MemcachedClient client);
    }
    
    // TODO remove the default values later.
    private final static String host = "9c0e27d0f09544c9.m.cnhzaliqshpub001.ocs.aliyuncs.com";//控制台上的“内网地址”
    private final static String port = "11211"; //默认端口 11211，不用改
    private final static String username = "9c0e27d0f09544c9";//控制台上的“访问账号”
    private final static String password = "Yuanguang2014";//邮件中提供的“密码”

    private static final String PROP_OCS_HOST = "ocs.tracehost";
    private static final String PROP_OCS_PORT = "ocs.traceport";
    private static final String PROP_OCS_USERNAME = "ocs.traceusername";
    private static final String PROP_OCS_PASSWORD = "ocs.tracepassword";

    private static final int DEFAULT_EXPIRE = 60 * 60;

    protected static final Logger logger = LoggerFactory.getLogger(OCSTraceUtil.class);

    private static final AtomicLong missCount = new AtomicLong();
    private static final AtomicLong hitCount = new AtomicLong();

    private static final class Clients {
        private static final int clientCount = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.CACHE.getValue(),
                "ocs.ins_count", "30"));
        private static MemcachedClient[] clients;
        private static final AtomicInteger callCount = new AtomicInteger();

        static {
            AuthDescriptor ad = new AuthDescriptor(new String[] { "PLAIN" },
                    new PlainCallbackHandler(readUsername(), readPassword()));
            ConnectionFactory connFactory = new ConnectionFactoryBuilder() //
                    .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY) //
                    .setAuthDescriptor(ad) //
                    .build();

            clients = new MemcachedClient[clientCount];
            for (int i = 0; i < clientCount; i++) {
                try {
                    clients[i] = new MemcachedClient(connFactory, AddrUtil.getAddresses(readHost() + ":" + readPort()));

                    logger.info("init memcached clients {}", clientCount);
                } catch (IOException ex) {
                    logger.error("初始化OCS MemcachedClient失败: " + ex.getMessage(), ex);

                    throw new RuntimeException(ex);
                }
            }
        }

        public static MemcachedClient getClient() {
            int count = callCount.incrementAndGet();
            if (count < 0) {
                count = 0;
                callCount.set(count);
            }

            return clients[count % clientCount];
        }
    }


    /**
     * set操作的retry次数
     */
    private static final int SET_RETRIES = 2;

    /**
     * 命中率调查的间隔次数
     */
    private static final int PRINT_SAMPLE_ON = 1000;

    /**
     * 执行一个删除操作
     *
     * @param key
     * @return 返回一个Future
     */
    public static OperationFuture<Boolean> delete(String key) {
        return execute((MemcachedClient client) -> client.delete(key));
    }

    /**
     * 执行一个memcached操作
     *
     * @param callback
     * @return
     */
    public static <T> T execute(MemcachedCallback<T> callback) {

        try {
            return callback.execute(Clients.getClient());
        } catch (Throwable tr) {
            logger.error("memcached execute fail:" + tr.getMessage(), tr);

            throw tr;
        }
    }

    /**
     * 读取memcached中的一个值
     *
     * @param key
     * @return
     */
    public static Object get(String key) {

        long stamp = System.currentTimeMillis();

        Object obj = execute((MemcachedClient client) -> client.get(key));

        logger.debug("OCS used {}ms with key get {}", String.format("%,d", System.currentTimeMillis() - stamp), key);

        if (obj != null) {
            logger.debug("OCS Cache HIT: key={}, value={}", key, obj);
            hitCount.incrementAndGet();
        } else {
            logger.debug(String.format("OCS Cache MISS: key={}", key));
            missCount.incrementAndGet();
        }

        logHitAndMiss();

        return obj;
    }

    /**
     * 打印一下缓存命中率情况
     */
    private static void logHitAndMiss() {
        long nowMiss = missCount.get(), nowHit = hitCount.get();
        long total = nowMiss + nowHit;

        if (total < 0) {
            String msg = "缓存命中率计数器超限，将重置";
            // 部分系统中日志会有filter by level设置，重要信息三重打印
            logger.info(msg);
            logger.warn(msg);
            logger.error(msg);

            missCount.set(0);
            hitCount.set(0);

            return;
        }

        if ((total % PRINT_SAMPLE_ON) == 0) {
            logger.info(String.format("Miss: %d, %.3f%%; Hit: %d, %.3f%%", nowMiss, nowMiss / (total / 100d), nowHit,
                    nowHit / (total / 100d)));
        }
    }

    /**
     * 返回一个MemcachedClient
     *
     * @return
     * @see #execute(MemcachedCallback)
     */
    public static MemcachedClient getClient() {
        return Clients.getClient();
    }

    /**
     * 批量获取
     *
     * @param keys
     * @return
     */
    public static Map<String, Object> getS(Collection<String> keys) {

        long stamp = System.currentTimeMillis();

        Map<String, Object> objs = execute((MemcachedClient client) -> client.getBulk(keys));

        logger.debug("OCS used {}ms with keys get {}", String.format("%,d", System.currentTimeMillis() - stamp), keys);

        if (objs != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("OCS Cache HIT: key={}, value={}", JSON.toJSONString(keys), JSON.toJSONString(objs));
            }

            hitCount.incrementAndGet();
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("OCS Cache MISS: key={}", JSON.toJSONString(keys));
            }

            missCount.incrementAndGet();
        }

        logHitAndMiss();

        return objs;
    }

    /**
     * 从缓存里获取一个字符串，
     *
     * @param key
     * @return
     * @throws ClassCastException 如果对象类型不是字符串
     */
    public static String getStringValue(String key) {
        return (String) get(key);
    }

    /**
     * 尝试（多次）将一个值写入到memcached中，并且等待操作完成
     *
     * @param key
     * @param exp
     * @param obj
     */
    public static void set(String key, int exp, Object obj) {
        long stamp = System.currentTimeMillis();

        logger.info("[CACHE_SET] key=" + key + ", exp=" + exp + ", obj=" + obj);

        for (int i = 0; i < SET_RETRIES; i++) {
            OperationFuture<Boolean> future = execute((MemcachedClient client) -> client.set(key, exp, obj));
            try {
                boolean objValue = future.get();

                if (objValue) {
                    break;
                } else {
                    logger.info("[" + i + "] 保存失败的key=" + ", exp=" + exp + ", obj=" + obj);
                }
            } catch (Exception e) {
                logger.error("OperationFuture Exception: " + e.getMessage(), e);
            }
        }

        logger.debug("OCS used {}ms with key set {}", String.format("%,d", System.currentTimeMillis() - stamp), key);
    }

    public static void set(String key, Object obj) {
        set(key, DEFAULT_EXPIRE, obj);
    }

    ////////////////////////////////////////
    //   Private Methods
    ////////////////////////////////////////


    private static String readHost() {
        String result = PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), PROP_OCS_HOST);
        if (StringUtils.isEmpty(result)) {
            return host;
        }
        return result;
    }

    private static String readPassword() {
        String result = PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), PROP_OCS_PASSWORD);
        if (StringUtils.isEmpty(result)) {
            return password;
        }
        return result;
    }

    private static String readPort() {
        String result = PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), PROP_OCS_PORT);
        if (StringUtils.isEmpty(result)) {
            return port;
        }
        return result;
    }

    private static String readUsername() {
        String result = PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), PROP_OCS_USERNAME);
        if (StringUtils.isEmpty(result)) {
            return username;
        }
        return result;
    }

}
