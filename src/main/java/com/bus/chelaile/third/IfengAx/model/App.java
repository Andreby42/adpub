/**
  * Copyright 2018 bejson.com 
  */
package com.bus.chelaile.third.IfengAx.model;
import java.util.Date;

/**
 * Auto-generated: 2018-09-21 17:51:4
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class App {

    private String domain;
    private String id;
    private String name;
    private String ver;
    public App(String domain, String id, String name, String ver) {
        super();
        this.domain = domain;
        this.id = id;
        this.name = name;
        this.ver = ver;
    }
    public App() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void setDomain(String domain) {
         this.domain = domain;
     }
     public String getDomain() {
         return domain;
     }

    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setVer(String ver) {
         this.ver = ver;
     }
     public String getVer() {
         return ver;
     }

}