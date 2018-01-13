package com.bus.chelaile.koubei;

/**
 * Created by zhaoling on 2018/1/12.
 */
public enum CouponState {
    //VALID,//可用 ;WRITED_OFF:已核销 ;EXPIRED:已过期 ;CLOSED:已关闭
    // ;WAIT_APPLY:已冻结 ;DELETED:已删除;
    VALID(1,"VALID"),WRITED_OFF(2,"WRITED_OFF"),EXPIRED(3,"EXPIRED"),CLOSED(4,"CLOSED")
    ,WAIT_APPLY(5,"WAIT_APPLY"),DELETED(6,"DELETED");

    private String desc;
    private int index;

    CouponState(int index, String desc) {
        this.desc = desc;
        this.index = index;
    }

    // 普通方法
    public static int getIndex(String desc) {
        for (CouponState c : CouponState.values()) {
            if (c.getDesc().equals(desc)) {
                return c.getIndex();
            }
        }
        return -1;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
