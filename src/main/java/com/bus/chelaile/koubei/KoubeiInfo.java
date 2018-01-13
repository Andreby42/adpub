package com.bus.chelaile.koubei;

import java.util.List;

/**
 * Created by zhaoling on 2018/1/11.
 */
public class KoubeiInfo {
    private int status;  // 用户状态，0未授权，1已授权
    private List<CouponInfo> coupons;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<CouponInfo> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<CouponInfo> coupons) {
        this.coupons = coupons;
    }

    @Override
    public String toString() {
        return "KoubeiInfo{" +
                "status=" + status +
                ", coupons=" + coupons +
                '}';
    }
}
