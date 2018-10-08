package com.bus.chelaile.model.record;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.util.New;

/**
 * 记录用户点击的pid、aid、title记录
 * @author Administrator
 *
 */
public class ClickCacheRecord {

    private Map<String, List<AdStruct>> pid_aids_map = New.hashMap();

    
    /**
     * 初始化
     * @param pid
     * @param aid
     * @param title
     */
    public ClickCacheRecord(String pid, String aid, String title) {
        super();
        AdStruct ad = new AdStruct(aid, title);
        
        List<AdStruct> ads = New.arrayList();
        ads.add(ad);
        
        this.pid_aids_map.put(pid, ads);
    }

    public ClickCacheRecord() {
        super();
    }

    /*
     * 根据pid、aid获取点击过的 title List
     */
    public List<String> getListByPidAndAid(String pid, String aid) {
        if (!pid_aids_map.isEmpty() && pid_aids_map.containsKey(pid)) {
            for (AdStruct adStruct : pid_aids_map.get(pid)) {
                // TODO 这一步有待商榷
                if (aid.contains(adStruct.getAid()) || adStruct.getAid().contains(aid)) {
                    return adStruct.getTitles();
                }
            }
        }
        return null;
    }

    public AdStruct hasAid(List<AdStruct> ads, String aid) {
        if (ads != null && !ads.isEmpty()) {
            for (AdStruct ad : ads) {
                if (aid.contains(ad.getAid())) {
                    return ad;
                }
            }
        }
        return null;
    }

    /**
     * 点击行为处理到缓存
     * @param pid
     * @param aid
     * @param title
     */
    public void addClickRecord(String pid, String aid, String title) {
        if (pid_aids_map.containsKey(pid)) {

            List<AdStruct> ads = pid_aids_map.get(pid);
            AdStruct ad = hasAid(ads, aid);
            if (ad != null) { // 该aid已经投放过
                ad.getTitles().add(title);
            } else { // 该aid未投放过
                AdStruct adNew = new AdStruct(aid, title);
                ads.add(adNew);
            }
        } else {
            AdStruct ad = new AdStruct(aid, title);

            List<AdStruct> ads = New.arrayList();
            ads.add(ad);

            pid_aids_map.put(pid, ads);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pid_aids_map == null) ? 0 : pid_aids_map.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClickCacheRecord other = (ClickCacheRecord) obj;
        if (pid_aids_map == null) {
            if (other.pid_aids_map != null)
                return false;
        } else if (!pid_aids_map.equals(other.pid_aids_map))
            return false;
        return true;
    }

    public Map<String, List<AdStruct>> getPid_aids_map() {
        return pid_aids_map;
    }

    public void setPid_aids_map(Map<String, List<AdStruct>> pid_aids_map) {
        this.pid_aids_map = pid_aids_map;
    }
    
    // TODO test
    public static void main(String[] args) {
        /**
         * 测试新元素 aidTitlesMap
         */
//      asdfad
        String recordStr = "{\"cacheRecordMap\":{17888:{\"clickCount\":0,\"dayClickMap\":{},\"dayCountMap\":{\"2018-09-30\":5},\"dayRateMap\":{},\"fakeCount\":0},17569:{\"clickCount\":0,\"dayClickMap\":{},\"dayCountMap\":{\"2018-09-30\":3},\"dayRateMap\":{},\"fakeCount\":0},17889:{\"clickCount\":0,\"dayClickMap\":{},\"dayCountMap\":{\"2018-09-30\":3},\"dayRateMap\":{},\"fakeCount\":0},16674:{\"clickCount\":0,\"dayClickMap\":{},\"dayCountMap\":{\"2018-09-30\":12,\"2018-09-28\":1,\"2018-09-29\":1},\"dayRateMap\":{},\"fakeCount\":0},16722:{\"clickCount\":0,\"dayClickMap\":{},\"dayCountMap\":{\"2018-09-30\":3,\"2018-09-29\":1},\"dayRateMap\":{},\"fakeCount\":0},17348:{\"clickCount\":0,\"dayClickMap\":{},\"dayCountMap\":{\"2018-09-28\":1},\"dayRateMap\":{},\"fakeCount\":0},17112:{\"clickCount\":0,\"dayClickMap\":{},\"dayCountMap\":{\"2018-09-29\":1},\"dayRateMap\":{},\"fakeCount\":0},16762:{\"clickCount\":0,\"dayClickMap\":{},\"dayCountMap\":{\"2018-09-30\":11},\"dayRateMap\":{},\"fakeCount\":0},16683:{\"clickCount\":2,\"dayClickMap\":{\"2018-09-30\":2},\"dayCountMap\":{\"2018-09-30\":12},\"dayRateMap\":{},\"fakeCount\":0},16685:{\"clickCount\":0,\"dayClickMap\":{},\"dayCountMap\":{\"2018-09-30\":3},\"dayRateMap\":{},\"fakeCount\":0}},\"cacheTime\":0,\"displayAdv\":true,\"firstClickMap\":{},\"todayHistoryMap\":{},\"todayNoAdHistoryMap\":{\"2018-09-30\":{\"00\":{},\"22\":{},\"23\":{17888:2,17889:0},\"04\":{-1:2},\"15\":{17569:8},\"16\":{-1:0},\"28\":{-1:0}},\"2018-09-28\":{\"00\":{}},\"2018-09-29\":{\"00\":{},\"04\":{},\"30\":{-1:0}}},\"todayOpenAdPubTime\":{},\"todayOpenHistoryMap\":{},\"uninterestedMap\":{\"11153\":{\"time\":1538297130610}},\"uvMap\":{}}";
        AdPubCacheRecord record = AdPubCacheRecord.fromJson(recordStr);
        
        record.buildAdPubCacheRecord(11111, "0", "1");
        
        record.buildAdClickRecord("00", "sdk_gdt_2", "京东特卖1");
        record.buildAdClickRecord("00", "sdk_gdt_2", "京东特卖1");
        
        record.buildAdClickRecord("00", "sdk_baidu", "京东特卖2");
        
        System.out.println("缓存结构体---->");
        System.out.println(JSONObject.toJSONString(record));
        
        System.out.println("有结果---->");
        System.out.println(record.getTodayClickedTitles("00", "sdk_gdt"));
        System.out.println(record.getTodayClickedTitles("00", "sdk_baidu"));
        System.out.println("无结果---->");
        System.out.println(record.getTodayClickedTitles("04", "sdk_gdt"));
        System.out.println("无结果---->");
        System.out.println(record.getTodayClickedTitles("00", "sdk_toutiao"));
        
    }
}


class AdStruct {

    private String aid;
    private List<String> titles = New.arrayList();

    public AdStruct() {
        super();
    }

    public AdStruct(String aid, String title) {
        super();
        this.aid = aid;
        this.titles.add(title);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((aid == null) ? 0 : aid.hashCode());
        result = prime * result + ((titles == null) ? 0 : titles.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AdStruct other = (AdStruct) obj;
        if (aid == null) {
            if (other.aid != null)
                return false;
        } else if (!aid.equals(other.aid))
            return false;
        if (titles == null) {
            if (other.titles != null)
                return false;
        } else if (!titles.equals(other.titles))
            return false;
        return true;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }
}
