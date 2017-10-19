package com.bus.chelaile.innob.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.innob.utils.*;

/**
 * Created by Administrator on 2016/8/8.
 */

public class App implements Validator {
    private String id;
    private String bundle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public App(String i, String b) {
        id = i;
        bundle = b;
    }

    @JSONField(serialize = false)
    public boolean isValid() {
        return (id != null) && (bundle != null);
    }
}
