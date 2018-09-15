package com.bus.chelaile.model.record;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import com.bus.chelaile.util.New;

/**
 * Created by Administrator on 2015/11/27 0027.
 */
public class CacheRecord {
    int clickCount;
    private int fakeCount;
    Map<String, Integer> dayCountMap = New.hashMap();
    Map<String, Integer> dayClickMap = New.hashMap();
    private Map<String, Integer> dayRateMap = New.hashMap();  // 记录误点击

    public CacheRecord() {
    }

    public CacheRecord(int count) {
        this.clickCount = count;
        this.fakeCount = count;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }
    
    public void incrClickCount() {
        this.clickCount ++;
    }
    
    public void incrFakeCount() {
        this.fakeCount ++;
    }
    
    public Map<String, Integer> getDayCountMap() {
        return dayCountMap;
    }

//    public void setDayCountMap(Map<String, Integer> dayCountMap) {
//        this.dayCountMap = dayCountMap;
//    }

    // day 发送 +1
    public void putDayCountMap(String dayStr, int dayCount) {
        int zcount = dayCount;
        if (dayCountMap.containsKey(dayStr)) {
            zcount += dayCountMap.get(dayStr);
        }
        dayCountMap.put(dayStr, zcount);
    }
    
    // day 点击 +1
    public void putDayClickMap(String dayStr, int dayClick) {
        int zcount = dayClick;
        if (dayClickMap.containsKey(dayStr)) {
            zcount += dayClickMap.get(dayStr);
        }
        dayClickMap.put(dayStr, zcount);
    }
    
    // day 误点击 +1
    public void putDayRateMap(String dayStr, int dayRate) {
        int zcount = dayRate;
        if (dayRateMap.containsKey(dayStr)) {
            zcount += dayRateMap.get(dayStr);
        }
        dayRateMap.put(dayStr, zcount);
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("CacheRecord{");
        sb.append("clickCount=").append(clickCount).append(", dayCountMap={");
        Set<Entry<String, Integer>> entrySet = dayCountMap.entrySet();
        
        boolean isFirst = true;
        for (Entry<String, Integer> entry : entrySet) {
            if(isFirst) isFirst = false;
            else sb.append(", ");
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
        }
        sb.append("}}");
        
        return sb.toString();
    }

    public Map<String, Integer> getDayClickMap() {
        return dayClickMap;
    }

    public Map<String, Integer> getDayRateMap() {
        return dayRateMap;
    }

    public int getFakeCount() {
        return fakeCount;
    }

    public void setFakeCount(int fakeCount) {
        this.fakeCount = fakeCount;
    }

//    public void setDayClickMap(Map<String, Integer> dayClickMap) {
//        this.dayClickMap = dayClickMap;
//    }
}
