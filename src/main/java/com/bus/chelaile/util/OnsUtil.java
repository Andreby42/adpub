package com.bus.chelaile.util;



import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;
import com.aliyun.openservices.ons.api.*;
public class OnsUtil {
    protected static Logger logger = LoggerFactory.getLogger(OnsUtil.class) ;

    private static Properties properties;
    private static Producer producer;

    static {
        try {
        	logger.warn("初始化ONS Client开始...");
            properties = new Properties();
            properties.put(PropertyKeyConst.ProducerId, getProducerName());
            properties.put(PropertyKeyConst.AccessKey, getAccessKey());
            properties.put(PropertyKeyConst.SecretKey, getSecretKey());
            logger.info(properties.getProperty(PropertyKeyConst.ProducerId));
            logger.info(properties.getProperty(PropertyKeyConst.AccessKey));
            logger.info(properties.getProperty(PropertyKeyConst.SecretKey));
            producer = ONSFactory.createProducer(properties);
            //在发送消息前，必须调用start方法来启动Producer，只需调用一次即可。
            producer.start();
            logger.warn("初始化ONS Client完成: ossClient==null ? " + (producer == null));
        }catch (Exception e){
        	logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void close(){
        producer.shutdown();
    }

    public static Producer getProducer(){
        return producer;
    }

    public static String send(String key,String value){
//    	return "0"; //测试直接返回
        try {
            Producer pro = getProducer();
            Message msg = new Message(
                    //Message Topic
                    getTopicName(),
                    //Message Tag,
                    //可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在ONS服务器过滤
                    getTag(),
                    //Message Body
                    //任何二进制形式的数据，ONS不做任何干预，需要Producer与Consumer协商好一致的序列化和反序列化方式
                    value.getBytes("UTF-8")
            );

            // 设置代表消息的业务关键属性，请尽可能全局唯一。
            // 以方便您在无法正常收到消息情况下，可通过ONS Console查询消息并补发。
            // 注意：不设置也不会影响消息正常收发
            msg.setKey(key);

            //发送消息，只要不抛异常就是成功
            SendResult sendResult = pro.send(msg);
            if (sendResult != null) {
                //System.out.println(AdvUtil.getTimeStr() + " - [ONS_SUCCESS] MessageID: " + sendResult.getMessageId());
                //logger.info("[ONS_SUCCESS] messageID={}", sendResult.getMessageId());
            }
            
            return sendResult.getMessageId();
        }catch (Exception e){
//            logger.info("ons e .");

//            logger.error(e.getMessage(), e);
            e.printStackTrace();
            logger.error(String.format("Send to ONS Exception: errMsg=%s", e.getMessage()), e);
            return String.valueOf(Constants.STATUS_FAIL);
        }
    }
    
    public static String getProducerName() {
    	return PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"ons.producer.name");
       // return PropertiesReader.read("ons.producer.name");
    }
            
    public static String getAccessKey() {
    	return PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"ons.access.key");
       // return PropertiesReader.read("ons.access.key");
    }
    
    public static String getSecretKey() {
    	return PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"ons.secret.key");
      //  return PropertiesReader.read("ons.secret.key");
    }
    
    public static String getTopicName() {
    	return PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"ons.topic.name");
       // return PropertiesReader.read("ons.topic.name");
    }
    
    public static String getTag() {
    	return PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"ons.tag");
        //return PropertiesReader.read("ons.tag");
    }
    
    public static void main(String[] args) {
        OnsUtil.send("ADV", "{type:1}");
    }
}
