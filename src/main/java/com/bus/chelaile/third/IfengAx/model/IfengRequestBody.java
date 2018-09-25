/**
  * Copyright 2018 bejson.com 
  */
package com.bus.chelaile.third.IfengAx.model;

import java.util.List;


import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.New;

/**
 * Auto-generated: 2018-09-21 17:51:4
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class IfengRequestBody {

    private String id;
    private List<Imp> imp;
    private App app;
    private Device device;
    private User user;
    private int test = 0;

    public IfengRequestBody() {
        super();
    }

    /**
     * 
     * @param p
     * @param isTest， 是否是测试， 0 不是， 1 是
     * @param bannerType， 1：Banner； 3：常规信息流；4：三小图；6：下载信息流；7：信息流大图；8:启动图；9:焦点图；0: 贴片 （需要线下约定，录入 IfengAX 管理系统中）
     * 
     */
    public IfengRequestBody(AdvParam p, int isTest, int bannerType) {
        super();
        this.id = "";
        // imp
        List<Imp> imps = New.arrayList();
        List<Banner> banners = New.arrayList();
        Banner banner = new Banner(200, 20, bannerType, 600);
        banners.add(banner);
        Imp imp = new Imp("广告位曝光请求的唯一识别符", banners, "广告位识别符");
        imps.add(imp);
        this.imp = imps;

        // app
        this.app = new App("app包名", "app 唯一标识符", "chelaile", p.getV());

        // device
        Geo geo = new Geo(119.0, 39.0);
        int ppi = Integer.parseInt(p.getDpi());

        this.device = new Device(2, geo, p.getIp(), p.getS(), p.getImei(), p.getAndroidID(), p.getIdfa(), p.getScreenHeight(),
                p.getScreenWidth(), ppi, p.getDeviceType(), p.getUa());
        //(int connectiontype, Geo geo, String ip, String os, String did, String dpid, String ifa,
        // int screenHeight, int screenWeight, int ppi, String phoneModel, String ua)

        // user
        this.user = new User(p.getUdid());

        // test
        this.test = isTest;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setImp(List<Imp> imp) {
        this.imp = imp;
    }

    public List<Imp> getImp() {
        return imp;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setTest(int test) {
        this.test = test;
    }

    public int getTest() {
        return test;
    }

}
