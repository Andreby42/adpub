package com.bus.chelaile.innob.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.innob.utils.Validator;
/**
 * Created by Administrator on 2016/8/8.
 */

public class Request implements Validator {
    private App app;
    private Device device;
    private Imp imp;
    private Ext ext = new Ext();

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Imp getImp() {
        return imp;
    }

    public void setImp(Imp imp) {
        this.imp = imp;
    }

    public Ext getExt() {
        return ext;
    }

    public void setExt(Ext ext) {
        this.ext = ext;
    }

    // private AdTypeRequested adTypeRequested;
    public class Ext {
        private String responseformat = "json";
        private boolean externalSupported = true;    // TODO  , inmobe 3.0 新增参数

        public String getResponseformat() {
            return responseformat;
        }

        public boolean isExternalSupported() {
            return externalSupported;
        }

        public void setExternalSupported(boolean externalSupported) {
            this.externalSupported = externalSupported;
        }
    }

    public Request(App a, Device d, Imp i) {
        app = a;
        device = d;
        imp = i;
    }

    private boolean isNotNullAndValid(Validator v, String className) {
        boolean valid = true;
        if (v == null) {
            System.err.println(String.format("%s is null", className));
            return false;
        }
        if (!v.isValid()) {
            valid = false;
            System.err.println(String.format("%s is not valid", className));
        }
        return valid;
    }

    @JSONField(serialize = false)
    public boolean isValid() {
        boolean valid;
        valid = isNotNullAndValid(app, "App");
        valid &= isNotNullAndValid(device, "Device");
        valid &= isNotNullAndValid(imp, "NativeImp");
        // valid &= (adTypeRequested == null);
        return valid;
    }
}
