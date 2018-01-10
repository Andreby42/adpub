package com.bus.chelaile.koubei;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaoling on 2018/1/10.
 */
public class CityCodeMap {
    private static Map<String, String> cityCodeMap = new HashMap<>();

    static {
        cityCodeMap.put("006", "120000");
        cityCodeMap.put("003", "500000");
        cityCodeMap.put("014", "440300");
    }

    public static String getCityCodeByCityId(String cityId) {
        return cityCodeMap.get(cityId);
    }
}
