package com.bus.chelaile.koubei;

/**
 * Created by zhaoling on 2018/1/15.
 */
public enum CouponType {
    discount("discount"), cash("cash")
    , exchange("exchange"), limit_reduce_cash("limit_reduce_cash");

    private String type;

    CouponType(String type) {
        this.type = type;
    }

    public static CouponType getType(String type) {
        for (CouponType c : CouponType.values()) {
            if (c.getType().equals(type)) {
                return c;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
