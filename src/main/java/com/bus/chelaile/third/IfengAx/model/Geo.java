/**
  * Copyright 2018 bejson.com 
  */
package com.bus.chelaile.third.IfengAx.model;

/**
 * Auto-generated: 2018-09-21 17:51:4
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Geo {

    private double lat;
    private double lon;
    
    public Geo(double lat, double lon) {
        super();
        this.lat = lat;
        this.lon = lon;
    }
    public Geo() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void setLat(double lat) {
         this.lat = lat;
     }
     public double getLat() {
         return lat;
     }

    public void setLon(double lon) {
         this.lon = lon;
     }
     public double getLon() {
         return lon;
     }

}