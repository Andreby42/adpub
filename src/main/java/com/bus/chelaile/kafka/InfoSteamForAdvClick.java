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

public class InfoSteamForAdvClick {

//	@Autowired
//	private ToutiaoHelp toutiaoHelp;
//	@Autowired
//	private LinkActiveHelp linkActiveHelp;

	private static final String TOPIC_ID = "adv_log";
	private static final String GROUP_ID = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "group_id_adv_click",
			"info_flow_adv_click_log");
	private static kafka.javaapi.consumer.ConsumerConnector consumer;

	public static final Object KAFKA_CLICK_LOCK = new Object();
	public static final Object TOPK_LOCK = new Object();
	public volatile static boolean kafkaStarted = false;
	public volatile static boolean topKStarted = false;
	public static final Logger logger = LoggerFactory.getLogger(InfoStreamDispatcher.class);

	public static ExecutorService adClickLogExec = Executors.newFixedThreadPool(5); // 固定5个线程执行解析的任务。

	public InfoSteamForAdvClick() {
	}


	public void readKafka() {
		synchronized (KAFKA_CLICK_LOCK) {
			if (kafkaStarted) {
				logger.warn("<Info-Stream top-k>: InfoStreamDispatcher信息流分发者客户端已经启动");
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
		        	adClickLogExec.execute(new ConsumerHandle(stream, threadNumber));
		        	threadNumber ++;
		        }
					
				logger.info("<Info-Stream top-k>: InfoStreamDispatcher信息流分发者客户端成功启动");
				
				} catch (Exception e) {
					logger.error("<Info-Stream top-k>: 启动Kafka客户端错误：" + e.getMessage(), e);
					e.printStackTrace();
				} finally {
					logger.info("<Info-Stream top-k>: InfoStreamDispatcher信息流分发者客户端退出");
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
		// InfoStreamDispatcher st = new InfoStreamDispatcher();
		// // st.runGetTopKArticles();
		// // st.readKafka();
		//
		// String
		// str =
		// "line=<190>Apr 14 17:41:39 web6 nginx: 123.147.244.13 |# - |# 2017-04-14 17:41:39 |# GET /?link=http://m.lemall.com/cn/sale/hongse414/index.html?cps_id=QT_sspmj_chelaile_youshangjiaotp_zh&deviceType=m1 metal&advId=3028&adtype=05&lng=106.53626518425347&udid=44785918-549e-4c2a-b99e-1c2d0b78e2a3&nw=MOBILE_LTE&lat=29.584922334963398&ip=123.147.244.13&utm_medium=floating&adv_id=3028&last_src=app_qq_sj&s=android&stats_referer=lineDetail&push_open=1&stats_act=auto_refresh&userId=unknown&provider_id=1&geo_lt=5&timestamp=1492158320493&geo_lat=29.578926&line_id=023-319-1&vc=78&sv=5.1&v=3.30.0&imei=868024027752105&udid=44785918-549e-4c2a-b99e-1c2d0b78e2a3&platform_v=22&utm_source=app_linedetail&stn_name=大庙&cityId=003&adv_type=5&ad_switch=63&geo_type=gcj&wifi_open=0&mac=68:3e:34:66:b2:08&deviceType=m1 metal&lchsrc=icon&stats_order=1-1&nw=MOBILE_LTE&AndroidID=3dcece3350d1d4f4&api_type=0&stn_order=2&geo_lac=25.0&language=1&first_src=app_meizhu_store&geo_lng=106.529763 HTTP/1.1 |# 302 |# 0.000 |# 264 |# - |# Chelaile/3.30.0 Duiba/1.0.7 Mozilla/5.0 (Linux; Android 5.1; m1 metal Build/LMY47I) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.127 Mobile Safari/537.36 |# - |# ad.chelaile.net.cn |# - |# -";
		// int filterCode = st.filterContent(str.trim());
		// System.out.println(filterCode);
		// st.processLog(str, filterCode);
		//
		//
		// CacheUtil.initClient();
		// String msg =
		// "<190>Apr 13 15:53:34 web4 nginx: 117.136.40.230 |# - |# 2017-04-13 15:53:34 |# GET /realtimelog?<ADV_EXHIBIT>adv_id:3025+%7C%23+s:android+%7C%23+last_src:app_huawei_store+%7C%23+push_open:1+%7C%23+adv_type:1+%7C%23+userId:unknown+%7C%23+provider_id:1+%7C%23+deviceType:HUAWEI+RIO-TL00+%7C%23+mac:74%3Aa5%3A28%3A3d%3Afb%3Aaa+%7C%23+wifi_open:1+%7C%23+lchsrc:icon+%7C%23+nw:MOBILE_LTE+%7C%23+AndroidID:34933e8aec55710+%7C%23+sv:6.0.1+%7C%23+vc:78+%7C%23+v:3.30.0+%7C%23+imei:867119024362584+%7C%23+udid:ac853978-a760-4ddb-8e83-bf23cdef2734+%7C%23+language:1+%7C%23+first_src:app_huawei_store+%7C%23+cityId:014 HTTP/1.1 |# 200 |# 0.000 |# 67 |# - |# Dalvik/2.1.0 (Linux; U; Android 6.0.1; HUAWEI RIO-TL00 Build/HuaweiRIO-TL00) |# - |# logs.chelaile.net.cn |# - |# -";
		// st.processLog(msg, 5);

		// String amc = "20:5d:47:6a:ea:ec";
		// System.out.println(amc.replace(":", "").toUpperCase());
		// System.out.println(DigestUtils.md5Hex(amc.replace(":", "")));
		// System.out.println(DigestUtils.md5Hex(amc.replace(":",
		// "").toUpperCase()));
		
		
		
		System.out.println(System.currentTimeMillis());
	}

}
