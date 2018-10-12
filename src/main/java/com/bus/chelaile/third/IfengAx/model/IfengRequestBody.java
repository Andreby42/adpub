/**
  * Copyright 2018 bejson.com 
  */
package com.bus.chelaile.third.IfengAx.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.New;

/**
 * Auto-generated: 2018-09-21 17:51:4
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class IfengRequestBody {

    private static final String APPID = "bus01";
    private static final String APPNAME = "车来了";

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
     * @param bannerType， 1: 贴片 ；2：Banner； 3:焦点图；4:启动图；6：常规信息流；7：三小图；9：下载信息流；10：信息流大图；
     * @param w 图片宽
     * @param h 图片高
     * @param tagid 广告位识别符。网盟的广告位id，要求implist中的tagid不重复，demo给的是‘1-1-1’
     */
    public IfengRequestBody(AdvParam p, int isTest, int bannerType, int w, int h, String tagid) {
        super();
        this.id = p.getUdid();
        // imp
        List<Imp> imps = New.arrayList();
        List<Banner> banners = New.arrayList();

        Banner banner = new Banner(h, 21, bannerType, w);
        banners.add(banner);
        Imp imp = new Imp(p.getUdid(), banners, tagid);
        imps.add(imp);
        this.imp = imps;

        // app
        // user
        this.user = new User(p.getImei());
        String domain = "com.ygkj.chelaile.standard";
        if (p.getS().equalsIgnoreCase("ios")) {
            domain = "com.chelaile.lite";
            this.user = new User(p.getIdfa());
        }
        this.app = new App(domain, APPID, APPNAME, p.getV());

        // device
        Geo geo = new Geo(p.getLng(), p.getLat());
        int ppi = 3; // 像素密度
        //        if(StringUtils.isNoneBlank(p.getDpi()))
        //            ppi = Math.round(Float.parseFloat(p.getDpi()));

        // 0 : Unknown， 1 : Ethernet，2 : WIFI ，3 : Cellular Network – Unknown Generation ，4 : Cellular Network – 2G ，5 : Cellular Network – 3G ，6 : Cellular Network – 4G
        this.device = new Device(2, geo, p.getIp(), p.getS(), p.getImei(), p.getAndroidID(), p.getIdfa(), p.getScreenHeight(),
                p.getScreenWidth(), ppi, p.getDeviceType(), p.getUa());

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
