package com.bus.chelaile.innob.request;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Administrator on 2016/8/17.
 */
public class IOSDevice extends Device {
    private String ifa;

    public void setIfa(String ifa) {
        this.ifa = ifa;
    }

    public String getIfa() {
        return ifa;
    }

    @JSONField(serialize = false)
    @Override
    public boolean isValid() {
        return ifa != null && super.isValid();
    }
}
