package com.bus.chelaile.koubei;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaoling on 2018/1/10.
 */
public class CityCodeMap {
    private static Map<String, String> cityCodeMap = new HashMap<>();

    /**
     * 天津 120000
     重庆 500000
     深圳 440300
     广州 440100
     佛山 440600
     贵阳 520100
     沈阳 210100
     成都 510100
     东莞 441900
     兰州 620100
     * */
    static {
        cityCodeMap.put("006", "120000");
        cityCodeMap.put("003", "500000");
        cityCodeMap.put("014", "440300");
        cityCodeMap.put("034", "310100");
        cityCodeMap.put("019", "440600");
        cityCodeMap.put("040", "440100");
        cityCodeMap.put("083", "520100");
        cityCodeMap.put("035", "210100");
        cityCodeMap.put("007", "510100");
        cityCodeMap.put("008", "441900");
        cityCodeMap.put("017", "620100");
    }

    public static String getCityCodeByCityId(String cityId) {
        return cityCodeMap.get(cityId);
    }
}
