package com.bus.chelaile.kafka;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.Constants;
import com.chelaile.logcenter2.sdk.api.Consumer;
import com.chelaile.logcenter2.sdk.api.LcFactory;
import com.chelaile.logcenter2.sdk.kafka.consumer.ConsumerCallbackWorker;
import com.chelaile.logcenter2.sdk.utils.LocaLUtil;

public class TestConsumer {

    private static Logger logger = LoggerFactory.getLogger(TestConsumer.class);

    private static final String TOPIC_ID = "adv_log";

    public static void main(String[] args) throws Exception {
        LcFactory lf = LcFactory.getInstance();

        Properties props = LocaLUtil.getProperties(TestConsumer.class, "kfk-consumer.properties");
        String groupId = (String) props.get("group.id");
        // 虽然卡夫支持很多种序列化方式 。 这两种作为sdk默认， lf.newByteProducer 中指定（所以配置不配置，都是一样的）
        // props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        Consumer cm = lf.newConsumer(TOPIC_ID, groupId, props);
        System.out.println(1111111);
        cm.receive(new ConsumerCallbackWorker() {

            AtomicInteger number = new AtomicInteger(0);

            @Override
            public void callback(byte[] bt) {
                System.out.println(222222222);
                try {
                    String str = new String(bt, "UTF-8");

                    logger.info("get string from kafka, str={}", str);

                    final int filterCode = filterContent(str.trim());
                    if (filterCode != Constants.ROW_SKIP) {
                        processLog(str, filterCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // System.out.println("================" + (System.currentTimeMillis() - b) / 1000);
            }
        });
    }

    private static int filterContent(String str) {
        if ((str.contains(Constants.AD_DOMAIN_NAME) || str.contains(Constants.REDIRECT_DOMAIN_NAME))
                && str.contains(Constants.PARAM_AD_ID) && !str.contains(Constants.FOR_DEVELOP_EXHIBIT)) { // 广告点击
            //          logger.info("读取到广告点击日志= {}", str);
            //            if (str.contains("s=IOS")) { // 仅处理iOS即可，android的有点击埋点了
            return Constants.ROW_ADV_CLICK;
        }

        return Constants.ROW_SKIP;
    }

    // 处理kafka日志（已被过滤过的）
    private static void processLog(String line, int filterCode) {
        InfoStreamHelp.analysisClick(line);
    }

}
