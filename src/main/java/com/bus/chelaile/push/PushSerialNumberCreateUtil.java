package com.bus.chelaile.push;



import com.bus.chelaile.util.DateUtil;

/**
 * Created by Administrator on 2016/4/6 0006.
 */
public class PushSerialNumberCreateUtil {
    private static long number = 1000;
    private static final String serialNumberPre = "wow2";

    // 返回时间类型 yyyy-MM-dd HH:mm:ss
    public synchronized static String getSerialNumber() {
        String keyStr = DateUtil.getTodayStr("yyyyMMddHHmm");
        ++number;
        return serialNumberPre+keyStr+number;
    }

}
