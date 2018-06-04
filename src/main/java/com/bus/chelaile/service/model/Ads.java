/**
  * Copyright 2018 bejson.com 
  */
package com.bus.chelaile.service.model;
import java.util.List;

/**
 * Auto-generated: 2018-06-04 17:40:19
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Ads {

    private String action;
    private String source;
    private int thumbnailType;
    private List<Thumbnails> thumbnails;
    private String timeShow;
    private String title;
    public void setAction(String action) {
         this.action = action;
     }
     public String getAction() {
         return action;
     }

    public void setSource(String source) {
         this.source = source;
     }
     public String getSource() {
         return source;
     }

    public void setThumbnailType(int thumbnailType) {
         this.thumbnailType = thumbnailType;
     }
     public int getThumbnailType() {
         return thumbnailType;
     }

    public void setThumbnails(List<Thumbnails> thumbnails) {
         this.thumbnails = thumbnails;
     }
     public List<Thumbnails> getThumbnails() {
         return thumbnails;
     }

    public void setTimeShow(String timeShow) {
         this.timeShow = timeShow;
     }
     public String getTimeShow() {
         return timeShow;
     }

    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

}