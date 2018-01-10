package com.bus.chelaile.koubei;

/**
 * Created by zhaoling on 2018/1/10.
 */
public class CouponInfo {
    private String name;
    private String distance;
    private String condition;
    private String price;
    // 0(未领取),1(可用),2(已使用),3(已过期),4(已关闭),5(已冻结)
    private int status;

    public CouponInfo(String name, String distance, String condition, String price) {
        this.name = name;
        this.distance = distance;
        this.condition = condition;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
