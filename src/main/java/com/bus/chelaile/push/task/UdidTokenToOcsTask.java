package com.bus.chelaile.push.task;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.util.New;
import com.bus.chelaile.util.StorageLog;

import java.util.List;

/**
 * Created by Administrator on 2016/5/11 0011.
 */
public class UdidTokenToOcsTask implements Runnable {
    protected static final Logger logger = LoggerFactory.getLogger(UdidTokenToOcsTask.class);

    private String msgId;
    private List<String> udidTokenList;
    private int expireTime;

    public UdidTokenToOcsTask(String msgId, List<String> udidTokenList, int expireTime) {
        this.msgId = msgId;
        this.udidTokenList = udidTokenList;
        this.expireTime = expireTime;
    }

    @Override
    public void run() {
        long st = System.currentTimeMillis();
        try {
            int size = udidTokenList.size();

            int writeUdidAndTokenCount = 0;
            
            List<String> wirteLogList = New.arrayList();
            
            for (int i = 0; i < size; i++) {
                String udidTokenStr = udidTokenList.get(i);
                if (StringUtils.isNotBlank(udidTokenStr)) {
                    String udid = StringUtils.substringAfter(udidTokenStr, "#");
                    String token = StringUtils.substringBefore(udidTokenStr, "#");
                    if (StringUtils.isNotBlank(token)) {
                        String signKey = DigestUtils.md5Hex(token.getBytes("utf8"));
                        
                        wirteLogList.add( token +"," +udid+","+signKey );
                        
                        if( wirteLogList.size() > 10000 ){
                        	StorageLog.log(wirteLogList);
                        	wirteLogList.clear();
                        }
                        
                        //AdvCache.setUdidTokenToOcs(signKey, udid, expireTime);

                        writeUdidAndTokenCount++;

                        logPartSuccessUdidAndToken(size, i, udidTokenStr);
                    }
                }
//                if (i % 10000 == 0) {
//                    Thread.sleep(5);
//                }
            }
            
            if( wirteLogList.size() > 0 ){
            	StorageLog.log(wirteLogList);
            	wirteLogList.clear();
            }

            logger.info("Write udidToken To Ocs success, write count={}", writeUdidAndTokenCount);
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
            logger.error("UdidTokenToOcsTask msgId={}, exception ", msgId, e);
        }
        logger.info("UdidTokenToOcsTask msgId={}, costs {} ms", msgId, (System.currentTimeMillis()-st));
    }

    /**
     * 打印部分写入ocs的udid和token
     * @param size udid和token的总个数
     * @param index 索引
     * @param udidAndToken udidAndToken
     */
    private void logPartSuccessUdidAndToken(int size, int index, String udidAndToken) {
        if(size > 10000) {
            if(index % 10000 == 0) {
                logger.info("Write udid and token to ocs success, udidAndToekn={}, index={}, size={}>10000", udidAndToken, index, size);
            }
        } else {
            if(index % 1000 == 0) {
                logger.info("Write udid and token to ocs success, udidAndToekn={}, index={}, size={}<10000", udidAndToken, index, size);
            }
        }
    }
    
    public static void main(String[] args) {
		
	}
}
