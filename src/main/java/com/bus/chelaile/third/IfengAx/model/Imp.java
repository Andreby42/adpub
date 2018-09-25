/**
  * Copyright 2018 bejson.com 
  */
package com.bus.chelaile.third.IfengAx.model;
import java.util.List;
import java.util.Date;

/**
 * Auto-generated: 2018-09-21 17:51:4
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Imp {

    private String id;
    private List<Banner> banner;
    private String tagid;
    public Imp(String id, List<Banner> banner,String tagid) {
        super();
        this.id = id;
        this.banner = banner;
        this.tagid = tagid;
    }
    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setBanner(List<Banner> banner) {
         this.banner = banner;
     }
     public List<Banner> getBanner() {
         return banner;
     }

    public void setTagid(String tagid) {
         this.tagid = tagid;
     }
     public String getTagid() {
         return tagid;
     }
    public Imp() {
        super();
        // TODO Auto-generated constructor stub
    }

}