/**
  * Copyright 2018 bejson.com 
  */
package com.bus.chelaile.third.IfengAx.model.response;
import java.util.List;

/**
 * Auto-generated: 2018-09-21 18:39:36
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class IfengResponse {

    private String id;
    private List<Ad> ad;
    private String cid;
    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setAd(List<Ad> ad) {
         this.ad = ad;
     }
     public List<Ad> getAd() {
         return ad;
     }

    public void setCid(String cid) {
         this.cid = cid;
     }
     public String getCid() {
         return cid;
     }

}