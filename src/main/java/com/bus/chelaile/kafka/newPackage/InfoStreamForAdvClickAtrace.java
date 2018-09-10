package com.bus.chelaile.kafka.newPackage;

import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InfoStreamForAdvClickAtrace {

//    private static final int threadCount = 3;
    private static final Object KAFKA_CLICK_LOCK = new Object();
    private volatile static boolean kafkaStarted = false;
    private static final Logger logger = LoggerFactory.getLogger(InfoStreamForAdvClickAtrace.class);

    private static ExecutorService adClickLogExec = Executors.newSingleThreadExecutor(); 

    public InfoStreamForAdvClickAtrace() {}

    public void readKafka() {
        synchronized (KAFKA_CLICK_LOCK) {
            if (kafkaStarted) {
                logger.warn("<Info-Stream top-k>: InfoSteamForAdvClick 广告点击日志 分发者客户端已经启动");
                return;
            }
            try {
                // 点击
                adClickLogExec.execute(new ConsumerHandleAtrace());

                kafkaStarted = true;
                logger.info("<Info-Stream top-k>: InfoStreamForAdvClick 广告点击日志 分发者客户端成功启动");
            } catch (Exception e) {
                logger.error("<Info-Stream top-k>: 启动Kafka客户端错误：" + e.getMessage(), e);
                e.printStackTrace();
            } finally {
                logger.info("<Info-Stream top-k>: InfoStreamForAdvClick 广告点击日志 分发者客户端退出");
            }
        }
    }

    public static void main(String[] args) {}

}
