/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.bus.chelaile.kafka.newPackage;

/****
 * 改lamba
 */
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.bus.chelaile.common.Constants;
import com.bus.chelaile.kafka.InfoStreamHelp;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;
import com.chelaile.logcenter2.sdk.api.Consumer;
import com.chelaile.logcenter2.sdk.api.LcFactory;
import com.chelaile.logcenter2.sdk.kafka.consumer.ConsumerCallbackWorker;

@Service()
@Scope()
public class ConsumerHandleAtrace implements Runnable {

    private static final String TOPIC_ID = "lua_ngx_kafka_click";
    private static final String GROUP_ID = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "group_id_adv_click_atrace",
          "adv_click_atrace_for_server");

    @Autowired
    ApplicationContext ctx;
    public static final Logger logger = LoggerFactory.getLogger(ConsumerHandleAtrace.class);

    public void run() {

        LcFactory lf;
        try {
            lf = LcFactory.getInstance();

            Properties props = new Properties();
            props.put("bootstrap.servers", "BKFK1:9092,BKFK2:9092,BKFK3:9092,BKFK4:9092,BKFK5:9092");
            props.put("auto.commit.interval.ms", "3000");
            props.put("session.timeout.ms","30000");
            props.put("enable.auto.commit","true");
            props.put("group.id", GROUP_ID);
            
            String groupId = (String) props.get("group.id");
            // 虽然卡夫支持很多种序列化方式 。 这两种作为sdk默认， lf.newByteProducer 中指定（所以配置不配置，都是一样的）
            // props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            // props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            Consumer cm = lf.newConsumer(TOPIC_ID, groupId, props);
            System.out.println(22222222);
            cm.receive(new ConsumerCallbackWorker() {

                @Override
                public void callback(byte[] bt) {
                    try {
                        String str = new String(bt, "UTF-8");

                        logger.info("get atrace.click  from kafka, str={}", str);

                        processLog(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

//    private int filterContent(String str) {
//        if ((str.contains(Constants.AD_DOMAIN_NAME) || str.contains(Constants.REDIRECT_DOMAIN_NAME))
//                && str.contains(Constants.PARAM_AD_ID) && !str.contains(Constants.FOR_DEVELOP_EXHIBIT)) { // 广告点击
//            //			logger.info("读取到广告点击日志= {}", str);
//            //            if (str.contains("s=IOS")) { // 仅处理iOS即可，android的有点击埋点了
//            return Constants.ROW_ADV_CLICK;
//        }
//
//        return Constants.ROW_SKIP;
//    }

    
    private void processLog(String line) {
        boolean isApp = line.contains("s=android") ||  line.contains("s=IOS") || line.contains("s=ios");
        if (! isApp)
            return;
        
        logger.info("**** get log from adv_log , str={}", line);
        InfoStreamHelp.analysisClick(line);
    }

}
