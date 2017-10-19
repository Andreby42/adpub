package com.bus.chelaile.push.pushModel;

import java.util.Date;

/**
 * Created by Administrator on 2015/12/15 0015.
 */
public class AdPushKey {
    private int id;
    private int advId;
    private int ruleId;
    private int pushCount;
    private int iosTokenCount;
    private int androidTokenCount;
    private int iosPushCount;
    private int androidPushCount;
    private String pushKey;
    private Date updateTime;


    public AdPushKey() {
    }

    public AdPushKey(int advId, int ruleId, int pushCount, int iosTokenCount, int androidTokenCount, String pushKey) {
        this.advId = advId;
        this.ruleId = ruleId;
        this.pushCount = pushCount;
        this.iosTokenCount = iosTokenCount;
        this.androidTokenCount = androidTokenCount;
        this.pushKey = pushKey;
    }

    public int getIosTokenCount() {
        return iosTokenCount;
    }

    public void setIosTokenCount(int iosTokenCount) {
        this.iosTokenCount = iosTokenCount;
    }

    public int getAndroidTokenCount() {
        return androidTokenCount;
    }

    public void setAndroidTokenCount(int androidTokenCount) {
        this.androidTokenCount = androidTokenCount;
    }

    public int getIosPushCount() {
        return iosPushCount;
    }

    public void setIosPushCount(int iosPushCount) {
        this.iosPushCount = iosPushCount;
    }

    public int getAndroidPushCount() {
        return androidPushCount;
    }

    public void setAndroidPushCount(int androidPushCount) {
        this.androidPushCount = androidPushCount;
    }

    public int getPushCount() {
        return pushCount;
    }

    public void setPushCount(int pushCount) {
        this.pushCount = pushCount;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdvId() {
        return advId;
    }

    public void setAdvId(int advId) {
        this.advId = advId;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
