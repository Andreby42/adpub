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
public class Banner {

    private int h;
    private int textlen;
    private int type;
    private int w;
    public void setH(int h) {
         this.h = h;
     }
     public int getH() {
         return h;
     }

    public void setTextlen(int textlen) {
         this.textlen = textlen;
     }
     public int getTextlen() {
         return textlen;
     }

    public void setType(int type) {
         this.type = type;
     }
     public int getType() {
         return type;
     }

    public void setW(int w) {
         this.w = w;
     }
     public int getW() {
         return w;
     }
    public Banner(int h, int textlen, int type, int w) {
        super();
        this.h = h;
        this.textlen = textlen;
        this.type = type;
        this.w = w;
    }
    public Banner() {
        super();
        // TODO Auto-generated constructor stub
    }

}