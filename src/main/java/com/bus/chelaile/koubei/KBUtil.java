package com.bus.chelaile.koubei;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhaoling on 2018/1/11.
 */
public class KBUtil {
    public static boolean isBlankParam(String ... params) {
        for (int i = 0; i < params.length; i++) {
            if (StringUtils.isBlank(params[i])
                    || "null".equalsIgnoreCase(params[i])) {
                return true;
            }
        }
        return false;
    }

    public static String getKbCouponOcsKey(String cityId, String stnName) {
        return "kbCoupon#"+cityId+"#"+stnName;
    }
}
