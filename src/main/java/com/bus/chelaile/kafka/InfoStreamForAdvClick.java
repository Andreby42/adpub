package com.bus.chelaile.kafka;

import java.util.*;
import java.util.concurrent.*;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.kafka.thread.ConsumerHandle;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

public class InfoStreamForAdvClick {


	private static final String TOPIC_ID = "adv_log";
	private static final String GROUP_ID = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "group_id_adv_click",
			"info_flow_adv_click_log");
	
	private static final String TOPIC_ID_MAIDIAN = "realtime_log";
    private static final String GROUP_ID_MAIDIAN = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "group_id_maidian_logs",
            "info_flow_adv_maidian_logs");
	private static kafka.javaapi.consumer.ConsumerConnector consumer;

	private static final Object KAFKA_CLICK_LOCK = new Object();
	private volatile static boolean kafkaStarted = false;
	private static final Logger logger = LoggerFactory.getLogger(InfoStreamForAdvClick.class);

	private static ExecutorService adClickLogExec = Executors.newFixedThreadPool(5); // 固定5个线程执行解析的任务。

	public InfoStreamForAdvClick() {
	}


	public void readKafka() {
		synchronized (KAFKA_CLICK_LOCK) {
			if (kafkaStarted) {
				logger.warn("<Info-Stream top-k>: InfoSteamForAdvClick 广告点击日志 分发者客户端已经启动");
				return;
			}
			try {
			    // 点击
				ConsumerConfig config = createConsumerConfig(GROUP_ID);

				consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);

				Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
				topicCountMap.put(TOPIC_ID, new Integer(1));

				Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(TOPIC_ID);
		        int threadNumber = 0;
		        for (KafkaStream<byte[], byte[]> stream : streams) {
		        	adClickLogExec.execute(new ConsumerHandle(stream, threadNumber));
		        	threadNumber ++;
		        }
					
		        
//		        // 埋点
//		        ConsumerConfig config1 = createConsumerConfig(GROUP_ID_MAIDIAN);
//
//                consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config1);
//
//                Map<String, Integer> topicCountMap1 = new HashMap<String, Integer>();
//                topicCountMap1.put(TOPIC_ID_MAIDIAN, new Integer(1));
//
//                Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap1 = consumer.createMessageStreams(topicCountMap1);
//                List<KafkaStream<byte[], byte[]>> streams1 = consumerMap1.get(TOPIC_ID_MAIDIAN);
//                int threadNumber1 = 0;
//                for (KafkaStream<byte[], byte[]> stream : streams1) {
//                    try {
////                        adClickLogExec.execute(new MaidianLogsHandle(stream, threadNumber1));
//                        threadNumber1++;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
		        
		        kafkaStarted = true;
				logger.info("<Info-Stream top-k>: InfoStreamDispatcher 广告点击日志 分发者客户端成功启动");
				} catch (Exception e) {
					logger.error("<Info-Stream top-k>: 启动Kafka客户端错误：" + e.getMessage(), e);
					e.printStackTrace();
				} finally {
					logger.info("<Info-Stream top-k>: InfoStreamDispatcher 广告点击日志 分发者客户端退出");
				}
		}
	}

	
	private ConsumerConfig createConsumerConfig(String groupId) {
		Properties props = new Properties();
		// zookeeper 配置
		props.put("zookeeper.connect", "nkfk1:2181,nkfk2:2181/kafka");

		// group 代表一个消费组
		props.put("group.id", groupId);

		// zk连接超时
		props.put("zookeeper.session.timeout.ms", "4000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.offset.reset", "smallest");
		// 序列化类
//		props.put("serializer.class", "kafka.serializer.StringEncoder");

		return new ConsumerConfig(props);
	}


	public static void main(String[] args) {
	}

}
