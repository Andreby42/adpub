package com.bus.chelaile.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 记录用户已经点击过的广告
 * @deprecated 目前该类没有用了。
 * @author liujh
 */
public class ClickAdsRecord {
    private String advId;
    private Date clickTime;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    protected static final Logger logger = LoggerFactory.getLogger(ClickAdsRecord.class);
    
    public String getAdvId() {
        return advId;
    }
    
    public void setAdvId(String advId) {
        this.advId = advId;
    }
    
    public Date getClickTime() {
        return clickTime;
    }
    
    public void setClickTime(Date clickTime) {
        this.clickTime = clickTime;
    }
    
    
    @Override
    public String toString() {
        return "ClickAdsRecord [advId=" + advId + ", clickTime=" + (clickTime==null? null : DATE_FORMAT.format(clickTime)) + "]";
    }

    public String toOCSString() {
        return advId + "," + ((clickTime == null) ? "-1" : clickTime.getTime());
    }
    
    public static ClickAdsRecord fromOCSString(String str) {
        if (str == null) {
            return null;
        }
        
        int idx = str.indexOf(",");
        if (idx < 0 || idx == str.length() - 1) {
            return null;
        }
        
        ClickAdsRecord record = new ClickAdsRecord();
        record.setAdvId(str.substring(0, idx));
        String timeStr = str.substring(idx + 1);
        
        if (StringUtils.equals("-1", timeStr)) {
            record.setClickTime(null);
        } else {
            try {
                long time = Long.parseLong(timeStr);
                Date clickTime = new Date(time);
                record.setClickTime(clickTime);
            } catch (NumberFormatException nfe) {
                logger.error("ClickAdsRecord保存的时间格式错误: " + nfe.getMessage(), nfe);
            }
        }
        
        return record;
    }
}