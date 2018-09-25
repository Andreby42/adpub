/**
  * Copyright 2018 bejson.com 
  */
package com.bus.chelaile.third.IfengAx.model;

import lombok.Data;

/**
 * Auto-generated: 2018-09-21 17:51:4
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Device {

    private int connectiontype;  //网络类型，  0:Unknown ,  1:Ethernet,  2:WIFI ,  3:Cellular Network – Unknown Generation,  4:Cellular Network – 2G,  5:Cellular Network – 3G,  6:Cellular Network – 4G 
    private String did;   // android imei
    private String dpid;  // android id
    private Geo geo;
    private String ip;
    private String os;
    private String ifa; // ios idfa
    private String mac;
    private String osv;
    
    private int h;
    private int w;
    private int ppi;
    private String model;
    
    private String ua;
    
    
    public Device(int connectiontype, Geo geo, String ip, String os, String did, String dpid, String ifa,
            int screenHeight, int screenWidth, int ppi, String phoneModel, String ua) {
        super();
        this.connectiontype = connectiontype;
        this.geo = geo;
        this.ip = ip;
        this.os = os;
        this.did = did;
        this.dpid = dpid;
        this.ifa = ifa;
        this.h = screenHeight;
        this.w = screenWidth;
        this.ppi = ppi;
        this.model = phoneModel;
        this.ua = ua;
    }
    
    public Device() {
        super();
    }
    
}