package com.bus.chelaile.koubei;

/**
 * Created by zhaoling on 2018/1/10.
 */
public class CouponDetailParam {
    private String user_id;
    private String voucher_id;

    public CouponDetailParam(String user_id, String voucher_id) {
        this.user_id = user_id;
        this.voucher_id = voucher_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVoucher_id() {
        return voucher_id;
    }

    public void setVoucher_id(String voucher_id) {
        this.voucher_id = voucher_id;
    }
}
