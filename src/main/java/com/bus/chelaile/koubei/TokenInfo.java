package com.bus.chelaile.koubei;

/**
 * Created by zhaoling on 2018/1/12.
 */
public class TokenInfo {
    private String aliUserId;
    private String token;

    public TokenInfo(String aliUserId) {
        this.aliUserId = aliUserId;
    }

    public String getAliUserId() {
        return aliUserId;
    }

    public void setAliUserId(String aliUserId) {
        this.aliUserId = aliUserId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
