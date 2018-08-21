///**
// * 
// */
///**
// * @author Administrator
// *
// */
//package com.bus.chelaile.kafka.thread;
//
///****
// * 改lamba
// */
//
//import java.io.UnsupportedEncodingException;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Service;
//
//import com.bus.chelaile.common.Constants;
//import com.bus.chelaile.kafka.InfoStreamHelp;
//
//import kafka.consumer.ConsumerIterator;
//import kafka.consumer.KafkaStream;
//
//
//
//@Service()
//@Scope()
//public class ConsumerHandle implements Runnable {
//	private KafkaStream<byte[], byte[]> m_stream;
//	private int m_threadNumber;
//	@Autowired
//	ApplicationContext ctx;
//	public static final Logger logger = LoggerFactory.getLogger(ConsumerHandle.class);
//
//	public ConsumerHandle(KafkaStream<byte[], byte[]> a_stream, int a_threadNumber) {
//		m_threadNumber = a_threadNumber;
//		m_stream = a_stream;
//	}
//	
////	public ConsumerHandle injector(KafkaStream<byte[], byte[]> a_stream, int a_threadNumber) {
////	    m_threadNumber = a_threadNumber;
////        m_stream = a_stream;
////        
////        return this;
////	}
//
//	public void run() {
//	    
////	    ctx.getBean(ConsumerHandle.class).injector(a_stream, a_threadNumber);
//	    
//	    
//		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
//		String log ;
//		while (it.hasNext()) {
//			try {
//				log = new String(it.next().message(), "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//			    throw new IllegalStateException(e);
////				e.printStackTrace();
//			}
////			logger.info("Thread " + m_threadNumber + ": " + log);
//			final int filterCode = filterContent(log.trim());
//			if (filterCode != Constants.ROW_SKIP) {
//				processLog(log, filterCode);
//			}
//		}
//	}
//
//
//	
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
//
//	// 处理kafka日志（已被过滤过的）
//	private void processLog(String line, int filterCode) {
//		InfoStreamHelp.analysisClick(line);
//	}
//
//}