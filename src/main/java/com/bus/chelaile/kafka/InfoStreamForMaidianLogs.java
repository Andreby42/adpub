package com.bus.chelaile.kafka;

import java.util.*;
import java.util.concurrent.*;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.kafka.thread.MaidianLogsHandle;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

public class InfoStreamForMaidianLogs {
	private static final String TOPIC_ID = "realtime_log";
	private static final String GROUP_ID = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "group_id_maidian_logs",
			"info_flow_adv_maidian_logs");
	private static kafka.javaapi.consumer.ConsumerConnector consumer;

	private static final Object KAFKA_CLICK_LOCK = new Object();
	private volatile static boolean kafkaStarted = false;
	private static final Logger logger = LoggerFactory.getLogger(InfoStreamDispatcher.class);

	private static ExecutorService adMaidianLogExec = Executors.newFixedThreadPool(5); // 固定5个线程执行解析的任务。

	public InfoStreamForMaidianLogs() {
	}


	public void readKafka() {
		synchronized (KAFKA_CLICK_LOCK) {
			if (kafkaStarted) {
				logger.warn("<Info-Stream top-k>: InfoSteamForMaidianLogs 埋点日志 分发者客户端已经启动");
				return;
			}
			try {
				ConsumerConfig config = createConsumerConfig();

				consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);

				Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
				topicCountMap.put(TOPIC_ID, new Integer(1));

				Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(TOPIC_ID);
		        
		        // string
//				Map<String, List<KafkaStream<String, byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap,
//						keyDecoder, valueDecoder);
//				KafkaStream<String, byte[]> stream = consumerMap.get("adv_log").get(0);
//				ConsumerIterator<String, String> it = stream.iterator();
		        
		        
		        // byte[]
		        int threadNumber = 0;
		        for (KafkaStream<byte[], byte[]> stream : streams) {
		            adMaidianLogExec.execute(new MaidianLogsHandle(stream, threadNumber));
		        	threadNumber ++;
		        }
					
				logger.info("<Info-Stream top-k>: InfoStreamDispatcher ************************** 埋点日志 分发者客户端成功启动");
				
				} catch (Exception e) {
					logger.error("<Info-Stream top-k>: 启动Kafka客户端错误：" + e.getMessage(), e);
					e.printStackTrace();
				} finally {
					logger.info("<Info-Stream top-k>: InfoStreamDispatcher 埋点日志 分发者客户端退出");
				}
		}
	}

	
	private ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		// zookeeper 配置
		props.put("zookeeper.connect", "nkfk1:2181,nkfk2:2181/kafka");

		// group 代表一个消费组
		props.put("group.id", GROUP_ID);

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
