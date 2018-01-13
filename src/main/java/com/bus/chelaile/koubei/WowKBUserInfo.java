package com.bus.chelaile.koubei;

import java.util.Date;

/**
 * Created by zhaoling on 2018/1/11.
 */
public class WowKBUserInfo {
    private String accountId;   //账号Id
    private String authUserId;  //授权用户Id
    private String phoneNumber; //手机号
    private int type;           //授权类型 1：口碑
    private Date createTime;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
