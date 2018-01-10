package com.bus.chelaile.koubei;

/**
 * Created by zhaoling on 2018/1/10.
 */
public class CouponGetParam {
    private String adv_id;
    private String channel_code = "chelaile";
    private String out_biz_no;

    public CouponGetParam(String adv_id, String out_biz_no) {
        this.adv_id = adv_id;
        this.out_biz_no = out_biz_no;
    }

    public String getAdv_id() {
        return adv_id;
    }

    public void setAdv_id(String adv_id) {
        this.adv_id = adv_id;
    }

    public String getChannel_code() {
        return channel_code;
    }

    public void setChannel_code(String channel_code) {
        this.channel_code = channel_code;
    }

    public String getOut_biz_no() {
        return out_biz_no;
    }

    public void setOut_biz_no(String out_biz_no) {
        this.out_biz_no = out_biz_no;
    }
}
