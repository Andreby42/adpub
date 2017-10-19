package com.bus.chelaile.innob.request;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Administrator on 2016/8/17.
 */

public class AndroidDevice extends Device {
    private String gpid;
    private String iem;
    private String o1;

    public String getGpid() {
        return gpid;
    }

    public void setGpid(String gpid) {
        this.gpid = gpid;
    }

    public String getIem() {
        return iem;
    }

    public void setIem(String iem) {
        this.iem = iem;
    }

    public String getO1() {
        return o1;
    }

    public void setO1(String o1) {
        this.o1 = o1;
    }

    @JSONField(serialize = false)
    @Override
    public boolean isValid() {
        return super.isValid() && gpid != null;
    }
}
