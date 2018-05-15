/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.bus.chelaile.kafka.thread;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.Constants;
import com.bus.chelaile.kafka.InfoStreamHelp;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class ConsumerHandle implements Runnable {
	private KafkaStream<byte[], byte[]> m_stream;
	private int m_threadNumber;

	public static final Logger logger = LoggerFactory.getLogger(ConsumerHandle.class);

	public ConsumerHandle(KafkaStream<byte[], byte[]> a_stream, int a_threadNumber) {
		m_threadNumber = a_threadNumber;
		m_stream = a_stream;
	}

	public void run() {
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
		while (it.hasNext()) {
			String log = null;
			try {
				log = new String(it.next().message(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
//			logger.info("Thread " + m_threadNumber + ": " + log);
			final int filterCode = filterContent(log.trim());
			if (filterCode != Constants.ROW_SKIP) {
				processLog(log, filterCode);
			}
			;
		}
	}

	// return 6: 广告点击日志
	// return 0: 其他
	private int filterContent(String str) {
//	    logger.info("str={}", str);
		if ((str.contains(Constants.AD_DOMAIN_NAME) || str.contains(Constants.REDIRECT_DOMAIN_NAME))
				&& (str.contains(Constants.PARAM_AD_ID) && !str.contains(Constants.FOR_DEVELOP_EXHIBIT))) { // 广告点击
//			logger.info("读取到广告点击日志= {}", str);
			return Constants.ROW_ADV_CLICK;
		}
		else {
//			logger.info("读取到不符合广告点击的日志={}", str);
			return Constants.ROW_SKIP;
		}
	}

	// 处理kafka日志（已被过滤过的）
	private void processLog(String line, int filterCode) {
		InfoStreamHelp.analysisClick(line);
	}

}