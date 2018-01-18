package com.bus.chelaile.koubei;

import java.util.List;

/**
 * Created by zhaoling on 2018/1/11.
 */
public class KoubeiInfo {
    private int status;  // 用户状态，0未授权，1已授权
    private List<CouponInfo> coupons;
    private int more; // 0,没有更多; 1,还有更多

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

    public int getMore() {
        return more;
    }

    public void setMore(int more) {
        this.more = more;
    }

    @Override
    public String toString() {
        return "KoubeiInfo{" +
                "status=" + status +
                ", coupons=" + coupons +
                ", more=" + more +
                '}';
    }
}
