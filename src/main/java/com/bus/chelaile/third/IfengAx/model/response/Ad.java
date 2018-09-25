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
public class Ad {

    private String id;
    private String impid;
    private String adid;
    private Creative creative;
    private List<String> adomain;
    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setImpid(String impid) {
         this.impid = impid;
     }
     public String getImpid() {
         return impid;
     }

    public void setAdid(String adid) {
         this.adid = adid;
     }
     public String getAdid() {
         return adid;
     }

    public void setCreative(Creative creative) {
         this.creative = creative;
     }
     public Creative getCreative() {
         return creative;
     }

    public void setAdomain(List<String> adomain) {
         this.adomain = adomain;
     }
     public List<String> getAdomain() {
         return adomain;
     }

}